package vswe.stevesvehicles.module.data.registry;

import static vswe.stevesvehicles.item.ComponentTypes.ADVANCED_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.GALGADORIAN_METAL;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import vswe.stevesvehicles.localization.entry.info.LocalizationGroup;
import vswe.stevesvehicles.module.common.addon.ModuleCreativeSupplies;
import vswe.stevesvehicles.module.common.addon.ModulePowerObserver;
import vswe.stevesvehicles.module.common.addon.enchanter.ModuleEnchants;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleDataGroup;
import vswe.stevesvehicles.module.data.registry.cart.ModuleRegistryCartTools;
import vswe.stevesvehicles.vehicle.VehicleRegistry;

public class ModuleRegistryUtility extends ModuleRegistry {
	public static final String ENCHANTABLE_KEY = "Enchantable";

	public ModuleRegistryUtility() {
		super("common.util");
		ModuleData observer = new ModuleData("power_observer", ModulePowerObserver.class, 12);
		observer.addShapedRecipe(null, Blocks.PISTON, null, Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT, Items.REDSTONE, ADVANCED_PCB, Items.REDSTONE);
		observer.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		observer.addRequirement(ModuleDataGroup.getGroup(ModuleRegistryEngines.ENGINE_KEY));
		register(observer);
		ModuleData enchanter = new ModuleData("enchanter", ModuleEnchants.class, 72);
		enchanter.addShapedRecipe(null, GALGADORIAN_METAL, null, Items.BOOK, Blocks.ENCHANTING_TABLE, Items.BOOK, Items.REDSTONE, ADVANCED_PCB, Items.REDSTONE);
		enchanter.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		enchanter.addRequirement(ModuleDataGroup.getCombinedGroup(ENCHANTABLE_KEY, LocalizationGroup.TOOL_SHOOTER, ModuleDataGroup.getGroup(ModuleRegistryCartTools.TOOL_KEY), ModuleDataGroup.getGroup(ModuleRegistryShooters.SHOOTER_KEY)));
		register(enchanter);
		ModuleData cheat = new ModuleData("creative_supplies", ModuleCreativeSupplies.class, 1);
		cheat.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(cheat);
	}
}
