package stevesvehicles.common.modules.common.addon.chunk;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stevesvehicles.common.vehicles.entitys.IVehicleEntity;

public class ChunkListener {
	public ChunkListener() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void invoke(EntityEvent.EnteringChunk event) {
		Entity entity = event.getEntity();
		if (!entity.isDead && entity instanceof IVehicleEntity) {
			((IVehicleEntity) entity).getVehicle().loadChunks(event.getNewChunkX(), event.getNewChunkZ());
		}
	}
}
