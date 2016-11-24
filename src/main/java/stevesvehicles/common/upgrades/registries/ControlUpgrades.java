package stevesvehicles.common.upgrades.registries;

import static stevesvehicles.common.items.ComponentTypes.BLANK_UPGRADE;
import static stevesvehicles.common.items.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import stevesvehicles.common.upgrades.Upgrade;
import stevesvehicles.common.upgrades.effects.assembly.Blueprint;
import stevesvehicles.common.upgrades.effects.assembly.Disassemble;
import stevesvehicles.common.upgrades.effects.assembly.InputChest;
import stevesvehicles.common.upgrades.effects.external.Deployer;
import stevesvehicles.common.upgrades.effects.external.Manager;
import stevesvehicles.common.upgrades.effects.external.Redstone;
import stevesvehicles.common.upgrades.effects.external.Transposer;
import stevesvehicles.common.upgrades.effects.time.TimeFlatCart;

public enum ControlUpgrades {
	MODULE_INPUT("module_input"){

		/*
		 * @SideOnly(Side.CLIENT) private IIcon side;
		 * @SideOnly(Side.CLIENT)
		 * @Override protected void createIcon(IIconRegister register) {
		 * super.createIcon(register); side =
		 * register.registerIcon(StevesVehicles.instance.textureHeader +
		 * ":upgrades/sides/chest"); }
		 * @SideOnly(Side.CLIENT)
		 * @Override public IIcon getSideTexture() { return side; }
		 */

		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(null, SIMPLE_PCB, null, Items.REDSTONE, Blocks.CHEST, Items.REDSTONE, Items.IRON_INGOT, BLANK_UPGRADE, Items.IRON_INGOT);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(InputChest.class, 7, 3);
		}

	},
	PRODUCTION_LINE("production_line"){

		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(null, SIMPLE_PCB, null, Items.REDSTONE, Blocks.PISTON, Items.REDSTONE, Items.IRON_INGOT, BLANK_UPGRADE, Items.IRON_INGOT);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(Blueprint.class);
		}

	},
	CART_DEPLOYER("cart_deployer"){

		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(Items.IRON_INGOT, Blocks.RAIL, Items.IRON_INGOT, Items.REDSTONE, Blocks.PISTON, Items.REDSTONE, Items.IRON_INGOT, BLANK_UPGRADE, Items.IRON_INGOT);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(Deployer.class);
		}

	},
	CART_MODIFIER("cart_modifier"){

		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(Items.IRON_INGOT, null, Items.IRON_INGOT, Items.REDSTONE, Blocks.ANVIL, Items.REDSTONE, Items.IRON_INGOT, BLANK_UPGRADE, Items.IRON_INGOT);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(Disassemble.class);
		}

	},
	CART_CRANE("cart_crane"){

		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(Items.IRON_INGOT, Blocks.PISTON, Items.IRON_INGOT, Items.REDSTONE, Blocks.RAIL, Items.REDSTONE, Items.IRON_INGOT, BLANK_UPGRADE, Items.IRON_INGOT);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(Transposer.class);
		}

	},
	REDSTONE_CONTROL("redstone_control", true){

		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(Blocks.REDSTONE_TORCH, null, Blocks.REDSTONE_TORCH, Items.REDSTONE, SIMPLE_PCB, Items.REDSTONE, Items.IRON_INGOT, BLANK_UPGRADE, Items.IRON_INGOT);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(Redstone.class);
		}

	},
	MANAGER_CONTROL("manager_bridge"){

		@Override
		protected void registerRecipe() {
			upgrade.addShapedRecipe(Items.IRON_INGOT, Blocks.PISTON, Items.IRON_INGOT, Items.REDSTONE, Blocks.RAIL, Items.REDSTONE, Items.IRON_INGOT, BLANK_UPGRADE, Items.IRON_INGOT);
		}

		@Override
		protected void addEffects() {
			upgrade.addEffect(Manager.class);
			upgrade.addEffect(TimeFlatCart.class, 100);
		}

	};

	private static final UpgradeRegistry registry = new UpgradeRegistry("control");
	private final String unlocalizedName;
	private final boolean connectToRedstone;
	protected Upgrade upgrade;

	private ControlUpgrades(String unlocalizedName) {
		this(unlocalizedName, false);
	}

	private ControlUpgrades(String unlocalizedName, boolean connectToRedstone) {
		this.unlocalizedName = unlocalizedName;
		this.connectToRedstone = connectToRedstone;
	}

	protected abstract void registerRecipe();

	protected abstract void addEffects();

	public static void registerUpgrades(){
		for(ControlUpgrades upgradeEnum : values()){
			registry.register(upgradeEnum.upgrade = new Upgrade(upgradeEnum.unlocalizedName, upgradeEnum.connectToRedstone));
			upgradeEnum.addEffects();
		}
		UpgradeRegistry.add(registry);
	}

	public static void registerRecipes(){
		for(ControlUpgrades upgrade : values()){
			upgrade.registerRecipe();
		}
	}
}
