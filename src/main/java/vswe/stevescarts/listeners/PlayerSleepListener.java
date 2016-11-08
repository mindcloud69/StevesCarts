package vswe.stevescarts.listeners;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.items.ModItems;

public class PlayerSleepListener {
	public PlayerSleepListener() {
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void tickEnd(final TickEvent.PlayerTickEvent event) {
		if (event.side == Side.SERVER) {
			final EntityPlayer player = event.player;
			if (StevesCarts.isChristmas && player.isPlayerFullyAsleep()) {
				for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
					final ItemStack item = player.inventory.getStackInSlot(i);
					if (item != null && item.getItem() == ModItems.component && item.getItemDamage() == 56) {
						item.setItemDamage(item.getItemDamage() + 1);
					}
				}
			}
		}
	}
}
