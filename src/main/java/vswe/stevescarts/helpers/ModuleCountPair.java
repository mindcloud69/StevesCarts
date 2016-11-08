package vswe.stevescarts.helpers;

import net.minecraft.util.text.translation.I18n;
import vswe.stevescarts.modules.data.ModuleData;

public class ModuleCountPair {
	private ModuleData data;
	private int count;
	private String name;
	private byte extraData;

	public ModuleCountPair(final ModuleData data) {
		this.data = data;
		this.count = 1;
		this.name = data.getUnlocalizedName();
	}

	public int getCount() {
		return this.count;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void increase() {
		++this.count;
	}

	public boolean isContainingData(final ModuleData data) {
		return this.data.equals(data);
	}

	public ModuleData getData() {
		return this.data;
	}

	public void setExtraData(final byte b) {
		this.extraData = b;
	}

	@Override
	public String toString() {
		String ret = this.data.getCartInfoText(I18n.translateToLocal(this.name), this.extraData);
		if (this.count != 1) {
			ret = ret + " x" + this.count;
		}
		return ret;
	}
}
