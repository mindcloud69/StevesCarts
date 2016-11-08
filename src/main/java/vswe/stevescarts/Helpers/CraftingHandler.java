package vswe.stevescarts.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import vswe.stevescarts.items.ModItems;

public class CraftingHandler {
	public CraftingHandler() {
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void onCrafting(final PlayerEvent.ItemCraftedEvent event) {
		this.onCrafting(event.player, event.crafting, event.craftMatrix);
	}

	private void onCrafting(final EntityPlayer player, final ItemStack item, final IInventory craftMatrix) {
		if (item.getItem() == ModItems.component || item.getItem() == ModItems.modules) {
			for (int i = 0; i < craftMatrix.getSizeInventory(); ++i) {
				final ItemStack sItem = craftMatrix.getStackInSlot(i);
				if (sItem != null && sItem.getItem().getContainerItem() != null) {
					craftMatrix.setInventorySlotContents(i, null);
				}
			}
		}
	}
}
