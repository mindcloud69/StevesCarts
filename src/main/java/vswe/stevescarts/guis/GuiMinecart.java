package vswe.stevescarts.guis;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.helpers.ModuleCountPair;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

import java.util.ArrayList;

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
		scrollBox = new int[] { 450, 15, 18, 225 };
		setup(cart);
	}

	protected void setup(final EntityMinecartModular cart) {
		this.cart = cart;
		setXSize(478);
		setYSize(256);
	}

	@Override
	public void drawGuiForeground(final int x, final int y) {
		GL11.glDisable(2896);
		if (cart.getModules() != null) {
			final ModuleBase thief = cart.getInterfaceThief();
			if (thief != null) {
				drawModuleForeground(thief);
				drawModuleMouseOver(thief, x, y);
			} else {
				for (final ModuleBase module : cart.getModules()) {
					drawModuleForeground(module);
				}
				renderModuleListText(x, y);
				for (final ModuleBase module : cart.getModules()) {
					drawModuleMouseOver(module, x, y);
				}
				renderModuleListMouseOver(x, y);
			}
		}
		GL11.glEnable(2896);
	}

	@Override
	public void drawGuiBackground(final float f, final int x, final int y) {
		GL11.glDisable(2896);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		final int j = getGuiLeft();
		final int k = getGuiTop();
		ResourceHelper.bindResource(GuiMinecart.textureLeft);
		drawTexturedModalRect(j, k, 0, 0, 256, ySize);
		ResourceHelper.bindResource(GuiMinecart.textureRight);
		drawTexturedModalRect(j + 256, k, 0, 0, xSize - 256, ySize);
		final ModuleBase thief = cart.getInterfaceThief();
		if (thief != null) {
			drawModuleSlots(thief);
			drawModuleBackground(thief, x, y);
			drawModuleBackgroundItems(thief, x, y);
			for (final ModuleBase module : cart.getModules()) {
				if (module.hasGui() && module.hasSlots()) {
					final ArrayList<SlotBase> slotsList = module.getSlots();
					for (final SlotBase slot : slotsList) {
						resetSlot(slot);
					}
				}
			}
		} else if (cart.getModules() != null) {
			drawTexturedModalRect(j + scrollBox[0], k + scrollBox[1], 222, 24, scrollBox[2], scrollBox[3]);
			drawTexturedModalRect(j + scrollBox[0] + 2, k + scrollBox[1] + 2 + cart.getScrollY(), 240, 26 + (cart.canScrollModules ? 0 : 25), 14, 25);
			for (final ModuleBase module : cart.getModules()) {
				drawModuleSlots(module);
			}
			for (final ModuleBase module : cart.getModules()) {
				drawModuleBackground(module, x, y);
			}
			renderModuleList(x, y);
			for (final ModuleBase module : cart.getModules()) {
				drawModuleBackgroundItems(module, x, y);
			}
		}
		GL11.glEnable(2896);
	}

	private void renderModuleList(int x, int y) {
		x -= getGuiLeft();
		y -= getGuiTop();
		final ArrayList<ModuleCountPair> moduleCounts = cart.getModuleCounts();
		GL11.glEnable(3042);
		for (int i = 0; i < moduleCounts.size(); ++i) {
			final ModuleCountPair count = moduleCounts.get(i);
			final float alpha = inRect(x, y, getModuleDisplayX(i), getModuleDisplayY(i), 16, 16) ? 1.0f : 0.5f;
			GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
			drawModuleIcon(count.getData(), getGuiLeft() + getModuleDisplayX(i), getGuiTop() + getModuleDisplayY(i), 1.0f, 1.0f, 0.0f, 0.0f);
		}
		GL11.glDisable(3042);
	}

	private void renderModuleListText(int x, int y) {
		x -= getGuiLeft();
		y -= getGuiTop();
		final ArrayList<ModuleCountPair> moduleCounts = cart.getModuleCounts();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		getFontRenderer().drawString(cart.getCartName(), 5, 172, 4210752);
		GL11.glEnable(3042);
		for (int i = 0; i < moduleCounts.size(); ++i) {
			final ModuleCountPair count = moduleCounts.get(i);
			if (count.getCount() != 1) {
				final int alpha = (int) ((inRect(x, y, getModuleDisplayX(i), getModuleDisplayY(i), 16, 16) ? 1.0f : 0.75f) * 256.0f);
				final String str = String.valueOf(count.getCount());
				getFontRenderer().drawStringWithShadow(str, getModuleDisplayX(i) + 16 - getFontRenderer().getStringWidth(str), getModuleDisplayY(i) + 8, 0xFFFFFF | alpha << 24);
			}
		}
		GL11.glDisable(3042);
	}

	private void renderModuleListMouseOver(int x, int y) {
		x -= getGuiLeft();
		y -= getGuiTop();
		final ArrayList<ModuleCountPair> moduleCounts = cart.getModuleCounts();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		for (int i = 0; i < moduleCounts.size(); ++i) {
			final ModuleCountPair count = moduleCounts.get(i);
			if (inRect(x, y, getModuleDisplayX(i), getModuleDisplayY(i), 16, 16)) {
				drawMouseOver(count.toString(), x, y);
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
		final ModuleBase thief = cart.getInterfaceThief();
		if (thief != null) {
			handleModuleMouseClicked(thief, x, y, button);
		} else if (cart.getModules() != null) {
			if (inRect(x - getGuiLeft(), y - getGuiTop(), scrollBox[0], scrollBox[1], scrollBox[2], scrollBox[3])) {
				isScrolling = true;
			}
			for (final ModuleBase module : cart.getModules()) {
				handleModuleMouseClicked(module, x, y, button);
			}
		}
	}

	protected boolean inRect(final int x, final int y, final int x1, final int y1, final int sizeX, final int sizeY) {
		return x >= x1 && x <= x1 + sizeX && y >= y1 && y <= y1 + sizeY;
	}

	@Override
	public void mouseMoved(final int x, final int y, final int button) {
		super.mouseMoved(x, y, button);
		if (isScrolling) {
			int temp = y - getGuiTop() - 12 - (scrollBox[1] + 2);
			if (temp < 0) {
				temp = 0;
			} else if (temp > 198) {
				temp = 198;
			}
			cart.setScrollY(temp);
		}
		if (button != -1) {
			isScrolling = false;
		}
		if (cart.getModules() != null) {
			final ModuleBase thief = cart.getInterfaceThief();
			if (thief != null) {
				handleModuleMouseMoved(thief, x, y, button);
			} else {
				for (final ModuleBase module : cart.getModules()) {
					handleModuleMouseMoved(module, x, y, button);
				}
			}
		}
	}

	@Override
	public void keyPress(final char character, final int extraInformation) {
		super.keyPress(character, extraInformation);
		if (cart.getModules() != null) {
			final ModuleBase thief = cart.getInterfaceThief();
			if (thief != null) {
				handleModuleKeyPress(thief, character, extraInformation);
			} else {
				for (final ModuleBase module : cart.getModules()) {
					handleModuleKeyPress(module, character, extraInformation);
				}
			}
		}
	}

	@Override
	public boolean disableStandardKeyFunctionality() {
		if (cart.getModules() != null) {
			final ModuleBase thief = cart.getInterfaceThief();
			if (thief != null) {
				return thief.disableStandardKeyFunctionality();
			}
			for (final ModuleBase module : cart.getModules()) {
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
			module.drawMouseOver(this, x - getGuiLeft() - module.getX(), y - getGuiTop() - module.getY());
			if (module.useButtons()) {
				module.drawButtonOverlays(this, x - getGuiLeft() - module.getX(), y - getGuiTop() - module.getY());
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
					slot.xPos = slot.getX() + module.getX() + 1;
					slot.yPos = slot.getY() + module.getY() + 1 - cart.getRealScrollY();
				} else {
					resetSlot(slot);
				}
				module.drawImage(this, slot.getX(), slot.getY(), xSize - 256, 0, 18, 18);
				if (!drawAll) {
					module.drawImage(this, slot.getX() + 1, slot.getY() + 1, xSize - 256 + 18, 1, 16, 16);
				}
			}
		}
	}

	private void resetSlot(final SlotBase slot) {
		slot.xPos = -9001;
		slot.yPos = -9001;
	}

	private void drawModuleBackground(final ModuleBase module, final int x, final int y) {
		if (module.hasGui()) {
			module.drawBackground(this, x - getGuiLeft() - module.getX(), y - getGuiTop() - module.getY());
			if (module.useButtons()) {
				module.drawButtons(this, x - getGuiLeft() - module.getX(), y - getGuiTop() - module.getY());
			}
		}
	}

	private void drawModuleBackgroundItems(final ModuleBase module, final int x, final int y) {
		if (module.hasGui()) {
			module.drawBackgroundItems(this, x - getGuiLeft() - module.getX(), y - getGuiTop() - module.getY());
		}
	}

	private void handleModuleMouseClicked(final ModuleBase module, final int x, final int y, final int button) {
		module.mouseClicked(this, x - getGuiLeft() - module.getX(), y - getGuiTop() - module.getY(), button);
		if (module.useButtons()) {
			module.mouseClickedButton(this, x - getGuiLeft() - module.getX(), y - getGuiTop() - module.getY(), button);
		}
	}

	private void handleModuleMouseMoved(final ModuleBase module, final int x, final int y, final int button) {
		module.mouseMovedOrUp(this, x - getGuiLeft() - module.getX(), y - getGuiTop() - module.getY(), button);
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
