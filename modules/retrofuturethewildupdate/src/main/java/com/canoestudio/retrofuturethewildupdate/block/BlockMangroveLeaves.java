package com.canoestudio.retrofuturethewildupdate.block;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockMangroveLeaves extends BlockLeaves {

    public BlockMangroveLeaves() {
        this.setRegistryName(RTWU.ID, "mangrove_leaves");
        this.setTranslationKey(RTWU.ID + ".mangrove_leaves");
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setSoundType(SoundType.PLANT);
        this.setHardness(0.2f);
        this.setLightOpacity(1);
        this.setDefaultState(this.blockState.getBaseState()
            .withProperty(CHECK_DECAY, true)
            .withProperty(DECAYABLE, true));
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 60;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 30;
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isTranslucent(IBlockState state) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHECK_DECAY, DECAYABLE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
            .withProperty(DECAYABLE, (meta & 2) != 0)
            .withProperty(CHECK_DECAY, (meta & 1) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (state.getValue(CHECK_DECAY)) {
            meta |= 1;
        }
        if (state.getValue(DECAYABLE)) {
            meta |= 2;
        }
        return meta;
    }

    @Override
    public BlockPlanks.EnumType getWoodType(int meta) {
        return BlockPlanks.EnumType.JUNGLE;
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return NonNullList.withSize(1, new ItemStack(this));
    }
}
