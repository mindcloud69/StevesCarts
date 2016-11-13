package vswe.stevesvehicles.upgrade.registry;

import static vswe.stevesvehicles.item.ComponentTypes.ADVANCED_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.BLANK_UPGRADE;
import static vswe.stevesvehicles.item.ComponentTypes.CLEANING_FAN;
import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.SOLAR_PANEL;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import vswe.stevesvehicles.upgrade.Upgrade;
import vswe.stevesvehicles.upgrade.effect.fuel.CombustionFuel;
import vswe.stevesvehicles.upgrade.effect.fuel.FuelCapacity;
import vswe.stevesvehicles.upgrade.effect.fuel.FuelCost;
import vswe.stevesvehicles.upgrade.effect.fuel.Recharger;
import vswe.stevesvehicles.upgrade.effect.fuel.Solar;
import vswe.stevesvehicles.upgrade.effect.fuel.ThermalFuel;

public class UpgradeRegistryPower extends UpgradeRegistry {
	public UpgradeRegistryPower() {
		super("power");


		Upgrade battery = new Upgrade("batteries");
		battery.addEffect(FuelCapacity.class, 5000);
		battery.addEffect(Recharger.class, 40);

		battery.addShapedRecipe(    Items.REDSTONE,     Items.REDSTONE,                     Items.REDSTONE,
				Items.REDSTONE,     new ItemStack(Items.DYE, 1, 4),     Items.REDSTONE,
				Items.REDSTONE,     BLANK_UPGRADE,                      Items.REDSTONE);

		register(battery);



		Upgrade crystal = new Upgrade("power_crystal");
		crystal.addEffect(FuelCapacity.class, 15000);
		crystal.addEffect(Recharger.class, 150);

		crystal.addShapedRecipe(    Items.REDSTONE,         Items.GLOWSTONE_DUST,       Items.REDSTONE,
				Items.GLOWSTONE_DUST,   Blocks.LAPIS_BLOCK,         Items.GLOWSTONE_DUST,
				Items.DIAMOND,          battery,                    Items.DIAMOND);

		register(crystal);



		Upgrade co2 = new Upgrade("co2_friendly");
		co2.addEffect(FuelCost.class, 0.5F);

		co2.addShapedRecipe(    null,           Blocks.PISTON,      null,
				SIMPLE_PCB,     Blocks.IRON_BARS,   SIMPLE_PCB,
				CLEANING_FAN,   BLANK_UPGRADE,      CLEANING_FAN);

		register(co2);



		Upgrade engine = new Upgrade("generic_engine");
		engine.addEffect(CombustionFuel.class);
		engine.addEffect(FuelCost.class, 1.03F);

		engine.addShapedRecipe( null,               SIMPLE_PCB,         null,
				Blocks.PISTON,      Blocks.FURNACE,     Blocks.PISTON,
				Items.IRON_INGOT,   BLANK_UPGRADE,      Items.IRON_INGOT);

		register(engine);



		Upgrade thermal = new Upgrade("thermal_engine_upgrade");
		thermal.addEffect(ThermalFuel.class);
		thermal.addEffect(FuelCost.class, 1.03F);

		thermal.addShapedRecipe(    Blocks.NETHER_BRICK,    ADVANCED_PCB,       Blocks.NETHER_BRICK,
				Blocks.PISTON,          Blocks.FURNACE,     Blocks.PISTON,
				Blocks.OBSIDIAN,        BLANK_UPGRADE,      Blocks.OBSIDIAN);
		register(thermal);



		Upgrade solar = new Upgrade("solar_panel");
		solar.addEffect(Solar.class);

		solar.addShapedRecipe(  SOLAR_PANEL,        SOLAR_PANEL,        SOLAR_PANEL,
				Items.REDSTONE,     Items.DIAMOND,      Items.REDSTONE,
				Items.REDSTONE,     BLANK_UPGRADE,      Items.REDSTONE);

		register(solar);
	}
}
