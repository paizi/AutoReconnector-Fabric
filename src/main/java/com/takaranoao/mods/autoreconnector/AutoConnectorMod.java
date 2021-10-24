package com.takaranoao.mods.autoreconnector;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.apache.logging.log4j.LogManager;

public class AutoConnectorMod implements ClientModInitializer {
    public static final int MAX_TICK = 20 * 15;
    @Environment(EnvType.CLIENT)
    public static ServerInfo lastestServerEntry;
    public static int disconnectTick = 0;

    public static void clientTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world != null && mc.getCurrentServerEntry() != null) {
            lastestServerEntry = mc.getCurrentServerEntry();
        }
        if (mc.currentScreen instanceof DisconnectedScreen) {
            disconnectTick++;
            if (disconnectTick >= MAX_TICK && lastestServerEntry != null) {
                mc.disconnect();
                ConnectScreen.connect(new TitleScreen(), mc, ServerAddress.parse(lastestServerEntry.address), lastestServerEntry);
            }
        } else {
            disconnectTick = 0;
        }
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> clientTick());
        LogManager.getLogger().info("Loading Auto Reconnect");
    }
}
