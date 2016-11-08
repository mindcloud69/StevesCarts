package vswe.stevescarts.Upgrades;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import vswe.stevescarts.Blocks.ModBlocks;
import vswe.stevescarts.Helpers.ComponentTypes;
import vswe.stevescarts.Helpers.RecipeHelper;
import vswe.stevescarts.Items.ModItems;
import vswe.stevescarts.TileEntities.TileEntityUpgrade;

public class AssemblerUpgrade {
	private static HashMap<Byte, AssemblerUpgrade> upgrades;
	//	private static HashMap<Byte, IIcon> sides;
	private byte id;
	private int sideTexture;
	private String name;
	private ArrayList<BaseEffect> effects;
	//	private IIcon icon;

	public static HashMap<Byte, AssemblerUpgrade> getUpgrades() {
		return AssemblerUpgrade.upgrades;
	}

	public static Collection<AssemblerUpgrade> getUpgradesList() {
		return AssemblerUpgrade.upgrades.values();
	}

	public static AssemblerUpgrade getUpgrade(final int id) {
		return AssemblerUpgrade.upgrades.get((byte) id);
	}

	public static void init() {
		final AssemblerUpgrade batteries = new AssemblerUpgrade(0, "Batteries").addEffect(new FuelCapacity(5000)).addEffect(new Recharger(40)).addRecipe(new Object[][] {
			{ Items.REDSTONE, Items.REDSTONE, Items.REDSTONE }, { Items.REDSTONE, Items.DIAMOND, Items.REDSTONE }, { Items.REDSTONE, ComponentTypes.BLANK_UPGRADE.getItemStack(), Items.REDSTONE } });
		new AssemblerUpgrade(1, "Power Crystal").addEffect(new FuelCapacity(15000)).addEffect(new Recharger(150)).addRecipe(new Object[][] { { Items.DIAMOND, Items.GLOWSTONE_DUST, Items.DIAMOND },
			{ Items.GLOWSTONE_DUST, Blocks.EMERALD_BLOCK, Items.GLOWSTONE_DUST }, { Items.DIAMOND, batteries.getItemStack(), Items.DIAMOND } });
		final AssemblerUpgrade knowledge = new AssemblerUpgrade(2, "Module knowledge").addEffect(new TimeFlat(-750)).addEffect(new TimeFlatCart(-5000)).addEffect(new WorkEfficiency(-0.01f)).addRecipe(new Object[][] {
			{ Items.BOOK, Blocks.BOOKSHELF, Items.BOOK }, { Blocks.BOOKSHELF, Blocks.ENCHANTING_TABLE, Blocks.BOOKSHELF },
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.BLANK_UPGRADE.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() } });
		new AssemblerUpgrade(3, "Industrial espionage").addEffect(new TimeFlat(-2500)).addEffect(new TimeFlatCart(-14000)).addEffect(new WorkEfficiency(-0.01f)).addRecipe(new Object[][] {
			{ Blocks.BOOKSHELF, ComponentTypes.REINFORCED_METAL.getItemStack(), Blocks.BOOKSHELF },
			{ ComponentTypes.EYE_OF_GALGADOR.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.EYE_OF_GALGADOR.getItemStack() },
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), knowledge.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() } });
		final ItemStack[] books = new ItemStack[5];
		for (int i = 0; i < 5; ++i) {
			books[i] = Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(Enchantments.EFFICIENCY, i + 1));
		}
		final AssemblerUpgrade experienced = new AssemblerUpgrade(4, "Experienced assembler").addEffect(new WorkEfficiency(0.1f)).addEffect(new FuelCost(0.3f)).addRecipe(new Object[][] {
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), books[0], ComponentTypes.SIMPLE_PCB.getItemStack() },
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.ADVANCED_PCB.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() },
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.BLANK_UPGRADE.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() } }).addRecipe(new Object[][] {
				{ Items.REDSTONE, books[1], Items.REDSTONE },
				{ ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.ADVANCED_PCB.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() },
				{ ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.BLANK_UPGRADE.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() } }).addRecipe(new Object[][] {
					{ Items.REDSTONE, books[2], Items.REDSTONE }, { Items.IRON_INGOT, ComponentTypes.ADVANCED_PCB.getItemStack(), Items.IRON_INGOT },
					{ ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.BLANK_UPGRADE.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() } }).addRecipe(new Object[][] {
						{ null, books[3], null }, { Items.IRON_INGOT, ComponentTypes.SIMPLE_PCB.getItemStack(), Items.IRON_INGOT },
						{ ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.BLANK_UPGRADE.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() } }).addRecipe(new Object[][] {
							{ null, books[4], null }, { null, Items.REDSTONE, null }, { Items.IRON_INGOT, ComponentTypes.BLANK_UPGRADE.getItemStack(), Items.IRON_INGOT } });
		new AssemblerUpgrade(5, "New Era").addEffect(new WorkEfficiency(1.0f)).addEffect(new FuelCost(30.0f)).addRecipe(new Object[][] {
			{ ComponentTypes.GALGADORIAN_METAL.getItemStack(), books[4], ComponentTypes.GALGADORIAN_METAL.getItemStack() },
			{ ComponentTypes.GALGADORIAN_METAL.getItemStack(), ComponentTypes.ADVANCED_PCB, ComponentTypes.GALGADORIAN_METAL.getItemStack() },
			{ ComponentTypes.GALGADORIAN_METAL.getItemStack(), experienced.getItemStack(), ComponentTypes.GALGADORIAN_METAL.getItemStack() } });
		new AssemblerUpgrade(6, "CO2 friendly").addEffect(new FuelCost(-0.15f)).addRecipe(new Object[][] { { null, Blocks.PISTON, null },
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), Blocks.OAK_FENCE, ComponentTypes.SIMPLE_PCB.getItemStack() },
			{ ComponentTypes.CLEANING_FAN, ComponentTypes.BLANK_UPGRADE.getItemStack(), ComponentTypes.CLEANING_FAN } });
		new AssemblerUpgrade(7, "Generic engine").addEffect(new CombustionFuel()).addEffect(new FuelCost(0.05f)).addRecipe(new Object[][] { { null, ComponentTypes.SIMPLE_PCB.getItemStack(), null },
			{ Blocks.PISTON, Blocks.FURNACE, Blocks.PISTON }, { Items.IRON_INGOT, ComponentTypes.BLANK_UPGRADE.getItemStack(), Items.IRON_INGOT } });
		new AssemblerUpgrade(8, "Module input", 1).addEffect(new InputChest(7, 3)).addRecipe(new Object[][] { { null, ComponentTypes.ADVANCED_PCB.getItemStack(), null },
			{ Blocks.PISTON, Blocks.CHEST, Blocks.PISTON }, { Items.IRON_INGOT, ComponentTypes.BLANK_UPGRADE.getItemStack(), Items.IRON_INGOT } });
		new AssemblerUpgrade(9, "Production line").addEffect(new Blueprint()).addRecipe(new Object[][] { { null, ComponentTypes.SIMPLE_PCB.getItemStack(), null },
			{ ComponentTypes.ADVANCED_PCB.getItemStack(), Items.REDSTONE, ComponentTypes.ADVANCED_PCB.getItemStack() },
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), ComponentTypes.BLANK_UPGRADE.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack() } });
		new AssemblerUpgrade(10, "Cart Deployer").addEffect(new Deployer()).addRecipe(new Object[][] { { Items.IRON_INGOT, Blocks.RAIL, Items.IRON_INGOT },
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), Blocks.PISTON, ComponentTypes.SIMPLE_PCB.getItemStack() },
			{ Items.IRON_INGOT, ComponentTypes.BLANK_UPGRADE.getItemStack(), Items.IRON_INGOT } });
		new AssemblerUpgrade(11, "Cart Modifier").addEffect(new Disassemble()).addRecipe(new Object[][] { { Items.IRON_INGOT, null, Items.IRON_INGOT },
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), Blocks.ANVIL, ComponentTypes.SIMPLE_PCB.getItemStack() },
			{ Items.IRON_INGOT, ComponentTypes.BLANK_UPGRADE.getItemStack(), Items.IRON_INGOT } });
		new AssemblerUpgrade(12, "Cart Crane").addEffect(new Transposer()).addRecipe(new Object[][] { { Blocks.PISTON, Blocks.RAIL, Blocks.PISTON },
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), Items.IRON_INGOT, ComponentTypes.SIMPLE_PCB.getItemStack() }, { null, ComponentTypes.BLANK_UPGRADE.getItemStack(), null } });
		new AssemblerUpgrade(13, "Redstone Control").addEffect(new Redstone()).addRecipe(new Object[][] {
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), Items.REPEATER, ComponentTypes.SIMPLE_PCB.getItemStack() },
			{ Blocks.REDSTONE_TORCH, ComponentTypes.ADVANCED_PCB.getItemStack(), Blocks.REDSTONE_TORCH }, { Items.REDSTONE, ComponentTypes.BLANK_UPGRADE.getItemStack(), Items.REDSTONE } });
		new AssemblerUpgrade(14, "Creative Mode").addEffect(new WorkEfficiency(10000.0f)).addEffect(new FuelCost(-1.0f));
		final AssemblerUpgrade demolisher = new AssemblerUpgrade(15, "Quick Demolisher").addEffect(new TimeFlatRemoved(-8000)).addRecipe(new Object[][] {
			{ Blocks.OBSIDIAN, ComponentTypes.REINFORCED_METAL.getItemStack(), Blocks.OBSIDIAN },
			{ ComponentTypes.REINFORCED_METAL.getItemStack(), Blocks.IRON_BLOCK, ComponentTypes.REINFORCED_METAL.getItemStack() },
			{ Blocks.OBSIDIAN, ComponentTypes.BLANK_UPGRADE.getItemStack(), Blocks.OBSIDIAN } });
		new AssemblerUpgrade(16, "Entropy").addEffect(new TimeFlatRemoved(-32000)).addEffect(new TimeFlat(3000)).addRecipe(new Object[][] {
			{ ComponentTypes.EYE_OF_GALGADOR.getItemStack(), ComponentTypes.REINFORCED_METAL.getItemStack(), null }, { Items.DIAMOND, Blocks.LAPIS_BLOCK, Items.DIAMOND },
			{ null, demolisher.getItemStack(), ComponentTypes.EYE_OF_GALGADOR.getItemStack() } });
		new AssemblerUpgrade(17, "Manager Bridge").addEffect(new Manager()).addEffect(new TimeFlatCart(200)).addRecipe(new Object[][] { { Items.IRON_INGOT, Items.ENDER_PEARL, Items.IRON_INGOT },
			{ ComponentTypes.SIMPLE_PCB.getItemStack(), ModBlocks.EXTERNAL_DISTRIBUTOR.getBlock(), ComponentTypes.SIMPLE_PCB.getItemStack() },
			{ Items.IRON_INGOT, ComponentTypes.BLANK_UPGRADE.getItemStack(), Items.IRON_INGOT } });
		new AssemblerUpgrade(18, "Thermal Engine Upgrade").addEffect(new ThermalFuel()).addEffect(new FuelCost(0.05f)).addRecipe(new Object[][] {
			{ Blocks.NETHER_BRICK, ComponentTypes.ADVANCED_PCB.getItemStack(), Blocks.NETHER_BRICK }, { Blocks.PISTON, Blocks.FURNACE, Blocks.PISTON },
			{ Blocks.OBSIDIAN, ComponentTypes.BLANK_UPGRADE.getItemStack(), Blocks.OBSIDIAN } });
		new AssemblerUpgrade(19, "Solar Panel").addEffect(new Solar()).addRecipe(new Object[][] {
			{ ComponentTypes.SOLAR_PANEL.getItemStack(), ComponentTypes.SOLAR_PANEL.getItemStack(), ComponentTypes.SOLAR_PANEL.getItemStack() }, { Items.DIAMOND, Items.REDSTONE, Items.DIAMOND },
			{ Items.REDSTONE, ComponentTypes.BLANK_UPGRADE.getItemStack(), Items.REDSTONE } });
	}

	//	public static IIcon getStandardIcon() {
	//		return AssemblerUpgrade.sides.get((byte) 0);
	//	}
	//
	//	@SideOnly(Side.CLIENT)
	//	public static void initSides(final IIconRegister register) {
	//		final ArrayList<Integer> used = new ArrayList<Integer>();
	//		for (final AssemblerUpgrade upgrade : getUpgradesList()) {
	//			if (!used.contains(upgrade.sideTexture)) {
	//				final HashMap<Byte, IIcon> sides = AssemblerUpgrade.sides;
	//				final Byte value = (byte) upgrade.sideTexture;
	//				final StringBuilder sb = new StringBuilder();
	//				StevesCarts.instance.getClass();
	//				sides.put(value, register.registerIcon(sb.append("stevescarts").append(":upgrade_side_").append(upgrade.sideTexture).append("_icon").toString()));
	//				used.add(upgrade.sideTexture);
	//			}
	//		}
	//	}

	public AssemblerUpgrade(final int id, final String name) {
		this(id, name, 0);
	}

	public AssemblerUpgrade(final int id, final String name, final int sideTexture) {
		this.id = (byte) id;
		this.sideTexture = sideTexture;
		this.name = name;
		this.effects = new ArrayList<BaseEffect>();
		AssemblerUpgrade.upgrades.put(this.id, this);
	}

	public byte getId() {
		return this.id;
	}

	public String getName() {
		return I18n.translateToLocal("item.SC2:" + this.getRawName() + ".name");
	}

	public AssemblerUpgrade addEffect(final BaseEffect effect) {
		this.effects.add(effect);
		return this;
	}

	public AssemblerUpgrade addRecipe(final int resultCount, final Object[][] recipe) {
		RecipeHelper.addRecipe(this.getItemStack(resultCount), recipe);
		return this;
	}

	public AssemblerUpgrade addRecipe(final Object[][] recipe) {
		return this.addRecipe(1, recipe);
	}

	protected ItemStack getItemStack() {
		return this.getItemStack(1);
	}

	protected ItemStack getItemStack(final int count) {
		return new ItemStack(ModItems.upgrades, count, this.id);
	}

	public ArrayList<BaseEffect> getEffects() {
		return this.effects;
	}

	public boolean useStandardInterface() {
		return this.getInterfaceEffect() == null;
	}

	public int getInventorySize() {
		final InventoryEffect inv = this.getInventoryEffect();
		if (inv != null) {
			return inv.getInventorySize();
		}
		return 0;
	}

	public InterfaceEffect getInterfaceEffect() {
		for (final BaseEffect effect : this.effects) {
			if (effect instanceof InterfaceEffect) {
				return (InterfaceEffect) effect;
			}
		}
		return null;
	}

	public InventoryEffect getInventoryEffect() {
		for (final BaseEffect effect : this.effects) {
			if (effect instanceof InventoryEffect) {
				return (InventoryEffect) effect;
			}
		}
		return null;
	}

	public TankEffect getTankEffect() {
		for (final BaseEffect effect : this.effects) {
			if (effect instanceof TankEffect) {
				return (TankEffect) effect;
			}
		}
		return null;
	}

	public void init(final TileEntityUpgrade upgrade) {
		for (final BaseEffect effect : this.effects) {
			effect.init(upgrade);
		}
	}

	public void load(final TileEntityUpgrade upgrade, final NBTTagCompound compound) {
		for (final BaseEffect effect : this.effects) {
			effect.load(upgrade, compound);
		}
	}

	public void save(final TileEntityUpgrade upgrade, final NBTTagCompound compound) {
		for (final BaseEffect effect : this.effects) {
			effect.save(upgrade, compound);
		}
	}

	public void update(final TileEntityUpgrade upgrade) {
		for (final BaseEffect effect : this.effects) {
			effect.update(upgrade);
		}
	}

	public void removed(final TileEntityUpgrade upgrade) {
		for (final BaseEffect effect : this.effects) {
			effect.removed(upgrade);
		}
	}

	public String getRawName() {
		return this.name.replace(":", "").replace(" ", "_").toLowerCase();
	}

	//	@SideOnly(Side.CLIENT)
	//	public void createIcon(final IIconRegister register) {
	//		final StringBuilder sb = new StringBuilder();
	//		StevesCarts.instance.getClass();
	//		this.icon = register.registerIcon(sb.append("stevescarts").append(":").append(this.getRawName().replace("_upgrade", "")).append("_icon").toString());
	//	}
	//
	//	@SideOnly(Side.CLIENT)
	//	public IIcon getIcon() {
	//		return this.icon;
	//	}
	//
	//	@SideOnly(Side.CLIENT)
	//	public IIcon getMainTexture() {
	//		return this.icon;
	//	}
	//
	//	@SideOnly(Side.CLIENT)
	//	public IIcon getSideTexture() {
	//		return AssemblerUpgrade.sides.get((byte) this.sideTexture);
	//	}

	static {
		AssemblerUpgrade.upgrades = new HashMap<Byte, AssemblerUpgrade>();
		//		AssemblerUpgrade.sides = new HashMap<Byte, IIcon>();
	}
}
