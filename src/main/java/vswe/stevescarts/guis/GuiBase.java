package vswe.stevescarts.guis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.modules.data.ModuleData;

@SideOnly(Side.CLIENT)
public abstract class GuiBase extends GuiContainer {
	private int myOwnEventButton;
	private long myOwnTimeyWhineyThingy;
	private int myOwnTouchpadTimeWhineyThingy;

	public GuiBase(final Container container) {
		super(container);
		this.myOwnEventButton = 0;
		this.myOwnTimeyWhineyThingy = 0L;
		this.myOwnTouchpadTimeWhineyThingy = 0;
	}

	public void drawMouseOver(final String str, final int x, final int y) {
		final List text = new ArrayList();
		final String[] split = str.split("\n");
		for (int i = 0; i < split.length; ++i) {
			text.add(split[i]);
		}
		this.drawMouseOver(text, x, y);
	}

	public boolean inRect(final int x, final int y, final int[] coords) {
		return coords != null && x >= coords[0] && x < coords[0] + coords[2] && y >= coords[1] && y < coords[1] + coords[3];
	}

	public void drawMouseOver(final List<String> text, final int x, final int y) {
		GL11.glDisable(2896);
		GL11.glDisable(2929);
		int var5 = 0;
		for (final String var7 : text) {
			final int var8 = this.getFontRenderer().getStringWidth(var7);
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
		this.zLevel = 300.0f;
		//GuiBase.itemRender.zLevel = 300.0f;
		final int var12 = -267386864;
		this.drawGradientRect(var9 - 3, var10 - 4, var9 + var5 + 3, var10 - 3, var12, var12);
		this.drawGradientRect(var9 - 3, var10 + var11 + 3, var9 + var5 + 3, var10 + var11 + 4, var12, var12);
		this.drawGradientRect(var9 - 3, var10 - 3, var9 + var5 + 3, var10 + var11 + 3, var12, var12);
		this.drawGradientRect(var9 - 4, var10 - 3, var9 - 3, var10 + var11 + 3, var12, var12);
		this.drawGradientRect(var9 + var5 + 3, var10 - 3, var9 + var5 + 4, var10 + var11 + 3, var12, var12);
		final int var13 = 1347420415;
		final int var14 = (var13 & 0xFEFEFE) >> 1 | (var13 & 0xFF000000);
		this.drawGradientRect(var9 - 3, var10 - 3 + 1, var9 - 3 + 1, var10 + var11 + 3 - 1, var13, var14);
		this.drawGradientRect(var9 + var5 + 2, var10 - 3 + 1, var9 + var5 + 3, var10 + var11 + 3 - 1, var13, var14);
		this.drawGradientRect(var9 - 3, var10 - 3, var9 + var5 + 3, var10 - 3 + 1, var13, var13);
		this.drawGradientRect(var9 - 3, var10 + var11 + 2, var9 + var5 + 3, var10 + var11 + 3, var14, var14);
		for (int var15 = 0; var15 < text.size(); ++var15) {
			final String var16 = text.get(var15);
			this.getFontRenderer().drawStringWithShadow(var16, var9, var10, -1);
			if (var15 == 0) {
				var10 += 2;
			}
			var10 += 10;
		}
		this.zLevel = 0.0f;
		//GuiBase.itemRender.zLevel = 0.0f;
		GL11.glEnable(2929);
		GL11.glEnable(2896);
	}

	public Minecraft getMinecraft() {
		return this.mc;
	}

	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}

	public void setXSize(final int val) {
		this.xSize = val;
		this.guiLeft = (this.width - this.xSize) / 2;
	}

	public void setYSize(final int val) {
		this.ySize = val;
		this.guiTop = (this.height - this.ySize) / 2;
	}

	public int getXSize() {
		return this.xSize;
	}

	public int getYSize() {
		return this.ySize;
	}

	public int getGuiLeft() {
		return this.guiLeft;
	}

	public int getGuiTop() {
		return this.guiTop;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int x, final int y) {
		this.drawGuiForeground(x, y);
	}

	public void drawGuiForeground(final int x, final int y) {
	}

	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
		this.startScaling();
	}

	private int scaleX(float x) {
		final float scale = this.getScale();
		x /= scale;
		x += this.getGuiLeft();
		x -= (this.width - this.xSize * scale) / (2.0f * scale);
		return (int) x;
	}

	private int scaleY(float y) {
		final float scale = this.getScale();
		y /= scale;
		y += this.getGuiTop();
		y -= (this.height - this.ySize * scale) / (2.0f * scale);
		return (int) y;
	}

	@Override
	public void drawScreen(final int x, final int y, final float f) {
		super.drawScreen(this.scaleX(x), this.scaleY(y), f);
		this.stopScaling();
	}

	protected float getScale() {
		final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		final float w = scaledresolution.getScaledWidth() * 0.9f;
		final float h = scaledresolution.getScaledHeight() * 0.9f;
		final float multX = w / this.getXSize();
		final float multY = h / this.getYSize();
		float mult = Math.min(multX, multY);
		if (mult > 1.0f) {
			mult = 1.0f;
		}
		return mult;
	}

	private void startScaling() {
		GL11.glPushMatrix();
		final float scale = this.getScale();
		GL11.glScalef(scale, scale, 1.0f);
		GL11.glTranslatef((-this.guiLeft), (-this.guiTop), 0.0f);
		GL11.glTranslatef((this.width - this.xSize * scale) / (2.0f * scale), (this.height - this.ySize * scale) / (2.0f * scale), 0.0f);
	}

	private void stopScaling() {
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float f, final int x, final int y) {
		this.drawGuiBackground(f, x, y);
	}

	public void drawGuiBackground(final float f, final int x, final int y) {
	}

	@Override
	protected void mouseClicked(int x, int y, final int button) throws IOException {
		x = this.scaleX(x);
		y = this.scaleY(y);
		super.mouseClicked(x, y, button);
		this.mouseClick(x, y, button);
	}

	public void mouseClick(final int x, final int y, final int button) {
	}


	protected void mouseMovedOrUp(int x, int y, final int button) {
		x = this.scaleX(x);
		y = this.scaleY(y);
		//super.mouseMovedOrUp(x, y, button);
		this.mouseMoved(x, y, button);
		this.mouseDraged(x, y, button);
	}

	@Override
	public void handleMouseInput() throws IOException {
		final int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
		final int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		if (Mouse.getEventButtonState()) {
			if (this.mc.gameSettings.touchscreen && this.myOwnTouchpadTimeWhineyThingy++ > 0) {
				return;
			}
			this.myOwnEventButton = Mouse.getEventButton();
			this.myOwnTimeyWhineyThingy = Minecraft.getSystemTime();
			this.mouseClicked(i, j, this.myOwnEventButton);
		} else if (Mouse.getEventButton() != -1) {
			if (this.mc.gameSettings.touchscreen && --this.myOwnTouchpadTimeWhineyThingy > 0) {
				return;
			}
			this.myOwnEventButton = -1;
			this.mouseMovedOrUp(i, j, Mouse.getEventButton());
		} else if (this.myOwnEventButton != -1 && this.myOwnTimeyWhineyThingy > 0L) {
			final long k = Minecraft.getSystemTime() - this.myOwnTimeyWhineyThingy;
			this.mouseClickMove(i, j, this.myOwnEventButton, k);
		} else {
			this.mouseMovedOrUp(i, j, -1);
		}
	}

	@Override
	protected void mouseClickMove(int x, int y, final int button, final long timeSinceClick) {
		x = this.scaleX(x);
		y = this.scaleY(y);
		super.mouseClickMove(x, y, button, timeSinceClick);
		this.mouseMoved(x, y, -1);
		this.mouseDraged(x, y, button);
	}

	public void mouseMoved(final int x, final int y, final int button) {
	}

	public void mouseDraged(final int x, final int y, final int button) {
	}

	@Override
	protected void keyTyped(final char character, final int extraInformation) throws IOException {
		if (extraInformation == 1 || !this.disableStandardKeyFunctionality()) {
			super.keyTyped(character, extraInformation);
		}
		this.keyPress(character, extraInformation);
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
		return this.zLevel;
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
		//TODO
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
		//TODO
		//		final Tessellator tessellator = Tessellator.getInstance();
		//		tessellator.startDrawingQuads();
		//		tessellator.addVertexWithUV((double) (x + 0), (double) (y + h), (double) this.zLevel, pt1[0], pt1[1]);
		//		tessellator.addVertexWithUV((double) (x + w), (double) (y + h), (double) this.zLevel, pt2[0], pt2[1]);
		//		tessellator.addVertexWithUV((double) (x + w), (double) (y + 0), (double) this.zLevel, pt3[0], pt3[1]);
		//		tessellator.addVertexWithUV((double) (x + 0), (double) (y + 0), (double) this.zLevel, pt4[0], pt4[1]);
		//		tessellator.draw();
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
