package vswe.stevesvehicles.client.gui;

import net.minecraft.entity.player.EntityPlayer;

import vswe.stevesvehicles.vehicle.entity.IVehicleEntity;

public class OverlayRenderer {

	public OverlayRenderer() {
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {

		if (event.phase == TickEvent.Phase.END) {
			renderOverlay();
		}
	}


	@SideOnly(Side.CLIENT)
	private void renderOverlay() {
		net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getMinecraft();		
		EntityPlayer player = minecraft.thePlayer;

		if (minecraft.currentScreen == null && player.ridingEntity != null && player.ridingEntity instanceof IVehicleEntity)
		{
			((IVehicleEntity)player.ridingEntity).getVehicle().renderOverlay(minecraft);
		}
	}


}