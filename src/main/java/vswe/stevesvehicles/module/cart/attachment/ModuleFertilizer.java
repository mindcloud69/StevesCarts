package vswe.stevesvehicles.module.cart.attachment;

import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.container.slots.SlotBase;
import vswe.stevesvehicles.container.slots.SlotFertilizer;
import vswe.stevesvehicles.localization.entry.module.cart.LocalizationCartCultivationUtil;
import vswe.stevesvehicles.module.ISuppliesModule;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.cart.ModuleWorker;
import vswe.stevesvehicles.module.cart.tool.ModuleFarmer;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleFertilizer extends ModuleWorker implements ISuppliesModule {
	public ModuleFertilizer(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	// lower numbers are prioritized
	@Override
	public byte getWorkPriority() {
		return 127;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected int getInventoryWidth() {
		return 1;
	}

	private int tankPosX = guiWidth() - 21;
	private int tankPosY = 20;
	private int range = 1;

	@Override
	public void init() {
		super.init();
		for (ModuleBase module : getVehicle().getModules()) {
			if (module instanceof ModuleFarmer) {
				range = ((ModuleFarmer) module).getExternalRange();
				break;
			}
		}
	}

	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/fertilize.png");

	@Override
	public void drawBackground(GuiVehicle gui, int x, int y) {
		ResourceHelper.bindResource(TEXTURE);
		float percentage = fertilizerStorage / (float) getMaxFertilizerStorage();
		int size = (int) (percentage * 23);
		drawImage(gui, tankPosX + 2, tankPosY + 2 + (23 - size), 20, 1 + (23 - size), 14, size);
		drawImage(gui, tankPosX, tankPosY, 1, 1, 18, 27);
	}

	@Override
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		drawStringOnMouseOver(gui, LocalizationCartCultivationUtil.FERTILIZER_TITLE.translate() + ": " + fertilizerStorage + " / " + getMaxFertilizerStorage(), x, y, tankPosX, tankPosY, 18, 27);
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, getModuleName(), 8, 6, 0x404040);
	}

	@Override
	public int guiWidth() {
		return super.guiWidth() + 25;
	}

	@Override
	public int guiHeight() {
		return Math.max(super.guiHeight(), 50);
	}

	@Override
	protected SlotBase getSlot(int slotId, int x, int y) {
		return new SlotFertilizer(getVehicle().getVehicleEntity(), slotId, 8 + x * 18, 23 + y * 18);
	}

	// return true when the work is done, false allow other modules to continue
	// the work
	@Override
	public boolean work() {
		// get the next block so the cart knows where to mine
		BlockPos next = getNextBlock();
		// loop through the blocks in the "hole" in front of the cart
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				// calculate the coordinates of this "hole"
				BlockPos target = next.add(i, -1, j);
				fertilize(getVehicle().getWorld(), target);
			}
		}
		return false;
	}

	private void fertilize(World world, BlockPos pos) {
		IBlockState stateAbove = world.getBlockState(pos.up());
		IBlockState state = world.getBlockState(pos);
		if (fertilizerStorage > 0) {
			/*
			 * if (state.getBlock() instanceof BlockCrops &&
			 * metadataOfBlockAbove != 7) { if ((metadata > 0 &&
			 * getVehicle().getRandom().nextInt(250) == 0) || (metadata == 0 &&
			 * getVehicle().getRandom().nextInt(1000) == 0)) {
			 * getVehicle().getWorld().setBlockMetadataWithNotify(x, y + 1, z,
			 * metadataOfBlockAbove + 1, 3); if
			 * (!getVehicle().hasCreativeSupplies()) { fertilizerStorage--; } }
			 * } else if (block instanceof BlockSapling &&
			 * getVehicle().getWorld().getBlockLightValue(x, y + 2, z) >= 9) {
			 * if (getVehicle().getRandom().nextInt(100) == 0) { if
			 * (getVehicle().getRandom().nextInt(6) == 0) {
			 * getVehicle().getWorld().setBlockMetadataWithNotify(x, y + 1, z,
			 * metadataOfBlockAbove | 8, 3); ((BlockSapling)
			 * Blocks.sapling).func_149878_d(getVehicle().getWorld(), x, y + 1,
			 * z, getVehicle().getRandom()); } if
			 * (!getVehicle().hasCreativeSupplies()) { fertilizerStorage--; } }
			 * }
			 */
			if (stateAbove.getBlock() instanceof IGrowable) {
				IGrowable growable = (IGrowable) stateAbove.getBlock();
				if (growable.canGrow(world, pos, stateAbove, false)) {
					if (growable.canUseBonemeal(world, world.rand, pos, stateAbove)) {
						growable.grow(world, world.rand, pos, stateAbove);
						--this.fertilizerStorage;
					}
				}
			}
		}
	}

	@Override
	public int numberOfGuiData() {
		return 1;
	}

	@Override
	protected void checkGuiData(Object[] info) {
		updateGuiData(info, 0, (short) fertilizerStorage);
	}

	@Override
	public void receiveGuiData(int id, short data) {
		if (id == 0) {
			fertilizerStorage = data;
		}
	}

	@Override
	public void update() {
		super.update();
		loadSupplies();
	}

	private void loadSupplies() {
		if (getVehicle().getWorld().isRemote) {
			return;
		}
		if (getStack(0) != null) {
			boolean isBone = getStack(0).getItem() == Items.BONE;
			boolean isBoneMeal = getStack(0).getItem() == Items.DYE && getStack(0).getItemDamage() == 15;
			if (isBone || isBoneMeal) {
				int amount;
				if (isBoneMeal) {
					amount = 1;
				} else {
					amount = 3;
				}
				if (fertilizerStorage <= FERTILIZERS_PER_BONE_MEAL * (MAX_STACKS_OF_BONES * BONE_MEALS_PER_BONE * STACK_SIZE - amount) && getStack(0).getCount() > 0) {
					getStack(0).shrink(1);
					fertilizerStorage += amount * FERTILIZERS_PER_BONE_MEAL;
				}
				if (getStack(0).getCount() == 0) {
					setStack(0, null);
				}
			}
		}
	}

	private int getMaxFertilizerStorage() {
		return FERTILIZERS_PER_BONE_MEAL * MAX_STACKS_OF_BONES * BONE_MEALS_PER_BONE * STACK_SIZE;
	}

	private int fertilizerStorage = 0;
	private static final int STACK_SIZE = 64;
	private static final int BONE_MEALS_PER_BONE = 3;
	private static final int FERTILIZERS_PER_BONE_MEAL = 4;
	private static final int MAX_STACKS_OF_BONES = 1;

	@Override
	protected void save(NBTTagCompound tagCompound) {
		tagCompound.setShort("Fertilizers", (short) fertilizerStorage);
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		fertilizerStorage = tagCompound.getShort("Fertilizers");
	}

	@Override
	public boolean haveSupplies() {
		return fertilizerStorage > 0;
	}
}
