package com.canoestudio.retrofuturemc.contents.world.gen.cave;

import com.canoestudio.retrofuturemc.contents.world.gen.noise.OpenSimplex2;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainAPI;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainCaveBiomeType;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainUndergroundBiomeDefinition;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainUndergroundBiomeSample;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;

public final class RetroFutureCaveBiomes {
    public static final ResourceLocation LUSH_CAVES = new ResourceLocation(Tags.MOD_ID, "lush_caves");
    public static final ResourceLocation DRIPSTONE_CAVES = new ResourceLocation(Tags.MOD_ID, "dripstone_caves");

    private static boolean registered;

    private RetroFutureCaveBiomes() {
    }

    public static void register() {
        if (registered) {
            return;
        }

        ModernCaveTerrainAPI.registerUndergroundBiome(ModernCaveTerrainUndergroundBiomeDefinition.builder(LUSH_CAVES)
                .caveBiomeType(ModernCaveTerrainCaveBiomeType.GENERIC_3D)
                .yRange(8, 72)
                .priority(40)
                .selector(RetroFutureCaveBiomes::lushWeight)
                .decorator(new LushCaveDecorator())
                .build());

        ModernCaveTerrainAPI.registerUndergroundBiome(ModernCaveTerrainUndergroundBiomeDefinition.builder(DRIPSTONE_CAVES)
                .caveBiomeType(ModernCaveTerrainCaveBiomeType.GENERIC_3D)
                .yRange(6, 80)
                .priority(38)
                .selector(RetroFutureCaveBiomes::dripstoneWeight)
                .decorator(new DripstoneCaveDecorator())
                .build());

        registered = true;
    }

    private static double lushWeight(World world, ModernCaveTerrainUndergroundBiomeSample sample) {
        if (!canReplaceGeneric(sample)) {
            return 0.0D;
        }

        double climate = smoothstep(0.0D, 0.78D, sample.getHumidity())
                * smoothstep(-0.45D, 0.45D, sample.getTemperature())
                * (1.0D - smoothstep(0.82D, 1.1D, sample.getDepth()));

        if (BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.DRY)
                || BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.SANDY)
                || BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.NETHER)
                || BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.END)) {
            climate *= 0.2D;
        }

        if (BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.WET)
                || BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.JUNGLE)
                || BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.SWAMP)) {
            climate += 0.22D;
        }

        double region = OpenSimplex2.fractal3_ImproveXZ(world.getSeed() ^ 0x4C5553485F524547L,
                sample.getBlockX(), sample.getBlockY() * 0.65D, sample.getBlockZ(), 3, 0.012D, 2.0D, 0.52D);
        double score = climate + region * 0.42D - Math.max(0.0D, -sample.getWeirdness()) * 0.08D;
        return score > 0.47D ? clamp((score - 0.47D) * 2.8D, 0.0D, 1.0D) : 0.0D;
    }

    private static double dripstoneWeight(World world, ModernCaveTerrainUndergroundBiomeSample sample) {
        if (!canReplaceGeneric(sample)) {
            return 0.0D;
        }

        double dry = 1.0D - smoothstep(0.05D, 0.82D, sample.getHumidity());
        double warmEnough = smoothstep(-0.65D, 0.18D, sample.getTemperature());
        double depth = smoothstep(0.18D, 0.78D, sample.getDepth()) * (1.0D - smoothstep(1.05D, 1.2D, sample.getDepth()));
        double erosion = smoothstep(-0.75D, 0.15D, sample.getErosion());
        double climate = dry * 0.55D + warmEnough * 0.18D + depth * 0.32D + erosion * 0.14D;

        if (BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.WET)
                || BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.SNOWY)
                || BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.OCEAN)) {
            climate *= 0.55D;
        }

        if (BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.DRY)
                || BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.SANDY)
                || BiomeDictionary.hasType(sample.getSurfaceBiome(), BiomeDictionary.Type.MESA)) {
            climate += 0.2D;
        }

        double region = OpenSimplex2.fractal3_ImproveXZ(world.getSeed() ^ 0x445249505F524547L,
                sample.getBlockX(), sample.getBlockY() * 0.75D, sample.getBlockZ(), 3, 0.011D, 2.0D, 0.5D);
        double score = climate + region * 0.36D;
        return score > 0.64D ? clamp((score - 0.64D) * 2.5D, 0.0D, 1.0D) : 0.0D;
    }

    private static boolean canReplaceGeneric(ModernCaveTerrainUndergroundBiomeSample sample) {
        return sample.getCaveBiomeType() == ModernCaveTerrainCaveBiomeType.GENERIC_3D
                || sample.getCaveBiomeType() == ModernCaveTerrainCaveBiomeType.NORMAL;
    }

    private static double smoothstep(double min, double max, double value) {
        double normalized = clamp((value - min) / (max - min), 0.0D, 1.0D);
        return normalized * normalized * (3.0D - 2.0D * normalized);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
