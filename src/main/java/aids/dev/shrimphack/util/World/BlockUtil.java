package aids.dev.shrimphack.util.World;

import aids.dev.shrimphack.Shrimphack;
import aids.dev.shrimphack.util.Combat.CombatUtil;
import aids.dev.shrimphack.util.Combat.EntityUtil;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.*;


import java.util.*;

import static aids.dev.shrimphack.util.World.PlayerUtility.getWorldActionId;
import static aids.dev.shrimphack.util.traits.Util.mc;

public class BlockUtil {

    // Define your set of unsafe blocks
    private static final Set<Block> unSafeBlocks = new HashSet<>();
    public static final List<Block> shiftBlocks = Arrays.asList(
            Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE,
            Blocks.BIRCH_TRAPDOOR, Blocks.BAMBOO_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.CHERRY_TRAPDOOR,
            Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER,
            Blocks.ACACIA_TRAPDOOR, Blocks.ENCHANTING_TABLE, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX
    );
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
    public static boolean isAir(BlockPos pos) {
        return mc.world.isAir(pos);
    }
    public static boolean isMining(BlockPos pos) {
        return Shrimphack.mineManager.isMining(pos);
    }
    public static boolean canPlace(BlockPos pos) {
        return canPlace(pos, 1000);
    }
    public static boolean canPlace(BlockPos pos, double distance) {
        if (getPlaceSide(pos, distance) == null) return false;
        if (!canReplace(pos)) return false;
        return !hasEntity(pos, false);
    }
    public static boolean canPlace(BlockPos pos, double distance, boolean ignoreCrystal) {
        if (getPlaceSide(pos, distance) == null) return false;
        if (!canReplace(pos)) return false;
        return !hasEntity(pos, ignoreCrystal);
    }
    public static boolean clientCanPlace(BlockPos pos) {
        return clientCanPlace(pos, false);
    }
    public static boolean clientCanPlace(BlockPos pos, boolean ignoreCrystal) {
        if (!canReplace(pos)) return false;
        return !hasEntity(pos, ignoreCrystal);
    }

    public static boolean hasEntity(BlockPos pos, boolean ignoreCrystal) {
        for (Entity entity : mc.world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
            if (!entity.isAlive() || entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof ExperienceBottleEntity || entity instanceof ArrowEntity || ignoreCrystal && entity instanceof EndCrystalEntity || entity instanceof ArmorStandEntity)
                continue;
            return true;
        }
        return false;
    }
    public static boolean hasCrystal(BlockPos pos) {
        for (Entity entity : mc.world.getNonSpectatingEntities(EndCrystalEntity.class, new Box(pos))) {
            if (!entity.isAlive() || !(entity instanceof EndCrystalEntity))
                continue;
            return true;
        }
        return false;
    }

    public static boolean hasEntityBlockCrystal(BlockPos pos, boolean ignoreCrystal) {
        for (Entity entity : mc.world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
            if (!entity.isAlive() || ignoreCrystal && entity instanceof EndCrystalEntity || entity instanceof ArmorStandEntity)
                continue;
            return true;
        }
        return false;
    }

    public static boolean hasEntityBlockCrystal(BlockPos pos, boolean ignoreCrystal, boolean ignoreItem) {
        for (Entity entity : mc.world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
            if (!entity.isAlive() || ignoreItem && entity instanceof ItemEntity || ignoreCrystal && entity instanceof EndCrystalEntity || entity instanceof ArmorStandEntity)
                continue;
            return true;
        }
        return false;
    }


    public static Direction getBestNeighboring(BlockPos pos, Direction facing) {
        for (Direction i : Direction.values()) {
            if (facing != null && pos.offset(i).equals(pos.offset(facing, -1)) || i == Direction.DOWN) continue;
            if (getPlaceSide(pos, false, true) != null) return i;
        }
        Direction bestFacing = null;
        double distance = 0;
        for (Direction i : Direction.values()) {
            if (facing != null && pos.offset(i).equals(pos.offset(facing, -1)) || i == Direction.DOWN) continue;
            if (getPlaceSide(pos) != null) {
                if (bestFacing == null || mc.player.squaredDistanceTo(pos.offset(i).toCenterPos()) < distance) {
                    bestFacing = i;
                    distance = mc.player.squaredDistanceTo(pos.offset(i).toCenterPos());
                }
            }
        }
        return bestFacing;
    }
    public static boolean canPlaceCrystal(BlockPos pos) {
        BlockPos obsPos = pos.down();
        BlockPos boost = obsPos.up();
        return (getBlock(obsPos) == Blocks.BEDROCK || getBlock(obsPos) == Blocks.OBSIDIAN)
                && getClickSideStrict(obsPos) != null
                && (getBlock(boost) == Blocks.AIR)
                && !BlockUtil.hasEntityBlockCrystal(boost, false)
                && !BlockUtil.hasEntityBlockCrystal(boost.up(), false)
                && getBlock(boost.up()) == Blocks.AIR;
    }
    public static void placeCrystal(BlockPos pos, boolean rotate) {
        boolean offhand = mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL;
        BlockPos obsPos = pos.down();
        Direction facing = BlockUtil.getClickSide(obsPos);
        Vec3d vec = obsPos.toCenterPos().add(facing.getVector().getX() * 0.5,facing.getVector().getY() * 0.5,facing.getVector().getZ() * 0.5);
        if (rotate) {
            EntityUtil.faceVector(vec);
        }
        BlockUtil.clickBlock(obsPos, facing, false, offhand ? Hand.OFF_HAND : Hand.MAIN_HAND);
    }
    public static final ArrayList<BlockPos> placedPos = new ArrayList<>();

    public static void placeBlock(BlockPos pos, boolean rotate) {
        placeBlock(pos, rotate);
    }
    public static Direction getClickSide(BlockPos pos) {
        if (pos.equals(EntityUtil.getPlayerPos())) {
            return Direction.UP;
        }
        Direction side = null;
        double range = 100;
        for (Direction i : Direction.values()) {
            if (!EntityUtil.canSee(pos, i)) continue;
            if (MathHelper.sqrt((float) mc.player.squaredDistanceTo(pos.offset(i).toCenterPos())) > range) continue;
            side = i;
            range = MathHelper.sqrt((float) mc.player.squaredDistanceTo(pos.offset(i).toCenterPos()));
        }
        if (side != null)
            return side;
        side = Direction.UP;
        for (Direction i : Direction.values()) {
           /* if (placement.getValue() == Placement.Strict) {
                if (!isStrictDirection(pos, i)) continue;
                if (!isAir(pos.offset(i))) continue;
            }
            */
            if (MathHelper.sqrt((float) mc.player.squaredDistanceTo(pos.offset(i).toCenterPos())) > range) continue;
            side = i;
            range = MathHelper.sqrt((float) mc.player.squaredDistanceTo(pos.offset(i).toCenterPos()));
        }
        return side;
    }
    public static Direction getClickSideStrict(BlockPos pos) {
        if (pos.equals(EntityUtil.getPlayerPos())) {
            return Direction.UP;
        }
        Direction side = null;
        double range = 100;
        for (Direction i : Direction.values()) {
            if (!EntityUtil.canSee(pos, i)) continue;
            if (MathHelper.sqrt((float) mc.player.squaredDistanceTo(pos.offset(i).toCenterPos())) > range) continue;
            side = i;
            range = MathHelper.sqrt((float) mc.player.squaredDistanceTo(pos.offset(i).toCenterPos()));
        }
        if (side != null)
            return side;
        side = Direction.UP;
        for (Direction i : Direction.values()) {
           /* if (placement.getValue() == Placement.Strict) {
                if (!isStrictDirection(pos, i)) continue;
                if (!isAir(pos.offset(i))) continue;
            }
            */
            if (MathHelper.sqrt((float) mc.player.squaredDistanceTo(pos.offset(i).toCenterPos())) > range) continue;
            side = i;
            range = MathHelper.sqrt((float) mc.player.squaredDistanceTo(pos.offset(i).toCenterPos()));
        }
        return side;
    }
    public static void clickBlock(BlockPos pos, Direction side, boolean rotate) {
        clickBlock(pos, side, rotate, Hand.MAIN_HAND);
    }

    public static void clickBlock(BlockPos pos, Direction side, boolean rotate, Hand hand) {
        clickBlock(pos, side, rotate, hand);
    }

    public static void clickBlock(BlockPos pos, Direction side, boolean rotate, boolean packet) {
        clickBlock(pos, side, rotate, Hand.MAIN_HAND, packet);
    }

    public static void clickBlock(BlockPos pos, Direction side, boolean rotate, Hand hand, boolean packet) {
        Vec3d directionVec = new Vec3d(pos.getX() + 0.5 + side.getVector().getX() * 0.5, pos.getY() + 0.5 + side.getVector().getY() * 0.5, pos.getZ() + 0.5 + side.getVector().getZ() * 0.5);
        if (rotate) {
            EntityUtil.faceVector(directionVec);
        }
        BlockHitResult result = new BlockHitResult(directionVec, side, pos, false);
        if (packet) {
            mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, getWorldActionId(mc.world)));
        } else {
            mc.interactionManager.interactBlock(mc.player, hand, result);
        }
    }
    public static Direction getPlaceSide(BlockPos pos) {
        return getPlaceSide(pos);
    }

    public static Direction getPlaceSide(BlockPos pos, double distance) {
        double dis = 114514;
        Direction side = null;
        for (Direction i : Direction.values()) {
            if (canClick(pos.offset(i)) && !canReplace(pos.offset(i))) {
                    if (!EntityUtil.canSee(pos.offset(i), i.getOpposite())) continue;
                    if (!isStrictDirection(pos.offset(i), i.getOpposite())) continue;
                }
                double vecDis = mc.player.squaredDistanceTo(pos.toCenterPos().add(i.getVector().getX() * 0.5, i.getVector().getY() * 0.5, i.getVector().getZ() * 0.5));
                if (MathHelper.sqrt((float) vecDis) > distance) {
                    continue;
                }
                if (side == null || vecDis < dis) {
                    side = i;
                    dis = vecDis;
                }
            }

        if (side == null) {
            for (Direction i : Direction.values()) {
                if (mc.world.isAir(pos.offset(i))) {
                    return i;
                }
            }
        }
        return side;
    }
    public static Direction getPlaceSide(BlockPos pos, boolean strict, boolean legit) {
        double dis = 114514;
        Direction side = null;
        for (Direction i : Direction.values()) {
            if (canClick(pos.offset(i)) && !canReplace(pos.offset(i))) {
                if (legit) {
                    if (!EntityUtil.canSee(pos.offset(i), i.getOpposite())) continue;
                }
                if (strict) {
                    if (!isStrictDirection(pos.offset(i), i.getOpposite())) continue;
                }
                double vecDis = mc.player.squaredDistanceTo(pos.toCenterPos().add(i.getVector().getX() * 0.5, i.getVector().getY() * 0.5, i.getVector().getZ() * 0.5));
                if (side == null || vecDis < dis) {
                    side = i;
                    dis = vecDis;
                }
            }
        }
        if (side == null) {
            for (Direction i : Direction.values()) {
                if (mc.world.isAir(pos.offset(i))) {
                    return i;
                }
            }
        }
        return side;
    }
    public static boolean isStrictDirection(BlockPos pos, Direction side) {
        BlockState blockState = mc.world.getBlockState(pos);
        boolean isFullBox = blockState.getBlock() == Blocks.AIR || blockState.isFullCube(mc.world, pos) || getBlock(pos) == Blocks.COBWEB;
        return isStrictDirection(pos, side, isFullBox);
    }

    public static boolean isStrictDirection(BlockPos pos, Direction side, boolean isFullBox) {
        if (EntityUtil.getPlayerPos().getY() - pos.getY() >= 0 && side == Direction.DOWN) return false;
        if (getBlock(pos.offset(side)) == Blocks.OBSIDIAN || getBlock(pos.offset(side)) == Blocks.BEDROCK || getBlock(pos.offset(side)) == Blocks.RESPAWN_ANCHOR) return false;
        Vec3d eyePos = EntityUtil.getEyesPos();
        Vec3d blockCenter = pos.toCenterPos();
        ArrayList<Direction> validAxis = new ArrayList<>();
        validAxis.addAll(checkAxis(eyePos.x - blockCenter.x, Direction.WEST, Direction.EAST, !isFullBox));
        validAxis.addAll(checkAxis(eyePos.y - blockCenter.y, Direction.DOWN, Direction.UP, true));
        validAxis.addAll(checkAxis(eyePos.z - blockCenter.z, Direction.NORTH, Direction.SOUTH, !isFullBox));
        return validAxis.contains(side);
    }

    public static ArrayList<Direction> checkAxis(double diff, Direction negativeSide, Direction positiveSide, boolean bothIfInRange) {
        ArrayList<Direction> valid = new ArrayList<>();
        if (diff < -0.5) {
            valid.add(negativeSide);
        }
        if (diff > 0.5) {
            valid.add(positiveSide);
        }
        if (bothIfInRange) {
            if (!valid.contains(negativeSide)) valid.add(negativeSide);
            if (!valid.contains(positiveSide)) valid.add(positiveSide);
        }
        return valid;
    }
    public static boolean canClick(BlockPos pos) {
        return mc.world.getBlockState(pos).isSolid() && (!(shiftBlocks.contains(getBlock(pos)) || getBlock(pos) instanceof BedBlock) || mc.player.isSneaking());
    }
    public static void airPlace(){
    }
    public static boolean canReplace(BlockPos pos) {
        return getState(pos).isReplaceable();
    }


}

