package vswe.stevescarts.Modules.Addons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.IActivatorModule;

public class ModuleInvisible extends ModuleAddon implements IActivatorModule {
	private int[] buttonRect;

	public ModuleInvisible(final MinecartModular cart) {
		super(cart);
		this.buttonRect = new int[] { 20, 20, 24, 12 };
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
		return 90;
	}

	@Override
	public int guiHeight() {
		return 35;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/invis.png");
		final int imageID = this.isVisible() ? 1 : 0;
		int borderID = 0;
		if (this.inRect(x, y, this.buttonRect)) {
			borderID = 1;
		}
		this.drawImage(gui, this.buttonRect, 0, this.buttonRect[3] * borderID);
		final int srcY = this.buttonRect[3] * 2 + imageID * (this.buttonRect[3] - 2);
		this.drawImage(gui, this.buttonRect[0] + 1, this.buttonRect[1] + 1, 0, srcY, this.buttonRect[2] - 2, this.buttonRect[3] - 2);
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, this.getStateName(), x, y, this.buttonRect);
	}

	@Override
	public void update() {
		super.update();
		if (!this.isVisible() && !this.getCart().hasFuelForModule() && !this.getCart().worldObj.isRemote) {
			this.setIsVisible(true);
		}
	}

	private boolean isVisible() {
		if (this.isPlaceholder()) {
			return !this.getSimInfo().getInvisActive();
		}
		return this.getDw(0) != 0;
	}

	private String getStateName() {
		return Localization.MODULES.ADDONS.INVISIBILITY.translate(this.isVisible() ? "0" : "1");
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0 && this.inRect(x, y, this.buttonRect)) {
			this.sendPacket(0);
		}
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.setIsVisible(!this.isVisible());
		}
	}

	public void setIsVisible(final boolean val) {
		this.updateDw(0, val ? 1 : 0);
	}

	public int numberOfPackets() {
		return 1;
	}

	@Override
	public boolean shouldCartRender() {
		return this.isVisible();
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		this.addDw(0, 1);
	}

	@Override
	public int getConsumption(final boolean isMoving) {
		return this.isVisible() ? super.getConsumption(isMoving) : 3;
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setBoolean(this.generateNBTName("Invis", id), !this.isVisible());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.setIsVisible(!tagCompound.getBoolean(this.generateNBTName("Invis", id)));
	}

	@Override
	public void doActivate(final int id) {
		this.setIsVisible(false);
	}

	@Override
	public void doDeActivate(final int id) {
		this.setIsVisible(true);
	}

	@Override
	public boolean isActive(final int id) {
		return !this.isVisible();
	}
}
