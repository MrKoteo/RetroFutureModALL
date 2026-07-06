package com.canoestudio.retrofuturemc.contents.world.gen;

import com.canoestudio.retrofuturemc.contents.blocks.AmethystClusterBlock;
import com.canoestudio.retrofuturemc.contents.blocks.CaveVine.CaveVine;
import com.canoestudio.retrofuturemc.contents.blocks.CaveVine.CaveVinePlant;
import com.canoestudio.retrofuturemc.contents.blocks.GlowLichenBlock;
import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.contents.blocks.PointedDripstoneBlock;
import com.canoestudio.retrofuturemc.contents.blocks.dripLeaf.BigDripleaf;
import com.canoestudio.retrofuturemc.contents.blocks.dripLeaf.DripleafStem;
import com.canoestudio.retrofuturemc.contents.blocks.dripLeaf.SmallDripleaf;
import com.canoestudio.retrofuturemc.contents.mobs.axolotl.EntityAxolotl;
import com.canoestudio.retrofuturemc.contents.mobs.glowsquid.EntityGlowSquid;
import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RetroFutureWorldGenerator implements IWorldGenerator {
    private static final IBlockState STONE = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE);
    private static final IBlockState DEEPSLATE = ModBlocks.DeepSlate.getDefaultState();
    private static final int GEODE_RARITY = 24;
    private static final int GEODE_MIN_Y = 15;
    private static final int GEODE_MAX_Y = 35;
    private static final int GEODE_MIN_OFFSET = -16;
    private static final int GEODE_MAX_OFFSET = 16;
    private static final int GEODE_MIN_OUTER_WALL_DISTANCE = 4;
    private static final int GEODE_MAX_OUTER_WALL_DISTANCE = 6;
    private static final int GEODE_MIN_DISTRIBUTION_POINTS = 3;
    private static final int GEODE_MAX_DISTRIBUTION_POINTS = 4;
    private static final int GEODE_MIN_POINT_OFFSET = 1;
    private static final int GEODE_MAX_POINT_OFFSET = 2;
    private static final int GEODE_CRACK_POINT_OFFSET = 2;
    private static final int GEODE_INVALID_BLOCKS_THRESHOLD = 1;
    private static final double GEODE_FILLING = 1.7D;
    private static final double GEODE_INNER_LAYER = 2.2D;
    private static final double GEODE_MIDDLE_LAYER = 3.2D;
    private static final double GEODE_OUTER_LAYER = 4.2D;
    private static final double GEODE_CRACK_CHANCE = 0.95D;
    private static final double GEODE_BASE_CRACK_SIZE = 2.0D;
    private static final double GEODE_NOISE_MULTIPLIER = 0.05D;
    private static final double GEODE_BUDDING_AMETHYST_CHANCE = 0.083D;
    private static final double GEODE_CRYSTAL_PLACEMENT_CHANCE = 0.35D;
    private static final int LEGACY_CAVE_MIN_Y = 8;
    private static final int LEGACY_CAVE_MAX_Y = 58;
    private static final int LEGACY_CAVE_DECORATION_ATTEMPTS = 64;
    private static final int LEGACY_CAVE_AIR_SCAN_DISTANCE = 24;
    private static final int LEGACY_CAVE_SURFACE_SCAN_DISTANCE = 14;
    private static final int LEGACY_LUSH_PATCH_RADIUS = 4;
    private static final int LEGACY_DRIPSTONE_PATCH_RADIUS = 5;
    private static final long LEGACY_CAVE_POSITION_SEED_SALT = 0x52464C4547434156L;
    private static final long LEGACY_CAVE_REGION_SEED_SALT = 0x5246434156454249L;
    private static final long LEGACY_LUSH_REGION_SEED_SALT = 0x52464C5553484341L;
    private static final long LEGACY_DRIPSTONE_REGION_SEED_SALT = 0x5246445249504341L;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() != 0) {
            return;
        }

        int blockX = chunkX * 16;
        int blockZ = chunkZ * 16;
        generateOres(world, random, blockX, blockZ);

        if (shouldGenerateAmethystGeode(world, random, blockX, blockZ)) {
            generateAmethystGeode(world, random, new BlockPos(
                    blockX + random.nextInt(16),
                    randomRange(random, GEODE_MIN_Y, GEODE_MAX_Y),
                    blockZ + random.nextInt(16)));
        }

        decorateLegacyCaves(world, new Random(getLegacyCaveSeed(world, chunkX, chunkZ, LEGACY_CAVE_POSITION_SEED_SALT)), blockX, blockZ);
        spawnAquaticCaveMobs(world, random, blockX, blockZ);
    }

    private void generateOres(World world, Random random, int blockX, int blockZ) {
        generateOre(world, random, ModBlocks.COPPER_ORE.getDefaultState(), 10, 16, 0, 64, blockX, blockZ, STONE);
        generateOre(world, random, ModBlocks.DEEPSLATE_COPPER_ORE.getDefaultState(), 6, 10, 0, 28, blockX, blockZ, DEEPSLATE);
        generateOre(world, random, ModBlocks.DEEPSLATE_IRON_ORE.getDefaultState(), 6, 8, 0, 24, blockX, blockZ, DEEPSLATE);
        generateOre(world, random, ModBlocks.DEEPSLATE_GOLD_ORE.getDefaultState(), 2, 8, 0, 24, blockX, blockZ, DEEPSLATE);
        generateOre(world, random, ModBlocks.DEEPSLATE_REDSTONE_ORE.getDefaultState(), 8, 7, 0, 16, blockX, blockZ, DEEPSLATE);
        generateOre(world, random, ModBlocks.DEEPSLATE_LAPIS_ORE.getDefaultState(), 1, 6, 0, 24, blockX, blockZ, DEEPSLATE);
        generateOre(world, random, ModBlocks.DEEPSLATE_DIAMOND_ORE.getDefaultState(), 1, 7, 0, 18, blockX, blockZ, DEEPSLATE);
        generateOre(world, random, ModBlocks.DEEPSLATE_EMERALD_ORE.getDefaultState(), 1, 3, 4, 28, blockX, blockZ, DEEPSLATE);

        if (random.nextInt(4) == 0) {
            replaceStonePatch(world, random, blockX + random.nextInt(16), 6 + random.nextInt(24), blockZ + random.nextInt(16), ModBlocks.TUFF.getDefaultState(), 24);
        }
    }

    private void generateOre(World world, Random random, IBlockState ore, int count, int size, int minY, int maxY, int blockX, int blockZ, IBlockState target) {
        for (int i = 0; i < count; i++) {
            BlockPos pos = new BlockPos(blockX + random.nextInt(16), minY + random.nextInt(Math.max(1, maxY - minY)), blockZ + random.nextInt(16));
            new WorldGenMinable(ore, size, state -> state != null && state.getBlock() == target.getBlock()).generate(world, random, pos);
        }
    }

    private void replaceStonePatch(World world, Random random, int x, int y, int z, IBlockState state, int radius) {
        BlockPos center = new BlockPos(x, y, z);
        for (int i = 0; i < radius; i++) {
            BlockPos pos = center.add(random.nextInt(9) - 4, random.nextInt(7) - 3, random.nextInt(9) - 4);
            if (isNaturalStone(world.getBlockState(pos))) {
                world.setBlockState(pos, state, 2);
            }
        }
    }

    private boolean shouldGenerateAmethystGeode(World world, Random random, int blockX, int blockZ) {
        if (random.nextInt(GEODE_RARITY) != 0) {
            return false;
        }

        Biome biome = world.getBiome(new BlockPos(blockX + 8, 0, blockZ + 8));
        return !BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN);
    }

    private boolean generateAmethystGeode(World world, Random random, BlockPos origin) {
        int pointCount = randomRange(random, GEODE_MIN_DISTRIBUTION_POINTS, GEODE_MAX_DISTRIBUTION_POINTS);
        List<GeodePoint> shellPoints = new ArrayList<>();
        int invalidPoints = 0;

        for (int i = 0; i < pointCount; i++) {
            BlockPos point = origin.add(
                    randomRange(random, GEODE_MIN_OUTER_WALL_DISTANCE, GEODE_MAX_OUTER_WALL_DISTANCE),
                    randomRange(random, GEODE_MIN_OUTER_WALL_DISTANCE, GEODE_MAX_OUTER_WALL_DISTANCE),
                    randomRange(random, GEODE_MIN_OUTER_WALL_DISTANCE, GEODE_MAX_OUTER_WALL_DISTANCE));

            if (isInvalidGeodeSample(world.getBlockState(point))) {
                invalidPoints++;
                if (invalidPoints > GEODE_INVALID_BLOCKS_THRESHOLD) {
                    return false;
                }
            }

            shellPoints.add(new GeodePoint(point, randomRange(random, GEODE_MIN_POINT_OFFSET, GEODE_MAX_POINT_OFFSET)));
        }

        List<BlockPos> crackPoints = new ArrayList<>();
        boolean shouldGenerateCrack = random.nextDouble() < GEODE_CRACK_CHANCE;
        if (shouldGenerateCrack) {
            addGeodeCrackPoints(random, origin, pointCount, crackPoints);
        }

        double crackSizeAdjustment = (double)pointCount / (double)GEODE_MAX_OUTER_WALL_DISTANCE;
        double innerAir = inverseSqrt(GEODE_FILLING);
        double innermostBlockLayer = inverseSqrt(GEODE_INNER_LAYER + crackSizeAdjustment);
        double innerCrust = inverseSqrt(GEODE_MIDDLE_LAYER + crackSizeAdjustment);
        double outerCrust = inverseSqrt(GEODE_OUTER_LAYER + crackSizeAdjustment);
        double crackSize = inverseSqrt(GEODE_BASE_CRACK_SIZE + random.nextDouble() / 2.0D + (pointCount > 3 ? crackSizeAdjustment : 0.0D));
        List<BlockPos> potentialCrystalPlacements = new ArrayList<>();

        for (int x = GEODE_MIN_OFFSET; x <= GEODE_MAX_OFFSET; x++) {
            for (int y = GEODE_MIN_OFFSET; y <= GEODE_MAX_OFFSET; y++) {
                for (int z = GEODE_MIN_OFFSET; z <= GEODE_MAX_OFFSET; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    double noiseOffset = smoothNoise(world.getSeed() ^ 0x47454F44454CL, pos.getX() * 0.12D, pos.getY() * 0.12D, pos.getZ() * 0.12D) * GEODE_NOISE_MULTIPLIER;
                    double shellDistance = 0.0D;
                    double crackDistance = 0.0D;

                    for (GeodePoint point : shellPoints) {
                        shellDistance += inverseSqrt(distanceSq(pos, point.pos) + point.offset) + noiseOffset;
                    }

                    for (BlockPos crackPoint : crackPoints) {
                        crackDistance += inverseSqrt(distanceSq(pos, crackPoint) + GEODE_CRACK_POINT_OFFSET) + noiseOffset;
                    }

                    if (shellDistance < outerCrust) {
                        continue;
                    }

                    if (shouldGenerateCrack && crackDistance >= crackSize && shellDistance < innerAir) {
                        if (setGeodeBlock(world, pos, Blocks.AIR.getDefaultState(), 2)) {
                            notifyAdjacentFluids(world, pos);
                        }
                    } else if (shellDistance >= innerAir) {
                        setGeodeBlock(world, pos, Blocks.AIR.getDefaultState(), 2);
                    } else if (shellDistance >= innermostBlockLayer) {
                        boolean budding = random.nextDouble() < GEODE_BUDDING_AMETHYST_CHANCE;
                        if (setGeodeBlock(world, pos, budding ? ModBlocks.BUDDING_AMETHYST.getDefaultState() : ModBlocks.AMETHYST_BLOCK.getDefaultState(), 2)
                                && budding && random.nextDouble() < GEODE_CRYSTAL_PLACEMENT_CHANCE) {
                            potentialCrystalPlacements.add(pos);
                        }
                    } else if (shellDistance >= innerCrust) {
                        setGeodeBlock(world, pos, ModBlocks.CALCITE.getDefaultState(), 2);
                    } else {
                        setGeodeBlock(world, pos, getGeodeOuterLayerState(world, pos), 2);
                    }
                }
            }
        }

        for (BlockPos crystalPos : potentialCrystalPlacements) {
            placeGeodeCrystal(world, random, crystalPos);
        }

        return true;
    }

    private void addGeodeCrackPoints(Random random, BlockPos origin, int pointCount, List<BlockPos> crackPoints) {
        int crackOffset = pointCount * 2 + 1;
        int side = random.nextInt(4);

        if (side == 0) {
            crackPoints.add(origin.add(crackOffset, 7, 0));
            crackPoints.add(origin.add(crackOffset, 5, 0));
            crackPoints.add(origin.add(crackOffset, 1, 0));
        } else if (side == 1) {
            crackPoints.add(origin.add(0, 7, crackOffset));
            crackPoints.add(origin.add(0, 5, crackOffset));
            crackPoints.add(origin.add(0, 1, crackOffset));
        } else if (side == 2) {
            crackPoints.add(origin.add(crackOffset, 7, crackOffset));
            crackPoints.add(origin.add(crackOffset, 5, crackOffset));
            crackPoints.add(origin.add(crackOffset, 1, crackOffset));
        } else {
            crackPoints.add(origin.add(0, 7, 0));
            crackPoints.add(origin.add(0, 5, 0));
            crackPoints.add(origin.add(0, 1, 0));
        }
    }

    private IBlockState getGeodeOuterLayerState(World world, BlockPos pos) {
        double texture = smoothNoise(world.getSeed() ^ 0x444447454F4445L, pos.getX() * 0.35D, pos.getY() * 0.35D, pos.getZ() * 0.35D);
        return texture > -0.35D ? ModBlocks.TUFF.getDefaultState() : STONE;
    }

    private void placeGeodeCrystal(World world, Random random, BlockPos supportPos) {
        Block crystal = getRandomGeodeCrystal(random);
        int start = random.nextInt(EnumFacing.values().length);

        for (int i = 0; i < EnumFacing.values().length; i++) {
            EnumFacing facing = EnumFacing.values()[(start + i) % EnumFacing.values().length];
            BlockPos placePos = supportPos.offset(facing);
            IBlockState target = world.getBlockState(placePos);

            if (canGeodeClusterGrowAtState(world, placePos, target) && canReplaceGeodeBlock(world, placePos, target)) {
                IBlockState crystalState = crystal.getDefaultState().withProperty(AmethystClusterBlock.FACING, facing);
                FluidState fluidState = getFluidState(world, placePos, target);
                world.setBlockState(placePos, crystalState, 3);
                if (fluidState.getFluid() == FluidRegistry.WATER) {
                    FluidloggedUtils.setFluidState(world, placePos, world.getBlockState(placePos), fluidState, false, 3);
                }
                return;
            }
        }
    }

    private Block getRandomGeodeCrystal(Random random) {
        int choice = random.nextInt(4);
        if (choice == 0) {
            return ModBlocks.SMALL_AMETHYST_BUD;
        }
        if (choice == 1) {
            return ModBlocks.MEDIUM_AMETHYST_BUD;
        }
        if (choice == 2) {
            return ModBlocks.LARGE_AMETHYST_BUD;
        }
        return ModBlocks.AMETHYST_CLUSTER;
    }

    private boolean isInvalidGeodeSample(IBlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.AIR
                || block == Blocks.BEDROCK
                || block == Blocks.ICE
                || block == Blocks.PACKED_ICE
                || material == Material.WATER
                || material == Material.LAVA;
    }

    private boolean setGeodeBlock(World world, BlockPos pos, IBlockState state, int flags) {
        IBlockState current = world.getBlockState(pos);
        if (!canReplaceGeodeBlock(world, pos, current)) {
            return false;
        }

        world.setBlockState(pos, state, flags);
        return true;
    }

    private boolean canReplaceGeodeBlock(World world, BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        return block != Blocks.BEDROCK
                && block != Blocks.MOB_SPAWNER
                && block != Blocks.CHEST
                && block != Blocks.TRAPPED_CHEST
                && block != Blocks.END_PORTAL_FRAME
                && block != Blocks.END_PORTAL
                && block != Blocks.PORTAL
                && block != Blocks.COMMAND_BLOCK
                && block != Blocks.CHAIN_COMMAND_BLOCK
                && block != Blocks.REPEATING_COMMAND_BLOCK
                && block != Blocks.STRUCTURE_BLOCK
                && !block.hasTileEntity(state);
    }

    private boolean canGeodeClusterGrowAtState(World world, BlockPos pos, IBlockState state) {
        return state.getBlock() == Blocks.AIR
                || state.getMaterial() == Material.WATER
                || FluidloggedUtils.getFluidState(world, pos, state).getFluid() == FluidRegistry.WATER;
    }

    private FluidState getFluidState(World world, BlockPos pos, IBlockState state) {
        return state.getMaterial() == Material.WATER ? FluidState.of(state) : FluidloggedUtils.getFluidState(world, pos, state);
    }

    private void notifyAdjacentFluids(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos adjacent = pos.offset(facing);
            IBlockState state = world.getBlockState(adjacent);
            if (state.getMaterial().isLiquid()) {
                world.scheduleUpdate(adjacent, state.getBlock(), 0);
            }
        }
    }

    private void decorateLegacyCaves(World world, Random random, int blockX, int blockZ) {
        CaveStyle style = chooseLegacyCaveStyle(world, blockX, blockZ);
        if (style == CaveStyle.NORMAL) {
            return;
        }

        for (int i = 0; i < LEGACY_CAVE_DECORATION_ATTEMPTS; i++) {
            BlockPos start = new BlockPos(
                    blockX + random.nextInt(16),
                    randomRange(random, LEGACY_CAVE_MIN_Y, Math.min(LEGACY_CAVE_MAX_Y, world.getActualHeight() - 8)),
                    blockZ + random.nextInt(16));
            BlockPos caveAir = findCaveAir(world, start, LEGACY_CAVE_AIR_SCAN_DISTANCE);
            if (caveAir == null || !isInsideChunk(caveAir, blockX, blockZ)) {
                continue;
            }

            if (style == CaveStyle.LUSH) {
                decorateLushCave(world, random, caveAir, blockX, blockZ, getLegacyLushStrength(world, caveAir));
            } else if (style == CaveStyle.DRIPSTONE) {
                decorateDripstoneCave(world, random, caveAir, blockX, blockZ, getLegacyDripstoneStrength(world, caveAir));
            }
        }
    }

    private CaveStyle chooseLegacyCaveStyle(World world, int blockX, int blockZ) {
        BlockPos surfacePos = new BlockPos(blockX + 8, 0, blockZ + 8);
        Biome biome = world.getBiome(surfacePos);
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) {
            return CaveStyle.NORMAL;
        }

        double region = smoothNoise(world.getSeed() ^ LEGACY_CAVE_REGION_SEED_SALT, blockX * 0.004D, 0.0D, blockZ * 0.004D);
        double lushScore = getLegacyLushBiomeBias(biome)
                + smoothNoise(world.getSeed() ^ LEGACY_LUSH_REGION_SEED_SALT, blockX * 0.011D, 0.0D, blockZ * 0.011D) * 0.45D
                - Math.max(0.0D, region) * 0.25D;
        double dripstoneScore = getLegacyDripstoneBiomeBias(biome)
                + smoothNoise(world.getSeed() ^ LEGACY_DRIPSTONE_REGION_SEED_SALT, blockX * 0.010D, 0.0D, blockZ * 0.010D) * 0.45D
                + Math.max(0.0D, region) * 0.20D;

        if (lushScore < 0.30D && dripstoneScore < 0.32D) {
            return CaveStyle.NORMAL;
        }

        return lushScore >= dripstoneScore ? CaveStyle.LUSH : CaveStyle.DRIPSTONE;
    }

    private double getLegacyLushBiomeBias(Biome biome) {
        double bias = 0.0D;
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.LUSH)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.JUNGLE)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST)) {
            bias += 0.45D;
        }
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)) {
            bias += 0.28D;
        }
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)) {
            bias -= 0.32D;
        }
        return bias;
    }

    private double getLegacyDripstoneBiomeBias(Biome biome) {
        double bias = 0.12D;
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.MOUNTAIN)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.HILLS)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.MESA)) {
            bias += 0.38D;
        }
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.JUNGLE)) {
            bias -= 0.24D;
        }
        return bias;
    }

    private double getLegacyLushStrength(World world, BlockPos pos) {
        double noise = smoothNoise(world.getSeed() ^ LEGACY_LUSH_REGION_SEED_SALT,
                pos.getX() * 0.035D, pos.getY() * 0.05D, pos.getZ() * 0.035D);
        return clamp(0.70D + noise * 0.30D, 0.35D, 1.0D);
    }

    private double getLegacyDripstoneStrength(World world, BlockPos pos) {
        double noise = smoothNoise(world.getSeed() ^ LEGACY_DRIPSTONE_REGION_SEED_SALT,
                pos.getX() * 0.032D, pos.getY() * 0.045D, pos.getZ() * 0.032D);
        return clamp(0.72D + noise * 0.28D, 0.35D, 1.0D);
    }

    private void decorateLushCave(World world, Random random, BlockPos caveAir, int blockX, int blockZ, double strength) {
        BlockPos floor = scanToSolid(world, caveAir, EnumFacing.DOWN, LEGACY_CAVE_SURFACE_SCAN_DISTANCE);
        BlockPos ceiling = scanToSolid(world, caveAir, EnumFacing.UP, LEGACY_CAVE_SURFACE_SCAN_DISTANCE);

        if (floor != null && floor.getY() > 3 && isInsideChunk(floor, blockX, blockZ) && isLushGroundReplaceable(world.getBlockState(floor))) {
            placeMossPatch(world, random, floor.up(), blockX, blockZ, 2 + random.nextInt(LEGACY_LUSH_PATCH_RADIUS), strength);
            if (random.nextDouble() < 0.36D * strength) {
                placeClayAndDripleafPatch(world, random, floor.up(), blockX, blockZ, strength);
            }
            if (random.nextDouble() < 0.10D * strength) {
                world.setBlockState(floor, ModBlocks.ROOTED_DIRT.getDefaultState(), 2);
            }
        }

        if (ceiling != null && ceiling.getY() < world.getActualHeight() - 2 && isInsideChunk(ceiling, blockX, blockZ)) {
            if (random.nextDouble() < 0.60D * strength) {
                placeCeilingMoss(world, random, ceiling, blockX, blockZ, 1 + random.nextInt(3), strength);
            }
            BlockPos hangingPos = ceiling.down();
            if (isReplaceableCavePlantTarget(world, hangingPos) && random.nextDouble() < 0.55D * strength) {
                placeCaveVine(world, random, hangingPos, blockX, blockZ);
            } else if (isReplaceableCavePlantTarget(world, hangingPos) && random.nextDouble() < 0.12D * strength) {
                world.setBlockState(hangingPos, ModBlocks.SPORE_BLOSSOM.getDefaultState(), 2);
            } else if (isReplaceableCavePlantTarget(world, hangingPos) && random.nextDouble() < 0.18D * strength) {
                world.setBlockState(hangingPos, ModBlocks.HANGING_ROOTS.getDefaultState(), 2);
            }
        }

        if (random.nextDouble() < 0.16D * strength) {
            placeGlowLichen(world, random, caveAir);
        }
    }

    private void placeMossPatch(World world, Random random, BlockPos center, int blockX, int blockZ, int radius, double strength) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius + random.nextInt(3)) {
                    continue;
                }

                BlockPos plantPos = center.add(dx, 0, dz);
                BlockPos groundPos = plantPos.down();
                if (!isInsideChunk(plantPos, blockX, blockZ)
                        || !isReplaceableCavePlantTarget(world, plantPos)
                        || !isLushGroundReplaceable(world.getBlockState(groundPos))) {
                    continue;
                }

                world.setBlockState(groundPos, ModBlocks.MOSS_BLOCK.getDefaultState(), 2);
                if (random.nextDouble() < 0.28D * strength && isReplaceableCavePlantTarget(world, plantPos)) {
                    world.setBlockState(plantPos, ModBlocks.MOSS_CARPET.getDefaultState(), 2);
                } else if (random.nextDouble() < 0.05D * strength && isReplaceableCavePlantTarget(world, plantPos)) {
                    world.setBlockState(plantPos, (random.nextBoolean() ? ModBlocks.Azalea : ModBlocks.Flowering_Azalea).getDefaultState(), 2);
                } else if (random.nextDouble() < 0.07D * strength && isReplaceableCavePlantTarget(world, plantPos)) {
                    world.setBlockState(plantPos, Blocks.TALLGRASS.getDefaultState(), 2);
                }
            }
        }
    }

    private void placeCeilingMoss(World world, Random random, BlockPos ceiling, int blockX, int blockZ, int radius, double strength) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius + random.nextInt(3)) {
                    continue;
                }

                BlockPos support = ceiling.add(dx, 0, dz);
                BlockPos below = support.down();
                if (!isInsideChunk(support, blockX, blockZ)
                        || !isReplaceableCavePlantTarget(world, below)
                        || !isLushGroundReplaceable(world.getBlockState(support))
                        || random.nextDouble() > 0.56D * strength) {
                    continue;
                }

                world.setBlockState(support, ModBlocks.MOSS_BLOCK.getDefaultState(), 2);
            }
        }
    }

    private void placeClayAndDripleafPatch(World world, Random random, BlockPos center, int blockX, int blockZ, double strength) {
        int count = 5 + (int)(8.0D * strength);
        for (int i = 0; i < count; i++) {
            BlockPos plantPos = center.add(random.nextInt(9) - 4, 0, random.nextInt(9) - 4);
            BlockPos groundPos = plantPos.down();
            if (!isInsideChunk(plantPos.up(3), blockX, blockZ)
                    || !isReplaceableCavePlantTarget(world, plantPos)
                    || !isLushGroundReplaceable(world.getBlockState(groundPos))) {
                continue;
            }

            world.setBlockState(groundPos, Blocks.CLAY.getDefaultState(), 2);
            EnumFacing facing = EnumFacing.HORIZONTALS[random.nextInt(EnumFacing.HORIZONTALS.length)];
            if (random.nextDouble() < 0.54D && canPlaceSmallDripleaf(world, plantPos)) {
                ((SmallDripleaf)ModBlocks.SMALL_DRIPLEAF).placeAt(world, plantPos, facing, 2);
            } else if (random.nextDouble() < 0.62D) {
                int height = 1 + random.nextInt(3);
                if (canPlaceBigDripleaf(world, plantPos, height)) {
                    placeBigDripleaf(world, plantPos, facing, height);
                }
            }
        }
    }

    private void placeCaveVine(World world, Random random, BlockPos start, int blockX, int blockZ) {
        if (!isInsideChunk(start, blockX, blockZ)
                || !isReplaceableCavePlantTarget(world, start)
                || !world.getBlockState(start.up()).isSideSolid(world, start.up(), EnumFacing.DOWN)) {
            return;
        }

        int length = 1 + random.nextInt(6);
        for (int i = 0; i < length; i++) {
            BlockPos pos = start.down(i);
            if (!isInsideChunk(pos, blockX, blockZ) || !isReplaceableCavePlantTarget(world, pos)) {
                break;
            }

            boolean berries = random.nextInt(5) == 0;
            if (i == length - 1) {
                world.setBlockState(pos, ModBlocks.CAVE_VINE.getDefaultState()
                        .withProperty(CaveVine.AGE, 1)
                        .withProperty(CaveVine.BERRIES, berries), 2);
            } else {
                world.setBlockState(pos, ModBlocks.CAVE_VINE_PLANT.getDefaultState()
                        .withProperty(CaveVinePlant.BERRIES, berries), 2);
            }
        }
    }

    private void decorateDripstoneCave(World world, Random random, BlockPos caveAir, int blockX, int blockZ, double strength) {
        BlockPos floor = scanToSolid(world, caveAir, EnumFacing.DOWN, LEGACY_CAVE_SURFACE_SCAN_DISTANCE);
        BlockPos ceiling = scanToSolid(world, caveAir, EnumFacing.UP, LEGACY_CAVE_SURFACE_SCAN_DISTANCE);

        if (floor != null && ceiling != null && random.nextDouble() < 0.18D * strength) {
            placeDripstoneCluster(world, random, floor, blockX, blockZ, 2 + random.nextInt(LEGACY_DRIPSTONE_PATCH_RADIUS), strength);
            return;
        }

        if (floor != null && isInsideChunk(floor, blockX, blockZ) && random.nextDouble() < 0.52D * strength) {
            placePointedDripstone(world, floor.up(), EnumFacing.UP, 1 + random.nextInt(3 + (int)(strength * 3.0D)));
        }
        if (ceiling != null && isInsideChunk(ceiling, blockX, blockZ) && random.nextDouble() < 0.68D * strength) {
            placePointedDripstone(world, ceiling.down(), EnumFacing.DOWN, 1 + random.nextInt(4 + (int)(strength * 4.0D)));
        }
        if (random.nextDouble() < 0.22D * strength) {
            placeGlowLichen(world, random, caveAir);
        }
    }

    private void placeDripstoneCluster(World world, Random random, BlockPos floor, int blockX, int blockZ, int radius, double strength) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance > radius || random.nextDouble() > (1.0D - distance / (radius + 1.0D)) * strength + 0.08D) {
                    continue;
                }

                BlockPos support = floor.add(dx, 0, dz);
                BlockPos air = support.up();
                if (!isInsideChunk(air, blockX, blockZ)
                        || !isDripstoneReplaceable(world.getBlockState(support))
                        || !isReplaceableCavePlantTarget(world, air)) {
                    continue;
                }

                world.setBlockState(support, ModBlocks.DRIPSTONE_BLOCK.getDefaultState(), 2);
                int height = Math.max(1, (int)Math.round((radius - distance + 1.0D) * (0.35D + strength * 0.35D))) + random.nextInt(2);
                placePointedDripstone(world, air, EnumFacing.UP, height);

                BlockPos ceiling = scanToSolid(world, air, EnumFacing.UP, LEGACY_CAVE_SURFACE_SCAN_DISTANCE);
                if (ceiling != null && isInsideChunk(ceiling, blockX, blockZ) && random.nextDouble() < 0.65D * strength) {
                    int ceilingHeight = Math.max(1, (int)Math.round((radius - distance + 1.0D) * (0.45D + strength * 0.45D))) + random.nextInt(3);
                    world.setBlockState(ceiling, ModBlocks.DRIPSTONE_BLOCK.getDefaultState(), 2);
                    placePointedDripstone(world, ceiling.down(), EnumFacing.DOWN, ceilingHeight);
                }
            }
        }
    }

    private void placePointedDripstone(World world, BlockPos start, EnumFacing direction, int length) {
        if (!isReplaceableCavePlantTarget(world, start)) {
            return;
        }

        BlockPos support = start.offset(direction.getOpposite());
        IBlockState supportState = world.getBlockState(support);
        if (!supportState.isSideSolid(world, support, direction)) {
            if (isDripstoneReplaceable(supportState)) {
                world.setBlockState(support, ModBlocks.DRIPSTONE_BLOCK.getDefaultState(), 2);
            } else {
                return;
            }
        }

        ((PointedDripstoneBlock)ModBlocks.POINTED_DRIPSTONE).placeColumn(world, start, direction, length, 2);
    }

    private void placeGlowLichen(World world, Random random, BlockPos pos) {
        if (!isReplaceableCavePlantTarget(world, pos)) {
            return;
        }

        int start = random.nextInt(EnumFacing.values().length);
        for (int i = 0; i < EnumFacing.values().length; i++) {
            EnumFacing face = EnumFacing.values()[(start + i) % EnumFacing.values().length];
            BlockPos support = pos.offset(face);
            if (world.getBlockState(support).isSideSolid(world, support, face.getOpposite())) {
                world.setBlockState(pos, ((GlowLichenBlock)ModBlocks.GLOW_LICHEN).getStateForFace(face), 2);
                return;
            }
        }
    }

    private boolean canPlaceSmallDripleaf(World world, BlockPos pos) {
        return isReplaceableCavePlantTarget(world, pos)
                && isReplaceableCavePlantTarget(world, pos.up())
                && ModBlocks.SMALL_DRIPLEAF.canPlaceBlockAt(world, pos);
    }

    private boolean canPlaceBigDripleaf(World world, BlockPos pos, int height) {
        if (!isReplaceableCavePlantTarget(world, pos)) {
            return false;
        }

        for (int i = 0; i <= height; i++) {
            if (!isReplaceableCavePlantTarget(world, pos.up(i))) {
                return false;
            }
        }

        return world.getBlockState(pos.down()).getBlock() == Blocks.CLAY
                || world.getBlockState(pos.down()).getBlock() == ModBlocks.MOSS_BLOCK
                || world.getBlockState(pos.down()).getBlock() == ModBlocks.ROOTED_DIRT;
    }

    private void placeBigDripleaf(World world, BlockPos pos, EnumFacing facing, int height) {
        int safeHeight = 0;
        for (int i = 0; i < height && isReplaceableCavePlantTarget(world, pos.up(i)); i++) {
            safeHeight++;
        }
        if (safeHeight <= 0 || !isReplaceableCavePlantTarget(world, pos.up(safeHeight))) {
            return;
        }

        for (int i = 0; i < safeHeight; i++) {
            world.setBlockState(pos.up(i), ModBlocks.DRIPLEAF_STEM.getDefaultState().withProperty(DripleafStem.FACING, facing), 2);
        }
        world.setBlockState(pos.up(safeHeight), ModBlocks.BIG_DRIPLEAF.getDefaultState()
                .withProperty(BigDripleaf.FACING, facing)
                .withProperty(BigDripleaf.TILT, BigDripleaf.EnumTilt.NONE), 2);
    }

    private BlockPos findCaveAir(World world, BlockPos start, int distance) {
        if (isCaveAir(world, start)) {
            return start;
        }

        for (int offset = 1; offset <= distance; offset++) {
            BlockPos up = start.up(offset);
            if (isCaveAir(world, up)) {
                return up;
            }

            BlockPos down = start.down(offset);
            if (isCaveAir(world, down)) {
                return down;
            }
        }

        return null;
    }

    private boolean isCaveAir(World world, BlockPos pos) {
        return pos.getY() > 3
                && pos.getY() < world.getActualHeight() - 3
                && world.isAirBlock(pos)
                && !world.canBlockSeeSky(pos)
                && hasNearbyNaturalStone(world, pos);
    }

    private boolean hasNearbyNaturalStone(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (isNaturalStone(world.getBlockState(pos.offset(facing)))) {
                return true;
            }
        }
        return false;
    }

    private BlockPos scanToSolid(World world, BlockPos start, EnumFacing direction, int distance) {
        BlockPos pos = start;
        for (int i = 0; i < distance; i++) {
            pos = pos.offset(direction);
            if (pos.getY() <= 1 || pos.getY() >= world.getActualHeight() - 1) {
                return null;
            }
            if (world.getBlockState(pos).getMaterial().isSolid()) {
                return pos;
            }
        }
        return null;
    }

    private void spawnAquaticCaveMobs(World world, Random random, int blockX, int blockZ) {
        if (random.nextInt(8) != 0) {
            return;
        }

        BlockPos pos = new BlockPos(blockX + random.nextInt(16), 12 + random.nextInt(38), blockZ + random.nextInt(16));
        if (EntityGlowSquid.canSpawnAt(world, pos, random)) {
            spawnMob(world, new EntityGlowSquid(world), pos);
        } else if (random.nextInt(3) == 0 && world.getBlockState(pos).getMaterial() == Material.WATER && world.getBlockState(pos.down()).getBlock() == Blocks.CLAY) {
            EntityAxolotl axolotl = new EntityAxolotl(world);
            axolotl.setRandomVariant();
            spawnMob(world, axolotl, pos);
        }
    }

    private void spawnMob(World world, EntityLiving entity, BlockPos pos) {
        if (world.countEntities(EnumCreatureType.WATER_CREATURE, false) > 40) {
            return;
        }
        entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.2D, pos.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);
        if (entity.getCanSpawnHere()) {
            world.spawnEntity(entity);
        }
    }

    private boolean isNaturalStone(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.STONE || block == ModBlocks.DeepSlate || block == ModBlocks.TUFF || block == ModBlocks.DRIPSTONE_BLOCK;
    }

    private boolean isLushGroundReplaceable(IBlockState state) {
        Block block = state.getBlock();
        return isNaturalStone(state)
                || block == Blocks.DIRT
                || block == Blocks.GRASS
                || block == Blocks.GRAVEL
                || block == Blocks.CLAY
                || block == ModBlocks.ROOTED_DIRT;
    }

    private boolean isDripstoneReplaceable(IBlockState state) {
        Block block = state.getBlock();
        return isNaturalStone(state)
                || block == Blocks.DIRT
                || block == Blocks.GRAVEL
                || block == Blocks.CLAY;
    }

    private boolean isReplaceableCavePlantTarget(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == Blocks.AIR || state.getMaterial() == Material.WATER;
    }

    private boolean isInsideChunk(BlockPos pos, int blockX, int blockZ) {
        return pos.getX() >= blockX && pos.getX() < blockX + 16
                && pos.getZ() >= blockZ && pos.getZ() < blockZ + 16
                && pos.getY() > 0 && pos.getY() < 255;
    }

    private static long getLegacyCaveSeed(World world, int chunkX, int chunkZ, long salt) {
        return world.getSeed() ^ (long)chunkX * 341873128712L ^ (long)chunkZ * 132897987541L ^ salt;
    }

    private static int randomRange(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static double inverseSqrt(double value) {
        return 1.0D / Math.sqrt(value);
    }

    private static double distanceSq(BlockPos first, BlockPos second) {
        double x = first.getX() - second.getX();
        double y = first.getY() - second.getY();
        double z = first.getZ() - second.getZ();
        return x * x + y * y + z * z;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double smoothNoise(long seed, double x, double y, double z) {
        int x0 = floor(x);
        int y0 = floor(y);
        int z0 = floor(z);
        double tx = smoothstep(x - x0);
        double ty = smoothstep(y - y0);
        double tz = smoothstep(z - z0);
        return lerp(tz,
                lerp(ty, lerp(tx, valueNoise(seed, x0, y0, z0), valueNoise(seed, x0 + 1, y0, z0)), lerp(tx, valueNoise(seed, x0, y0 + 1, z0), valueNoise(seed, x0 + 1, y0 + 1, z0))),
                lerp(ty, lerp(tx, valueNoise(seed, x0, y0, z0 + 1), valueNoise(seed, x0 + 1, y0, z0 + 1)), lerp(tx, valueNoise(seed, x0, y0 + 1, z0 + 1), valueNoise(seed, x0 + 1, y0 + 1, z0 + 1))));
    }

    private static double valueNoise(long seed, int x, int y, int z) {
        long hash = seed;
        hash ^= x * 341873128712L;
        hash ^= y * 132897987541L;
        hash ^= z * 42317861L;
        hash ^= hash >> 33;
        hash *= 0xff51afd7ed558ccdL;
        hash ^= hash >> 33;
        hash *= 0xc4ceb9fe1a85ec53L;
        hash ^= hash >> 33;
        return ((hash >>> 11) * 0x1.0p-53D) * 2.0D - 1.0D;
    }

    private static int floor(double value) {
        int integer = (int)value;
        return value < integer ? integer - 1 : integer;
    }

    private static double smoothstep(double value) {
        return value * value * (3.0D - 2.0D * value);
    }

    private static double lerp(double factor, double from, double to) {
        return from + factor * (to - from);
    }

    private enum CaveStyle {
        NORMAL,
        LUSH,
        DRIPSTONE
    }

    private static class GeodePoint {
        private final BlockPos pos;
        private final int offset;

        private GeodePoint(BlockPos pos, int offset) {
            this.pos = pos;
            this.offset = offset;
        }
    }
}
