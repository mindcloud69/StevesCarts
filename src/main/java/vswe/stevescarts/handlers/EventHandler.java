package vswe.stevescarts.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.items.ModItems;

import java.util.List;

public class EventHandler implements ForgeChunkManager.LoadingCallback {
	public EventHandler() {
		ForgeChunkManager.setForcedChunkLoadingCallback(StevesCarts.instance, this);
	}

	@Override
	public void ticketsLoaded(final List<ForgeChunkManager.Ticket> tickets, final World world) {
		for (final ForgeChunkManager.Ticket ticket : tickets) {
			final Entity entity = ticket.getEntity();
			if (entity instanceof EntityMinecartModular) {
				final EntityMinecartModular cart = (EntityMinecartModular) entity;
				cart.loadChunks(ticket);
			}
		}
	}

	@SubscribeEvent
	public void onRenderTick(final TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			this.renderOverlay();
		}
	}

	@SideOnly(Side.CLIENT)
	private void renderOverlay() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final EntityPlayer player = minecraft.player;
		if (minecraft.currentScreen == null && player.getRidingEntity() != null && player.getRidingEntity() instanceof EntityMinecartModular) {
			((EntityMinecartModular) player.getRidingEntity()).renderOverlay(minecraft);
		}
	}

	@SubscribeEvent
	public void invoke(final EntityEvent.EnteringChunk event) {
		if (!event.getEntity().isDead && event.getEntity() instanceof EntityMinecartModular) {
			((EntityMinecartModular) event.getEntity()).loadChunks(event.getNewChunkX(), event.getNewChunkZ());
		}
	}

	@SubscribeEvent
	public void onCrafting(final PlayerEvent.ItemCraftedEvent event) {
		this.onCrafting(event.player, event.crafting, event.craftMatrix);
	}

	private void onCrafting(final EntityPlayer player,
	                        @Nonnull
		                        ItemStack item, final IInventory craftMatrix) {
		if (item.getItem() == ModItems.component || item.getItem() == ModItems.modules) {
			for (int i = 0; i < craftMatrix.getSizeInventory(); ++i) {
				@Nonnull
				ItemStack sItem = craftMatrix.getStackInSlot(i);
				if (sItem != null && sItem.getItem().getContainerItem() != null) {
					craftMatrix.setInventorySlotContents(i, null);
				}
			}
		}
	}
}
