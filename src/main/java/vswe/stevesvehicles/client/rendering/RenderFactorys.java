package vswe.stevesvehicles.client.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vswe.stevesvehicles.buoy.EntityBuoy;
import vswe.stevesvehicles.holiday.EntityEasterEgg;
import vswe.stevesvehicles.item.ComponentTypes;
import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.module.common.addon.projectile.EntityCake;
import vswe.stevesvehicles.vehicle.entity.EntityModularBoat;
import vswe.stevesvehicles.vehicle.entity.EntityModularCart;

public class RenderFactorys {
	public static class BuoyFactory implements IRenderFactory<EntityBuoy> {
		@Override
		public Render<? super EntityBuoy> createRenderFor(RenderManager manager) {
			return new RenderBuoy(manager);
		}
	}

	public static class ModularCartFactory implements IRenderFactory<EntityModularCart> {
		@Override
		public Render<? super EntityModularCart> createRenderFor(RenderManager manager) {
			return new RenderCart(manager);
		}
	}

	public static class ModularBoatFactory implements IRenderFactory<EntityModularBoat> {
		@Override
		public Render<? super EntityModularBoat> createRenderFor(RenderManager manager) {
			return new RenderBoat(manager);
		}
	}

	public static class EasterEggFactory implements IRenderFactory<EntityEasterEgg> {
		@Override
		public Render<? super EntityEasterEgg> createRenderFor(RenderManager manager) {
			return new RenderSnowball(manager, null, Minecraft.getMinecraft().getRenderItem()) {
				@Override
				public ItemStack getStackToRender(Entity entityIn) {
					return new ItemStack(ModItems.component, 1, ComponentTypes.PAINTED_EASTER_EGG.getId());
				}
			};
		}
	}

	public static class CakeFactory implements IRenderFactory<EntityCake> {
		@Override
		public Render<? super EntityCake> createRenderFor(RenderManager manager) {
			return new RenderSnowball(manager, Items.CAKE, Minecraft.getMinecraft().getRenderItem());
		}
	}
}
