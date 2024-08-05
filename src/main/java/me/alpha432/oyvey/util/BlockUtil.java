package me.alpha432.oyvey.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.Set;

import static me.alpha432.oyvey.util.traits.Util.mc;

public class BlockUtil {

    // Define your set of unsafe blocks
    private static final Set<Block> unSafeBlocks = new HashSet<>();

    // Example unsafe blocks initialization (adjust as needed)
    static {
        unSafeBlocks.add(Blocks.BEDROCK);
        unSafeBlocks.add(Blocks.OBSIDIAN);
        unSafeBlocks.add(Blocks.ENDER_CHEST);
        unSafeBlocks.add(Blocks.ANVIL);

        // Add more blocks as needed
    }

    public static boolean isBlockUnSafe(Block block) {
        return unSafeBlocks.contains(block);
    }

    public static boolean isPosInFov(BlockPos pos) {
        int dirnumber = RotationUtil.getDirection4D();
        double playerX = MinecraftClient.getInstance().player.getX();
        double playerZ = MinecraftClient.getInstance().player.getZ();

        switch (dirnumber) {
            case 0:
                return pos.getZ() - playerZ >= 0.0;
            case 1:
                return pos.getX() - playerX <= 0.0;
            case 2:
                return pos.getZ() - playerZ <= 0.0;
            case 3:
                return pos.getX() - playerX >= 0.0;
            default:
                return false;
        }
    }
    public static boolean isHole(BlockPos pos) {
        return isHole(pos, true, false, false);
    }
    public static boolean isHole(BlockPos pos, boolean canStand, boolean checkTrap, boolean anyBlock) {
        int blockProgress = 0;
        for (Direction i : Direction.values()) {
            if (i == Direction.UP || i == Direction.DOWN) continue;
            if (anyBlock && !mc.world.isAir(pos.offset(i)) || CombatUtil.isHard(pos.offset(i)))
                blockProgress++;
        }
        return
                (
                        !checkTrap || (getBlock(pos) == Blocks.AIR
                                && getBlock(pos.add(0, 1, 0)) == Blocks.AIR
                                && getBlock(pos.add(0, 2, 0)) == Blocks.AIR)
                )
                        && blockProgress > 3
                        && (!canStand || getState(pos.add(0, -1, 0)).blocksMovement());
    }
    public static BlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }
    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }
}
