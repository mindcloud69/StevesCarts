package vswe.stevescarts;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
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
import vswe.stevescarts.helpers.DetectorType;
import vswe.stevescarts.helpers.MinecartSoundMuter;
import vswe.stevescarts.items.ModItems;
import vswe.stevescarts.modules.data.ModuleData;
import vswe.stevescarts.renders.ItemStackRenderer;
import vswe.stevescarts.renders.RendererCart;
import vswe.stevescarts.renders.model.ItemModelManager;
import vswe.stevescarts.renders.model.ModelGenerator;

public class ClientProxy extends CommonProxy {

	@Override
	public void init() {

		ModuleData.initModels();

		ModelLoader.setCustomStateMapper(ModBlocks.UPGRADE.getBlock(), new DefaultStateMapper() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				String resourceDomain = Block.REGISTRY.getNameForObject(state.getBlock()).getResourceDomain();
				String propertyString = getPropertyString(state.getProperties());
				return new ModelResourceLocation(resourceDomain + ":upgrade", propertyString);
			}
		});
	}

	public class RenderManagerCart implements IRenderFactory<EntityMinecartModular> {

		@Override
		public Render<? super EntityMinecartModular> createRenderFor(RenderManager manager) {
			return new RendererCart(manager);
		}
	}

	@Override
	public void preInit() {
		ItemModelManager.load(); //Called in pre-init
		RenderingRegistry.registerEntityRenderingHandler(EntityMinecartModular.class, new RenderManagerCart()); //Needs to be done after the mc ones have been done
		new SoundHandler();
		new MinecartSoundMuter();
	}

	@Override
	public void loadComplete() {
		super.loadComplete();
		//Done here to try and load after all other mods, as some mods override this
		TileEntityItemStackRenderer.instance = new ItemStackRenderer(TileEntityItemStackRenderer.instance);
	}

	@Override
	public void postInit() {
		super.postInit();
		ModelGenerator.postInit();
	}

	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().world;
	}

	@Override
	public void initItemModels() {
		registerItemModel(ModBlocks.CART_ASSEMBLER.getBlock(), 0);
		registerItemModel(ModBlocks.CARGO_MANAGER.getBlock(), 0);
		registerItemModel(ModItems.carts, 0);
		registerItemModel(ModBlocks.LIQUID_MANAGER.getBlock(), 0);
		for (int i = 0; i < 3; ++i) {
			ModelResourceLocation location = new ModelResourceLocation("stevescarts:BlockMetalStorage", "type=" + i);
			ModelLoader.setCustomModelResourceLocation(ModItems.storages, i, location);
		}
		registerItemModel(ModBlocks.JUNCTION.getBlock(), 0);
		registerItemModel(ModBlocks.ADVANCED_DETECTOR.getBlock(), 0);
		registerItemModel(ModBlocks.MODULE_TOGGLER.getBlock(), 0);
		registerItemModel(ModBlocks.EXTERNAL_DISTRIBUTOR.getBlock(), 0);
		registerItemModel(ModBlocks.DETECTOR_UNIT.getBlock(), 0);
		for (int i = 0; i < 5; ++i) {
			ModelResourceLocation location = new ModelResourceLocation("stevescarts:BlockDetector", "detectortype=" + DetectorType.getTypeFromint(i).getName() + ",powered=false");
			ModelLoader.setCustomModelResourceLocation(ModItems.detectors, i, location);
		}
	}

	public static void registerItemModel(Item i, int meta) {
		ResourceLocation loc = i.getRegistryName();
		ModelLoader.setCustomModelResourceLocation(i, meta, new ModelResourceLocation(loc, "inventory"));
	}

	public static void registerItemModel(Block b, int meta) {
		registerItemModel(Item.getItemFromBlock(b), meta);
	}
}
