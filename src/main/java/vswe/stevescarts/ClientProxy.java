package vswe.stevescarts;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vswe.stevescarts.blocks.ModBlocks;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.handlers.SoundHandler;
import vswe.stevescarts.helpers.MinecartSoundMuter;
import vswe.stevescarts.modules.data.ModuleData;
import vswe.stevescarts.renders.ItemStackRenderer;
import vswe.stevescarts.renders.RendererCart;
import vswe.stevescarts.renders.model.ItemModelManager;

public class ClientProxy extends CommonProxy {

	@Override
	public void renderInit() {


		//		RenderingRegistry.registerEntityRenderingHandler((Class) EntityEasterEgg.class, new RenderSnowball((Item) ModItems.component, ComponentTypes.PAINTED_EASTER_EGG.getId()));
		//	StevesCarts.instance.blockRenderer = (ISimpleBlockRenderingHandler) new RendererUpgrade();

		//		RenderingRegistry.registerEntityRenderingHandler((Class) EntityCake.class, new RenderSnowball(Items.CAKE));
		ModuleData.initModels();

		TileEntityItemStackRenderer.instance = new ItemStackRenderer(TileEntityItemStackRenderer.instance);
	}

	public class RenderManagerCart implements IRenderFactory<EntityMinecartModular> {

		@Override
		public Render<? super EntityMinecartModular> createRenderFor(RenderManager manager) {
			return new RendererCart(manager);
		}
	}

	@Override
	public void soundInit() {
		ItemModelManager.load(); //Called in pre-init
		RenderingRegistry.registerEntityRenderingHandler(EntityMinecartModular.class, new RenderManagerCart()); //Needs to be done after the mc ones have been done
		new SoundHandler();
		new MinecartSoundMuter();
	}

	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}

    @Override
    public void initItemModels()
    {
        registerItemModel(ModBlocks.CART_ASSEMBLER.getBlock(), 0);
        registerItemModel(ModBlocks.CARGO_MANAGER.getBlock(), 0);
    }

	public static void registerItemModel(Item i, int meta)
	{
		ResourceLocation loc = i.getRegistryName();
		ModelLoader.setCustomModelResourceLocation(i, meta, new ModelResourceLocation(loc, "inventory"));
	}

	public static void registerItemModel(Block b, int meta)
	{
		registerItemModel(Item.getItemFromBlock(b), meta);
	}
}
