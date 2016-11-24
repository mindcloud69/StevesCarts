package stevesvehicles.common.modules.datas.registries;

import static stevesvehicles.common.items.ComponentTypes.DYNAMITE;
import static stevesvehicles.common.items.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.localization.entry.info.LocalizationMessage;
import stevesvehicles.client.rendering.models.common.ModelCake;
import stevesvehicles.common.core.StevesVehicles;
import stevesvehicles.common.holiday.HolidayType;
import stevesvehicles.common.modules.common.attachment.ModuleCakeServer;
import stevesvehicles.common.modules.common.attachment.ModuleCakeServerDynamite;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleSide;
import stevesvehicles.common.vehicles.VehicleRegistry;

public class ModuleRegistryCake extends ModuleRegistry {
	public ModuleRegistryCake() {
		super("common.cake");
		ModuleData cake = new ModuleData("cake_server", ModuleCakeServer.class, 10) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Cake", new ModelCake());
			}
		};
		cake.addShapedRecipe(null, Items.CAKE, null, "slabWood", "slabWood", "slabWood", null, SIMPLE_PCB, null);
		cake.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		cake.addSides(ModuleSide.TOP);
		cake.addMessage(LocalizationMessage.YEAR);
		register(cake);
		ModuleData trick = new ModuleData("trick_or_treat_cake_server", ModuleCakeServerDynamite.class, 15) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Cake", new ModelCake());
			}
		};
		trick.addShapedRecipe(null, Items.CAKE, null, "slabWood", "slabWood", "slabWood", DYNAMITE, SIMPLE_PCB, DYNAMITE);
		trick.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		trick.addSides(ModuleSide.TOP);
		register(trick);
		if (!StevesVehicles.holidays.contains(HolidayType.HALLOWEEN)) {
			trick.lock();
		}
	}
}
