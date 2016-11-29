package stevesvehicles.common.modules.common.attachment;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.ResourceLocation;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.gui.assembler.SimulationInfo;
import stevesvehicles.client.gui.assembler.SimulationInfoBoolean;
import stevesvehicles.client.gui.assembler.SimulationInfoInteger;
import stevesvehicles.client.gui.screen.GuiVehicle;
import stevesvehicles.client.localization.entry.block.LocalizationAssembler;
import stevesvehicles.client.localization.entry.module.LocalizationIndependence;
import stevesvehicles.common.container.slots.SlotBase;
import stevesvehicles.common.container.slots.SlotExplosion;
import stevesvehicles.common.items.ComponentTypes;
import stevesvehicles.common.modules.cart.attachment.ModuleAttachment;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleDynamite extends ModuleAttachment {
	private DataParameter<Byte> FUSE;
	private DataParameter<Byte> FUSE_LENGTH;
	private DataParameter<Byte> EXPLOSION;

	public ModuleDynamite(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.add(new SimulationInfoInteger(LocalizationAssembler.INFO_FUSE, "fuse", 1, 75, 35));
		simulationInfo.add(new SimulationInfoInteger(LocalizationAssembler.INFO_EXPLOSIVES, "explosives", 4, 54, 4));
		simulationInfo.add(new SimulationInfoBoolean(LocalizationAssembler.INFO_EXPLODE, "explode"));
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, LocalizationIndependence.EXPLOSIVES_TITLE.translate(), 8, 6, 0x404040);
	}

	@Override
	protected SlotBase getSlot(int slotId, int x, int y) {
		return new SlotExplosion(getVehicle().getVehicleEntity(), slotId, 8 + x * 18, 23 + y * 18);
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected int getInventoryWidth() {
		return 1;
	}

	@Override
	public void activatedByRail(int x, int y, int z, boolean active) {
		if (active && getFuse() == 0) {
			prime();
		}
	}

	@Override
	public void update() {
		super.update();
		if (isPlaceholder()) {
			boolean shouldExplode = ((SimulationInfoBoolean) getSimulationInfo(2)).getValue();
			if (getFuse() == 0 && shouldExplode) {
				setFuse(1);
			} else if (getFuse() != 0 && !shouldExplode) {
				setFuse(0);
			}
		}
		if (getFuse() > 0) {
			setFuse(getFuse() + 1);
			if (getFuse() == getFuseLength()) {
				explode();
				if (!isPlaceholder()) {
					getVehicle().getEntity().setDead();
				}
			}
		}
	}

	@Override
	public int guiWidth() {
		return super.guiWidth() + 136;
	}

	private boolean markerMoving;
	private static final int FUSE_START_X = 30;
	private static final int FUSE_START_Y = 27;
	private static final int[] FUSE_AREA = { FUSE_START_X, FUSE_START_Y + 3, 105, 4 };

	private int[] getMovableMarker() {
		return new int[] { FUSE_START_X + (int) (105 * (1F - getFuseLength() / (float) MAX_FUSE_LENGTH)), FUSE_START_Y, 4, 10 };
	}

	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/explosions.png");

	@Override
	public void drawBackground(GuiVehicle gui, int x, int y) {
		ResourceHelper.bindResource(TEXTURE);
		drawImage(gui, FUSE_AREA, 16, 1);
		drawImage(gui, FUSE_START_X + 105, FUSE_START_Y - 4, 1, 12, 16, 16);
		drawImage(gui, FUSE_START_X + (int) (105 * (1F - (getFuseLength() - getFuse()) / (float) MAX_FUSE_LENGTH)), FUSE_START_Y, (isPrimed() ? 11 : 6), 1, 4, 10);
		drawImage(gui, getMovableMarker(), 1, 1);
	}

	@Override
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		if (markerMoving || inRect(x, y, FUSE_AREA) || inRect(x, y, getMovableMarker())) {
			if (getFuse() != 0) {
				drawStringOnMouseOver(gui, LocalizationIndependence.EXPLODING.translate(String.valueOf(getFuseLength() - getFuse()), String.valueOf(getFuseLength())), x, y);
			} else {
				drawStringOnMouseOver(gui, LocalizationIndependence.FUSE_LENGTH.translate(String.valueOf(getFuseLength())), x, y);
			}
		}
	}

	@Override
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) throws IOException {
		if (button == 0) {
			if (getFuse() == 0 && (inRect(x, y, getMovableMarker()) || inRect(x, y, FUSE_AREA))) {
				markerMoving = true;
				moveMarker(x);
			}
		}
	}

	@Override
	public void mouseMovedOrUp(GuiVehicle gui, int x, int y, int button) throws IOException {
		if (getFuse() != 0) {
			markerMoving = false;
		} else if (markerMoving) {
			moveMarker(x);
		}
		if (button != -1) {
			markerMoving = false;
		}
	}

	private void moveMarker(int x) throws IOException {
		int tempFuse = MAX_FUSE_LENGTH - (int) ((x - FUSE_START_X) / (105F / MAX_FUSE_LENGTH));
		if (tempFuse < 2) {
			tempFuse = 2;
		} else if (tempFuse > MAX_FUSE_LENGTH) {
			tempFuse = MAX_FUSE_LENGTH;
		}
		DataWriter dw = getDataWriter();
		dw.writeByte(tempFuse);
		sendPacketToServer(dw);
	}

	private boolean isPrimed() {
		return (getFuse() / 5) % 2 == 0 && getFuse() != 0;
	}

	private void explode() {
		if (isPlaceholder()) {
			setFuse(1);
		} else {
			float f = explosionSize();
			setStack(0, null);
			getVehicle().getWorld().createExplosion(null, getVehicle().getEntity().posX, getVehicle().getEntity().posY, getVehicle().getEntity().posZ, f,
					true/* true means real, false otherwise */);
		}
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		createExplosives();
	}

	@Override
	public boolean dropOnDeath() {
		return getFuse() == 0;
	}

	@Override
	public void onDeath() {
		if (getFuse() > 0 && getFuse() < getFuseLength()) {
			explode();
		}
	}

	public float explosionSize() {
		if (isPlaceholder()) {
			return ((SimulationInfoInteger) getSimulationInfo(1)).getValue() / 2.5F;
		} else {
			return getDw(EXPLOSION) / 2.5F;
		}
	}

	public void createExplosives() {
		if (isPlaceholder() || getVehicle().getWorld().isRemote) {
			return;
		}
		int f = 8;
		if (ComponentTypes.DYNAMITE.isStackOfType(getStack(0))) {
			f += getStack(0).getCount() * 2;
		}
		updateDw(EXPLOSION, (byte) f);
	}

	@Override
	public int numberOfDataWatchers() {
		return 3;
	}

	@Override
	public void initDw() {
		FUSE = createDw(DataSerializers.BYTE);
		FUSE_LENGTH = createDw(DataSerializers.BYTE);
		EXPLOSION = createDw(DataSerializers.BYTE);
		registerDw(FUSE, (byte) 0);
		registerDw(FUSE_LENGTH, (byte) 70);
		registerDw(EXPLOSION, (byte) 8);
	}

	private int simulationFuse;

	public int getFuse() {
		if (isPlaceholder()) {
			return simulationFuse;
		} else {
			int val = getDw(FUSE);
			if (val < 0) {
				return val + 256;
			} else {
				return val;
			}
		}
	}

	private void setFuse(int val) {
		if (isPlaceholder()) {
			simulationFuse = val;
		} else {
			updateDw(FUSE, (byte) val);
		}
	}

	public void setFuseLength(int val) {
		if (val > MAX_FUSE_LENGTH) {
			val = MAX_FUSE_LENGTH;
		}
		updateDw(FUSE_LENGTH, (byte) val);
	}

	public int getFuseLength() {
		if (isPlaceholder()) {
			return ((SimulationInfoInteger) getSimulationInfo(0)).getValue();
		} else {
			int val = getDw(FUSE_LENGTH);
			if (val < 0) {
				return val + 256;
			} else {
				return val;
			}
		}
	}

	public void prime() {
		setFuse(1);
	}

	private static final int MAX_FUSE_LENGTH = 150;

	@Override
	protected void receivePacket(DataReader dr, EntityPlayer player) throws IOException {
		setFuseLength(dr.readByte());
	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		tagCompound.setShort("FuseLength", (short) getFuseLength());
		tagCompound.setShort("Fuse", (short) getFuse());
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		setFuseLength(tagCompound.getShort("FuseLength"));
		setFuse(tagCompound.getShort("Fuse"));
		createExplosives();
	}
}
