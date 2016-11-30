package stevesvehicles.common.container;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.client.gui.screen.GuiBuoy;
import stevesvehicles.common.blocks.tileentitys.TileEntityBase;
import stevesvehicles.common.entitys.buoy.EntityBuoy;
import stevesvehicles.common.network.PacketHandler;
import stevesvehicles.common.network.packets.PacketBuoy;

public class ContainerBuoy extends ContainerBase {
	private EntityBuoy entityBuoy;

	public ContainerBuoy(EntityBuoy entityBuoy) {
		this.entityBuoy = entityBuoy;
	}

	@Override
	public IInventory getMyInventory() {
		return null;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistanceSqToEntity(entityBuoy) <= 64;
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		if (listener instanceof EntityPlayer) {
			if (((EntityPlayer) listener).getEntityWorld().isRemote) {
				PacketHandler.sendToPlayer(new PacketBuoy(), (EntityPlayer) listener);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public GuiBuoy gui;

	public void receiveInfo(DataReader dr, boolean server) throws IOException {
		if (server) {
			int entityId = dr.readInt();
			Entity entity = entityBuoy.world.getEntityByID(entityId);
			EntityBuoy otherBuoy = null;
			if (entity instanceof EntityBuoy && !entity.isDead) {
				otherBuoy = (EntityBuoy) entity;
			}
			boolean next = dr.readBoolean();
			EntityBuoy oldNext = entityBuoy.getBuoy(next);
			if (oldNext != null) {
				oldNext.setBuoy(null, !next);
			}
			entityBuoy.setBuoy(otherBuoy, next);
			if (otherBuoy != null) {
				EntityBuoy oldPrev = otherBuoy.getBuoy(!next);
				if (oldPrev != null) {
					oldPrev.setBuoy(null, next);
				}
				otherBuoy.setBuoy(entityBuoy, !next);
			}
		}
	}
}
