package stevesvehicles.common.core;

import java.util.EnumSet;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.RecipeSorter;
import stevesvehicles.client.fancy.FancyPancyLoader;
import stevesvehicles.client.gui.GuiHandler;
import stevesvehicles.client.gui.OverlayRenderer;
import stevesvehicles.client.rendering.ItemStackRenderer;
import stevesvehicles.client.rendering.RenderFactorys;
import stevesvehicles.client.rendering.models.items.ItemModelManager;
import stevesvehicles.client.sounds.MinecartSoundMuter;
import stevesvehicles.client.sounds.SoundHandler;
import stevesvehicles.common.blocks.ModBlocks;
import stevesvehicles.common.blocks.tileentitys.TileEntityCargo;
import stevesvehicles.common.blocks.tileentitys.detector.modulestate.registry.ModuleStateRegistry;
import stevesvehicles.common.core.tabs.CreativeTabLoader;
import stevesvehicles.common.entitys.buoy.EntityBuoy;
import stevesvehicles.common.holiday.EntityEasterEgg;
import stevesvehicles.common.holiday.GiftItem;
import stevesvehicles.common.holiday.HolidayType;
import stevesvehicles.common.holiday.MobChristmasDrop;
import stevesvehicles.common.holiday.MobHatEquip;
import stevesvehicles.common.holiday.PlayerSockFiller;
import stevesvehicles.common.holiday.TradeHandler;
import stevesvehicles.common.items.ItemBlockStorage;
import stevesvehicles.common.items.ModItems;
import stevesvehicles.common.modules.common.addon.chunk.ChunkListener;
import stevesvehicles.common.modules.common.addon.chunk.TicketListener;
import stevesvehicles.common.modules.common.addon.projectile.EntityCake;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.registries.ModuleRegistry;
import stevesvehicles.common.network.PacketHandler;
import stevesvehicles.common.recipe.ShapedModuleRecipe;
import stevesvehicles.common.recipe.ShapelessModuleRecipe;
import stevesvehicles.common.registries.RegistrySynchronizer;
import stevesvehicles.common.upgrades.registries.UpgradeRegistry;
import stevesvehicles.common.vehicles.VehicleRegistry;
import stevesvehicles.common.vehicles.entitys.EntityModularBoat;
import stevesvehicles.common.vehicles.entitys.EntityModularCart;

@Mod(modid = Constants.MOD_ID, name = Constants.NAME, version = Constants.version)
public class StevesVehicles {
	public static boolean debugMode = false;
	public static boolean hasGreenScreen = false;
	public static boolean freezeCartSimulation = false;
	public static boolean renderSteve = false;
	public static boolean arcadeDevOperator = false;
	public static EnumSet<HolidayType> holidays = EnumSet.allOf(HolidayType.class);
	public static final String CHANNEL = "SC2";
	public final String texturePath = "/assets/stevescarts/textures";
	// public final String soundPath = "/assets/stevescarts/sounds";
	public static final String localStart = "SC2:";
	@Instance(Constants.MOD_ID)
	public static StevesVehicles instance;
	// public ISimpleBlockRenderingHandler blockRenderer;
	public int maxDynamites = 50;
	public boolean useArcadeSounds;
	public boolean useArcadeMobSounds;
	public static FMLEventChannel packetHandler;
	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// TODO make sure everything here is called in the correct order and
		// still allow other mods to hook into it and still maintaining the
		// correct sequence
		packetHandler = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL);
		VehicleRegistry.init();
		ModuleRegistry.init();
		UpgradeRegistry.init();
		ModuleStateRegistry.init();
		CreativeTabLoader.init();
		logger = event.getModLog();
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		maxDynamites = Math.min(maxDynamites, config.get("Settings", "MaximumNumberOfDynamites", maxDynamites).getInt(maxDynamites));
		useArcadeSounds = config.get("Settings", "useArcadeSounds", true).getBoolean(true);
		useArcadeMobSounds = config.get("Settings", "useTetrisMobSounds", true).getBoolean(true);
		ModItems.preBlockInit(config);
		ItemBlockStorage.init();
		ModBlocks.init();
		ModItems.postBlockInit(config);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.MOD_ID, "egg"), EntityEasterEgg.class, "Egg.Vswe", 20, instance, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.MOD_ID, "cake"), EntityCake.class, "Cake.Vswe", 21, instance, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Constants.MOD_ID, "buoy"), EntityBuoy.class, "buoy", 22, instance, 80, 3, false);
		if (event.getSide() == Side.CLIENT) {
			loadClient();
		}
		config.save();
	}

	public TradeHandler tradeHandler;

	@EventHandler
	public void load(FMLInitializationEvent event) {
		CreativeTabLoader.postInit();
		RecipeSorter.register(Constants.MOD_ID + ":shaped", ShapedModuleRecipe.class, RecipeSorter.Category.SHAPED, "before:minecraft:shaped before:steves_vehicles:shapeless");
		RecipeSorter.register(Constants.MOD_ID + ":shapeless", ShapelessModuleRecipe.class, RecipeSorter.Category.SHAPELESS, "after:steves_vehicles:shaped");
		packetHandler.register(new PacketHandler());
		new OverlayRenderer();
		new TicketListener();
		new ChunkListener();
		if (holidays.contains(HolidayType.CHRISTMAS)) {
			tradeHandler = new TradeHandler();
			new MobChristmasDrop();
			new MobHatEquip();
			new PlayerSockFiller();
		}
		new RegistrySynchronizer();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		if (event.getSide() == Side.CLIENT) {
			loadRendering();
		}
		TileEntityCargo.loadSelectionSettings();
		ModItems.addRecipes();
		ModBlocks.addRecipes();
		GiftItem.init();
		UpgradeRegistry.postInit();
	}

	@SideOnly(Side.CLIENT)
	private void loadRendering() {
		new FancyPancyLoader();
	}

	@SideOnly(Side.CLIENT)
	private void loadClient() {
		new SoundHandler();
		new MinecartSoundMuter();
		ItemModelManager.load();
		// TODO move to the vehicle types?
		RenderingRegistry.registerEntityRenderingHandler(EntityModularCart.class, new RenderFactorys.ModularCartFactory());
		RenderingRegistry.registerEntityRenderingHandler(EntityModularBoat.class, new RenderFactorys.ModularBoatFactory());
		RenderingRegistry.registerEntityRenderingHandler(EntityEasterEgg.class, new RenderFactorys.EasterEggFactory());
		// StevesVehicles.instance.blockRenderer = new RendererUpgrade();
		RenderingRegistry.registerEntityRenderingHandler(EntityCake.class, new RenderFactorys.CakeFactory());
		for (ModuleData moduleData : ModuleRegistry.getAllModules()) {
			moduleData.loadClientValues();
		}
		RenderingRegistry.registerEntityRenderingHandler(EntityBuoy.class, new RenderFactorys.BuoyFactory());
	}

	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {
		if (event.getSide() == Side.CLIENT) {
			loadItemRenderer();
		}
	}

	@SideOnly(Side.CLIENT)
	private void loadItemRenderer() {
		// Done here to try and load after all other mods, as some mods override
		// this
		TileEntityItemStackRenderer.instance = new ItemStackRenderer(TileEntityItemStackRenderer.instance);
	}
}
