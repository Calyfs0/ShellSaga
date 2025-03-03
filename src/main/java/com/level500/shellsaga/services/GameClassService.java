package com.level500.shellsaga.services;

import com.level500.shellsaga.core.game.entities.GameClasses;
import com.level500.shellsaga.infrastructure.database.GameClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameClassService {
    private final GameClassesRepository gameClassesRepository;

    @Autowired
    public GameClassService(GameClassesRepository gameClassesRepository){
        this.gameClassesRepository = gameClassesRepository;
    }

    public GameClasses findClassByClassName(String className){
        GameClasses gameClasses = gameClassesRepository.findByClassNameIgnoreCase(className);
        if(gameClasses == null){
            throw new RuntimeException("Illegal class, please select a valid class!");
        }

        return gameClasses;
    }
}
