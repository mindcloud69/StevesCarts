package vswe.stevescarts.modules.addons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.IActivatorModule;

public class ModuleChunkLoader extends ModuleAddon implements IActivatorModule {
	private boolean rdyToInit;
	private int[] buttonRect;
	private static DataParameter<Boolean> LOADING_CHUNK = createDw(DataSerializers.BOOLEAN);

	public ModuleChunkLoader(final EntityMinecartModular cart) {
		super(cart);
		this.buttonRect = new int[] { 20, 20, 24, 12 };
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
		return 80;
	}

	@Override
	public int guiHeight() {
		return 35;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, "Chunk Loader", 8, 6, 4210752);
	}

	@Override
	public void update() {
		super.update();
		if (!this.rdyToInit) {
			this.rdyToInit = true;
		}
		if (this.isLoadingChunk() && !this.getCart().hasFuelForModule() && !this.getCart().worldObj.isRemote) {
			this.setChunkLoading(false);
		}
	}

	public void setChunkLoading(final boolean val) {
		if (!this.isPlaceholder()) {
			this.updateDw(LOADING_CHUNK, val);
			if (!this.getCart().worldObj.isRemote && this.rdyToInit) {
				if (val) {
					this.getCart().initChunkLoading();
				} else {
					this.getCart().dropChunkLoading();
				}
			}
		}
	}

	private boolean isLoadingChunk() {
		return !this.isPlaceholder() && getDw(LOADING_CHUNK);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/chunk.png");
		final int imageID = this.isLoadingChunk() ? 1 : 0;
		int borderID = 0;
		if (this.inRect(x, y, this.buttonRect)) {
			borderID = 1;
		}
		this.drawImage(gui, this.buttonRect, 0, this.buttonRect[3] * borderID);
		final int srcY = this.buttonRect[3] * 2 + imageID * (this.buttonRect[3] - 2);
		this.drawImage(gui, this.buttonRect[0] + 1, this.buttonRect[1] + 1, 0, srcY, this.buttonRect[2] - 2, this.buttonRect[3] - 2);
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, this.getStateName(), x, y, this.buttonRect);
	}

	private String getStateName() {
		if (!this.isLoadingChunk()) {
			return "Activate chunk loading";
		}
		return "Deactivate chunk loading";
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0 && this.inRect(x, y, this.buttonRect)) {
			this.sendPacket(0);
		}
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.setChunkLoading(!this.isLoadingChunk());
		}
	}

	@Override
	public int numberOfPackets() {
		return 1;
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		registerDw(LOADING_CHUNK, false);
	}

	@Override
	public int getConsumption(final boolean isMoving) {
		return this.isLoadingChunk() ? 5 : super.getConsumption(isMoving);
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setBoolean(this.generateNBTName("ChunkLoading", id), this.isLoadingChunk());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.setChunkLoading(tagCompound.getBoolean(this.generateNBTName("ChunkLoading", id)));
	}

	@Override
	public void doActivate(final int id) {
		this.setChunkLoading(true);
	}

	@Override
	public void doDeActivate(final int id) {
		this.setChunkLoading(false);
	}

	@Override
	public boolean isActive(final int id) {
		return this.isLoadingChunk();
	}
}
