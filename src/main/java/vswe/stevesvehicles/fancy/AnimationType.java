package vswe.stevesvehicles.fancy;

@SideOnly(Side.CLIENT)
public enum AnimationType {
	STILL("Still"),
	ANIMATION("Loop"),
	PAUSE("Pause"),
	RANDOM("Random");

	private String code;

	AnimationType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
