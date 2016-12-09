package stevesvehicles.common.blocks.tileentitys;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.gui.screen.GuiBase;
import stevesvehicles.common.container.ContainerBase;
import stevesvehicles.common.network.DataReader;
import stevesvehicles.common.network.DataWriter;

public abstract class TileEntityBase extends TileEntity {
	public TileEntityBase() {
		super();
	}

	/**
	 * Returns a new interface for this tile entity
	 * 
	 * @param inv
	 *            The inventory of the player opening the interface
	 * @return The interface to be shown
	 */
	@SideOnly(Side.CLIENT)
	public abstract GuiBase getGui(InventoryPlayer inv);

	/**
	 * Returns a new container for this tile entity
	 * 
	 * @param inv
	 *            The inventory of the player opening the container
	 * @return The container to be used
	 */
	public abstract ContainerBase getContainer(InventoryPlayer inv);

	/**
	 * Synchronizes the client with the server by sending some data to it
	 * 
	 * @param con
	 *            The container associated with the player on the server
	 * @param crafting
	 *            The player to send information to
	 * @param id
	 *            The id of this data
	 * @param data
	 *            The data to send
	 */
	public void updateGuiData(Container con, IContainerListener crafting, int id, short data) {
		crafting.sendProgressBarUpdate(con, id, data);
	}

	/**
	 * Initializes the synchronizing from the server to the client
	 * 
	 * @param con
	 *            The container on the server for the player
	 * @param crafting
	 *            The player
	 * @deprecated use updateGuiData at opening of a container
	 */
	@Deprecated
	public void initGuiData(Container con, IContainerListener crafting) {
	}

	public void writeGuiData(DataWriter writer, Container container) throws IOException {
	}

	/**
	 * Check if some data has to be synchronized from the server to the client
	 * 
	 * @param con
	 *            The container on the server for the player
	 */
	public boolean checkGuiData(Container con) {
		return false;
	}

	/**
	 * Called when the client is synchronized by receiving new data from the
	 * server
	 * 
	 * @param id
	 *            The id of the data
	 * @param data
	 *            The data itself
	 */
	public void readGuiData(DataReader reader) throws IOException {
	}

	/**
	 * If this Tile Entity can be used by the given player
	 * 
	 * @param entityPlayer
	 *            The player that wants to interact
	 * @return If the player can use this tile entity
	 */
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		return world.getTileEntity(getPos()) == this && entityPlayer.getDistanceSqToCenter(getPos()) <= 64D;
	}

	public short getShortFromInt(boolean first, int val) {
		if (first) {
			return (short) (val & 65535);
		} else {
			return (short) ((val >> 16) & 65535);
		}
	}

	public int getIntFromShort(boolean first, int oldVal, short val) {
		if (first) {
			oldVal = (oldVal & -65536) | val;
		} else {
			oldVal = (oldVal & 65535) | (val << 16);
		}
		return oldVal;
	}
}
