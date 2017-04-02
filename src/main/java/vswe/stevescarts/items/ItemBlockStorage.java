package vswe.stevescarts.items;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.helpers.ComponentTypes;
import vswe.stevescarts.helpers.storages.StorageBlock;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemBlockStorage extends ItemBlock {
	public static StorageBlock[] blocks;
	//public IIcon[] icons;

	public static void init() {
		ItemBlockStorage.blocks = new StorageBlock[] { new StorageBlock("Reinforced Metal Block", ComponentTypes.REINFORCED_METAL.getItemStack()),
			new StorageBlock("Galgadorian Block", ComponentTypes.GALGADORIAN_METAL.getItemStack()),
			new StorageBlock("Enhanced Galgadorian Block", ComponentTypes.ENHANCED_GALGADORIAN_METAL.getItemStack()) };
	}

	public static void loadRecipes() {
		for (int i = 0; i < ItemBlockStorage.blocks.length; ++i) {
			ItemBlockStorage.blocks[i].loadRecipe(i);
		}
	}

	public ItemBlockStorage(final Block block) {
		super(block);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

	//	@SideOnly(Side.CLIENT)
	//	public IIcon getIconFromDamage(int dmg) {
	//		dmg %= this.icons.length;
	//		return this.icons[dmg];
	//	}
	//
	//	@SideOnly(Side.CLIENT)
	//	public void registerIcons(final IIconRegister register) {
	//		this.icons = new IIcon[ItemBlockStorage.blocks.length];
	//		for (int i = 0; i < this.icons.length; ++i) {
	//			final IIcon[] icons = this.icons;
	//			final int n = i;
	//			final StringBuilder sb = new StringBuilder();
	//			StevesCarts.instance.getClass();
	//			icons[n] = register.registerIcon(sb.append("stevescarts").append(":").append(ItemBlockStorage.blocks[i].getName().replace(":", "").replace(" ", "_").toLowerCase()).toString());
	//		}
	//	}

	public String getName(
		@Nonnull
			ItemStack item) {
		if (item.isEmpty()) {
			return "Unknown";
		}
		int dmg = item.getItemDamage();
		dmg %= ItemBlockStorage.blocks.length;
		return ItemBlockStorage.blocks[dmg].getName();
	}

	@Override
	public String getUnlocalizedName(
		@Nonnull
			ItemStack item) {
		if (!item.isEmpty()) {
			final StringBuilder append = new StringBuilder().append("item.");
			final StevesCarts instance = StevesCarts.instance;
			return append.append("SC2:").append("BlockStorage").append(item.getItemDamage()).toString();
		}
		return "item.unknown";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(final Item item, final CreativeTabs tab, final NonNullList items) {
		for (int i = 0; i < ItemBlockStorage.blocks.length; ++i) {
			items.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public int getMetadata(final int dmg) {
		return dmg;
	}
}
