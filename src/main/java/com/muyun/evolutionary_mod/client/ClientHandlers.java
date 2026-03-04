package com.muyun.evolutionary_mod.client;

import net.minecraft.client.KeyMapping;

import net.neoforged.api.distmarker.Dist;

import org.lwjgl.glfw.GLFW;

public class ClientHandlers {
    public static KeyMapping OPEN_ACCESSORIES;
    public static KeyMapping OPEN_PLAYER_ATTRIBUTES;

    public static void registerKeybindings() {
        OPEN_ACCESSORIES = new KeyMapping("key.accessories.open", GLFW.GLFW_KEY_K, "key.categories.evolutionary_mod");
        OPEN_PLAYER_ATTRIBUTES = new KeyMapping("key.accessories.open_attributes", GLFW.GLFW_KEY_J, "key.categories.evolutionary_mod");
    }

    public static void register(KeyMapping... keyMappings) {
    }
}
