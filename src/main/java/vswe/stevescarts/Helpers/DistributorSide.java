package vswe.stevescarts.Helpers;

import net.minecraft.util.EnumFacing;
import vswe.stevescarts.TileEntities.TileEntityDistributor;

public class DistributorSide {
	private int id;
	private Localization.GUI.DISTRIBUTOR name;
	private EnumFacing side;
	private int data;

	public DistributorSide(final int id, final Localization.GUI.DISTRIBUTOR name, final EnumFacing side) {
		this.name = name;
		this.id = id;
		this.side = side;
		this.data = 0;
	}

	public void setData(final int data) {
		this.data = data;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name.translate();
	}

	public EnumFacing getSide() {
		return this.side;
	}

	public EnumFacing getFacing() {
		return side;
	}

	public int getData() {
		return this.data;
	}

	public boolean isEnabled(final TileEntityDistributor distributor) {
		if (distributor.getInventories().length == 0) {
			return false;
		}
		if (this.getSide() == EnumFacing.DOWN) {
			return !distributor.hasBot;
		}
		return this.getSide() != EnumFacing.UP || !distributor.hasTop;
	}

	public boolean isSet(final int id) {
		return (this.data & 1 << id) != 0x0;
	}

	public void set(final int id) {
		int count = 0;
		for (final DistributorSetting setting : DistributorSetting.settings) {
			if (this.isSet(setting.getId())) {
				++count;
			}
		}
		if (count < 11) {
			this.data |= 1 << id;
		}
	}

	public void reset(final int id) {
		this.data &= ~(1 << id);
	}

	public short getLowShortData() {
		return (short) (this.getData() & 0xFFFF);
	}

	public short getHighShortData() {
		return (short) (this.getData() >> 16 & 0xFFFF);
	}

	public void setLowShortData(final short data) {
		this.data = (this.fixSignedIssue(this.getHighShortData()) << 16 | this.fixSignedIssue(data));
	}

	public void setHighShortData(final short data) {
		this.data = (this.fixSignedIssue(this.getLowShortData()) | this.fixSignedIssue(data) << 16);
	}

	private int fixSignedIssue(final short val) {
		if (val < 0) {
			return val + 65536;
		}
		return val;
	}

	public String getInfo() {
		return Localization.GUI.DISTRIBUTOR.SIDE_TOOL_TIP.translate(this.getName());
	}
}
