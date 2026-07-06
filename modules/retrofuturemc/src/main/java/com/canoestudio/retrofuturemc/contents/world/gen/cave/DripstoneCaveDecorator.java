package com.canoestudio.retrofuturemc.contents.world.gen.cave;

import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.contents.blocks.PointedDripstoneBlock;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainCaveDecorationContext;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainUndergroundBiomeDefinition;
import net.minecraft.util.EnumFacing;

public class DripstoneCaveDecorator extends AbstractRetroFutureCaveDecorator {
    @Override
    public void decorate(ModernCaveTerrainCaveDecorationContext context, ModernCaveTerrainUndergroundBiomeDefinition definition) {
        for (int x = 1; x < 15; x++) {
            for (int z = 1; z < 15; z++) {
                int surfaceY = getSurfaceY(context, x, z);
                int maxY = Math.min(Math.min(definition.getMaxY(), 96), surfaceY - 6);
                for (int y = definition.getMinY(); y <= maxY; y++) {
                    if (!matchesBiome(context, definition, x, y, z, surfaceY)) {
                        continue;
                    }

                    int worldX = context.getBlockX(x);
                    int worldZ = context.getBlockZ(z);
                    double patch = detailNoise(context, 0x4452495053544F4EL, worldX, y, worldZ, 0.04D);
                    if (patch < -0.26D) {
                        continue;
                    }

                    decorateCeiling(context, x, y, z, patch);
                    decorateFloor(context, x, y, z, patch);
                    if (patch > 0.0D) {
                        placeGlowLichen(context, x, y, z, 0.01D + patch * 0.018D, 0x445249504C494348L);
                    }
                }
            }
        }
    }

    private void decorateCeiling(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z, double patch) {
        if (!isAir(context, x, y, z) || !canReplaceNatural(context, x, y + 1, z)) {
            return;
        }

        double dripstoneBlockChance = 0.23D + patch * 0.18D;
        if (randomDouble(context, x, y, z, 0x445249505F434549L) < dripstoneBlockChance) {
            context.setBlockState(x, y + 1, z, ModBlocks.DRIPSTONE_BLOCK.getDefaultState());
        }

        if (context.getBlockState(x, y + 1, z).getBlock() == ModBlocks.DRIPSTONE_BLOCK
                && randomDouble(context, x, y, z, 0x5354414C414354L) < 0.16D + patch * 0.12D) {
            placeColumn(context, x, y, z, EnumFacing.DOWN, chooseColumnLength(context, x, y, z, patch, 0x5354414C4CL));
        }
    }

    private void decorateFloor(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z, double patch) {
        if (!isAir(context, x, y, z) || !canReplaceNatural(context, x, y - 1, z)) {
            return;
        }

        double dripstoneBlockChance = 0.18D + patch * 0.14D;
        if (randomDouble(context, x, y, z, 0x445249505F464C52L) < dripstoneBlockChance) {
            context.setBlockState(x, y - 1, z, ModBlocks.DRIPSTONE_BLOCK.getDefaultState());
        }

        if (context.getBlockState(x, y - 1, z).getBlock() == ModBlocks.DRIPSTONE_BLOCK
                && randomDouble(context, x, y, z, 0x5354414C41474DL) < 0.12D + patch * 0.09D) {
            placeColumn(context, x, y, z, EnumFacing.UP, chooseColumnLength(context, x, y, z, patch, 0x475249505F5550L));
        }
    }

    private int chooseColumnLength(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z, double patch, long salt) {
        double roll = randomDouble(context, x, y, z, salt);
        int length = 1;
        if (roll < 0.42D + patch * 0.16D) {
            length++;
        }
        if (roll < 0.13D + patch * 0.08D) {
            length++;
        }
        if (patch > 0.34D && roll < 0.04D) {
            length++;
        }
        return length;
    }

    private void placeColumn(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z, EnumFacing direction, int maxLength) {
        int length = 0;
        for (int i = 0; i < maxLength; i++) {
            int cy = y + direction.getYOffset() * i;
            if (!isAir(context, x, cy, z) && !isWater(context, x, cy, z)) {
                break;
            }
            length++;
        }

        if (length == 0) {
            return;
        }

        for (int i = 0; i < length; i++) {
            int cy = y + direction.getYOffset() * i;
            context.setBlockState(x, cy, z, ModBlocks.POINTED_DRIPSTONE.getDefaultState()
                    .withProperty(PointedDripstoneBlock.VERTICAL_DIRECTION, direction)
                    .withProperty(PointedDripstoneBlock.THICKNESS, thicknessFor(i, length)));
        }
    }

    private PointedDripstoneBlock.Thickness thicknessFor(int index, int length) {
        if (length == 1) {
            return PointedDripstoneBlock.Thickness.TIP;
        }
        if (index == 0) {
            return PointedDripstoneBlock.Thickness.BASE;
        }
        if (index == length - 1) {
            return PointedDripstoneBlock.Thickness.TIP;
        }
        return index == length - 2 ? PointedDripstoneBlock.Thickness.FRUSTUM : PointedDripstoneBlock.Thickness.MIDDLE;
    }
}
