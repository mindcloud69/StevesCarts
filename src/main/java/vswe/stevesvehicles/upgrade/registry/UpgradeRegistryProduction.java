package vswe.stevesvehicles.upgrade.registry;


import static vswe.stevesvehicles.item.ComponentTypes.ADVANCED_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.BLANK_UPGRADE;
import static vswe.stevesvehicles.item.ComponentTypes.EYE_OF_GALGADOR;
import static vswe.stevesvehicles.item.ComponentTypes.REINFORCED_METAL;
import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import vswe.stevesvehicles.upgrade.Upgrade;
import vswe.stevesvehicles.upgrade.effect.assembly.FreeModules;
import vswe.stevesvehicles.upgrade.effect.assembly.WorkEfficiency;
import vswe.stevesvehicles.upgrade.effect.fuel.FuelCost;
import vswe.stevesvehicles.upgrade.effect.fuel.FuelCostFree;
import vswe.stevesvehicles.upgrade.effect.time.TimeFlat;
import vswe.stevesvehicles.upgrade.effect.time.TimeFlatCart;
import vswe.stevesvehicles.upgrade.effect.time.TimeFlatRemoved;

public class UpgradeRegistryProduction extends UpgradeRegistry {
	public UpgradeRegistryProduction() {
		super("production");

		Upgrade knowledge = new Upgrade("module_knowledge");
		knowledge.addEffect(TimeFlat.class, -750);
		knowledge.addEffect(TimeFlatCart.class, -5000);

		knowledge.addShapedRecipe(  Items.BOOK,         Blocks.BOOKSHELF,           Items.BOOK,
				Blocks.BOOKSHELF,   Blocks.ENCHANTING_TABLE,    Blocks.BOOKSHELF,
				Items.IRON_INGOT,   BLANK_UPGRADE,              Items.IRON_INGOT);

		register(knowledge);



		Upgrade espionage = new Upgrade("industrial_espionage");
		espionage.addEffect(TimeFlat.class, -2500);
		espionage.addEffect(TimeFlatCart.class, -14000);

		espionage.addShapedRecipe(  Blocks.BOOKSHELF,       Items.IRON_INGOT,   Blocks.BOOKSHELF,
				Items.GLOWSTONE_DUST,   REINFORCED_METAL,   Items.GLOWSTONE_DUST,
				REINFORCED_METAL,       knowledge,          REINFORCED_METAL);

		register(espionage);



		Upgrade experienced = new Upgrade("experienced_assembler");
		experienced.addEffect(WorkEfficiency.class, 1.0F);
		experienced.addEffect(FuelCost.class, 2F);

		experienced.addShapedRecipe(    SIMPLE_PCB,             Items.BOOK,         SIMPLE_PCB,
				Items.IRON_INGOT,       ADVANCED_PCB,       Items.IRON_INGOT,
				Items.IRON_INGOT,       BLANK_UPGRADE,      Items.IRON_INGOT);

		register(experienced);



		Upgrade era = new Upgrade("new_era");
		era.addEffect(WorkEfficiency.class, 2.5F);
		era.addEffect(FuelCost.class, 6F);

		era.addShapedRecipe(    EYE_OF_GALGADOR,    Items.BOOK,     EYE_OF_GALGADOR,
				Items.IRON_INGOT,    SIMPLE_PCB,    Items.IRON_INGOT,
				EYE_OF_GALGADOR,    experienced,    EYE_OF_GALGADOR);

		register(era);



		Upgrade demolisher = new Upgrade("quick_demolisher");
		demolisher.addEffect(TimeFlatRemoved.class, -8000);

		demolisher.addShapedRecipe(     Blocks.OBSIDIAN,    Items.IRON_INGOT,       Blocks.OBSIDIAN,
				Items.IRON_INGOT,   Blocks.IRON_BLOCK,      Items.IRON_INGOT,
				Blocks.OBSIDIAN,    BLANK_UPGRADE,          Blocks.OBSIDIAN);

		register(demolisher);



		Upgrade entropy = new Upgrade("entropy");
		entropy.addEffect(TimeFlatRemoved.class, -32000);
		entropy.addEffect(TimeFlat.class, 500);

		entropy.addShapedRecipe(Blocks.LAPIS_BLOCK,     Items.IRON_INGOT,       null,
				Items.REDSTONE,         EYE_OF_GALGADOR,        Items.REDSTONE,
				null,                   demolisher,             Blocks.LAPIS_BLOCK);

		register(entropy);



		Upgrade cheat = new Upgrade("creative_mode");
		cheat.addEffect(WorkEfficiency.class, 10000F);
		cheat.addEffect(FuelCostFree.class);

		register(cheat);


		Upgrade cheatDeluxe = new Upgrade("creative_mode_deluxe");
		cheatDeluxe.addEffect(WorkEfficiency.class, 10000F);
		cheatDeluxe.addEffect(FuelCostFree.class);
		cheatDeluxe.addEffect(FreeModules.class);

		register(cheatDeluxe);
	}
}
