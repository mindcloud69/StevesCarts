package vswe.stevesvehicles;

import java.util.EnumSet;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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

import vswe.stevesvehicles.block.ModBlocks;
import vswe.stevesvehicles.buoy.EntityBuoy;
import vswe.stevesvehicles.client.gui.GuiHandler;
import vswe.stevesvehicles.client.gui.OverlayRenderer;
import vswe.stevesvehicles.client.rendering.ItemStackRenderer;
import vswe.stevesvehicles.client.rendering.RenderBoat;
import vswe.stevesvehicles.client.rendering.RenderBuoy;
import vswe.stevesvehicles.client.rendering.RenderCart;
import vswe.stevesvehicles.client.rendering.models.items.ItemModelManager;
import vswe.stevesvehicles.client.sounds.MinecartSoundMuter;
import vswe.stevesvehicles.client.sounds.SoundHandler;
import vswe.stevesvehicles.fancy.FancyPancyLoader;
import vswe.stevesvehicles.holiday.EntityEasterEgg;
import vswe.stevesvehicles.holiday.GiftItem;
import vswe.stevesvehicles.holiday.HolidayType;
import vswe.stevesvehicles.holiday.MobChristmasDrop;
import vswe.stevesvehicles.holiday.MobHatEquip;
import vswe.stevesvehicles.holiday.PlayerSockFiller;
import vswe.stevesvehicles.holiday.TradeHandler;
import vswe.stevesvehicles.item.ComponentTypes;
import vswe.stevesvehicles.item.ItemBlockStorage;
import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.module.common.addon.chunk.ChunkListener;
import vswe.stevesvehicles.module.common.addon.chunk.TicketListener;
import vswe.stevesvehicles.module.common.addon.projectile.EntityCake;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.network.PacketHandler;
import vswe.stevesvehicles.recipe.ModuleRecipeShaped;
import vswe.stevesvehicles.recipe.ModuleRecipeShapeless;
import vswe.stevesvehicles.registry.RegistrySynchronizer;
import vswe.stevesvehicles.tab.CreativeTabLoader;
import vswe.stevesvehicles.tileentity.TileEntityCargo;
import vswe.stevesvehicles.tileentity.detector.modulestate.registry.ModuleStateRegistry;
import vswe.stevesvehicles.upgrade.registry.UpgradeRegistry;
import vswe.stevesvehicles.vehicle.VehicleRegistry;
import vswe.stevesvehicles.vehicle.entity.EntityModularBoat;
import vswe.stevesvehicles.vehicle.entity.EntityModularCart;

@Mod(modid = "StevesVehicles", name = "Steve's Vehicles", version = GeneratedInfo.version)
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
	public final String textureHeader = "stevescarts";
	public static final String localStart = "SC2:";
	@Instance("StevesVehicles")
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
		EntityRegistry.registerModEntity(EntityEasterEgg.class, "Egg.Vswe", 20, instance, 80, 3, true);
		EntityRegistry.registerModEntity(EntityCake.class, "Cake.Vswe", 21, instance, 80, 3, true);
		EntityRegistry.registerModEntity(EntityBuoy.class, "buoy", 22, instance, 80, 3, false);
		if (event.getSide() == Side.CLIENT) {
			loadSounds();
		}
		config.save();
		ItemModelManager.load();
	}

	public TradeHandler tradeHandler;

	@EventHandler
	public void load(FMLInitializationEvent event) {
		CreativeTabLoader.postInit();
		RecipeSorter.register("steves_vehicles:shaped", ModuleRecipeShaped.class, RecipeSorter.Category.SHAPED, "before:minecraft:shaped before:steves_vehicles:shapeless");
		RecipeSorter.register("steves_vehicles:shapeless", ModuleRecipeShapeless.class, RecipeSorter.Category.SHAPELESS, "after:steves_vehicles:shaped");
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
	}

	@SideOnly(Side.CLIENT)
	private void loadRendering() {
		new FancyPancyLoader();
		// TODO move to the vehicle types?
		RenderingRegistry.registerEntityRenderingHandler(EntityModularCart.class, (RenderManager manager) -> new RenderCart(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityModularBoat.class, (RenderManager manager) -> new RenderBoat(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityEasterEgg.class, (RenderManager manager) -> new RenderSnowball(manager, null, Minecraft.getMinecraft().getRenderItem()) {
			@Override
			public ItemStack getStackToRender(Entity entityIn) {
				return new ItemStack(ModItems.component, 1, ComponentTypes.PAINTED_EASTER_EGG.getId());
			}
		});
		// StevesVehicles.instance.blockRenderer = new RendererUpgrade();
		RenderingRegistry.registerEntityRenderingHandler(EntityCake.class, (RenderManager manager) -> new RenderSnowball(manager, Items.CAKE, Minecraft.getMinecraft().getRenderItem()));
		for (ModuleData moduleData : ModuleRegistry.getAllModules()) {
			moduleData.loadClientValues();
		}
		RenderingRegistry.registerEntityRenderingHandler(EntityBuoy.class, (RenderManager manager) -> new RenderBuoy(manager));
	}

	@SideOnly(Side.CLIENT)
	private void loadSounds() {
		new SoundHandler();
		new MinecartSoundMuter();
	}

	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event){
		if(event.getSide() == Side.CLIENT){
			loadItemRenderer();
		}
	}

	@SideOnly(Side.CLIENT)
	private void loadItemRenderer(){
		//Done here to try and load after all other mods, as some mods override this
		TileEntityItemStackRenderer.instance = new ItemStackRenderer(TileEntityItemStackRenderer.instance);
	}
}