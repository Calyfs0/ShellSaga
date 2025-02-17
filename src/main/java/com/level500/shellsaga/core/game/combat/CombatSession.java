package com.level500.shellsaga.core.game.combat;

import com.level500.shellsaga.config.ItemConstants;
import com.level500.shellsaga.core.game.characters.Enemy;
import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.entities.STKEarningRule;
import com.level500.shellsaga.core.game.factories.EnemyFactory;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.infrastructure.database.EnemyRepository;
import com.level500.shellsaga.services.PlayerService;
import com.level500.shellsaga.services.PlayerSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CombatSession {
    private static final Logger logger = LoggerFactory.getLogger(CombatSession.class);
    private final Player player;
    private final Enemy enemy;
    private final Random random = new Random();
    private final ExecutorService enemyAttackExecutor = Executors.newSingleThreadExecutor();
    private boolean battleActive = true;
    private final PlayerService playerService;
    private final PlayerSessionService playerSessionService;
    private final EnemyFactory enemyFactory;


    public CombatSession(Player player, Enemy enemy, PlayerService playerService, PlayerSessionService playerSessionService, EnemyRepository enemyRepository) {
        this.player = player;
        this.enemy = enemy;
        this.playerService = playerService;
        this.playerSessionService = playerSessionService;
        this.enemyFactory = new EnemyFactory(enemyRepository);
    }

    public void startBattle(){
        logger.info("\n⚔️ You are now fighting: {} (Level {})", enemy.getName(), enemy.getLevel());

        //Start enemy attack thread
        enemyAttackExecutor.submit(() ->{
            while(battleActive){
                try{
                    int attackDelay = 2000 - (enemy.getSpeed() * 100);
                    attackDelay = Math.max(500, attackDelay); //Ensure minimum attack delay
                    TimeUnit.MILLISECONDS.sleep(attackDelay);

                    if(battleActive) {
                        int damage = Math.max(1, enemy.getAttackPower() - player.getBaseDefense());
                        player.setReceivedDamage(player.getReceivedDamage() + damage);
                        int remainingHp = player.getTotalHp() - player.getReceivedDamage();
                        logger.info("\n💥 {} attacks you! You take {} damage. (HP: {}/{})",
                                enemy.getName(), damage, remainingHp, player.getTotalHp());


                        if (remainingHp <= 0) {
                            logger.info("\n💀 You have been defeated by {}!", enemy.getName());
                            logger.info("Press enter to return to hometown!");
                            battleActive = false;
                            enemyAttackExecutor.shutdownNow();
                            break;
                        }
                    }

                }catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
            }
        });
    }

    public void playerAttack(){
        if(!battleActive){
            logger.info("\n❌ You can't attack. The battle is over.");
            return;
        }
        boolean isCritical = random.nextInt(100) < player.getTotalCritRate();
        double playerAttack = player.getTotalAttackPower() * (isCritical ? player.getTotalCritMultiplier() : 1);
        int damage = (int) Math.max(1, playerAttack - enemy.getDefense());
        enemy.setHp(Math.max(0, enemy.getHp() - damage));
        if(isCritical){
            logger.info("\n🗡️🗡️🗡️ Critical Hit! You attack {} and deal {} damage! (Enemy HP: {}/{})",
                    enemy.getName(), damage, enemy.getHp(), enemy.getMaxHp());
        }else{
            logger.info("\n🗡️ You attack {} and deal {} damage! (Enemy HP: {}/{})",
                    enemy.getName(), damage, enemy.getHp(), enemy.getMaxHp());
        }


        if(enemy.getHp() <= 0){
            logger.info("\n🏆 You have defeated {}!", enemy.getName());
            rewardPlayer(); // Grand rewards to the player
            battleActive = false;
            enemyAttackExecutor.shutdownNow();
        }
    }

    public void rewardPlayer(){
        int xpEarned = enemy.getLevel() * 10;
        int goldEarned = enemy.getLevel() * 5;

        player.gainXp(xpEarned);
        player.addItem(ItemConstants.GOLD, goldEarned);

        logger.info("\n🎉 You gained {} {} and {} {}", xpEarned,ItemConstants.EXP, goldEarned,ItemConstants.GOLD);

        // ✅ Check for STK drops (Based on rules stored in DB)

        List<STKEarningRule> stkEarningRules = playerService.getSTKEarningRules();
        for(STKEarningRule rule : stkEarningRules){
            if(rule.getCondition().equalsIgnoreCase("Defeat Rare Enemy") && enemy.isRare()){
                player.addItem(ItemConstants.STK, rule.getStkReward());
                logger.info("\n💎 Bonus! You earned {} {}!", rule.getStkReward(),ItemConstants.STK);
            }
        }

        handleEquipmentDrop(player,enemy);
        playerSessionService.storePlayerSession(player);
        playerService.savePlayer(player);
    }

    public void escape() {
        if (!battleActive) {
            logger.info("\n❌ You can't escape. The battle is over.");
            return;
        }
        logger.info("\n🏃 You successfully escaped from {}!", enemy.getName());
        battleActive = false;
        enemyAttackExecutor.shutdownNow();
    }

    private void handleEquipmentDrop(Player player, Enemy enemy){
        Random random = new Random();
        int dropChance = random.nextInt(100);
        if(dropChance < 25){
            Equipment droppedItem = enemyFactory.generateEquipmentDrop(enemy);
            player.addEquipment(droppedItem);

            logger.info("🎉 You received a new item: " + droppedItem.getName() + " (Level " + droppedItem.getLevel() + ")");
        }
    }

}
