package vswe.stevesvehicles.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import vswe.stevesvehicles.tab.CreativeTabLoader;
import vswe.stevesvehicles.tileentity.TileEntityLiquid;

public class BlockLiquidManager extends BlockContainerBase {
	public BlockLiquidManager() {
		super(Material.ROCK);
		setCreativeTab(CreativeTabLoader.blocks);
	}

	/*
	 * private IIcon topIcon; private IIcon botIcon; private IIcon redIcon;
	 * private IIcon blueIcon; private IIcon greenIcon; private IIcon
	 * yellowIcon;
	 * @SideOnly(Side.CLIENT)
	 * @Override public IIcon getIcon(int side, int meta) { if (side == 0) {
	 * return botIcon; } else if (side == 1) { return topIcon; } else if (side
	 * == 2) { return yellowIcon; } else if (side == 3) { return blueIcon; }
	 * else if (side == 4) { return greenIcon; } else { return redIcon; } }
	 * @SideOnly(Side.CLIENT)
	 * @Override public void registerBlockIcons(IIconRegister register) {
	 * topIcon = register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":managers/liquid/top"); botIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":managers/liquid/bot"); redIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":managers/liquid/red"); blueIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":managers/liquid/blue"); greenIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":managers/liquid/green"); yellowIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":managers/liquid/yellow"); }
	 */
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLiquid();
	}
}
