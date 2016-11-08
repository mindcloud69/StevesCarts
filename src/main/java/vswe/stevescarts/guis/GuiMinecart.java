package vswe.stevescarts.guis;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.helpers.ModuleCountPair;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class GuiMinecart extends GuiBase {
	private static ResourceLocation textureLeft;
	private static ResourceLocation textureRight;
	public static ResourceLocation moduleTexture;
	private boolean isScrolling;
	private int[] scrollBox;
	private EntityMinecartModular cart;

	public GuiMinecart(final InventoryPlayer invPlayer, final EntityMinecartModular cart) {
		super(cart.getCon(invPlayer));
		this.scrollBox = new int[] { 450, 15, 18, 225 };
		this.setup(cart);
	}

	protected void setup(final EntityMinecartModular cart) {
		this.cart = cart;
		this.setXSize(478);
		this.setYSize(256);
	}

	@Override
	public void drawGuiForeground(final int x, final int y) {
		GL11.glDisable(2896);
		if (this.cart.getModules() != null) {
			final ModuleBase thief = this.cart.getInterfaceThief();
			if (thief != null) {
				this.drawModuleForeground(thief);
				this.drawModuleMouseOver(thief, x, y);
			} else {
				for (final ModuleBase module : this.cart.getModules()) {
					this.drawModuleForeground(module);
				}
				this.renderModuleListText(x, y);
				for (final ModuleBase module : this.cart.getModules()) {
					this.drawModuleMouseOver(module, x, y);
				}
				this.renderModuleListMouseOver(x, y);
			}
		}
		GL11.glEnable(2896);
	}

	@Override
	public void drawGuiBackground(final float f, final int x, final int y) {
		GL11.glDisable(2896);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		final int j = this.getGuiLeft();
		final int k = this.getGuiTop();
		ResourceHelper.bindResource(GuiMinecart.textureLeft);
		this.drawTexturedModalRect(j, k, 0, 0, 256, this.ySize);
		ResourceHelper.bindResource(GuiMinecart.textureRight);
		this.drawTexturedModalRect(j + 256, k, 0, 0, this.xSize - 256, this.ySize);
		final ModuleBase thief = this.cart.getInterfaceThief();
		if (thief != null) {
			this.drawModuleSlots(thief);
			this.drawModuleBackground(thief, x, y);
			this.drawModuleBackgroundItems(thief, x, y);
			for (final ModuleBase module : this.cart.getModules()) {
				if (module.hasGui() && module.hasSlots()) {
					final ArrayList<SlotBase> slotsList = module.getSlots();
					for (final SlotBase slot : slotsList) {
						this.resetSlot(slot);
					}
				}
			}
		} else if (this.cart.getModules() != null) {
			this.drawTexturedModalRect(j + this.scrollBox[0], k + this.scrollBox[1], 222, 24, this.scrollBox[2], this.scrollBox[3]);
			this.drawTexturedModalRect(j + this.scrollBox[0] + 2, k + this.scrollBox[1] + 2 + this.cart.getScrollY(), 240, 26 + (this.cart.canScrollModules ? 0 : 25), 14, 25);
			for (final ModuleBase module : this.cart.getModules()) {
				this.drawModuleSlots(module);
			}
			for (final ModuleBase module : this.cart.getModules()) {
				this.drawModuleBackground(module, x, y);
			}
			this.renderModuleList(x, y);
			for (final ModuleBase module : this.cart.getModules()) {
				this.drawModuleBackgroundItems(module, x, y);
			}
		}
		GL11.glEnable(2896);
	}

	private void renderModuleList(int x, int y) {
		x -= this.getGuiLeft();
		y -= this.getGuiTop();
		final ArrayList<ModuleCountPair> moduleCounts = this.cart.getModuleCounts();
		ResourceHelper.bindResource(GuiMinecart.moduleTexture);
		GL11.glEnable(3042);
		for (int i = 0; i < moduleCounts.size(); ++i) {
			final ModuleCountPair count = moduleCounts.get(i);
			final float alpha = this.inRect(x, y, this.getModuleDisplayX(i), this.getModuleDisplayY(i), 16, 16) ? 1.0f : 0.5f;
			GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
			this.drawModuleIcon(count.getData(), this.getGuiLeft() + this.getModuleDisplayX(i), this.getGuiTop() + this.getModuleDisplayY(i), 1.0f, 1.0f, 0.0f, 0.0f);
		}
		GL11.glDisable(3042);
	}

	private void renderModuleListText(int x, int y) {
		x -= this.getGuiLeft();
		y -= this.getGuiTop();
		final ArrayList<ModuleCountPair> moduleCounts = this.cart.getModuleCounts();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.getFontRenderer().drawString(this.cart.getCartName(), 5, 172, 4210752);
		GL11.glEnable(3042);
		for (int i = 0; i < moduleCounts.size(); ++i) {
			final ModuleCountPair count = moduleCounts.get(i);
			if (count.getCount() != 1) {
				final int alpha = (int) ((this.inRect(x, y, this.getModuleDisplayX(i), this.getModuleDisplayY(i), 16, 16) ? 1.0f : 0.75f) * 256.0f);
				final String str = String.valueOf(count.getCount());
				this.getFontRenderer().drawStringWithShadow(str, this.getModuleDisplayX(i) + 16 - this.getFontRenderer().getStringWidth(str), this.getModuleDisplayY(i) + 8, 0xFFFFFF | alpha << 24);
			}
		}
		GL11.glDisable(3042);
	}

	private void renderModuleListMouseOver(int x, int y) {
		x -= this.getGuiLeft();
		y -= this.getGuiTop();
		final ArrayList<ModuleCountPair> moduleCounts = this.cart.getModuleCounts();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		for (int i = 0; i < moduleCounts.size(); ++i) {
			final ModuleCountPair count = moduleCounts.get(i);
			if (this.inRect(x, y, this.getModuleDisplayX(i), this.getModuleDisplayY(i), 16, 16)) {
				this.drawMouseOver(count.toString(), x, y);
			}
		}
	}

	private int getModuleDisplayX(final int id) {
		return id % 8 * 18 + 7;
	}

	private int getModuleDisplayY(final int id) {
		return id / 8 * 18 + 182;
	}

	@Override
	public void mouseClick(final int x, final int y, final int button) {
		super.mouseClick(x, y, button);
		final ModuleBase thief = this.cart.getInterfaceThief();
		if (thief != null) {
			this.handleModuleMouseClicked(thief, x, y, button);
		} else if (this.cart.getModules() != null) {
			if (this.inRect(x - this.getGuiLeft(), y - this.getGuiTop(), this.scrollBox[0], this.scrollBox[1], this.scrollBox[2], this.scrollBox[3])) {
				this.isScrolling = true;
			}
			for (final ModuleBase module : this.cart.getModules()) {
				this.handleModuleMouseClicked(module, x, y, button);
			}
		}
	}

	protected boolean inRect(final int x, final int y, final int x1, final int y1, final int sizeX, final int sizeY) {
		return x >= x1 && x <= x1 + sizeX && y >= y1 && y <= y1 + sizeY;
	}

	@Override
	public void mouseMoved(final int x, final int y, final int button) {
		super.mouseMoved(x, y, button);
		if (this.isScrolling) {
			int temp = y - this.getGuiTop() - 12 - (this.scrollBox[1] + 2);
			if (temp < 0) {
				temp = 0;
			} else if (temp > 198) {
				temp = 198;
			}
			this.cart.setScrollY(temp);
		}
		if (button != -1) {
			this.isScrolling = false;
		}
		if (this.cart.getModules() != null) {
			final ModuleBase thief = this.cart.getInterfaceThief();
			if (thief != null) {
				this.handleModuleMouseMoved(thief, x, y, button);
			} else {
				for (final ModuleBase module : this.cart.getModules()) {
					this.handleModuleMouseMoved(module, x, y, button);
				}
			}
		}
	}

	@Override
	public void keyPress(final char character, final int extraInformation) {
		super.keyPress(character, extraInformation);
		if (this.cart.getModules() != null) {
			final ModuleBase thief = this.cart.getInterfaceThief();
			if (thief != null) {
				this.handleModuleKeyPress(thief, character, extraInformation);
			} else {
				for (final ModuleBase module : this.cart.getModules()) {
					this.handleModuleKeyPress(module, character, extraInformation);
				}
			}
		}
	}

	@Override
	public boolean disableStandardKeyFunctionality() {
		if (this.cart.getModules() != null) {
			final ModuleBase thief = this.cart.getInterfaceThief();
			if (thief != null) {
				return thief.disableStandardKeyFunctionality();
			}
			for (final ModuleBase module : this.cart.getModules()) {
				if (module.disableStandardKeyFunctionality()) {
					return true;
				}
			}
		}
		return false;
	}

	private void drawModuleForeground(final ModuleBase module) {
		if (module.hasGui()) {
			module.drawForeground(this);
			if (module.useButtons()) {
				module.drawButtonText(this);
			}
		}
	}

	private void drawModuleMouseOver(final ModuleBase module, final int x, final int y) {
		if (module.hasGui()) {
			module.drawMouseOver(this, x - this.getGuiLeft() - module.getX(), y - this.getGuiTop() - module.getY());
			if (module.useButtons()) {
				module.drawButtonOverlays(this, x - this.getGuiLeft() - module.getX(), y - this.getGuiTop() - module.getY());
			}
		}
	}

	private void drawModuleSlots(final ModuleBase module) {
		if (module.hasGui() && module.hasSlots()) {
			final ArrayList<SlotBase> slotsList = module.getSlots();
			for (final SlotBase slot : slotsList) {
				final int[] rect = { slot.getX() + 1, slot.getY() + 1, 16, 16 };
				module.handleScroll(rect);
				final boolean drawAll = rect[3] == 16;
				if (drawAll) {
					slot.xDisplayPosition = slot.getX() + module.getX() + 1;
					slot.yDisplayPosition = slot.getY() + module.getY() + 1 - this.cart.getRealScrollY();
				} else {
					this.resetSlot(slot);
				}
				module.drawImage(this, slot.getX(), slot.getY(), this.xSize - 256, 0, 18, 18);
				if (!drawAll) {
					module.drawImage(this, slot.getX() + 1, slot.getY() + 1, this.xSize - 256 + 18, 1, 16, 16);
				}
			}
		}
	}

	private void resetSlot(final SlotBase slot) {
		slot.xDisplayPosition = -9001;
		slot.yDisplayPosition = -9001;
	}

	private void drawModuleBackground(final ModuleBase module, final int x, final int y) {
		if (module.hasGui()) {
			module.drawBackground(this, x - this.getGuiLeft() - module.getX(), y - this.getGuiTop() - module.getY());
			if (module.useButtons()) {
				module.drawButtons(this, x - this.getGuiLeft() - module.getX(), y - this.getGuiTop() - module.getY());
			}
		}
	}

	private void drawModuleBackgroundItems(final ModuleBase module, final int x, final int y) {
		if (module.hasGui()) {
			module.drawBackgroundItems(this, x - this.getGuiLeft() - module.getX(), y - this.getGuiTop() - module.getY());
		}
	}

	private void handleModuleMouseClicked(final ModuleBase module, final int x, final int y, final int button) {
		module.mouseClicked(this, x - this.getGuiLeft() - module.getX(), y - this.getGuiTop() - module.getY(), button);
		if (module.useButtons()) {
			module.mouseClickedButton(this, x - this.getGuiLeft() - module.getX(), y - this.getGuiTop() - module.getY(), button);
		}
	}

	private void handleModuleMouseMoved(final ModuleBase module, final int x, final int y, final int button) {
		module.mouseMovedOrUp(this, x - this.getGuiLeft() - module.getX(), y - this.getGuiTop() - module.getY(), button);
	}

	private void handleModuleKeyPress(final ModuleBase module, final char character, final int extraInformation) {
		module.keyPress(this, character, extraInformation);
	}

	static {
		GuiMinecart.textureLeft = ResourceHelper.getResource("/gui/guiBase1.png");
		GuiMinecart.textureRight = ResourceHelper.getResource("/gui/guiBase2.png");
		GuiMinecart.moduleTexture = ResourceHelper.getResourceFromPath("/atlas/items.png");
	}
}
