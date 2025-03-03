package com.level500.shellsaga.services;

import com.level500.shellsaga.core.game.characters.Player;
import com.level500.shellsaga.core.game.items.Equipment;
import com.level500.shellsaga.infrastructure.database.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository){
        this.equipmentRepository = equipmentRepository;
    }

    public List<Equipment> getAllEquipments(){
        return equipmentRepository.findAll();
    }

    public void updateEquipments(Equipment equipment){
        equipmentRepository.save(equipment);
    }

    public List<Equipment> getAllEquipmentsByPlayerId(Long playerId){
        return equipmentRepository.getEquipmentById(playerId);
    }

    public List<Equipment> getAllPlayerEquipments(Player player){
        return equipmentRepository.getAllEquipmentByPlayer(player);
    }

    public Equipment addEquipment(Equipment equipment){
        return equipmentRepository.save(equipment);
    }
}
