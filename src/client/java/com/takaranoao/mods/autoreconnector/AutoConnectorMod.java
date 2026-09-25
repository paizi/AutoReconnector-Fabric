package com.takaranoao.mods.autoreconnector;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.language.I18n;
import org.apache.logging.log4j.LogManager;

public class AutoConnectorMod implements ClientModInitializer {
    public static final int MAX_TICK = 20 * 15;
    private static final int FONT_HEIGHT = 9;
    @Environment(EnvType.CLIENT)
    public static ServerData lastestServerEntry;
    public static int disconnectTick = 0;

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.getCurrentServer() != null) {
            lastestServerEntry = mc.getCurrentServer();
        }
        if (mc.gui.screen() instanceof DisconnectedScreen) {
            disconnectTick++;
            if (disconnectTick == MAX_TICK && lastestServerEntry != null) {
                mc.disconnect(new TitleScreen(), false);
                ConnectScreen.startConnecting(new TitleScreen(), mc, ServerAddress.parseString(lastestServerEntry.ip), lastestServerEntry, false, null);
                disconnectTick = 0;
            }
        } else {
            disconnectTick = 0;
        }
    }

    private static void renderCountdown(DisconnectedScreen screen, GuiGraphicsExtractor graphics) {
        if (lastestServerEntry == null || disconnectTick == 0) return;

        graphics.centeredText(
                Minecraft.getInstance().font,
                I18n.get("autoReconnector.waitingTime", (MAX_TICK - disconnectTick) / 20),
                screen.width / 2,
                screen.height - 10 - FONT_HEIGHT * 2,
                0xFFFFFFFF); // GUI text colors are ARGB; the alpha channel must be opaque.
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> clientTick());
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof DisconnectedScreen disconnectedScreen) {
                ScreenEvents.afterExtract(screen).register((drawnScreen, graphics, mouseX, mouseY, delta) ->
                        renderCountdown(disconnectedScreen, graphics));
            }
        });
        LogManager.getLogger().info("Loading Auto Reconnect");
    }
}
