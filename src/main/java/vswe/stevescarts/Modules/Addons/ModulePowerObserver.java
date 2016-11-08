package vswe.stevescarts.Modules.Addons;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.Engines.ModuleEngine;

public class ModulePowerObserver extends ModuleAddon {
	private short[] areaData;
	private short[] powerLevel;
	private int currentEngine;

	public ModulePowerObserver(final MinecartModular cart) {
		super(cart);
		this.areaData = new short[4];
		this.powerLevel = new short[4];
		this.currentEngine = -1;
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
	public int guiWidth() {
		return 190;
	}

	@Override
	public int guiHeight() {
		return 150;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
		for (int i = 0; i < 4; ++i) {
			final int[] rect = this.getPowerRect(i);
			this.drawString(gui, this.powerLevel[i] + Localization.MODULES.ADDONS.K.translate(new String[0]), rect, 4210752);
		}
	}

	private boolean removeOnPickup() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		for (int i = 0; i < this.getCart().getEngines().size(); ++i) {
			if (!this.removeOnPickup() || this.currentEngine != i) {
				this.drawEngine(gui, i, this.getEngineRect(i));
			}
		}
		ResourceHelper.bindResource("/gui/observer.png");
		for (int i = 0; i < 4; ++i) {
			int[] rect = this.getAreaRect(i);
			this.drawImage(gui, rect, 18, 22 * i);
			if (this.inRect(x, y, rect)) {
				this.drawImage(gui, rect, 18, 22 * (i + 4));
			}
			int count = 0;
			for (int j = 0; j < this.getCart().getEngines().size(); ++j) {
				if ((this.areaData[i] & 1 << j) != 0x0) {
					this.drawEngine(gui, j, this.getEngineRectInArea(i, count));
					++count;
				}
			}
			ResourceHelper.bindResource("/gui/observer.png");
			rect = this.getPowerRect(i);
			if (this.isAreaActive(i)) {
				this.drawImage(gui, rect, 122, 0);
			} else {
				this.drawImage(gui, rect, 122 + rect[2], 0);
			}
			if (this.inRect(x, y, rect)) {
				this.drawImage(gui, rect, 122 + rect[2] * 2, 0);
			}
		}
		if (this.currentEngine != -1) {
			this.drawEngine(gui, this.currentEngine, this.getEngineRectMouse(x, y + this.getCart().getRealScrollY()));
		}
	}

	private void drawEngine(final GuiMinecart gui, final int id, final int[] rect) {
		final ModuleEngine engine = this.getCart().getEngines().get(id);
		ResourceHelper.bindResourcePath("/atlas/items.png");
		//TODO
		//this.drawImage(gui, engine.getData().getIcon(), rect, 0, 0);
	}

	private int[] getAreaRect(final int id) {
		return new int[] { 10, 40 + 25 * id, 104, 22 };
	}

	private int[] getEngineRect(final int id) {
		return new int[] { 11 + id * 20, 21, 16, 16 };
	}

	private int[] getEngineRectMouse(final int x, final int y) {
		return new int[] { x - 8, y - 8, 16, 16 };
	}

	private int[] getEngineRectInArea(final int areaid, final int number) {
		final int[] area = this.getAreaRect(areaid);
		return new int[] { area[0] + 4 + number * 20, area[1] + 3, 16, 16 };
	}

	private int[] getPowerRect(final int areaid) {
		final int[] area = this.getAreaRect(areaid);
		return new int[] { area[0] + area[2] + 10, area[1] + 2, 35, 18 };
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		for (int i = 0; i < this.getCart().getEngines().size(); ++i) {
			if (!this.removeOnPickup() || this.currentEngine != i) {
				final ModuleEngine engine = this.getCart().getEngines().get(i);
				this.drawStringOnMouseOver(gui, engine.getData().getName() + "\n" + Localization.MODULES.ADDONS.OBSERVER_INSTRUCTION.translate(), x, y, this.getEngineRect(i));
			}
		}
		for (int i = 0; i < 4; ++i) {
			int count = 0;
			for (int j = 0; j < this.getCart().getEngines().size(); ++j) {
				if ((this.areaData[i] & 1 << j) != 0x0) {
					final ModuleEngine engine2 = this.getCart().getEngines().get(j);
					this.drawStringOnMouseOver(gui, engine2.getData().getName() + "\n" + Localization.MODULES.ADDONS.OBSERVER_REMOVE.translate(), x, y, this.getEngineRectInArea(i, count));
					++count;
				}
			}
			if (this.currentEngine != -1) {
				this.drawStringOnMouseOver(gui, Localization.MODULES.ADDONS.OBSERVER_DROP.translate(), x, y, this.getAreaRect(i));
			}
			this.drawStringOnMouseOver(gui, Localization.MODULES.ADDONS.OBSERVER_CHANGE.translate() + "\n" + Localization.MODULES.ADDONS.OBSERVER_CHANGE_10.translate(), x, y, this.getPowerRect(i));
		}
	}

	@Override
	public int numberOfGuiData() {
		return 8;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		for (int i = 0; i < 4; ++i) {
			this.updateGuiData(info, i, this.areaData[i]);
		}
		for (int i = 0; i < 4; ++i) {
			this.updateGuiData(info, i + 4, this.powerLevel[i]);
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id >= 0 && id < 4) {
			this.areaData[id] = data;
		} else if (id >= 4 && id < 8) {
			this.powerLevel[id - 4] = data;
		}
	}

	@Override
	public int numberOfPackets() {
		return 3;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			final int area = data[0];
			final int engine = data[1];
			final short[] areaData = this.areaData;
			final int n = area;
			areaData[n] |= (short) (1 << engine);
		} else if (id == 1) {
			final int area = data[0];
			final int engine = data[1];
			final short[] areaData2 = this.areaData;
			final int n2 = area;
			areaData2[n2] &= (short) ~(1 << engine);
		} else if (id == 2) {
			final int area = data[0];
			final int button = data[1] & 0x1;
			final boolean shift = (data[1] & 0x2) != 0x0;
			int change = (button == 0) ? 1 : -1;
			if (shift) {
				change *= 10;
			}
			short value = this.powerLevel[area];
			value += (short) change;
			if (value < 0) {
				value = 0;
			} else if (value > 999) {
				value = 999;
			}
			this.powerLevel[area] = value;
		}
	}

	@Override
	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button != -1) {
			if (button == 0) {
				for (int i = 0; i < 4; ++i) {
					final int[] rect = this.getAreaRect(i);
					if (this.inRect(x, y, rect)) {
						this.sendPacket(0, new byte[] { (byte) i, (byte) this.currentEngine });
						break;
					}
				}
			}
			this.currentEngine = -1;
		}
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		for (int i = 0; i < 4; ++i) {
			final int[] rect = this.getPowerRect(i);
			if (this.inRect(x, y, rect)) {
				this.sendPacket(2, new byte[] { (byte) i, (byte) (button | (GuiScreen.isShiftKeyDown() ? 2 : 0)) });
				break;
			}
		}
		if (button == 0) {
			for (int i = 0; i < this.getCart().getEngines().size(); ++i) {
				final int[] rect = this.getEngineRect(i);
				if (this.inRect(x, y, rect)) {
					this.currentEngine = i;
					break;
				}
			}
		} else if (button == 1) {
			for (int i = 0; i < 4; ++i) {
				int count = 0;
				for (int j = 0; j < this.getCart().getEngines().size(); ++j) {
					if ((this.areaData[i] & 1 << j) != 0x0) {
						final int[] rect2 = this.getEngineRectInArea(i, count);
						if (this.inRect(x, y, rect2)) {
							this.sendPacket(1, new byte[] { (byte) i, (byte) j });
							break;
						}
						++count;
					}
				}
			}
		}
	}

	public boolean isAreaActive(final int area) {
		int power = 0;
		for (int i = 0; i < this.getCart().getEngines().size(); ++i) {
			final ModuleEngine engine = this.getCart().getEngines().get(i);
			if ((this.areaData[area] & 1 << i) != 0x0) {
				power += engine.getTotalFuel();
			}
		}
		return power > this.powerLevel[area] * 1000;
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		for (int i = 0; i < 4; ++i) {
			tagCompound.setShort(this.generateNBTName("AreaData" + i, id), this.areaData[i]);
			tagCompound.setShort(this.generateNBTName("PowerLevel" + i, id), this.powerLevel[i]);
		}
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		for (int i = 0; i < 4; ++i) {
			this.areaData[i] = tagCompound.getShort(this.generateNBTName("AreaData" + i, id));
			this.powerLevel[i] = tagCompound.getShort(this.generateNBTName("PowerLevel" + i, id));
		}
	}
}
