package stevesvehicles.common.modules.datas.registries;

import static stevesvehicles.common.items.ComponentTypes.ADVANCED_PCB;
import static stevesvehicles.common.items.ComponentTypes.DYNAMITE;
import static stevesvehicles.common.items.ComponentTypes.REFINED_HARDENER;
import static stevesvehicles.common.items.ComponentTypes.REINFORCED_METAL;
import static stevesvehicles.common.items.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.rendering.models.common.ModelDynamite;
import stevesvehicles.client.rendering.models.common.ModelShield;
import stevesvehicles.common.modules.common.addon.ModuleShield;
import stevesvehicles.common.modules.common.addon.chunk.ModuleChunkLoader;
import stevesvehicles.common.modules.common.attachment.ModuleDynamite;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleSide;
import stevesvehicles.common.vehicles.VehicleRegistry;

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
