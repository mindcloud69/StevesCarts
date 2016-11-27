package stevesvehicles.common.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.rendering.models.items.ItemModelManager;
import stevesvehicles.client.rendering.models.items.TexturedItem;
import stevesvehicles.common.core.Constants;
import stevesvehicles.common.core.tabs.CreativeTabLoader;
import stevesvehicles.common.upgrades.Upgrade;
import stevesvehicles.common.upgrades.registries.UpgradeRegistry;

public class ItemUpgrade extends Item implements TexturedItem {
	public ItemUpgrade() {
		setRegistryName(new ResourceLocation(Constants.MOD_ID, "upgrade"));
		setHasSubtypes(true);
		setCreativeTab(CreativeTabLoader.blocks);
		ItemModelManager.registerItem(this);
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		Upgrade upgrade = UpgradeRegistry.getUpgradeFromId(item.getItemDamage());
		if (upgrade != null) {
			return upgrade.getUnlocalizedNameForItem();
		}
		return Constants.UNKNOWN_ITEM;
	}

	private Upgrade getModelUpgrade(int dmg) {
		Upgrade upgrade = UpgradeRegistry.getUpgradeFromId(dmg);
		if (upgrade == null) {
			upgrade = UpgradeRegistry.getAllUpgrades().get(dmg);
		}
		return upgrade;
	}

	@Override
	public String getCustomModelLocation(ItemStack stack) {
		Upgrade upgrade = getModelUpgrade(stack.getItemDamage());
		if (upgrade != null) {
			return upgrade.getUnlocalizedNameForItem();
		}
		return getUnlocalizedName();
	}

	@Override
	public boolean useMeshDefinition() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList lst) {
		for (Upgrade upgrade : UpgradeRegistry.getAllUpgrades()) {
			lst.add(upgrade.getItemStack());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List lst, boolean useExtraInfo) {
		Upgrade upgrade = UpgradeRegistry.getUpgradeFromId(item.getItemDamage());
		if (upgrade != null) {
			upgrade.addInfo(lst);
		}
	}

	@Override
	public String getTextureName(int damage) {
		Upgrade data = getModelUpgrade(damage);
		if (data != null) {
			if (data.getIcon() == null) {
				data.setIcon(Constants.MOD_ID + ":blocks/upgrades/" + data.getFullRawUnlocalizedName().replace(".", "/").replace(":", "/"));
			}
			return data.getIcon();
		}
		return Constants.UNKNOWN_SPRITE;
	}

	@Override
	public int getMaxMeta() {
		return UpgradeRegistry.getAllUpgrades().size();
	}
}
