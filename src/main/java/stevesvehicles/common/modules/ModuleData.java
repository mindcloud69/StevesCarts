package stevesvehicles.common.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.modules.Module;
import stevesvehicles.api.modules.container.IModuleContainer;
import stevesvehicles.api.modules.data.ILocalizedText;
import stevesvehicles.api.modules.data.IModelData;
import stevesvehicles.api.modules.data.IModuleData;
import stevesvehicles.api.modules.data.IModuleDataGroup;
import stevesvehicles.api.modules.data.IModuleSide;
import stevesvehicles.api.modules.data.IModuleType;
import stevesvehicles.api.modules.handlers.ContentHandler;
import stevesvehicles.api.modules.handlers.IContentHandlerFactory;
import stevesvehicles.api.modules.handlers.IModuleHandler;
import stevesvehicles.api.modules.handlers.ModuleHandlerType;
import stevesvehicles.client.gui.ColorHelper;
import stevesvehicles.client.localization.entry.info.LocalizationLabel;
import stevesvehicles.common.modules.datas.ModuleType;

public class ModuleData extends Impl<IModuleData> implements IModuleData{
	private final Class<? extends Module> moduleClass;
	private final String unlocalizedName;
	private String fullUnlocalizedName;
	private final int modularCost;
	private final IModuleType moduleType;
	private ArrayList<IModuleSide> sides;
	private boolean allowDuplicate;
	private ArrayList<IModuleData> nemesis;
	private ArrayList<IModuleDataGroup> requirement;
	private IModuleData parent;
	private boolean isValid;
	private boolean isLocked;
	private boolean defaultLock;
	private boolean hasRecipe;
	private ArrayList<ILocalizedText> message;
	private ArrayList<ModuleHandlerType> validHandlers;
	private boolean extraData;
	@SideOnly(Side.CLIENT)
	private HashMap<ModuleHandlerType, IModelData> modelDatas;

	public ModuleData(String unlocalizedName, Class<? extends Module> moduleClass, int modularCost) {
		this.moduleClass = moduleClass;
		if (unlocalizedName.contains(":")) {
			System.err.println("The raw unlocalized name can't contain colons. Any colons have been replaced with underscores.");
		}
		this.unlocalizedName = unlocalizedName.replace(":", "_");
		this.modularCost = modularCost;
		IModuleType moduleType = ModuleType.INVALID;
		for (IModuleType type : ModuleType.values()) {
			if (type.getModuleClass().isAssignableFrom(moduleClass)) {
				moduleType = type;
				break;
			}
		}
		this.moduleType = moduleType;
		System.out.println("Created " + this.unlocalizedName + "(" + this.moduleClass.getName() + ") with type " + this.moduleType.getName());
	}

	@Override
	public final Class<? extends Module> getModuleClass() {
		return moduleClass;
	}

	@Override
	public final String getRawUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public final String getFullRawUnlocalizedName() {
		return fullUnlocalizedName;
	}

	@Override
	public final void setFullRawUnlocalizedName(String val) {
		fullUnlocalizedName = val;
	}

	@Override
	public final int getCost() {
		return modularCost;
	}

	@Override
	public final IModuleType getModuleType() {
		return moduleType;
	}

	@Override
	public boolean getIsValid() {
		return isValid;
	}

	@Override
	public boolean getIsLocked() {
		return isLocked;
	}

	@Override
	public ModuleData lock() {
		isLocked = true;
		return this;
	}

	@Override
	public boolean getEnabledByDefault() {
		return !defaultLock;
	}

	@Override
	public ModuleData lockByDefault() {
		defaultLock = true;
		return this;
	}

	@Override
	public ModuleData setAllowDuplicate(boolean b) {
		allowDuplicate = b;
		return this;
	}

	@Override
	public boolean getAllowDuplicate() {
		return allowDuplicate;
	}

	@Override
	public ModuleData setHasExtraData(boolean val) {
		extraData = val;
		return this;
	}

	@Override
	public boolean hasExtraData() {
		return extraData;
	}

	@Override
	public void addDefaultExtraData(NBTTagCompound compound) {
	}

	@Override
	public void addExtraData(NBTTagCompound compound, Module module) {
	}

	@Override
	public void readExtraData(NBTTagCompound compound, Module moduleBase) {
	}

	@Override
	public String getModuleInfoText(NBTTagCompound compound) {
		return null;
	}

	@Override
	public String getCartInfoText(String name, NBTTagCompound compound) {
		return name;
	}

	@Override
	public ArrayList<IModuleSide> getSides() {
		return sides;
	}

	@Override
	public ModuleData addSides(IModuleSide... sides) {
		if (this.sides == null) {
			this.sides = new ArrayList<>();
		}
		Collections.addAll(this.sides, sides);
		return this;
	}

	@Override
	public ModuleData addParent(IModuleData parent) {
		this.parent = parent;
		return this;
	}

	@Override
	public ModuleData addMessage(ILocalizedText s) {
		if (message == null) {
			message = new ArrayList<>();
		}
		message.add(s);
		return this;
	}

	@Override
	public void addNemesis(IModuleData nemesis) {
		if (this.nemesis == null) {
			this.nemesis = new ArrayList<>();
		}
		this.nemesis.add(nemesis);
	}

	@Override
	public IModuleData addRequirement(IModuleDataGroup requirement) {
		if (this.requirement == null) {
			this.requirement = new ArrayList<>();
		}
		this.requirement.add(requirement);
		return this;
	}

	public static void addNemesis(ModuleData m1, ModuleData m2) {
		m2.addNemesis(m1);
		m1.addNemesis(m2);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IModuleData addModelData(ModuleHandlerType type, IModelData modelData) {
		if(!modelDatas.containsKey(type)){
			modelDatas.put(type, modelData);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IModelData getModelData(ModuleHandlerType type) {
		return modelDatas.get(type);
	}

	@Override
	public String getName() {
		return I18n.translateToLocal(getUnlocalizedName());
	}

	@Override
	public String getUnlocalizedName() {
		return getUnlocalizedNameForItem() + ".name";
	}

	@Override
	public String getUnlocalizedNameForItem() {
		return "steves_vehicles:item." + getFullRawUnlocalizedName();
	}

	@Override
	public IModuleData getParent() {
		return parent;
	}

	@Override
	public ArrayList<IModuleData> getNemesis() {
		return nemesis;
	}

	@Override
	public ArrayList<IModuleDataGroup> getRequirement() {
		return requirement;
	}

	@Override
	public boolean getHasRecipe() {
		return hasRecipe;
	}

	@Override
	public void addSpecificInformation(List<String> list) {
		list.add(ColorHelper.LIGHT_GRAY + LocalizationLabel.MODULAR_COST.translate() + ": " + modularCost);
	}

	public static final String NBT_MODULE_EXTRA_DATA = "ExtraData";

	@Override
	public final void addInformation(List<String> list, NBTTagCompound compound) {
		addSpecificInformation(list);
		if (compound != null && compound.hasKey(NBT_MODULE_EXTRA_DATA)) {
			NBTTagCompound extraData = compound.getCompoundTag(NBT_MODULE_EXTRA_DATA);
			if (extraData != null) {
				String extraDataInfo = getModuleInfoText(extraData);
				if (extraDataInfo != null) {
					list.add(ColorHelper.WHITE + extraDataInfo);
				}
			}
		}
		if (GuiScreen.isShiftKeyDown()) {
			if (sides == null || sides.size() == 0) {
				list.add(ColorHelper.CYAN + LocalizationLabel.NO_SIDES.translate());
			} else {
				String sidesText = "";
				for (int i = 0; i < sides.size(); i++) {
					IModuleSide side = sides.get(i);
					if (i == 0) {
						sidesText += side.toString();
					} else if (i == sides.size() - 1) {
						sidesText += " " + LocalizationLabel.AND.translate() + " " + side.toString();
					} else {
						sidesText += ", " + side.toString();
					}
				}
				list.add(ColorHelper.CYAN + LocalizationLabel.SIDES.translate(sidesText, String.valueOf(sides.size())));
			}
			if (getNemesis() != null && getNemesis().size() != 0) {
				if (sides == null || sides.size() == 0) {
					list.add(ColorHelper.RED + LocalizationLabel.MODULE_CONFLICT_HOWEVER.translate() + ":");
				} else {
					list.add(ColorHelper.RED + LocalizationLabel.MODULE_CONFLICT_ALSO.translate() + ":");
				}
				for (IModuleData module : getNemesis()) {
					list.add(ColorHelper.RED + module.getName());
				}
			}
			if (parent != null) {
				list.add(ColorHelper.YELLOW + LocalizationLabel.REQUIRES.translate() + " " + parent.getName());
			}
			if (getRequirement() != null && getRequirement().size() != 0) {
				for (IModuleDataGroup group : getRequirement()) {
					list.add(ColorHelper.YELLOW + LocalizationLabel.REQUIRES.translate() + " " + group.getCountName() + " " + group.getName());
				}
			}
			if (getAllowDuplicate()) {
				list.add(ColorHelper.LIME + LocalizationLabel.DUPLICATES.translate());
			}
			if (validHandlers == null || validHandlers.isEmpty()) {
				list.add(ColorHelper.RED + LocalizationLabel.MISSING_VEHICLE_ERROR.translate());
			} else {
				String vehicleText = "";
				for (int i = 0; i < validHandlers.size(); i++) {
					ModuleHandlerType vehicle = validHandlers.get(i);
					if (i == 0) {
						vehicleText += vehicle.getName();
					} else if (i == validHandlers.size() - 1) {
						vehicleText += " " + LocalizationLabel.AND.translate() + " " + vehicle.getName();
					} else {
						vehicleText += ", " + vehicle.getName();
					}
				}
				list.add(ColorHelper.MAGENTA + LocalizationLabel.VEHICLE_TYPES.translate(vehicleText, String.valueOf(validHandlers.size())));
			}
		}
		list.add(ColorHelper.LIGHT_BLUE + LocalizationLabel.TYPE.translate() + ": " + moduleType.getName());
		addExtraMessage(list);
	}

	private static final int MAX_MESSAGE_ROW_LENGTH = 30;

	@Override
	public void addExtraMessage(List<String> list) {
		if (message != null) {
			list.add("");
			for (ILocalizedText m : message) {
				String str = m.translate();
				if (str.length() <= MAX_MESSAGE_ROW_LENGTH) {
					addExtraMessage(list, str);
				} else {
					String[] words = str.split(" ");
					String row = "";
					for (String word : words) {
						String next = (row + " " + word).trim();
						if (next.length() <= MAX_MESSAGE_ROW_LENGTH) {
							row = next;
						} else {
							addExtraMessage(list, row);
							row = word;
						}
					}
					addExtraMessage(list, row);
				}
			}
		}
	}

	private void addExtraMessage(List<String> list, String str) {
		list.add(ColorHelper.GRAY + "\u00a7o" + str + "\u00a7r");
	}

	@Override
	public ModuleData addHandlers(ModuleHandlerType... types) {
		if (validHandlers == null) {
			validHandlers = new ArrayList<>();
		}
		for (ModuleHandlerType type : types) {
			if (validHandlers.size() > 0 && moduleType == ModuleType.HULL) {
				System.err.println("You can't add more than one vehicle type to a hull module. Failed to add type " + type.getUnlocalizedName() + " to " + getRawUnlocalizedName());
				break;
			}
			validHandlers.add(type);
		}
		return this;
	}

	@Override
	public ArrayList<ModuleHandlerType> getValidHandlers() {
		return validHandlers;
	}

	@Override
	public Module createModule(IModuleContainer container, IModuleHandler handler, ItemStack stack) {
		return null;
	}

	protected void initOptionalHandlers(Module module) {
		for (IContentHandlerFactory factory : factorys) {
			ContentHandler handler = factory.createHandler(module);
		}
	}

	private final List<IContentHandlerFactory> factorys = new ArrayList<>();

	@Override
	public void addOptionalHandlers(IContentHandlerFactory... factorys) {
		if (factorys != null) {
			Collections.addAll(this.factorys, factorys);
		}
	}
}
