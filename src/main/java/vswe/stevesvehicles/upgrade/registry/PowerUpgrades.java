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
import vswe.stevesvehicles.upgrade.effect.assembly.Blueprint;
import vswe.stevesvehicles.upgrade.effect.assembly.Disassemble;
import vswe.stevesvehicles.upgrade.effect.assembly.InputChest;
import vswe.stevesvehicles.upgrade.effect.external.Deployer;
import vswe.stevesvehicles.upgrade.effect.external.Manager;
import vswe.stevesvehicles.upgrade.effect.external.Redstone;
import vswe.stevesvehicles.upgrade.effect.external.Transposer;
import vswe.stevesvehicles.upgrade.effect.fuel.CombustionFuel;
import vswe.stevesvehicles.upgrade.effect.fuel.FuelCapacity;
import vswe.stevesvehicles.upgrade.effect.fuel.FuelCost;
import vswe.stevesvehicles.upgrade.effect.fuel.Recharger;
import vswe.stevesvehicles.upgrade.effect.fuel.Solar;
import vswe.stevesvehicles.upgrade.effect.fuel.ThermalFuel;
import vswe.stevesvehicles.upgrade.effect.time.TimeFlatCart;

public enum PowerUpgrades {
	BATTERY("batteries"){
		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(Items.REDSTONE, Items.REDSTONE, Items.REDSTONE, Items.REDSTONE, new ItemStack(Items.DYE, 1, 4), Items.REDSTONE, Items.REDSTONE, BLANK_UPGRADE, Items.REDSTONE);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(FuelCapacity.class, 5000);
			upgrade.addEffect(Recharger.class, 40);
		}
	},
	CRYSTAL("power_crystal"){
		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(Items.REDSTONE, Items.GLOWSTONE_DUST, Items.REDSTONE, Items.GLOWSTONE_DUST, Blocks.LAPIS_BLOCK, Items.GLOWSTONE_DUST, Items.DIAMOND, BATTERY.upgrade, Items.DIAMOND);
		}
			
		@Override
		protected void addEffects() {
			upgrade.addEffect(FuelCapacity.class, 15000);
			upgrade.addEffect(Recharger.class, 150);
		}
	},
	CO2("co2_friendly"){
		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(null, Blocks.PISTON, null, SIMPLE_PCB, Blocks.IRON_BARS, SIMPLE_PCB, CLEANING_FAN, BLANK_UPGRADE, CLEANING_FAN);
		}
			
		@Override
		protected void addEffects() {
			upgrade.addEffect(FuelCost.class, 0.5F);
		}
	},
	GENERIC_ENGINE("generic_engine"){
		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(null, SIMPLE_PCB, null, Blocks.PISTON, Blocks.FURNACE, Blocks.PISTON, Items.IRON_INGOT, BLANK_UPGRADE, Items.IRON_INGOT);
		}
			
		@Override
		protected void addEffects() {
			upgrade.addEffect(CombustionFuel.class);
			upgrade.addEffect(FuelCost.class, 1.03F);
		}
	},
	THERMAL_ENGINE("thermal_engine"){
		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(Blocks.NETHER_BRICK, ADVANCED_PCB, Blocks.NETHER_BRICK, Blocks.PISTON, Blocks.FURNACE, Blocks.PISTON, Blocks.OBSIDIAN, BLANK_UPGRADE, Blocks.OBSIDIAN);
		}
			
		@Override
		protected void addEffects() {
			upgrade.addEffect(ThermalFuel.class);
			upgrade.addEffect(FuelCost.class, 1.03F);
		}
	},
	SOLAR_PANEL("solar_panel"){
		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(SOLAR_PANEL, SOLAR_PANEL, SOLAR_PANEL, Items.REDSTONE, Items.DIAMOND, Items.REDSTONE, Items.REDSTONE, BLANK_UPGRADE, Items.REDSTONE);
		}
			
		@Override
		protected void addEffects() {
			upgrade.addEffect(Solar.class);
		}
	};
	
	private static final UpgradeRegistry registry = new UpgradeRegistry("power");
	private final String unlocalizedName;
	private final boolean connectToRedstone;
	protected Upgrade upgrade;
	
	private PowerUpgrades(String unlocalizedName) {
		this(unlocalizedName, false);
	}
	
	private PowerUpgrades(String unlocalizedName, boolean connectToRedstone) {
		this.unlocalizedName = unlocalizedName;
		this.connectToRedstone = connectToRedstone;
	}
	
	protected abstract void registerRecipe();
	
	protected abstract void addEffects();
	
	public static void registerUpgrades(){
		for(PowerUpgrades upgradeEnum : values()){
			registry.register(upgradeEnum.upgrade = new Upgrade(upgradeEnum.unlocalizedName, upgradeEnum.connectToRedstone));
			upgradeEnum.addEffects();
		}
		UpgradeRegistry.add(registry);
	}
	
	public static void registerRecipes(){
		for(PowerUpgrades upgrade : values()){
			upgrade.registerRecipe();
		}
	}
}
