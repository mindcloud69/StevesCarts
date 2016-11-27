package stevesvehicles.common.upgrades.effects.util;

import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.gui.screen.GuiUpgrade;
import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.container.ContainerUpgrade;
import stevesvehicles.common.container.slots.SlotLiquidOutput;
import stevesvehicles.common.container.slots.SlotLiquidUpgradeInput;
import stevesvehicles.common.tanks.ITankHolder;
import stevesvehicles.common.tanks.Tank;
import stevesvehicles.common.transfer.TransferHandler;

public abstract class TankEffect extends InventoryEffect implements ITankHolder {
	public TankEffect(UpgradeContainer upgrade) {
		super(upgrade);
	}

	public Tank getTank() {
		return tank;
	}

	public abstract int getTankSize();

	@Override
	public Class<? extends Slot> getSlot(int id) {
		return SlotLiquidOutput.class;
	}

	@Override
	public Slot createSlot(int id) {
		if (id == 0) {
			return new SlotLiquidUpgradeInput(upgrade, tank, 16, id, getSlotX(id), getSlotY(id));
		} else {
			return super.createSlot(id);
		}
	}

	@Override
	public int getInventorySize() {
		return 2;
	}

	@Override
	public int getSlotX(int id) {
		return 8;
	}

	@Override
	public int getSlotY(int id) {
		return 24 * (id + 1);
	}

	private static final int tankInterfaceX = 35;
	private static final int tankInterfaceY = 20;
	@SideOnly(Side.CLIENT)
	private static ResourceLocation texture;

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiUpgrade gui, int x, int y) {
		if (texture == null) {
			texture = ResourceHelper.getResource("/gui/tank.png");
		}
		tank.drawFluid(gui, tankInterfaceX, tankInterfaceY);
		ResourceHelper.bindResource(texture);
		gui.drawTexturedModalRect(gui.getGuiLeft() + tankInterfaceX, gui.getGuiTop() + tankInterfaceY, 1, 1, 36, 51);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawMouseOver(GuiUpgrade gui, int x, int y) {
		drawMouseOver(gui, tank.getMouseOver(), x, y, new int[] { tankInterfaceX, tankInterfaceY, 36, 51 });
	}

	// TODO synchronize the tag somehow :S
	@Override
	public void checkGuiData(ContainerUpgrade con, IContainerListener crafting, boolean isNew) {
		boolean changed = false;
		int id = 0;
		int amount1 = 1;
		int amount2 = 2;
		int meta = 3;
		FluidStack oldFluid = (FluidStack) con.olddata;
		if ((isNew || oldFluid != null) && tank.getFluid() == null) {
			upgrade.getMaster().updateGuiData(con, crafting, id, (short) -1);
			changed = true;
		} else if (tank.getFluid() != null) {
			// TODO: rework gui data system
			/*
			 * if (isNew || oldFluid == null) { upgrade.updateGuiData(con,
			 * crafting, id, (short)
			 * FluidRegistry.getFluidID(tank.getFluid().getFluid()));
			 * upgrade.updateGuiData(con, crafting, amount1,
			 * upgrade.getShortFromInt(true, tank.getFluid().amount));
			 * upgrade.updateGuiData(con, crafting, amount2,
			 * upgrade.getShortFromInt(false, tank.getFluid().amount)); changed
			 * = true; } else { if
			 * (!oldFluid.getFluid().getName().equals(tank.getFluid().getFluid()
			 * .getName())) { upgrade.updateGuiData(con, crafting, id, (short)
			 * FluidRegistry.getFluidID(tank.getFluid().getFluid())); changed =
			 * true; } if (oldFluid.amount != tank.getFluid().amount) {
			 * upgrade.updateGuiData(con, crafting, amount1,
			 * upgrade.getShortFromInt(true, tank.getFluid().amount));
			 * upgrade.updateGuiData(con, crafting, amount2,
			 * upgrade.getShortFromInt(false, tank.getFluid().amount)); changed
			 * = true; } }
			 */
		}
		if (changed) {
			if (tank.getFluid() == null) {
				con.olddata = null;
			} else {
				con.olddata = tank.getFluid().copy();
			}
		}
	}

	// TODO Synchronize the tag somehow :S
	@Override
	public void receiveGuiData(int id, short data) {
		// TODO: rework gui data system
		/*
		 * if (id == 0) { if (data == -1) { tank.setFluid(null); } else if
		 * (tank.getFluid() == null) { tank.setFluid(new
		 * FluidStack(FluidRegistry.getFluid(data), 0)); } } else if
		 * (tank.getFluid() != null) { tank.getFluid().amount =
		 * upgrade.getIntFromShort(id == 1, tank.getFluid().amount, data); }
		 */
	}

	private int tick;
	private Tank tank;

	@Override
	public void init() {
		tank = new Tank(this, getTankSize(), 0);
	}

	@Override
	public void update() {
		super.update();
		if (--tick <= 0) {
			tick = 5;
		} else {
			return;
		}
		if (!upgrade.getMaster().getWorld().isRemote && slots != null && slots.size() >= 2) {
			tank.containerTransfer();
		}
	}

	@Override
	public void load(NBTTagCompound compound) {
		tank.setFluid(FluidStack.loadFluidStackFromNBT(compound));
	}

	@Override
	public void save(NBTTagCompound compound) {
		if (tank.getFluid() != null) {
			tank.getFluid().writeToNBT(compound);
		}
	}

	@Override
	public ItemStack getInputContainer(int tankId) {
		return upgrade.getStackInSlot(0);
	}

	@Override
	public void clearInputContainer(int tankId) {
		upgrade.setInventorySlotContents(0, null);
	}

	@Override
	public void addToOutputContainer(int tankId, ItemStack item) {
		TransferHandler.TransferItem(item, upgrade, 1, 1, new ContainerUpgrade(null, upgrade), Slot.class, null, -1);
	}

	@Override
	public void onFluidUpdated(int tankId) {
	}
	/*
	 * @Override
	 * @SideOnly(Side.CLIENT) public void drawImage(int tankId, GuiBase gui,
	 * IIcon icon, int targetX, int targetY, int srcX, int srcY, int sizeX, int
	 * sizeY) { gui.drawIcon(icon, gui.getGuiLeft() + targetX, gui.getGuiTop() +
	 * targetY, sizeX / 16F, sizeY / 16F, srcX / 16F, srcY / 16F); }
	 */
}
