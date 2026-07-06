package com.canoestudio.retrofuturethewildupdate.world.biome;

import com.canoestudio.retrofuturemccore.api.entity.RetroEntityRegistry;
import com.canoestudio.retrofuturemccore.api.entity.RetroEntitySpawn;
import com.canoestudio.retrofuturethewildupdate.RTWU;
import com.canoestudio.retrofuturethewildupdate.entity.EntityFrog;
import com.canoestudio.retrofuturethewildupdate.entity.EntityTadpole;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RTWU.ID)
public final class ModBiomes {

    public static final Biome MANGROVE_SWAMP = new BiomeMangroveSwamp();

    private ModBiomes() {
    }

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        event.getRegistry().register(MANGROVE_SWAMP);
    }

    public static void init() {
        BiomeDictionary.addTypes(MANGROVE_SWAMP,
            BiomeDictionary.Type.SWAMP,
            BiomeDictionary.Type.WET,
            BiomeDictionary.Type.HOT,
            BiomeDictionary.Type.DENSE);
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(MANGROVE_SWAMP, 7));
        BiomeManager.addSpawnBiome(MANGROVE_SWAMP);

        RetroEntityRegistry.addSpawn(RetroEntitySpawn.of(EntityFrog.class, EnumCreatureType.CREATURE, 12, 2, 5,
            MANGROVE_SWAMP, Biomes.SWAMPLAND, Biomes.MUTATED_SWAMPLAND));
        RetroEntityRegistry.addSpawn(RetroEntitySpawn.of(EntityTadpole.class, EnumCreatureType.WATER_CREATURE, 8, 2, 5,
            MANGROVE_SWAMP, Biomes.SWAMPLAND, Biomes.MUTATED_SWAMPLAND));
    }
}
