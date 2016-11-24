package stevesvehicles.common.modules.datas.registries;

import static stevesvehicles.common.items.ComponentTypes.ADVANCED_PCB;
import static stevesvehicles.common.items.ComponentTypes.GALGADORIAN_METAL;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import stevesvehicles.client.localization.entry.info.LocalizationGroup;
import stevesvehicles.common.modules.common.addon.ModuleCreativeSupplies;
import stevesvehicles.common.modules.common.addon.ModulePowerObserver;
import stevesvehicles.common.modules.common.addon.enchanter.ModuleEnchants;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleDataGroup;
import stevesvehicles.common.modules.datas.registries.cart.ModuleRegistryCartTools;
import stevesvehicles.common.vehicles.VehicleRegistry;

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
