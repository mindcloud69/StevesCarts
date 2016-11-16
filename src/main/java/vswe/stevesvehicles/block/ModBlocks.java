package vswe.stevesvehicles.block;

import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;

import java.lang.reflect.Constructor;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vswe.stevesvehicles.StevesVehicles;
import vswe.stevesvehicles.item.ItemBlockDetector;
import vswe.stevesvehicles.item.ItemBlockStorage;
import vswe.stevesvehicles.item.ItemUpgrade;
import vswe.stevesvehicles.recipe.IRecipeOutput;
import vswe.stevesvehicles.recipe.ModuleRecipeShaped;
import vswe.stevesvehicles.tileentity.TileEntityActivator;
import vswe.stevesvehicles.tileentity.TileEntityCargo;
import vswe.stevesvehicles.tileentity.TileEntityCartAssembler;
import vswe.stevesvehicles.tileentity.TileEntityDetector;
import vswe.stevesvehicles.tileentity.TileEntityDistributor;
import vswe.stevesvehicles.tileentity.TileEntityLiquid;
import vswe.stevesvehicles.tileentity.TileEntityUpgrade;
import vswe.stevesvehicles.tileentity.detector.DetectorType;

public enum ModBlocks implements IRecipeOutput {
	CARGO_MANAGER("cargo_manager", BlockCargoManager.class, TileEntityCargo.class, "cargo"),
	JUNCTION("junction_rail", BlockRailJunction.class),
	ADVANCED_DETECTOR("advanced_detector_rail", BlockRailAdvancedDetector.class),
	CART_ASSEMBLER("vehicle_assembler", BlockCartAssembler.class, TileEntityCartAssembler.class, "assembler"),
	MODULE_TOGGLER("module_toggler", BlockActivator.class, TileEntityActivator.class, "toggler"),
	EXTERNAL_DISTRIBUTOR("external_distributor", BlockDistributor.class, TileEntityDistributor.class, "distributor"),
	DETECTOR_UNIT("detector_unit", BlockDetector.class, TileEntityDetector.class, "detector"){
		@Override
		protected Item createItem(Block block) {
			return new ItemBlockDetector(block);
		}
	},
	UPGRADE("upgrade", BlockUpgrade.class, TileEntityUpgrade.class, "upgrade"){
		@Override
		protected Item createItem(Block block) {
			return new ItemUpgrade(block);
		}
	},
	LIQUID_MANAGER("liquid_manager", BlockLiquidManager.class, TileEntityLiquid.class, "liquid"),
	STORAGE("metal_storage", BlockMetalStorage.class){
		@Override
		protected Item createItem(Block block) {
			return new ItemBlockStorage(block);
		}
	};
	private final String unlocalizedName;
	private final Class<? extends IBlockBase> clazz;
	private final Class<? extends TileEntity> tileEntityClazz;
	private final String tileEntityName;
	private Block block;

	ModBlocks(String unlocalizedName, Class<? extends IBlockBase> clazz) {
		this(unlocalizedName, clazz, null, null);
	}

	ModBlocks(String unlocalizedName, Class<? extends IBlockBase> clazz, Class<? extends TileEntity> tileEntityClazz, String tileEntityName) {
		this.unlocalizedName = unlocalizedName;
		this.clazz = clazz;
		this.tileEntityClazz = tileEntityClazz;
		this.tileEntityName = tileEntityName;
	}

	public static void init() {
		for (ModBlocks blockInfo : values()) {
			try {
				if (Block.class.isAssignableFrom(blockInfo.clazz)) {
					Constructor<? extends IBlockBase> blockConstructor = blockInfo.clazz.getConstructor();
					Object blockInstance = blockConstructor.newInstance();
					IBlockBase blockBase = (IBlockBase) blockInstance;
					Block block = (Block) blockInstance;
					block.setHardness(2F);
					block.setRegistryName(new ResourceLocation(StevesVehicles.instance.textureHeader, blockInfo.unlocalizedName));
					GameRegistry.register(block);
					Item item = blockInfo.createItem(block);
					item.setRegistryName(block.getRegistryName());
					GameRegistry.register(item);
					blockBase.setUnlocalizedName("steves_vehicles:tile.common:" + blockInfo.unlocalizedName);
					blockInfo.block = block;
					if (blockInfo.tileEntityClazz != null) {
						GameRegistry.registerTileEntity(blockInfo.tileEntityClazz, blockInfo.tileEntityName);
					}
				} else {
					System.err.println("This is not a block (" + blockInfo.unlocalizedName + ")");
				}
			} catch (Exception e) {
				System.err.println("Failed to create block (" + blockInfo.unlocalizedName + ")");
				e.printStackTrace();
			}
		}
	}

	protected Item createItem(Block block){
		return new ItemBlock(block);
	}

	private void addRecipeWithCount(int count, Object... recipe) {
		GameRegistry.addRecipe(new ModuleRecipeShaped(this, count, 3, 3, recipe));
	}

	private void addRecipe(Object... recipe) {
		GameRegistry.addRecipe(new ModuleRecipeShaped(this, 3, 3, recipe));
	}

	private static final String PLANKS = "plankWood";
	private static final String GLASS = "blockGlass";
	private static final String BLUE = "dyeBlue";
	private static final String ORANGE = "dyeOrange";

	public static void addRecipes() {
		CARGO_MANAGER.addRecipe(PLANKS, PLANKS, PLANKS, PLANKS, SIMPLE_PCB, PLANKS, PLANKS, PLANKS, PLANKS);
		LIQUID_MANAGER.addRecipe(GLASS, GLASS, GLASS, GLASS, SIMPLE_PCB, GLASS, GLASS, GLASS, GLASS);
		MODULE_TOGGLER.addRecipe(ORANGE, Items.GOLD_INGOT, BLUE, Blocks.STONE, Items.IRON_INGOT, Blocks.STONE, Items.REDSTONE, SIMPLE_PCB, Items.REDSTONE);
		EXTERNAL_DISTRIBUTOR.addRecipe(Blocks.STONE, SIMPLE_PCB, Blocks.STONE, SIMPLE_PCB, Items.REDSTONE, SIMPLE_PCB, Blocks.STONE, SIMPLE_PCB, Blocks.STONE);
		CART_ASSEMBLER.addRecipe(Items.IRON_INGOT, Blocks.STONE, Items.IRON_INGOT, Blocks.STONE, Items.IRON_INGOT, Blocks.STONE, SIMPLE_PCB, Blocks.STONE, SIMPLE_PCB);
		JUNCTION.addRecipe(null, Items.REDSTONE, null, Items.REDSTONE, Blocks.RAIL, Items.REDSTONE, null, Items.REDSTONE, null);
		ADVANCED_DETECTOR.addRecipeWithCount(2, Items.IRON_INGOT, Blocks.STONE_PRESSURE_PLATE, Items.IRON_INGOT, Items.IRON_INGOT, Items.REDSTONE, Items.IRON_INGOT, Items.IRON_INGOT, Blocks.STONE_PRESSURE_PLATE, Items.IRON_INGOT);
		DetectorType.UNIT.addShapedRecipe(Blocks.COBBLESTONE, Blocks.STONE_PRESSURE_PLATE, Blocks.COBBLESTONE, Items.IRON_INGOT, SIMPLE_PCB, Items.IRON_INGOT, Blocks.COBBLESTONE, Blocks.STONE_PRESSURE_PLATE, Blocks.COBBLESTONE);
		DetectorType.NORMAL.addShapelessRecipe(DetectorType.UNIT, SIMPLE_PCB, Items.REDSTONE);
		DetectorType.STOP.addShapelessRecipe(DetectorType.UNIT, SIMPLE_PCB, Items.IRON_INGOT);
		DetectorType.JUNCTION.addShapelessRecipe(DetectorType.UNIT, SIMPLE_PCB, Blocks.REDSTONE_TORCH);
		DetectorType.REDSTONE.addShapelessRecipe(DetectorType.UNIT, Items.REDSTONE, Items.REDSTONE, Items.REDSTONE);
	}

	public Block getBlock() {
		return block;
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(getBlock());
	}
}
