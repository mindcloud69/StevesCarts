package vswe.stevescarts.modules.addons;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.HeightControlOre;
import vswe.stevescarts.helpers.ResourceHelper;

public class ModuleHeightControl extends ModuleAddon {
	private int levelNumberBoxX;
	private int levelNumberBoxY;
	private int[] arrowUp;
	private int[] arrowMiddle;
	private int[] arrowDown;
	private int oreMapX;
	private int oreMapY;
	private DataParameter<Integer> Y_TARGET = createDw(DataSerializers.VARINT);

	public ModuleHeightControl(final EntityMinecartModular cart) {
		super(cart);
		this.levelNumberBoxX = 8;
		this.levelNumberBoxY = 18;
		this.arrowUp = new int[] { 9, 36, 17, 9 };
		this.arrowMiddle = new int[] { 9, 46, 17, 6 };
		this.arrowDown = new int[] { 9, 53, 17, 9 };
		this.oreMapX = 40;
		this.oreMapY = 18;
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
		return Math.max(100, this.oreMapX + 5 + HeightControlOre.ores.size() * 4);
	}

	@Override
	public int guiHeight() {
		return 65;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
		final String s = String.valueOf(this.getYTarget());
		int x = this.levelNumberBoxX + 6;
		int color = 16777215;
		if (this.getYTarget() >= 100) {
			x -= 4;
		} else if (this.getYTarget() < 10) {
			x += 3;
			if (this.getYTarget() < 5) {
				color = 16711680;
			}
		}
		this.drawString(gui, s, x, this.levelNumberBoxY + 5, color);
	}

	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/heightcontrol.png");
		this.drawImage(gui, this.levelNumberBoxX, this.levelNumberBoxY, 4, 36, 21, 15);
		this.drawImage(gui, this.arrowUp, 4, 12);
		this.drawImage(gui, this.arrowMiddle, 4, 21);
		this.drawImage(gui, this.arrowDown, 4, 27);
		for (int i = 0; i < HeightControlOre.ores.size(); ++i) {
			final HeightControlOre ore = HeightControlOre.ores.get(i);
			for (int j = 0; j < 11; ++j) {
				final int altitude = this.getYTarget() - j + 5;
				final boolean empty = ore.spanLowest > altitude || altitude > ore.spanHighest;
				final boolean high = ore.bestLowest <= altitude && altitude <= ore.bestHighest;
				int srcY;
				int srcX;
				if (empty) {
					srcY = 0;
					srcX = 0;
				} else {
					srcX = ore.srcX;
					srcY = ore.srcY;
					if (high) {
						srcY += 4;
					}
				}
				this.drawImage(gui, this.oreMapX + i * 4, this.oreMapY + j * 4, srcX, srcY, 4, 4);
			}
		}
		if (this.getYTarget() != (int) this.getCart().posY) {
			this.drawMarker(gui, 5, false);
		}
		final int pos = this.getYTarget() + 5 - (int) this.getCart().posY;
		if (pos >= 0 && pos < 11) {
			this.drawMarker(gui, pos, true);
		}
	}

	private void drawMarker(final GuiMinecart gui, final int pos, final boolean isTargetLevel) {
		final int srcX = 4;
		final int srcY = isTargetLevel ? 6 : 0;
		this.drawImage(gui, this.oreMapX - 1, this.oreMapY + pos * 4 - 1, srcX, srcY, 1, 6);
		for (int i = 0; i < HeightControlOre.ores.size(); ++i) {
			this.drawImage(gui, this.oreMapX + i * 4, this.oreMapY + pos * 4 - 1, srcX + 1, srcY, 4, 6);
		}
		this.drawImage(gui, this.oreMapX + HeightControlOre.ores.size() * 4, this.oreMapY + pos * 4 - 1, srcX + 5, srcY, 1, 6);
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0) {
			byte packetData = 0;
			if (this.inRect(x, y, this.arrowMiddle)) {
				packetData |= 0x1;
			} else {
				if (!this.inRect(x, y, this.arrowUp)) {
					if (!this.inRect(x, y, this.arrowDown)) {
						return;
					}
					packetData |= 0x2;
				}
				if (GuiScreen.isShiftKeyDown()) {
					packetData |= 0x4;
				}
			}
			this.sendPacket(0, packetData);
		}
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			final byte info = data[0];
			if ((info & 0x1) != 0x0) {
				this.setYTarget((int) this.getCart().posY);
			} else {
				int mult;
				if ((info & 0x2) == 0x0) {
					mult = 1;
				} else {
					mult = -1;
				}
				int dif;
				if ((info & 0x4) == 0x0) {
					dif = 1;
				} else {
					dif = 10;
				}
				int targetY = this.getYTarget();
				targetY += mult * dif;
				if (targetY < 0) {
					targetY = 0;
				} else if (targetY > 255) {
					targetY = 255;
				}
				this.setYTarget(targetY);
			}
		}
	}

	@Override
	public int numberOfPackets() {
		return 1;
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		registerDw(Y_TARGET, (int) this.getCart().posY);
	}

	public void setYTarget(final int val) {
		this.updateDw(Y_TARGET, val);
	}

	@Override
	public int getYTarget() {
		if (this.isPlaceholder()) {
			return 64;
		}
		int data = this.getDw(Y_TARGET);
		if (data < 0) {
			data += 256;
		}
		return data;
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setShort(this.generateNBTName("Height", id), (short) this.getYTarget());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.setYTarget(tagCompound.getShort(this.generateNBTName("Height", id)));
	}
}
