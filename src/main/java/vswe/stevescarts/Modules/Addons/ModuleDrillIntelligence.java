package vswe.stevescarts.Modules.Addons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Workers.Tools.ModuleDrill;

public class ModuleDrillIntelligence extends ModuleAddon {
	private ModuleDrill drill;
	private boolean hasHeightController;
	private int guiW;
	private int guiH;
	private short[] isDisabled;
	private boolean clickedState;
	private boolean clicked;
	private int lastId;

	public ModuleDrillIntelligence(final MinecartModular cart) {
		super(cart);
		this.guiW = -1;
		this.guiH = -1;
	}

	@Override
	public void preInit() {
		super.preInit();
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module instanceof ModuleDrill) {
				this.drill = (ModuleDrill) module;
			} else {
				if (!(module instanceof ModuleHeightControl)) {
					continue;
				}
				this.hasHeightController = true;
			}
		}
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

	private int getDrillWidth() {
		if (this.drill == null) {
			return 0;
		}
		return this.drill.getAreaWidth();
	}

	private int getDrillHeight() {
		if (this.drill == null) {
			return 0;
		}
		return this.drill.getAreaHeight() + (this.hasHeightController ? 2 : 0);
	}

	@Override
	public int guiWidth() {
		if (this.guiW == -1) {
			this.guiW = Math.max(15 + this.getDrillWidth() * 10 + 5, 93);
		}
		return this.guiW;
	}

	@Override
	public int guiHeight() {
		if (this.guiH == -1) {
			this.guiH = 20 + this.getDrillHeight() * 10 + 5;
		}
		return this.guiH;
	}

	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/intelligence.png");
		final int w = this.getDrillWidth();
		final int h = this.getDrillHeight();
		for (int i = 0; i < w; ++i) {
			for (int j = 0; j < h; ++j) {
				final int[] rect = this.getSettingRect(i, j);
				int srcX = (!this.hasHeightController || (j != 0 && j != h - 1)) ? 0 : 8;
				int srcY = 0;
				this.drawImage(gui, rect, srcX, srcY);
				if (this.isActive(j * w + i)) {
					srcX = (this.isLocked(j * w + i) ? 8 : 0);
					srcY = 8;
					this.drawImage(gui, rect, srcX, srcY);
				}
				srcX = (this.inRect(x, y, rect) ? 8 : 0);
				srcY = 16;
				this.drawImage(gui, rect, srcX, srcY);
			}
		}
	}

	private void initDisabledData() {
		if (this.isDisabled == null) {
			this.isDisabled = new short[(int) Math.ceil(this.getDrillWidth() * this.getDrillHeight() / 16.0f)];
		}
	}

	public boolean isActive(int x, int y, final int offset, final boolean direction) {
		y = this.getDrillHeight() - 1 - y;
		if (this.hasHeightController) {
			y -= offset;
		}
		if (!direction) {
			x = this.getDrillWidth() - 1 - x;
		}
		return this.isActive(y * this.getDrillWidth() + x);
	}

	private boolean isActive(final int id) {
		this.initDisabledData();
		return this.isLocked(id) || (this.isDisabled[id / 16] & 1 << id % 16) == 0x0;
	}

	private boolean isLocked(final int id) {
		final int x = id % this.getDrillWidth();
		final int y = id / this.getDrillWidth();
		return (y == this.getDrillHeight() - 1 || (this.hasHeightController && y == this.getDrillHeight() - 2)) && x == (this.getDrillWidth() - 1) / 2;
	}

	private void swapActiveness(final int id) {
		this.initDisabledData();
		if (!this.isLocked(id)) {
			final short[] isDisabled = this.isDisabled;
			final int n = id / 16;
			isDisabled[n] ^= (short) (1 << id % 16);
		}
	}

	private int[] getSettingRect(final int x, final int y) {
		return new int[] { 15 + x * 10, 20 + y * 10, 8, 8 };
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		final int w = this.getDrillWidth();
		final int h = this.getDrillHeight();
		for (int i = 0; i < w; ++i) {
			for (int j = 0; j < h; ++j) {
				final int[] rect = this.getSettingRect(i, j);
				final String str = this.isLocked(j * w + i) ? Localization.MODULES.ADDONS.LOCKED.translate()
				                                            : (Localization.MODULES.ADDONS.CHANGE_INTELLIGENCE.translate() + "\n" + Localization.MODULES.ADDONS.CURRENT_INTELLIGENCE.translate(
					                                            this.isActive(j * w + i) ? "0" : "1"));
				this.drawStringOnMouseOver(gui, str, x, y, rect);
			}
		}
	}

	@Override
	public int numberOfGuiData() {
		final int maxDrillWidth = 9;
		final int maxDrillHeight = 9;
		return (int) Math.ceil(maxDrillWidth * (maxDrillHeight + 2) / 16.0f);
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		if (this.isDisabled != null) {
			for (int i = 0; i < this.isDisabled.length; ++i) {
				this.updateGuiData(info, i, this.isDisabled[i]);
			}
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		this.initDisabledData();
		if (id >= 0 && id < this.isDisabled.length) {
			this.isDisabled[id] = data;
		}
	}

	public int numberOfPackets() {
		return 1;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.swapActiveness(data[0]);
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		this.initDisabledData();
		for (int i = 0; i < this.isDisabled.length; ++i) {
			tagCompound.setShort(this.generateNBTName("isDisabled" + i, id), this.isDisabled[i]);
		}
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.initDisabledData();
		for (int i = 0; i < this.isDisabled.length; ++i) {
			this.isDisabled[i] = tagCompound.getShort(this.generateNBTName("isDisabled" + i, id));
		}
	}

	@Override
	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == -1 && this.clicked) {
			final int w = this.getDrillWidth();
			final int h = this.getDrillHeight();
			for (int i = 0; i < w; ++i) {
				for (int j = 0; j < h; ++j) {
					if (this.lastId != j * w + i) {
						if (this.isActive(j * w + i) == this.clickedState) {
							final int[] rect = this.getSettingRect(i, j);
							if (this.inRect(x, y, rect)) {
								this.lastId = j * w + i;
								this.sendPacket(0, (byte) (j * w + i));
								return;
							}
						}
					}
				}
			}
		}
		if (button == 0) {
			this.clicked = false;
		}
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0) {
			final int w = this.getDrillWidth();
			final int h = this.getDrillHeight();
			for (int i = 0; i < w; ++i) {
				for (int j = 0; j < h; ++j) {
					final int[] rect = this.getSettingRect(i, j);
					if (this.inRect(x, y, rect)) {
						this.clicked = true;
						this.clickedState = this.isActive(j * w + i);
						this.lastId = j * w + i;
						this.sendPacket(0, (byte) (j * w + i));
						return;
					}
				}
			}
		}
	}
}
