package stevesvehicles.common.modules.datas.registries.boat;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.rendering.models.boat.ModelHull;
import stevesvehicles.client.rendering.models.common.ModelHullTop;
import stevesvehicles.common.modules.common.hull.HullCreative;
import stevesvehicles.common.modules.common.hull.HullGalgadorian;
import stevesvehicles.common.modules.common.hull.HullReinforced;
import stevesvehicles.common.modules.common.hull.HullStandard;
import stevesvehicles.common.modules.common.hull.HullWood;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleDataHull;
import stevesvehicles.common.modules.datas.registries.ModuleRegistry;
import stevesvehicles.common.vehicles.VehicleRegistry;

public class ModuleRegistryBoatHulls extends ModuleRegistry {
	public ModuleRegistryBoatHulls() {
		super("boat.hulls");
		ModuleData wood = new ModuleDataHull("wooden_hull", HullWood.class, 50, 1, 2, 0, 15) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/boat/wooden_hull.png")));
				addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cart/wooden_hull_top.png")));
			}
		};
		wood.addVehicles(VehicleRegistry.BOAT);
		register(wood);
		ModuleData standard = new ModuleDataHull("standard_hull", HullStandard.class, 200, 3, 4, 6, 50) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/boat/standard_hull.png")));
				addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cart/standard_hull_top.png")));
			}
		};
		standard.addVehicles(VehicleRegistry.BOAT);
		register(standard);
		ModuleData reinforced = new ModuleDataHull("reinforced_hull", HullReinforced.class, 500, 5, 6, 12, 150) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/boat/reinforced_hull.png")));
				addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cart/reinforced_hull_top.png")));
			}
		};
		reinforced.addVehicles(VehicleRegistry.BOAT);
		register(reinforced);
		ModuleData galgadorian = new ModuleDataHull("galgadorian_hull", HullGalgadorian.class, 1000, 5, 6, 12, 150) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/boat/galgadorian_hull.png")));
				addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cart/galgadorian_hull_top.png")));
			}
		};
		galgadorian.addVehicles(VehicleRegistry.BOAT);
		register(galgadorian);
		ModuleData creative = new ModuleDataHull("creative_hull", HullCreative.class, 10000, 5, 6, 12, 150) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/boat/creative_hull.png")));
				addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/cart/creative_hull_top.png")));
			}
		};
		creative.addVehicles(VehicleRegistry.BOAT);
		register(creative);
		// TODO add recipes
	}
}
