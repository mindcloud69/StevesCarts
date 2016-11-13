package vswe.stevescarts.modules.workers.tools;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotSeed;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.modules.ICropModule;
import vswe.stevescarts.modules.ISuppliesModule;
import vswe.stevescarts.modules.ModuleBase;

public abstract class ModuleFarmer extends ModuleTool implements ISuppliesModule, ICropModule {
	private ArrayList<ICropModule> plantModules;
	private int farming;
	private float farmAngle;
	private float rigAngle;
	private DataParameter<Boolean> IS_FARMING;

	public ModuleFarmer(final EntityMinecartModular cart) {
		super(cart);
		this.rigAngle = -3.926991f;
	}

	protected abstract int getRange();

	public int getExternalRange() {
		return this.getRange();
	}

	@Override
	public void init() {
		super.init();
		this.plantModules = new ArrayList<>();
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module instanceof ICropModule) {
				this.plantModules.add((ICropModule) module);
			}
		}
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
		this.drawString(gui, Localization.MODULES.TOOLS.FARMER.translate(), 8, 6, 4210752);
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
		return new SlotSeed(this.getCart(), this, slotId, 8 + x * 18, 28 + y * 18);
	}

	@Override
	public boolean work() {
		World world = getCart().worldObj;
		BlockPos next = this.getNextblock();
		for (int i = -this.getRange(); i <= this.getRange(); ++i) {
			for (int j = -this.getRange(); j <= this.getRange(); ++j) {
				BlockPos coord = next.add(i, -1, j);
				if (this.farm(world, coord)) {
					return true;
				}
				if (this.till(world, coord)) {
					return true;
				}
				if (this.plant(world, coord)) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean till(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (world.isAirBlock(pos.up()) && (block == Blocks.GRASS || block == Blocks.DIRT)) {
			if (this.doPreWork()) {
				this.startWorking(10);
				return true;
			}
			this.stopWorking();
			world.setBlockState(pos, Blocks.FARMLAND.getDefaultState());
		}
		return false;
	}

	protected boolean plant(World world, BlockPos pos) {
		int hasSeeds = -1;
		IBlockState soilState = world.getBlockState(pos);
		Block soilblock = soilState.getBlock();
		if (soilblock != null) {
			for (int i = 0; i < this.getInventorySize(); ++i) {
				if (this.getStack(i) != null && this.isSeedValidHandler(this.getStack(i))) {
					Block cropblock = this.getCropFromSeedHandler(this.getStack(i));
					if (cropblock != null && cropblock instanceof IPlantable && world.isAirBlock(pos.up()) && soilblock.canSustainPlant(soilState, world, pos, EnumFacing.UP, (IPlantable) cropblock)) {
						hasSeeds = i;
						break;
					}
				}
			}
			if (hasSeeds != -1) {
				if (this.doPreWork()) {
					this.startWorking(25);
					return true;
				}
				this.stopWorking();
				Block cropblock2 = this.getCropFromSeedHandler(this.getStack(hasSeeds));
				world.setBlockState(pos.up(), cropblock2.getDefaultState());
				ItemStack stack = this.getStack(hasSeeds);
				--stack.stackSize;
				if (this.getStack(hasSeeds).stackSize <= 0) {
					this.setStack(hasSeeds, null);
				}
			}
		}
		return false;
	}

	protected boolean farm(World world, BlockPos pos) {
		EntityMinecartModular cart = getCart();
		if (!this.isBroken()) {
			pos = pos.up();
			IBlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (this.isReadyToHarvestHandler(world, pos)) {
				if (this.doPreWork()) {
					final int efficiency = (this.enchanter != null) ? this.enchanter.getEfficiencyLevel() : 0;
					final int workingtime = (int) (this.getBaseFarmingTime() / Math.pow(1.2999999523162842, efficiency));
					this.setFarming(workingtime * 4);
					this.startWorking(workingtime);
					return true;
				}
				this.stopWorking();
				List<ItemStack> stuff;
				if (this.shouldSilkTouch(blockState, pos)) {
					stuff = new ArrayList<>();
					final ItemStack stack = this.getSilkTouchedItem(blockState);
					if (stack != null) {
						stuff.add(stack);
					}
				} else {
					final int fortune = (this.enchanter != null) ? this.enchanter.getFortuneLevel() : 0;
					stuff = block.getDrops(world, pos, blockState, fortune);
				}
				for (final ItemStack iStack : stuff) {
					cart.addItemToChest(iStack);
					if (iStack.stackSize != 0) {
						final EntityItem entityitem = new EntityItem(world, cart.posX, cart.posY, cart.posZ, iStack);
						entityitem.motionX = (pos.getX() - cart.x()) / 10.0f;
						entityitem.motionY = 0.15000000596046448;
						entityitem.motionZ = (pos.getZ() - cart.z()) / 10.0f;
						world.spawnEntityInWorld(entityitem);
					}
				}
				world.setBlockToAir(pos);
				this.damageTool(3);
			}
		}
		return false;
	}

	protected int getBaseFarmingTime() {
		return 25;
	}

	public boolean isSeedValidHandler(final ItemStack seed) {
		for (final ICropModule module : this.plantModules) {
			if (module.isSeedValid(seed)) {
				return true;
			}
		}
		return false;
	}

	protected Block getCropFromSeedHandler(final ItemStack seed) {
		for (final ICropModule module : this.plantModules) {
			if (module.isSeedValid(seed)) {
				return module.getCropFromSeed(seed);
			}
		}
		return null;
	}

	protected boolean isReadyToHarvestHandler(World world, BlockPos pos) {
		for (final ICropModule module : this.plantModules) {
			if (module.isReadyToHarvest(world, pos)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSeedValid(final ItemStack seed) {
		return seed.getItem() == Items.WHEAT_SEEDS || seed.getItem() == Items.POTATO || seed.getItem() == Items.CARROT;
	}

	@Override
	public Block getCropFromSeed(final ItemStack seed) {
		if (seed.getItem() == Items.CARROT) {
			return Blocks.CARROTS;
		}
		if (seed.getItem() == Items.POTATO) {
			return Blocks.POTATOES;
		}
		if (seed.getItem() == Items.WHEAT_SEEDS) {
			return Blocks.WHEAT;
		}
		return null;
	}

	@Override
	public boolean isReadyToHarvest(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		return blockState.getBlock() instanceof BlockCrops && blockState.getValue(BlockCrops.AGE) == 7;
	}

	public float getFarmAngle() {
		return this.farmAngle;
	}

	public float getRigAngle() {
		return this.rigAngle;
	}

	@Override
	public void initDw() {
		IS_FARMING = createDw(DataSerializers.BOOLEAN);
		registerDw(IS_FARMING, false);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	private void setFarming(final int val) {
		this.farming = val;
		this.updateDw(IS_FARMING, val > 0);
	}

	protected boolean isFarming() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getIsFarming();
		}
		return this.getCart().isEngineBurning() && this.getDw(IS_FARMING);
	}

	@Override
	public void update() {
		super.update();
		if (!this.getCart().worldObj.isRemote) {
			this.setFarming(this.farming - 1);
		} else {
			final float up = -3.926991f;
			final float down = -3.1415927f;
			final boolean flag = this.isFarming();
			if (flag) {
				if (this.rigAngle < down) {
					this.rigAngle += 0.1f;
					if (this.rigAngle > down) {
						this.rigAngle = down;
					}
				} else {
					this.farmAngle = (float) ((this.farmAngle + 0.15f) % 6.283185307179586);
				}
			} else if (this.rigAngle > up) {
				this.rigAngle -= 0.075f;
				if (this.rigAngle < up) {
					this.rigAngle = up;
				}
			}
		}
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < this.getInventorySize(); ++i) {
			final ItemStack item = this.getStack(i);
			if (item != null && this.isSeedValidHandler(item)) {
				return true;
			}
		}
		return false;
	}
}
