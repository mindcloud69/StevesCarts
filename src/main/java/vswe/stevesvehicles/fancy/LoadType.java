package vswe.stevesvehicles.fancy;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum LoadType {
	KEEP("Keep"), OVERRIDE("Override"), REQUIRE("Require");
	private String code;

	LoadType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
