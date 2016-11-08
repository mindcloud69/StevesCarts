package vswe.stevescarts.Modules.Engines;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;

public abstract class ModuleSolarBase extends ModuleEngine {
	private int light;
	private boolean maxLight;
	private int panelCoolDown;
	private boolean down;
	private boolean upState;
	
	private static DataParameter<Integer> LIGHT = createDw(DataSerializers.VARINT);
	private static DataParameter<Boolean> UP_STATE = createDw(DataSerializers.BOOLEAN);

	public ModuleSolarBase(final MinecartModular cart) {
		super(cart);
		this.down = true;
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public void update() {
		super.update();
		this.updateSolarModel();
	}

	@Override
	protected void loadFuel() {
		this.updateLight();
		this.updateDataForModel();
		this.chargeSolar();
	}

	@Override
	public int getTotalFuel() {
		return this.getFuelLevel();
	}

	@Override
	public float[] getGuiBarColor() {
		return new float[] { 1.0f, 1.0f, 0.0f };
	}

	private void updateLight() {
		this.light = this.getCart().worldObj.getLight(this.getCart().getPosition());
		if (this.light == 15 && !this.getCart().worldObj.canBlockSeeSky(this.getCart().getPosition())) {
			this.light = 14;
		}
	}

	private void updateDataForModel() {
		if (this.isPlaceholder()) {
			this.light = (this.getSimInfo().getMaxLight() ? 15 : 14);
		} else if (this.getCart().worldObj.isRemote) {
			this.light = this.getDw(LIGHT);
		} else {
			this.updateDw(LIGHT, this.light);
		}
		this.maxLight = (this.light == 15);
		if (!this.upState && this.light == 15) {
			this.light = 14;
		}
	}

	private void chargeSolar() {
		if (this.light == 15 && this.getCart().worldObj.rand.nextInt(8) < 4) {
			this.setFuelLevel(this.getFuelLevel() + this.getGenSpeed());
			if (this.getFuelLevel() > this.getMaxCapacity()) {
				this.setFuelLevel(this.getMaxCapacity());
			}
		}
	}

	public int getLight() {
		return this.light;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ENGINES.SOLAR.translate(), 8, 6, 4210752);
		String strfuel = Localization.MODULES.ENGINES.NO_POWER.translate();
		if (this.getFuelLevel() > 0) {
			strfuel = Localization.MODULES.ENGINES.POWER.translate(String.valueOf(this.getFuelLevel()));
		}
		this.drawString(gui, strfuel, 8, 42, 4210752);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		super.drawBackground(gui, x, y);
		ResourceHelper.bindResource("/gui/solar.png");
		int lightWidth = this.light * 3;
		if (this.light == 15) {
			lightWidth += 2;
		}
		this.drawImage(gui, 9, 20, 0, 0, 54, 18);
		this.drawImage(gui, 15, 21, 0, 18, lightWidth, 16);
	}

	@Override
	public int numberOfDataWatchers() {
		return super.numberOfDataWatchers() + 2;
	}

	@Override
	public void initDw() {
		super.initDw();
		registerDw(LIGHT, 0);
		registerDw(UP_STATE, false);
	}

	protected boolean isGoingDown() {
		return this.down;
	}

	public void updateSolarModel() {
		if (this.getCart().worldObj.isRemote) {
			this.updateDataForModel();
		}
		this.panelCoolDown += (this.maxLight ? 1 : -1);
		if (this.down && this.panelCoolDown < 0) {
			this.panelCoolDown = 0;
		} else if (!this.down && this.panelCoolDown > 0) {
			this.panelCoolDown = 0;
		} else if (Math.abs(this.panelCoolDown) > 20) {
			this.panelCoolDown = 0;
			this.down = !this.down;
		}
		this.upState = this.updatePanels();
		if (!this.getCart().worldObj.isRemote) {
			this.updateDw(UP_STATE, this.upState);
		}
	}

	@Override
	public int numberOfGuiData() {
		return 2;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		this.updateGuiData(info, 0, (short) (this.getFuelLevel() & 0xFFFF));
		this.updateGuiData(info, 1, (short) (this.getFuelLevel() >> 16 & 0xFFFF));
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			int dataint = data;
			if (dataint < 0) {
				dataint += 65536;
			}
			this.setFuelLevel((this.getFuelLevel() & 0xFFFF0000) | dataint);
		} else if (id == 1) {
			this.setFuelLevel((this.getFuelLevel() & 0xFFFF) | data << 16);
		}
	}

	protected abstract int getMaxCapacity();

	protected abstract int getGenSpeed();

	protected abstract boolean updatePanels();

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		super.Save(tagCompound, id);
		tagCompound.setInteger(this.generateNBTName("Fuel", id), this.getFuelLevel());
		tagCompound.setBoolean(this.generateNBTName("Up", id), this.upState);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		super.Load(tagCompound, id);
		this.setFuelLevel(tagCompound.getInteger(this.generateNBTName("Fuel", id)));
		this.upState = tagCompound.getBoolean(this.generateNBTName("Up", id));
	}
}
