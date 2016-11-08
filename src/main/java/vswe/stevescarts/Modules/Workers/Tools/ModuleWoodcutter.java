package vswe.stevescarts.Modules.Workers.Tools;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.BlockPosHelpers;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.ISuppliesModule;
import vswe.stevescarts.Modules.ITreeModule;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Addons.Plants.ModulePlantSize;
import vswe.stevescarts.Slots.SlotBase;
import vswe.stevescarts.Slots.SlotFuel;
import vswe.stevescarts.Slots.SlotSapling;

public abstract class ModuleWoodcutter extends ModuleTool implements ISuppliesModule, ITreeModule {
	private ArrayList<ITreeModule> treeModules;
	private ModulePlantSize plantSize;
	private boolean isPlanting;
	private float cutterAngle;
	private static DataParameter<Boolean> IS_CUTTING = createDw(DataSerializers.BOOLEAN);

	public ModuleWoodcutter(final MinecartModular cart) {
		super(cart);
		this.cutterAngle = 0.7853982f;
	}

	@Override
	public byte getWorkPriority() {
		return 80;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.TOOLS.CUTTER.translate(), 8, 6, 4210752);
	}

	@Override
	protected int getInventoryWidth() {
		return super.getInventoryWidth() + 3;
	}

	@Override
	protected SlotBase getSlot(final int slotId, int x, final int y) {
		if (x == 0) {
			return super.getSlot(slotId, x, y);
		}
		--x;
		return new SlotSapling(this.getCart(), this, slotId, 8 + x * 18, 28 + y * 18);
	}

	@Override
	public boolean useDurability() {
		return true;
	}

	@Override
	public void init() {
		super.init();
		this.treeModules = new ArrayList<ITreeModule>();
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module instanceof ITreeModule) {
				this.treeModules.add((ITreeModule) module);
			} else {
				if (!(module instanceof ModulePlantSize)) {
					continue;
				}
				this.plantSize = (ModulePlantSize) module;
			}
		}
	}

	public abstract int getPercentageDropChance();

	public ArrayList<ItemStack> getTierDrop(final ArrayList<ItemStack> baseItems) {
		final ArrayList<ItemStack> nerfedItems = new ArrayList<ItemStack>();
		for (final ItemStack item : baseItems) {
			if (item != null) {
				this.dropItemByMultiplierChance(nerfedItems, item, this.getPercentageDropChance());
			}
		}
		return nerfedItems;
	}

	private void dropItemByMultiplierChance(final ArrayList<ItemStack> items, final ItemStack item, int percentage) {
		int drop = 0;
		while (percentage > 0) {
			if (this.getCart().rand.nextInt(100) < percentage) {
				items.add(item.copy());
				++drop;
			}
			percentage -= 100;
		}
	}

	@Override
	public boolean work() {
		BlockPos next = this.getNextblock();
		final int size = this.getPlantSize();
		this.destroyLeaveBlockOnTrack(next);
		this.destroyLeaveBlockOnTrack(next.up());
		for (int i = -size; i <= size; ++i) {
			if (i != 0) {
				int i2 = i;
				if (i2 < 0) {
					i2 = -size - i2 - 1;
				}
				BlockPos plant = next.add(((this.getCart().z() != next.getZ()) ? i2 : 0), -1, ((this.getCart().x() != next.getX()) ? i2 : 0));
				if (this.plant(size, plant, next.getX(), next.getZ())) {
					this.setCutting(false);
					return true;
				}
			}
		}
		if (!this.isPlanting) {
			for (int i = -1; i <= 1; ++i) {
				for (int j = -1; j <= 1; ++j) {
					BlockPos farm = next.add(i, -1, j);
					if (this.farm(farm)) {
						this.setCutting(true);
						return true;
					}
				}
			}
		}
		this.setCutting(this.isPlanting = false);
		return false;
	}

	private boolean plant(final int size, BlockPos pos, final int cx, final int cz) {
		if (size == 1) {
			if ((pos.getX() + pos.getZ()) % 2 == 0) {
				return false;
			}
		} else if ((pos.getX() == cx && pos.getX() / size % 2 == 0) || (pos.getZ() == cz && pos.getZ() / size % 2 == 0)) {
			return false;
		}
		int saplingSlotId = -1;
		ItemStack sapling = null;
		for (int i = 0; i < this.getInventorySize(); ++i) {
			final SlotBase slot = this.getSlots().get(i);
			if (slot.containsValidItem()) {
				saplingSlotId = i;
				sapling = this.getStack(i);
				break;
			}
		}
		if (sapling != null) {
			if (this.doPreWork()) {
				if (sapling.getItem().onItemUse(sapling, this.getFakePlayer(), getCart().worldObj, pos, EnumHand.MAIN_HAND, EnumFacing.UP, 0.0f, 0.0f, 0.0f) == EnumActionResult.SUCCESS) {
					if (sapling.stackSize == 0) {
						this.setStack(saplingSlotId, null);
					}
					this.startWorking(25);
					return this.isPlanting = true;
				}
			} else {
				this.stopWorking();
				this.isPlanting = false;
			}
		}
		return false;
	}

	private boolean farm(BlockPos pos) {
		if (!this.isBroken()) {
			pos = pos.up();
			IBlockState state = getCart().worldObj.getBlockState(pos);
			if (state != null && this.isWoodHandler(state, pos)) {
				final ArrayList<BlockPos> checked = new ArrayList<BlockPos>();
				if (this.removeAt(pos, checked)) {
					return true;
				}
				this.stopWorking();
			}
		}
		return false;
	}

	private boolean removeAt(BlockPos here, final ArrayList<BlockPos> checked) {
		checked.add(here);
		IBlockState blockState = getCart().worldObj.getBlockState(here);
		final Block block = blockState.getBlock();
		if (block == null) {
			return false;
		}
		if (checked.size() < 125 && BlockPosHelpers.getHorizontalDistToCartSquared(here, this.getCart()) < 175.0) {
			for (int type = 0; type < 2; ++type) {
				boolean hitWood = false;
				if (this.isLeavesHandler(blockState, here)) {
					type = 1;
				} else if (type == 1) {
					hitWood = true;
				}
				for (int x = -1; x <= 1; ++x) {
					for (int y = 1; y >= 0; --y) {
						for (int z = -1; z <= 1; ++z) {
							BlockPos pos = here.add(x, y, z);
							IBlockState currentState = this.getCart().worldObj.getBlockState(pos);
							if (currentState != null) {
								if (hitWood) {
									if (!this.isWoodHandler(currentState, pos)) {
										continue;
									}
								} else if (!this.isLeavesHandler(currentState, pos)) {
									continue;
								}
								if (!checked.contains(pos)) {
									return this.removeAt(pos, checked);
								}
							}
						}
					}
				}
			}
		}
		ArrayList<ItemStack> stuff;
		if (shouldSilkTouch(blockState, here)) {
			stuff = new ArrayList<ItemStack>();
			final ItemStack stack = this.getSilkTouchedItem(blockState);
			if (stack != null) {
				stuff.add(stack);
			}
		} else {
			final int fortune = (this.enchanter != null) ? this.enchanter.getFortuneLevel() : 0;
			stuff = (ArrayList<ItemStack>) block.getDrops(getCart().worldObj, here, blockState, fortune);
			int applerand = 200;
			if (fortune > 0) {
				applerand -= 10 << fortune;
				if (applerand < 40) {
					applerand = 40;
				}
			}
			if (/*(m & 0x3) == 0x0 &&*/ block == Blocks.LEAVES && blockState.getValue(BlockOldLeaf.VARIANT) == EnumType.OAK && this.getCart().rand.nextInt(applerand) == 0) {
				stuff.add(new ItemStack(Items.APPLE, 1, 0));
			}
		}
		final ArrayList<ItemStack> nerfedstuff = this.getTierDrop(stuff);
		boolean first = true;
		for (final ItemStack iStack : nerfedstuff) {
			this.getCart().addItemToChest(iStack, Slot.class, SlotFuel.class);
			if (iStack.stackSize != 0) {
				if (first) {
					return false;
				}
				final EntityItem entityitem = new EntityItem(this.getCart().worldObj, this.getCart().posX, this.getCart().posY, this.getCart().posZ, iStack);
				entityitem.motionX = (here.getX() - this.getCart().x()) / 10.0f;
				entityitem.motionY = 0.15000000596046448;
				entityitem.motionZ = (here.getZ() - this.getCart().z()) / 10.0f;
				this.getCart().worldObj.spawnEntityInWorld(entityitem);
			}
			first = false;
		}
		this.getCart().worldObj.setBlockToAir(here);
		int basetime;
		if (this.isLeavesHandler(blockState, here)) {
			basetime = 2;
			this.damageTool(1);
		} else {
			basetime = 25;
			this.damageTool(5);
		}
		final int efficiency = (this.enchanter != null) ? this.enchanter.getEfficiencyLevel() : 0;
		this.startWorking((int) (basetime / Math.pow(1.2999999523162842, efficiency)));
		return true;
	}

	@Override
	public void initDw() {
		registerDw(IS_CUTTING, false);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	private void setCutting(final boolean val) {
		this.updateDw(IS_CUTTING, val);
	}

	protected boolean isCutting() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getIsCutting();
		}
		return this.getDw(IS_CUTTING);
	}

	public float getCutterAngle() {
		return this.cutterAngle;
	}

	@Override
	public void update() {
		super.update();
		final boolean cuttingflag = this.isCutting();
		if (cuttingflag || this.cutterAngle != 0.7853982f) {
			boolean flag = false;
			if (!cuttingflag && this.cutterAngle < 0.7853982f) {
				flag = true;
			}
			this.cutterAngle = (float) ((this.cutterAngle + 0.9f) % 6.283185307179586);
			if (!cuttingflag && this.cutterAngle > 0.7853982f && flag) {
				this.cutterAngle = 0.7853982f;
			}
		}
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < this.getInventorySize(); ++i) {
			if (this.getSlots().get(i).containsValidItem()) {
				return true;
			}
		}
		return false;
	}

	public boolean isLeavesHandler(IBlockState blockState, BlockPos pos) {
		for (final ITreeModule module : this.treeModules) {
			if (module.isLeaves(blockState, pos)) {
				return true;
			}
		}
		return false;
	}

	public boolean isWoodHandler(IBlockState blockState, BlockPos pos) {
		for (final ITreeModule module : this.treeModules) {
			if (module.isWood(blockState, pos)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSaplingHandler(final ItemStack sapling) {
		for (final ITreeModule module : this.treeModules) {
			if (module.isSapling(sapling)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isLeaves(IBlockState blockState, BlockPos pos) {
		return blockState.getBlock() == Blocks.LEAVES || blockState.getBlock() == Blocks.LEAVES2;
	}

	@Override
	public boolean isWood(IBlockState blockState, BlockPos pos) {
		return blockState.getBlock() == Blocks.LOG || blockState.getBlock() == Blocks.LOG2;
	}

	@Override
	public boolean isSapling(final ItemStack sapling) {
		return sapling != null && Block.getBlockFromItem(sapling.getItem()) == Blocks.SAPLING;
	}

	private int getPlantSize() {
		if (this.plantSize != null) {
			return this.plantSize.getSize();
		}
		return 1;
	}

	private void destroyLeaveBlockOnTrack(BlockPos pos) {
		IBlockState state = getCart().worldObj.getBlockState(pos);
		if (state != null && this.isLeavesHandler(state, pos)) {
			getCart().worldObj.setBlockToAir(pos);
		}
	}
}
