package stevesvehicles.common.modules.datas.registries.cart;

import static stevesvehicles.common.items.ComponentTypes.ADVANCED_PCB;
import static stevesvehicles.common.items.ComponentTypes.BLADE_ARM;
import static stevesvehicles.common.items.ComponentTypes.EMPTY_DISK;
import static stevesvehicles.common.items.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.rendering.models.cart.ModelLawnMower;
import stevesvehicles.common.modules.cart.addon.cultivation.ModuleModTrees;
import stevesvehicles.common.modules.cart.addon.cultivation.ModuleNetherWart;
import stevesvehicles.common.modules.cart.addon.cultivation.ModulePlantSize;
import stevesvehicles.common.modules.cart.attachment.ModuleFertilizer;
import stevesvehicles.common.modules.cart.attachment.ModuleFlowerRemover;
import stevesvehicles.common.modules.cart.attachment.ModuleHydrater;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleDataGroup;
import stevesvehicles.common.modules.datas.ModuleSide;
import stevesvehicles.common.modules.datas.registries.ModuleRegistry;
import stevesvehicles.common.modules.datas.registries.ModuleRegistryTanks;
import stevesvehicles.common.vehicles.VehicleRegistry;

public class ModuleRegistryCartCultivationUtil extends ModuleRegistry {
	public ModuleRegistryCartCultivationUtil() {
		super("cart.cultivation");
		loadFarmingUtil();
		loadWoodCuttingUtil();
	}

	private void loadFarmingUtil() {
		ModuleDataGroup farmers = ModuleDataGroup.getGroup(ModuleRegistryCartTools.FARM_KEY);
		ModuleData netherWart = new ModuleData("crop_nether_wart", ModuleNetherWart.class, 20);
		netherWart.addShapedRecipeWithSize(1, 2, Items.NETHER_WART, EMPTY_DISK);
		netherWart.addVehicles(VehicleRegistry.CART);
		netherWart.addRequirement(farmers);
		register(netherWart);
		ModuleData hydrator = new ModuleData("hydrator", ModuleHydrater.class, 6);
		hydrator.addShapedRecipeWithSize(3, 2, Items.IRON_INGOT, Items.GLASS_BOTTLE, Items.IRON_INGOT, null, Blocks.IRON_BARS, null);
		hydrator.addVehicles(VehicleRegistry.CART);
		hydrator.addRequirement(ModuleDataGroup.getGroup(ModuleRegistryTanks.TANK_KEY));
		register(hydrator);
		ModuleData fertilizer = new ModuleData("fertilizer", ModuleFertilizer.class, 10);
		fertilizer.addShapedRecipe(new ItemStack(Items.DYE, 1, 15), null, new ItemStack(Items.DYE, 1, 15), Items.GLASS_BOTTLE, Items.LEATHER, Items.GLASS_BOTTLE, Items.LEATHER, SIMPLE_PCB, Items.LEATHER);
		fertilizer.addVehicles(VehicleRegistry.CART);
		register(fertilizer);
		ModuleData mower = new ModuleData("lawn_mower", ModuleFlowerRemover.class, 38) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("LawnMower", new ModelLawnMower());
				setModelMultiplier(0.4F);
			}
		};
		mower.addShapedRecipe(BLADE_ARM, null, BLADE_ARM, null, SIMPLE_PCB, null, BLADE_ARM, null, BLADE_ARM);
		mower.addSides(ModuleSide.RIGHT, ModuleSide.LEFT);
		mower.addVehicles(VehicleRegistry.CART);
		register(mower);
	}

	private void loadWoodCuttingUtil() {
		ModuleDataGroup cutters = ModuleDataGroup.getGroup(ModuleRegistryCartTools.WOOD_KEY);
		ModuleData exotic = new ModuleData("tree_exotic", ModuleModTrees.class, 30);
		exotic.addShapedRecipeWithSize(1, 2, Blocks.SAPLING, EMPTY_DISK);
		exotic.addVehicles(VehicleRegistry.CART);
		exotic.addRequirement(cutters);
		register(exotic);
		ModuleData range = new ModuleData("planter_range_extender", ModulePlantSize.class, 20);
		range.addShapedRecipe(Items.REDSTONE, ADVANCED_PCB, Items.REDSTONE, null, Blocks.SAPLING, null, SIMPLE_PCB, Blocks.SAPLING, SIMPLE_PCB);
		range.addVehicles(VehicleRegistry.CART);
		range.addRequirement(cutters);
		register(range);
	}
}
