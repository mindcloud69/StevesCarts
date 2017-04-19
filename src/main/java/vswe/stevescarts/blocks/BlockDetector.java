package vswe.stevescarts.blocks;

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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.blocks.tileentities.TileEntityDetector;
import vswe.stevescarts.helpers.DetectorType;

public class BlockDetector extends BlockContainerBase {
	public BlockDetector() {
		super(Material.CIRCUITS);
		setCreativeTab(StevesCarts.tabsSC2Blocks);
		setDefaultState(getStateFromMeta(0));
	}

	@Override
	public void getSubBlocks(final Item item, final CreativeTabs tab, final NonNullList<ItemStack> list) {
		for (final DetectorType type : DetectorType.values()) {
			list.add(new ItemStack(item, 1, type.getMeta()));
		}
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world,
	                                BlockPos pos,
	                                IBlockState state,
	                                EntityPlayer entityPlayer,
	                                EnumHand hand,
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
		return blockState.getValue(DetectorType.POWERED) && DetectorType.getTypeFromSate(blockState).shouldEmitRedstone() ? 15 : 0;
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
		boolean powered = false;
		if (meta > DetectorType.values().length) {
			powered = true;
		}
		return getDefaultState().withProperty(DetectorType.SATE, DetectorType.getTypeFromint(meta - (powered ? DetectorType.values().length + 1 : 0))).withProperty(DetectorType.POWERED, powered);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		boolean powered = state.getValue(DetectorType.POWERED);
		return (state.getValue(DetectorType.SATE)).getMeta() + (powered ? DetectorType.values().length + 1 : 0);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, DetectorType.SATE, DetectorType.POWERED);
	}
}
