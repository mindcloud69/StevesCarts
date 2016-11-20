package vswe.stevesvehicles.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.rendering.models.items.ItemModelManager;
import vswe.stevesvehicles.client.rendering.models.items.TexturedItem;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.tab.CreativeTabLoader;
import vswe.stevesvehicles.tab.CreativeTabVehicle;

public class ItemVehicleModule extends Item implements TexturedItem {
	public ItemVehicleModule() {
		setCreativeTab(CreativeTabs.TRANSPORTATION);
		setHasSubtypes(true);
		ItemModelManager.registerItem(this);
	}

	@Override
	public CreativeTabs[] getCreativeTabs() {
		return CreativeTabLoader.getAllVehicleTabs();
	}
	/*
	 * @Override
	 * @SideOnly(Side.CLIENT) public IIcon getIconFromDamage(int dmg) {
	 * ModuleData data = getModuleData(dmg); if (data != null) { return
	 * data.getIcon(); } return unknownIcon; }
	 */

	@Override
	public String getUnlocalizedName() {
		return "steves_vehicles:item.common:unknown_module.name";
	}

	/*
	 * IIcon unknownIcon;
	 * @Override
	 * @SideOnly(Side.CLIENT) public void registerIcons(IIconRegister register)
	 * { for (ModuleData moduleData : ModuleRegistry.getAllModules()) {
	 * moduleData.createIcon(register); } unknownIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":unknown"); }
	 */
	@Override
	public String getUnlocalizedName(ItemStack item) {
		ModuleData data = getModuleData(item);
		if (data != null) {
			return data.getUnlocalizedNameForItem();
		}
		return getUnlocalizedName();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList lst) {
		for (ModuleData module : ModuleRegistry.getAllModules()) {
			if (module.getIsValid()) {
				ItemStack stack = null;
				if (tab instanceof CreativeTabVehicle) {
					CreativeTabVehicle vehicleTab = (CreativeTabVehicle) tab;
					if (module.getValidVehicles() != null && module.getValidVehicles().contains(vehicleTab.getVehicleType())) {
						stack = module.getItemStack();
					}
				} else {
					stack = module.getItemStack();
				}
				if (stack != null) {
					lst.add(stack);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List lst, boolean useExtraInfo) {
		ModuleData module = getModuleData(item);
		if (module != null) {
			module.addInformation(lst, item.getTagCompound());
		} else if (item != null && item.getItem() == this) {
			lst.add("Module id " + item.getItemDamage());
		} else {
			lst.add("Unknown module id");
		}
	}

	private ModuleData getModuleData(int dmg) {
		return ModuleRegistry.getModuleFromId(dmg);
	}

	public ModuleData getModuleData(ItemStack item) {
		if (item != null && item.getItem() == this) {
			return getModuleData(item.getItemDamage());
		} else {
			return null;
		}
	}

	private ModuleData getModelModuleData(int dmg) {
		ModuleData data = ModuleRegistry.getModuleFromId(dmg);
		if (data == null) {
			data = ModuleRegistry.getAllModules().get(dmg);
		}
		return data;
	}

	@Override
	public String getCustomModelLocation(ItemStack stack) {
		ModuleData data = getModelModuleData(stack.getItemDamage());
		if (data != null) {
			return data.getUnlocalizedNameForItem();
		}
		return getUnlocalizedName();
	}

	@Override
	public boolean useMeshDefinition() {
		return true;
	}

	@Override
	public String getTextureName(int damage) {
		ModuleData data = getModelModuleData(damage);
		if (data != null) {
			if (data.getIcon() == null) {
				data.setIcon("stevescarts:items/modules/" + data.getFullRawUnlocalizedName().replace(".", "/").replace(":", "/"));
			}
			return data.getIcon();
		}
		return "stevescarts:items/unknown";
	}

	@Override
	public int getMaxMeta() {
		return ModuleRegistry.getAllModules().size();
	}
}
