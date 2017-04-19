package vswe.stevescarts.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.blocks.tileentities.TileEntityLiquid;

import javax.annotation.Nonnull;

public class BlockLiquidManager extends BlockContainerBase {

	public BlockLiquidManager() {
		super(Material.ROCK);
		setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

	@Override
	public void breakBlock(World par1World, BlockPos pos, IBlockState state) {
		final TileEntityLiquid var7 = (TileEntityLiquid) par1World.getTileEntity(pos);
		if (var7 != null) {
			for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
				@Nonnull
				ItemStack var9 = var7.getStackInSlot(var8);
				if (var9 != null) {
					final float var10 = par1World.rand.nextFloat() * 0.8f + 0.1f;
					final float var11 = par1World.rand.nextFloat() * 0.8f + 0.1f;
					final float var12 = par1World.rand.nextFloat() * 0.8f + 0.1f;
					while (var9.getCount() > 0) {
						int var13 = par1World.rand.nextInt(21) + 10;
						if (var13 > var9.getCount()) {
							var13 = var9.getCount();
						}
						@Nonnull
						ItemStack itemStack = var9;
						itemStack.shrink(var13);
						final EntityItem var14 = new EntityItem(par1World, pos.getX() + var10, pos.getY() + var11, pos.getZ() + var12, new ItemStack(var9.getItem(), var13, var9.getItemDamage()));
						final float var15 = 0.05f;
						var14.motionX = (float) par1World.rand.nextGaussian() * var15;
						var14.motionY = (float) par1World.rand.nextGaussian() * var15 + 0.2f;
						var14.motionZ = (float) par1World.rand.nextGaussian() * var15;
						if (var9.hasTagCompound()) {
							var14.getEntityItem().setTagCompound(var9.getTagCompound().copy());
						}
						par1World.spawnEntity(var14);
					}
				}
			}
		}
		super.breakBlock(par1World, pos, state);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			return false;
		}
		if (world.isRemote) {
			return true;
		}
		player.openGui(StevesCarts.instance, 2, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int var2) {
		return new TileEntityLiquid();
	}
}
