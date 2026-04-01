package dev.jaegyu.aegis.hearth.listener;

import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

public class ArmorStandListener {

    private static final String POSE_KEY = "aegis_hearth:armor_stand_pose";

    private static final List<float[][]> POSES = List.of(
            pose(0,-15,0,  0,0,0,   -10,0,-10, -15,0,10,  -1,0,-1,  1,0,1),   // default
            pose(0,0,0,    0,0,0,   0,0,0,     0,0,0,     0,0,0,    0,0,0),   // no_pose
            pose(15,0,0,   0,0,2,   -30,15,15, -60,-20,-10,-1,0,-1,  1,0,1),  // solemn
            pose(-5,0,0,   0,0,2,   10,0,-5,   -60,20,-10, -3,-3,-3, 3,3,3),  // athena
            pose(-15,0,0,  0,0,-2,  20,0,-10,  -110,50,0,  5,-3,-3,  -5,3,3), // brandish
            pose(-15,0,0,  0,0,0,   -110,35,0, -110,-35,0, 5,-3,-3,  -5,3,3), // honor
            pose(-15,0,0,  0,0,0,   -110,-35,0,-110,35,0,  5,-3,-3,  -5,3,3), // entertain
            pose(0,0,0,    0,0,0,   10,0,-5,   -70,-40,0,  -1,0,-1,  1,0,1),  // salute
            pose(16,20,0,  0,0,0,   4,8,237,   246,0,89,   -14,-18,-16,8,20,4),// riposte
            pose(-10,0,-5, 0,0,0,   -105,0,0,  -100,0,0,   7,0,0,   -46,0,0), // zombie
            pose(-5,18,0,  0,22,0,  8,0,-114,  0,84,111,   -111,55,0, 0,23,-13),  // cancan_a
            pose(-10,-20,0,0,-18,0, 0,0,-112,  8,90,111,   0,0,13,  -119,-42,0),  // cancan_b
            pose(-4,67,0,  0,8,0,   16,32,-8,  -99,63,0,   0,-75,-8, 4,63,8)  // hero
    );

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!(event.getTarget() instanceof ArmorStand stand)) return;

        Player player = event.getEntity();
        var held = player.getMainHandItem();

        if (!stand.showArms()) {
            if (!held.is(Items.STICK)) return;
            event.setCanceled(true);
            stand.setShowArms(true);
            applyPose(stand, 0);
            if (!player.isCreative()) {
                held.shrink(1);
            }
            return;
        }

        if (player.isShiftKeyDown()) {
            event.setCanceled(true);
            int current = stand.getPersistentData().getIntOr(POSE_KEY, 0);
            int next = (current + 1) % POSES.size();
            applyPose(stand, next);
        }
    }

    private void applyPose(ArmorStand stand, int index) {
        float[][] pose = POSES.get(index);
        stand.getPersistentData().putInt(POSE_KEY, index);

        stand.setHeadPose(new Rotations(pose[0][0], pose[0][1], pose[0][2]));
        stand.setBodyPose(new Rotations(pose[1][0], pose[1][1], pose[1][2]));
        stand.setLeftArmPose(new Rotations(pose[2][0], pose[2][1], pose[2][2]));
        stand.setRightArmPose(new Rotations(pose[3][0], pose[3][1], pose[3][2]));
        stand.setLeftLegPose(new Rotations(pose[4][0], pose[4][1], pose[4][2]));
        stand.setRightLegPose(new Rotations(pose[5][0], pose[5][1], pose[5][2]));
    }

    private static ListTag rotList(float[] deg) {
        ListTag list = new ListTag();
        for (float d : deg) {
            list.add(FloatTag.valueOf((float) Math.toRadians(d)));
        }
        return list;
    }

    private static float[][] pose(
            float hx, float hy, float hz,
            float bx, float by, float bz,
            float lax, float lay, float laz,
            float rax, float ray, float raz,
            float llx, float lly, float llz,
            float rlx, float rly, float rlz) {
        return new float[][]{
                {hx, hy, hz}, {bx, by, bz},
                {lax, lay, laz}, {rax, ray, raz},
                {llx, lly, llz}, {rlx, rly, rlz}
        };
    }
}