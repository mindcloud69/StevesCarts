package vswe.stevesvehicles.upgrade.registry;


import static vswe.stevesvehicles.item.ComponentTypes.BLANK_UPGRADE;
import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.StevesVehicles;
import vswe.stevesvehicles.upgrade.Upgrade;
import vswe.stevesvehicles.upgrade.effect.assembly.Blueprint;
import vswe.stevesvehicles.upgrade.effect.assembly.Disassemble;
import vswe.stevesvehicles.upgrade.effect.assembly.InputChest;
import vswe.stevesvehicles.upgrade.effect.external.Deployer;
import vswe.stevesvehicles.upgrade.effect.external.Manager;
import vswe.stevesvehicles.upgrade.effect.external.Redstone;
import vswe.stevesvehicles.upgrade.effect.external.Transposer;
import vswe.stevesvehicles.upgrade.effect.time.TimeFlatCart;

public class UpgradeRegistryControl extends UpgradeRegistry {
	public UpgradeRegistryControl() {
		super("control");

		Upgrade input = new Upgrade("module_input") {
			/*@SideOnly(Side.CLIENT)
			private IIcon side;

			@SideOnly(Side.CLIENT)
			@Override
			protected void createIcon(IIconRegister register) {
				super.createIcon(register);
				side = register.registerIcon(StevesVehicles.instance.textureHeader + ":upgrades/sides/chest");
			}

			@SideOnly(Side.CLIENT)
			@Override
			public IIcon getSideTexture() {
				return side;
			}*/
		};
		input.addEffect(InputChest.class, 7, 3);

		input.addShapedRecipe(  null,               SIMPLE_PCB,     null,
				Items.REDSTONE,     Blocks.CHEST,   Items.REDSTONE,
				Items.IRON_INGOT,   BLANK_UPGRADE,  Items.IRON_INGOT);

		register(input);



		Upgrade production = new Upgrade("production_line");
		production.addEffect(Blueprint.class);

		production.addShapedRecipe( null,               SIMPLE_PCB,         null,
				Items.REDSTONE,     Blocks.PISTON,      Items.REDSTONE,
				Items.IRON_INGOT,   BLANK_UPGRADE,      Items.IRON_INGOT);

		register(production);



		Upgrade deployer = new Upgrade("cart_deployer");
		deployer.addEffect(Deployer.class);

		deployer.addShapedRecipe(   Items.IRON_INGOT,   Blocks.RAIL,    Items.IRON_INGOT,
				Items.REDSTONE,     Blocks.PISTON,  Items.REDSTONE,
				Items.IRON_INGOT,   BLANK_UPGRADE,  Items.IRON_INGOT);

		register(deployer);



		Upgrade modifier = new Upgrade("cart_modifier");
		modifier.addEffect(Disassemble.class);

		modifier.addShapedRecipe(   Items.IRON_INGOT,   null,           Items.IRON_INGOT,
				Items.REDSTONE,     Blocks.ANVIL,   Items.REDSTONE,
				Items.IRON_INGOT,     BLANK_UPGRADE,  Items.IRON_INGOT);

		register(modifier);



		Upgrade crane = new Upgrade("cart_crane");
		crane.addEffect(Transposer.class);

		crane.addShapedRecipe(  Items.IRON_INGOT,   Blocks.PISTON,      Items.IRON_INGOT,
				Items.REDSTONE,     Blocks.RAIL,        Items.REDSTONE,
				Items.IRON_INGOT,   BLANK_UPGRADE,      Items.IRON_INGOT);

		register(crane);



		Upgrade redstone = new Upgrade("redstone_control") {
			@Override
			public boolean connectToRedstone() {
				return true;
			}
		};
		redstone.addEffect(Redstone.class);

		redstone.addShapedRecipe(   Blocks.REDSTONE_TORCH,  null,           Blocks.REDSTONE_TORCH,
				Items.REDSTONE,         SIMPLE_PCB,     Items.REDSTONE,
				Items.IRON_INGOT,       BLANK_UPGRADE,  Items.IRON_INGOT);

		register(redstone);



		Upgrade manager = new Upgrade("manager_bridge");
		manager.addEffect(Manager.class);
		manager.addEffect(TimeFlatCart.class, 100);

		manager.addShapedRecipe(    Items.IRON_INGOT,   Blocks.PISTON,      Items.IRON_INGOT,
				Items.REDSTONE,     Blocks.RAIL,        Items.REDSTONE,
				Items.IRON_INGOT,   BLANK_UPGRADE,      Items.IRON_INGOT);

		register(manager);
	}
}
