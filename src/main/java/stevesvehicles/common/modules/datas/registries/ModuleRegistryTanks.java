package stevesvehicles.common.modules.datas.registries;

import static stevesvehicles.common.items.ComponentTypes.TANK_VALVE;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.localization.entry.info.LocalizationGroup;
import stevesvehicles.client.rendering.models.common.ModelAdvancedTank;
import stevesvehicles.client.rendering.models.common.ModelFrontTank;
import stevesvehicles.client.rendering.models.common.ModelSideTanks;
import stevesvehicles.client.rendering.models.common.ModelTopTank;
import stevesvehicles.common.modules.common.storage.tank.ModuleAdvancedTank;
import stevesvehicles.common.modules.common.storage.tank.ModuleCheatTank;
import stevesvehicles.common.modules.common.storage.tank.ModuleFrontTank;
import stevesvehicles.common.modules.common.storage.tank.ModuleInternalTank;
import stevesvehicles.common.modules.common.storage.tank.ModuleOpenTank;
import stevesvehicles.common.modules.common.storage.tank.ModuleSideTanks;
import stevesvehicles.common.modules.common.storage.tank.ModuleTopTank;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleDataGroup;
import stevesvehicles.common.modules.datas.ModuleSide;
import stevesvehicles.common.vehicles.VehicleRegistry;

public class ModuleRegistryTanks extends ModuleRegistry {
	private static final String GLASS = "blockGlass";
	private static final String PANE = "paneGlass";

	public ModuleRegistryTanks() {
		super("common.tanks");
		ModuleData side = new ModuleData("side_tanks", ModuleSideTanks.class, 10) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("SideTanks", new ModelSideTanks());
			}
		};
		side.addShapedRecipe(GLASS, PANE, GLASS, GLASS, TANK_VALVE, GLASS, GLASS, PANE, GLASS);
		side.addSides(ModuleSide.LEFT, ModuleSide.RIGHT);
		side.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(side);
		ModuleData top = new ModuleData("top_tank", ModuleTopTank.class, 22) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("TopTank", new ModelTopTank(false));
			}
		};
		top.addShapedRecipe(PANE, PANE, PANE, PANE, TANK_VALVE, PANE, GLASS, GLASS, GLASS);
		top.addSides(ModuleSide.TOP);
		top.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(top);
		ModuleData front = new ModuleData("front_tank", ModuleFrontTank.class, 15) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				setModelMultiplier(0.68F);
				addModel("FrontTank", new ModelFrontTank());
			}
		};
		front.addShapedRecipe(null, GLASS, null, PANE, TANK_VALVE, PANE, GLASS, GLASS, GLASS);
		front.addSides(ModuleSide.FRONT);
		front.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(front);
		ModuleData open = new ModuleData("open_tank", ModuleOpenTank.class, 31) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("TopTank", new ModelTopTank(true));
			}
		};
		open.addShapedRecipe(PANE, null, PANE, PANE, TANK_VALVE, PANE, GLASS, GLASS, GLASS);
		open.addSides(ModuleSide.TOP);
		open.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(open);
		ModuleData internal = new ModuleData("internal_tank", ModuleInternalTank.class, 37);
		internal.addShapedRecipe(PANE, PANE, PANE, PANE, TANK_VALVE, PANE, PANE, PANE, PANE);
		internal.setAllowDuplicate(true);
		internal.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(internal);
		ModuleData advanced = new ModuleData("advanced_tank", ModuleAdvancedTank.class, 54) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("LargeTank", new ModelAdvancedTank());
				removeModel("Top");
			}
		};
		advanced.addShapedRecipe(GLASS, GLASS, GLASS, GLASS, TANK_VALVE, GLASS, GLASS, GLASS, GLASS);
		advanced.addSides(ModuleSide.CENTER, ModuleSide.TOP);
		advanced.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(advanced);
		ModuleData cheat = new ModuleData("creative_tank", ModuleCheatTank.class, 1);
		cheat.setAllowDuplicate(true);
		cheat.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(cheat);
	}

	public static final String TANK_KEY = "Tanks";
	private ModuleDataGroup tanks = ModuleDataGroup.createGroup(TANK_KEY, LocalizationGroup.TANK);

	@Override
	public void register(ModuleData moduleData) {
		super.register(moduleData);
		tanks.add(moduleData);
	}
}
