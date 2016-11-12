package vswe.stevescarts.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.blocks.tileentities.TileEntityActivator;

public class BlockActivator extends BlockContainerBase {

	public BlockActivator() {
		super(Material.ROCK);
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

	@Override
	public boolean onBlockActivated(World worldIn,
			BlockPos pos,
			IBlockState state,
			EntityPlayer playerIn,
			EnumHand hand,
			@Nullable
			ItemStack heldItem,
			EnumFacing side,
			float hitX,
			float hitY,
			float hitZ) {
		if (playerIn.isSneaking()) {
			return false;
		}
		if (worldIn.isRemote) {
			return true;
		}
		FMLNetworkHandler.openGui(playerIn, StevesCarts.instance, 4, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int var2) {
		return new TileEntityActivator();
	}
}
