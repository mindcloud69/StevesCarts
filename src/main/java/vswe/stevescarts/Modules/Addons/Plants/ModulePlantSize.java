package vswe.stevescarts.Modules.Addons.Plants;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.Addons.ModuleAddon;

public class ModulePlantSize extends ModuleAddon {
	private int size;
	private int[] boxrect;

	public ModulePlantSize(final MinecartModular cart) {
		super(cart);
		this.size = 1;
		this.boxrect = new int[] { 10, 18, 44, 44 };
	}

	public int getSize() {
		return this.size;
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
		return 70;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ADDONS.PLANTER_RANGE.translate(), 8, 6, 4210752);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/plantsize.png");
		final int srcX = (this.size - 1) % 5 * 44;
		final int srcY = ((this.size - 1) / 5 + 1) * 44;
		this.drawImage(gui, this.boxrect, srcX, srcY);
		if (this.inRect(x, y, this.boxrect)) {
			this.drawImage(gui, this.boxrect, 0, 0);
		}
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, Localization.MODULES.ADDONS.SAPLING_AMOUNT.translate() + ": " + this.size + "x" + this.size, x, y, this.boxrect);
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if ((button == 0 || button == 1) && this.inRect(x, y, this.boxrect)) {
			this.sendPacket(0, (byte) button);
		}
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			if (data[0] == 1) {
				--this.size;
				if (this.size < 1) {
					this.size = 7;
				}
			} else {
				++this.size;
				if (this.size > 7) {
					this.size = 1;
				}
			}
		}
	}

	public int numberOfPackets() {
		return 1;
	}

	@Override
	public int numberOfGuiData() {
		return 1;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		this.updateGuiData(info, 0, (short) this.size);
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.size = data;
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setByte(this.generateNBTName("size", id), (byte) this.size);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.size = tagCompound.getByte(this.generateNBTName("size", id));
	}
}
