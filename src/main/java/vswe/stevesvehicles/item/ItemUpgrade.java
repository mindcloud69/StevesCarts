package vswe.stevesvehicles.item;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import vswe.stevesvehicles.tab.CreativeTabLoader;
import vswe.stevesvehicles.tileentity.TileEntityUpgrade;
import vswe.stevesvehicles.upgrade.Upgrade;
import vswe.stevesvehicles.upgrade.registry.UpgradeRegistry;

public class ItemUpgrade extends ItemBlock {

	public ItemUpgrade(Block block) {
		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CreativeTabLoader.blocks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg){
		Upgrade upgrade = UpgradeRegistry.getUpgradeFromId(dmg);
		if (upgrade != null) {
			return upgrade.getIcon();
		}
		return null;
	}	

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		Upgrade.registerIcons(register);
	}	


	@Override
	public String getUnlocalizedName(ItemStack item) {
		Upgrade upgrade = UpgradeRegistry.getUpgradeFromId(item.getItemDamage());
		if (upgrade != null) {
			return upgrade.getUnlocalizedNameForItem();
		}	

		return "item.unknown";
	}	

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List lst) {
		for (Upgrade upgrade : UpgradeRegistry.getAllUpgrades()) {
			lst.add(upgrade.getItemStack());
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata){

		if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) {
			TileEntity tile = world.getTileEntity(x,y,z);
			if (tile != null && tile instanceof TileEntityUpgrade) {
				TileEntityUpgrade upgrade = (TileEntityUpgrade)tile;
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

}
