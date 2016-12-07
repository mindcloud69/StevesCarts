package stevesvehicles.common.blocks.tileentitys.distributor;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import stevesvehicles.api.modules.data.ILocalizedText;
import stevesvehicles.client.localization.entry.block.LocalizationDistributor;
import stevesvehicles.common.blocks.tileentitys.TileEntityDistributor;

public class DistributorSide implements IFluidHandler {
	private int id;
	private ILocalizedText name;
	private EnumFacing side;
	private int data;
	private TileEntityDistributor tile;

	public DistributorSide(int id, ILocalizedText name, EnumFacing side, TileEntityDistributor tile) {
		this.name = name;
		this.id = id;
		this.side = side;
		this.data = 0;
		this.tile = tile;
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

	// more clear what it does like
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

	/**
	 * Fills fluid into internal tanks, distribution is left to the
	 * ITankContainer.
	 * 
	 * @param from
	 *            Orientation the fluid is pumped in from.
	 * @param resource
	 *            FluidStack representing the maximum amount of fluid filled
	 *            into the ITankContainer
	 * @param doFill
	 *            If false filling will only be simulated.
	 * @return Amount of resource that was filled into internal tanks.
	 */
	@Override
	public int fill(FluidStack resource, boolean doFill) {
		IFluidTank[] tanks = tile.getTanks(side);
		int amount = 0;
		for (IFluidTank tank : tanks) {
			amount += tank.fill(resource, doFill);
		}
		return amount;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return drain(null, maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return drain(resource, resource == null ? 0 : resource.amount, doDrain);
	}

	private FluidStack drain(FluidStack resource, int maxDrain, boolean doDrain) {
		FluidStack ret = resource;
		if (ret != null) {
			ret = ret.copy();
			ret.amount = 0;
		}
		IFluidTank[] tanks = tile.getTanks(side);
		for (IFluidTank tank : tanks) {
			FluidStack temp;
			temp = tank.drain(maxDrain, doDrain);
			if (temp != null && (ret == null || ret.isFluidEqual(temp))) {
				if (ret == null) {
					ret = temp;
				} else {
					ret.amount += temp.amount;
				}
				maxDrain -= temp.amount;
				if (maxDrain <= 0) {
					break;
				}
			}
		}
		if (ret != null && ret.amount == 0) {
			return null;
		}
		return ret;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		IFluidTank[] tanks = tile.getTanks(side);
		IFluidTankProperties[] info = new IFluidTankProperties[tanks.length];
		for (int i = 0; i < info.length; i++) {
			info[i] = new FluidTankProperties(tanks[i].getFluid(), tanks[i].getCapacity());
		}
		return info;
	}
}
