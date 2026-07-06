package com.canoestudio.retrofuturethewildupdate.world.biome;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import com.canoestudio.retrofuturethewildupdate.block.ModBlocks;
import com.canoestudio.retrofuturethewildupdate.entity.EntityFrog;
import com.canoestudio.retrofuturethewildupdate.entity.EntityTadpole;
import com.canoestudio.retrofuturethewildupdate.world.gen.WorldGenMangroveTree;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.biome.Biome;

public class BiomeMangroveSwamp extends Biome {

    private static final WorldGenMangroveTree MANGROVE_TREE = new WorldGenMangroveTree(false);

    public BiomeMangroveSwamp() {
        super(new BiomeProperties("Mangrove Swamp")
            .setBaseHeight(-0.18F)
            .setHeightVariation(0.08F)
            .setTemperature(0.85F)
            .setRainfall(0.9F)
            .setWaterColor(0x3a7a6a));

        this.setRegistryName(RTWU.ID, "mangrove_swamp");
        this.topBlock = ModBlocks.MUD.getDefaultState();
        this.fillerBlock = ModBlocks.MUD.getDefaultState();

        this.decorator.treesPerChunk = 5;
        this.decorator.grassPerChunk = 7;
        this.decorator.waterlilyPerChunk = 5;
        this.decorator.reedsPerChunk = 7;
        this.decorator.clayPerChunk = 2;

        this.spawnableCreatureList.clear();
        this.spawnableCreatureList.add(new SpawnListEntry(EntityFrog.class, 12, 2, 5));
        this.spawnableWaterCreatureList.add(new SpawnListEntry(EntityTadpole.class, 8, 2, 5));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 1, 1, 1));
    }

    @Override
    public int getGrassColorAtPos(net.minecraft.util.math.BlockPos pos) {
        return 0x6a7f2a;
    }

    @Override
    public int getFoliageColorAtPos(net.minecraft.util.math.BlockPos pos) {
        return 0x6b8f3a;
    }

    @Override
    public net.minecraft.world.gen.feature.WorldGenAbstractTree getRandomTreeFeature(java.util.Random rand) {
        return MANGROVE_TREE;
    }
}
