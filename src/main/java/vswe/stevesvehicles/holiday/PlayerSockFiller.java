package vswe.stevesvehicles.holiday;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import vswe.stevesvehicles.StevesVehicles;
import vswe.stevesvehicles.item.ComponentTypes;

public class PlayerSockFiller {
	public PlayerSockFiller() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void tickEnd(TickEvent.PlayerTickEvent event) {
		if (event.side == Side.SERVER) {
			EntityPlayer player = event.player;
			if (StevesVehicles.holidays.contains(HolidayType.CHRISTMAS) && player.isPlayerFullyAsleep()) {
				for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
					ItemStack item = player.inventory.getStackInSlot(i);
					if (ComponentTypes.SOCK.isStackOfType(item)) {
						item.setItemDamage(ComponentTypes.STUFFED_SOCK.getId());
					}
				}
			}
		}
	}
}
