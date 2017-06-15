package vswe.stevescarts.items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.blocks.tileentities.TileEntityUpgrade;
import vswe.stevescarts.renders.model.ItemModelManager;
import vswe.stevescarts.renders.model.TexturedItem;
import vswe.stevescarts.upgrades.AssemblerUpgrade;
import vswe.stevescarts.upgrades.BaseEffect;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemUpgrade extends ItemBlock implements TexturedItem {
	public ItemUpgrade(final Block block) {
		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(StevesCarts.tabsSC2Blocks);
		ItemModelManager.registerItem(this);
	}

	public String getName(
		@Nonnull
			ItemStack item) {
		final AssemblerUpgrade upgrade = AssemblerUpgrade.getUpgrade(item.getItemDamage());
		if (upgrade != null) {
			return upgrade.getName();
		}
		return "Unknown";
	}

	@Override
	public String getUnlocalizedName(
		@Nonnull
			ItemStack item) {
		final AssemblerUpgrade upgrade = AssemblerUpgrade.getUpgrade(item.getItemDamage());
		if (upgrade != null) {
			return "item.SC2:" + upgrade.getRawName();
		}
		return "item.unknown";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(final CreativeTabs par2CreativeTabs, final NonNullList<ItemStack> par3List) {
		if (!isInCreativeTab(par2CreativeTabs)) {
			return;
		}
		for (final AssemblerUpgrade upgrade : AssemblerUpgrade.getUpgradesList()) {
			@Nonnull
			ItemStack iStack = new ItemStack(this, 1, upgrade.getId());
			par3List.add(iStack);
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		if (super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
			final TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tile instanceof TileEntityUpgrade) {
				final TileEntityUpgrade upgrade = (TileEntityUpgrade) tile;
				upgrade.setType(stack.getItemDamage());
			}
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(
		@Nonnull
			ItemStack par1ItemStack, final World world, final List par3List, final ITooltipFlag par4) {
		final AssemblerUpgrade upgrade = AssemblerUpgrade.getUpgrade(par1ItemStack.getItemDamage());
		if (upgrade != null) {
			for (final BaseEffect effect : upgrade.getEffects()) {
				par3List.add(effect.getName());
			}
		}
	}

	@Override
	public String getTextureName(int damage) {
		AssemblerUpgrade data = AssemblerUpgrade.getUpgrade(damage);
		if (data != null) {
			if (data.getIcon() == null) {
				data.setIcon("stevescarts:blocks/" + data.getRawName().toLowerCase() + "_icon");
			}
			return data.getIcon();
		}
		return "stevescarts:items/unknown_icon";
	}

	@Override
	public int getMaxMeta() {
		return AssemblerUpgrade.getUpgradesList().size();
	}
}
