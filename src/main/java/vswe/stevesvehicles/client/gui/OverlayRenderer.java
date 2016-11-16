package vswe.stevesvehicles.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.vehicle.entity.IVehicleEntity;

public class OverlayRenderer {
	public OverlayRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			renderOverlay();
		}
	}

	@SideOnly(Side.CLIENT)
	private void renderOverlay() {
		Minecraft minecraft = Minecraft.getMinecraft();
		EntityPlayer player = minecraft.player;
		if(player != null){
			Entity ridingEntity = player.getRidingEntity();
			if (minecraft.currentScreen == null && ridingEntity instanceof IVehicleEntity) {
				((IVehicleEntity) ridingEntity).getVehicle().renderOverlay(minecraft);
			}
		}
	}
}