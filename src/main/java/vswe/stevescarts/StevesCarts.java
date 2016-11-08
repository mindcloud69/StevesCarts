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
import vswe.stevescarts.blocks.ModBlocks;
import vswe.stevescarts.blocks.tileentities.TileEntityCargo;
import vswe.stevescarts.entitys.EntityCake;
import vswe.stevescarts.entitys.EntityEasterEgg;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.handlers.TradeHandler;
import vswe.stevescarts.handlers.WoodFuelHandler;
import vswe.stevescarts.helpers.CreativeTabSC2;
import vswe.stevescarts.helpers.GiftItem;
import vswe.stevescarts.helpers.crafting.CraftingHandler;
import vswe.stevescarts.items.ItemBlockStorage;
import vswe.stevescarts.items.ModItems;
import vswe.stevescarts.listeners.ChunkListener;
import vswe.stevescarts.listeners.MobDeathListener;
import vswe.stevescarts.listeners.MobInteractListener;
import vswe.stevescarts.listeners.OverlayRenderer;
import vswe.stevescarts.listeners.PlayerSleepListener;
import vswe.stevescarts.listeners.TicketListener;
import vswe.stevescarts.upgrades.AssemblerUpgrade;

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
	@Mod.Instance("stevescarts")
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
		this.initCart(0, EntityMinecartModular.class);
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

	private void initCart(final int ID, final Class<? extends EntityMinecartModular> cart) {
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
