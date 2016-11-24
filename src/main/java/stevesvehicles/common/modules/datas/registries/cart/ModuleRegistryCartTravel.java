package stevesvehicles.common.modules.datas.registries.cart;

import static stevesvehicles.common.items.ComponentTypes.GRAPHICAL_INTERFACE;
import static stevesvehicles.common.items.ComponentTypes.REFINED_HANDLE;
import static stevesvehicles.common.items.ComponentTypes.SPEED_HANDLE;
import static stevesvehicles.common.items.ComponentTypes.WHEEL;

import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.rendering.models.cart.ModelLever;
import stevesvehicles.client.rendering.models.cart.ModelWheel;
import stevesvehicles.common.modules.cart.addon.ModuleBrake;
import stevesvehicles.common.modules.cart.attachment.ModuleAdvancedControl;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleDataGroup;
import stevesvehicles.common.modules.datas.ModuleSide;
import stevesvehicles.common.modules.datas.registries.ModuleRegistry;
import stevesvehicles.common.modules.datas.registries.ModuleRegistryTravel;
import stevesvehicles.common.vehicles.VehicleRegistry;

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
