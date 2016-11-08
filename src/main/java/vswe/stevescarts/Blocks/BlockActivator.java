package vswe.stevescarts.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.TileEntities.TileEntityActivator;

public class BlockActivator extends BlockContainerBase {
	//	private IIcon topIcon;
	//	private IIcon botIcon;
	//	private IIcon sideIcon;

	public BlockActivator() {
		super(Material.ROCK);
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

	//	@SideOnly(Side.CLIENT)
	//	public IIcon getIcon(final int side, final int meta) {
	//		if (side == 0) {
	//			return this.botIcon;
	//		}
	//		if (side == 1) {
	//			return this.topIcon;
	//		}
	//		return this.sideIcon;
	//	}
	//
	//	@SideOnly(Side.CLIENT)
	//	public void registerBlockIcons(final IIconRegister register) {
	//		final StringBuilder sb = new StringBuilder();
	//		StevesCarts.instance.getClass();
	//		this.topIcon = register.registerIcon(sb.append("stevescarts").append(":").append("module_toggler").append("_top").toString());
	//		final StringBuilder sb2 = new StringBuilder();
	//		StevesCarts.instance.getClass();
	//		this.botIcon = register.registerIcon(sb2.append("stevescarts").append(":").append("module_toggler").append("_bot").toString());
	//		final StringBuilder sb3 = new StringBuilder();
	//		StevesCarts.instance.getClass();
	//		this.sideIcon = register.registerIcon(sb3.append("stevescarts").append(":").append("module_toggler").append("_side").toString());
	//	}

	public boolean onBlockActivated(final World world, final int i, final int j, final int k, final EntityPlayer entityplayer, final int par6, final float par7, final float par8, final float par9) {
		if (entityplayer.isSneaking()) {
			return false;
		}
		if (world.isRemote) {
			return true;
		}
		FMLNetworkHandler.openGui(entityplayer, StevesCarts.instance, 4, world, i, j, k);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int var2) {
		return new TileEntityActivator();
	}
}
