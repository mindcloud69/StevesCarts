package vswe.stevescarts.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.TileEntities.TileEntityLiquid;

public class BlockLiquidManager extends BlockContainerBase {
//	private IIcon topIcon;
//	private IIcon botIcon;
//	private IIcon redIcon;
//	private IIcon blueIcon;
//	private IIcon greenIcon;
//	private IIcon yellowIcon;

	public BlockLiquidManager() {
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
//		this.topIcon = register.registerIcon(sb.append("stevescarts").append(":").append("liquid_manager").append("_top").toString());
//		final StringBuilder sb2 = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.botIcon = register.registerIcon(sb2.append("stevescarts").append(":").append("liquid_manager").append("_bot").toString());
//		final StringBuilder sb3 = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.redIcon = register.registerIcon(sb3.append("stevescarts").append(":").append("liquid_manager").append("_red").toString());
//		final StringBuilder sb4 = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.blueIcon = register.registerIcon(sb4.append("stevescarts").append(":").append("liquid_manager").append("_blue").toString());
//		final StringBuilder sb5 = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.greenIcon = register.registerIcon(sb5.append("stevescarts").append(":").append("liquid_manager").append("_green").toString());
//		final StringBuilder sb6 = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.yellowIcon = register.registerIcon(sb6.append("stevescarts").append(":").append("liquid_manager").append("_yellow").toString());
//	}

	@Override
	public void breakBlock(World par1World, BlockPos pos, IBlockState state) {
		final TileEntityLiquid var7 = (TileEntityLiquid) par1World.getTileEntity(pos);
		if (var7 != null) {
			for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
				final ItemStack var9 = var7.getStackInSlot(var8);
				if (var9 != null) {
					final float var10 = par1World.rand.nextFloat() * 0.8f + 0.1f;
					final float var11 = par1World.rand.nextFloat() * 0.8f + 0.1f;
					final float var12 = par1World.rand.nextFloat() * 0.8f + 0.1f;
					while (var9.stackSize > 0) {
						int var13 = par1World.rand.nextInt(21) + 10;
						if (var13 > var9.stackSize) {
							var13 = var9.stackSize;
						}
						final ItemStack itemStack = var9;
						itemStack.stackSize -= var13;
						final EntityItem var14 = new EntityItem(par1World, (double) (pos.getX() + var10), (double) (pos.getY() + var11), (double) (pos.getZ() + var12), new ItemStack(var9.getItem(), var13, var9.getItemDamage()));
						final float var15 = 0.05f;
						var14.motionX = (float) par1World.rand.nextGaussian() * var15;
						var14.motionY = (float) par1World.rand.nextGaussian() * var15 + 0.2f;
						var14.motionZ = (float) par1World.rand.nextGaussian() * var15;
						if (var9.hasTagCompound()) {
							var14.getEntityItem().setTagCompound(var9.getTagCompound().copy());
						}
						par1World.spawnEntityInWorld(var14);
					}
				}
			}
		}
		super.breakBlock(par1World, pos, state);
	}

	public boolean onBlockActivated(final World world, final int i, final int j, final int k, final EntityPlayer entityplayer, final int par6, final float par7, final float par8, final float par9) {
		if (entityplayer.isSneaking()) {
			return false;
		}
		if (world.isRemote) {
			return true;
		}
		FMLNetworkHandler.openGui(entityplayer, StevesCarts.instance, 2, world, i, j, k);
		return true;
	}

	public TileEntity createNewTileEntity(final World world, final int var2) {
		return new TileEntityLiquid();
	}
}
