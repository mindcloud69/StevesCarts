package stevesvehicles.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import stevesvehicles.common.blocks.tileentitys.TileEntityActivator;
import stevesvehicles.common.core.tabs.CreativeTabLoader;

public class BlockActivator extends BlockContainerBase {
	public BlockActivator() {
		super(Material.ROCK);
		setCreativeTab(CreativeTabLoader.blocks);
	}

	/*
	 * private IIcon topIcon; private IIcon botIcon; private IIcon sideIcon;
	 * @SideOnly(Side.CLIENT)
	 * @Override public IIcon getIcon(int side, int meta) { if (side == 0) {
	 * return botIcon; } else if (side == 1) { return topIcon; } else { return
	 * sideIcon; } }
	 * @SideOnly(Side.CLIENT)
	 * @Override public void registerBlockIcons(IIconRegister register) {
	 * topIcon = register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":toggler/top"); botIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":toggler/bot"); sideIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":toggler/side"); }
	 */
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityActivator();
	}
}
