package com.level500.shellsaga.services;

import com.level500.shellsaga.core.game.entities.GearSlotEntity;
import com.level500.shellsaga.infrastructure.database.GearSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GearSlotService {
    private final GearSlotRepository gearSlotRepository;

    @Autowired
    public GearSlotService(GearSlotRepository gearSlotRepository){
        this.gearSlotRepository = gearSlotRepository;
    }

    public String getDisplayName(String slotKey){
        return gearSlotRepository.findBySlotKey(slotKey)
                .map(GearSlotEntity::getDisplayName)
                .orElse("Unknown Slot");
    }
}
