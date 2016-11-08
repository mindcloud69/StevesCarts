package vswe.stevescarts.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.TileEntities.TileEntityUpgrade;
import vswe.stevescarts.Upgrades.AssemblerUpgrade;

public class BlockUpgrade extends BlockContainerBase {
	public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, AssemblerUpgrade.UPGRADE_AMOUNT);

	public BlockUpgrade() {
		super(Material.ROCK);
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
	}

	//	@SideOnly(Side.CLIENT)
	//	public IIcon getIcon(final int side, final int meta) {
	//		return AssemblerUpgrade.getStandardIcon();
	//	}
	//
	//	public void registerBlockIcons(final IIconRegister register) {
	//	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE);
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side != EnumFacing.UP && getMetaFromState(state) == 13;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		((BlockCartAssembler) ModBlocks.CART_ASSEMBLER.getBlock()).addUpgrade(world, pos);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode) {
			worldIn.setBlockState(pos, getStateFromMeta(1), 0);
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		final TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityUpgrade) {
			final TileEntityUpgrade upgrade = (TileEntityUpgrade) tile;
			if (upgrade.getUpgrade() != null) {
				upgrade.getUpgrade().removed(upgrade);
			}
			/*if (getMetaFromState(state) != 1) {
				spawnAsEntity(world, pos, new ItemStack(ModItems.upgrades, 1, getMetaFromState(state)));
			}*/
			dropBlockAsItem(world, pos, state, 1);
			if (upgrade.hasInventory()) {
				for (int var8 = 0; var8 < upgrade.getSizeInventory(); ++var8) {
					final ItemStack var9 = upgrade.getStackInSlotOnClosing(var8);
					if (var9 != null) {
						final float var10 = world.rand.nextFloat() * 0.8f + 0.1f;
						final float var11 = world.rand.nextFloat() * 0.8f + 0.1f;
						final float var12 = world.rand.nextFloat() * 0.8f + 0.1f;
						while (var9.stackSize > 0) {
							int var13 = world.rand.nextInt(21) + 10;
							if (var13 > var9.stackSize) {
								var13 = var9.stackSize;
							}
							final ItemStack itemStack = var9;
							itemStack.stackSize -= var13;
							final EntityItem var14 = new EntityItem(world, pos.getX() + var10, pos.getY() + var11, pos.getZ() + var12, new ItemStack(var9.getItem(), var13, var9.getItemDamage()));
							final float var15 = 0.05f;
							var14.motionX = (float) world.rand.nextGaussian() * var15;
							var14.motionY = (float) world.rand.nextGaussian() * var15 + 0.2f;
							var14.motionZ = (float) world.rand.nextGaussian() * var15;
							if (var9.hasTagCompound()) {
								var14.getEntityItem().setTagCompound(var9.getTagCompound().copy());
							}
							world.spawnEntityInWorld(var14);
						}
					}
				}
			}
		}
		super.breakBlock(world, pos, state);
		((BlockCartAssembler) ModBlocks.CART_ASSEMBLER.getBlock()).removeUpgrade(world, pos);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	//TODO
	/*public int getRenderType() {
		return (this.renderAsNormalBlock() || StevesCarts.instance.blockRenderer == null) ? 0 : StevesCarts.instance.blockRenderer.getRenderId();
	}*/
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return super.getRenderType(state);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return getUpgradeBounds(source, pos);
	}

	public final EnumFacing getUpgradeFace(IBlockAccess world, BlockPos pos){
		final TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityUpgrade) {
			final TileEntityUpgrade upgrade = (TileEntityUpgrade) tile;
			BlockPos master = upgrade.getMaster().getPos();
			if (master == null) {
				return null;
			}
			if (master.getY() < pos.getY()) {
				return EnumFacing.DOWN;
			}
			if (master.getY() > pos.getY()) {
				return EnumFacing.UP;
			}
			if (master.getX() < pos.getX()) {
				return EnumFacing.SOUTH;
			}
			if (master.getX() > pos.getX()) {
				return EnumFacing.EAST;
			}
			if (master.getZ() < pos.getZ()) {
				return EnumFacing.WEST;
			}
			if (master.getZ() >pos.getZ()) {
				return EnumFacing.NORTH;
			}
		}
		return null;
	}

	public final AxisAlignedBB getUpgradeBounds(IBlockAccess world, BlockPos pos) {
		final TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityUpgrade) {
			final TileEntityUpgrade upgrade = (TileEntityUpgrade) tile;
			BlockPos master = upgrade.getMaster().getPos();
			final float margin = 0.1875f;
			final float width = 0.125f;
			if (master == null) {
				return getIdleBlockBounds();
			}
			if (master.getY() < pos.getY()) {
				return new AxisAlignedBB(margin, 0.0f, margin, 1.0f - margin, width, 1.0f - margin);
			}
			if (master.getY() > pos.getY()) {
				return new AxisAlignedBB(margin, 1.0f - width, margin, 1.0f - margin, 1.0f, 1.0f - margin);
			}
			if (master.getX() < pos.getX()) {
				return new AxisAlignedBB(0.0f, margin, margin, width, 1.0f - margin, 1.0f - margin);
			}
			if (master.getX() > pos.getX()) {
				return new AxisAlignedBB(1.0f - width, margin, margin, 1.0f, 1.0f - margin, 1.0f - margin);
			}
			if (master.getZ() < pos.getZ()) {
				return new AxisAlignedBB(margin, margin, 0.0f, 1.0f - margin, 1.0f - margin, width);
			}
			if (master.getZ() > pos.getZ()) {
				return new AxisAlignedBB(margin, margin, 1.0f - width, 1.0f - margin, 1.0f - margin, 1.0f);
			}
		}
		return FULL_BLOCK_AABB;
	}

	public AxisAlignedBB getIdleBlockBounds() {
		final float margin = 0.1875f;
		final float width = 0.125f;
		return new AxisAlignedBB(margin, width, margin, 1.0f - margin, 1.0f - width, 1.0f - margin);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			return false;
		}
		final TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityUpgrade) {
			final TileEntityUpgrade upgrade = (TileEntityUpgrade) tile;
			if (upgrade.getMaster() == null) {
				return false;
			}
			if (world.isRemote) {
				return true;
			}
			if (upgrade.getUpgrade().useStandardInterface()) {
				BlockPos masterPos = upgrade.getMaster().getPos();
				FMLNetworkHandler.openGui(player, StevesCarts.instance, 3, world, masterPos.getX(), masterPos.getY(), masterPos.getZ());
				return true;
			}
			FMLNetworkHandler.openGui(player, StevesCarts.instance, 7, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int var2) {
		return new TileEntityUpgrade();
	}
}
