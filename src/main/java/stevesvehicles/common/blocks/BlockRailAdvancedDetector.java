package stevesvehicles.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
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
import stevesvehicles.common.blocks.tileentitys.TileEntityActivator;
import stevesvehicles.common.blocks.tileentitys.TileEntityCartAssembler;
import stevesvehicles.common.blocks.tileentitys.TileEntityDetector;
import stevesvehicles.common.blocks.tileentitys.TileEntityManager;
import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.blocks.tileentitys.detector.DetectorType;
import stevesvehicles.common.core.tabs.CreativeTabLoader;
import stevesvehicles.common.modules.datas.ModuleDataItemHandler;
import stevesvehicles.common.upgrades.effects.BaseEffect;
import stevesvehicles.common.upgrades.effects.assembly.Disassemble;
import stevesvehicles.common.upgrades.effects.external.Transposer;
import stevesvehicles.common.vehicles.entitys.EntityModularCart;

public class BlockRailAdvancedDetector extends BlockRailSpecialBase implements IBlockBase {
	private String unlocalizedName;

	public BlockRailAdvancedDetector() {
		super(false);
		setCreativeTab(CreativeTabLoader.blocks);
		setSoundType(SoundType.METAL);
	}

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
					} else if (block == ModBlocks.CART_ASSEMBLER.getBlock()) {
						TileEntity tileentity = world.getTileEntity(pos.add(i, 0, j));
						TileEntityCartAssembler assembler = (TileEntityCartAssembler) tileentity;
						UpgradeContainer container = assembler.getUpgrade(EnumFacing.VALUES[side]);
						if (container != null && container.getEffects() != null) {
							for (BaseEffect effect : container.getEffects()) {
								if (effect instanceof Transposer) {
									TileEntityCartAssembler master = container.getMaster();
									for (UpgradeContainer container2 : master.getUpgrades()) {
										if (container2.getEffects() != null) {
											for (BaseEffect effect2 : container2.getEffects()) {
												if (effect2 instanceof Disassemble) {
													if (container2.getStackInSlot(0) == null) {
														container2.setInventorySlotContents(0, ModuleDataItemHandler.createModularVehicle(entityCart.getVehicle()));
														master.managerInteract(entityCart, false);
														for (int p = 0; p < entityCart.getSizeInventory(); p++) {
															ItemStack item = entityCart.removeStackFromSlot(p);
															if (item != null) {
																master.puke(item);
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
					} else if (block == ModBlocks.CART_ASSEMBLER.getBlock()) {
						TileEntity tileentity = world.getTileEntity(pos.add(i, 0, j));
						TileEntityCartAssembler assembler = (TileEntityCartAssembler) tileentity;
						UpgradeContainer container = assembler.getUpgrade(side);
						if (container != null && container.getEffects() != null) {
							for (BaseEffect effect : container.getEffects()) {
								if (effect instanceof Transposer) {
									TileEntityCartAssembler master = container.getMaster();
									for (UpgradeContainer container2 : master.getUpgrades()) {
										if (container2.getEffects() != null) {
											for (BaseEffect effect2 : container2.getEffects()) {
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
