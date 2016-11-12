package vswe.stevescarts.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.blocks.tileentities.TileEntityDetector;
import vswe.stevescarts.helpers.DetectorType;

import javax.annotation.Nullable;

public class BlockDetector extends BlockContainerBase {
	public BlockDetector() {
		super(Material.CIRCUITS);
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
		this.setDefaultState(this.getStateFromMeta(0));
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

	//	@SideOnly(Side.CLIENT)
	//	public IIcon getIcon(final int side, final int meta) {
	//		return DetectorType.getTypeFromSate(meta).getIcon(side);
	//	}
	//
	//	@SideOnly(Side.CLIENT)
	//	public void registerBlockIcons(final IIconRegister register) {
	//		for (final DetectorType type : DetectorType.values()) {
	//			type.registerIcons(register);
	//		}
	//	}

	@Override
	public void getSubBlocks(final Item item, final CreativeTabs tab, final List list) {
		for (final DetectorType type : DetectorType.values()) {
			list.add(new ItemStack(item, 1, type.getMeta()));
		}
	}

	public boolean isSideSolid(final IBlockAccess world, final int x, final int y, final int z, final EnumFacing side) {
		return true;
	}

	public boolean isBlockNormalCube() {
		return true;
	}

	public boolean isBlockSolid(final IBlockAccess world, final int x, final int y, final int z, final int side) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world,
	                                BlockPos pos,
	                                IBlockState state,
	                                EntityPlayer entityPlayer,
	                                EnumHand hand,
	                                @Nullable
		                                ItemStack heldItem,
	                                EnumFacing side,
	                                float hitX,
	                                float hitY,
	                                float hitZ) {
		if (entityPlayer.isSneaking()) {
			return false;
		}
		if (world.isRemote) {
			return true;
		}
		FMLNetworkHandler.openGui(entityPlayer, StevesCarts.instance, 6, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return ((getMetaFromState(blockState) & 0x8) != 0x0 && DetectorType.getTypeFromSate(blockState).shouldEmitRedstone()) ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return 0;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		if (side == null) {
			return false;
		}
		final DetectorType type = DetectorType.getTypeFromSate(world.getBlockState(pos));
		return type.shouldEmitRedstone() || type == DetectorType.REDSTONE;
	}


	@Override
	public TileEntity createNewTileEntity(final World world, final int var2) {
		return new TileEntityDetector();
	}

	public int damageDropped(final int meta) {
		return meta;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(DetectorType.SATE, DetectorType.getTypeFromint(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(DetectorType.SATE)).getMeta();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, DetectorType.SATE);
	}
}
