package com.canoestudio.retrofuturemc.contents.world.gen.cave;

import com.canoestudio.retrofuturemc.contents.blocks.GlowLichenBlock;
import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.contents.world.gen.noise.OpenSimplex2;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainCaveDecorationContext;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainUndergroundBiomeDefinition;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainUndergroundBiomeDecorator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

abstract class AbstractRetroFutureCaveDecorator implements ModernCaveTerrainUndergroundBiomeDecorator {
    private static final long HASH_X = 341873128712L;
    private static final long HASH_Y = 132897987541L;
    private static final long HASH_Z = 42317861L;

    protected static final int MIN_LOCAL = 0;
    protected static final int MAX_LOCAL = 15;

    protected boolean matchesBiome(ModernCaveTerrainCaveDecorationContext context, ModernCaveTerrainUndergroundBiomeDefinition definition, int x, int y, int z, int surfaceY) {
        return isUndergroundCaveSpace(context, x, y, z, surfaceY)
                && definition.getId().equals(context.sampleUndergroundBiomeLocal(x, y, z).getBiomeId());
    }

    protected boolean isInside(int x, int y, int z) {
        return x >= MIN_LOCAL && x <= MAX_LOCAL && z >= MIN_LOCAL && z <= MAX_LOCAL && y >= 1 && y <= 254;
    }

    protected int getSurfaceY(ModernCaveTerrainCaveDecorationContext context, int x, int z) {
        for (int y = 255; y >= 1; y--) {
            if (isTerrainSurfaceBlock(context.getBlockState(x, y, z))) {
                return y;
            }
        }

        return 0;
    }

    private boolean isUndergroundCaveSpace(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z, int surfaceY) {
        return isAir(context, x, y, z)
                && surfaceY > 0
                && y <= surfaceY - 6
                && hasNaturalCeiling(context, x, y, z);
    }

    private boolean hasNaturalCeiling(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z) {
        for (int dy = 1; dy <= 32 && y + dy <= 254; dy++) {
            if (isSolidCaveSupport(context, x, y + dy, z)) {
                return true;
            }
        }

        return false;
    }

    protected boolean isAir(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z) {
        return isInside(x, y, z) && context.getBlockState(x, y, z).getBlock() == Blocks.AIR;
    }

    protected boolean isWater(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z) {
        return isInside(x, y, z) && context.getBlockState(x, y, z).getMaterial() == Material.WATER;
    }

    protected boolean canReplaceNatural(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z) {
        return isInside(x, y, z) && isNaturalReplaceable(context.getBlockState(x, y, z));
    }

    protected boolean isSolidCaveSupport(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z) {
        if (!isInside(x, y, z)) {
            return false;
        }

        IBlockState state = context.getBlockState(x, y, z);
        Block block = state.getBlock();
        return isNaturalCaveBlock(state)
                || block == ModBlocks.MOSS_BLOCK
                || block == ModBlocks.ROOTED_DIRT
                || block == ModBlocks.DRIPSTONE_BLOCK;
    }

    protected boolean isNaturalReplaceable(IBlockState state) {
        return isNaturalCaveBlock(state) && state.getBlock() != Blocks.GRASS && state.getBlock() != Blocks.MYCELIUM;
    }

    private boolean isTerrainSurfaceBlock(IBlockState state) {
        Block block = state.getBlock();
        return isNaturalCaveBlock(state)
                || block == Blocks.GRASS
                || block == Blocks.MYCELIUM
                || block == Blocks.SNOW
                || block == Blocks.ICE
                || block == Blocks.PACKED_ICE;
    }

    private boolean isNaturalCaveBlock(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.STONE
                || block == Blocks.DIRT
                || block == Blocks.SAND
                || block == Blocks.SANDSTONE
                || block == Blocks.RED_SANDSTONE
                || block == Blocks.GRAVEL
                || block == Blocks.CLAY
                || block == Blocks.HARDENED_CLAY
                || block == Blocks.STAINED_HARDENED_CLAY
                || block == ModBlocks.DeepSlate
                || block == ModBlocks.TUFF;
    }

    protected boolean hasNearbyWater(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (Math.abs(dx) + Math.abs(dz) > 3) {
                        continue;
                    }
                    if (isWater(context, x + dx, y + dy, z + dz)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    protected void placeGlowLichen(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z, double chance, long salt) {
        if (!isAir(context, x, y, z) || randomDouble(context, x, y, z, salt) >= chance) {
            return;
        }

        int start = randomInt(context, x, y, z, salt ^ 0x4C494348454EL, EnumFacing.values().length);
        for (int i = 0; i < EnumFacing.values().length; i++) {
            EnumFacing face = EnumFacing.values()[(start + i) % EnumFacing.values().length];
            int sx = x + face.getXOffset();
            int sy = y + face.getYOffset();
            int sz = z + face.getZOffset();
            if (isSolidCaveSupport(context, sx, sy, sz)) {
                context.setBlockState(x, y, z, ((GlowLichenBlock)ModBlocks.GLOW_LICHEN).getStateForFace(face));
                return;
            }
        }
    }

    protected double detailNoise(ModernCaveTerrainCaveDecorationContext context, long salt, int worldX, int y, int worldZ, double frequency) {
        return OpenSimplex2.fractal3_ImproveXZ(context.getWorld().getSeed() ^ salt, worldX, y * 1.7D, worldZ, 2, frequency, 2.0D, 0.5D);
    }

    protected double randomDouble(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z, long salt) {
        long hash = context.getWorld().getSeed() ^ salt;
        hash ^= (long)context.getBlockX(x) * HASH_X;
        hash ^= (long)y * HASH_Y;
        hash ^= (long)context.getBlockZ(z) * HASH_Z;
        hash ^= hash >> 33;
        hash *= 0xff51afd7ed558ccdL;
        hash ^= hash >> 33;
        hash *= 0xc4ceb9fe1a85ec53L;
        hash ^= hash >> 33;
        return ((hash >>> 11) * 0x1.0p-53D);
    }

    protected int randomInt(ModernCaveTerrainCaveDecorationContext context, int x, int y, int z, long salt, int bound) {
        return (int)(randomDouble(context, x, y, z, salt) * bound);
    }
}
