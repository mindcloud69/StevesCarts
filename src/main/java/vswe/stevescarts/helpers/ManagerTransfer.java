package vswe.stevescarts.helpers;

import vswe.stevescarts.entitys.MinecartModular;

public class ManagerTransfer {
	private int side;
	private int setting;
	private int lastsetting;
	private int lowestsetting;
	private int workload;
	private MinecartModular cart;
	private boolean toCartEnabled;
	private boolean fromCartEnabled;

	public ManagerTransfer() {
		this.reset();
	}

	public void reset() {
		this.side = 0;
		this.setting = -1;
		this.lastsetting = 0;
		this.lowestsetting = 0;
		this.workload = 0;
		this.cart = null;
		this.toCartEnabled = true;
		this.fromCartEnabled = true;
	}

	public int getSetting() {
		return this.setting;
	}

	public void setSetting(final int val) {
		this.setting = val;
	}

	public int getSide() {
		return this.side;
	}

	public void setSide(final int val) {
		this.side = val;
	}

	public int getLastSetting() {
		return this.lastsetting;
	}

	public void setLastSetting(final int val) {
		this.lastsetting = val;
	}

	public int getLowestSetting() {
		return this.lowestsetting;
	}

	public void setLowestSetting(final int val) {
		this.lowestsetting = val;
	}

	public int getWorkload() {
		return this.workload;
	}

	public void setWorkload(final int val) {
		this.workload = val;
	}

	public MinecartModular getCart() {
		return this.cart;
	}

	public void setCart(final MinecartModular val) {
		this.cart = val;
	}

	public boolean getFromCartEnabled() {
		return this.fromCartEnabled;
	}

	public void setFromCartEnabled(final boolean val) {
		this.fromCartEnabled = val;
	}

	public boolean getToCartEnabled() {
		return this.toCartEnabled;
	}

	public void setToCartEnabled(final boolean val) {
		this.toCartEnabled = val;
	}
}
