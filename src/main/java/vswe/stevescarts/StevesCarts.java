package vswe.stevescarts;

import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vswe.stevescarts.blocks.ModBlocks;
import vswe.stevescarts.blocks.tileentities.TileEntityCargo;
import vswe.stevescarts.entitys.CartDataSerializers;
import vswe.stevescarts.entitys.EntityCake;
import vswe.stevescarts.entitys.EntityEasterEgg;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.handlers.EventHandler;
import vswe.stevescarts.handlers.EventHandlerChristmas;
import vswe.stevescarts.handlers.TradeHandler;
import vswe.stevescarts.helpers.CreativeTabSC2;
import vswe.stevescarts.helpers.GiftItem;
import vswe.stevescarts.items.ItemBlockStorage;
import vswe.stevescarts.items.ItemCartComponent;
import vswe.stevescarts.items.ModItems;
import vswe.stevescarts.plugins.PluginLoader;
import vswe.stevescarts.upgrades.AssemblerUpgrade;

import javax.annotation.Nonnull;

@Mod(modid = Constants.MOD_ID, name = Constants.NAME, version = Constants.VERSION)
public class StevesCarts {
	@SidedProxy(clientSide = "vswe.stevescarts.ClientProxy", serverSide = "vswe.stevescarts.CommonProxy")
	public static CommonProxy proxy;
	@Mod.Instance(Constants.MOD_ID)
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
		SCConfig.load(config);
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
		StevesCarts.proxy.preInit();
		StevesCarts.proxy.initItemModels();
		config.save();
		PluginLoader.preInit(event);
	}

	@Mod.EventHandler
	public void load(final FMLInitializationEvent evt) {
		StevesCarts.packetHandler.register(new PacketHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		GameRegistry.registerFuelHandler(new WoodFuelHandler());
		if (Constants.isChristmas) {
			this.tradeHandler = new TradeHandler();
			MinecraftForge.EVENT_BUS.register(new EventHandlerChristmas());
		}
		GiftItem.init();
		AssemblerUpgrade.initRecipes();
		NetworkRegistry.INSTANCE.registerGuiHandler(StevesCarts.instance, StevesCarts.proxy);
		StevesCarts.proxy.init();
		StevesCarts.tabsSC2Blocks.setIcon(new ItemStack(ModBlocks.CART_ASSEMBLER.getBlock(), 1));
		TileEntityCargo.loadSelectionSettings();
		ModItems.addRecipes();
		ModBlocks.addRecipes();
		CartDataSerializers.init();
		PluginLoader.init(evt);
	}

	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event){
		proxy.loadComplete();
	}

	public class WoodFuelHandler implements IFuelHandler {
		@Override
		public int getBurnTime(@Nonnull
			                       ItemStack fuel) {
			if (fuel != null && fuel.getItem() != null && fuel.getItem() == ModItems.component) {
				if (ItemCartComponent.isWoodLog(fuel)) {
					return 150;
				}
				if (ItemCartComponent.isWoodTwig(fuel)) {
					return 50;
				}
			}
			return 0;
		}
	}

	private void initCart(final int ID, final Class<? extends EntityMinecartModular> cart) {
		EntityRegistry.registerModEntity(cart, "Minecart.Vswe." + ID, ID, StevesCarts.instance, 80, 3, true);
	}

	static {
		StevesCarts.tabsSC2 = new CreativeTabSC2("SC2Modules");
		StevesCarts.tabsSC2Components = new CreativeTabSC2("SC2Items");
		StevesCarts.tabsSC2Blocks = new CreativeTabSC2("SC2Blocks");
	}
}
