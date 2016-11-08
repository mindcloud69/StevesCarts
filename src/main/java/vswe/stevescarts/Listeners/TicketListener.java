package vswe.stevescarts.Listeners;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.StevesCarts;

import java.util.List;

public class TicketListener implements ForgeChunkManager.LoadingCallback {
	public TicketListener() {
		ForgeChunkManager.setForcedChunkLoadingCallback(StevesCarts.instance, this);
	}

	public void ticketsLoaded(final List<ForgeChunkManager.Ticket> tickets, final World world) {
		for (final ForgeChunkManager.Ticket ticket : tickets) {
			final Entity entity = ticket.getEntity();
			if (entity instanceof MinecartModular) {
				final MinecartModular cart = (MinecartModular) entity;
				cart.loadChunks(ticket);
			}
		}
	}
}
