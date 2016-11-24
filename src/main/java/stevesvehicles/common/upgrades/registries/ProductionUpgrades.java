package stevesvehicles.common.upgrades.registries;

import static stevesvehicles.common.items.ComponentTypes.ADVANCED_PCB;
import static stevesvehicles.common.items.ComponentTypes.BLANK_UPGRADE;
import static stevesvehicles.common.items.ComponentTypes.EYE_OF_GALGADOR;
import static stevesvehicles.common.items.ComponentTypes.REINFORCED_METAL;
import static stevesvehicles.common.items.ComponentTypes.SIMPLE_PCB;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import stevesvehicles.common.upgrades.Upgrade;
import stevesvehicles.common.upgrades.effects.assembly.FreeModules;
import stevesvehicles.common.upgrades.effects.assembly.WorkEfficiency;
import stevesvehicles.common.upgrades.effects.fuel.FuelCost;
import stevesvehicles.common.upgrades.effects.fuel.FuelCostFree;
import stevesvehicles.common.upgrades.effects.time.TimeFlat;
import stevesvehicles.common.upgrades.effects.time.TimeFlatCart;
import stevesvehicles.common.upgrades.effects.time.TimeFlatRemoved;

public enum ProductionUpgrades {
	KNOWLEDGE("module_knowledge"){
		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(Items.BOOK, Blocks.BOOKSHELF, Items.BOOK, Blocks.BOOKSHELF, Blocks.ENCHANTING_TABLE, Blocks.BOOKSHELF, Items.IRON_INGOT, BLANK_UPGRADE, Items.IRON_INGOT);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(TimeFlat.class, -750);
			upgrade.addEffect(TimeFlatCart.class, -5000);
		}
	},
	INDUSTRIAL_ESPIONAGE("industrial_espionage"){

		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(Blocks.BOOKSHELF, Items.IRON_INGOT, Blocks.BOOKSHELF, Items.GLOWSTONE_DUST, REINFORCED_METAL, Items.GLOWSTONE_DUST, REINFORCED_METAL, upgrade, REINFORCED_METAL);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(TimeFlat.class, -2500);
			upgrade.addEffect(TimeFlatCart.class, -14000);
		}
	},
	EXPERIENCED_ASSEMBLER("experienced_assembler"){

		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(SIMPLE_PCB, Items.BOOK, SIMPLE_PCB, Items.IRON_INGOT, ADVANCED_PCB, Items.IRON_INGOT, Items.IRON_INGOT, BLANK_UPGRADE, Items.IRON_INGOT);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(WorkEfficiency.class, 1.0F);
			upgrade.addEffect(FuelCost.class, 2F);
		}
	},
	NEW_ERA("new_era"){
		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(EYE_OF_GALGADOR, Items.BOOK, EYE_OF_GALGADOR, Items.IRON_INGOT, SIMPLE_PCB, Items.IRON_INGOT, EYE_OF_GALGADOR, upgrade, EYE_OF_GALGADOR);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(WorkEfficiency.class, 2.5F);
			upgrade.addEffect(FuelCost.class, 6F);
		}
	},
	QUICK_DEMOLISHER("quick_demolisher"){
		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(Blocks.OBSIDIAN, Items.IRON_INGOT, Blocks.OBSIDIAN, Items.IRON_INGOT, Blocks.IRON_BLOCK, Items.IRON_INGOT, Blocks.OBSIDIAN, BLANK_UPGRADE, Blocks.OBSIDIAN);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(TimeFlatRemoved.class, -8000);
		}
	},
	ENTROPY("entropy"){
		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(Blocks.LAPIS_BLOCK, Items.IRON_INGOT, null, Items.REDSTONE, EYE_OF_GALGADOR, Items.REDSTONE, null, upgrade, Blocks.LAPIS_BLOCK);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(TimeFlatRemoved.class, -32000);
			upgrade.addEffect(TimeFlat.class, 500);
		}
	},
	CREATIVE_MODE("creative_mode"){
		@Override
		protected void registerRecipe() {
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(WorkEfficiency.class, 10000F);
			upgrade.addEffect(FuelCostFree.class);
		}
	},
	CREATIVE_MODE_DELUXE("creative_mode_deluxe"){
		@Override
		protected void registerRecipe() {
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(WorkEfficiency.class, 10000F);
			upgrade.addEffect(FuelCostFree.class);
			upgrade.addEffect(FreeModules.class);
		}
	};

	private static final UpgradeRegistry registry = new UpgradeRegistry("production");
	private final String unlocalizedName;
	private final boolean connectToRedstone;
	protected Upgrade upgrade;

	private ProductionUpgrades(String unlocalizedName) {
		this(unlocalizedName, false);
	}

	private ProductionUpgrades(String unlocalizedName, boolean connectToRedstone) {
		this.unlocalizedName = unlocalizedName;
		this.connectToRedstone = connectToRedstone;
	}

	protected abstract void registerRecipe();

	protected abstract void addEffects();

	public static void registerUpgrades(){
		for(ProductionUpgrades upgradeEnum : values()){
			registry.register(upgradeEnum.upgrade = new Upgrade(upgradeEnum.unlocalizedName, upgradeEnum.connectToRedstone));
			upgradeEnum.addEffects();
		}
		UpgradeRegistry.add(registry);
	}

	public static void registerRecipes(){
		for(ProductionUpgrades upgrade : values()){
			upgrade.registerRecipe();
		}
	}
}
