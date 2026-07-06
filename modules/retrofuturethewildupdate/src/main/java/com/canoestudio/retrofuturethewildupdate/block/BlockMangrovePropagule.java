package com.canoestudio.retrofuturethewildupdate.block;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockMangrovePropagule extends BlockBush implements IGrowable {

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.28125D, 0.0D, 0.28125D, 0.71875D, 0.9375D, 0.71875D);

    public BlockMangrovePropagule() {
        super(Material.PLANTS);
        this.setRegistryName(RTWU.ID, "mangrove_propagule");
        this.setTranslationKey(RTWU.ID + ".mangrove_propagule");
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(0.0f);
        this.setSoundType(SoundType.PLANT);
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.FARMLAND
            || block == ModBlocks.MUD || block == ModBlocks.MANGROVE_ROOTS || block == ModBlocks.MUDDY_MANGROVE_ROOTS;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return rand.nextFloat() < 0.45f;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        if (worldIn.isRemote || !hasRoom(worldIn, pos)) {
            return;
        }

        worldIn.setBlockState(pos, ModBlocks.MANGROVE_LOG.getDefaultState(), 3);
        for (int y = 1; y <= 4; y++) {
            worldIn.setBlockState(pos.up(y), ModBlocks.MANGROVE_LOG.getDefaultState(), 3);
        }

        BlockPos crown = pos.up(5);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                int distance = Math.abs(x) + Math.abs(z);
                if (distance <= 3 && worldIn.isAirBlock(crown.add(x, 0, z))) {
                    worldIn.setBlockState(crown.add(x, 0, z), ModBlocks.MANGROVE_LEAVES.getDefaultState(), 3);
                }
                if (distance <= 2 && worldIn.isAirBlock(crown.add(x, 1, z))) {
                    worldIn.setBlockState(crown.add(x, 1, z), ModBlocks.MANGROVE_LEAVES.getDefaultState(), 3);
                }
            }
        }

        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos rootPos = pos.offset(facing);
            if (worldIn.isAirBlock(rootPos)) {
                worldIn.setBlockState(rootPos, ModBlocks.MANGROVE_ROOTS.getDefaultState(), 3);
            }
        }
    }

    private boolean hasRoom(World world, BlockPos pos) {
        for (int y = 1; y <= 6; y++) {
            BlockPos check = pos.up(y);
            if (!world.isAirBlock(check) && world.getBlockState(check).getBlock() != ModBlocks.MANGROVE_LEAVES) {
                return false;
            }
        }
        return true;
    }
}
