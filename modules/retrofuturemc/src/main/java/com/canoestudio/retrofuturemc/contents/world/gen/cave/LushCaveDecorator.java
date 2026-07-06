package com.canoestudio.retrofuturemc.contents.world.gen.cave;

import com.canoestudio.retrofuturemc.contents.blocks.CaveVine.CaveVine;
import com.canoestudio.retrofuturemc.contents.blocks.CaveVine.CaveVinePlant;
import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.contents.blocks.dripLeaf.SmallDripleaf;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainCaveDecorationContext;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainUndergroundBiomeDefinition;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

public class LushCaveDecorator extends AbstractRetroFutureCaveDecorator {
    @Override
    public void decorate(ModernCaveTerrainCaveDecorationContext context, ModernCaveTerrainUndergroundBiomeDefinition definition) {
        for (int x = 1; x < 15; x++) {
            for (int z = 1; z < 15; z++) {
                int surfaceY = getSurfaceY(context, x, z);
                int maxY = Math.min(Math.min(definition.getMaxY(), 90), surfaceY - 6);
                for (int y = definition.getMinY(); y <= maxY; y++) {
                    if (!matchesBiome(context, definition, x, y, z, surfaceY)) {
                        continue;
                    }

                    int worldX = context.getBlockX(x);
                    int worldZ = context.getBlockZ(z);
                    double patch = detailNoise(context, 0x4C5553485F434156L, worldX, y, worldZ, 0.045D);
                    if (patch < -0.22D) {
                        continue;
                    }

                    decorateFloor(context, x, y, z, patch);
                    decorateCeiling(context, x, y, z, patch);
                    if (patch > 0.1D) {
                        placeGlowLichen(context, x, y, z, 0.018D + patch * 0.025D, 0x4C5553485F4C4943L);
                    }
                }
            }
        }
    }

    private void decorateFloor(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z, double patch) {
        if (!isAir(context, x, y, z) || !canReplaceNatural(context, x, y - 1, z)) {
            return;
        }

        double moisture = hasNearbyWater(context, x, y, z) ? 0.16D : 0.0D;
        if (moisture > 0.0D && randomDouble(context, x, y, z, 0x4C5553485F434C59L) < 0.11D + patch * 0.08D) {
            context.setBlockState(x, y - 1, z, Blocks.CLAY.getDefaultState());
            return;
        }

        double mossChance = 0.34D + patch * 0.18D + moisture;
        if (randomDouble(context, x, y, z, 0x4D4F53535F464C52L) < mossChance) {
            context.setBlockState(x, y - 1, z, ModBlocks.MOSS_BLOCK.getDefaultState());

            if (isAir(context, x, y, z)) {
                plantOnMoss(context, x, y, z, patch, moisture);
            }
        } else if (randomDouble(context, x, y, z, 0x524F4F544544L) < 0.045D + moisture * 0.08D) {
            context.setBlockState(x, y - 1, z, ModBlocks.ROOTED_DIRT.getDefaultState());
        }
    }

    private void plantOnMoss(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z, double patch, double moisture) {
        double roll = randomDouble(context, x, y, z, 0x4C5553485F504C41L);

        if (roll < 0.13D + patch * 0.04D) {
            context.setBlockState(x, y, z, ModBlocks.MOSS_CARPET.getDefaultState());
            return;
        }

        if (roll < 0.21D + moisture * 0.18D && isAir(context, x, y + 1, z)) {
            context.setBlockState(x, y, z, ModBlocks.SMALL_DRIPLEAF.getDefaultState()
                    .withProperty(SmallDripleaf.HALF, BlockDoublePlant.EnumBlockHalf.LOWER)
                    .withProperty(SmallDripleaf.FACING, EnumFacing.byHorizontalIndex(randomInt(context, x, y, z, 0x445249504C454146L, 4))));
            context.setBlockState(x, y + 1, z, ModBlocks.SMALL_DRIPLEAF.getDefaultState()
                    .withProperty(SmallDripleaf.HALF, BlockDoublePlant.EnumBlockHalf.UPPER)
                    .withProperty(SmallDripleaf.FACING, EnumFacing.byHorizontalIndex(randomInt(context, x, y, z, 0x445249504C454146L, 4))));
            return;
        }

        if (roll < 0.29D + patch * 0.04D) {
            context.setBlockState(x, y, z, Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS));
            return;
        }

        if (roll < 0.315D) {
            context.setBlockState(x, y, z, randomDouble(context, x, y, z, 0x415A414C4541L) < 0.24D
                    ? ModBlocks.Flowering_Azalea.getDefaultState()
                    : ModBlocks.Azalea.getDefaultState());
        }
    }

    private void decorateCeiling(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z, double patch) {
        if (!isAir(context, x, y, z) || !isSolidCaveSupport(context, x, y + 1, z)) {
            return;
        }

        double roll = randomDouble(context, x, y, z, 0x4C5553485F434549L);
        if (roll < 0.035D + patch * 0.02D) {
            context.setBlockState(x, y + 1, z, ModBlocks.ROOTED_DIRT.getDefaultState());
            if (isAir(context, x, y, z) && randomDouble(context, x, y, z, 0x48414E47494E47L) < 0.72D) {
                context.setBlockState(x, y, z, ModBlocks.HANGING_ROOTS.getDefaultState());
            }
            return;
        }

        if (roll < 0.065D + patch * 0.035D) {
            placeCaveVines(context, x, y, z);
            return;
        }

        if (roll < 0.076D + patch * 0.02D) {
            context.setBlockState(x, y, z, ModBlocks.SPORE_BLOSSOM.getDefaultState());
        }
    }

    private void placeCaveVines(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z) {
        int maxLength = 1 + randomInt(context, x, y, z, 0x56494E455FL, 5);
        int length = 0;
        for (int dy = 0; dy < maxLength && y - dy >= 1; dy++) {
            if (!isAir(context, x, y - dy, z)) {
                break;
            }
            length++;
        }

        if (length == 0) {
            return;
        }

        for (int i = 0; i < length; i++) {
            boolean tip = i == length - 1;
            boolean berries = randomDouble(context, x, y - i, z, 0x474C4F5742455252L) < (tip ? 0.28D : 0.08D);
            if (tip) {
                context.setBlockState(x, y - i, z, ModBlocks.CAVE_VINE.getDefaultState()
                        .withProperty(CaveVine.BERRIES, berries)
                        .withProperty(CaveVine.AGE, length < maxLength ? 0 : 1));
            } else {
                context.setBlockState(x, y - i, z, ModBlocks.CAVE_VINE_PLANT.getDefaultState()
                        .withProperty(CaveVinePlant.BERRIES, berries));
            }
        }
    }
}
