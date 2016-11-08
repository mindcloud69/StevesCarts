package vswe.stevescarts.listeners;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vswe.stevescarts.entitys.MinecartModular;

public class ChunkListener {
	public ChunkListener() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void invoke(final EntityEvent.EnteringChunk event) {
		if (!event.getEntity().isDead && event.getEntity() instanceof MinecartModular) {
			((MinecartModular) event.getEntity()).loadChunks(event.getNewChunkX(), event.getNewChunkZ());
		}
	}
}
