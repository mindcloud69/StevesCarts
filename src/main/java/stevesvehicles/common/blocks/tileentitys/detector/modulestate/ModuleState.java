package stevesvehicles.common.blocks.tileentitys.detector.modulestate;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.common.vehicles.VehicleBase;

public abstract class ModuleState {
	private String fullUnlocalizedName;
	private final String unlocalizedName;
	private ResourceLocation texture;

	public ModuleState(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
	}

	public final String getRawUnlocalizedName() {
		return unlocalizedName;
	}

	public final String getFullRawUnlocalizedName() {
		return fullUnlocalizedName;
	}

	public final void setFullUnlocalizedName(String val) {
		fullUnlocalizedName = val;
	}

	public ResourceLocation getTexture() {
		if (texture == null) {
			texture = ResourceHelper.getResource("/gui/states/" + fullUnlocalizedName.replace(":", "/") + ".png");
		}
		return texture;
	}

	public String getUnlocalizedName() {
		return "steves_vehicles:gui.state." + fullUnlocalizedName;
	}

	public abstract boolean isValid(VehicleBase vehicle);

	public String getName() {
		return I18n.translateToLocal(getUnlocalizedName());
	}
}
