package dev.jaegyu.aegis.bulwark.mixin;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.ModMismatchDisconnectedScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModMismatchDisconnectedScreen.class)
public class ModMismatchScreenMixin {

    @Mutable
    @Shadow
    private net.minecraft.network.chat.Component reason;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void appendDownloadLink(
            net.minecraft.client.gui.screens.Screen parentScreen,
            Component reason,
            Map<Identifier, Component> mismatchedChannelData,
            CallbackInfo ci) {

        // TODO: pull this from config once we wire up client config
        String url = "https://github.com/JaegyuDev/HBPack";

        this.reason = reason.copy()
                .append(Component.literal("\n\nDownload the latest HBPack: ")
                        .append(Component.literal(url)
                                .withStyle(Style.EMPTY
                                        .withUnderlined(true)
                                        .withClickEvent(new ClickEvent.OpenUrl(java.net.URI.create(url))))));
    }
}