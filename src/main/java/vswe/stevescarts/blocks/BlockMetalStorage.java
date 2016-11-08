package vswe.stevescarts.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import vswe.stevescarts.StevesCarts;

public class BlockMetalStorage extends Block  {

	public BlockMetalStorage() {
		super(Material.IRON);
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

	//	@SideOnly(Side.CLIENT)
	//	public IIcon getIcon(final int side, int meta) {
	//		meta %= ModItems.storages.icons.length;
	//		return ModItems.storages.icons[meta];
	//	}

	public int damageDropped(final int meta) {
		return meta;
	}

	//	@SideOnly(Side.CLIENT)
	//	public void registerBlockIcons(final IIconRegister register) {
	//	}
}
