package com.canoestudio.retrofuturethewildupdate.block;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = RTWU.ID)
public final class ModBlocks {

    public static final Block SCULK = new BlockSculk();
    public static final Block SCULK_VEIN = new BlockSculkVein();
    public static final BlockSculkSensor SCULK_SENSOR = new BlockSculkSensor();
    public static final BlockSculkShrieker SCULK_SHRIEKER = new BlockSculkShrieker();
    public static final BlockSculkCatalyst SCULK_CATALYST = new BlockSculkCatalyst();
    public static final Block MUD = new BlockWildSimple("mud", Material.GROUND, SoundType.GROUND, 0.5f, 0.5f, CreativeTabs.BUILDING_BLOCKS);
    public static final Block PACKED_MUD = new BlockWildSimple("packed_mud", Material.ROCK, SoundType.STONE, 1.0f, 3.0f, CreativeTabs.BUILDING_BLOCKS);
    public static final Block MUD_BRICKS = new BlockWildSimple("mud_bricks", Material.ROCK, SoundType.STONE, 1.5f, 3.0f, CreativeTabs.BUILDING_BLOCKS);
    public static final Block MANGROVE_LOG = new BlockWildPillar("mangrove_log", Material.WOOD, SoundType.WOOD, 2.0f, 2.0f, CreativeTabs.BUILDING_BLOCKS);
    public static final Block STRIPPED_MANGROVE_LOG = new BlockWildPillar("stripped_mangrove_log", Material.WOOD, SoundType.WOOD, 2.0f, 2.0f, CreativeTabs.BUILDING_BLOCKS);
    public static final Block MANGROVE_PLANKS = new BlockWildSimple("mangrove_planks", Material.WOOD, SoundType.WOOD, 2.0f, 3.0f, CreativeTabs.BUILDING_BLOCKS);
    public static final Block MANGROVE_LEAVES = new BlockMangroveLeaves();
    public static final Block MANGROVE_ROOTS = new BlockMangroveRoots();
    public static final Block MUDDY_MANGROVE_ROOTS = new BlockWildPillar("muddy_mangrove_roots", Material.WOOD, SoundType.WOOD, 0.7f, 0.7f, CreativeTabs.DECORATIONS);
    public static final Block MANGROVE_PROPAGULE = new BlockMangrovePropagule();
    public static final Block FROGSPAWN = new BlockFrogspawn();
    public static final Block OCHRE_FROGLIGHT = withLight(new BlockWildPillar("ochre_froglight", Material.GLASS, SoundType.GLASS, 0.3f, 0.3f, CreativeTabs.DECORATIONS));
    public static final Block VERDANT_FROGLIGHT = withLight(new BlockWildPillar("verdant_froglight", Material.GLASS, SoundType.GLASS, 0.3f, 0.3f, CreativeTabs.DECORATIONS));
    public static final Block PEARLESCENT_FROGLIGHT = withLight(new BlockWildPillar("pearlescent_froglight", Material.GLASS, SoundType.GLASS, 0.3f, 0.3f, CreativeTabs.DECORATIONS));
    public static final Block REINFORCED_DEEPSLATE = new BlockWildSimple("reinforced_deepslate", Material.ROCK, SoundType.STONE, 55.0f, 1200.0f, CreativeTabs.BUILDING_BLOCKS);

    private static final Block[] BLOCKS = {
        SCULK,
        SCULK_VEIN,
        SCULK_SENSOR,
        SCULK_SHRIEKER,
        SCULK_CATALYST,
        MUD,
        PACKED_MUD,
        MUD_BRICKS,
        MANGROVE_LOG,
        STRIPPED_MANGROVE_LOG,
        MANGROVE_PLANKS,
        MANGROVE_LEAVES,
        MANGROVE_ROOTS,
        MUDDY_MANGROVE_ROOTS,
        MANGROVE_PROPAGULE,
        FROGSPAWN,
        OCHRE_FROGLIGHT,
        VERDANT_FROGLIGHT,
        PEARLESCENT_FROGLIGHT,
        REINFORCED_DEEPSLATE
    };

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(BLOCKS);
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        for (Block block : BLOCKS) {
            registry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }
    }

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntitySculkSensor.class, key("sculk_sensor"));
        GameRegistry.registerTileEntity(TileEntitySculkShrieker.class, key("sculk_shrieker"));
        GameRegistry.registerTileEntity(TileEntitySculkCatalyst.class, key("sculk_catalyst"));
    }

    private static ResourceLocation key(String name) {
        return new ResourceLocation(RTWU.ID, name);
    }

    private static Block withLight(Block block) {
        return block.setLightLevel(1.0f);
    }

    private ModBlocks() {
    }
}
