package com.canoestudio.retrofuturetrailsandtales.proxy;

import com.canoestudio.retrofuturetrailsandtales.RTAT;
import com.canoestudio.retrofuturetrailsandtales.block.ModBlocks;
import net.minecraft.util.ResourceLocation;

public class CommonProxy {

    public void preInit() {
        ModBlocks.registerTileEntities();
    }

    public void init() {
    }

    protected static ResourceLocation prefix(String name) {
        return new ResourceLocation(RTAT.ID, name);
    }
}
