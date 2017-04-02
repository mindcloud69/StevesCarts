package vswe.stevescarts.helpers;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.guis.GuiDetector;
import vswe.stevescarts.modules.data.ModuleData;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class DropDownMenu {
	public static final int SCROLL_HEIGHT = 170;
	public static final int SCROLL_HEADER_HEIGHT = 14;
	public static final int SCROLL_TOP_MARGIN = 20;
	public static final int TAB_COUNT = 3;
	private int moduleScroll;
	private int index;
	private boolean forceGoUp;

	public DropDownMenu(final int index) {
		this.index = index;
		this.moduleScroll = 0;
	}

	public static void update(final GuiDetector gui, final int x, final int y, final ArrayList<DropDownMenu> menus) {
		if (gui.currentObject == null) {
			for (final DropDownMenu menu : menus) {
				if (gui.inRect(x, y, menu.getHeaderRect())) {
					menu.forceGoUp = false;
					menu.update(true);
					for (final DropDownMenu menu2 : menus) {
						if (!menu.equals(menu2)) {
							menu2.forceGoUp = true;
							menu2.update(false);
						}
					}
					return;
				}
			}
			for (final DropDownMenu menu : menus) {
				menu.update(gui.inRect(x, y, menu.getMainRect()));
			}
		} else {
			for (final DropDownMenu menu : menus) {
				menu.update(false);
			}
		}
	}

	private void update(final boolean hasFocus) {
		if (!this.forceGoUp && hasFocus) {
			if (this.moduleScroll < 156) {
				this.moduleScroll += 10;
				if (this.moduleScroll > 156) {
					this.moduleScroll = 156;
				}
			}
		} else if (this.moduleScroll > 0) {
			this.moduleScroll -= 25;
			if (this.moduleScroll <= 0) {
				this.moduleScroll = 0;
				this.forceGoUp = false;
			}
		}
	}

	public void drawMain(final GuiDetector gui, final int x, final int y) {
		ResourceHelper.bindResource(GuiDetector.dropdownTexture);
		final int[] rect = this.getMainRect();
		gui.drawTexturedModalRect(gui.getGuiLeft() + rect[0], gui.getGuiTop() + rect[1], 0, 156 - this.moduleScroll, rect[2], rect[3]);
	}

	public void drawHeader(final GuiDetector gui) {
		ResourceHelper.bindResource(GuiDetector.dropdownTexture);
		final int[] rect = this.getHeaderRect();
		gui.drawTexturedModalRect(gui.getGuiLeft() + rect[0], gui.getGuiTop() + rect[1], 77 * this.index, 156, rect[2], rect[3]);
	}

	public void drawContent(final GuiDetector gui, final int index, final int srcX, final int srcY) {
		final int[] rect = this.getContentRect(index);
		if (rect == null) {
			return;
		}
		final int gap = rect[1] - this.getMainRect()[1] + rect[3];
		if (gap > 0) {
			final int height = Math.min(rect[3], gap);
			final int offset = rect[3] - height;
			gui.drawTexturedModalRect(gui.getGuiLeft() + rect[0], gui.getGuiTop() + rect[1] + offset, srcX, srcY + offset, rect[2], height);
		}
	}

	public void drawContent(final GuiDetector gui, final int index, final ModuleData moduleData) {
		final int[] rect = this.getContentRect(index);
		if (rect == null) {
			return;
		}
		final int gap = rect[1] - this.getMainRect()[1] + rect[3];
		if (gap > 0) {
			final int height = Math.min(rect[3], gap);
			final int offset = rect[3] - height;
			gui.drawModuleIcon(moduleData, gui.getGuiLeft() + rect[0], gui.getGuiTop() + rect[1] + offset, rect[2] / 16.0f, height / 16.0f, 0.0f, offset / 16.0f);
		}
	}

	public int[] getContentRect(int posId) {
		int objectsPerRow = 11;
		int objectsRows = 7;
		int objectWidth = 16;
		int objectHeight = 16;
		int objectY = 31;
		if (this.index == 2) {
			objectsPerRow = 9;
			objectsRows = 10;
			objectWidth = 20;
			objectHeight = 11;
			objectY = 34;
		}
		posId = this.getCurrentId(posId, objectsPerRow * objectsRows);
		if (posId < 0 || posId >= objectsPerRow * objectsRows) {
			return null;
		}
		final int x = posId % objectsPerRow;
		final int y = posId / objectsPerRow;
		final int targetX = x * (objectWidth + 3) + 25;
		final int targetY = y * (objectHeight + 3) + 20 + objectY + this.getScroll() - 170;
		return new int[] { targetX, targetY, objectWidth, objectHeight };
	}

	public int[] getMainRect() {
		return new int[] { 11, 20, 232, this.moduleScroll };
	}

	public int[] getHeaderRect() {
		return new int[] { 11 + 77 * this.index, 20 + this.moduleScroll, (int) Math.ceil(77.33333587646484), 14 };
	}

	public int getScroll() {
		return this.moduleScroll;
	}

	protected int getCurrentId(final int index, final int objects) {
		return index;
	}

	public void onClick(final GuiDetector gui, final int x, final int y) {
	}
}
