package vswe.stevescarts.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.blocks.tileentities.TileEntityCartAssembler;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.data.ModuleData;
import vswe.stevescarts.renders.model.ItemModelManager;
import vswe.stevescarts.renders.model.TexturedItem;

import java.util.List;

public class ItemCartModule extends Item implements TexturedItem {
	//	IIcon unknownIcon;

	public ItemCartModule() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(StevesCarts.tabsSC2);
		ItemModelManager.registerItem(this);
	}

	public String getName(
		@Nonnull
			ItemStack par1ItemStack) {
		final ModuleData data = this.getModuleData(par1ItemStack, true);
		if (data == null) {
			return "Unknown SC2 module";
		}
		return data.getName();
	}

	@Override
	public String getUnlocalizedName() {
		return "item.SC2:unknownmodule";
	}

	@Override
	public String getUnlocalizedName(
		@Nonnull
			ItemStack item) {
		final ModuleData data = this.getModuleData(item, true);
		if (data != null) {
			return "item.SC2:" + data.getRawName();
		}
		return this.getUnlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(final Item item, final CreativeTabs par2CreativeTabs, final List par3List) {
		for (final ModuleData module : ModuleData.getList().values()) {
			if (module.getIsValid()) {
				par3List.add(module.getItemStack());
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(
		@Nonnull
			ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4) {
		final ModuleData module = this.getModuleData(par1ItemStack, true);
		if (module != null) {
			module.addInformation(par3List, par1ItemStack.getTagCompound());
		} else if (par1ItemStack != null && par1ItemStack.getItem() instanceof ItemCartModule) {
			par3List.add("Module id " + par1ItemStack.getItemDamage());
		} else {
			par3List.add("Unknown module id");
		}
	}

	public ModuleData getModuleData(
		@Nonnull
			ItemStack itemstack) {
		return this.getModuleData(itemstack, false);
	}

	public ModuleData getModuleData(
		@Nonnull
			ItemStack itemstack, final boolean ignoreSize) {
		if (itemstack != null && itemstack.getItem() instanceof ItemCartModule && (ignoreSize || itemstack.stackSize != TileEntityCartAssembler.getRemovedSize())) {
			return ModuleData.getList().get((byte) itemstack.getItemDamage());
		}
		return null;
	}

	public void addExtraDataToCart(final NBTTagCompound save,
	                               @Nonnull
		                               ItemStack module, final int i) {
		if (module.getTagCompound() != null && module.getTagCompound().hasKey("Data")) {
			save.setByte("Data" + i, module.getTagCompound().getByte("Data"));
		} else {
			final ModuleData data = this.getModuleData(module, true);
			if (data.isUsingExtraData()) {
				save.setByte("Data" + i, data.getDefaultExtraData());
			}
		}
	}

	public void addExtraDataToModule(final NBTTagCompound save, final ModuleBase module, final int i) {
		if (module.hasExtraData()) {
			save.setByte("Data" + i, module.getExtraData());
		}
	}

	public void addExtraDataToModule(
		@Nonnull
			ItemStack module, final NBTTagCompound info, final int i) {
		NBTTagCompound save = module.getTagCompound();
		if (save == null) {
			module.setTagCompound(save = new NBTTagCompound());
		}
		if (info != null && info.hasKey("Data" + i)) {
			save.setByte("Data", info.getByte("Data" + i));
		} else {
			final ModuleData data = this.getModuleData(module, true);
			if (data.isUsingExtraData()) {
				save.setByte("Data", data.getDefaultExtraData());
			}
		}
	}

	@Override
	public String getTextureName(int damage) {
		ModuleData data = ModuleData.getList().get((byte) damage);
		if (data != null) {
			if (data.getIcon() == null) {
				data.setIcon("stevescarts:items/" + data.getRawName() + "_icon");
			}
			return data.getIcon();
		}
		return "stevescarts:items/unknown_icon";
	}

	@Override
	public int getMaxMeta() {
		return 102;
	}

}
