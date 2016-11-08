package vswe.stevescarts;

import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import vswe.stevescarts.Blocks.ModBlocks;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.CraftingHandler;
import vswe.stevescarts.Helpers.CreativeTabSC2;
import vswe.stevescarts.Helpers.EntityCake;
import vswe.stevescarts.Helpers.EntityEasterEgg;
import vswe.stevescarts.Helpers.GiftItem;
import vswe.stevescarts.Helpers.TradeHandler;
import vswe.stevescarts.Helpers.WoodFuelHandler;
import vswe.stevescarts.Items.ItemBlockStorage;
import vswe.stevescarts.Items.ModItems;
import vswe.stevescarts.Listeners.ChunkListener;
import vswe.stevescarts.Listeners.MobDeathListener;
import vswe.stevescarts.Listeners.MobInteractListener;
import vswe.stevescarts.Listeners.OverlayRenderer;
import vswe.stevescarts.Listeners.PlayerSleepListener;
import vswe.stevescarts.Listeners.TicketListener;
import vswe.stevescarts.TileEntities.TileEntityCargo;
import vswe.stevescarts.Upgrades.AssemblerUpgrade;

@Mod(modid = "stevescarts", name = "Steve's Carts 2", version = "2.0.0.b18")
public class StevesCarts {
	public static boolean hasGreenScreen;
	public static boolean isChristmas;
	public static boolean isHalloween;
	public static boolean isEaster;
	public static boolean freezeCartSimulation;
	public static boolean renderSteve;
	public static boolean arcadeDevOperator;
	@SidedProxy(clientSide = "vswe.stevescarts.ClientProxy", serverSide = "vswe.stevescarts.CommonProxy")
	public static CommonProxy proxy;
	@Mod.Instance("StevesCarts")
	public static StevesCarts instance;
	public static CreativeTabSC2 tabsSC2;
	public static CreativeTabSC2 tabsSC2Components;
	public static CreativeTabSC2 tabsSC2Blocks;
	public int maxDynamites;
	public boolean useArcadeSounds;
	public boolean useArcadeMobSounds;
	public static FMLEventChannel packetHandler;
	public static Logger logger;
	public TradeHandler tradeHandler;

	public StevesCarts() {
		this.maxDynamites = 50;
	}

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		StevesCarts.logger = event.getModLog();
		StevesCarts.packetHandler = NetworkRegistry.INSTANCE.newEventDrivenChannel("SC2");
		final Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		this.maxDynamites = Math.min(this.maxDynamites, config.get("Settings", "MaximumNumberOfDynamites", this.maxDynamites).getInt(this.maxDynamites));
		this.useArcadeSounds = config.get("Settings", "useArcadeSounds", true).getBoolean(true);
		this.useArcadeMobSounds = config.get("Settings", "useTetrisMobSounds", true).getBoolean(true);
		ModItems.preBlockInit(config);
		ItemBlockStorage.init();
		ModBlocks.init();
		ModItems.postBlockInit(config);
		AssemblerUpgrade.init();
		this.initCart(0, MinecartModular.class);
		EntityRegistry.registerModEntity(EntityEasterEgg.class, "Egg.Vswe", 2, StevesCarts.instance, 80, 3, true);
		EntityRegistry.registerModEntity(EntityCake.class, "Cake.Vswe", 3, StevesCarts.instance, 80, 3, true);
		StevesCarts.proxy.soundInit();
		config.save();
	}

	@Mod.EventHandler
	public void load(final FMLInitializationEvent evt) {
		StevesCarts.packetHandler.register(new PacketHandler());
		new OverlayRenderer();
		new TicketListener();
		new ChunkListener();
		new CraftingHandler();
		new WoodFuelHandler();
		if (StevesCarts.isChristmas) {
			this.tradeHandler = new TradeHandler();
			new MobDeathListener();
			new MobInteractListener();
			new PlayerSleepListener();
		}
		GiftItem.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(StevesCarts.instance, StevesCarts.proxy);
		StevesCarts.proxy.renderInit();
		StevesCarts.tabsSC2Blocks.setIcon(new ItemStack(ModBlocks.CART_ASSEMBLER.getBlock(), 1));
		TileEntityCargo.loadSelectionSettings();
		ModItems.addRecipes();
		ModBlocks.addRecipes();
	}

	private void initCart(final int ID, final Class<? extends MinecartModular> cart) {
		EntityRegistry.registerModEntity(cart, "Minecart.Vswe." + ID, ID, StevesCarts.instance, 80, 3, true);
	}

	static {
		StevesCarts.hasGreenScreen = false;
		StevesCarts.isChristmas = false;
		StevesCarts.isHalloween = false;
		StevesCarts.isEaster = false;
		StevesCarts.freezeCartSimulation = false;
		StevesCarts.renderSteve = false;
		StevesCarts.arcadeDevOperator = false;
		StevesCarts.tabsSC2 = new CreativeTabSC2("SC2Modules");
		StevesCarts.tabsSC2Components = new CreativeTabSC2("SC2Items");
		StevesCarts.tabsSC2Blocks = new CreativeTabSC2("SC2Blocks");
	}
}
