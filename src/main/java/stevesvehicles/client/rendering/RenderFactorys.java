package stevesvehicles.client.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import stevesvehicles.common.entitys.buoy.EntityBuoy;
import stevesvehicles.common.holiday.EntityEasterEgg;
import stevesvehicles.common.items.ComponentTypes;
import stevesvehicles.common.items.ModItems;
import stevesvehicles.common.modules.common.addon.projectile.EntityCake;
import stevesvehicles.common.vehicles.entitys.EntityModularBoat;
import stevesvehicles.common.vehicles.entitys.EntityModularCart;

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
