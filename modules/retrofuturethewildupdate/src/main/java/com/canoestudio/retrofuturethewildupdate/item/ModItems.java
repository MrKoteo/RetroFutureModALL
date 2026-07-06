package com.canoestudio.retrofuturethewildupdate.item;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

@Mod.EventBusSubscriber(modid = RTWU.ID)
public class ModItems {

    public static final ItemWardenEgg WARDEN_EGG = new ItemWardenEgg();
    public static final Item ECHO_SHARD = simpleItem("echo_shard", CreativeTabs.MATERIALS);
    public static final Item DISC_FRAGMENT_5 = simpleItem("disc_fragment_5", CreativeTabs.MISC);
    public static final Item RECOVERY_COMPASS = simpleItem("recovery_compass", CreativeTabs.TOOLS);
    public static final Item TADPOLE_BUCKET = simpleItem("tadpole_bucket", CreativeTabs.MISC).setMaxStackSize(1);
    public static final Item FROG_SPAWN_EGG = simpleItem("frog_spawn_egg", CreativeTabs.MISC).setMaxStackSize(64);
    public static final Item TADPOLE_SPAWN_EGG = simpleItem("tadpole_spawn_egg", CreativeTabs.MISC).setMaxStackSize(64);

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
            WARDEN_EGG,
            ECHO_SHARD,
            DISC_FRAGMENT_5,
            RECOVERY_COMPASS,
            TADPOLE_BUCKET,
            FROG_SPAWN_EGG,
            TADPOLE_SPAWN_EGG
        );
    }

    private static Item simpleItem(String name, CreativeTabs tab) {
        return new Item()
            .setRegistryName(RTWU.ID, name)
            .setTranslationKey(RTWU.ID + "." + name)
            .setCreativeTab(tab);
    }
}
