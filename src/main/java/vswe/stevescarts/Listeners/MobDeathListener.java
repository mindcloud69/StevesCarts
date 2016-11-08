package vswe.stevescarts.Listeners;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vswe.stevescarts.Helpers.ComponentTypes;

public class MobDeathListener {
	public MobDeathListener() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onEntityLivingDeath(final LivingDeathEvent event) {
		final EntityLivingBase monster = event.getEntityLiving();
		if (monster.worldObj.isRemote || !event.getSource().getDamageType().equals("player")) {
			return;
		}
		if (monster instanceof EntityMob && Math.random() < 0.1) {
			this.dropItem(monster, ComponentTypes.STOLEN_PRESENT.getItemStack());
		}
		if (monster instanceof EntityBlaze && Math.random() < 0.12) {
			this.dropItem(monster, ComponentTypes.RED_WRAPPING_PAPER.getItemStack());
		}
	}

	private void dropItem(final EntityLivingBase monster, final ItemStack item) {
		final EntityItem obj = new EntityItem(monster.worldObj, monster.posX, monster.posY, monster.posZ, item);
		obj.motionX = monster.worldObj.rand.nextGaussian() * 0.05000000074505806;
		obj.motionY = monster.worldObj.rand.nextGaussian() * 0.05000000074505806 + 0.20000000298023224;
		obj.motionZ = monster.worldObj.rand.nextGaussian() * 0.05000000074505806;
		monster.worldObj.spawnEntityInWorld(obj);
	}
}
