package vswe.stevescarts.Modules.Storages.Tanks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.ColorHelper;
import vswe.stevescarts.Helpers.Localization;

public class ModuleCheatTank extends ModuleTank {
	private static final ColorHelper[] colors;
	private int mode;

	public ModuleCheatTank(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected String getTankInfo() {
		String str = super.getTankInfo();
		str = str + "\n\n" + Localization.MODULES.TANKS.CREATIVE_MODE.translate(ModuleCheatTank.colors[this.mode].toString(), String.valueOf(this.mode)) + "\n" + Localization.MODULES.TANKS.CHANGE_MODE.translate();
		if (this.mode != 0) {
			str = str + "\n" + Localization.MODULES.TANKS.RESET_MODE.translate();
		}
		return str;
	}

	@Override
	protected int getTankSize() {
		return 2000000000;
	}

	@Override
	public boolean hasVisualTank() {
		return false;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0 && (data[0] & 0x1) != 0x0) {
			if (this.mode != 0 && (data[0] & 0x2) != 0x0) {
				this.mode = 0;
			} else if (++this.mode == ModuleCheatTank.colors.length) {
				this.mode = 1;
			}
			this.updateAmount();
			this.updateDw();
		} else {
			super.receivePacket(id, data, player);
		}
	}

	@Override
	public int numberOfGuiData() {
		return super.numberOfGuiData() + 1;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		super.checkGuiData(info);
		this.updateGuiData(info, super.numberOfGuiData(), (short) this.mode);
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == super.numberOfGuiData()) {
			this.mode = data;
		} else {
			super.receiveGuiData(id, data);
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		super.Save(tagCompound, id);
		tagCompound.setByte(this.generateNBTName("mode", id), (byte) this.mode);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		super.Load(tagCompound, id);
		this.mode = tagCompound.getByte(this.generateNBTName("mode", id));
	}

	private void updateAmount() {
		if (this.tank.getFluid() != null) {
			if (this.mode == 1) {
				this.tank.getFluid().amount = this.getTankSize();
			} else if (this.mode == 2) {
				this.tank.getFluid().amount = 0;
				if (!this.tank.isLocked()) {
					this.tank.setFluid(null);
				}
			} else if (this.mode == 3) {
				this.tank.getFluid().amount = this.getTankSize() / 2;
			}
		}
	}

	@Override
	public void onFluidUpdated(final int tankid) {
		this.updateAmount();
		super.onFluidUpdated(tankid);
	}

	static {
		colors = new ColorHelper[] { ColorHelper.YELLOW, ColorHelper.GREEN, ColorHelper.RED, ColorHelper.ORANGE };
	}
}
