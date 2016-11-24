package stevesvehicles.common.vehicles;

import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.vehicles.entitys.IVehicleEntity;

public class VehicleType {
	private Class<? extends IVehicleEntity> clazz;
	private final String unlocalizedName;
	@SideOnly(Side.CLIENT) 
	private String icon;

	public VehicleType(Class<? extends IVehicleEntity> clazz, String unlocalizedName) {
		this.clazz = clazz;
		this.unlocalizedName = unlocalizedName;
	}

	public String getUnlocalizedNameForItem() {
		return "steves_vehicles:item.vehicle:" + unlocalizedName;
	}

	public String getUnlocalizedName() {
		return getUnlocalizedNameForItem() + ".name";
	}

	public final String getRawUnlocalizedName() {
		return unlocalizedName;
	}

	public Class<? extends IVehicleEntity> getClazz() {
		return clazz;
	}

	public String getName() {
		return I18n.translateToLocal(getUnlocalizedName());
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
