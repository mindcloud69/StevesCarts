package vswe.stevesvehicles.module.data.registry.cart;

import static vswe.stevesvehicles.item.ComponentTypes.GRAPHICAL_INTERFACE;
import static vswe.stevesvehicles.item.ComponentTypes.REFINED_HANDLE;
import static vswe.stevesvehicles.item.ComponentTypes.SPEED_HANDLE;
import static vswe.stevesvehicles.item.ComponentTypes.WHEEL;

import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.rendering.models.cart.ModelLever;
import vswe.stevesvehicles.client.rendering.models.cart.ModelWheel;
import vswe.stevesvehicles.module.cart.addon.ModuleBrake;
import vswe.stevesvehicles.module.cart.attachment.ModuleAdvancedControl;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleDataGroup;
import vswe.stevesvehicles.module.data.ModuleSide;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.module.data.registry.ModuleRegistryTravel;
import vswe.stevesvehicles.vehicle.VehicleRegistry;

public class ModuleRegistryCartTravel extends ModuleRegistry {
	public ModuleRegistryCartTravel() {
		super("cart.travel");
		ModuleDataGroup seats = ModuleDataGroup.getGroup(ModuleRegistryTravel.SEAT_KEY);
		ModuleData brake = new ModuleData("brake_handle", ModuleBrake.class, 12) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Lever", new ModelLever(ResourceHelper.getResource("/models/leverModel.png")));
			}
		};
		brake.addShapedRecipe(null, null, "dyeRed", Items.IRON_INGOT, REFINED_HANDLE, null, Items.REDSTONE, Items.IRON_INGOT, null);
		brake.addVehicles(VehicleRegistry.CART);
		brake.addSides(ModuleSide.RIGHT);
		brake.addRequirement(seats);
		register(brake);
		ModuleData controller = new ModuleData("advanced_control_system", ModuleAdvancedControl.class, 38) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Lever", new ModelLever(ResourceHelper.getResource("/models/leverModel2.png")));
				addModel("Wheel", new ModelWheel());
			}
		};
		controller.addShapedRecipe(null, GRAPHICAL_INTERFACE, null, Items.REDSTONE, WHEEL, Items.REDSTONE, Items.IRON_INGOT, Items.IRON_INGOT, SPEED_HANDLE);
		controller.addVehicles(VehicleRegistry.CART);
		controller.addSides(ModuleSide.RIGHT);
		controller.addRequirement(seats);
		register(controller);
	}
}
