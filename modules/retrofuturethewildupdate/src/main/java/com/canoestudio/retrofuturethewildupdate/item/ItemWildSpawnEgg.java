package com.canoestudio.retrofuturethewildupdate.item;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWildSpawnEgg extends Item {

    private final ResourceLocation entityId;

    public ItemWildSpawnEgg(String name, ResourceLocation entityId) {
        this.entityId = entityId;
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.MISC);
        this.setRegistryName(RTWU.ID, name);
        this.setTranslationKey(RTWU.ID + "." + name);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            BlockPos spawnPos = pos.offset(facing);
            Entity entity = EntityList.createEntityByIDFromName(this.entityId, world);
            if (entity != null) {
                entity.setLocationAndAngles(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                    player.rotationYaw, 0.0F);
                if (entity instanceof EntityLiving) {
                    ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(spawnPos), null);
                }
                world.spawnEntity(entity);
                if (!player.capabilities.isCreativeMode) {
                    player.getHeldItem(hand).shrink(1);
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
