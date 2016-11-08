package vswe.stevescarts.Modules.Realtimers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.Engines.ModuleEngine;
import vswe.stevescarts.Modules.ILeverModule;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.PacketHandler;

public class ModuleAdvControl extends ModuleBase implements ILeverModule {
	private byte[] engineInformation;
	private int tripPacketTimer;
	private int enginePacketTimer;
	private byte keyinformation;
	private double lastPosX;
	private double lastPosY;
	private double lastPosZ;
	private boolean first;
	private int speedChangeCooldown;
	private boolean lastBackKey;
	private double odo;
	private double trip;
	private int[] buttonRect;

	public ModuleAdvControl(final MinecartModular cart) {
		super(cart);
		this.first = true;
		this.buttonRect = new int[] { 15, 20, 24, 12 };
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public int guiWidth() {
		return 90;
	}

	@Override
	public int guiHeight() {
		return 35;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderOverlay(final Minecraft minecraft) {
		ResourceHelper.bindResource("/gui/drive.png");
		if (this.engineInformation != null) {
			for (int i = 0; i < this.getCart().getEngines().size(); ++i) {
				this.drawImage(5, i * 15, 0, 0, 66, 15);
				final int upperBarLength = this.engineInformation[i * 2] & 0x3F;
				final int lowerBarLength = this.engineInformation[i * 2 + 1] & 0x3F;
				final ModuleEngine engine = this.getCart().getEngines().get(i);
				final float[] rgb = engine.getGuiBarColor();
				GL11.glColor4f(rgb[0], rgb[1], rgb[2], 1.0f);
				this.drawImage(7, i * 15 + 2, 66, 0, upperBarLength, 5);
				this.drawImage(7, i * 15 + 2 + 6, 66, 6, lowerBarLength, 5);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				this.drawImage(5, i * 15, 66 + engine.getPriority() * 7, 11, 7, 15);
			}
		}
		final int enginesEndAt = this.getCart().getEngines().size() * 15;
		this.drawImage(5, enginesEndAt, 0, 15, 32, 32);
		if (minecraft.gameSettings.keyBindForward.isPressed()) {
			this.drawImage(15, enginesEndAt + 5, 42, 20, 12, 6);
		} else if (minecraft.gameSettings.keyBindLeft.isPressed()) {
			this.drawImage(7, enginesEndAt + 13, 34, 28, 6, 12);
		} else if (minecraft.gameSettings.keyBindRight.isPressed()) {
			this.drawImage(29, enginesEndAt + 13, 56, 28, 6, 12);
		}
		final int speedGraphicHeight = this.getSpeedSetting() * 2;
		this.drawImage(14, enginesEndAt + 13 + 12 - speedGraphicHeight, 41, 40 - speedGraphicHeight, 14, speedGraphicHeight);
		this.drawImage(0, 0, 0, 67, 5, 130);
		this.drawImage(1, 1 + (256 - this.getCart().y()) / 2, 5, 67, 5, 1);
		this.drawImage(5, enginesEndAt + 32, 0, 47, 32, 20);
		this.drawImage(5, enginesEndAt + 52, 0, 47, 32, 20);
		this.drawImage(5, enginesEndAt + 72, 0, 47, 32, 20);
		minecraft.fontRendererObj.drawString(Localization.MODULES.ATTACHMENTS.ODO.translate(), 7, enginesEndAt + 52 + 2, 4210752);
		minecraft.fontRendererObj.drawString(this.distToString(this.odo), 7, enginesEndAt + 52 + 11, 4210752);
		minecraft.fontRendererObj.drawString(Localization.MODULES.ATTACHMENTS.TRIP.translate(), 7, enginesEndAt + 52 + 22, 4210752);
		minecraft.fontRendererObj.drawString(this.distToString(this.trip), 7, enginesEndAt + 52 + 31, 4210752);
		//TODO
		//final RenderItem itemRenderer = new RenderItem();
	//	itemRenderer.renderItemIntoGUI(minecraft.fontRendererObj, minecraft.renderEngine, new ItemStack(Items.CLOCK, 1), 5, enginesEndAt + 32 + 3);
	//	itemRenderer.renderItemIntoGUI(minecraft.fontRendererObj, minecraft.renderEngine, new ItemStack(Items.COMPASS, 1), 21, enginesEndAt + 32 + 3);
	}

	private String distToString(double dist) {
		int i;
		for (i = 0; dist >= 1000.0; dist /= 1000.0, ++i) {}
		int val;
		if (dist >= 100.0) {
			val = 1;
		} else if (dist >= 10.0) {
			val = 10;
		} else {
			val = 100;
		}
		final double d = Math.round(dist * val) / val;
		String s;
		if (d == (int) d) {
			s = String.valueOf((int) d);
		} else {
			s = String.valueOf(d);
		}
		while (s.length() < ((s.indexOf(46) != -1) ? 4 : 3)) {
			if (s.indexOf(46) != -1) {
				s += "0";
			} else {
				s += ".0";
			}
		}
		s += Localization.MODULES.ATTACHMENTS.DISTANCES.translate(String.valueOf(i));
		return s;
	}

//	@Override
//	public RAILDIRECTION getSpecialRailDirection(final int x, final int y, final int z) {
//		if (this.isForwardKeyDown()) {
//			return RAILDIRECTION.FORWARD;
//		}
//		if (this.isLeftKeyDown()) {
//			return RAILDIRECTION.LEFT;
//		}
//		if (this.isRightKeyDown()) {
//			return RAILDIRECTION.RIGHT;
//		}
//		return RAILDIRECTION.DEFAULT;
//	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.engineInformation = data;
		} else if (id == 1) {
			if (this.getCart().getRidingEntity() != null && this.getCart().getRidingEntity() instanceof EntityPlayer && this.getCart().getRidingEntity() == player) {
				this.keyinformation = data[0];
				this.getCart().resetRailDirection();
			}
		} else if (id == 2) {
			int intOdo = 0;
			int intTrip = 0;
			for (int i = 0; i < 4; ++i) {
				int temp = data[i];
				if (temp < 0) {
					temp += 256;
				}
				intOdo |= temp << i * 8;
				temp = data[i + 4];
				if (temp < 0) {
					temp += 256;
				}
				intTrip |= temp << i * 8;
			}
			this.odo = intOdo;
			this.trip = intTrip;
		} else if (id == 3) {
			this.trip = 0.0;
			this.tripPacketTimer = 0;
		}
	}

	@Override
	public void update() {
		super.update();
		if (!this.getCart().worldObj.isRemote && this.getCart().getRidingEntity() != null && this.getCart().getRidingEntity() instanceof EntityPlayer) {
			if (this.enginePacketTimer == 0) {
				this.sendEnginePacket((EntityPlayer) this.getCart().getRidingEntity());
				this.enginePacketTimer = 15;
			} else {
				--this.enginePacketTimer;
			}
			if (this.tripPacketTimer == 0) {
				this.sendTripPacket((EntityPlayer) this.getCart().getRidingEntity());
				this.tripPacketTimer = 500;
			} else {
				--this.tripPacketTimer;
			}
		} else {
			this.enginePacketTimer = 0;
			this.tripPacketTimer = 0;
		}
		if (this.getCart().worldObj.isRemote) {
			this.encodeKeys();
		}
		if (!this.lastBackKey && this.isBackKeyDown()) {
			this.turnback();
		}
		this.lastBackKey = this.isBackKeyDown();
		if (!this.getCart().worldObj.isRemote) {
			if (this.speedChangeCooldown == 0) {
				if (!this.isJumpKeyDown() || !this.isSneakKeyDown()) {
					if (this.isJumpKeyDown()) {
						this.setSpeedSetting(this.getSpeedSetting() + 1);
						this.speedChangeCooldown = 8;
					} else if (this.isSneakKeyDown()) {
						this.setSpeedSetting(this.getSpeedSetting() - 1);
						this.speedChangeCooldown = 8;
					} else {
						this.speedChangeCooldown = 0;
					}
				}
			} else {
				--this.speedChangeCooldown;
			}
			if (this.isForwardKeyDown() && this.isLeftKeyDown() && this.isRightKeyDown() && this.getCart().getRidingEntity() != null && this.getCart().getRidingEntity() instanceof EntityPlayer) {
				this.getCart().getRidingEntity().mountEntity((Entity) this.getCart());
				this.keyinformation = 0;
			}
		}
		final double x = this.getCart().posX - this.lastPosX;
		final double y = this.getCart().posY - this.lastPosY;
		final double z = this.getCart().posZ - this.lastPosZ;
		this.lastPosX = this.getCart().posX;
		this.lastPosY = this.getCart().posY;
		this.lastPosZ = this.getCart().posZ;
		final double dist = Math.sqrt(x * x + y * y + z * z);
		if (!this.first) {
			this.odo += dist;
			this.trip += dist;
		} else {
			this.first = false;
		}
	}

	@Override
	public double getPushFactor() {
		switch (this.getSpeedSetting()) {
			case 1: {
				return 0.01;
			}
			case 2: {
				return 0.03;
			}
			case 3: {
				return 0.05;
			}
			case 4: {
				return 0.07;
			}
			case 5: {
				return 0.09;
			}
			case 6: {
				return 0.11;
			}
			default: {
				return super.getPushFactor();
			}
		}
	}

	private void encodeKeys() {
		if (this.getCart().getRidingEntity() != null && this.getCart().getRidingEntity() instanceof EntityPlayer && this.getCart().getRidingEntity() == this.getClientPlayer()) {
			final Minecraft minecraft = Minecraft.getMinecraft();
			final byte oldVal = this.keyinformation;
			this.keyinformation = 0;
			this.keyinformation |= (byte) ((minecraft.gameSettings.keyBindForward.isPressed() ? 1 : 0) << 0);
			this.keyinformation |= (byte) ((minecraft.gameSettings.keyBindLeft.isPressed() ? 1 : 0) << 1);
			this.keyinformation |= (byte) ((minecraft.gameSettings.keyBindRight.isPressed() ? 1 : 0) << 2);
			this.keyinformation |= (byte) ((minecraft.gameSettings.keyBindBack.isPressed() ? 1 : 0) << 3);
			this.keyinformation |= (byte) ((minecraft.gameSettings.keyBindJump.isPressed() ? 1 : 0) << 4);
			this.keyinformation |= (byte) ((minecraft.gameSettings.keyBindSneak.isPressed() ? 1 : 0) << 5);
			if (oldVal != this.keyinformation) {
				PacketHandler.sendPacket(this.getCart(), 1 + this.getPacketStart(), new byte[] { this.keyinformation });
			}
		}
	}

	private boolean isForwardKeyDown() {
		return (this.keyinformation & 0x1) != 0x0;
	}

	private boolean isLeftKeyDown() {
		return (this.keyinformation & 0x2) != 0x0;
	}

	private boolean isRightKeyDown() {
		return (this.keyinformation & 0x4) != 0x0;
	}

	private boolean isBackKeyDown() {
		return (this.keyinformation & 0x8) != 0x0;
	}

	private boolean isJumpKeyDown() {
		return (this.keyinformation & 0x10) != 0x0;
	}

	private boolean isSneakKeyDown() {
		return (this.keyinformation & 0x20) != 0x0;
	}

	private void sendTripPacket(final EntityPlayer player) {
		final byte[] data = new byte[8];
		final int intOdo = (int) this.odo;
		final int intTrip = (int) this.trip;
		for (int i = 0; i < 4; ++i) {
			data[i] = (byte) ((intOdo & 255 << i * 8) >> i * 8);
			data[i + 4] = (byte) ((intTrip & 255 << i * 8) >> i * 8);
		}
		this.sendPacket(2, data, player);
	}

	private void sendEnginePacket(final EntityPlayer player) {
		final int engineCount = this.getCart().getEngines().size();
		final byte[] data = new byte[engineCount * 2];
		for (int i = 0; i < this.getCart().getEngines().size(); ++i) {
			final ModuleEngine engine = this.getCart().getEngines().get(i);
			final int totalfuel = engine.getTotalFuel();
			final int fuelInTopBar = 20000;
			final int maxBarLength = 62;
			final float percentage = totalfuel % fuelInTopBar / fuelInTopBar;
			final int upperBarLength = (int) (maxBarLength * percentage);
			int lowerBarLength = totalfuel / fuelInTopBar;
			if (lowerBarLength > maxBarLength) {
				lowerBarLength = maxBarLength;
			}
			data[i * 2] = (byte) (upperBarLength & 0x3F);
			data[i * 2 + 1] = (byte) (lowerBarLength & 0x3F);
		}
		this.sendPacket(0, data, player);
	}

	public int numberOfPackets() {
		return 4;
	}

	private void setSpeedSetting(final int val) {
		if (val < 0 || val > 6) {
			return;
		}
		this.updateDw(0, val);
	}

	private int getSpeedSetting() {
		if (this.isPlaceholder()) {
			return 1;
		}
		return this.getDw(0);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		this.addDw(0, 0);
	}

	@Override
	public boolean stopEngines() {
		return this.getSpeedSetting() == 0;
	}

	@Override
	public int getConsumption(final boolean isMoving) {
		if (!isMoving) {
			return super.getConsumption(isMoving);
		}
		switch (this.getSpeedSetting()) {
			case 4: {
				return 1;
			}
			case 5: {
				return 3;
			}
			case 6: {
				return 5;
			}
			default: {
				return super.getConsumption(isMoving);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/advlever.png");
		if (this.inRect(x, y, this.buttonRect)) {
			this.drawImage(gui, this.buttonRect, 0, this.buttonRect[3]);
		} else {
			this.drawImage(gui, this.buttonRect, 0, 0);
		}
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, Localization.MODULES.ATTACHMENTS.CONTROL_RESET.translate(), x, y, this.buttonRect);
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0 && this.inRect(x, y, this.buttonRect)) {
			this.sendPacket(3);
		}
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ATTACHMENTS.CONTROL_SYSTEM.translate(), 8, 6, 4210752);
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setByte(this.generateNBTName("Speed", id), (byte) this.getSpeedSetting());
		tagCompound.setDouble(this.generateNBTName("ODO", id), this.odo);
		tagCompound.setDouble(this.generateNBTName("TRIP", id), this.trip);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.setSpeedSetting(tagCompound.getByte(this.generateNBTName("Speed", id)));
		this.odo = tagCompound.getDouble(this.generateNBTName("ODO", id));
		this.trip = tagCompound.getDouble(this.generateNBTName("TRIP", id));
	}

	public float getWheelAngle() {
		if (!this.isForwardKeyDown()) {
			if (this.isLeftKeyDown()) {
				return 0.3926991f;
			}
			if (this.isRightKeyDown()) {
				return -0.3926991f;
			}
		}
		return 0.0f;
	}

	@Override
	public float getLeverState() {
		if (this.isPlaceholder()) {
			return 0.0f;
		}
		return this.getSpeedSetting() / 6.0f;
	}

	@Override
	public void postUpdate() {
		if (this.getCart().worldObj.isRemote && this.getCart().getRidingEntity() != null && this.getCart().getRidingEntity() instanceof EntityPlayer && this.getCart().getRidingEntity() == this.getClientPlayer()) {
			KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode(), false);
		}
	}
}
