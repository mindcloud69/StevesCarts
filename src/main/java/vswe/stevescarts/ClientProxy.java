package vswe.stevescarts;

import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vswe.stevescarts.entitys.MinecartModular;
import vswe.stevescarts.helpers.MinecartSoundMuter;
import vswe.stevescarts.helpers.SoundHandler;
import vswe.stevescarts.modules.data.ModuleData;
import vswe.stevescarts.renders.RendererMinecart;
import vswe.stevescarts.renders.model.ItemModelManager;

public class ClientProxy extends CommonProxy {

	@Override
	public void renderInit() {

		RenderingRegistry.registerEntityRenderingHandler(MinecartModular.class, new RenderManagerCart());
		//		RenderingRegistry.registerEntityRenderingHandler((Class) EntityEasterEgg.class, new RenderSnowball((Item) ModItems.component, ComponentTypes.PAINTED_EASTER_EGG.getId()));
		//	StevesCarts.instance.blockRenderer = (ISimpleBlockRenderingHandler) new RendererUpgrade();

		//		RenderingRegistry.registerEntityRenderingHandler((Class) EntityCake.class, new RenderSnowball(Items.CAKE));
		ModuleData.initModels();
	}

	public class RenderManagerCart implements IRenderFactory<MinecartModular> {

		@Override
		public Render<? super MinecartModular> createRenderFor(RenderManager manager) {
			return new RendererMinecart(manager);
		}
	}

	@Override
	public void soundInit() {
		ItemModelManager.load(); //Called in pre-init
		new SoundHandler();
		new MinecartSoundMuter();
	}

	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}
}
