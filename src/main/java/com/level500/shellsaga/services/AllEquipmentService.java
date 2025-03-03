package com.level500.shellsaga.services;

import com.level500.shellsaga.core.game.entities.GameClasses;
import com.level500.shellsaga.core.game.items.AllEquipments;
import com.level500.shellsaga.infrastructure.database.AllEquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllEquipmentService {
    private final AllEquipmentRepository allEquipmentRepository;

    @Autowired
    public AllEquipmentService(AllEquipmentRepository allEquipmentRepository){
        this.allEquipmentRepository = allEquipmentRepository;
    }

    public List<AllEquipments> getAllEquipmentsByClass(GameClasses gameClass){
        return allEquipmentRepository.getAllEquipmentsByGameClass(gameClass);
    }

}
