package vswe.stevescarts.Items;

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
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.TileEntities.TileEntityUpgrade;
import vswe.stevescarts.Upgrades.AssemblerUpgrade;
import vswe.stevescarts.Upgrades.BaseEffect;

import java.util.List;

public class ItemUpgrade extends ItemBlock {
	public ItemUpgrade(final Block block) {
		super(block);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

//	@SideOnly(Side.CLIENT)
//	public IIcon getIconFromDamage(final int dmg) {
//		final AssemblerUpgrade upgrade = AssemblerUpgrade.getUpgrade(dmg);
//		if (upgrade != null) {
//			return upgrade.getIcon();
//		}
//		return null;
//	}
//
//	@SideOnly(Side.CLIENT)
//	public void registerIcons(final IIconRegister register) {
//		for (final AssemblerUpgrade upgrade : AssemblerUpgrade.getUpgradesList()) {
//			upgrade.createIcon(register);
//		}
//		AssemblerUpgrade.initSides(register);
//	}

	public String getName(final ItemStack item) {
		final AssemblerUpgrade upgrade = AssemblerUpgrade.getUpgrade(item.getItemDamage());
		if (upgrade != null) {
			return upgrade.getName();
		}
		return "Unknown";
	}

	public String getUnlocalizedName(final ItemStack item) {
		final AssemblerUpgrade upgrade = AssemblerUpgrade.getUpgrade(item.getItemDamage());
		if (upgrade != null) {
			return "item.SC2:" + upgrade.getRawName();
		}
		return "item.unknown";
	}

	@SideOnly(Side.CLIENT)
	public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
		for (final AssemblerUpgrade upgrade : AssemblerUpgrade.getUpgradesList()) {
			final ItemStack iStack = new ItemStack(par1, 1, (int) upgrade.getId());
			par3List.add(iStack);
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		if(super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)){
			final TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tile instanceof TileEntityUpgrade) {
				final TileEntityUpgrade upgrade = (TileEntityUpgrade) tile;
				upgrade.setType(stack.getItemDamage());
			}
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4) {
		final AssemblerUpgrade upgrade = AssemblerUpgrade.getUpgrade(par1ItemStack.getItemDamage());
		if (upgrade != null) {
			for (final BaseEffect effect : upgrade.getEffects()) {
				par3List.add(effect.getName());
			}
		}
	}
}
