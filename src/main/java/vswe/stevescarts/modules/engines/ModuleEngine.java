package vswe.stevescarts.modules.engines;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

public abstract class ModuleEngine extends ModuleBase {
	private int fuel;
	protected int[] priorityButton;
	private static DataParameter<Integer> PRIORITY = createDw(DataSerializers.VARINT);

	public ModuleEngine(final EntityMinecartModular cart) {
		super(cart);
		this.initPriorityButton();
	}

	protected void initPriorityButton() {
		this.priorityButton = new int[] { 78, 7, 16, 16 };
	}

	@Override
	public void update() {
		super.update();
		this.loadFuel();
	}

	@Override
	public boolean hasFuel(final int comsumption) {
		return this.getFuelLevel() >= comsumption && !this.isDisabled();
	}

	public int getFuelLevel() {
		return this.fuel;
	}

	public void setFuelLevel(final int val) {
		this.fuel = val;
	}

	protected boolean isDisabled() {
		return this.getPriority() >= 3 || this.getPriority() < 0;
	}

	public int getPriority() {
		if (this.isPlaceholder()) {
			return 0;
		}
		int temp = this.getDw(PRIORITY);
		if (temp < 0 || temp > 3) {
			temp = 3;
		}
		return temp;
	}

	private void setPriority(int data) {
		if (data < 0) {
			data = 0;
		} else if (data > 3) {
			data = 3;
		}
		this.updateDw(PRIORITY, data);
	}

	public void consumeFuel(final int comsumption) {
		this.setFuelLevel(this.getFuelLevel() - comsumption);
	}

	protected abstract void loadFuel();

	public void smoke() {
	}

	public abstract int getTotalFuel();

	public abstract float[] getGuiBarColor();

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public int guiWidth() {
		return 100;
	}

	@Override
	public int guiHeight() {
		return 50;
	}

	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/engine.png");
		final int sourceX = 16 * this.getPriority();
		int sourceY = 0;
		if (this.inRect(x, y, this.priorityButton)) {
			sourceY = 16;
		}
		this.drawImage(gui, this.priorityButton, sourceX, sourceY);
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, this.getPriorityText(), x, y, this.priorityButton);
	}

	private String getPriorityText() {
		if (this.isDisabled()) {
			return Localization.MODULES.ENGINES.ENGINE_DISABLED.translate();
		}
		return Localization.MODULES.ENGINES.ENGINE_PRIORITY.translate(String.valueOf(this.getPriority()));
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (this.inRect(x, y, this.priorityButton) && (button == 0 || button == 1)) {
			this.sendPacket(0, (byte) button);
		}
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			int prio = this.getPriority();
			prio += ((data[0] == 0) ? 1 : -1);
			prio %= 4;
			if (prio < 0) {
				prio += 4;
			}
			this.setPriority(prio);
		}
	}

	@Override
	public int numberOfPackets() {
		return 1;
	}

	@Override
	public void initDw() {
		registerDw(PRIORITY, 0);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setByte(this.generateNBTName("Priority", id), (byte) this.getPriority());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.setPriority(tagCompound.getByte(this.generateNBTName("Priority", id)));
	}
}
