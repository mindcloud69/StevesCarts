package stevesvehicles.common.modules.datas.registries.cart;

import static stevesvehicles.common.items.ComponentTypes.GALGADORIAN_METAL;
import static stevesvehicles.common.items.ComponentTypes.GALGADORIAN_WHEELS;
import static stevesvehicles.common.items.ComponentTypes.IRON_WHEELS;
import static stevesvehicles.common.items.ComponentTypes.REINFORCED_METAL;
import static stevesvehicles.common.items.ComponentTypes.REINFORCED_WHEELS;
import static stevesvehicles.common.items.ComponentTypes.WOODEN_WHEELS;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.localization.entry.info.LocalizationMessage;
import stevesvehicles.client.rendering.models.cart.ModelHull;
import stevesvehicles.client.rendering.models.cart.ModelPigHead;
import stevesvehicles.client.rendering.models.cart.ModelPigHelmet;
import stevesvehicles.client.rendering.models.cart.ModelPigTail;
import stevesvehicles.client.rendering.models.cart.ModelPumpkinHull;
import stevesvehicles.client.rendering.models.cart.ModelPumpkinHullTop;
import stevesvehicles.client.rendering.models.common.ModelHullTop;
import stevesvehicles.common.core.StevesVehicles;
import stevesvehicles.common.holiday.HolidayType;
import stevesvehicles.common.modules.common.hull.HullCreative;
import stevesvehicles.common.modules.common.hull.HullGalgadorian;
import stevesvehicles.common.modules.common.hull.HullPig;
import stevesvehicles.common.modules.common.hull.HullPumpkin;
import stevesvehicles.common.modules.common.hull.HullReinforced;
import stevesvehicles.common.modules.common.hull.HullStandard;
import stevesvehicles.common.modules.common.hull.HullWood;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleDataHull;
import stevesvehicles.common.modules.datas.ModuleSide;
import stevesvehicles.common.modules.datas.registries.ModuleRegistry;
import stevesvehicles.common.vehicles.VehicleRegistry;

public class ModuleRegistryCartHulls extends ModuleRegistry {
	public ModuleRegistryCartHulls() {
		super("cart.hulls");
		ModuleData wood = new ModuleDataHull("wooden_hull", HullWood.class, 50, 1, 2, 0, 15) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/cart/wooden_hull.png")));
				addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cart/wooden_hull_top.png")));
			}
		};
		wood.addShapedRecipe("plankWood", null, "plankWood", "plankWood", "plankWood", "plankWood", WOODEN_WHEELS, null, WOODEN_WHEELS);
		wood.addVehicles(VehicleRegistry.CART);
		register(wood);
		ModuleData standard = new ModuleDataHull("standard_hull", HullStandard.class, 200, 3, 4, 6, 50) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/cart/standard_hull.png")));
				addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cart/standard_hull_top.png")));
			}
		};
		standard.addShapedRecipe(Items.IRON_INGOT, null, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT, IRON_WHEELS, null, IRON_WHEELS);
		standard.addVehicles(VehicleRegistry.CART);
		register(standard);
		ModuleData reinforced = new ModuleDataHull("reinforced_hull", HullReinforced.class, 500, 5, 6, 12, 150) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/cart/reinforced_hull.png")));
				addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cart/reinforced_hull_top.png")));
			}
		};
		reinforced.addShapedRecipe(REINFORCED_METAL, null, REINFORCED_METAL, REINFORCED_METAL, REINFORCED_METAL, REINFORCED_METAL, REINFORCED_WHEELS, null, REINFORCED_WHEELS);
		reinforced.addVehicles(VehicleRegistry.CART);
		register(reinforced);
		ModuleData galgadorian = new ModuleDataHull("galgadorian_hull", HullGalgadorian.class, 1000, 5, 6, 12, 150) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/cart/galgadorian_hull.png")));
				addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cart/galgadorian_hull_top.png")));
			}
		};
		galgadorian.addShapedRecipe(GALGADORIAN_METAL, null, GALGADORIAN_METAL, GALGADORIAN_METAL, GALGADORIAN_METAL, GALGADORIAN_METAL, GALGADORIAN_WHEELS, null, GALGADORIAN_WHEELS);
		galgadorian.addVehicles(VehicleRegistry.CART);
		register(galgadorian);
		ModuleData pumpkin = new ModuleDataHull("pumpkin_chariot", HullPumpkin.class, 40, 1, 2, 0, 15) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Hull", new ModelPumpkinHull(ResourceHelper.getResource("/models/cart/pumpkin_hull.png"), ResourceHelper.getResource("/models/cart/wooden_hull.png")));
				addModel("Top", new ModelPumpkinHullTop(ResourceHelper.getResource("/models/cart/pumpkin_hull_top.png"), ResourceHelper.getResource("/models/cart/wooden_hull_top.png")));
			}
		};
		pumpkin.addShapedRecipe("plankWood", null, "plankWood", "plankWood", Blocks.PUMPKIN, "plankWood", WOODEN_WHEELS, null, WOODEN_WHEELS);
		if (!StevesVehicles.holidays.contains(HolidayType.HALLOWEEN)) {
			pumpkin.lock();
		}
		pumpkin.addVehicles(VehicleRegistry.CART);
		register(pumpkin);
		ModuleData pig = new ModuleDataHull("mechanical_pig", HullPig.class, 150, 2, 4, 4, 50) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/cart/pig_hull.png")));
				addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cart/pig_hull_top.png")));
				addModel("Head", new ModelPigHead());
				addModel("Tail", new ModelPigTail());
				addModel("Helmet", new ModelPigHelmet(false));
				addModel("Helmet_Overlay", new ModelPigHelmet(true));
			}
		};
		pig.addSides(ModuleSide.FRONT);
		pig.addMessage(LocalizationMessage.THUNDER_PIG);
		pig.addShapedRecipe(Items.PORKCHOP, null, Items.PORKCHOP, Items.PORKCHOP, Items.PORKCHOP, Items.PORKCHOP, IRON_WHEELS, null, IRON_WHEELS);
		pig.addVehicles(VehicleRegistry.CART);
		register(pig);
		ModuleData creative = new ModuleDataHull("creative_hull", HullCreative.class, 10000, 5, 6, 12, 150) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/cart/creative_hull.png")));
				addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cart/creative_hull_top.png")));
			}
		};
		creative.addVehicles(VehicleRegistry.CART);
		register(creative);
	}
}
