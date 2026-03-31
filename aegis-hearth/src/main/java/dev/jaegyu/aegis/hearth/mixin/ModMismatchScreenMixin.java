package dev.jaegyu.aegis.hearth.mixin;

import dev.jaegyu.aegis.hearth.BulwarkHandshakeCache;
import net.minecraft.client.gui.screens.Screen;
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

import java.net.URI;
import java.util.Map;

@Mixin(ModMismatchDisconnectedScreen.class)
public class ModMismatchScreenMixin {

    @Mutable
    @Shadow
    private Component reason;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void appendDownloadLink(
            Screen parentScreen,
            Component reason,
            Map<Identifier, Component> mismatchedChannelData,
            CallbackInfo ci) {

        String url = BulwarkHandshakeCache.get();
        if (url == null) return;

        this.reason = reason.copy()
                .append(Component.literal("\n\nDownload the latest pack: ")
                        .append(Component.literal(url)
                                .withStyle(Style.EMPTY
                                        .withUnderlined(true)
                                        .withClickEvent(new ClickEvent.OpenUrl(URI.create(url))))));
    }
}
