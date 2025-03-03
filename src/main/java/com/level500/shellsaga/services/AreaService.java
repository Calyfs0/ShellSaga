package com.level500.shellsaga.services;

import com.level500.shellsaga.core.game.locations.Area;
import com.level500.shellsaga.infrastructure.database.AreaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AreaService {
    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository){
        this.areaRepository = areaRepository;
    }

    public void initializeAreas() {
        if (areaRepository.count() == 0) {
            System.out.println("❌ No areas found in the database. Please insert areas manually.");
        } else {
            System.out.println("✅ Areas loaded from the database.");
        }
    }

    public List<Area> getAvailableAreas(int playerLevel) {
        return areaRepository.findByLevelRequirementLessThanEqual(playerLevel);
    }

    public Optional<Area> getAreaByName(String name) {
        return areaRepository.findByName(name);
    }

}
