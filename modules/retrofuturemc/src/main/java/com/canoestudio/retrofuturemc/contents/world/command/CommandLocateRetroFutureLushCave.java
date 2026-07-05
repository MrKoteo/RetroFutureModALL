package com.canoestudio.retrofuturemc.contents.world.command;

import com.canoestudio.retrofuturemc.contents.world.gen.RetroFutureWorldGenerator;
import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainAPI;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainCaveSample;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainConfig;
import com.lonelyxiya.minecraft.moderncaveterrain.api.ModernCaveTerrainUndergroundBiomeSample;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CommandLocateRetroFutureLushCave extends CommandBase {
    private static final int SEARCH_RADIUS_CHUNKS = 192;
    private static final int SAMPLE_STEP = 4;
    private static final int CENTER_SCAN_RADIUS = 24;
    private static final String LUSH_CAVE = "LushCave";
    private static final String DRIPSTONE_CAVE = "DripstoneCave";

    @Override
    public String getName() {
        return "locateretrofuturelushcave";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "locateretrofuturelushcave <location>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException(getUsage(sender));
        }

        String location = args[0];
        CaveLocation target = getTargetLocation(location);
        if (target == null) {
            throw new CommandException("commands.locate.failure", location);
        }

        World world = sender.getEntityWorld();
        if (world.provider.getDimension() != 0) {
            throw new CommandException("commands.locate.failure", target.displayName);
        }

        BlockPos origin = sender.getPosition();
        BlockPos cave = findNearestCave(world, origin, target.style, SEARCH_RADIUS_CHUNKS);
        if (cave != null) {
            sender.sendMessage(new TextComponentTranslation("commands.locate.success", target.displayName, cave.getX(), cave.getZ()));
        } else {
            throw new CommandException("commands.locate.failure", target.displayName);
        }
    }

    @Nullable
    private CaveLocation getTargetLocation(String location) {
        if (LUSH_CAVE.equals(location) || "LushCaves".equals(location) || "lush".equalsIgnoreCase(location)
                || "lush_cave".equalsIgnoreCase(location) || "lush_caves".equalsIgnoreCase(location)
                || "minecraft:lush_caves".equalsIgnoreCase(location)) {
            return CaveLocation.LUSH;
        }
        if (DRIPSTONE_CAVE.equals(location) || "DripstoneCaves".equals(location) || "dripstone".equalsIgnoreCase(location)
                || "dripstone_cave".equalsIgnoreCase(location) || "dripstone_caves".equalsIgnoreCase(location)
                || "minecraft:dripstone_caves".equalsIgnoreCase(location)) {
            return CaveLocation.DRIPSTONE;
        }
        return null;
    }

    @Nullable
    private BlockPos findNearestCave(World world, BlockPos origin, RetroFutureWorldGenerator.CaveStyle target, int radiusChunks) {
        ModernCaveTerrainConfig config = ModernCaveTerrainAPI.getConfigForDimension(world.provider.getDimension());
        if (!RetroFutureWorldGenerator.isModernCaveTerrainActive(world)) {
            return findNearestWorldDecoratedCave(world, origin, target, radiusChunks);
        }

        int minY = RetroFutureWorldGenerator.getModernCaveMinY(world, config);
        int maxY = RetroFutureWorldGenerator.getModernCaveMaxY(world, config);
        if (maxY <= minY) {
            maxY = Math.min(96, world.getActualHeight() - 1);
            minY = 4;
        }

        int originChunkX = origin.getX() >> 4;
        int originChunkZ = origin.getZ() >> 4;
        LocatedCave nearest = null;

        for (int radius = 0; radius <= radiusChunks; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }

                    LocatedCave cave = sampleModernGeneratedChunk(world, config, originChunkX + dx, originChunkZ + dz, minY, maxY, origin, target);
                    if (cave == null || (nearest != null && cave.distanceSq >= nearest.distanceSq)) {
                        continue;
                    }

                    nearest = cave;
                }
            }

            if (nearest != null && radius > 2 && nearest.distanceSq < ((radius - 1) * 16) * ((radius - 1) * 16)) {
                return nearest.pos;
            }
        }

        if (nearest != null) {
            return nearest.pos;
        }

        return findNearestSampledModernCave(world, config, origin, target, radiusChunks, minY, maxY);
    }

    @Nullable
    private LocatedCave sampleModernGeneratedChunk(World world, ModernCaveTerrainConfig config, int chunkX, int chunkZ, int minY, int maxY,
                                                   BlockPos origin, RetroFutureWorldGenerator.CaveStyle target) {
        LocatedCave best = null;
        Random random = RetroFutureWorldGenerator.createModernCavePositionRandom(world, chunkX, chunkZ);

        for (int i = 0; i < RetroFutureWorldGenerator.MODERN_CAVE_TERRAIN_DECORATION_ATTEMPTS; i++) {
            int x = (chunkX << 4) + random.nextInt(16);
            int z = (chunkZ << 4) + random.nextInt(16);
            int y = minY + random.nextInt(Math.max(1, maxY - minY + 1));
            BlockPos cave = findModernCaveNear(world, config, x, y, z, RetroFutureWorldGenerator.PRIMER_CAVE_AIR_SCAN_DISTANCE, target);
            if (cave == null) {
                continue;
            }

            double distance = origin.distanceSq(cave);
            if (best == null || distance < best.distanceSq) {
                best = new LocatedCave(cave, distance);
            }
        }

        return best;
    }

    @Nullable
    private BlockPos findModernCaveNear(World world, ModernCaveTerrainConfig config, int x, int startY, int z, int distance,
                                        RetroFutureWorldGenerator.CaveStyle target) {
        BlockPos pos = getModernCaveAt(world, config, x, startY, z, target);
        if (pos != null) {
            return pos;
        }

        for (int offset = 1; offset <= distance; offset++) {
            pos = getModernCaveAt(world, config, x, startY + offset, z, target);
            if (pos != null) {
                return pos;
            }

            pos = getModernCaveAt(world, config, x, startY - offset, z, target);
            if (pos != null) {
                return pos;
            }
        }

        return null;
    }

    @Nullable
    private BlockPos getModernCaveAt(World world, ModernCaveTerrainConfig config, int x, int y, int z,
                                     RetroFutureWorldGenerator.CaveStyle target) {
        if (y < 1 || y >= world.getActualHeight()) {
            return null;
        }

        ModernCaveTerrainCaveSample caveSample = ModernCaveTerrainAPI.sampleCave(world, config, x, y, z);
        if (!caveSample.isOpen() || caveSample.getFluidState() != null) {
            return null;
        }

        ModernCaveTerrainUndergroundBiomeSample biomeSample = ModernCaveTerrainAPI.sampleUndergroundBiome(
                world, config, x, y, z, caveSample.getFluidState());
        if (RetroFutureWorldGenerator.classifyModernCaveStyle(biomeSample) != target) {
            return null;
        }

        double strength = RetroFutureWorldGenerator.getModernCaveStyleStrength(biomeSample, target);
        if (strength <= 0.0D) {
            return null;
        }

        return new BlockPos(x, y, z);
    }

    @Nullable
    private BlockPos findNearestSampledModernCave(World world, ModernCaveTerrainConfig config, BlockPos origin,
                                                  RetroFutureWorldGenerator.CaveStyle target, int radiusChunks,
                                                  int minY, int maxY) {
        LocatedCave best = null;
        int originChunkX = origin.getX() >> 4;
        int originChunkZ = origin.getZ() >> 4;

        for (int radius = 0; radius <= radiusChunks; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }

                    LocatedCave cave = sampleModernOpenChunk(world, config, originChunkX + dx, originChunkZ + dz, minY, maxY, origin, target);
                    if (cave == null || (best != null && cave.distanceSq >= best.distanceSq)) {
                        continue;
                    }

                    best = cave;
                }
            }

            if (best != null && radius > 2 && best.distanceSq < ((radius - 1) * 16) * ((radius - 1) * 16)) {
                return best.pos;
            }
        }

        return best != null ? best.pos : null;
    }

    @Nullable
    private LocatedCave sampleModernOpenChunk(World world, ModernCaveTerrainConfig config, int chunkX, int chunkZ, int minY, int maxY,
                                              BlockPos origin, RetroFutureWorldGenerator.CaveStyle target) {
        LocatedCave best = null;
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int localX = 2; localX < 16; localX += SAMPLE_STEP) {
            for (int localZ = 2; localZ < 16; localZ += SAMPLE_STEP) {
                for (int y = minY; y <= maxY; y += SAMPLE_STEP) {
                    BlockPos pos = getModernCaveAt(world, config, baseX + localX, y, baseZ + localZ, target);
                    if (pos == null) {
                        continue;
                    }

                    double distance = origin.distanceSq(pos);
                    if (best == null || distance < best.distanceSq) {
                        best = new LocatedCave(pos, distance);
                    }
                }
            }
        }

        return best;
    }

    @Nullable
    private BlockPos findNearestWorldDecoratedCave(World world, BlockPos origin, RetroFutureWorldGenerator.CaveStyle target, int radiusChunks) {
        int originChunkX = origin.getX() >> 4;
        int originChunkZ = origin.getZ() >> 4;
        LocatedCave nearest = null;

        for (int radius = 0; radius <= radiusChunks; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }

                    LocatedCave cave = sampleGeneratedBlocks(world, originChunkX + dx, originChunkZ + dz, origin, target);
                    if (cave == null || (nearest != null && cave.distanceSq >= nearest.distanceSq)) {
                        continue;
                    }

                    nearest = cave;
                }
            }

            if (nearest != null && radius > 2 && nearest.distanceSq < ((radius - 1) * 16) * ((radius - 1) * 16)) {
                return nearest.pos;
            }
        }

        return nearest != null ? nearest.pos : null;
    }

    @Nullable
    private LocatedCave sampleGeneratedBlocks(World world, int chunkX, int chunkZ, BlockPos origin, RetroFutureWorldGenerator.CaveStyle target) {
        LocatedCave best = sampleWorldDecorationCandidates(world, chunkX, chunkZ, origin, target);
        if (!world.isBlockLoaded(new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8), false)) {
            return best;
        }

        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int localX = 0; localX < 16; localX += SAMPLE_STEP) {
            for (int localZ = 0; localZ < 16; localZ += SAMPLE_STEP) {
                for (int y = 5; y <= Math.min(86, world.getActualHeight() - 1); y += SAMPLE_STEP) {
                    BlockPos pos = new BlockPos(baseX + localX, y, baseZ + localZ);
                    if (RetroFutureWorldGenerator.classifyWorldCaveStyle(world, pos) != target) {
                        continue;
                    }
                    if (!hasGeneratedMarkerNear(world, pos, target)) {
                        continue;
                    }

                    double distance = origin.distanceSq(pos);
                    if (best == null || distance < best.distanceSq) {
                        best = new LocatedCave(pos, distance);
                    }
                }
            }
        }

        return best;
    }

    @Nullable
    private LocatedCave sampleWorldDecorationCandidates(World world, int chunkX, int chunkZ, BlockPos origin,
                                                       RetroFutureWorldGenerator.CaveStyle target) {
        if (!world.isBlockLoaded(new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8), false)) {
            return null;
        }

        LocatedCave best = null;
        Random random = RetroFutureWorldGenerator.createWorldCavePositionRandom(world, chunkX, chunkZ);
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;

        for (int i = 0; i < RetroFutureWorldGenerator.WORLD_CAVE_DECORATION_ATTEMPTS; i++) {
            int x = blockX + random.nextInt(16);
            int y = 8 + random.nextInt(62);
            int z = blockZ + random.nextInt(16);
            BlockPos pos = findLoadedCaveAirInColumn(world, x, y, z, RetroFutureWorldGenerator.WORLD_CAVE_AIR_SCAN_DISTANCE);
            if (pos == null || RetroFutureWorldGenerator.classifyWorldCaveStyle(world, pos) != target) {
                continue;
            }

            double distance = origin.distanceSq(pos);
            if (best == null || distance < best.distanceSq) {
                best = new LocatedCave(pos, distance);
            }
        }

        return best;
    }

    @Nullable
    private BlockPos findLoadedCaveAirInColumn(World world, int x, int startY, int z, int distance) {
        BlockPos pos = getLoadedDecoratableCaveAir(world, x, startY, z);
        if (pos != null) {
            return pos;
        }

        for (int offset = 1; offset <= distance; offset++) {
            pos = getLoadedDecoratableCaveAir(world, x, startY + offset, z);
            if (pos != null) {
                return pos;
            }

            pos = getLoadedDecoratableCaveAir(world, x, startY - offset, z);
            if (pos != null) {
                return pos;
            }
        }

        return null;
    }

    @Nullable
    private BlockPos getLoadedDecoratableCaveAir(World world, int x, int y, int z) {
        if (y < 5 || y > 86) {
            return null;
        }

        BlockPos pos = new BlockPos(x, y, z);
        if (!world.isBlockLoaded(pos, false)) {
            return null;
        }
        return world.isAirBlock(pos) && !world.canSeeSky(pos) ? pos : null;
    }

    private boolean hasGeneratedMarkerNear(World world, BlockPos center, RetroFutureWorldGenerator.CaveStyle target) {
        if (!world.isAreaLoaded(center, CENTER_SCAN_RADIUS + 1, false)) {
            return false;
        }

        for (int dx = -CENTER_SCAN_RADIUS; dx <= CENTER_SCAN_RADIUS; dx += 2) {
            for (int dy = -8; dy <= 8; dy += 2) {
                for (int dz = -CENTER_SCAN_RADIUS; dz <= CENTER_SCAN_RADIUS; dz += 2) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (target == RetroFutureWorldGenerator.CaveStyle.LUSH && isLushMarker(world, pos)) {
                        return true;
                    }
                    if (target == RetroFutureWorldGenerator.CaveStyle.DRIPSTONE && isDripstoneMarker(world, pos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isLushMarker(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == ModBlocks.MOSS_BLOCK
                || world.getBlockState(pos).getBlock() == ModBlocks.MOSS_CARPET
                || world.getBlockState(pos).getBlock() == ModBlocks.SPORE_BLOSSOM
                || world.getBlockState(pos).getBlock() == ModBlocks.CAVE_VINE
                || world.getBlockState(pos).getBlock() == ModBlocks.CAVE_VINE_PLANT
                || world.getBlockState(pos).getBlock() == ModBlocks.HANGING_ROOTS
                || world.getBlockState(pos).getBlock() == ModBlocks.Azalea
                || world.getBlockState(pos).getBlock() == ModBlocks.Flowering_Azalea;
    }

    private boolean isDripstoneMarker(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == ModBlocks.DRIPSTONE_BLOCK
                || world.getBlockState(pos).getBlock() == ModBlocks.POINTED_DRIPSTONE;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, LUSH_CAVE, DRIPSTONE_CAVE);
        }
        return Collections.emptyList();
    }

    private enum CaveLocation {
        LUSH(LUSH_CAVE, RetroFutureWorldGenerator.CaveStyle.LUSH),
        DRIPSTONE(DRIPSTONE_CAVE, RetroFutureWorldGenerator.CaveStyle.DRIPSTONE);

        private final String displayName;
        private final RetroFutureWorldGenerator.CaveStyle style;

        CaveLocation(String displayName, RetroFutureWorldGenerator.CaveStyle style) {
            this.displayName = displayName;
            this.style = style;
        }
    }

    private static class LocatedCave {
        private final BlockPos pos;
        private final double distanceSq;

        private LocatedCave(BlockPos pos, double distanceSq) {
            this.pos = pos;
            this.distanceSq = distanceSq;
        }
    }
}
