package vswe.stevesvehicles.module.data.registry.cart;

import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.TRI_TORCH;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.rendering.models.cart.ModelBridge;
import vswe.stevesvehicles.client.rendering.models.cart.ModelRailer;
import vswe.stevesvehicles.client.rendering.models.cart.ModelToolPlate;
import vswe.stevesvehicles.client.rendering.models.cart.ModelTorchPlacer;
import vswe.stevesvehicles.client.rendering.models.cart.ModelTrackRemover;
import vswe.stevesvehicles.module.cart.addon.ModuleHeightControl;
import vswe.stevesvehicles.module.cart.attachment.ModuleBridge;
import vswe.stevesvehicles.module.cart.attachment.ModuleRailer;
import vswe.stevesvehicles.module.cart.attachment.ModuleRailerLarge;
import vswe.stevesvehicles.module.cart.attachment.ModuleRemover;
import vswe.stevesvehicles.module.cart.attachment.ModuleTorch;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleSide;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.vehicle.VehicleRegistry;

public class ModuleRegistryCartRails extends ModuleRegistry {
	public ModuleRegistryCartRails() {
		super("cart.rails");

		ModuleData railer = new ModuleData("railer", ModuleRailer.class, 3) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Rails", new ModelRailer(3));
			}
		};

		railer.addShapedRecipe( Blocks.STONE,       Blocks.STONE,       Blocks.STONE,
				Items.IRON_INGOT,   Blocks.RAIL,        Items.IRON_INGOT,
				Blocks.STONE,       Blocks.STONE,       Blocks.STONE);

		railer.addVehicles(VehicleRegistry.CART);
		register(railer);



		ModuleData railerLarge = new ModuleData("large_railer", ModuleRailerLarge.class, 17) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Rails", new ModelRailer(6));
			}
		};

		railerLarge.addShapedRecipe(    Items.IRON_INGOT,       Items.IRON_INGOT,       Items.IRON_INGOT,
				railer,                 Blocks.RAIL,            railer,
				Blocks.STONE,           Blocks.STONE,           Blocks.STONE);

		railerLarge.addVehicles(VehicleRegistry.CART);
		register(railerLarge);

		ModuleData.addNemesis(railer, railerLarge);


		ModuleData torch = new ModuleData("torch_placer", ModuleTorch.class, 14) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Torch", new ModelTorchPlacer());
			}
		};

		torch.addShapedRecipe(  TRI_TORCH,              null,                   TRI_TORCH,
				Items.IRON_INGOT,       null,                   Items.IRON_INGOT,
				Items.IRON_INGOT,       Items.IRON_INGOT,       Items.IRON_INGOT);

		torch.addSides(ModuleSide.RIGHT, ModuleSide.LEFT);
		torch.addVehicles(VehicleRegistry.CART);
		register(torch);


		ModuleData bridge = new ModuleData("bridge_builder", ModuleBridge.class, 14) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Bridge", new ModelBridge());
				addModel("Plate", new ModelToolPlate());
			}
		};

		bridge.addShapedRecipe(    null,                   Items.REDSTONE,     null,
				Blocks.BRICK_BLOCK,     SIMPLE_PCB,         Blocks.BRICK_BLOCK,
				null,                   Blocks.PISTON,      null);

		bridge.addVehicles(VehicleRegistry.CART);
		register(bridge);


		ModuleData remover = new ModuleData("track_remover", ModuleRemover.class, 8) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Remover", new ModelTrackRemover());
				setModelMultiplier(0.60F);
			}
		};

		remover.addShapedRecipe(    Items.IRON_INGOT,       Items.IRON_INGOT,       Items.IRON_INGOT,
				Items.IRON_INGOT,       null,                   Items.IRON_INGOT,
				Items.IRON_INGOT,       null,                   null);

		remover.addVehicles(VehicleRegistry.CART);
		remover.addSides(ModuleSide.TOP, ModuleSide.BACK);
		register(remover);


		ModuleData height = new ModuleData("height_controller", ModuleHeightControl.class, 20);
		height.addShapedRecipe(     null,              Items.COMPASS,       null,
				Items.PAPER,       SIMPLE_PCB,          Items.PAPER,
				Items.PAPER,       Items.PAPER,         Items.PAPER);

		height.addVehicles(VehicleRegistry.CART);
		register(height);
	}

}
