package vswe.stevesvehicles.holiday;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import vswe.stevesvehicles.item.ComponentTypes;
import vswe.stevesvehicles.item.ModItems;

public class MobHatEquip {
	public MobHatEquip() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onEntityInteract(final PlayerInteractEvent.EntityInteract event) {
		final EntityPlayer player = event.getEntityPlayer();
		final Entity target = event.getTarget();
		if (target instanceof EntityVillager) {
			final EntityVillager villager = (EntityVillager) target;
			if (villager.getProfessionForge() != TradeHandler.santaProfession) {
				final ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
				if (item != null && item.getItem() == ModItems.component && item.getItemDamage() == ComponentTypes.WARM_HAT.getId()) {
					if (!player.capabilities.isCreativeMode) {
						final ItemStack itemStack = item;
						itemStack.shrink(1);
					}
					if (!player.world.isRemote) {
						villager.setProfession(TradeHandler.santaProfession);
						try {
							ReflectionHelper.findMethod(EntityVillager.class, villager, new String[] { "populateBuyingList" }).invoke(null);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (item.getCount() <= 0 && !player.capabilities.isCreativeMode) {
						player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
					}
					event.setCanceled(true);
				}
			}
		}
	}
}
