package vswe.stevesvehicles.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vswe.stevesvehicles.tab.CreativeTabLoader;
import vswe.stevesvehicles.tileentity.TileEntityDetector;
import vswe.stevesvehicles.tileentity.detector.DetectorType;

public class BlockDetector extends BlockContainerBase {
	public BlockDetector() {
		super(Material.CIRCUITS);
		setCreativeTab(CreativeTabLoader.blocks);
	}

	/*@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return DetectorType.getTypeFromMeta(meta).getIcon(side);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		for (DetectorType type : DetectorType.values()) {
			type.registerIcons(register);
		}
	}*/

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (DetectorType type : DetectorType.values()) {
			list.add(new ItemStack(item, 1, type.getMeta()));
		}
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockState.getValue(DetectorType.ACTIVE) && DetectorType.getTypeFromSate(blockState).shouldEmitRedstone() ? 15 : 0;
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
		} else {
			DetectorType type = DetectorType.getTypeFromSate(state);
			return type.shouldEmitRedstone() || type == DetectorType.REDSTONE;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityDetector();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean isActive = false;
		int length = DetectorType.VALUES.length;
		if(meta >= length){
			meta -= length;
			isActive = false;
		}
		return this.getDefaultState().withProperty(DetectorType.SATE, DetectorType.getTypeFromInt(meta)).withProperty(DetectorType.ACTIVE, isActive);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(DetectorType.ACTIVE) ? DetectorType.VALUES.length : 0) + state.getValue(DetectorType.SATE).getMeta();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, DetectorType.SATE, DetectorType.ACTIVE);
	}
}
