package vswe.stevesvehicles.block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import vswe.stevesvehicles.StevesVehicles;
import vswe.stevesvehicles.tab.CreativeTabLoader;
import vswe.stevesvehicles.tileentity.TileEntityCargo;

public class BlockCargoManager extends BlockContainerBase {


	public BlockCargoManager() {
		super(Material.rock);
		setCreativeTab(CreativeTabLoader.blocks);
	}


	private IIcon topIcon;
	private IIcon botIcon;
	private IIcon redIcon;
	private IIcon blueIcon;
	private IIcon greenIcon;
	private IIcon yellowIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		if (side == 0) {
			return botIcon;
		}else if(side == 1) {
			return topIcon;
		}else if(side == 2){
			return yellowIcon;
		}else if(side == 3){
			return blueIcon;
		}else if(side == 4){
			return greenIcon;
		}else{
			return redIcon;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		topIcon = register.registerIcon(StevesVehicles.instance.textureHeader + ":managers/cargo/top");
		botIcon = register.registerIcon(StevesVehicles.instance.textureHeader + ":managers/cargo/bot");
		redIcon = register.registerIcon(StevesVehicles.instance.textureHeader + ":managers/cargo/red");
		blueIcon = register.registerIcon(StevesVehicles.instance.textureHeader + ":managers/cargo/blue");
		greenIcon = register.registerIcon(StevesVehicles.instance.textureHeader + ":managers/cargo/green");
		yellowIcon = register.registerIcon(StevesVehicles.instance.textureHeader + ":managers/cargo/yellow");
	}	






	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityCargo();
	}
}
