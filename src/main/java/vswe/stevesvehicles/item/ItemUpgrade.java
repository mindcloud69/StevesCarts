package vswe.stevesvehicles.item;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.rendering.models.items.ItemModelManager;
import vswe.stevesvehicles.client.rendering.models.items.TexturedItem;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.tab.CreativeTabLoader;
import vswe.stevesvehicles.tileentity.TileEntityUpgrade;
import vswe.stevesvehicles.upgrade.Upgrade;
import vswe.stevesvehicles.upgrade.registry.UpgradeRegistry;

public class ItemUpgrade extends ItemBlock implements TexturedItem{
	public ItemUpgrade(Block block) {
		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CreativeTabLoader.blocks);
		ItemModelManager.registerItem(this);
	}
	/*
	 * @Override
	 * @SideOnly(Side.CLIENT) public IIcon getIconFromDamage(int dmg){ Upgrade
	 * upgrade = UpgradeRegistry.getUpgradeFromId(dmg); if (upgrade != null) {
	 * return upgrade.getIcon(); } return null; }
	 * @Override
	 * @SideOnly(Side.CLIENT) public void registerIcons(IIconRegister register)
	 * { Upgrade.registerIcons(register); }
	 */

	@Override
	public String getUnlocalizedName(ItemStack item) {
		Upgrade upgrade = UpgradeRegistry.getUpgradeFromId(item.getItemDamage());
		if (upgrade != null) {
			return upgrade.getUnlocalizedNameForItem();
		}
		return "item.unknown";
	}

	private Upgrade getModelUpgrade(int dmg) {
		Upgrade upgrade = UpgradeRegistry.getUpgradeFromId(dmg);
		if(upgrade == null){
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
	public void getSubItems(Item item, CreativeTabs tab, List lst) {
		for (Upgrade upgrade : UpgradeRegistry.getAllUpgrades()) {
			lst.add(upgrade.getItemStack());
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		if (super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tile instanceof TileEntityUpgrade) {
				TileEntityUpgrade upgrade = (TileEntityUpgrade) tile;
				upgrade.setType(stack.getItemDamage());
				if (upgrade.getMaster() != null) {
					upgrade.getMaster().onUpgradeUpdate();
				}
			}
			return true;
		}
		return false;
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
			if(data.getIcon() == null){
				data.setIcon("stevescarts:blocks/upgrades/" + data.getFullRawUnlocalizedName().replace(".", "/").replace(":", "/"));
			}
			return data.getIcon();
		}
		return "stevescarts:items/unknown";
	}

	@Override
	public int getMaxMeta() {
		return UpgradeRegistry.getAllUpgrades().size();
	}
}
