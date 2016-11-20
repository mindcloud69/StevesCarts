package vswe.stevesvehicles.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import vswe.stevesvehicles.module.data.ModuleDataItemHandler;
import vswe.stevesvehicles.tab.CreativeTabLoader;
import vswe.stevesvehicles.tileentity.TileEntityActivator;
import vswe.stevesvehicles.tileentity.TileEntityDetector;
import vswe.stevesvehicles.tileentity.TileEntityManager;
import vswe.stevesvehicles.tileentity.TileEntityUpgrade;
import vswe.stevesvehicles.tileentity.detector.DetectorType;
import vswe.stevesvehicles.upgrade.effect.BaseEffect;
import vswe.stevesvehicles.upgrade.effect.assembly.Disassemble;
import vswe.stevesvehicles.upgrade.effect.external.Transposer;
import vswe.stevesvehicles.vehicle.entity.EntityModularCart;

public class BlockRailAdvancedDetector extends BlockRailDetector implements IBlockBase {
	// private IIcon normalIcon;
	// private IIcon cornerIcon;
	private String unlocalizedName;

	public BlockRailAdvancedDetector() {
		setCreativeTab(CreativeTabLoader.blocks);
		setSoundType(SoundType.METAL);
	}

	/*
	 * @Override public IIcon getIcon(int side, int meta) { return meta >= 6 ?
	 * cornerIcon : normalIcon; }
	 * @Override
	 * @SideOnly(Side.CLIENT) public void registerBlockIcons(IIconRegister
	 * register) { normalIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":rails/detector"); cornerIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":rails/detector_corner"); }
	 */
	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public BlockRailAdvancedDetector setUnlocalizedName(String name) {
		this.unlocalizedName = name;
		return this;
	}

	@Override
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
		if (world.isRemote || !(cart instanceof EntityModularCart)) {
			return;
		}
		EntityModularCart entityCart = (EntityModularCart) cart;
		if (world.getBlockState(pos.down()) == ModBlocks.DETECTOR_UNIT.getBlock() && DetectorType.getTypeFromSate(world.getBlockState(pos.down())).canInteractWithCart()) {
			TileEntity tileentity = world.getTileEntity(pos.down());
			if (tileentity != null && tileentity instanceof TileEntityDetector) {
				TileEntityDetector detector = (TileEntityDetector) tileentity;
				detector.handleCart(entityCart.getVehicle());
			}
			return;
		}
		if (!isCartReadyForAction(entityCart, pos)) {
			return;
		}
		int side = 0;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (Math.abs(i) != Math.abs(j)) {
					Block block = world.getBlockState(pos.add(i, 0, j)).getBlock();
					if (block == ModBlocks.CARGO_MANAGER.getBlock() || block == ModBlocks.LIQUID_MANAGER.getBlock()) {
						TileEntity tileentity = world.getTileEntity(pos.add(i, 0, j));
						if (tileentity != null && tileentity instanceof TileEntityManager) {
							TileEntityManager manager = (TileEntityManager) tileentity;
							if (manager.getCart() == null) {
								manager.setCart(entityCart);
								manager.setSide(side);
							}
						}
						return;
					} else if (block == ModBlocks.MODULE_TOGGLER.getBlock()) {
						TileEntity tileentity = world.getTileEntity(pos.add(i, 0, j));
						if (tileentity != null && tileentity instanceof TileEntityActivator) {
							TileEntityActivator activator = (TileEntityActivator) tileentity;
							boolean isOrange = false;
							if ((entityCart.temppushX == 0) == (entityCart.temppushZ == 0)) {
								continue;
							}
							if (i == 0) {
								if (j == -1) {
									isOrange = entityCart.temppushX < 0;
								} else {
									isOrange = entityCart.temppushX > 0;
								}
							} else if (j == 0) {
								if (i == -1) {
									isOrange = entityCart.temppushZ > 0;
								} else {
									isOrange = entityCart.temppushZ < 0;
								}
							}
							activator.handleCart(entityCart, isOrange);
							entityCart.releaseCart();
						}
						return;
					} else if (block == ModBlocks.UPGRADE.getBlock()) {
						TileEntity tileentity = world.getTileEntity(pos.add(i, 0, j));
						TileEntityUpgrade upgrade = (TileEntityUpgrade) tileentity;
						if (upgrade != null && upgrade.getEffects() != null) {
							for (BaseEffect effect : upgrade.getEffects()) {
								if (effect instanceof Transposer) {
									if (upgrade.getMaster() != null) {
										for (TileEntityUpgrade tile : upgrade.getMaster().getUpgradeTiles()) {
											if (tile.getEffects() != null) {
												for (BaseEffect effect2 : tile.getEffects()) {
													if (effect2 instanceof Disassemble) {
														if (tile.getStackInSlot(0) == null) {
															tile.setInventorySlotContents(0, ModuleDataItemHandler.createModularVehicle(entityCart.getVehicle()));
															upgrade.getMaster().managerInteract(entityCart, false);
															for (int p = 0; p < entityCart.getSizeInventory(); p++) {
																ItemStack item = entityCart.removeStackFromSlot(p);
																if (item != null) {
																	upgrade.getMaster().puke(item);
																}
															}
															entityCart.setDead();
															return;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
					side++;
				}
			}
		}
		boolean receivesPower = world.isBlockIndirectlyGettingPowered(pos) > 0;
		if (receivesPower) {
			entityCart.releaseCart();
		}
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		// check if any block is using this detector for something else
		IBlockState stateDown = world.getBlockState(pos.down());
		if (stateDown == ModBlocks.DETECTOR_UNIT.getBlock() && DetectorType.getTypeFromSate(stateDown).canInteractWithCart()) {
			return false;
		}
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (Math.abs(i) != Math.abs(j)) {
					Block block = world.getBlockState(pos.add(i, 0, j)).getBlock();
					if (block == ModBlocks.CARGO_MANAGER.getBlock() || block == ModBlocks.LIQUID_MANAGER.getBlock() || block == ModBlocks.MODULE_TOGGLER.getBlock()) {
						return false;
					} else if (block == ModBlocks.UPGRADE.getBlock()) {
						TileEntity tileentity = world.getTileEntity(pos.add(i, 0, j));
						TileEntityUpgrade upgrade = (TileEntityUpgrade) tileentity;
						if (upgrade != null && upgrade.getEffects() != null) {
							for (BaseEffect effect : upgrade.getEffects()) {
								if (effect instanceof Transposer) {
									if (upgrade.getMaster() != null) {
										for (TileEntityUpgrade tile : upgrade.getMaster().getUpgradeTiles()) {
											if (tile.getEffects() != null) {
												for (BaseEffect effect2 : tile.getEffects()) {
													if (effect2 instanceof Disassemble) {
														return false;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		// if nothing else used this activator it can be controlled by redstone
		return true;
	}

	private boolean isCartReadyForAction(EntityModularCart cart, BlockPos pos) {
		return (cart.disabledPos != null && cart.disabledPos.equals(pos)) && cart.getVehicle().isDisabled();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		return world.getBlockState(pos.down()).getBlock() == ModBlocks.DETECTOR_UNIT.getBlock() && ModBlocks.DETECTOR_UNIT.getBlock().onBlockActivated(world, pos.down(), world.getBlockState(pos.down()), playerIn, hand, EnumFacing.UP, hitX, hitY, hitZ);
	}

	public void refreshState(World world, BlockPos pos, boolean flag) {
		new Rail(world, pos, world.getBlockState(pos)).place(flag, false);
	}
}
