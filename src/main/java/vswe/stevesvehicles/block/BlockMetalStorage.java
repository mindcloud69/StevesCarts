package vswe.stevesvehicles.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.tab.CreativeTabLoader;

public class BlockMetalStorage extends Block implements IBlockBase {
	public BlockMetalStorage() {
		super(Material.iron);
		this.setCreativeTab(CreativeTabLoader.blocks);
		setHardness(5.0F);
		setResistance(10.0F);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		meta %= ModItems.storage.icons.length;
		return ModItems.storage.icons[meta];
	}

	public int damageDropped(int meta) {
		return meta;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		// do nothing here
	}

	private String unlocalizedName;

	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public void setUnlocalizedName(String name) {
		this.unlocalizedName = name;
	}
}
