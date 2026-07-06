package com.canoestudio.retrofuturethewildupdate.proxy;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import com.canoestudio.retrofuturethewildupdate.block.ModBlocks;
import com.canoestudio.retrofuturethewildupdate.world.biome.ModBiomes;
import com.canoestudio.retrofuturethewildupdate.world.gen.WildUpdateWorldGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

    public void preInit() {
        ModBlocks.registerTileEntities();
        GameRegistry.registerWorldGenerator(new WildUpdateWorldGenerator(), 30);
    }

    public void init() {
        ModBiomes.init();
    }

    public void spawnSonicBoom(World world, double x, double y, double z) {
    }

    protected static ResourceLocation prefix(String name) {
        return new ResourceLocation(RTWU.ID, name);
    }
}
