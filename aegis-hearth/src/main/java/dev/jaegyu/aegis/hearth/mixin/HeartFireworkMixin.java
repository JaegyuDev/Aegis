// aegis-hearth/src/main/java/dev/jaegyu/aegis/hearth/mixin/HeartFireworkMixin.java
package dev.jaegyu.aegis.hearth.mixin;

import dev.jaegyu.aegis.hearth.listener.HeartFireworkListener;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public class HeartFireworkMixin {

    @Inject(method = "causeExplosion", at = @At("HEAD"), cancellable = true)
    private void onCauseExplosion(CallbackInfo ci) {
        FireworkRocketEntity self = (FireworkRocketEntity) (Object) this;
        if (HeartFireworkListener.tryHandleExplosion(self)) {
            ci.cancel();
        }
    }
}