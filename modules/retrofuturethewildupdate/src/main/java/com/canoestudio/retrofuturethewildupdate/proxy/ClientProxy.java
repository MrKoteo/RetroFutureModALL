package com.canoestudio.retrofuturethewildupdate.proxy;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import com.canoestudio.retrofuturethewildupdate.block.ModBlocks;
import com.canoestudio.retrofuturethewildupdate.client.particle.ParticleSonicBoom;
import com.canoestudio.retrofuturethewildupdate.entity.Warden;
import com.canoestudio.retrofuturethewildupdate.client.renderer.RenderWarden;
import com.canoestudio.retrofuturethewildupdate.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = {Side.CLIENT}, modid = RTWU.ID)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
        RenderingRegistry.registerEntityRenderingHandler(Warden.class, manager -> new RenderWarden(manager));
    }

    @Override
    public void spawnSonicBoom(World world, double x, double y, double z) {
        ParticleSonicBoom particle = new ParticleSonicBoom(world, x, y, z);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        registerItemModel(ModItems.WARDEN_EGG);
        registerItemModel(ModItems.ECHO_SHARD);
        registerItemModel(ModItems.DISC_FRAGMENT_5);
        registerItemModel(ModItems.RECOVERY_COMPASS);
        registerItemModel(ModItems.TADPOLE_BUCKET);
        registerItemModel(ModItems.FROG_SPAWN_EGG);
        registerItemModel(ModItems.TADPOLE_SPAWN_EGG);
        registerBlockModel(ModBlocks.SCULK);
        registerBlockModel(ModBlocks.SCULK_VEIN);
        registerBlockModel(ModBlocks.SCULK_SENSOR);
        registerBlockModel(ModBlocks.SCULK_SHRIEKER);
        registerBlockModel(ModBlocks.SCULK_CATALYST);
        registerBlockModel(ModBlocks.MUD);
        registerBlockModel(ModBlocks.PACKED_MUD);
        registerBlockModel(ModBlocks.MUD_BRICKS);
        registerBlockModel(ModBlocks.MANGROVE_LOG);
        registerBlockModel(ModBlocks.STRIPPED_MANGROVE_LOG);
        registerBlockModel(ModBlocks.MANGROVE_PLANKS);
        registerBlockModel(ModBlocks.MANGROVE_LEAVES);
        registerBlockModel(ModBlocks.MANGROVE_ROOTS);
        registerBlockModel(ModBlocks.MUDDY_MANGROVE_ROOTS);
        registerBlockModel(ModBlocks.MANGROVE_PROPAGULE);
        registerBlockModel(ModBlocks.FROGSPAWN);
        registerBlockModel(ModBlocks.OCHRE_FROGLIGHT);
        registerBlockModel(ModBlocks.VERDANT_FROGLIGHT);
        registerBlockModel(ModBlocks.PEARLESCENT_FROGLIGHT);
        registerBlockModel(ModBlocks.REINFORCED_DEEPSLATE);
    }

    private static void registerItemModel(Item item) {
        ResourceLocation name = item.getRegistryName();
        if (name != null) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(name, "inventory"));
        }
    }

    private static void registerBlockModel(Block block) {
        ResourceLocation name = block.getRegistryName();
        if (name != null) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(name, "inventory"));
        }
    }
}
