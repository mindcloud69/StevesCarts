package vswe.stevescarts.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.DetectorType;
import vswe.stevescarts.ModuleData.ModuleData;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.TileEntities.TileEntityActivator;
import vswe.stevescarts.TileEntities.TileEntityDetector;
import vswe.stevescarts.TileEntities.TileEntityManager;
import vswe.stevescarts.TileEntities.TileEntityUpgrade;
import vswe.stevescarts.Upgrades.BaseEffect;
import vswe.stevescarts.Upgrades.Disassemble;
import vswe.stevescarts.Upgrades.Transposer;

public class BlockRailAdvDetector extends BlockRailDetector {
//	private IIcon normalIcon;
//	private IIcon cornerIcon;
	
	public BlockRailAdvDetector() {
		super();
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

//	public IIcon getIcon(final int side, final int meta) {
//		return (meta >= 6) ? this.cornerIcon : this.normalIcon;
//	}
//
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(final IIconRegister register) {
//		final StringBuilder sb = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.normalIcon = register.registerIcon(sb.append("stevescarts").append(":").append("advanced_detector_rail").toString());
//		final StringBuilder sb2 = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.cornerIcon = register.registerIcon(sb2.append("stevescarts").append(":").append("advanced_detector_rail").append("_corner").toString());
//	}
	
	@Override
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart entityMinecart, BlockPos pos) {
		if (world.isRemote || !(entityMinecart instanceof MinecartModular)) {
			return;
		}
		final MinecartModular cart = (MinecartModular) entityMinecart;
		if (world.getBlockState(pos.down()).getBlock() == ModBlocks.DETECTOR_UNIT.getBlock() && DetectorType.getTypeFromSate(world.getBlockState(pos.down())).canInteractWithCart()) {
			final TileEntity tileentity = world.getTileEntity(pos.down());
			if (tileentity != null && tileentity instanceof TileEntityDetector) {
				final TileEntityDetector detector = (TileEntityDetector) tileentity;
				detector.handleCart(cart);
			}
			return;
		}
		if (!this.isCartReadyForAction(cart, pos)) {
			return;
		}
		int side = 0;
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				if (Math.abs(i) != Math.abs(j)) {
					Block block = world.getBlockState(pos.add(i, 0, j)).getBlock();
					if (block == ModBlocks.CARGO_MANAGER.getBlock() || block == ModBlocks.LIQUID_MANAGER.getBlock()) {
						final TileEntity tileentity2 = world.getTileEntity(pos.add(i, 0, j));
						if (tileentity2 != null && tileentity2 instanceof TileEntityManager) {
							final TileEntityManager manager = (TileEntityManager) tileentity2;
							if (manager.getCart() == null) {
								manager.setCart(cart);
								manager.setSide(side);
							}
						}
						return;
					}
					if (block == ModBlocks.MODULE_TOGGLER.getBlock()) {
						final TileEntity tileentity2 = world.getTileEntity(pos.add(i, 0, j));
						if (tileentity2 != null && tileentity2 instanceof TileEntityActivator) {
							final TileEntityActivator activator = (TileEntityActivator) tileentity2;
							boolean isOrange = false;
							if (cart.temppushX == 0.0 == (cart.temppushZ == 0.0)) {
								continue;
							}
							if (i == 0) {
								if (j == -1) {
									isOrange = (cart.temppushX < 0.0);
								} else {
									isOrange = (cart.temppushX > 0.0);
								}
							} else if (j == 0) {
								if (i == -1) {
									isOrange = (cart.temppushZ > 0.0);
								} else {
									isOrange = (cart.temppushZ < 0.0);
								}
							}
							final boolean isBlueBerry = false;
							activator.handleCart(cart, isOrange);
							cart.releaseCart();
						}
						return;
					}
					if (block == ModBlocks.UPGRADE.getBlock()) {
						final TileEntity tileentity2 = world.getTileEntity(pos.add(i, 0, j));
						final TileEntityUpgrade upgrade = (TileEntityUpgrade) tileentity2;
						if (upgrade != null && upgrade.getUpgrade() != null) {
							for (final BaseEffect effect : upgrade.getUpgrade().getEffects()) {
								if (effect instanceof Transposer) {
									final Transposer transposer = (Transposer) effect;
									if (upgrade.getMaster() == null) {
										continue;
									}
									for (final TileEntityUpgrade tile : upgrade.getMaster().getUpgradeTiles()) {
										if (tile.getUpgrade() != null) {
											for (final BaseEffect effect2 : tile.getUpgrade().getEffects()) {
												if (effect2 instanceof Disassemble) {
													final Disassemble disassembler = (Disassemble) effect2;
													if (tile.getStackInSlot(0) == null) {
														tile.setInventorySlotContents(0, ModuleData.createModularCart(cart));
														upgrade.getMaster().managerInteract(cart, false);
														for (int p = 0; p < cart.getSizeInventory(); ++p) {
															final ItemStack item = cart.removeStackFromSlot(p);
															if (item != null) {
																upgrade.getMaster().puke(item);
															}
														}
														cart.setDead();
														return;
													}
													continue;
												}
											}
										}
									}
								}
							}
						}
					}
					++side;
				}
			}
		}
		int power = world.isBlockIndirectlyGettingPowered(pos);
		if (power > 0) {
			cart.releaseCart();
		}
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		IBlockState blockState = world.getBlockState(pos.down());
		if (world.getBlockState(pos.down()) == ModBlocks.DETECTOR_UNIT.getBlock() && DetectorType.getTypeFromSate(blockState).canInteractWithCart()) {
			return false;
		}
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				if (Math.abs(i) != Math.abs(j)) {
					BlockPos posOther = pos.add(i, 0, j);
					final Block block = world.getBlockState(posOther).getBlock();
					if (block == ModBlocks.CARGO_MANAGER.getBlock() || block == ModBlocks.LIQUID_MANAGER.getBlock() || block == ModBlocks.MODULE_TOGGLER.getBlock()) {
						return false;
					}
					if (block == ModBlocks.UPGRADE.getBlock()) {
						final TileEntity tileentity = world.getTileEntity(posOther);
						final TileEntityUpgrade upgrade = (TileEntityUpgrade) tileentity;
						if (upgrade != null && upgrade.getUpgrade() != null) {
							for (final BaseEffect effect : upgrade.getUpgrade().getEffects()) {
								if (effect instanceof Transposer && upgrade.getMaster() != null) {
									for (final TileEntityUpgrade tile : upgrade.getMaster().getUpgradeTiles()) {
										if (tile.getUpgrade() != null) {
											for (final BaseEffect effect2 : tile.getUpgrade().getEffects()) {
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
		return true;
	}

	private boolean isCartReadyForAction(final MinecartModular cart, BlockPos pos) {
		return cart.disabledPos.equals(pos) && cart.isDisabled();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		pos = pos.down();
		IBlockState blockState = world.getBlockState(pos);
		return blockState.getBlock() == ModBlocks.DETECTOR_UNIT.getBlock() && ModBlocks.DETECTOR_UNIT.getBlock().onBlockActivated(world, pos, blockState, player, hand, heldItem, side, hitX, hitY, hitZ);
	}

	public void refreshState(World world, BlockPos pos, IBlockState state, final boolean flag) {
		new BlockRailBase.Rail(world, pos, state).place(flag, false);
	}
	
}
