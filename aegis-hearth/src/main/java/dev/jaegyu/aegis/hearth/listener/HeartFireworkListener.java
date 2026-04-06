// aegis-hearth/src/main/java/dev/jaegyu/aegis/hearth/listener/HeartFireworkListener.java
package dev.jaegyu.aegis.hearth.listener;

import dev.jaegyu.aegis.hearth.HearthConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.joml.Vector3f;

public class HeartFireworkListener {

    public static final String NBT_KEY = "aegis_hearth:heart_color";

    // -------------------------------------------------------------------------
    // Craft event — copies the heart color tag from the firework star ingredient
    // onto the rocket result so the mixin can read it at explosion time.
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!HearthConfig.SERVER.heartFirework.get()) return;

        var result = event.getCrafting();
        if (!result.is(Items.FIREWORK_ROCKET)) return;

        var matrix = event.getInventory();
        for (int i = 0; i < matrix.getContainerSize(); i++) {
            var stack = matrix.getItem(i);
            if (!stack.is(Items.FIREWORK_STAR)) continue;

            var data = stack.get(DataComponents.CUSTOM_DATA);
            if (data == null) continue;

            CompoundTag tag = data.copyTag();
            if (!tag.contains(NBT_KEY)) continue;

            int rgb = tag.getInt(NBT_KEY).orElse(0);

            CompoundTag rocketTag = result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            rocketTag.putInt(NBT_KEY, rgb);
            result.set(DataComponents.CUSTOM_DATA, CustomData.of(rocketTag));
            break;
        }
    }

    // -------------------------------------------------------------------------
    // Called from HeartFireworkMixin — static so the mixin can reach it without
    // holding a listener reference. Returns true if this was a heart rocket and
    // we handled the explosion ourselves (mixin will cancel vanilla behaviour).
    // -------------------------------------------------------------------------

    public static boolean tryHandleExplosion(FireworkRocketEntity rocket) {
        if (!HearthConfig.SERVER.heartFirework.get()) return false;
        if (!(rocket.level() instanceof ServerLevel level)) return false;

        var data = rocket.getItem().get(DataComponents.CUSTOM_DATA);
        if (data == null) return false;

        CompoundTag tag = data.copyTag();
        if (!tag.contains(NBT_KEY)) return false;

        int rgb = tag.getInt(NBT_KEY).orElse(0);
        int argb = 0xFF000000 | rgb;
        DustParticleOptions dust = new DustParticleOptions(argb, 1.8f);

        double ox = rocket.getX();
        double oy = rocket.getY();
        double oz = rocket.getZ();

        for (double scale : new double[]{2.4, 4.0, 5.6}) {
            spawnHeart(level, ox, oy, oz, dust, scale);
        }

        level.playSound(null, ox, oy, oz,
                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.AMBIENT, 3.0f, 1.0f);
        level.playSound(null, ox, oy, oz,
                SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.AMBIENT, 2.0f, 1.0f);

        return true;
    }

    private static void spawnHeart(ServerLevel level,
                                   double ox, double oy, double oz,
                                   DustParticleOptions dust, double scale) {
        final double normalizer = 17.0;
        for (double t = 0; t < 2 * Math.PI; t += 0.07) {
            double rawX = 16.0 * Math.pow(Math.sin(t), 3);
            double rawY = 13.0 * Math.cos(t)
                    - 5.0 * Math.cos(2 * t)
                    - 2.0 * Math.cos(3 * t)
                    - Math.cos(4 * t);

            double x = ox + (rawX / normalizer) * scale;
            double y = oy + (rawY / normalizer) * scale;

            level.sendParticles(dust, x, y, oz, 1, 0, 0, 0, 0);
        }
    }
}