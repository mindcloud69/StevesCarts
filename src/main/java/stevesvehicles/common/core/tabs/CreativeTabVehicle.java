package stevesvehicles.common.core.tabs;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.localization.LocalizedTextSimple;
import stevesvehicles.common.items.ModItems;
import stevesvehicles.common.vehicles.VehicleRegistry;
import stevesvehicles.common.vehicles.VehicleType;

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
