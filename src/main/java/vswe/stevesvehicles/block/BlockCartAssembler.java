package vswe.stevesvehicles.block;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vswe.stevesvehicles.network.PacketHandler;
import vswe.stevesvehicles.tab.CreativeTabLoader;
import vswe.stevesvehicles.tileentity.TileEntityCartAssembler;
import vswe.stevesvehicles.tileentity.TileEntityUpgrade;

public class BlockCartAssembler extends BlockContainerBase {
	public BlockCartAssembler() {
		super(Material.ROCK);
		setCreativeTab(CreativeTabLoader.blocks);
	}

	/*
	 * private IIcon topIcon; private IIcon botIcon; private IIcon sideIcons [];
	 * @SideOnly(Side.CLIENT)
	 * @Override public IIcon getIcon(int side, int meta) { if (side == 0) {
	 * return botIcon; }else if(side == 1) { return topIcon; }else { return
	 * sideIcons[side - 2]; } }
	 * @SideOnly(Side.CLIENT)
	 * @Override public void registerBlockIcons(IIconRegister register) {
	 * topIcon = register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":assembler/top"); botIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":assembler/bot"); sideIcons = new IIcon[4]; for (int i = 1; i <= 4; i++)
	 * { sideIcons[i-1] =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":assembler/side_" + i); } }
	 */
	public void updateMultiBlock(World world, BlockPos pos) {
		TileEntityCartAssembler master = (TileEntityCartAssembler) world.getTileEntity(pos);
		if (master != null) {
			master.clearUpgrades();
		}
		checkForUpgrades(world, pos);
		if (!world.isRemote) {
			PacketHandler.sendBlockInfoToClients(world, new byte[] {}, pos);
		}
		if (master != null) {
			master.onUpgradeUpdate();
		}
	}

	private void checkForUpgrades(World world, BlockPos pos) {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			this.checkForUpgrade(world, pos.offset(facing), facing);
		}
	}

	private TileEntityCartAssembler checkForUpgrade(World world, BlockPos pos, EnumFacing facing) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityUpgrade) {
			TileEntityUpgrade upgrade = (TileEntityUpgrade) tile;
			ArrayList<TileEntityCartAssembler> masters = getMasters(world, pos);
			if (masters.size() == 1) {
				TileEntityCartAssembler master = masters.get(0);
				master.addUpgrade(upgrade);
				upgrade.setMaster(master, facing);
				return master;
			} else {
				for (TileEntityCartAssembler master : masters) {
					master.removeUpgrade(upgrade);
					master.onUpgradeUpdate();
				}
				upgrade.setMaster(null, null);
			}
		}
		return null;
	}

	private ArrayList<TileEntityCartAssembler> getMasters(World world, BlockPos pos) {
		ArrayList<TileEntityCartAssembler> masters = new ArrayList<>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (Math.abs(i) + Math.abs(j) + Math.abs(k) == 1) {
						TileEntityCartAssembler temp = getMaster(world, pos.add(i, j, k));
						if (temp != null) {
							masters.add(temp);
						}
					}
				}
			}
		}
		return masters;
	}

	private TileEntityCartAssembler getValidMaster(World world, BlockPos pos) {
		TileEntityCartAssembler master = null;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (Math.abs(i) + Math.abs(j) + Math.abs(k) == 1) {
						TileEntityCartAssembler temp = getMaster(world, pos.add(i, j, k));
						if (temp != null) {
							if (master != null) {
								return null;
							} else {
								master = temp;
							}
						}
					}
				}
			}
		}
		return master;
	}

	private TileEntityCartAssembler getMaster(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityCartAssembler) {
			TileEntityCartAssembler master = (TileEntityCartAssembler) tile;
			if (!master.isDead) {
				return master;
			}
		}
		return null;
	}

	public void addUpgrade(World world, BlockPos pos) {
		TileEntityCartAssembler master = getValidMaster(world, pos);
		if (master != null) {
			updateMultiBlock(world, master.getPos());
		}
	}

	public void removeUpgrade(World world, BlockPos pos) {
		TileEntityCartAssembler master = getValidMaster(world, pos);
		if (master != null) {
			updateMultiBlock(world, master.getPos());
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int var2) {
		return new TileEntityCartAssembler();
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		updateMultiBlock(world, pos);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityCartAssembler) {
			TileEntityCartAssembler assembler = (TileEntityCartAssembler) te;
			assembler.isDead = true;
			updateMultiBlock(world, pos);
			ItemStack outputItem = assembler.getOutputOnInterrupt();
			if (outputItem != null) {
				EntityItem entityItem = new EntityItem(world, (double) pos.getX() + 0.2F, (double) pos.getY() + 0.2F, pos.getZ() + 0.2F, outputItem.copy());
				entityItem.motionX = world.rand.nextGaussian() * 0.05F;
				entityItem.motionY = world.rand.nextGaussian() * 0.25F;
				entityItem.motionZ = world.rand.nextGaussian() * 0.05F;
				world.spawnEntityInWorld(entityItem);
			}
		}
		super.breakBlock(world, pos, state);
	}
}
