package vswe.stevescarts.Items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.StevesCarts;

public class ItemBlockDetector extends ItemBlock {
	public ItemBlockDetector(final Block b) {
		super(b);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

	@Override
	public String getUnlocalizedName(final ItemStack item) {
		if (item != null) {
			return "item.SC2:BlockDetector" + item.getItemDamage();
		}
		return "item.unknown";
	}

	@Override
	public int getMetadata(final int dmg) {
		return dmg;
	}
}
