package vswe.stevescarts.Items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.ModuleData.ModuleData;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.TileEntities.TileEntityCartAssembler;

import java.util.List;

public class ItemCartModule extends Item {
//	IIcon unknownIcon;

	public ItemCartModule() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(StevesCarts.tabsSC2);
	}

	public String getName(final ItemStack par1ItemStack) {
		final ModuleData data = this.getModuleData(par1ItemStack, true);
		if (data == null) {
			return "Unknown SC2 module";
		}
		return data.getName();
	}

//	@SideOnly(Side.CLIENT)
//	public IIcon getIconFromDamage(final int dmg) {
//		final ModuleData data = ModuleData.getList().get((byte) dmg);
//		if (data != null) {
//			return data.getIcon();
//		}
//		return this.unknownIcon;
//	}
//
//	@SideOnly(Side.CLIENT)
//	public void registerIcons(final IIconRegister register) {
//		for (final ModuleData module : ModuleData.getList().values()) {
//			module.createIcon(register);
//		}
//		final StringBuilder sb = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.unknownIcon = register.registerIcon(sb.append("stevescarts").append(":").append("unknown_icon").toString());
//	}

	public String getUnlocalizedName() {
		return "item.SC2:unknownmodule";
	}

	public String getUnlocalizedName(final ItemStack item) {
		final ModuleData data = this.getModuleData(item, true);
		if (data != null) {
			return "item.SC2:" + data.getRawName();
		}
		return this.getUnlocalizedName();
	}

	@SideOnly(Side.CLIENT)
	public void getSubItems(final Item item, final CreativeTabs par2CreativeTabs, final List par3List) {
		for (final ModuleData module : ModuleData.getList().values()) {
			if (module.getIsValid()) {
				par3List.add(module.getItemStack());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4) {
		final ModuleData module = this.getModuleData(par1ItemStack, true);
		if (module != null) {
			module.addInformation(par3List, par1ItemStack.getTagCompound());
		} else if (par1ItemStack != null && par1ItemStack.getItem() instanceof ItemCartModule) {
			par3List.add("Module id " + par1ItemStack.getItemDamage());
		} else {
			par3List.add("Unknown module id");
		}
	}

	public ModuleData getModuleData(final ItemStack itemstack) {
		return this.getModuleData(itemstack, false);
	}

	public ModuleData getModuleData(final ItemStack itemstack, final boolean ignoreSize) {
		if (itemstack != null && itemstack.getItem() instanceof ItemCartModule && (ignoreSize || itemstack.stackSize != TileEntityCartAssembler.getRemovedSize())) {
			return ModuleData.getList().get((byte) itemstack.getItemDamage());
		}
		return null;
	}

	public void addExtraDataToCart(final NBTTagCompound save, final ItemStack module, final int i) {
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

	public void addExtraDataToModule(final ItemStack module, final NBTTagCompound info, final int i) {
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
}
