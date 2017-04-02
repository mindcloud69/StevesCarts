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
import vswe.stevescarts.PacketHandler;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.blocks.tileentities.TileEntityCartAssembler;
import vswe.stevescarts.blocks.tileentities.TileEntityUpgrade;

import java.util.ArrayList;

public class BlockCartAssembler extends BlockContainerBase {

	public BlockCartAssembler() {
		super(Material.ROCK);
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityplayer, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (entityplayer.isSneaking()) {
			return false;
		}
		final TileEntityCartAssembler assembler = (TileEntityCartAssembler) world.getTileEntity(pos);
		if (assembler != null) {
			if (!world.isRemote) {
				entityplayer.openGui(StevesCarts.instance, 3, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
		return false;
	}

	public void updateMultiBlock(final World world, final BlockPos pos) {
		final TileEntityCartAssembler master = (TileEntityCartAssembler) world.getTileEntity(pos);
		if (master != null) {
			master.clearUpgrades();
		}
		this.checkForUpgrades(world, pos);
		if (!world.isRemote) {
			PacketHandler.sendBlockInfoToClients(world, new byte[0], pos);
		}
		if (master != null) {
			master.onUpgradeUpdate();
		}
	}

	private void checkForUpgrades(final World world, final BlockPos pos) {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			this.checkForUpgrade(world, pos.offset(facing), facing);
		}
	}

	private TileEntityCartAssembler checkForUpgrade(final World world, final BlockPos pos, EnumFacing facing) {
		final TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityUpgrade) {
			final TileEntityUpgrade upgrade = (TileEntityUpgrade) tile;
			final ArrayList<TileEntityCartAssembler> masters = this.getMasters(world, pos);
			if (masters.size() == 1) {
				final TileEntityCartAssembler master = masters.get(0);
				master.addUpgrade(upgrade);
				upgrade.setMaster(master, facing);
				return master;
			}
			for (final TileEntityCartAssembler master2 : masters) {
				master2.removeUpgrade(upgrade);
			}
			upgrade.setMaster(null, null);
		}
		return null;
	}

	private ArrayList<TileEntityCartAssembler> getMasters(final World world, final BlockPos pos) {
		final ArrayList<TileEntityCartAssembler> masters = new ArrayList<>();
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				for (int k = -1; k <= 1; ++k) {
					if (Math.abs(i) + Math.abs(j) + Math.abs(k) == 1) {
						final TileEntityCartAssembler temp = this.getMaster(world, pos.add(i, j, k));
						if (temp != null) {
							masters.add(temp);
						}
					}
				}
			}
		}
		return masters;
	}

	private TileEntityCartAssembler getValidMaster(final World world, final BlockPos pos) {
		TileEntityCartAssembler master = null;
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				for (int k = -1; k <= 1; ++k) {
					if (Math.abs(i) + Math.abs(j) + Math.abs(k) == 1) {
						final TileEntityCartAssembler temp = this.getMaster(world, pos.add(i, j, k));
						if (temp != null) {
							if (master != null) {
								return null;
							}
							master = temp;
						}
					}
				}
			}
		}
		return master;
	}

	private TileEntityCartAssembler getMaster(final World world, final BlockPos pos) {
		final TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityCartAssembler) {
			final TileEntityCartAssembler master = (TileEntityCartAssembler) tile;
			if (!master.isDead) {
				return master;
			}
		}
		return null;
	}

	public void addUpgrade(final World world, final BlockPos pos) {
		final TileEntityCartAssembler master = this.getValidMaster(world, pos);
		if (master != null) {
			this.updateMultiBlock(world, master.getPos());
		}
	}

	public void removeUpgrade(final World world, final BlockPos pos) {
		final TileEntityCartAssembler master = this.getValidMaster(world, pos);
		if (master != null) {
			this.updateMultiBlock(world, pos);
		}
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int var2) {
		return new TileEntityCartAssembler();
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		this.updateMultiBlock(worldIn, pos);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		final TileEntityCartAssembler var7 = (TileEntityCartAssembler) world.getTileEntity(pos);
		var7.isDead = true;
		this.updateMultiBlock(world, pos);
		if (var7 != null) {
			for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
				@Nonnull
				ItemStack var9 = var7.removeStackFromSlot(var8);
				if (var9 != null) {
					final float var10 = world.rand.nextFloat() * 0.8f + 0.1f;
					final float var11 = world.rand.nextFloat() * 0.8f + 0.1f;
					final float var12 = world.rand.nextFloat() * 0.8f + 0.1f;
					while (var9.stackSize > 0) {
						int var13 = world.rand.nextInt(21) + 10;
						if (var13 > var9.stackSize) {
							var13 = var9.stackSize;
						}
						@Nonnull
						ItemStack itemStack = var9;
						itemStack.stackSize -= var13;
						final EntityItem var14 = new EntityItem(world, pos.getX() + var10, pos.getY() + var11, pos.getZ() + var12, new ItemStack(var9.getItem(), var13, var9.getItemDamage()));
						final float var15 = 0.05f;
						var14.motionX = (float) world.rand.nextGaussian() * var15;
						var14.motionY = (float) world.rand.nextGaussian() * var15 + 0.2f;
						var14.motionZ = (float) world.rand.nextGaussian() * var15;
						if (var9.hasTagCompound()) {
							var14.getEntityItem().setTagCompound(var9.getTagCompound().copy());
						}
						world.spawnEntity(var14);
					}
				}
			}
			@Nonnull
			ItemStack outputItem = var7.getOutputOnInterupt();
			if (outputItem != null) {
				final EntityItem eItem = new EntityItem(world, pos.getX() + 0.20000000298023224, pos.getY() + 0.20000000298023224, pos.getZ() + 0.2f, outputItem);
				eItem.motionX = (float) world.rand.nextGaussian() * 0.05f;
				eItem.motionY = (float) world.rand.nextGaussian() * 0.25f;
				eItem.motionZ = (float) world.rand.nextGaussian() * 0.05f;
				if (outputItem.hasTagCompound()) {
					eItem.getEntityItem().setTagCompound(outputItem.getTagCompound().copy());
				}
				world.spawnEntity(eItem);
			}
		}
		super.breakBlock(world, pos, this.getDefaultState());
	}

}
