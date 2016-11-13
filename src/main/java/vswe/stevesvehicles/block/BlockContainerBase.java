package vswe.stevesvehicles.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vswe.stevesvehicles.StevesVehicles;


public abstract class BlockContainerBase extends BlockContainer implements IBlockBase {
	private String unlocalizedName;
	protected BlockContainerBase(Material p_i45386_1_) {
		super(p_i45386_1_);
	}

	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public BlockContainerBase setUnlocalizedName(String name) {
		this.unlocalizedName = name;
		return this;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			player.openGui(StevesVehicles.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof IInventory) {
			IInventory inventory = (IInventory)te;
			for (int i = 0; i < inventory.getSizeInventory(); ++i) {
				ItemStack item = inventory.removeStackFromSlot(i);

				if (item != null) {
					float offsetX = world.rand.nextFloat() * 0.8F + 0.1F;
					float offsetY = world.rand.nextFloat() * 0.8F + 0.1F;
					float offsetZ = world.rand.nextFloat() * 0.8F + 0.1F;

					EntityItem entityItem = new EntityItem(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, item.copy());
					entityItem.motionX = world.rand.nextGaussian() * 0.05F;
					entityItem.motionY = world.rand.nextGaussian() * 0.05F + 0.2F;
					entityItem.motionZ = world.rand.nextGaussian() * 0.05F;

					world.spawnEntityInWorld(entityItem);
				}
			}
		}
		super.breakBlock(world, pos, state);
	}
}
