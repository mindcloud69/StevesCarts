package vswe.stevesvehicles.tab;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.localization.LocalizedTextSimple;
import vswe.stevesvehicles.vehicle.VehicleRegistry;
import vswe.stevesvehicles.vehicle.VehicleType;

public class CreativeTabVehicle extends CreativeTabCustom {
	private VehicleType vehicleType;

	public CreativeTabVehicle(VehicleType vehicleType) {
		super(new LocalizedTextSimple(vehicleType.getUnlocalizedName()));
		this.vehicleType = vehicleType;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getIconItemStack() {
		int id = VehicleRegistry.getInstance().getIdFromType(vehicleType);
		if (id >= 0) {
			return new ItemStack(ModItems.vehicles, 1, id);
		} else {
			return null;
		}
	}
}
