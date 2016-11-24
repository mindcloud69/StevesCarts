package stevesvehicles.common.upgrades;

import stevesvehicles.client.localization.entry.info.LocalizationUpgrade;
import stevesvehicles.common.upgrades.effects.assembly.Blueprint;
import stevesvehicles.common.upgrades.effects.assembly.Disassemble;
import stevesvehicles.common.upgrades.effects.assembly.FreeModules;
import stevesvehicles.common.upgrades.effects.assembly.InputChest;
import stevesvehicles.common.upgrades.effects.assembly.WorkEfficiency;
import stevesvehicles.common.upgrades.effects.external.Deployer;
import stevesvehicles.common.upgrades.effects.external.Manager;
import stevesvehicles.common.upgrades.effects.external.Redstone;
import stevesvehicles.common.upgrades.effects.external.Transposer;
import stevesvehicles.common.upgrades.effects.fuel.CombustionFuel;
import stevesvehicles.common.upgrades.effects.fuel.FuelCapacity;
import stevesvehicles.common.upgrades.effects.fuel.FuelCost;
import stevesvehicles.common.upgrades.effects.fuel.FuelCostFree;
import stevesvehicles.common.upgrades.effects.fuel.Recharger;
import stevesvehicles.common.upgrades.effects.fuel.Solar;
import stevesvehicles.common.upgrades.effects.fuel.ThermalFuel;
import stevesvehicles.common.upgrades.effects.time.TimeFlat;
import stevesvehicles.common.upgrades.effects.time.TimeFlatCart;
import stevesvehicles.common.upgrades.effects.time.TimeFlatRemoved;

public final class EffectNameLoader {
	public static void initNames() {
		Upgrade.registerInfo(Blueprint.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				return LocalizationUpgrade.BLUEPRINT.translate();
			}
		});
		Upgrade.registerInfo(Disassemble.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				return LocalizationUpgrade.DISASSEMBLE.translate();
			}
		});
		Upgrade.registerInfo(InputChest.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				return LocalizationUpgrade.INPUT_CHEST.translate(String.valueOf((Integer) params[0] * (Integer) params[1]));
			}
		});
		Upgrade.registerInfo(WorkEfficiency.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				int percentage = (int) ((Float) params[0] * 100);
				return LocalizationUpgrade.EFFICIENCY.translate((percentage >= 0 ? "+" : "") + percentage);
			}
		});
		Upgrade.registerInfo(Deployer.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				return LocalizationUpgrade.DEPLOYER.translate();
			}
		});
		Upgrade.registerInfo(Manager.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				return LocalizationUpgrade.MANAGER.translate();
			}
		});
		Upgrade.registerInfo(Redstone.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				return LocalizationUpgrade.REDSTONE.translate();
			}
		});
		Upgrade.registerInfo(Transposer.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				return LocalizationUpgrade.TRANSPOSER.translate();
			}
		});
		Upgrade.registerInfo(CombustionFuel.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				return LocalizationUpgrade.COMBUSTION.translate();
			}
		});
		Upgrade.registerInfo(FuelCapacity.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				int capacity = (Integer) params[0];
				return LocalizationUpgrade.FUEL_CAPACITY.translate((capacity >= 0 ? "+" : "") + capacity);
			}
		});
		Upgrade.registerInfo(FuelCost.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				float p = (((Float) params[0]) - 1) * 100;
				int percentage = Math.round(p * 10) / 10;
				return LocalizationUpgrade.FUEL_COST.translate((percentage >= 0 ? "+" : "") + percentage);
			}
		});
		Upgrade.registerInfo(FuelCostFree.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				return LocalizationUpgrade.FUEL_COST_FREE.translate();
			}
		});
		Upgrade.registerInfo(Recharger.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				int amount = (Integer) params[0];
				return LocalizationUpgrade.GENERATOR.translate(String.valueOf(amount), String.valueOf(amount));
			}
		});
		Upgrade.registerInfo(Solar.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				return LocalizationUpgrade.SOLAR.translate();
			}
		});
		Upgrade.registerInfo(ThermalFuel.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				return LocalizationUpgrade.THERMAL.translate();
			}
		});
		Upgrade.registerInfo(TimeFlat.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				int seconds = (Integer) params[0] / 20;
				return LocalizationUpgrade.TIME_FLAT.translate((seconds >= 0 ? "+" : "") + seconds, String.valueOf(seconds));
			}
		});
		Upgrade.registerInfo(TimeFlatCart.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				int seconds = (Integer) params[0] / 20;
				return LocalizationUpgrade.TIME_FLAT_CART.translate((seconds >= 0 ? "+" : "") + seconds, String.valueOf(seconds));
			}
		});
		Upgrade.registerInfo(TimeFlatRemoved.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				int seconds = (Integer) params[0] / 20;
				return LocalizationUpgrade.TIME_FLAT_REMOVE.translate((seconds >= 0 ? "+" : "") + seconds, String.valueOf(seconds));
			}
		});
		Upgrade.registerInfo(FreeModules.class, new IEffectInfo() {
			@Override
			public String getName(Object... params) {
				return LocalizationUpgrade.FREE_MODULES.translate();
			}
		});
	}

	private EffectNameLoader() {
	}
}
