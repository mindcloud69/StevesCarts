package vswe.stevescarts.modules.engines;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;

public abstract class ModuleSolarBase extends ModuleEngine {
	private int light;
	private boolean maxLight;
	private int panelCoolDown;
	private boolean down;
	private boolean upState;

	private DataParameter<Integer> LIGHT;
	private DataParameter<Boolean> UP_STATE;
	private DataParameter<Integer> PRIORITY;

	public ModuleSolarBase(final EntityMinecartModular cart) {
		super(cart);
		down = true;
	}

	@Override
	protected DataParameter<Integer> getPriorityDw() {
		return PRIORITY;
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public void update() {
		super.update();
		updateSolarModel();
	}

	@Override
	protected void loadFuel() {
		updateLight();
		updateDataForModel();
		chargeSolar();
	}

	@Override
	public int getTotalFuel() {
		return getFuelLevel();
	}

	@Override
	public float[] getGuiBarColor() {
		return new float[] { 1.0f, 1.0f, 0.0f };
	}

	private void updateLight() {
		if (!getCart().world.isDaytime() || getCart().world.isRaining()) {
			light = 0;
		} else {
			light = getCart().world.getLight(getCart().getPosition());
			if (light == 15 && !getCart().world.canBlockSeeSky(getCart().getPosition())) {
				light = 14;
			}
		}
	}

	private void updateDataForModel() {
		if (isPlaceholder()) {
			light = (getSimInfo().getMaxLight() ? 15 : 14);
		} else if (getCart().world.isRemote) {
			light = getDw(LIGHT);
		} else {
			updateDw(LIGHT, light);
		}
		maxLight = (light == 15);
		if (!upState && light == 15) {
			light = 14;
		}
	}

	private void chargeSolar() {
		if (light == 15 && getCart().world.rand.nextInt(8) < 4) {
			setFuelLevel(getFuelLevel() + getGenSpeed());
			if (getFuelLevel() > getMaxCapacity()) {
				setFuelLevel(getMaxCapacity());
			}
		}
	}

	public int getLight() {
		return light;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		drawString(gui, Localization.MODULES.ENGINES.SOLAR.translate(), 8, 6, 4210752);
		String strfuel = Localization.MODULES.ENGINES.NO_POWER.translate();
		if (getFuelLevel() > 0) {
			strfuel = Localization.MODULES.ENGINES.POWER.translate(String.valueOf(getFuelLevel()));
		}
		drawString(gui, strfuel, 8, 42, 4210752);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		super.drawBackground(gui, x, y);
		ResourceHelper.bindResource("/gui/solar.png");
		int lightWidth = light * 3;
		if (light == 15) {
			lightWidth += 2;
		}
		drawImage(gui, 9, 20, 0, 0, 54, 18);
		drawImage(gui, 15, 21, 0, 18, lightWidth, 16);
	}

	@Override
	public int numberOfDataWatchers() {
		return super.numberOfDataWatchers() + 2;
	}

	@Override
	public void initDw() {
		PRIORITY = createDw(DataSerializers.VARINT);
		super.initDw();
		LIGHT = createDw(DataSerializers.VARINT);
		UP_STATE = createDw(DataSerializers.BOOLEAN);
		registerDw(LIGHT, 0);
		registerDw(UP_STATE, false);
	}

	protected boolean isGoingDown() {
		return down;
	}

	public void updateSolarModel() {
		if (getCart().world.isRemote) {
			updateDataForModel();
		}
		panelCoolDown += (maxLight ? 1 : -1);
		if (down && panelCoolDown < 0) {
			panelCoolDown = 0;
		} else if (!down && panelCoolDown > 0) {
			panelCoolDown = 0;
		} else if (Math.abs(panelCoolDown) > 20) {
			panelCoolDown = 0;
			down = !down;
		}
		upState = updatePanels();
		if (!getCart().world.isRemote) {
			updateDw(UP_STATE, upState);
		}
	}

	@Override
	public int numberOfGuiData() {
		return 2;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		updateGuiData(info, 0, (short) (getFuelLevel() & 0xFFFF));
		updateGuiData(info, 1, (short) (getFuelLevel() >> 16 & 0xFFFF));
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			int dataint = data;
			if (dataint < 0) {
				dataint += 65536;
			}
			setFuelLevel((getFuelLevel() & 0xFFFF0000) | dataint);
		} else if (id == 1) {
			setFuelLevel((getFuelLevel() & 0xFFFF) | data << 16);
		}
	}

	protected abstract int getMaxCapacity();

	protected abstract int getGenSpeed();

	protected abstract boolean updatePanels();

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		super.Save(tagCompound, id);
		tagCompound.setInteger(generateNBTName("Fuel", id), getFuelLevel());
		tagCompound.setBoolean(generateNBTName("Up", id), upState);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		super.Load(tagCompound, id);
		setFuelLevel(tagCompound.getInteger(generateNBTName("Fuel", id)));
		upState = tagCompound.getBoolean(generateNBTName("Up", id));
	}
}
