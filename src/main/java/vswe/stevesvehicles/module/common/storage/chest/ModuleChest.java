package vswe.stevesvehicles.module.common.storage.chest;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfo;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfoBoolean;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.container.slots.SlotBase;
import vswe.stevesvehicles.container.slots.SlotChest;
import vswe.stevesvehicles.localization.entry.block.LocalizationAssembler;
import vswe.stevesvehicles.module.common.storage.ModuleStorage;
import vswe.stevesvehicles.vehicle.VehicleBase;

public abstract class ModuleChest extends ModuleStorage {
	private DataParameter<Boolean> IS_OPEN;

	public ModuleChest(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
		if (hasVisualChest()) {
			simulationInfo.add(new SimulationInfoBoolean(LocalizationAssembler.INFO_CHEST, "storage"));
		}
	}

	// called to update the module's actions. Called by the cart's update code.
	@Override
	public void update() {
		super.update();
		handleChest();
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected SlotBase getSlot(int slotId, int x, int y) {
		return new SlotChest(getVehicle().getVehicleEntity(), slotId, 8 + x * 18, 16 + y * 18);
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, getModuleName(), 8, 6, 0x404040);
	}

	@Override
	public int guiWidth() {
		return 15 + getInventoryWidth() * 18;
	}

	@Override
	public int guiHeight() {
		return 20 + getInventoryHeight() * 18;
	}

	private float chestAngle;

	public float getChestAngle() {
		return chestAngle;
	}

	protected boolean lidClosed() {
		return chestAngle <= 0.0F;
	}

	protected float getLidSpeed() {
		return (float) (Math.PI / 20);
	}

	protected float chestFullyOpenAngle() {
		return (float) Math.PI * 7 / 16F;
	}

	protected boolean hasVisualChest() {
		return true;
	}

	protected boolean playChestSound() {
		return hasVisualChest();
	}

	@Override
	public int numberOfDataWatchers() {
		if (hasVisualChest()) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public void initDw() {
		if (hasVisualChest()) {
			IS_OPEN = createDw(DataSerializers.BOOLEAN);
			registerDw(IS_OPEN, false);
		}
	}

	@Override
	public void openInventory(EntityPlayer player) {
		if (hasVisualChest()) {
			updateDw(IS_OPEN, true);
		}
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		if (hasVisualChest()) {
			updateDw(IS_OPEN, false);
		}
	}

	protected boolean isChestActive() {
		if (hasVisualChest()) {
			if (isPlaceholder()) {
				return getBooleanSimulationInfo();
			} else {
				return getDw(IS_OPEN);
			}
		} else {
			return false;
		}
	}

	protected void handleChest() {
		if (!hasVisualChest()) {
			return;
		}
		if (isChestActive() && lidClosed() && playChestSound()) {
			getVehicle().getEntity().playSound(SoundEvents.BLOCK_CHEST_OPEN, 0.5F, getVehicle().getRandom().nextFloat() * 0.1F + 0.9F);
		}
		if (isChestActive() && chestAngle < chestFullyOpenAngle()) {
			chestAngle += getLidSpeed();
			if (chestAngle > chestFullyOpenAngle()) {
				chestAngle = chestFullyOpenAngle();
			}
		} else if (!isChestActive() && !lidClosed()) {
			float lastAngle = chestAngle;
			chestAngle -= getLidSpeed();
			if (chestAngle < Math.PI * 3 / 8 && lastAngle >= Math.PI * 3 / 8 && playChestSound()) {
				getVehicle().getEntity().playSound(SoundEvents.BLOCK_CHEST_CLOSE, 0.5F, getVehicle().getRandom().nextFloat() * 0.1F + 0.9F);
			}
			if (chestAngle < 0.0F) {
				chestAngle = 0.0F;
			}
		}
	}

	public boolean isCompletelyFilled() {
		for (int i = 0; i < getInventorySize(); i++) {
			if (getStack(i) == null) {
				return false;
			}
		}
		return true;
	}

	public boolean isCompletelyEmpty() {
		for (int i = 0; i < getInventorySize(); i++) {
			if (getStack(i) != null) {
				return false;
			}
		}
		return true;
	}
}