package vswe.stevescarts.arcade;

import java.util.ArrayList;

import vswe.stevescarts.guis.GuiMinecart;

public class ScrollableList {
	private int x;
	private int y;
	private ArcadeTracks game;
	private ArrayList<String> items;
	private int scrollPosition;
	private boolean isScrolling;
	private int selectedIndex;

	public ScrollableList(final ArcadeTracks game, final int x, final int y) {
		this.selectedIndex = -1;
		this.x = x;
		this.y = y;
		this.game = game;
		this.items = new ArrayList<String>();
	}

	public void clearList() {
		this.items.clear();
	}

	public void clear() {
		this.selectedIndex = -1;
		this.scrollPosition = 0;
	}

	public void add(final String str) {
		this.items.add(str);
	}

	public boolean isVisible() {
		return true;
	}

	public int getSelectedIndex() {
		return this.selectedIndex;
	}

	public void onClick() {
	}

	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		if (!this.isVisible()) {
			return;
		}
		final int[] menu = this.game.getMenuArea();
		this.game.getModule().drawImage(gui, menu[0] + this.x, menu[1] + this.y, 0, 192, 132, 64);
		for (int i = 0; i < this.items.size(); ++i) {
			final int[] rect = this.getLevelButtonArea(i);
			if (rect[3] > 0) {
				int srcY = 188 + ((this.items.get(i) == null) ? 34 : (this.game.getModule().inRect(x, y, rect) ? 17 : 0));
				int borderSrcY = 239;
				if (rect[4] < 0) {
					srcY -= rect[4];
					borderSrcY -= rect[4];
				}
				this.game.getModule().drawImage(gui, rect, 146, srcY);
				if (i == this.selectedIndex) {
					this.game.getModule().drawImage(gui, rect, 146, borderSrcY);
				}
			}
		}
		final int[] area = this.getScrollArea();
		this.game.getModule().drawImage(gui, area[0], area[1] + this.scrollPosition, 132, 256 - ((this.items.size() >= 4) ? 32 : 16), 14, 16);
	}

	public void drawForeground(final GuiMinecart gui) {
		if (!this.isVisible()) {
			return;
		}
		for (int i = 0; i < this.items.size(); ++i) {
			final int[] rect = this.getLevelButtonArea(i);
			final int x = rect[0] + 4;
			int y = rect[1] + 5;
			if (rect[4] < 0) {
				y += rect[4];
			}
			if (rect[4] >= -5) {
				if (rect[4] <= 48) {
					this.game.getModule().drawString(gui, (this.items.get(i) == null) ? "<???>" : this.items.get(i), x, y, 4210752);
				}
			}
		}
	}

	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
		if (!this.isVisible()) {
			return;
		}
		if (this.isScrolling) {
			if (button != -1) {
				this.isScrolling = false;
			} else {
				this.doScroll(y);
			}
		}
	}

	private void doScroll(final int y) {
		final int[] area = this.getScrollArea();
		this.scrollPosition = y - area[1] - 8;
		if (this.scrollPosition < 0) {
			this.scrollPosition = 0;
		} else if (this.scrollPosition > 42) {
			this.scrollPosition = 42;
		}
	}

	private int getScrollLevel() {
		final int totalSize = this.items.size() * 18;
		final int availableSpace = 60;
		final int canNotFit = totalSize - availableSpace;
		final int scrollLength = this.getScrollArea()[3] - 16;
		return canNotFit * (this.scrollPosition / scrollLength);
	}

	private int[] getLevelButtonArea(final int id) {
		final int[] menu = this.game.getMenuArea();
		final int offSetY = 18 * id - this.getScrollLevel();
		int height = 17;
		int y = menu[1] + this.y + 2 + offSetY;
		if (offSetY < 0) {
			height += offSetY;
			y -= offSetY;
		} else if (offSetY + height > 60) {
			height = 60 - offSetY;
		}
		return new int[] { menu[0] + 2 + this.x, y, 108, height, offSetY };
	}

	private int[] getScrollArea() {
		final int[] menu = this.game.getMenuArea();
		return new int[] { menu[0] + this.x + 116, menu[1] + this.y + 3, 14, 58 };
	}

	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (!this.isVisible()) {
			return;
		}
		for (int i = 0; i < this.items.size(); ++i) {
			if (this.items.get(i) != null) {
				final int[] rect = this.getLevelButtonArea(i);
				if (rect[3] > 0 && this.game.getModule().inRect(x, y, rect)) {
					if (this.selectedIndex == i) {
						this.selectedIndex = -1;
					} else {
						this.selectedIndex = i;
					}
					this.onClick();
					break;
				}
			}
		}
		if (this.items.size() >= 4 && this.game.getModule().inRect(x, y, this.getScrollArea())) {
			this.doScroll(y);
			this.isScrolling = true;
		}
	}
}
