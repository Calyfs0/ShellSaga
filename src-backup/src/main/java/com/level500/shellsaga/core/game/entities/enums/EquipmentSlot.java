package com.level500.shellsaga.core.game.entities.enums;

public enum EquipmentSlot {
    WEAPON(1),
    HELMET(2),
    ARMOR(3),
    SHOULDERS(4),
    PANTS(5),
    BOOTS(6);

    private final int slotId;

    EquipmentSlot(int slotId){
        this.slotId = slotId;
    }

    public int getSlotId(){
        return slotId;
    }

    public String getSlotName(int slotId){
        return slotId == 1 ? "Weapon" : slotId == 2 ? "Helmet" : slotId == 3 ? "Armor" : slotId == 4 ? "Shoulders" : slotId == 5 ? "Pants" : "Boots";
    }

    public static EquipmentSlot fromSlot(int id){
        for(EquipmentSlot slot : values()){
            if(slot.getSlotId() == id){
                return slot;
            }
        }
        throw new IllegalArgumentException("Invalid Equipment Slot ID: " + id);
    }
}
