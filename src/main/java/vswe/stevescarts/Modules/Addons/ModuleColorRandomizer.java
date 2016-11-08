package vswe.stevescarts.Modules.Addons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;

import java.util.Random;

public class ModuleColorRandomizer extends ModuleAddon {
	private int[] button;
	private int cooldown;
	private boolean hover;
	private Random random;

	public ModuleColorRandomizer(final MinecartModular cart) {
		super(cart);
		this.button = new int[] { 10, 26, 16, 16 };
		this.random = new Random();
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
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
		ResourceHelper.bindResource("/gui/color_randomizer.png");
		final float[] color = this.getColor();
		GL11.glColor4f(color[0], color[1], color[2], 1.0f);
		this.drawImage(gui, 50, 20, 0, 16, 28, 28);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		if (this.inRect(x, y, this.button)) {
			this.drawImage(gui, 10, 26, 32, 0, 16, 16);
		} else {
			this.drawImage(gui, 10, 26, 16, 0, 16, 16);
		}
		this.drawImage(gui, 10, 26, 0, 0, 16, 16);
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		if (this.inRect(x, y, this.button)) {
			final String randomizeString = Localization.MODULES.ADDONS.BUTTON_RANDOMIZE.translate();
			this.drawStringOnMouseOver(gui, randomizeString, x, y, this.button);
		}
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0 && this.inRect(x, y, this.button)) {
			this.sendPacket(0);
		}
	}

	@Override
	public void activatedByRail(final int x, final int y, final int z, final boolean active) {
		if (active && this.cooldown == 0) {
			this.randomizeColor();
			this.cooldown = 5;
		}
	}

	@Override
	public void update() {
		if (this.cooldown > 0) {
			--this.cooldown;
		}
	}

	private void randomizeColor() {
		final int red = this.random.nextInt(256);
		final int green = this.random.nextInt(256);
		final int blue = this.random.nextInt(256);
		this.setColorVal(0, (byte) red);
		this.setColorVal(1, (byte) green);
		this.setColorVal(2, (byte) blue);
	}

	@Override
	public int numberOfDataWatchers() {
		return 3;
	}

	@Override
	public void initDw() {
		this.addDw(0, 255);
		this.addDw(1, 255);
		this.addDw(2, 255);
	}

	public int numberOfPackets() {
		return 3;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.randomizeColor();
		}
	}

	public int getColorVal(final int i) {
		if (this.isPlaceholder()) {
			return 255;
		}
		int tempVal = this.getDw(i);
		if (tempVal < 0) {
			tempVal += 256;
		}
		return tempVal;
	}

	public void setColorVal(final int id, final int val) {
		this.updateDw(id, val);
	}

	private float getColorComponent(final int i) {
		return this.getColorVal(i) / 255.0f;
	}

	@Override
	public float[] getColor() {
		return new float[] { this.getColorComponent(0), this.getColorComponent(1), this.getColorComponent(2) };
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setByte(this.generateNBTName("Red", id), (byte) this.getColorVal(0));
		tagCompound.setByte(this.generateNBTName("Green", id), (byte) this.getColorVal(1));
		tagCompound.setByte(this.generateNBTName("Blue", id), (byte) this.getColorVal(2));
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.setColorVal(0, tagCompound.getByte(this.generateNBTName("Red", id)));
		this.setColorVal(1, tagCompound.getByte(this.generateNBTName("Green", id)));
		this.setColorVal(2, tagCompound.getByte(this.generateNBTName("Blue", id)));
	}
}
