package vswe.stevesvehicles.tileentity.distributor;

import net.minecraft.util.EnumFacing;
import vswe.stevesvehicles.localization.ILocalizedText;
import vswe.stevesvehicles.localization.entry.block.LocalizationDistributor;
import vswe.stevesvehicles.tileentity.TileEntityDistributor;

public class DistributorSide {
	private int id;
	private ILocalizedText name;
	private EnumFacing side;
	private int data;

	public DistributorSide(int id, ILocalizedText name, EnumFacing side) {
		this.name = name;
		this.id = id;
		this.side = side;
		this.data = 0;
	}

	public void setData(int data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name.translate();
	}

	public EnumFacing getSide() {
		return side;
	}

	public int getIntSide() {
		for (EnumFacing facing : EnumFacing.VALUES) {
			if (facing == side) {
				return facing.getIndex();
			}
		}
		return 6;
	}

	public int getData() {
		return data;
	}

	@SuppressWarnings("SimplifiableIfStatement") // more clear what it does like
	// this
	public boolean isEnabled(TileEntityDistributor distributor) {
		if (distributor.getInventories().length == 0) {
			return false;
		} else if (getSide() == EnumFacing.DOWN) {
			return !distributor.hasBot;
		} else if (getSide() == EnumFacing.UP) {
			return !distributor.hasTop;
		} else {
			return true;
		}
	}

	public boolean isSet(int id) {
		return (data & (1 << id)) != 0;
	}

	public void set(int id) {
		int count = 0;
		for (DistributorSetting setting : DistributorSetting.settings) {
			if (isSet(setting.getId())) {
				count++;
			}
		}
		if (count < 11) {
			data |= (1 << id);
		}
	}

	public void reset(int id) {
		data &= ~(1 << id);
	}

	public short getLowShortData() {
		return (short) (getData() & 65535);
	}

	public short getHighShortData() {
		return (short) ((getData() >> 16) & 65535);
	}

	public void setLowShortData(short data) {
		this.data = (fixSignedIssue(getHighShortData()) << 16) | fixSignedIssue(data);
	}

	public void setHighShortData(short data) {
		this.data = fixSignedIssue(getLowShortData()) | (fixSignedIssue(data) << 16);
	}

	private int fixSignedIssue(short val) {
		if (val < 0) {
			return val + 65536;
		} else {
			return val;
		}
	}

	public String getInfo() {
		return LocalizationDistributor.SIDE_TOOLTIP.translate(getName());
	}
}