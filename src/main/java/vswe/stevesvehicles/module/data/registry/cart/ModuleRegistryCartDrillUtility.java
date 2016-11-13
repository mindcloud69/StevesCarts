package vswe.stevesvehicles.module.data.registry.cart;

import static vswe.stevesvehicles.item.ComponentTypes.ADVANCED_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.EYE_OF_GALGADOR;
import static vswe.stevesvehicles.item.ComponentTypes.GALGADORIAN_METAL;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.rendering.models.cart.ModelLiquidSensors;
import vswe.stevesvehicles.module.cart.addon.ModuleCreativeIncinerator;
import vswe.stevesvehicles.module.cart.addon.ModuleDrillIntelligence;
import vswe.stevesvehicles.module.cart.addon.ModuleIncinerator;
import vswe.stevesvehicles.module.cart.addon.ModuleLiquidSensors;
import vswe.stevesvehicles.module.cart.addon.ModuleOreTracker;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleDataGroup;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.module.data.registry.ModuleRegistryTanks;
import vswe.stevesvehicles.vehicle.VehicleRegistry;

public class ModuleRegistryCartDrillUtility extends ModuleRegistry {
	public ModuleRegistryCartDrillUtility() {
		super("cart.drill_utility");
		ModuleData liquidSensors = new ModuleData("liquid_sensors", ModuleLiquidSensors.class, 27) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Sensor", new ModelLiquidSensors());
			}
		};
		liquidSensors.addShapedRecipe(Items.REDSTONE, null, Items.REDSTONE, Items.LAVA_BUCKET, Items.DIAMOND, Items.WATER_BUCKET, Items.IRON_INGOT, ADVANCED_PCB, Items.IRON_INGOT);
		liquidSensors.addVehicles(VehicleRegistry.CART);
		register(liquidSensors);
		ModuleData incinerator = new ModuleData("incinerator", ModuleIncinerator.class, 23);
		incinerator.addShapedRecipe(Blocks.NETHER_BRICK, Blocks.NETHER_BRICK, Blocks.NETHER_BRICK, Blocks.OBSIDIAN, Blocks.FURNACE, Blocks.OBSIDIAN, Blocks.NETHER_BRICK, Blocks.NETHER_BRICK, Blocks.NETHER_BRICK);
		incinerator.addVehicles(VehicleRegistry.CART);
		incinerator.addRequirement(ModuleDataGroup.getGroup(ModuleRegistryTanks.TANK_KEY));
		register(incinerator);
		ModuleData cheatIncinerator = new ModuleData("creative_incinerator", ModuleCreativeIncinerator.class, 1);
		cheatIncinerator.addVehicles(VehicleRegistry.CART);
		register(cheatIncinerator);
		ModuleData intelligence = new ModuleData("drill_intelligence", ModuleDrillIntelligence.class, 21);
		intelligence.addShapedRecipe(Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.IRON_INGOT, ADVANCED_PCB, Items.IRON_INGOT, ADVANCED_PCB, Items.REDSTONE, ADVANCED_PCB);
		intelligence.addVehicles(VehicleRegistry.CART);
		register(intelligence);
		ModuleData extractor = new ModuleData("ore_extractor", ModuleOreTracker.class, 80);
		extractor.addShapedRecipe(Blocks.REDSTONE_TORCH, null, Blocks.REDSTONE_TORCH, EYE_OF_GALGADOR, GALGADORIAN_METAL, EYE_OF_GALGADOR, Items.QUARTZ, ADVANCED_PCB, Items.QUARTZ);
		extractor.addVehicles(VehicleRegistry.CART);
		register(extractor);
	}

	private ModuleDataGroup drills = ModuleDataGroup.getGroup(ModuleRegistryCartTools.DRILL_KEY);

	@Override
	public void register(ModuleData moduleData) {
		super.register(moduleData);
		moduleData.addRequirement(drills);
	}
}
