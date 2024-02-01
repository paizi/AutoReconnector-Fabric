package com.takaranoao.mods.autoreconnector.mixins;

import com.takaranoao.mods.autoreconnector.AutoConnectorMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public class MixinDisconnectedScreen {
    private static final int FONT_HEIGHT = 9;

    //ref: https://fabricmc.net/wiki/tutorial:mixin_injects
    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (AutoConnectorMod.lastestServerEntry == null) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer textRenderer = mc.textRenderer;
        if (mc.currentScreen == null) return;
        int width = mc.currentScreen.width;
        int height = mc.currentScreen.height;
        context.drawCenteredTextWithShadow(
                textRenderer,
                I18n.translate("autoReconnector.waitingTime", (AutoConnectorMod.MAX_TICK - AutoConnectorMod.disconnectTick) / 20),
                width / 2,
                height - 10 - FONT_HEIGHT * 2,
                0xffffff);
    }
}
