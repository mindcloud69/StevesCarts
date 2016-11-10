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
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
		this.setDefaultState(this.getStateFromMeta(0));
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

	public boolean onBlockActivated(final World world, final int i, final int j, final int k, final EntityPlayer entityplayer, final int par6, final float par7, final float par8, final float par9) {
		if (entityplayer.isSneaking()) {
			return false;
		}
		if (world.isRemote) {
			return true;
		}
		FMLNetworkHandler.openGui(entityplayer, StevesCarts.instance, 6, world, i, j, k);
		return true;
	}

	public int isProvidingWeakPower(final IBlockAccess world, final BlockPos pos, final int side) {
		IBlockState blockState = world.getBlockState(pos);
		return ((getMetaFromState(blockState) & 0x8) != 0x0 && DetectorType.getTypeFromSate(blockState).shouldEmitRedstone()) ? 15 : 0;
	}

	public int isProvidingStrongPower(final IBlockAccess world, final int x, final int y, final int z, final int side) {
		return 0;
	}

	public boolean canProvidePower() {
		return true;
	}

	public boolean canConnectRedstone(final IBlockAccess world, final BlockPos pos, final int side) {
		if (side == -1) {
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
