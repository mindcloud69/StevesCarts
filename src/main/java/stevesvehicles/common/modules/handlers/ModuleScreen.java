package stevesvehicles.common.modules.handlers;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.gui.screen.GuiBase;
import stevesvehicles.client.gui.screen.GuiVehicle;
import stevesvehicles.common.vehicles.VehicleBase;

public abstract class ModuleScreen {
	// the vehicle this module is part of
	private VehicleBase vehicle;
	// where in the interface the module is located, used to draw things where
	// they should be.
	// the values are calculated in the vehicle on initializing
	private int offSetX;
	private int offSetY;

	/**
	 * Creates a new instance of this module, the module will be created at the
	 * given vehicle.
	 * 
	 * @param vehicle
	 *            The vehicle this module is created on
	 */
	public ModuleScreen(VehicleBase vehicle) {
		// save the vehicle
		this.vehicle = vehicle;
	}

	/**
	 * Get the vehicle this module is a part of
	 * 
	 * @return The vehicle this module was created at
	 */
	public stevesvehicles.common.vehicles.VehicleBase getVehicle() {
		return vehicle;
	}

	/**
	 * If this module is part of a placeholder vehicle, a placeholder vehicle is
	 * a client side only vehicle used in the vehicle assembler.
	 * 
	 * @return If this module is a placeholder module
	 */
	public boolean isPlaceholder() {
		return getVehicle().isPlaceholder;
	}

	/**
	 * Used to get where to start draw the interface, this is calculated by the
	 * vehicle.
	 * 
	 * @return The x offset of the interface
	 */
	public int getX() {
		if (doStealInterface()) {
			return 0;
		} else {
			return offSetX;
		}
	}

	/**
	 * Used to get where to start draw the interface, this is calculated by the
	 * vehicle.
	 * 
	 * @return The y offset of the interface
	 */
	public int getY() {
		if (doStealInterface()) {
			return 0;
		} else {
			return offSetY;
		}
	}

	/**
	 * Used to set where the interface of this module starts, this is set by the
	 * vehicle
	 * 
	 * @param val
	 *            The x offset to use
	 */
	public void setX(int val) {
		offSetX = val;
	}

	/**
	 * Used to set where the interface of this module starts, this is set by the
	 * vehicle
	 * 
	 * @param val
	 *            The y offset to use
	 */
	public void setY(int val) {
		offSetY = val;
	}

	/**
	 * Returns the size this module wants to allocate in the interface. One
	 * shouldn't draw anything outside this area.
	 * 
	 * @return The width of the module's interface
	 */
	public int guiWidth() {
		return 15;
	}

	/**
	 * Returns the size this module wants to allocate in the interface. One
	 * shouldn't draw anything outside this area.
	 * 
	 * @return The height of the module's interface
	 */
	public int guiHeight() {
		return 27;
	}

	/**
	 * Called by the interface when the user has pressed a key on the keyboard
	 * 
	 * @param character
	 *            The character pressed
	 * @param extraInformation
	 *            Extra information of special keys
	 */
	public void keyPress(GuiVehicle gui, char character, int extraInformation) {
	}

	/**
	 * If the module should draw any foreground, it is done here.
	 * 
	 * @param gui
	 *            The GUI that will draw the interface
	 */
	@SideOnly(Side.CLIENT)
	public void drawForeground(GuiVehicle gui) {
	}

	/**
	 * Draws a one lined string in the center of the given rectangle. It will
	 * handle scrolling as well as module offset.
	 * 
	 * @param gui
	 *            The gui to draw it on.
	 * @param str
	 *            The string to be drawn.
	 * @param rect
	 *            The rectangle
	 * @param c
	 *            The color to be used
	 */
	@SideOnly(Side.CLIENT)
	public void drawString(GuiVehicle gui, String str, int[] rect, int c) {
		if (rect.length < 4) {
			return;
		} else {
			drawString(gui, str, rect[0] + (rect[2] - gui.getFontRenderer().getStringWidth(str)) / 2, rect[1] + (rect[3] - gui.getFontRenderer().FONT_HEIGHT + 3) / 2, c);
		}
	}

	/**
	 * Draws a string at the given location. It will handle scrolling as well as
	 * module offset.
	 * 
	 * @param gui
	 *            The gui to draw it on.
	 * @param str
	 *            The string to be draw
	 * @param x
	 *            The local x coordinate
	 * @param y
	 *            The local y coordinate
	 * @param c
	 *            The color to be used
	 */
	@SideOnly(Side.CLIENT)
	public void drawString(GuiVehicle gui, String str, int x, int y, int c) {
		drawString(gui, str, x, y, -1, false, c);
	}

	@SideOnly(Side.CLIENT)
	public void drawString(GuiVehicle gui, String str, int x, int y, int w, boolean center, int c) {
		int j = gui.getGuiLeft();
		int k = gui.getGuiTop();
		int[] rect = new int[] { x, y, w, 8 };
		boolean stealInterface = doStealInterface();
		int dif = 0;
		// scroll the bounding box
		if (!stealInterface) {
			dif = handleScroll(rect);
		}
		if (rect[3] > 0) {
			if (!stealInterface) {
				gui.setupAndStartScissor();
			}
			if (center) {
				gui.getFontRenderer().drawString(str, rect[0] + (rect[2] - gui.getFontRenderer().getStringWidth(str)) / 2 + getX(), rect[1] + getY() + dif, c);
			} else {
				gui.getFontRenderer().drawString(str, rect[0] + getX(), rect[1] + getY() + dif, c);
			}
			if (!stealInterface) {
				gui.stopScissor();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void drawScaledCenteredString(GuiVehicle gui, String str, int x, int y, int w, float multiplier, int color) {
		x -= gui.getGuiLeft();
		y -= gui.getGuiTop();
		GL11.glPushMatrix();
		GL11.glScalef(multiplier, multiplier, 1F);
		int width = gui.getFontRenderer().getStringWidth(str);
		x += (w - width * multiplier) / 2;
		x += getX();
		y += getY() - getVehicle().getRealScrollY();
		gui.setupAndStartScissor();
		gui.getFontRenderer().drawString(str, (int) ((x + gui.getGuiLeft()) / multiplier), (int) ((y + gui.getGuiTop()) / multiplier), color);
		gui.stopScissor();
		GL11.glPopMatrix();
	}

	@SideOnly(Side.CLIENT)
	public void drawStringWithShadow(GuiVehicle gui, String str, int x, int y, int c) {
		int j = gui.getGuiLeft();
		int k = gui.getGuiTop();
		int[] rect = new int[] { x, y, 0, 8 };
		// scroll the bounding box
		if (!doStealInterface()) {
			handleScroll(rect);
		}
		// just draw the text if the whole text can be drawn
		if (rect[3] == 8) {
			gui.getFontRenderer().drawStringWithShadow(str, rect[0] + getX(), rect[1] + getY(), c);
		}
	}

	/**
	 * Draws a multiline string at the given location. It will handle scrolling
	 * as well as module offset.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param str
	 *            The string to be drawn
	 * @param x
	 *            The local x coordinate
	 * @param y
	 *            The local y coordinate
	 * @param w
	 *            The maximum width of the text area
	 * @param c
	 *            The color to be used
	 */
	@SideOnly(Side.CLIENT)
	public void drawSplitString(GuiVehicle gui, String str, int x, int y, int w, int c) {
		drawSplitString(gui, str, x, y, w, false, c);
	}

	@SideOnly(Side.CLIENT)
	public void drawSplitString(GuiVehicle gui, String str, int x, int y, int w, boolean center, int c) {
		// split the string in multiple lines
		List newlines = gui.getFontRenderer().listFormattedStringToWidth(str, w);
		// loop through the lines and draw then using drawString
		for (int i = 0; i < newlines.size(); i++) {
			String line = newlines.get(i).toString();
			drawString(gui, line, x, y + i * 8, w, center, c);
		}
	}

	@SideOnly(Side.CLIENT)
	public void drawItemInInterface(GuiVehicle gui, ItemStack item, int x, int y) {
		int[] rect = new int[] { x, y, 16, 16 };
		int dif = handleScroll(rect);
		if (rect[3] > 0) {
			gui.setZLevel(100);
			gui.setupAndStartScissor();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			gui.drawItemStack(item, rect[0] + getX(), rect[1] + getY() + dif);
			gui.stopScissor();
			gui.setZLevel(0);
		}
	}

	/**
	 * Draw an image in the given interface, using the current texture and using
	 * the given dimensions.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param targetX
	 *            The local x coordinate to draw it on
	 * @param targetY
	 *            The local y coordinate to draw it on
	 * @param srcX
	 *            The x coordinate in the source file
	 * @param srcY
	 *            The y coordinate in the source file
	 * @param sizeX
	 *            The width of the image
	 * @param sizeY
	 *            The height of the image
	 */
	@SideOnly(Side.CLIENT)
	public void drawImage(GuiVehicle gui, int targetX, int targetY, int srcX, int srcY, int sizeX, int sizeY) {
		drawImage(gui, targetX, targetY, srcX, srcY, sizeX, sizeY, GuiBase.RenderRotation.NORMAL);
	}

	/**
	 * Draw an image in the given interface, using the current texture and using
	 * the given dimensions.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param targetX
	 *            The local x coordinate to draw it on
	 * @param targetY
	 *            The local y coordinate to draw it on
	 * @param srcX
	 *            The x coordinate in the source file
	 * @param srcY
	 *            The y coordinate in the source file
	 * @param sizeX
	 *            The width of the image
	 * @param sizeY
	 *            The height of the image
	 * @param rotation
	 *            The rotation this will be drawn with
	 */
	@SideOnly(Side.CLIENT)
	public void drawImage(GuiVehicle gui, int targetX, int targetY, int srcX, int srcY, int sizeX, int sizeY, GuiBase.RenderRotation rotation) {
		// create a rectangle and call the other drawImage function to do the
		// job
		drawImage(gui, new int[] { targetX, targetY, sizeX, sizeY }, srcX, srcY, rotation);
	}

	/**
	 * Draw an image in the given interface, using the current texture and using
	 * the given dimentiosn.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param rect
	 *            The rectangle indicating where to draw it {targetX, targetY,
	 *            sizeX, sizeY}
	 * @param srcX
	 *            The x coordinate in the source file
	 * @param srcY
	 *            They y coordinate in the source file
	 */
	@SideOnly(Side.CLIENT)
	public void drawImage(GuiVehicle gui, int[] rect, int srcX, int srcY) {
		drawImage(gui, rect, srcX, srcY, GuiBase.RenderRotation.NORMAL);
	}

	/**
	 * Draw an image in the given interface, using the current texture and using
	 * the given dimentiosn.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param rect
	 *            The rectangle indicating where to draw it {targetX, targetY,
	 *            sizeX, sizeY}
	 * @param srcX
	 *            The x coordinate in the source file
	 * @param srcY
	 *            They y coordinate in the source file
	 * @param rotation
	 *            The rotation this will be drawn with
	 */
	@SideOnly(Side.CLIENT)
	public void drawImage(GuiVehicle gui, int[] rect, int srcX, int srcY, GuiBase.RenderRotation rotation) {
		// the rectangle need to be valid
		if (rect.length < 4) {
			return;
		} else {
			// clones the rectangle and scroll the clone
			rect = cloneRect(rect);
			if (!doStealInterface()) {
				srcY -= handleScroll(rect);
			}
			// if there's still something to draw(that it's not scrolled outside
			// the screen)
			if (rect[3] > 0) {
				gui.drawRect(gui.getGuiLeft() + rect[0] + getX(), gui.getGuiTop() + rect[1] + getY(), srcX, srcY, rect[2], rect[3], rotation, textureSize);
			}
		}
	}

	/**
	 * Draw an icon in the given interface, using the current texture and using
	 * the given dimensions.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param icon
	 *            The Icon to draw
	 * @param targetX
	 *            The local x coordinate to draw it on
	 * @param targetY
	 *            The local y coordinate to draw it on
	 * @param srcX
	 *            The x coordinate in the source file
	 * @param srcY
	 *            The y coordinate in the source file
	 * @param sizeX
	 *            The width of the image
	 * @param sizeY
	 *            The height of the image
	 */
	// TODO: sprites
	/*
	 * @SideOnly(Side.CLIENT) public void drawImage(GuiVehicle gui, IIcon icon,
	 * int targetX, int targetY, int srcX, int srcY, int sizeX, int sizeY) { //
	 * create a rectangle and call the other drawImage function to do the // job
	 * drawImage(gui, icon, new int[] { targetX, targetY, sizeX, sizeY }, srcX,
	 * srcY); } /** Draw an image in the given interface, using the current
	 * texture and using the given dimentiosn.
	 * @param gui The gui to draw it on
	 * @param rect The rectangle indicating where to draw it {targetX, targetY,
	 * sizeX, sizeY}
	 * @param srcX The x coordinate in the source file
	 * @param srcY They y coordinate in the source file
	 * @SideOnly(Side.CLIENT) public void drawImage(GuiVehicle gui, IIcon icon,
	 * int[] rect, int srcX, int srcY) { // the rectangle need to be valid if
	 * (rect.length < 4) { return; } else { // clones the rectangle and scroll
	 * the clone rect = cloneRect(rect); if (!doStealInterface()) { srcY -=
	 * handleScroll(rect); } // if there's still something to draw(that it's not
	 * scrolled outside // the screen) if (rect[3] > 0) { gui.drawIcon(icon,
	 * gui.getGuiLeft() + rect[0] + getX(), gui.getGuiTop() + rect[1] + getY(),
	 * rect[2] / 16F, rect[3] / 16F, srcX / 16F, srcY / 16F); } } }
	 */
	/**
	 * Scrolls a given rectangle accordingly to the scrollbar in the interface
	 * 
	 * @param rect
	 *            The rectangle to scroll {targetX, targetY, sizeX, sizeY}
	 * @return The start offset caused by the scroll, i.e. if the middle part of
	 *         the rectangle is the topmost visible part. Used to change the
	 *         srcY when drawing images for instance, see drawImage.
	 */
	public int handleScroll(int rect[]) {
		// scroll the rectangle
		rect[1] -= getVehicle().getRealScrollY();
		// calculate the y val
		int y = rect[1] + getY();
		// if it's too far up
		if (y < 4) {
			int dif = (y - 4);
			rect[3] += dif;
			y = 4;
			rect[1] = y - getY();
			return dif;
			// if it's too far down
		} else if (y + rect[3] > stevesvehicles.common.vehicles.VehicleBase.MODULAR_SPACE_HEIGHT) {
			rect[3] = Math.max(0, stevesvehicles.common.vehicles.VehicleBase.MODULAR_SPACE_HEIGHT - y);
			return 0;
			// if the whole rectangle do fit
		} else {
			return 0;
		}
	}

	/**
	 * Clones a rectangle
	 * 
	 * @param rect
	 *            The rectangle to be clones {targetX, targetY, sizeX, sizeY}
	 * @return The cloned rectangle {targetX, targetY, sizeX, sizeY}
	 */
	private int[] cloneRect(int[] rect) {
		return new int[] { rect[0], rect[1], rect[2], rect[3] };
	}

	/**
	 * Whether the module is using client/server buttons. Currently not used
	 * 
	 * @return whether buttons are used or not
	 */
	public boolean useButtons() {
		return false;
	}

	/**
	 * Initializing any server/client buttons
	 */
	protected void loadButtons() {
	}

	/**
	 * Used to draw background for a module
	 * 
	 * @param gui
	 *            The gui to draw on
	 * @param x
	 *            The x coordinate of the mouse
	 * @param y
	 *            The y coordinate of the mouse
	 */
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiVehicle gui, int x, int y) {
	}

	@SideOnly(Side.CLIENT)
	public void drawBackgroundItems(GuiVehicle gui, int x, int y) {
	}

	/**
	 * Used to handle mouse clicks on the module's interface
	 * 
	 * @param gui
	 *            The gui that was clicked
	 * @param x
	 *            The x coordinate of the mouse
	 * @param y
	 *            The y coordinate of the mouse
	 * @param button
	 *            The button that was pressed on the mouse
	 */
	@SideOnly(Side.CLIENT)
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) {
	}

	/**
	 * Used to handle mouse movement and move releases in the module's interface
	 * 
	 * @param gui
	 *            The gui that is being used
	 * @param x
	 *            The x coordinate of the mouse
	 * @param y
	 *            The y coordinate of the mouse
	 * @param button
	 *            The button that was released, or -1 if the cursor is just
	 *            being moved
	 */
	@SideOnly(Side.CLIENT)
	public void mouseMovedOrUp(GuiVehicle gui, int x, int y, int button) {
	}

	/**
	 * Used to draw mouse over text for a module
	 * 
	 * @param gui
	 *            The gui to draw on
	 * @param x
	 *            The x coordinate of the mouse
	 * @param y
	 *            The y coordiante of the mouse
	 */
	@SideOnly(Side.CLIENT)
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
	}

	/**
	 * Detects if the given mouse coordinates are within the given rectangle
	 * 
	 * @param x
	 *            The mouse x coordinate
	 * @param y
	 *            The mouse y coordinate
	 * @param x1
	 *            The x coordinate of the rectangle
	 * @param y1
	 *            The y coordinate of the rectangle
	 * @param sizeX
	 *            The width of the rectangle
	 * @param sizeY
	 *            The height of the rectangle
	 * @return If the mouse was inside the rectangle
	 */
	protected boolean inRect(int x, int y, int x1, int y1, int sizeX, int sizeY) {
		// creates a rectangle and call the other inRect
		return inRect(x, y, new int[] { x1, y1, sizeX, sizeY });
	}

	/**
	 * Detects if the given mouse coordinates are within the given rectangle
	 * 
	 * @param x
	 *            The mouse x coordinate
	 * @param y
	 *            The mouse y coordinate
	 * @param rect
	 *            The rectangle to check for {x,y,width, height}
	 * @return If the mouse was inside the rectangle
	 */
	public boolean inRect(int x, int y, int[] rect) {
		// check if we have a valid rectangle
		if (rect.length < 4) {
			return false;
		} else {
			// clone the rectangle and scroll that clone
			rect = cloneRect(rect);
			if (!doStealInterface()) {
				handleScroll(rect);
			}
			// check if the mouse is inside the scrolled rectangle
			return x >= rect[0] && x <= rect[0] + rect[2] && y >= rect[1] && y <= rect[1] + rect[3];
		}
	}

	/**
	 * Draw a specific mouse over string if the mouse is in a specific rectangle
	 * 
	 * @param gui
	 *            The gui to draw on
	 * @param str
	 *            The string to be drawn
	 * @param x
	 *            The x coordinate of the mouse
	 * @param y
	 *            the y coordinate of the mouse
	 * @param x1
	 *            The x coordinate of the rectangle
	 * @param y1
	 *            The y coordinate of the rectangle
	 * @param w
	 *            The width of the rectangle
	 * @param h
	 *            The height of the rectangle
	 */
	@SideOnly(Side.CLIENT)
	public final void drawStringOnMouseOver(GuiVehicle gui, String str, int x, int y, int x1, int y1, int w, int h) {
		// creates a rectangle and calls the other drawStringOnMouseOver
		drawStringOnMouseOver(gui, str, x, y, new int[] { x1, y1, w, h });
	}

	/**
	 * Draw a specific mouse over string if the mouse is in a specific rectangle
	 * 
	 * @param gui
	 *            The gui to draw on
	 * @param str
	 *            The string to be drawn
	 * @param x
	 *            The x coordinate of the mouse
	 * @param y
	 *            The y coordinate of the mouse
	 * @param rect
	 *            The rectangle that the mouse has to be in, defin as
	 *            {x,y,width,height}
	 */
	@SideOnly(Side.CLIENT)
	public final void drawStringOnMouseOver(GuiVehicle gui, String str, int x, int y, int[] rect) {
		// if it's not in the rectangle the text shouldn't be written
		if (!inRect(x, y, rect)) {
			return;
		}
		// convert to global coordinates
		x += getX();
		y += getY();
		// draw the mouse overlay
		gui.drawMouseOver(str, x, y);
	}

	@SideOnly(Side.CLIENT)
	public final void drawStringOnMouseOver(GuiVehicle gui, String str, int x, int y) {
		// convert to global coordinates
		x += getX();
		y += getY();
		// draw the mouse overlay
		gui.drawMouseOver(str, x, y);
	}

	@SideOnly(Side.CLIENT)
	public final void drawStringOnMouseOver(GuiVehicle gui, List<String> info, int x, int y) {
		// convert to global coordinates
		x += getX();
		y += getY();
		// draw the mouse overlay
		gui.drawMouseOver(info, x, y);
	}

	/**
	 * Draws an image overlay on the screen. Observe that this is not when a
	 * special interface is open.
	 * 
	 * @param rect
	 *            The rectangle for the image's dimensions {targetX, targetY,
	 *            width, height}
	 * @param sourceX
	 *            The x coordinate in the source file
	 * @param sourceY
	 *            The y coordinate in the source file
	 */
	protected void drawImage(int[] rect, int sourceX, int sourceY) {
		drawImage(rect[0], rect[1], sourceX, sourceY, rect[2], rect[3]);
	}

	/**
	 * Draws an image overlay on the screen. Observe that this is not when a
	 * special interface is open.
	 * 
	 * @param targetX
	 *            The x coordinate of the image
	 * @param targetY
	 *            The y coordinate of the image
	 * @param sourceX
	 *            The x coordinate in the source file
	 * @param sourceY
	 *            The y coordinate in the source file
	 * @param width
	 *            The width of the image
	 * @param height
	 *            The height of the image
	 */
	protected void drawImage(int targetX, int targetY, int sourceX, int sourceY, int width, int height) {
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		final Tessellator tess = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tess.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(targetX + 0, targetY + height, -90.0).tex((sourceX + 0) * var7, (sourceY + height) * var8).endVertex();
		vertexbuffer.pos(targetX + width, targetY + height, -90.0).tex((sourceX + width) * var7, (sourceY + height) * var8).endVertex();
		vertexbuffer.pos(targetX + width, targetY + 0, -90.0).tex((sourceX + width) * var7, (sourceY + 0) * var8).endVertex();
		vertexbuffer.pos(targetX + 0, targetY + 0, -90.0).tex((sourceX + 0) * var7, (sourceY + 0) * var8).endVertex();
		tess.draw();
	}

	/**
	 * Gets the player using the client. Used for example to check if a player
	 * is the active player.
	 * 
	 * @return The palyer
	 */
	@SideOnly(Side.CLIENT)
	protected EntityPlayer getClientPlayer() {
		if (net.minecraft.client.Minecraft.getMinecraft() != null) {
			return net.minecraft.client.Minecraft.getMinecraft().player;
		}
		return null;
	}

	/**
	 * Allows a module to steal the whole interface, preventing any other module
	 * from using the interface. This is not meant to be permanent, use it when
	 * a lot of interface is required, then when the user clicks on something to
	 * close it then return false again.
	 * 
	 * @return
	 */
	public boolean doStealInterface() {
		return false;
	}

	private static final int DEFAULT_TEXTURE_SIZE = 256;
	private int textureSize = DEFAULT_TEXTURE_SIZE;

	public void setTextureSize(int val) {
		this.textureSize = val;
	}

	public void resetTextureSize() {
		setTextureSize(DEFAULT_TEXTURE_SIZE);
	}

	private static final ResourceLocation TOGGLE_TEXTURE = ResourceHelper.getResource("/gui/toggle_base.png");
	private static final int TEXTURE_SPACING = 1;
	private static final int TOGGLE_IMAGE_BORDER_SRC_X = 1;
	private static final int TOGGLE_IMAGE_BORDER_SRC_Y = 19;
	protected static final int[] TOGGLE_BOX_RECT = new int[] { 10, 21, 8, 8 };
	protected static final int[] TOGGLE_IMAGE_RECT = new int[] { 20, 16, 18, 18 };
	private ResourceLocation toggleImageTexture;

	@SideOnly(Side.CLIENT)
	protected void drawToggleBox(GuiVehicle gui, String texture, boolean enabled, int x, int y) {
		if (toggleImageTexture == null) {
			toggleImageTexture = ResourceHelper.getResource("/gui/toggle/" + texture + ".png");
		}
		ResourceHelper.bindResource(TOGGLE_TEXTURE);
		int backgroundId = enabled ? 1 : 0;
		int borderID = inRect(x, y, TOGGLE_BOX_RECT) ? 1 : 0;
		ResourceHelper.bindResource(toggleImageTexture);
		setTextureSize(16);
		drawImage(gui, TOGGLE_IMAGE_RECT[0] + 1, TOGGLE_IMAGE_RECT[1] + 1, 0, 0, TOGGLE_IMAGE_RECT[2] - 2, TOGGLE_IMAGE_RECT[3] - 2);
		resetTextureSize();
		ResourceHelper.bindResource(TOGGLE_TEXTURE);
		drawImage(gui, TOGGLE_IMAGE_RECT, TOGGLE_IMAGE_BORDER_SRC_X, TOGGLE_IMAGE_BORDER_SRC_Y + (enabled ? 0 : TEXTURE_SPACING + TOGGLE_IMAGE_RECT[3]));
		drawImage(gui, TOGGLE_BOX_RECT, TEXTURE_SPACING + (TEXTURE_SPACING + TOGGLE_BOX_RECT[2]) * backgroundId, TEXTURE_SPACING + (TEXTURE_SPACING + TOGGLE_BOX_RECT[3]) * borderID);
	}
}