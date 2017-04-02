package vswe.stevescarts.modules.storages.tanks;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotLiquidInput;
import vswe.stevescarts.containers.slots.SlotLiquidOutput;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.helpers.storages.ITankHolder;
import vswe.stevescarts.helpers.storages.Tank;
import vswe.stevescarts.modules.storages.ModuleStorage;

import javax.annotation.Nonnull;

public abstract class ModuleTank extends ModuleStorage implements IFluidTank, ITankHolder {
	protected Tank tank;
	private int tick;
	protected int[] tankBounds;
	private DataParameter<String> FLUID_NAME;
	private DataParameter<Integer> FLUID_AMOUNT;

	public ModuleTank(final EntityMinecartModular cart) {
		super(cart);
		this.tankBounds = new int[] { 35, 20, 36, 51 };
		this.tank = new Tank(this, this.getTankSize(), 0);
	}

	protected abstract int getTankSize();

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		if (y == 0) {
			return new SlotLiquidInput(this.getCart(), this.tank, -1, slotId, 8 + x * 18, 24 + y * 24);
		}
		return new SlotLiquidOutput(this.getCart(), slotId, 8 + x * 18, 24 + y * 24);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	@Override
	public int getInventoryWidth() {
		return 1;
	}

	@Override
	public int getInventoryHeight() {
		return 2;
	}

	@Override
	public int guiWidth() {
		return 100;
	}

	@Override
	public int guiHeight() {
		return 80;
	}

	public boolean hasVisualTank() {
		return true;
	}

	@Override
	public void update() {
		super.update();
		if (this.tick-- <= 0) {
			this.tick = 5;
			if (!this.getCart().world.isRemote) {
				this.tank.containerTransfer();
			} else if (!this.isPlaceholder()) {
				if (this.getDw(FLUID_NAME).isEmpty()) {
					this.tank.setFluid(null);
				} else {
					this.tank.setFluid(new FluidStack(FluidRegistry.getFluid(this.getDw(FLUID_NAME)), this.getDw(FLUID_AMOUNT)));
				}
			}
		}
	}

	@Override
	@Nonnull
	public ItemStack getInputContainer(final int tankid) {
		return this.getStack(0);
	}

	@Override
	public void clearInputContainer(final int tankid) {
		this.setStack(0, null);
	}

	@Override
	public void addToOutputContainer(final int tankid,
	                                 @Nonnull
		                                 ItemStack item) {
		this.addStack(1, item);
	}

	@Override
	public void onFluidUpdated(final int tankid) {
		if (this.getCart().world.isRemote) {
			return;
		}
		this.updateDw();
	}

	//	@SideOnly(Side.CLIENT)
	//	public void drawImage(final int tankid, final GuiBase gui, final IIcon icon, final int targetX, final int targetY, final int srcX, final int srcY, final int sizeX, final int sizeY) {
	//		this.drawImage((GuiMinecart) gui, icon, targetX, targetY, srcX, srcY, sizeX, sizeY);
	//	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		this.tank.drawFluid(gui, this.tankBounds[0], this.tankBounds[1]);
		ResourceHelper.bindResource("/gui/tank.png");
		this.drawImage(gui, this.tankBounds, 0, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, this.getTankInfo(), x, y, this.tankBounds);
	}

	protected String getTankInfo() {
		String str = this.tank.getMouseOver();
		if (this.tank.isLocked()) {
			str = str + "\n\n" + TextFormatting.GREEN + Localization.MODULES.TANKS.LOCKED.translate() + "\n" + Localization.MODULES.TANKS.UNLOCK.translate();
		} else if (this.tank.getFluid() != null) {
			str = str + "\n\n" + Localization.MODULES.TANKS.LOCK.translate();
		}
		return str;
	}

	@Override
	public FluidStack getFluid() {
		return (this.tank.getFluid() == null) ? null : this.tank.getFluid().copy();
	}

	@Override
	public int getCapacity() {
		return this.getTankSize();
	}

	@Override
	public int fill(final FluidStack resource, final boolean doFill) {
		return this.tank.fill(resource, doFill, this.getCart().world.isRemote);
	}

	@Override
	public FluidStack drain(final int maxDrain, final boolean doDrain) {
		return this.tank.drain(maxDrain, doDrain, this.getCart().world.isRemote);
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		if (this.tank.getFluid() != null) {
			final NBTTagCompound compound = new NBTTagCompound();
			this.tank.getFluid().writeToNBT(compound);
			tagCompound.setTag(this.generateNBTName("Fluid", id), compound);
		}
		tagCompound.setBoolean(this.generateNBTName("Locked", id), this.tank.isLocked());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.tank.setFluid(FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag(this.generateNBTName("Fluid", id))));
		this.tank.setLocked(tagCompound.getBoolean(this.generateNBTName("Locked", id)));
		this.updateDw();
	}

	@Override
	public int numberOfDataWatchers() {
		return 2;
	}

	protected void updateDw() {
		this.updateDw(FLUID_NAME, (this.tank.getFluid() == null) ? "" : this.tank.getFluid().getFluid().getName());
		this.updateDw(FLUID_AMOUNT, (this.tank.getFluid() == null) ? -1 : this.tank.getFluid().amount);
	}

	@Override
	public void initDw() {
		FLUID_NAME = createDw(DataSerializers.STRING);
		FLUID_AMOUNT = createDw(DataSerializers.VARINT);
		registerDw(FLUID_NAME, (this.tank.getFluid() == null) ? "" : this.tank.getFluid().getFluid().getName());
		registerDw(FLUID_AMOUNT, (this.tank.getFluid() == null) ? -1 : this.tank.getFluid().amount);
	}

	public float getFluidRenderHeight() {
		if (this.tank.getFluid() == null) {
			return 0.0f;
		}
		return this.tank.getFluid().amount / this.getTankSize();
	}

	public boolean isCompletelyFilled() {
		return this.getFluid() != null && this.getFluid().amount >= this.getTankSize();
	}

	public boolean isCompletelyEmpty() {
		return this.getFluid() == null || this.getFluid().amount == 0;
	}

	@Override
	public int getFluidAmount() {
		return (this.getFluid() == null) ? 0 : this.getFluid().amount;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this.getFluid(), this.getCapacity());
	}

	@Override
	protected int numberOfPackets() {
		return 1;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0 && (this.getFluid() != null || this.tank.isLocked())) {
			this.tank.setLocked(!this.tank.isLocked());
			if (!this.tank.isLocked() && this.tank.getFluid() != null && this.tank.getFluid().amount <= 0) {
				this.tank.setFluid(null);
				this.updateDw();
			}
		}
	}

	@Override
	public int numberOfGuiData() {
		return 1;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		this.updateGuiData(info, 0, (short) (this.tank.isLocked() ? 1 : 0));
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.tank.setLocked(data != 0);
		}
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (this.inRect(x, y, this.tankBounds)) {
			byte data = (byte) button;
			if (GuiScreen.isShiftKeyDown()) {
				data |= 0x2;
			}
			this.sendPacket(0, data);
		}
	}

	@Override
	public void drawImage(int p0, GuiBase p1, int p3, int p4, int p5, int p6, int p7, int p8) {
		//TODO help me
	}
}
