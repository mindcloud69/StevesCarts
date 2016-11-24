package stevesvehicles.common.modules.datas.registries;

import static stevesvehicles.common.items.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import stevesvehicles.common.modules.common.addon.recipe.ModuleCrafter;
import stevesvehicles.common.modules.common.addon.recipe.ModuleCrafterAdv;
import stevesvehicles.common.modules.common.addon.recipe.ModuleSmelter;
import stevesvehicles.common.modules.common.addon.recipe.ModuleSmelterAdv;
import stevesvehicles.common.modules.common.attachment.ModuleMilker;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleDataGroup;
import stevesvehicles.common.vehicles.VehicleRegistry;

public class ModuleRegistryProduction extends ModuleRegistry {
	public ModuleRegistryProduction() {
		super("common.production");
		ModuleData crafter = new ModuleData("crafter", ModuleCrafter.class, 22);
		crafter.addShapelessRecipe(SIMPLE_PCB, Blocks.CRAFTING_TABLE);
		crafter.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		crafter.setAllowDuplicate(true);
		register(crafter);
		ModuleData smelter = new ModuleData("smelter", ModuleSmelter.class, 22);
		smelter.addShapelessRecipe(SIMPLE_PCB, Blocks.FURNACE);
		smelter.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		smelter.setAllowDuplicate(true);
		register(smelter);
		ModuleData crafterAdvanced = new ModuleData("advanced_crafter", ModuleCrafterAdv.class, 42);
		crafterAdvanced.addShapedRecipe(Items.REDSTONE, SIMPLE_PCB, Items.REDSTONE, SIMPLE_PCB, crafter, SIMPLE_PCB, Items.REDSTONE, SIMPLE_PCB, Items.REDSTONE);
		crafterAdvanced.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		crafterAdvanced.setAllowDuplicate(true);
		register(crafterAdvanced);
		ModuleData smelterAdvanced = new ModuleData("advanced_smelter", ModuleSmelterAdv.class, 42);
		smelterAdvanced.addShapedRecipe(Items.REDSTONE, SIMPLE_PCB, Items.REDSTONE, SIMPLE_PCB, smelter, SIMPLE_PCB, Items.REDSTONE, SIMPLE_PCB, Items.REDSTONE);
		smelterAdvanced.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		smelterAdvanced.setAllowDuplicate(true);
		register(smelterAdvanced);
		ModuleData milker = new ModuleData("milker", ModuleMilker.class, 26);
		milker.addShapedRecipe(Items.WHEAT, Items.WHEAT, Items.WHEAT, SIMPLE_PCB, Items.BUCKET, SIMPLE_PCB, null, SIMPLE_PCB, null);
		milker.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		milker.addRequirement(ModuleDataGroup.getGroup(ModuleRegistryTravel.CAGE_KEY));
		register(milker);
	}
}
