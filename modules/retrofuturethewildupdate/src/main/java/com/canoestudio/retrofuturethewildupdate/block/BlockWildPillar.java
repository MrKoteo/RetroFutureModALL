package com.canoestudio.retrofuturethewildupdate.block;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWildPillar extends BlockRotatedPillar {

    public BlockWildPillar(String name, Material material, SoundType soundType, float hardness, float resistance, CreativeTabs tab) {
        super(material);
        this.setRegistryName(RTWU.ID, name);
        this.setTranslationKey(RTWU.ID + "." + name);
        this.setCreativeTab(tab);
        this.setHardness(hardness);
        this.setResistance(resistance);
        this.setSoundType(soundType);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(AXIS, facing.getAxis());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing.Axis axis = EnumFacing.Axis.Y;
        int value = meta & 3;
        if (value == 1) {
            axis = EnumFacing.Axis.X;
        } else if (value == 2) {
            axis = EnumFacing.Axis.Z;
        }
        return this.getDefaultState().withProperty(AXIS, axis);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        EnumFacing.Axis axis = state.getValue(AXIS);
        if (axis == EnumFacing.Axis.X) {
            return 1;
        }
        return axis == EnumFacing.Axis.Z ? 2 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }
}
