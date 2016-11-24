package stevesvehicles.common.modules.datas.registries;

import static stevesvehicles.common.items.ComponentTypes.BLUE_PIGMENT;
import static stevesvehicles.common.items.ComponentTypes.FUSE;
import static stevesvehicles.common.items.ComponentTypes.GLASS_O_MAGIC;
import static stevesvehicles.common.items.ComponentTypes.GREEN_PIGMENT;
import static stevesvehicles.common.items.ComponentTypes.RED_PIGMENT;
import static stevesvehicles.common.items.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.rendering.models.common.ModelNote;
import stevesvehicles.common.modules.common.addon.ModuleColorRandomizer;
import stevesvehicles.common.modules.common.addon.ModuleColorizer;
import stevesvehicles.common.modules.common.addon.ModuleInvisible;
import stevesvehicles.common.modules.common.addon.ModuleLabel;
import stevesvehicles.common.modules.common.attachment.ModuleFirework;
import stevesvehicles.common.modules.common.attachment.ModuleNote;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleSide;
import stevesvehicles.common.vehicles.VehicleRegistry;

public class ModuleRegistryVisual extends ModuleRegistry {
	public ModuleRegistryVisual() {
		super("common.visual");
		ModuleData colorizer = new ModuleData("colorizer", ModuleColorizer.class, 15);
		colorizer.addShapedRecipe(RED_PIGMENT, GREEN_PIGMENT, BLUE_PIGMENT, Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT, null, Items.IRON_INGOT, null);
		colorizer.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(colorizer);
		ModuleData randomizer = new ModuleData("color_randomizer", ModuleColorRandomizer.class, 20);
		randomizer.addShapelessRecipe(colorizer, SIMPLE_PCB);
		randomizer.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(randomizer);
		ModuleData.addNemesis(colorizer, randomizer);
		ModuleData invisibility = new ModuleData("invisibility_core", ModuleInvisible.class, 21);
		invisibility.addShapedRecipe(null, GLASS_O_MAGIC, null, Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT, null, Items.IRON_INGOT, null);
		invisibility.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(invisibility);
		ModuleData firework = new ModuleData("firework_display", ModuleFirework.class, 30);
		firework.addShapedRecipe(Blocks.IRON_BARS, Blocks.DISPENSER, Blocks.IRON_BARS, Blocks.CRAFTING_TABLE, FUSE, Blocks.CRAFTING_TABLE, SIMPLE_PCB, Items.FLINT_AND_STEEL, SIMPLE_PCB);
		firework.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(firework);
		ModuleData note = new ModuleData("note_sequencer", ModuleNote.class, 30) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				setModelMultiplier(0.65F);
				addModel("Speakers", new ModelNote());
			}
		};
		note.addShapedRecipe(Blocks.NOTEBLOCK, null, Blocks.NOTEBLOCK, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.NOTEBLOCK, "plankWood", Items.REDSTONE, "plankWood");
		note.addSides(ModuleSide.RIGHT, ModuleSide.LEFT);
		note.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(note);
		ModuleData info = new ModuleData("information_provider", ModuleLabel.class, 12);
		info.addShapedRecipe(Blocks.GLASS_PANE, Blocks.GLASS_PANE, Blocks.GLASS_PANE, Items.IRON_INGOT, Items.GLOWSTONE_DUST, Items.IRON_INGOT, SIMPLE_PCB, Items.SIGN, SIMPLE_PCB);
		info.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(info);
	}
}
