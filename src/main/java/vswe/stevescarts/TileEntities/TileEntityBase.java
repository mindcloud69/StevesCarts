package vswe.stevescarts.TileEntities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Containers.ContainerBase;
import vswe.stevescarts.Interfaces.GuiBase;

public abstract class TileEntityBase extends TileEntity {

	@Deprecated
	int xCoord;

	@Deprecated
	int yCoord;

	@Deprecated
	int zCoord;

	//This is bad, remove asap
	@Override
	public void setPos(BlockPos posIn) {
		super.setPos(posIn);
		xCoord = posIn.getX();
		zCoord = posIn.getZ();
		yCoord = posIn.getY();
	}

	public void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
	}

	@SideOnly(Side.CLIENT)
	public abstract GuiBase getGui(final InventoryPlayer p0);

	public abstract ContainerBase getContainer(final InventoryPlayer p0);

	public void updateGuiData(final Container con, final IContainerListener crafting, final int id, final short data) {
		crafting.sendProgressBarUpdate(con, id, (int) data);
	}

	public void initGuiData(final Container con, final IContainerListener crafting) {
	}

	public void checkGuiData(final Container con, final IContainerListener crafting) {
	}

	public void receiveGuiData(final int id, final short data) {
	}

	public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
		return this.worldObj.getTileEntity(this.pos) == this && entityplayer.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
	}

	public short getShortFromInt(final boolean first, final int val) {
		if (first) {
			return (short) (val & 0xFFFF);
		}
		return (short) (val >> 16 & 0xFFFF);
	}

	public int getIntFromShort(final boolean first, int oldVal, final short val) {
		if (first) {
			oldVal = ((oldVal & 0xFFFF0000) | val);
		} else {
			oldVal = ((oldVal & 0xFFFF) | val << 16);
		}
		return oldVal;
	}
}
