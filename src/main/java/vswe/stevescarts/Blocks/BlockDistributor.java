package vswe.stevescarts.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.TileEntities.TileEntityDistributor;

public class BlockDistributor extends BlockContainerBase {
//	private IIcon purpleIcon;
//	private IIcon orangeIcon;
//	private IIcon redIcon;
//	private IIcon blueIcon;
//	private IIcon greenIcon;
//	private IIcon yellowIcon;

	public BlockDistributor() {
		super(Material.ROCK);
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(final int side, final int meta) {
//		if (side == 0) {
//			return this.purpleIcon;
//		}
//		if (side == 1) {
//			return this.orangeIcon;
//		}
//		if (side == 2) {
//			return this.yellowIcon;
//		}
//		if (side == 3) {
//			return this.blueIcon;
//		}
//		if (side == 4) {
//			return this.greenIcon;
//		}
//		return this.redIcon;
//	}
//
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(final IIconRegister register) {
//		final StringBuilder sb = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.purpleIcon = register.registerIcon(sb.append("stevescarts").append(":").append("cargo_distributor").append("_purple").toString());
//		final StringBuilder sb2 = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.orangeIcon = register.registerIcon(sb2.append("stevescarts").append(":").append("cargo_distributor").append("_orange").toString());
//		final StringBuilder sb3 = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.redIcon = register.registerIcon(sb3.append("stevescarts").append(":").append("cargo_distributor").append("_red").toString());
//		final StringBuilder sb4 = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.blueIcon = register.registerIcon(sb4.append("stevescarts").append(":").append("cargo_distributor").append("_blue").toString());
//		final StringBuilder sb5 = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.greenIcon = register.registerIcon(sb5.append("stevescarts").append(":").append("cargo_distributor").append("_green").toString());
//		final StringBuilder sb6 = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.yellowIcon = register.registerIcon(sb6.append("stevescarts").append(":").append("cargo_distributor").append("_yellow").toString());
//	}

	public boolean onBlockActivated(final World world, final int i, final int j, final int k, final EntityPlayer entityplayer, final int par6, final float par7, final float par8, final float par9) {
		if (entityplayer.isSneaking()) {
			return false;
		}
		if (world.isRemote) {
			return true;
		}
		FMLNetworkHandler.openGui(entityplayer, StevesCarts.instance, 5, world, i, j, k);
		return true;
	}

	public TileEntity createNewTileEntity(final World world, final int var2) {
		return new TileEntityDistributor();
	}
}
