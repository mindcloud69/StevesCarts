package vswe.stevescarts.modules.addons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.entitys.CartDataSerializers;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;

public class ModuleColorizer extends ModuleAddon {
	private int markerOffsetX;
	private int scrollWidth;
	private int markerMoving;
	private DataParameter<int[]> COLORS;

	public ModuleColorizer(final EntityMinecartModular cart) {
		super(cart);
		this.markerOffsetX = 10;
		this.scrollWidth = 64;
		this.markerMoving = -1;
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
		return 125;
	}

	@Override
	public int guiHeight() {
		return 75;
	}

	private int[] getMovableMarker(final int i) {
		return new int[] { this.markerOffsetX + (int) (this.scrollWidth * (this.getColorVal(i) / 255.0f)) - 2, 17 + i * 20, 4, 13 };
	}

	private int[] getArea(final int i) {
		return new int[] { this.markerOffsetX, 20 + i * 20, this.scrollWidth, 7 };
	}

	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/color.png");
		for (int i = 0; i < 3; ++i) {
			this.drawMarker(gui, x, y, i);
		}
		final float[] color = this.getColor();
		GL11.glColor4f(color[0], color[1], color[2], 1.0f);
		this.drawImage(gui, this.scrollWidth + 25, 29, 4, 7, 28, 28);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		final String[] colorNames = { Localization.MODULES.ADDONS.COLOR_RED.translate(), Localization.MODULES.ADDONS.COLOR_GREEN.translate(),
			Localization.MODULES.ADDONS.COLOR_BLUE.translate() };
		for (int i = 0; i < 3; ++i) {
			this.drawStringOnMouseOver(gui, colorNames[i] + ": " + this.getColorVal(i), x, y, this.getArea(i));
		}
	}

	private void drawMarker(final GuiMinecart gui, final int x, final int y, final int id) {
		final float[] colorArea = new float[3];
		final float[] colorMarker = new float[3];
		for (int i = 0; i < 3; ++i) {
			if (i == id) {
				colorArea[i] = 0.7f;
				colorMarker[i] = 1.0f;
			} else {
				colorArea[i] = 0.2f;
				colorMarker[i] = 0.0f;
			}
		}
		GL11.glColor4f(colorArea[0], colorArea[1], colorArea[2], 1.0f);
		this.drawImage(gui, this.getArea(id), 0, 0);
		GL11.glColor4f(colorMarker[0], colorMarker[1], colorMarker[2], 1.0f);
		this.drawImage(gui, this.getMovableMarker(id), 0, 7);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0) {
			for (int i = 0; i < 3; ++i) {
				if (this.inRect(x, y, this.getMovableMarker(i))) {
					this.markerMoving = i;
				}
			}
		}
	}

	@Override
	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
		if (this.markerMoving != -1) {
			int tempColor = (int) ((x - this.markerOffsetX) / (this.scrollWidth / 255.0f));
			if (tempColor < 0) {
				tempColor = 0;
			} else if (tempColor > 255) {
				tempColor = 255;
			}
			this.sendPacket(this.markerMoving, (byte) tempColor);
		}
		if (button != -1) {
			this.markerMoving = -1;
		}
	}

	@Override
	public int numberOfDataWatchers() {
		return 3;
	}

	@Override
	public void initDw() {
		COLORS = createDw(CartDataSerializers.VARINT);
		registerDw(COLORS, new int[] { 255, 255, 255 });
	}

	@Override
	public int numberOfPackets() {
		return 3;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id >= 0 && id < 3) {
			this.setColorVal(id, data[0]);
		}
	}

	public int getColorVal(final int i) {
		if (this.isPlaceholder()) {
			return 255;
		}
		int tempVal = getDw(COLORS)[i];
		if (tempVal < 0) {
			tempVal += 256;
		}
		return tempVal;
	}

	public void setColorVal(final int id, final int val) {
		int[] colors = getDw(COLORS);
		colors[id] = val;
		updateDw(COLORS, colors);
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
