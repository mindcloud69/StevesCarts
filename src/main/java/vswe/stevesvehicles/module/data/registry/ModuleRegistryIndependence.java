package vswe.stevesvehicles.module.data.registry;

import static vswe.stevesvehicles.item.ComponentTypes.ADVANCED_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.DYNAMITE;
import static vswe.stevesvehicles.item.ComponentTypes.REFINED_HARDENER;
import static vswe.stevesvehicles.item.ComponentTypes.REINFORCED_METAL;
import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.rendering.models.common.ModelDynamite;
import vswe.stevesvehicles.client.rendering.models.common.ModelShield;
import vswe.stevesvehicles.module.common.addon.ModuleShield;
import vswe.stevesvehicles.module.common.addon.chunk.ModuleChunkLoader;
import vswe.stevesvehicles.module.common.attachment.ModuleDynamite;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleSide;
import vswe.stevesvehicles.vehicle.VehicleRegistry;

public class ModuleRegistryIndependence extends ModuleRegistry {
	public ModuleRegistryIndependence() {
		super("common.independence");
		ModuleData dynamite = new ModuleData("dynamite_carrier", ModuleDynamite.class, 3) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Tnt", new ModelDynamite());
			}
		};
		dynamite.addShapedRecipe(null, DYNAMITE, null, DYNAMITE, Items.FLINT_AND_STEEL, DYNAMITE, null, DYNAMITE, null);
		dynamite.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		dynamite.addSides(ModuleSide.TOP);
		register(dynamite);
		ModuleData shield = new ModuleData("divine_shield", ModuleShield.class, 60) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Shield", new ModelShield());
				setModelMultiplier(0.68F);
			}
		};
		shield.addShapedRecipe(Blocks.OBSIDIAN, REFINED_HARDENER, Blocks.OBSIDIAN, REFINED_HARDENER, Blocks.DIAMOND_BLOCK, REFINED_HARDENER, Blocks.OBSIDIAN, REFINED_HARDENER, Blocks.OBSIDIAN);
		shield.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(shield);
		ModuleData chunk = new ModuleData("chunk_loader", ModuleChunkLoader.class, 84);
		chunk.addShapedRecipe(null, Items.ENDER_PEARL, null, SIMPLE_PCB, Items.IRON_INGOT, SIMPLE_PCB, REINFORCED_METAL, ADVANCED_PCB, REINFORCED_METAL);
		chunk.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(chunk);
	}
}
