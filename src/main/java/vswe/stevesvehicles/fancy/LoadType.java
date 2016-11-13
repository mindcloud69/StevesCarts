package vswe.stevesvehicles.fancy;

@SideOnly(Side.CLIENT)
public enum LoadType {
	KEEP("Keep"),
	OVERRIDE("Override"),
	REQUIRE("Require");

	private String code;

	LoadType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
