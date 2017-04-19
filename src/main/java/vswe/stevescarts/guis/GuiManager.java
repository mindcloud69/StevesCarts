package vswe.stevescarts.guis;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.blocks.tileentities.TileEntityManager;
import vswe.stevescarts.containers.ContainerManager;
import vswe.stevescarts.helpers.Localization;

@SideOnly(Side.CLIENT)
public abstract class GuiManager extends GuiBase {
	private TileEntityManager manager;
	private InventoryPlayer invPlayer;

	public GuiManager(final InventoryPlayer invPlayer, final TileEntityManager manager, final ContainerManager container) {
		super(container);
		this.manager = manager;
		this.invPlayer = invPlayer;
	}

	@Override
	public void drawGuiForeground(final int x, final int y) {
		GL11.glDisable(2896);
		int[] coords = getMiddleCoords();
		getFontRenderer().drawString(getManagerName(), coords[0] - 34, 65, 4210752);
		getFontRenderer().drawString(Localization.GUI.MANAGER.TITLE.translate(), coords[0] + coords[2], 65, 4210752);
		for (int i = 0; i < 4; ++i) {
			coords = getTextCoords(i);
			final String str = getMaxSizeText(i);
			getFontRenderer().drawString(str, coords[0], coords[1], 4210752);
		}
		for (int i = 0; i < 4; ++i) {
			drawExtraOverlay(i, x, y);
			drawMouseOver(Localization.GUI.MANAGER.CHANGE_TRANSFER_DIRECTION.translate() + "\n" + Localization.GUI.MANAGER.CURRENT_SETTING.translate() + ": " + (
				manager.toCart[i] ? Localization.GUI.MANAGER.DIRECTION_TO_CART.translate()
				                  : Localization.GUI.MANAGER.DIRECTION_FROM_CART.translate()), x, y, getArrowCoords(i));
			drawMouseOver(Localization.GUI.MANAGER.CHANGE_TURN_BACK_SETTING.translate() + "\n" + Localization.GUI.MANAGER.CURRENT_SETTING.translate() + ": " + (
				(manager.color[i] == 5) ? Localization.GUI.MANAGER.TURN_BACK_NOT_SELECTED.translate()
				                        : (manager.doReturn[manager.color[i] - 1] ? Localization.GUI.MANAGER.TURN_BACK_DO.translate()
				                                                                       : Localization.GUI.MANAGER.TURN_BACK_DO_NOT.translate())), x, y, getReturnCoords(i));
			drawMouseOver(Localization.GUI.MANAGER.CHANGE_TRANSFER_SIZE.translate() + "\n" + Localization.GUI.MANAGER.CURRENT_SETTING.translate() + ": " + getMaxSizeOverlay(i), x, y, getTextCoords(i));
			drawMouseOver(Localization.GUI.MANAGER.CHANGE_SIDE.translate() + "\n" + Localization.GUI.MANAGER.CURRENT_SIDE.translate() + ": " + (new String[] {
				Localization.GUI.MANAGER.SIDE_RED.translate(), Localization.GUI.MANAGER.SIDE_BLUE.translate(), Localization.GUI.MANAGER.SIDE_YELLOW.translate(),
				Localization.GUI.MANAGER.SIDE_GREEN.translate(),
				Localization.GUI.MANAGER.SIDE_DISABLED.translate() })[manager.color[i] - 1], x, y, getColorpickerCoords(i));
		}
		drawMouseOver(getLayoutString() + "\n" + Localization.GUI.MANAGER.CURRENT_SETTING.translate() + ": " + getLayoutOption(manager.layoutType), x, y, getMiddleCoords());
		GL11.glEnable(2896);
	}

	protected void drawMouseOver(final String str, final int x, final int y, final int[] rect) {
		if (inRect(x - getGuiLeft(), y - getGuiTop(), rect)) {
			drawMouseOver(str, x - getGuiLeft(), y - getGuiTop());
		}
	}

	@Override
	public void drawGuiBackground(final float f, final int x, final int y) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		final int left = getGuiLeft();
		final int top = getGuiTop();
		drawBackground(left, top);
		for (int i = 0; i < 4; ++i) {
			drawArrow(i, left, top);
			final int color = manager.color[i] - 1;
			if (color != 4) {
				drawColors(i, color, left, top);
			}
		}
		final RenderItem renderitem = Minecraft.getMinecraft().getRenderItem();
		final int[] coords = getMiddleCoords();
		renderitem.renderItemIntoGUI(new ItemStack(getBlock(), 1), left + coords[0], top + coords[1]);
		for (int j = 0; j < 4; ++j) {
			drawItems(j, renderitem, left, top);
		}
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	private void drawArrow(final int id, final int left, final int top) {
		int sourceX = getArrowSourceX();
		int sourceY = 28;
		sourceY += 56 * id;
		if (!manager.toCart[id]) {
			sourceX += 28;
		}
		final int targetX = getArrowCoords(id)[0];
		final int targetY = getArrowCoords(id)[1];
		int sizeX = 28;
		int sizeY = 28;
		drawTexturedModalRect(left + targetX, top + targetY, sourceX, sourceY, sizeX, sizeY);
		if (id == manager.getLastSetting() && manager.color[id] != 5) {
			sourceY -= 28;
			int scaledProgress = manager.moveProgressScaled(42);
			int offsetX = 0;
			int offsetY = 0;
			if (manager.toCart[id]) {
				sizeX = 14;
				if (id % 2 == 0) {
					offsetX = 14;
				}
				sizeY = scaledProgress;
				if (sizeY > 19) {
					sizeY = 19;
				}
				if (id < 2) {
					offsetY = 28 - sizeY;
				}
			} else {
				sizeY = 14;
				if (id >= 2) {
					offsetY = 14;
				}
				sizeX = scaledProgress;
				if (sizeX > 19) {
					sizeX = 19;
				}
				if (id % 2 == 1) {
					offsetX = 28 - sizeX;
				}
			}
			drawTexturedModalRect(left + targetX + offsetX, top + targetY + offsetY, sourceX + offsetX, sourceY + offsetY, sizeX, sizeY);
			offsetY = (offsetX = 0);
			sizeY = (sizeX = 28);
			if (scaledProgress > 19) {
				scaledProgress -= 19;
				if (manager.toCart[id]) {
					sizeX = scaledProgress;
					if (sizeX > 23) {
						sizeX = 23;
					}
					if (id % 2 == 0) {
						offsetX = 22 - sizeX;
					} else {
						offsetX = 6;
					}
				} else {
					sizeY = scaledProgress;
					if (sizeY > 23) {
						sizeY = 23;
					}
					if (id >= 2) {
						offsetY = 22 - sizeY;
					} else {
						offsetY = 6;
					}
				}
				drawTexturedModalRect(left + targetX + offsetX, top + targetY + offsetY, sourceX + offsetX, sourceY + offsetY, sizeX, sizeY);
			}
		}
	}

	protected void drawColors(final int id, final int color, final int left, final int top) {
		int[] coords = getReturnCoords(id);
		drawTexturedModalRect(left + coords[0], top + coords[1], getColorSourceX() + (manager.doReturn[manager.color[id] - 1] ? 8 : 0), 80 + 8 * color, 8, 8);
		coords = getBoxCoords(id);
		drawTexturedModalRect(left + coords[0] - 2, top + coords[1] - 2, getColorSourceX(), 20 * color, 20, 20);
	}

	protected int[] getMiddleCoords() {
		return new int[] { getCenterTargetX() + 45, 61, 20, 20 };
	}

	protected int[] getBoxCoords(final int id) {
		final int x = id % 2;
		final int y = id / 2;
		final int xCoord = getCenterTargetX() + 4 + x * 82;
		int yCoord = 17 + y * 88;
		yCoord += offsetObjectY(manager.layoutType, x, y);
		return new int[] { xCoord, yCoord, 20, 20 };
	}

	protected int[] getArrowCoords(final int id) {
		final int x = id % 2;
		final int y = id / 2;
		final int xCoord = getCenterTargetX() + 25 + x * 28;
		int yCoord = 17 + y * 76;
		yCoord += offsetObjectY(manager.layoutType, x, y);
		return new int[] { xCoord, yCoord, 28, 28 };
	}

	protected int[] getTextCoords(final int id) {
		final int[] coords = getBoxCoords(id);
		final int xCoord = coords[0];
		int yCoord = coords[1];
		if (id >= 2) {
			yCoord -= 12;
		} else {
			yCoord += 20;
		}
		return new int[] { xCoord, yCoord, 20, 10 };
	}

	protected int[] getColorpickerCoords(final int id) {
		final int x = id % 2;
		final int y = id / 2;
		final int xCoord = getCenterTargetX() + 3 + x * 92;
		int yCoord = 49 + y * 32;
		yCoord += offsetObjectY(manager.layoutType, x, y);
		return new int[] { xCoord, yCoord, 8, 8 };
	}

	protected int[] getReturnCoords(final int id) {
		final int x = id % 2;
		final int y = id / 2;
		final int xCoord = getCenterTargetX() + 14 + x * 70;
		int yCoord = 49 + y * 32;
		yCoord += offsetObjectY(manager.layoutType, x, y);
		return new int[] { xCoord, yCoord, 8, 8 };
	}

	@Override
	public void mouseClick(int x, int y, final int button) {
		super.mouseClick(x, y, button);
		if (button == 0 || button == 1) {
			x -= getGuiLeft();
			y -= getGuiTop();
			if (inRect(x, y, getMiddleCoords())) {
				manager.sendPacket(5, (byte) ((button == 0) ? 1 : -1));
			} else {
				for (int i = 0; i < 4; ++i) {
					byte data = (byte) i;
					data |= (byte) (button << 2);
					if (inRect(x, y, getArrowCoords(i))) {
						manager.sendPacket(0, (byte) i);
						break;
					}
					if (inRect(x, y, getTextCoords(i))) {
						manager.sendPacket(2, data);
						break;
					}
					if (inRect(x, y, getColorpickerCoords(i))) {
						manager.sendPacket(3, data);
						break;
					}
					if (inRect(x, y, getReturnCoords(i))) {
						manager.sendPacket(4, (byte) i);
						break;
					}
					if (sendOnClick(i, x, y, data)) {
						break;
					}
				}
			}
		}
	}

	protected void drawExtraOverlay(final int id, final int x, final int y) {
	}

	protected boolean sendOnClick(final int id, final int x, final int y, final byte data) {
		return false;
	}

	protected int offsetObjectY(final int layout, final int x, final int y) {
		return 0;
	}

	protected void drawItems(final int id, final RenderItem renderitem, final int left, final int top) {
	}

	protected abstract String getMaxSizeOverlay(final int p0);

	protected abstract String getMaxSizeText(final int p0);

	protected abstract void drawBackground(final int p0, final int p1);

	protected abstract int getArrowSourceX();

	protected abstract int getColorSourceX();

	protected abstract int getCenterTargetX();

	protected abstract Block getBlock();

	protected abstract String getManagerName();

	protected abstract String getLayoutOption(final int p0);

	protected abstract String getLayoutString();

	protected TileEntityManager getManager() {
		return manager;
	}
}
