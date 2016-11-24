package stevesvehicles.common.container;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.blocks.tileentitys.TileEntityBase;
import stevesvehicles.common.container.slots.SlotBase;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.vehicles.VehicleBase;

public class ContainerVehicle extends ContainerBase {
	private VehicleBase vehicle;

	public ContainerVehicle(IInventory player, VehicleBase vehicle) {
		initVehicleSlots(vehicle);
		initPlayerInventory(player);
	}

	@Override
	public IInventory getMyInventory() {
		return vehicle.getVehicleEntity();
	}

	@Override
	public TileEntityBase getTileEntity() {
		return null;
	}

	private void initVehicleSlots(VehicleBase vehicle) {
		this.vehicle = vehicle;
		if (vehicle.getModules() != null) {
			for (ModuleBase module : vehicle.getModules()) {
				if (module.hasSlots()) {
					ArrayList<SlotBase> slotsList = module.getSlots();
					for (SlotBase slot : slotsList) {
						slot.xPos = slot.getX() + module.getX() + 1;
						slot.yPos = slot.getY() + module.getY() + 1;
						addSlotToContainer(slot);
					}
				}
			}
		} else {
			for (int i = 0; i < 100; i++) {
				addSlotToContainer(new Slot(vehicle.getVehicleEntity(), i, -1000, -1000));
			}
		}
	}

	private void initPlayerInventory(IInventory player) {
		int startX = 159;
		int startY = 174;
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(player, k + i * 9 + 9, startX + k * 18, i * 18 + startY));
			}
		}
		for (int j = 0; j < 9; j++) {
			addSlotToContainer(new Slot(player, j, startX + j * 18, 58 + startY));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return vehicle.getVehicleEntity().isUsableByPlayer(player);
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		vehicle.getVehicleEntity().closeInventory(player);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int val) {
		val &= 65535;
		if (vehicle.getModules() != null) {
			for (ModuleBase module : vehicle.getModules()) {
				if (id >= module.getGuiDataStart() && id < module.getGuiDataStart() + module.numberOfGuiData()) {
					module.receiveGuiData(id - module.getGuiDataStart(), (short) val);
					break;
				}
			}
		}
	}

	public HashMap<Short, Short> cache;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (vehicle.getModules() != null) {
			if (listeners.size() > 0) {
				for (ModuleBase module : vehicle.getModules()) {
					module.checkGuiData(this, listeners, false);
				}
			}
		}
	}

	public VehicleBase getVehicle() {
		return vehicle;
	}
}
