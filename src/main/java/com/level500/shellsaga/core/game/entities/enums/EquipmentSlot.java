package com.level500.shellsaga.core.game.entities.enums;

import com.level500.shellsaga.config.ConfigManager;

public enum EquipmentSlot {
    EXECUTION_MODULE(1),
    NEURAL_UPLINK(2),
    FIREWALL_CORE(3),
    INTERFACE_GLOVES(4),
    QUANTUM_STABILIZERS(5),
    STEALTH_BOOTS(6),
    DATA_CIPHER_RING(7),
    DECRYPTION_LOCKET(8),
    CYBERNETIC_PROCESSOR(9),
    PACKET_DISRUPTOR(10);

    private final int slotId;

    EquipmentSlot(int slotId){
        this.slotId = slotId;
    }

    public int getSlotId(){
        return slotId;
    }

    public static EquipmentSlot fromSlot(int id){
        for (EquipmentSlot slot : values()) {
            if (slot.getSlotId() == id) {
                return slot;
            }
        }
        throw new IllegalArgumentException("Invalid Equipment Slot ID: " + id);
    }

    public String getSlotName() {
        return ConfigManager.getConfigValue(this.name()); // ✅ Fetch slot name from JSON
    }
}
