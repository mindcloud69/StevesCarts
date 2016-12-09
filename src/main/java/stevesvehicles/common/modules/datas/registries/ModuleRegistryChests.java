package stevesvehicles.common.modules.datas.registries;

import static stevesvehicles.common.items.ComponentTypes.BASKET;
import static stevesvehicles.common.items.ComponentTypes.BURNING_EASTER_EGG;
import static stevesvehicles.common.items.ComponentTypes.CHEST_LOCK;
import static stevesvehicles.common.items.ComponentTypes.CHOCOLATE_EASTER_EGG;
import static stevesvehicles.common.items.ComponentTypes.EXPLOSIVE_EASTER_EGG;
import static stevesvehicles.common.items.ComponentTypes.GLISTERING_EASTER_EGG;
import static stevesvehicles.common.items.ComponentTypes.GREEN_WRAPPING_PAPER;
import static stevesvehicles.common.items.ComponentTypes.PAINTED_EASTER_EGG;
import static stevesvehicles.common.items.ComponentTypes.RED_GIFT_RIBBON;
import static stevesvehicles.common.items.ComponentTypes.RED_WRAPPING_PAPER;
import static stevesvehicles.common.items.ComponentTypes.SIMPLE_PCB;
import static stevesvehicles.common.items.ComponentTypes.STUFFED_SOCK;
import static stevesvehicles.common.items.ComponentTypes.YELLOW_GIFT_RIBBON;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.localization.ILocalizedText;
import stevesvehicles.client.localization.entry.info.LocalizationMessage;
import stevesvehicles.client.rendering.models.common.ModelEggBasket;
import stevesvehicles.client.rendering.models.common.ModelExtractingChests;
import stevesvehicles.client.rendering.models.common.ModelFrontChest;
import stevesvehicles.client.rendering.models.common.ModelGiftStorage;
import stevesvehicles.client.rendering.models.common.ModelSideChests;
import stevesvehicles.client.rendering.models.common.ModelTopChest;
import stevesvehicles.common.core.StevesVehicles;
import stevesvehicles.common.holiday.GiftItem;
import stevesvehicles.common.holiday.HolidayType;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.modules.common.storage.chest.ModuleEggBasket;
import stevesvehicles.common.modules.common.storage.chest.ModuleExtractingChests;
import stevesvehicles.common.modules.common.storage.chest.ModuleFrontChest;
import stevesvehicles.common.modules.common.storage.chest.ModuleGiftStorage;
import stevesvehicles.common.modules.common.storage.chest.ModuleSideChests;
import stevesvehicles.common.modules.common.storage.chest.ModuleTopChest;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleSide;
import stevesvehicles.common.vehicles.VehicleRegistry;

public class ModuleRegistryChests extends ModuleRegistry {
	private static final String PLANK = "plankWood";
	private static final String SLAB = "slabWood";

	public ModuleRegistryChests() {
		super("common.chests");
		ModuleData side = new ModuleData("side_chests", ModuleSideChests.class, 3) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("SideChest", new ModelSideChests());
			}
		};
		side.addShapedRecipe(PLANK, SLAB, PLANK, PLANK, CHEST_LOCK, PLANK, PLANK, SLAB, PLANK);
		side.addSides(ModuleSide.LEFT, ModuleSide.RIGHT);
		side.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(side);
		ModuleData top = new ModuleData("top_chest", ModuleTopChest.class, 6) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				removeModel("Top");
				addModel("TopChest", new ModelTopChest());
			}
		};
		top.addShapedRecipe(SLAB, SLAB, SLAB, SLAB, CHEST_LOCK, SLAB, PLANK, PLANK, PLANK);
		top.addSides(ModuleSide.TOP);
		top.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(top);
		ModuleData front = new ModuleData("front_chest", ModuleFrontChest.class, 5) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("FrontChest", new ModelFrontChest());
				setModelMultiplier(0.68F);
			}
		};
		front.addShapedRecipe(null, PLANK, null, SLAB, CHEST_LOCK, SLAB, PLANK, PLANK, PLANK);
		front.addSides(ModuleSide.FRONT);
		front.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(front);
		ModuleData internal = new ModuleData("internal_storage", ModuleFrontChest.class, 25);
		internal.addShapedRecipe(SLAB, SLAB, SLAB, SLAB, CHEST_LOCK, SLAB, SLAB, SLAB, SLAB);
		internal.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		internal.setAllowDuplicate(true);
		register(internal);
		ModuleData extracting = new ModuleData("extracting_chests", ModuleExtractingChests.class, 75) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("SideChest", new ModelExtractingChests());
			}
		};
		extracting.addShapedRecipe(Items.IRON_INGOT, SIMPLE_PCB, Items.IRON_INGOT, Items.IRON_INGOT, CHEST_LOCK, Items.IRON_INGOT, Items.IRON_INGOT, SIMPLE_PCB, Items.IRON_INGOT);
		extracting.addSides(ModuleSide.LEFT, ModuleSide.RIGHT, ModuleSide.CENTER);
		extracting.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(extracting);
		ModuleData basket = new ModuleDataTreatStorage("egg_basket", ModuleEggBasket.class, 14, LocalizationMessage.EGG) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("TopChest", new ModelEggBasket());
			}

			@Override
			protected void spawnTreat(ModuleBase module) {
				Random rand = module.getVehicle().getRandom();
				int eggs = 1 + rand.nextInt(4) + rand.nextInt(4);
				ItemStack easterEgg = PAINTED_EASTER_EGG.getItemStack(eggs);
				module.setStack(0, easterEgg);
			}
		};
		basket.addShapedRecipe(new ItemStack(Blocks.WOOL, 1, 4), new ItemStack(Blocks.WOOL, 1, 4), new ItemStack(Blocks.WOOL, 1, 4), EXPLOSIVE_EASTER_EGG, CHEST_LOCK, BURNING_EASTER_EGG, GLISTERING_EASTER_EGG, BASKET, CHOCOLATE_EASTER_EGG);
		basket.addSides(ModuleSide.TOP);
		basket.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(basket);
		if (!StevesVehicles.holidays.contains(HolidayType.EASTER)) {
			basket.lock();
		}
		ModuleData gift = new ModuleDataTreatStorage("gift_storage", ModuleGiftStorage.class, 12, LocalizationMessage.GIFT) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("SideChest", new ModelGiftStorage());
			}

			@Override
			protected void spawnTreat(ModuleBase module) {
				Random rand = module.getVehicle().getRandom();
				ArrayList<ItemStack> items = GiftItem.generateItems(rand, GiftItem.ChristmasList, 50 + rand.nextInt(700), 1 + rand.nextInt(5));
				for (int i = 0; i < items.size(); i++) {
					module.getVehicle().setStack(i, items.get(i));
				}
			}
		};
		gift.addShapedRecipe(YELLOW_GIFT_RIBBON, null, RED_GIFT_RIBBON, RED_WRAPPING_PAPER, CHEST_LOCK, GREEN_WRAPPING_PAPER, RED_WRAPPING_PAPER, STUFFED_SOCK, GREEN_WRAPPING_PAPER);
		gift.addSides(ModuleSide.LEFT, ModuleSide.RIGHT);
		gift.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(gift);
		if (!StevesVehicles.holidays.contains(HolidayType.CHRISTMAS)) {
			gift.lock();
		}
	}

	private static final String STORAGE_OPENED = "Opened";

	private static abstract class ModuleDataTreatStorage extends ModuleData {
		private ILocalizedText fullText;

		public ModuleDataTreatStorage(String unlocalizedName, Class<? extends ModuleBase> moduleClass, int modularCost, ILocalizedText fullText) {
			super(unlocalizedName, moduleClass, modularCost);
			setHasExtraData(true);
			this.fullText = fullText;
		}

		@Override
		public void addDefaultExtraData(NBTTagCompound compound) {
			compound.setBoolean(STORAGE_OPENED, false);
		}

		@Override
		public void addExtraData(NBTTagCompound compound, ModuleBase module) {
			compound.setBoolean(STORAGE_OPENED, true);
		}

		@Override
		public void readExtraData(NBTTagCompound compound, ModuleBase moduleBase) {
			if (!compound.getBoolean(STORAGE_OPENED)) {
				spawnTreat(moduleBase);
			}
		}

		@Override
		public String getCartInfoText(String name, NBTTagCompound compound) {
			if (compound.getBoolean(STORAGE_OPENED)) {
				return LocalizationMessage.EMPTY_STORAGE.translate() + " " + name;
			} else {
				return LocalizationMessage.FULL_STORAGE.translate() + " " + name;
			}
		}

		@Override
		public String getModuleInfoText(NBTTagCompound compound) {
			if (compound.getBoolean(STORAGE_OPENED)) {
				return LocalizationMessage.EMPTY_STORAGE.translate();
			} else {
				return fullText.translate();
			}
		}

		protected abstract void spawnTreat(ModuleBase module);
	}
}
