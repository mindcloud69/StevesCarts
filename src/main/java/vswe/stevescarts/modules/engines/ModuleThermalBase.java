package vswe.stevescarts.modules.engines;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fluids.FluidRegistry;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;

public abstract class ModuleThermalBase extends ModuleEngine {
	private short coolantLevel;
	private static final int RELOAD_LIQUID_SIZE = 1;
	private DataParameter<Integer> PRIORITY = createDw(DataSerializers.VARINT);

	public ModuleThermalBase(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	protected DataParameter<Integer> getPriorityDw() {
		return PRIORITY;
	}

	private int getCoolantLevel() {
		return this.coolantLevel;
	}

	private void setCoolantLevel(final int val) {
		this.coolantLevel = (short) val;
	}

	@Override
	protected void initPriorityButton() {
		this.priorityButton = new int[] { 72, 17, 16, 16 };
	}

	protected abstract int getEfficiency();

	protected abstract int getCoolantEfficiency();

	private boolean requiresCoolant() {
		return this.getCoolantEfficiency() > 0;
	}

	@Override
	public int guiHeight() {
		return 40;
	}

	@Override
	public boolean hasFuel(final int consumption) {
		return super.hasFuel(consumption) && (!this.requiresCoolant() || this.getCoolantLevel() >= consumption);
	}

	@Override
	public void consumeFuel(final int consumption) {
		super.consumeFuel(consumption);
		this.setCoolantLevel(this.getCoolantLevel() - consumption);
	}

	@Override
	protected void loadFuel() {
		final int consumption = this.getCart().getConsumption(true) * 2;
		while (this.getFuelLevel() <= consumption) {
			final int amount = this.getCart().drain(FluidRegistry.LAVA, 1, false);
			if (amount <= 0) {
				break;
			}
			this.getCart().drain(FluidRegistry.LAVA, amount, true);
			this.setFuelLevel(this.getFuelLevel() + amount * this.getEfficiency());
		}
		while (this.requiresCoolant() && this.getCoolantLevel() <= consumption) {
			final int amount = this.getCart().drain(FluidRegistry.WATER, 1, false);
			if (amount <= 0) {
				break;
			}
			this.getCart().drain(FluidRegistry.WATER, amount, true);
			this.setCoolantLevel(this.getCoolantLevel() + amount * this.getCoolantEfficiency());
		}
	}

	@Override
	public int getTotalFuel() {
		final int totalfuel = this.getFuelLevel() + this.getCart().drain(FluidRegistry.LAVA, Integer.MAX_VALUE, false) * this.getEfficiency();
		if (this.requiresCoolant()) {
			final int totalcoolant = this.getCoolantLevel() + this.getCart().drain(FluidRegistry.WATER, Integer.MAX_VALUE, false) * this.getCoolantEfficiency();
			return Math.min(totalcoolant, totalfuel);
		}
		return totalfuel;
	}

	@Override
	public float[] getGuiBarColor() {
		return new float[] { 1.0f, 0.0f, 0.0f };
	}

	@Override
	public void smoke() {
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ENGINES.THERMAL.translate(), 8, 6, 4210752);
		int consumption = this.getCart().getConsumption();
		if (consumption == 0) {
			consumption = 1;
		}
		String str;
		if (this.getFuelLevel() >= consumption && (!this.requiresCoolant() || this.getCoolantLevel() >= consumption)) {
			str = Localization.MODULES.ENGINES.POWERED.translate();
		} else if (this.getFuelLevel() >= consumption) {
			str = Localization.MODULES.ENGINES.NO_WATER.translate();
		} else {
			str = Localization.MODULES.ENGINES.NO_LAVA.translate();
		}
		this.drawString(gui, str, 8, 22, 4210752);
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public int numberOfGuiData() {
		return 2;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		this.updateGuiData(info, 0, (short) this.getFuelLevel());
		if (this.requiresCoolant()) {
			this.updateGuiData(info, 1, (short) this.getCoolantLevel());
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.setFuelLevel(data);
		} else if (id == 1) {
			this.setCoolantLevel(data);
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		super.Save(tagCompound, id);
		tagCompound.setShort(this.generateNBTName("Fuel", id), (short) this.getFuelLevel());
		if (this.requiresCoolant()) {
			tagCompound.setShort(this.generateNBTName("Coolant", id), (short) this.getCoolantLevel());
		}
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		super.Load(tagCompound, id);
		this.setFuelLevel(tagCompound.getShort(this.generateNBTName("Fuel", id)));
		if (this.requiresCoolant()) {
			this.setCoolantLevel(tagCompound.getShort(this.generateNBTName("Coolant", id)));
		}
	}
}
