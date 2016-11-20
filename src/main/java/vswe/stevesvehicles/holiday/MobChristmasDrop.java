package vswe.stevesvehicles.holiday;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vswe.stevesvehicles.item.ComponentTypes;

public class MobChristmasDrop {
	public MobChristmasDrop() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onEntityLivingDeath(LivingDeathEvent event) {
		EntityLivingBase monster = event.getEntityLiving();
		if (monster.world.isRemote || !event.getSource().getDamageType().equals("player")) {
			return;
		}
		if (monster instanceof EntityMob) {
			if (Math.random() < 0.10d) {
				dropItem(monster, ComponentTypes.STOLEN_PRESENT.getItemStack());
			}
		}
		if (monster instanceof EntityBlaze) {
			if (Math.random() < 0.12) {
				dropItem(monster, ComponentTypes.RED_WRAPPING_PAPER.getItemStack());
			}
		}
	}

	private void dropItem(EntityLivingBase monster, ItemStack item) {
		EntityItem obj = new EntityItem(monster.world, monster.posX, monster.posY, monster.posZ, item);
		obj.motionX = monster.world.rand.nextGaussian() * 0.05F;
		obj.motionY = monster.world.rand.nextGaussian() * 0.05F + 0.2F;
		obj.motionZ = monster.world.rand.nextGaussian() * 0.05F;
		monster.world.spawnEntity(obj);
	}
}
