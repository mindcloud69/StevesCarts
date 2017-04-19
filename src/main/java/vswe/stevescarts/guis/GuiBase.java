package vswe.stevescarts.guis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.modules.data.ModuleData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class GuiBase extends GuiContainer {
	private int myOwnEventButton;
	private long myOwnTimeyWhineyThingy;
	private int myOwnTouchpadTimeWhineyThingy;

	public GuiBase(final Container container) {
		super(container);
		myOwnEventButton = 0;
		myOwnTimeyWhineyThingy = 0L;
		myOwnTouchpadTimeWhineyThingy = 0;
	}

	public void drawMouseOver(final String str, final int x, final int y) {
		final List text = new ArrayList();
		final String[] split = str.split("\n");
		for (int i = 0; i < split.length; ++i) {
			text.add(split[i]);
		}
		drawMouseOver(text, x, y);
	}

	public boolean inRect(final int x, final int y, final int[] coords) {
		return coords != null && x >= coords[0] && x < coords[0] + coords[2] && y >= coords[1] && y < coords[1] + coords[3];
	}

	public void drawMouseOver(final List<String> text, final int x, final int y) {
		GL11.glDisable(2896);
		GL11.glDisable(2929);
		int var5 = 0;
		for (final String var7 : text) {
			final int var8 = getFontRenderer().getStringWidth(var7);
			if (var8 > var5) {
				var5 = var8;
			}
		}
		final int var9 = x + 10;
		int var10 = y;
		int var11 = 8;
		if (text.size() > 1) {
			var11 += 2 + (text.size() - 1) * 10;
		}
		zLevel = 300.0f;
		//GuiBase.itemRender.zLevel = 300.0f;
		final int var12 = -267386864;
		drawGradientRect(var9 - 3, var10 - 4, var9 + var5 + 3, var10 - 3, var12, var12);
		drawGradientRect(var9 - 3, var10 + var11 + 3, var9 + var5 + 3, var10 + var11 + 4, var12, var12);
		drawGradientRect(var9 - 3, var10 - 3, var9 + var5 + 3, var10 + var11 + 3, var12, var12);
		drawGradientRect(var9 - 4, var10 - 3, var9 - 3, var10 + var11 + 3, var12, var12);
		drawGradientRect(var9 + var5 + 3, var10 - 3, var9 + var5 + 4, var10 + var11 + 3, var12, var12);
		final int var13 = 1347420415;
		final int var14 = (var13 & 0xFEFEFE) >> 1 | (var13 & 0xFF000000);
		drawGradientRect(var9 - 3, var10 - 3 + 1, var9 - 3 + 1, var10 + var11 + 3 - 1, var13, var14);
		drawGradientRect(var9 + var5 + 2, var10 - 3 + 1, var9 + var5 + 3, var10 + var11 + 3 - 1, var13, var14);
		drawGradientRect(var9 - 3, var10 - 3, var9 + var5 + 3, var10 - 3 + 1, var13, var13);
		drawGradientRect(var9 - 3, var10 + var11 + 2, var9 + var5 + 3, var10 + var11 + 3, var14, var14);
		for (int var15 = 0; var15 < text.size(); ++var15) {
			final String var16 = text.get(var15);
			getFontRenderer().drawStringWithShadow(var16, var9, var10, -1);
			if (var15 == 0) {
				var10 += 2;
			}
			var10 += 10;
		}
		zLevel = 0.0f;
		//GuiBase.itemRender.zLevel = 0.0f;
		GL11.glEnable(2929);
		GL11.glEnable(2896);
	}

	public Minecraft getMinecraft() {
		return mc;
	}

	public FontRenderer getFontRenderer() {
		return fontRendererObj;
	}

	public void setXSize(final int val) {
		xSize = val;
		guiLeft = (width - xSize) / 2;
	}

	public void setYSize(final int val) {
		ySize = val;
		guiTop = (height - ySize) / 2;
	}

	public int getXSize() {
		return xSize;
	}

	public int getYSize() {
		return ySize;
	}

	public int getGuiLeft() {
		return guiLeft;
	}

	public int getGuiTop() {
		return guiTop;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int x, final int y) {
		drawGuiForeground(x, y);
	}

	public void drawGuiForeground(final int x, final int y) {
	}

	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
		startScaling();
	}

	private int scaleX(float x) {
		final float scale = getScale();
		x /= scale;
		x += getGuiLeft();
		x -= (width - xSize * scale) / (2.0f * scale);
		return (int) x;
	}

	private int scaleY(float y) {
		final float scale = getScale();
		y /= scale;
		y += getGuiTop();
		y -= (height - ySize * scale) / (2.0f * scale);
		return (int) y;
	}

	@Override
	public void drawScreen(final int x, final int y, final float f) {
		super.drawScreen(scaleX(x), scaleY(y), f);
		stopScaling();
	}

	protected float getScale() {
		final ScaledResolution scaledresolution = new ScaledResolution(mc);
		final float w = scaledresolution.getScaledWidth() * 0.9f;
		final float h = scaledresolution.getScaledHeight() * 0.9f;
		final float multX = w / getXSize();
		final float multY = h / getYSize();
		float mult = Math.min(multX, multY);
		if (mult > 1.0f) {
			mult = 1.0f;
		}
		return mult;
	}

	private void startScaling() {
		GL11.glPushMatrix();
		final float scale = getScale();
		GL11.glScalef(scale, scale, 1.0f);
		GL11.glTranslatef((-guiLeft), (-guiTop), 0.0f);
		GL11.glTranslatef((width - xSize * scale) / (2.0f * scale), (height - ySize * scale) / (2.0f * scale), 0.0f);
	}

	private void stopScaling() {
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float f, final int x, final int y) {
		drawGuiBackground(f, x, y);
	}

	public void drawGuiBackground(final float f, final int x, final int y) {
	}

	@Override
	protected void mouseClicked(int x, int y, final int button) throws IOException {
		x = scaleX(x);
		y = scaleY(y);
		super.mouseClicked(x, y, button);
		mouseClick(x, y, button);
	}

	public void mouseClick(final int x, final int y, final int button) {
	}

	protected void mouseMovedOrUp(int x, int y, final int button) {
		x = scaleX(x);
		y = scaleY(y);
		//super.mouseMovedOrUp(x, y, button);
		mouseMoved(x, y, button);
		mouseDraged(x, y, button);
	}

	@Override
	public void handleMouseInput() throws IOException {
		final int mouseX = Mouse.getEventX() * width / mc.displayWidth;
		final int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
		if (Mouse.getEventButtonState()) {
			if (mc.gameSettings.touchscreen && myOwnTouchpadTimeWhineyThingy++ > 0) {
				return;
			}
			myOwnEventButton = Mouse.getEventButton();
			myOwnTimeyWhineyThingy = Minecraft.getSystemTime();
			mouseClicked(mouseX, mouseY, myOwnEventButton);
		} else if (Mouse.getEventButton() != -1) {
			if (mc.gameSettings.touchscreen && --myOwnTouchpadTimeWhineyThingy > 0) {
				return;
			}
			myOwnEventButton = -1;
			mouseReleased(mouseX, mouseY, Mouse.getEventButton());
			mouseMovedOrUp(mouseX, mouseY, Mouse.getEventButton());
		} else if (myOwnEventButton != -1 && myOwnTimeyWhineyThingy > 0L) {
			final long k = Minecraft.getSystemTime() - myOwnTimeyWhineyThingy;
			mouseClickMove(mouseX, mouseY, myOwnEventButton, k);
		} else {
			mouseMovedOrUp(mouseX, mouseY, -1);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		mouseX = scaleX(mouseX);
		mouseY = scaleY(mouseY);
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void mouseClickMove(int x, int y, final int button, final long timeSinceClick) {
		x = scaleX(x);
		y = scaleY(y);
		super.mouseClickMove(x, y, button, timeSinceClick);
		mouseMoved(x, y, -1);
		mouseDraged(x, y, button);
	}

	public void mouseMoved(final int x, final int y, final int button) {
	}

	public void mouseDraged(final int x, final int y, final int button) {
	}

	@Override
	protected void keyTyped(final char character, final int extraInformation) throws IOException {
		if (extraInformation == 1 || !disableStandardKeyFunctionality()) {
			super.keyTyped(character, extraInformation);
		}
		keyPress(character, extraInformation);
	}

	public boolean disableStandardKeyFunctionality() {
		return false;
	}

	public void keyPress(final char character, final int extraInformation) {
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	public void enableKeyRepeat(final boolean val) {
		Keyboard.enableRepeatEvents(val);
	}

	public float getZLevel() {
		return zLevel;
	}

	//	public void drawIcon(final IIcon icon, final int targetX, final int targetY, final float sizeX, final float sizeY, final float offsetX, final float offsetY) {
	//		final Tessellator tessellator = Tessellator.instance;
	//		tessellator.startDrawingQuads();
	//		final float x = icon.getMinU() + offsetX * (icon.getMaxU() - icon.getMinU());
	//		final float y = icon.getMinV() + offsetY * (icon.getMaxV() - icon.getMinV());
	//		final float width = (icon.getMaxU() - icon.getMinU()) * sizeX;
	//		final float height = (icon.getMaxV() - icon.getMinV()) * sizeY;
	//		tessellator.addVertexWithUV((double) (targetX + 0), (double) (targetY + 16.0f * sizeY), (double) this.getZLevel(), (double) (x + 0.0f), (double) (y + height));
	//		tessellator.addVertexWithUV((double) (targetX + 16.0f * sizeX), (double) (targetY + 16.0f * sizeY), (double) this.getZLevel(), (double) (x + width), (double) (y + height));
	//		tessellator.addVertexWithUV((double) (targetX + 16.0f * sizeX), (double) (targetY + 0), (double) this.getZLevel(), (double) (x + width), (double) (y + 0.0f));
	//		tessellator.addVertexWithUV((double) (targetX + 0), (double) (targetY + 0), (double) this.getZLevel(), (double) (x + 0.0f), (double) (y + 0.0f));
	//		tessellator.draw();
	//	}

	public void drawModuleIcon(ModuleData icon, final int targetX, final int targetY, final float sizeX, final float sizeY, final float offsetX, final float offsetY) {
		RenderHelper.enableGUIStandardItemLighting();
		RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
		itemRenderer.renderItemAndEffectIntoGUI(icon.getItemStack(), targetX, targetY);

	}

	public void drawTexturedModalRect(final int x, final int y, final int u, final int v, final int w, final int h, final RENDER_ROTATION rotation) {
		final float fw = 0.00390625f;
		final float fy = 0.00390625f;
		final double a = (u + 0) * fw;
		final double b = (u + w) * fw;
		final double c = (v + h) * fy;
		final double d = (v + 0) * fy;
		final double[] ptA = { a, c };
		final double[] ptB = { b, c };
		final double[] ptC = { b, d };
		final double[] ptD = { a, d };
		double[] pt1 = null;
		double[] pt2 = null;
		double[] pt3 = null;
		double[] pt4 = null;
		switch (rotation) {
			default: {
				pt1 = ptA;
				pt2 = ptB;
				pt3 = ptC;
				pt4 = ptD;
				break;
			}
			case ROTATE_90: {
				pt1 = ptB;
				pt2 = ptC;
				pt3 = ptD;
				pt4 = ptA;
				break;
			}
			case ROTATE_180: {
				pt1 = ptC;
				pt2 = ptD;
				pt3 = ptA;
				pt4 = ptB;
				break;
			}
			case ROTATE_270: {
				pt1 = ptD;
				pt2 = ptA;
				pt3 = ptB;
				pt4 = ptC;
				break;
			}
			case FLIP_HORIZONTAL: {
				pt1 = ptB;
				pt2 = ptA;
				pt3 = ptD;
				pt4 = ptC;
				break;
			}
			case ROTATE_90_FLIP: {
				pt1 = ptA;
				pt2 = ptD;
				pt3 = ptC;
				pt4 = ptB;
				break;
			}
			case FLIP_VERTICAL: {
				pt1 = ptD;
				pt2 = ptC;
				pt3 = ptB;
				pt4 = ptA;
				break;
			}
			case ROTATE_270_FLIP: {
				pt1 = ptC;
				pt2 = ptB;
				pt3 = ptA;
				pt4 = ptD;
				break;
			}
		}

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((x + 0), y + h, zLevel).tex(pt1[0], pt1[1]).endVertex();
		vertexbuffer.pos((x + w), y + h, zLevel).tex(pt2[0], pt2[1]).endVertex();
		vertexbuffer.pos((x + w), y + 0, zLevel).tex(pt3[0], pt3[1]).endVertex();
		vertexbuffer.pos((x + 0), y + 0, zLevel).tex(pt4[0], pt4[1]).endVertex();
		tessellator.draw();
	}

	public enum RENDER_ROTATION {
		NORMAL,
		ROTATE_90,
		ROTATE_180,
		ROTATE_270,
		FLIP_HORIZONTAL,
		ROTATE_90_FLIP,
		FLIP_VERTICAL,
		ROTATE_270_FLIP;

		public RENDER_ROTATION getNextRotation() {
			switch (this) {
				default: {
					return RENDER_ROTATION.ROTATE_90;
				}
				case ROTATE_90: {
					return RENDER_ROTATION.ROTATE_180;
				}
				case ROTATE_180: {
					return RENDER_ROTATION.ROTATE_270;
				}
				case ROTATE_270: {
					return RENDER_ROTATION.NORMAL;
				}
				case FLIP_HORIZONTAL: {
					return RENDER_ROTATION.ROTATE_90_FLIP;
				}
				case ROTATE_90_FLIP: {
					return RENDER_ROTATION.FLIP_VERTICAL;
				}
				case FLIP_VERTICAL: {
					return RENDER_ROTATION.ROTATE_270_FLIP;
				}
				case ROTATE_270_FLIP: {
					return RENDER_ROTATION.FLIP_HORIZONTAL;
				}
			}
		}

		public RENDER_ROTATION getFlippedRotation() {
			switch (this) {
				default: {
					return RENDER_ROTATION.FLIP_HORIZONTAL;
				}
				case ROTATE_90: {
					return RENDER_ROTATION.ROTATE_90_FLIP;
				}
				case ROTATE_180: {
					return RENDER_ROTATION.FLIP_VERTICAL;
				}
				case ROTATE_270: {
					return RENDER_ROTATION.ROTATE_270_FLIP;
				}
				case FLIP_HORIZONTAL: {
					return RENDER_ROTATION.NORMAL;
				}
				case ROTATE_90_FLIP: {
					return RENDER_ROTATION.ROTATE_90;
				}
				case FLIP_VERTICAL: {
					return RENDER_ROTATION.ROTATE_180;
				}
				case ROTATE_270_FLIP: {
					return RENDER_ROTATION.ROTATE_270;
				}
			}
		}
	}
}
