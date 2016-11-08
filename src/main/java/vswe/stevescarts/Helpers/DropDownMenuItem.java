package vswe.stevescarts.Helpers;

import vswe.stevescarts.Modules.ModuleBase;

public class DropDownMenuItem {
	private String name;
	private int imageID;
	private VALUETYPE type;
	private boolean isLarge;
	private boolean subOpen;
	private byte value;
	private Class<? extends ModuleBase> moduleClass;
	private Class<? extends ModuleBase> excludedClass;
	private int multiCount;
	private int intMinValue;
	private int intMaxValue;

	public DropDownMenuItem(final String name, final int imageID, final VALUETYPE type, final Class<? extends ModuleBase> moduleClass) {
		this(name, imageID, type, moduleClass, null);
	}

	public DropDownMenuItem(final String name, final int imageID, final VALUETYPE type, final Class<? extends ModuleBase> moduleClass, final Class<? extends ModuleBase> excludedClass) {
		this.name = name;
		this.imageID = imageID;
		this.type = type;
		this.moduleClass = moduleClass;
		this.excludedClass = excludedClass;
		this.isLarge = false;
		this.subOpen = false;
		this.value = 0;
	}

	public String getName() {
		return this.name;
	}

	public Class<? extends ModuleBase> getModuleClass() {
		return this.moduleClass;
	}

	public Class<? extends ModuleBase> getExcludedClass() {
		return this.excludedClass;
	}

	public int getImageID() {
		return this.imageID;
	}

	public boolean hasSubmenu() {
		return this.type != VALUETYPE.BOOL;
	}

	public boolean getIsSubMenuOpen() {
		return this.subOpen;
	}

	public void setIsSubMenuOpen(final boolean val) {
		this.subOpen = val;
	}

	public boolean getIsLarge() {
		return this.isLarge;
	}

	public void setIsLarge(final boolean val) {
		this.isLarge = val;
	}

	public int[] getRect(final int menuX, final int menuY, final int id) {
		if (this.getIsLarge()) {
			return new int[] { menuX, menuY + id * 20, 130, 20 };
		}
		return new int[] { menuX, menuY + id * 20, 54, 20 };
	}

	public int[] getSubRect(final int menuX, final int menuY, final int id) {
		if (this.getIsSubMenuOpen()) {
			return new int[] { menuX - 43, menuY + id * 20 + 2, 52, 16 };
		}
		return new int[] { menuX, menuY + id * 20 + 2, 9, 16 };
	}

	public VALUETYPE getType() {
		return this.type;
	}

	public boolean getBOOL() {
		return this.value != 0;
	}

	public void setBOOL(final boolean val) {
		this.value = (byte) (val ? 1 : 0);
	}

	public int getINT() {
		return this.value;
	}

	public void setINT(int val) {
		if (val < this.intMinValue) {
			val = this.intMinValue;
		} else if (val > this.intMaxValue) {
			val = this.intMaxValue;
		}
		this.value = (byte) val;
	}

	public void setMULTIBOOL(final byte val) {
		this.value = val;
	}

	public void setMULTIBOOL(final int i, final boolean val) {
		this.value = (byte) ((this.value & ~(1 << i)) | (val ? 1 : 0) << i);
	}

	public byte getMULTIBOOL() {
		return this.value;
	}

	public boolean getMULTIBOOL(final int i) {
		return (this.value & 1 << i) != 0x0;
	}

	public void setMULTIBOOLCount(int val) {
		if (val > 4) {
			val = 4;
		} else if (val < 2) {
			val = 2;
		}
		this.multiCount = val;
	}

	public int getMULTIBOOLCount() {
		return this.multiCount;
	}

	public void setINTLimit(final int min, final int max) {
		this.intMinValue = min;
		this.intMaxValue = max;
		this.setINT(this.getINT());
	}

	public enum VALUETYPE {
		BOOL,
		INT,
		MULTIBOOL
	}
}
