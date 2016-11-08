package vswe.stevescarts.modules.addons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.entitys.MinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ILeverModule;

public class ModuleBrake extends ModuleAddon implements ILeverModule {
	private int[] startstopRect;
	private int[] turnbackRect;
	private static DataParameter<Boolean> FORGE_STOPPING = createDw(DataSerializers.BOOLEAN);

	public ModuleBrake(final MinecartModular cart) {
		super(cart);
		this.startstopRect = new int[] { 15, 20, 24, 12 };
		this.turnbackRect = new int[] { this.startstopRect[0] + this.startstopRect[2] + 5, this.startstopRect[1], this.startstopRect[2], this.startstopRect[3] };
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public int guiWidth() {
		return 80;
	}

	@Override
	public int guiHeight() {
		return 35;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ADDONS.CONTROL_LEVER.translate(), 8, 6, 4210752);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/lever.png");
		this.drawButton(gui, x, y, this.startstopRect, this.isForceStopping() ? 2 : 1);
		this.drawButton(gui, x, y, this.turnbackRect, 0);
	}

	private void drawButton(final GuiMinecart gui, final int x, final int y, final int[] coords, final int imageID) {
		if (this.inRect(x, y, coords)) {
			this.drawImage(gui, coords, 0, coords[3]);
		} else {
			this.drawImage(gui, coords, 0, 0);
		}
		final int srcY = coords[3] * 2 + imageID * (coords[3] - 2);
		this.drawImage(gui, coords[0] + 1, coords[1] + 1, 0, srcY, coords[2] - 2, coords[3] - 2);
	}

	@Override
	public boolean stopEngines() {
		return this.isForceStopping();
	}

	private boolean isForceStopping() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getBrakeActive();
		}
		return getDw(FORGE_STOPPING);
	}

	private void setForceStopping(final boolean val) {
		this.updateDw(FORGE_STOPPING, val);
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui,
				this.isForceStopping() ? Localization.MODULES.ADDONS.LEVER_START.translate() : Localization.MODULES.ADDONS.LEVER_STOP.translate(), x, y, this.startstopRect);
		this.drawStringOnMouseOver(gui, Localization.MODULES.ADDONS.LEVER_TURN.translate(), x, y, this.turnbackRect);
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0) {
			if (this.inRect(x, y, this.startstopRect)) {
				this.sendPacket(0);
			} else if (this.inRect(x, y, this.turnbackRect)) {
				this.sendPacket(1);
			}
		}
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.setForceStopping(!this.isForceStopping());
		} else if (id == 1) {
			this.turnback();
		}
	}

	@Override
	public int numberOfPackets() {
		return 2;
	}

	@Override
	public float getLeverState() {
		if (this.isForceStopping()) {
			return 0.0f;
		}
		return 1.0f;
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		this.registerDw(FORGE_STOPPING, false);
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setBoolean(this.generateNBTName("ForceStop", id), this.isForceStopping());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.setForceStopping(tagCompound.getBoolean(this.generateNBTName("ForceStop", id)));
	}
}
