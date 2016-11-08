package vswe.stevescarts.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.PacketHandler;
import vswe.stevescarts.containers.ContainerMinecart;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.guis.buttons.ButtonBase;
import vswe.stevescarts.helpers.CompButtons;
import vswe.stevescarts.helpers.NBTHelper;
import vswe.stevescarts.helpers.SimulationInfo;
import vswe.stevescarts.models.ModelCartbase;
import vswe.stevescarts.modules.data.ModuleData;

public abstract class ModuleBase {
	private EntityMinecartModular cart;
	private ItemStack[] cargo;
	private int offSetX;
	private int offSetY;
	private int guiDataOffset;
	private int dataWatcherOffset;
	private int packetOffset;
	private ArrayList<ButtonBase> buttons;
	private CompButtons buttonSorter;
	protected int slotGlobalStart;
	private byte moduleId;
	private ArrayList<ModelCartbase> models;
	protected ArrayList<SlotBase> slotList;
	private int moduleButtonId;

	public ModuleBase(final EntityMinecartModular cart) {
		this.moduleButtonId = 0;
		this.cart = cart;
		this.cargo = new ItemStack[this.getInventorySize()];
	}

	public void init() {
		if (this.useButtons()) {
			this.buttons = new ArrayList<ButtonBase>();
			this.buttonSorter = new CompButtons();
			this.loadButtons();
			this.buttonVisibilityChanged();
		}
	}

	public void preInit() {
	}

	public EntityMinecartModular getCart() {
		return this.cart;
	}

	public boolean isPlaceholder() {
		return this.getCart().isPlaceholder;
	}

	protected SimulationInfo getSimInfo() {
		return this.getCart().placeholderAsssembler.getSimulationInfo();
	}

	public void setModuleId(final byte val) {
		this.moduleId = val;
	}

	public byte getModuleId() {
		return this.moduleId;
	}

	public void onInventoryChanged() {
	}

	public int getX() {
		if (this.doStealInterface()) {
			return 0;
		}
		return this.offSetX;
	}

	public int getY() {
		if (this.doStealInterface()) {
			return 0;
		}
		return this.offSetY;
	}

	public void setX(final int val) {
		this.offSetX = val;
	}

	public void setY(final int val) {
		this.offSetY = val;
	}

	public int getInventorySize() {
		if (!this.hasSlots()) {
			return 0;
		}
		return this.getInventoryWidth() * this.getInventoryHeight();
	}

	public int guiWidth() {
		return 15 + this.getInventoryWidth() * 18;
	}

	public int guiHeight() {
		return 27 + this.getInventoryHeight() * 18;
	}

	protected int getInventoryWidth() {
		return 3;
	}

	protected int getInventoryHeight() {
		return 1;
	}

	public void keyPress(final GuiMinecart gui, final char character, final int extraInformation) {
	}

	public ArrayList<SlotBase> getSlots() {
		return this.slotList;
	}

	public int generateSlots(int slotCount) {
		this.slotGlobalStart = slotCount;
		this.slotList = new ArrayList<SlotBase>();
		for (int j = 0; j < this.getInventoryHeight(); ++j) {
			for (int i = 0; i < this.getInventoryWidth(); ++i) {
				this.slotList.add(this.getSlot(slotCount++, i, j));
			}
		}
		return slotCount;
	}

	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return null;
	}

	public boolean hasSlots() {
		return this.hasGui();
	}

	public void update() {
	}

	public boolean hasFuel(final int consumption) {
		return false;
	}

	public float getMaxSpeed() {
		return 1.1f;
	}

	public int getYTarget() {
		return -1;
	}

	public void moveMinecartOnRail(BlockPos pos) {
	}

	public ItemStack getStack(final int slot) {
		return this.cargo[slot];
	}

	public void setStack(final int slot, final ItemStack item) {
		this.cargo[slot] = item;
	}

	public void addStack(final int slotStart, final int slotEnd, final ItemStack item) {
		this.getCart().addItemToChest(item, this.slotGlobalStart + slotStart, this.slotGlobalStart + slotEnd);
	}

	public void addStack(final int slot, final ItemStack item) {
		this.addStack(slot, slot, item);
	}

	public boolean dropOnDeath() {
		return true;
	}

	public void onDeath() {
	}

	public boolean hasGui() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void drawForeground(final GuiMinecart gui) {
	}

	@SideOnly(Side.CLIENT)
	public void drawString(final GuiMinecart gui, final String str, final int[] rect, final int c) {
		if (rect.length < 4) {
			return;
		}
		this.drawString(gui, str, rect[0] + (rect[2] - gui.getFontRenderer().getStringWidth(str)) / 2, rect[1] + (rect[3] - gui.getFontRenderer().FONT_HEIGHT + 3) / 2, c);
	}

	@SideOnly(Side.CLIENT)
	public void drawString(final GuiMinecart gui, final String str, final int x, final int y, final int c) {
		this.drawString(gui, str, x, y, -1, false, c);
	}

	@SideOnly(Side.CLIENT)
	public void drawString(final GuiMinecart gui, final String str, final int x, final int y, final int w, final boolean center, final int c) {
		final int j = gui.getGuiLeft();
		final int k = gui.getGuiTop();
		final int[] rect = { x, y, w, 8 };
		if (!this.doStealInterface()) {
			this.handleScroll(rect);
		}
		if (rect[3] == 8) {
			if (center) {
				gui.getFontRenderer().drawString(str, rect[0] + (rect[2] - gui.getFontRenderer().getStringWidth(str)) / 2 + this.getX(), rect[1] + this.getY(), c);
			} else {
				gui.getFontRenderer().drawString(str, rect[0] + this.getX(), rect[1] + this.getY(), c);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void drawStringWithShadow(final GuiMinecart gui, final String str, final int x, final int y, final int c) {
		final int j = gui.getGuiLeft();
		final int k = gui.getGuiTop();
		final int[] rect = { x, y, 0, 8 };
		if (!this.doStealInterface()) {
			this.handleScroll(rect);
		}
		if (rect[3] == 8) {
			gui.getFontRenderer().drawStringWithShadow(str, rect[0] + this.getX(), rect[1] + this.getY(), c);
		}
	}

	@SideOnly(Side.CLIENT)
	public void drawSplitString(final GuiMinecart gui, final String str, final int x, final int y, final int w, final int c) {
		this.drawSplitString(gui, str, x, y, w, false, c);
	}

	@SideOnly(Side.CLIENT)
	public void drawSplitString(final GuiMinecart gui, final String str, final int x, final int y, final int w, final boolean center, final int c) {
		final List newlines = gui.getFontRenderer().listFormattedStringToWidth(str, w);
		for (int i = 0; i < newlines.size(); ++i) {
			final String line = newlines.get(i).toString();
			this.drawString(gui, line, x, y + i * 8, w, center, c);
		}
	}

	public void drawItemInInterface(final GuiMinecart gui, final ItemStack item, final int x, final int y) {
		final int[] rect = { x, y, 16, 16 };
		this.handleScroll(rect);
		if (rect[3] == 16) {
			final RenderItem renderitem = Minecraft.getMinecraft().getRenderItem();
			GL11.glDisable(2896);
			renderitem.renderItemIntoGUI(item, gui.getGuiLeft() + rect[0] + this.getX(), gui.getGuiTop() + rect[1] + this.getY());
			GL11.glEnable(2896);
		}
	}

	@SideOnly(Side.CLIENT)
	public void drawImage(final GuiMinecart gui, final int targetX, final int targetY, final int srcX, final int srcY, final int sizeX, final int sizeY) {
		this.drawImage(gui, targetX, targetY, srcX, srcY, sizeX, sizeY, GuiBase.RENDER_ROTATION.NORMAL);
	}

	@SideOnly(Side.CLIENT)
	public void drawImage(final GuiMinecart gui, final int targetX, final int targetY, final int srcX, final int srcY, final int sizeX, final int sizeY, final GuiBase.RENDER_ROTATION rotation) {
		this.drawImage(gui, new int[] { targetX, targetY, sizeX, sizeY }, srcX, srcY, rotation);
	}

	@SideOnly(Side.CLIENT)
	public void drawImage(final GuiMinecart gui, final int[] rect, final int srcX, final int srcY) {
		this.drawImage(gui, rect, srcX, srcY, GuiBase.RENDER_ROTATION.NORMAL);
	}

	@SideOnly(Side.CLIENT)
	public void drawImage(final GuiMinecart gui, int[] rect, final int srcX, int srcY, final GuiBase.RENDER_ROTATION rotation) {
		if (rect.length < 4) {
			return;
		}
		rect = this.cloneRect(rect);
		if (!this.doStealInterface()) {
			srcY -= this.handleScroll(rect);
		}
		if (rect[3] > 0) {
			gui.drawTexturedModalRect(gui.getGuiLeft() + rect[0] + this.getX(), gui.getGuiTop() + rect[1] + this.getY(), srcX, srcY, rect[2], rect[3], rotation);
		}
	}

	//	@SideOnly(Side.CLIENT)
	//	public void drawImage(final GuiMinecart gui, final IIcon icon, final int targetX, final int targetY, final int srcX, final int srcY, final int sizeX, final int sizeY) {
	//		this.drawImage(gui, icon, new int[] { targetX, targetY, sizeX, sizeY }, srcX, srcY);
	//	}
	//
	//	@SideOnly(Side.CLIENT)
	//	public void drawImage(final GuiMinecart gui, final IIcon icon, int[] rect, final int srcX, int srcY) {
	//		if (rect.length < 4) {
	//			return;
	//		}
	//		rect = this.cloneRect(rect);
	//		if (!this.doStealInterface()) {
	//			srcY -= this.handleScroll(rect);
	//		}
	//		if (rect[3] > 0) {
	//			gui.drawIcon(icon, gui.getGuiLeft() + rect[0] + this.getX(), gui.getGuiTop() + rect[1] + this.getY(), rect[2] / 16.0f, rect[3] / 16.0f, srcX / 16.0f, srcY / 16.0f);
	//		}
	//	}

	public int handleScroll(final int[] rect) {
		final int n = 1;
		rect[n] -= this.getCart().getRealScrollY();
		int y = rect[1] + this.getY();
		if (y < 4) {
			final int dif = y - 4;
			final int n2 = 3;
			rect[n2] += dif;
			y = 4;
			rect[1] = y - this.getY();
			return dif;
		}
		if (y + rect[3] > 168) {
			rect[3] = Math.max(0, 168 - y);
			return 0;
		}
		return 0;
	}

	private int[] cloneRect(final int[] rect) {
		return new int[] { rect[0], rect[1], rect[2], rect[3] };
	}

	public boolean useButtons() {
		return false;
	}

	public final void buttonVisibilityChanged() {
		Collections.sort(this.buttons, this.buttonSorter);
		ButtonBase.LOCATION lastLoc = null;
		int id = 0;
		for (final ButtonBase button : this.buttons) {
			if (button.isVisible()) {
				if (lastLoc != null && button.getLocation() != lastLoc) {
					id = 0;
				}
				lastLoc = button.getLocation();
				button.setCurrentID(id);
				++id;
			}
		}
	}

	public RAILDIRECTION getSpecialRailDirection(BlockPos pos) {
		return RAILDIRECTION.DEFAULT;
	}

	protected void loadButtons() {
	}

	public final void addButton(final ButtonBase button) {
		button.setIdInModule(this.moduleButtonId++);
		this.buttons.add(button);
	}

	public String generateNBTName(final String name, final int id) {
		return "module" + id + name;
	}

	public final void writeToNBT(final NBTTagCompound tagCompound, final int id) {
		if (this.getInventorySize() > 0) {
			final NBTTagList items = new NBTTagList();
			for (int i = 0; i < this.getInventorySize(); ++i) {
				if (this.getStack(i) != null) {
					final NBTTagCompound item = new NBTTagCompound();
					item.setByte("Slot", (byte) i);
					this.getStack(i).writeToNBT(item);
					items.appendTag(item);
				}
			}
			tagCompound.setTag(this.generateNBTName("Items", id), items);
		}
		this.Save(tagCompound, id);
	}

	protected void Save(final NBTTagCompound tagCompound, final int id) {
	}

	public final void readFromNBT(final NBTTagCompound tagCompound, final int id) {
		if (this.getInventorySize() > 0) {
			final NBTTagList items = tagCompound.getTagList(this.generateNBTName("Items", id), NBTHelper.COMPOUND.getId());
			for (int i = 0; i < items.tagCount(); ++i) {
				final NBTTagCompound item = items.getCompoundTagAt(i);
				final int slot = item.getByte("Slot") & 0xFF;
				if (slot >= 0 && slot < this.getInventorySize()) {
					this.setStack(slot, ItemStack.loadItemStackFromNBT(item));
				}
			}
		}
		this.Load(tagCompound, id);
	}

	protected void Load(final NBTTagCompound tagCompound, final int id) {
	}

	@SideOnly(Side.CLIENT)
	public final void drawButtonText(final GuiMinecart gui) {
		for (final ButtonBase button : this.buttons) {
			button.drawButtonText(gui, this);
		}
	}

	@SideOnly(Side.CLIENT)
	public final void drawButtons(final GuiMinecart gui, final int x, final int y) {
		for (final ButtonBase button : this.buttons) {
			button.drawButton(gui, this, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public final void drawButtonOverlays(final GuiMinecart gui, final int x, final int y) {
		for (final ButtonBase button : this.buttons) {
			if (button.isVisible()) {
				this.drawStringOnMouseOver(gui, button.toString(), x, y, button.getBounds());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public final void mouseClickedButton(final GuiMinecart gui, final int x, final int y, final int mousebutton) {
		for (final ButtonBase button : this.buttons) {
			if (this.inRect(x, y, button.getBounds())) {
				button.computeOnClick(gui, mousebutton);
			}
		}
	}

	public void sendButtonPacket(final ButtonBase button, final byte clickinfo) {
		final byte id = (byte) button.getIdInModule();
		System.out.println("Sent button " + button.getIdInModule());
		this.sendPacket(this.totalNumberOfPackets() - 1, new byte[] { id, clickinfo });
	}

	@SideOnly(Side.CLIENT)
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
	}

	@SideOnly(Side.CLIENT)
	public void drawBackgroundItems(final GuiMinecart gui, final int x, final int y) {
	}

	@SideOnly(Side.CLIENT)
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
	}

	@SideOnly(Side.CLIENT)
	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
	}

	@SideOnly(Side.CLIENT)
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
	}

	protected boolean inRect(final int x, final int y, final int x1, final int y1, final int sizeX, final int sizeY) {
		return this.inRect(x, y, new int[] { x1, y1, sizeX, sizeY });
	}

	public boolean inRect(final int x, final int y, int[] rect) {
		if (rect.length < 4) {
			return false;
		}
		rect = this.cloneRect(rect);
		if (!this.doStealInterface()) {
			this.handleScroll(rect);
		}
		return x >= rect[0] && x <= rect[0] + rect[2] && y >= rect[1] && y <= rect[1] + rect[3];
	}

	public boolean receiveDamage(final DamageSource source, final float val) {
		return true;
	}

	protected void turnback() {
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module != this && module.preventTurnback()) {
				return;
			}
		}
		this.getCart().turnback();
	}

	protected boolean preventTurnback() {
		return false;
	}

	public final int totalNumberOfPackets() {
		return this.numberOfPackets() + (this.useButtons() ? 1 : 0);
	}

	protected int numberOfPackets() {
		return 0;
	}

	public int getPacketStart() {
		return this.packetOffset;
	}

	public void setPacketStart(final int val) {
		this.packetOffset = val;
	}

	protected void sendPacket(final int id) {
		this.sendPacket(id, new byte[0]);
	}

	public void sendPacket(final int id, final byte data) {
		this.sendPacket(id, new byte[] { data });
	}

	public void sendPacket(final int id, final byte[] data) {
		PacketHandler.sendPacket(this.getPacketStart() + id, data);
	}

	protected void sendPacket(final int id, final EntityPlayer player) {
		this.sendPacket(id, new byte[0], player);
	}

	protected void sendPacket(final int id, final byte data, final EntityPlayer player) {
		this.sendPacket(id, new byte[] { data }, player);
	}

	protected void sendPacket(final int id, final byte[] data, final EntityPlayer player) {
		PacketHandler.sendPacketToPlayer(this.getPacketStart() + id, data, player, this.getCart());
	}

	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
	}

	public final void delegateReceivedPacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id < 0 || id >= this.totalNumberOfPackets()) {
			return;
		}
		if (id == this.totalNumberOfPackets() - 1 && this.useButtons()) {
			int buttonId = data[0];
			if (buttonId < 0) {
				buttonId += 256;
			}
			System.out.println("Received button " + buttonId);
			for (final ButtonBase button : this.buttons) {
				if (button.getIdInModule() == buttonId) {
					final byte buttoninformation = data[1];
					final boolean isCtrlDown = (buttoninformation & 0x40) != 0x0;
					final boolean isShiftDown = (buttoninformation & 0x80) != 0x0;
					final int mousebutton = buttoninformation & 0x3F;
					if (button.isVisible() && button.isEnabled()) {
						button.onServerClick(player, mousebutton, isCtrlDown, isShiftDown);
						break;
					}
					break;
				}
			}
		} else {
			this.receivePacket(id, data, player);
		}
	}

	public int numberOfDataWatchers() {
		return 0;
	}

	public int getDataWatcherStart() {
		return this.dataWatcherOffset;
	}

	public void setDataWatcherStart(final int val) {
		this.dataWatcherOffset = val;
	}

	public void initDw() {
	}

	private int getDwId(int id) {
		id += 2 + this.getDataWatcherStart();
		if (id >= 16) {
			id += 7;
		}
		return id;
	}

	protected final <T> void registerDw(DataParameter<T> key, T value){
		this.getCart().getDataManager().register(key, value);
	}

	protected final <T> void updateDw(DataParameter<T> key, T value){
		this.getCart().getDataManager().set(key, value);
	}

	protected <T> T getDw(DataParameter<T> key){
		return this.getCart().getDataManager().get(key);
	}

	protected static <T> DataParameter<T> createDw(DataSerializer<T> serializer){
		return EntityDataManager.createKey(EntityMinecartModular.class, serializer);
	}

	public int numberOfGuiData() {
		return 0;
	}

	public int getGuiDataStart() {
		return this.guiDataOffset;
	}

	public void setGuiDataStart(final int val) {
		this.guiDataOffset = val;
	}

	private final void updateGuiData(final Container con, final List players, final int id, final short data) {
		//		for (final IContainerListener player : players) {
		//			player.sendProgressBarUpdate(con, id, (int) data);
		//		}
	}

	public final void updateGuiData(final Object[] info, final int id, final short data) {
		final ContainerMinecart con = (ContainerMinecart) info[0];
		if (con == null) {
			return;
		}
		final int globalId = id + this.getGuiDataStart();
		final List players = (List) info[1];
		boolean flag;
		//		final boolean isNew = flag = (boolean) info[2];
		//		if (!flag) {
		//			if (con.cache != null) {
		//				final Short val = con.cache.get((short) globalId);
		//				flag = (val == null || val != data);
		//			} else {
		//				flag = true;
		//			}
		//		}
		//		if (flag) {
		//			if (con.cache == null) {
		//				con.cache = new HashMap<Short, Short>();
		//			}
		//			this.updateGuiData(con, players, globalId, data);
		//			con.cache.put((short) globalId, data);
		//		}
	}

	public final void initGuiData(final Container con, final IContainerListener player) {
		final ArrayList players = new ArrayList();
		players.add(player);
		this.checkGuiData(con, players, true);
	}

	protected void checkGuiData(final Object[] info) {
	}

	public final void checkGuiData(final Container con, final List players, final boolean isNew) {
		if (con == null) {
			return;
		}
		this.checkGuiData(new Object[] { con, players, isNew });
	}

	public void receiveGuiData(final int id, final short data) {
	}

	public int getConsumption(final boolean isMoving) {
		return 0;
	}

	public void setModels(final ArrayList<ModelCartbase> models) {
		this.models = models;
	}

	public ArrayList<ModelCartbase> getModels() {
		return this.models;
	}

	public boolean haveModels() {
		return this.models != null;
	}

	@SideOnly(Side.CLIENT)
	public final void drawStringOnMouseOver(final GuiMinecart gui, final String str, final int x, final int y, final int x1, final int y1, final int w, final int h) {
		this.drawStringOnMouseOver(gui, str, x, y, new int[] { x1, y1, w, h });
	}

	@SideOnly(Side.CLIENT)
	public final void drawStringOnMouseOver(final GuiMinecart gui, final String str, int x, int y, final int[] rect) {
		if (!this.inRect(x, y, rect)) {
			return;
		}
		x += this.getX();
		y += this.getY();
		gui.drawMouseOver(str, x, y);
	}

	protected void drawImage(final int[] rect, final int sourceX, final int sourceY) {
		this.drawImage(rect[0], rect[1], sourceX, sourceY, rect[2], rect[3]);
	}

	protected void drawImage(final int targetX, final int targetY, final int sourceX, final int sourceY, final int width, final int height) {
		final float var7 = 0.00390625f;
		final float var8 = 0.00390625f;
		final Tessellator tess = Tessellator.getInstance();
		//		tess.startDrawingQuads();
		//		tess.addVertexWithUV((double) (targetX + 0), (double) (targetY + height), -90.0, (double) ((sourceX + 0) * var7), (double) ((sourceY + height) * var8));
		//		tess.addVertexWithUV((double) (targetX + width), (double) (targetY + height), -90.0, (double) ((sourceX + width) * var7), (double) ((sourceY + height) * var8));
		//		tess.addVertexWithUV((double) (targetX + width), (double) (targetY + 0), -90.0, (double) ((sourceX + width) * var7), (double) ((sourceY + 0) * var8));
		//		tess.addVertexWithUV((double) (targetX + 0), (double) (targetY + 0), -90.0, (double) ((sourceX + 0) * var7), (double) ((sourceY + 0) * var8));
		tess.draw();
	}

	@SideOnly(Side.CLIENT)
	protected EntityPlayer getClientPlayer() {
		if (Minecraft.getMinecraft() != null) {
			return Minecraft.getMinecraft().thePlayer;
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public void renderOverlay(final Minecraft minecraft) {
	}

	public boolean stopEngines() {
		return false;
	}

	public boolean shouldCartRender() {
		return true;
	}

	public double getPushFactor() {
		return -1.0;
	}

	public float[] getColor() {
		return new float[] { 1.0f, 1.0f, 1.0f };
	}

	public float mountedOffset(final Entity rider) {
		return 0.0f;
	}

	protected boolean countsAsAir(final BlockPos pos) {
		if (this.getCart().worldObj.isAirBlock(pos)) {
			return true;
		}
		final IBlockState b = this.getCart().worldObj.getBlockState(pos);
		return b instanceof BlockSnow || b instanceof BlockFlower || b instanceof BlockVine;
	}

	public void activatedByRail(final int x, final int y, final int z, final boolean active) {
	}

	public ModuleData getData() {
		return ModuleData.getList().get(this.getModuleId());
	}

	public boolean doStealInterface() {
		return false;
	}

	public boolean hasExtraData() {
		return false;
	}

	public byte getExtraData() {
		return 0;
	}

	public void setExtraData(final byte b) {
	}

	protected FakePlayer getFakePlayer() {
		return FakePlayerFactory.getMinecraft((WorldServer) this.getCart().worldObj);
	}

	public boolean disableStandardKeyFunctionality() {
		return false;
	}

	public void addToLabel(final ArrayList<String> label) {
	}

	public boolean onInteractFirst(final EntityPlayer entityplayer) {
		return false;
	}

	public void postUpdate() {
	}

	public String getModuleName() {
		return ModuleData.getList().get(this.getModuleId()).getName();
	}

	public enum RAILDIRECTION {
		DEFAULT,
		NORTH,
		WEST,
		SOUTH,
		EAST,
		LEFT,
		FORWARD,
		RIGHT
	}
}
