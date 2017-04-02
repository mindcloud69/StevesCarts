package vswe.stevescarts.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
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
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.blocks.tileentities.TileEntityActivator;
import vswe.stevescarts.blocks.tileentities.TileEntityDetector;
import vswe.stevescarts.blocks.tileentities.TileEntityManager;
import vswe.stevescarts.blocks.tileentities.TileEntityUpgrade;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.helpers.DetectorType;
import vswe.stevescarts.modules.data.ModuleData;
import vswe.stevescarts.upgrades.BaseEffect;
import vswe.stevescarts.upgrades.Disassemble;
import vswe.stevescarts.upgrades.Transposer;

import javax.annotation.Nonnull;

public class BlockRailAdvDetector extends BlockRailDetector {

	public BlockRailAdvDetector() {
		super();
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return 0;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return 0;
	}

	@Override
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public void onMinecartPass(World world, EntityMinecart entityMinecart, BlockPos pos) {
		if (world.isRemote || !(entityMinecart instanceof EntityMinecartModular)) {
			return;
		}
		final EntityMinecartModular cart = (EntityMinecartModular) entityMinecart;
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
															@Nonnull
															ItemStack item = cart.removeStackFromSlot(p);
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
		int power = world.getStrongPower(pos);
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

	private boolean isCartReadyForAction(final EntityMinecartModular cart, BlockPos pos) {
		return cart.disabledPos != null && cart.disabledPos.equals(pos) && cart.isDisabled();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		pos = pos.down();
		IBlockState blockState = world.getBlockState(pos);
		return blockState.getBlock() == ModBlocks.DETECTOR_UNIT.getBlock() && ModBlocks.DETECTOR_UNIT.getBlock().onBlockActivated(world, pos, blockState, player, hand, side, hitX, hitY, hitZ);
	}

	public void refreshState(World world, BlockPos pos, IBlockState state, final boolean flag) {
		new BlockRailBase.Rail(world, pos, state).place(flag, false);
	}

}
