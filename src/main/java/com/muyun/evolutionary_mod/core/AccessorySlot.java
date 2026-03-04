package com.muyun.evolutionary_mod.core;

/**
 * 饰品槽位定义 - Accessory Slot Definition
 * 
 * Defines the accessory slots available per player.
 * Order of declaration is used as the index for storage.
 * 
 * New configuration:
 * - 1 headwear (头饰)
 * - 2 earrings (耳环)
 * - 1 necklace (项链)
 * - 2 gloves (手套)
 * - 2 bracelets (手镯)
 * - 4 rings (戒指)
 * - 1 belt (腰带)
 * - 2 boots (靴子)
 * - 3 accessories (配饰)
 */
public enum AccessorySlot {
    HEAD,                    // 头饰 (1个)
    EARRING_1,              // 耳环1
    EARRING_2,              // 耳环2
    NECKLACE,               // 项链 (1个)
    GLOVE_1,                // 手套1
    GLOVE_2,                // 手套2
    BRACELET_1,             // 手镯1
    BRACELET_2,             // 手镯2
    RING_1,                 // 戒指1
    RING_2,                 // 戒指2
    RING_3,                 // 戒指3
    RING_4,                 // 戒指4
    BELT,                   // 腰带 (1个)
    BOOT_1,                 // 靴子1
    BOOT_2,                 // 靴子2
    ACCESSORY_1,            // 配饰1
    ACCESSORY_2,            // 配饰2
    ACCESSORY_3;           // 配饰3

    public static int count() {
        return values().length;
    }
}

