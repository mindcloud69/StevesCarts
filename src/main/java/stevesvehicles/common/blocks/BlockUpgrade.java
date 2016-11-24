package stevesvehicles.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.blocks.tileentitys.TileEntityUpgrade;
import stevesvehicles.common.core.tabs.CreativeTabLoader;
import stevesvehicles.common.upgrades.Upgrade;

public class BlockUpgrade extends BlockContainerBase {
	public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyUpgrade TYPE = new PropertyUpgrade("type");

	public BlockUpgrade() {
		super(Material.ROCK);
		setCreativeTab(CreativeTabLoader.blocks);
		setSoundType(SoundType.METAL);
	}

	/*
	 * @SideOnly(Side.CLIENT)
	 * @Override public IIcon getIcon(int side, int meta) { return
	 * Upgrade.getStandardIcon(); }
	 * @Override public void registerBlockIcons(IIconRegister register) { //
	 * Load nothing here }
	 */
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, TYPE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return getSideFromEnum(state.getValue(FACING));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, getSideFromint(meta));
	}

	public EnumFacing getSideFromint(int i) {
		if (i == 0) {
			return EnumFacing.NORTH;
		} else if (i == 1) {
			return EnumFacing.SOUTH;
		} else if (i == 2) {
			return EnumFacing.EAST;
		} else if (i == 3) {
			return EnumFacing.WEST;
		}
		return EnumFacing.NORTH;
	}

	public int getSideFromEnum(EnumFacing facing) {
		if (facing == EnumFacing.NORTH) {
			return 0;
		} else if (facing == EnumFacing.SOUTH) {
			return 1;
		} else if (facing == EnumFacing.EAST) {
			return 2;
		} else if (facing == EnumFacing.WEST) {
			return 3;
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		Upgrade upgrade = getUpgrade(world, pos);
		if (upgrade != null) {
			state = state.withProperty(TYPE, upgrade);
		}
		return super.getActualState(state, world, pos);
	}

	private Upgrade getUpgrade(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityUpgrade) {
			TileEntityUpgrade upgrade = (TileEntityUpgrade) tile;
			return upgrade.getUpgrade();
		}
		return null;
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		if (side == null) {
			return false;
		}
		Upgrade upgrade = getUpgrade(world, pos);
		return upgrade != null && upgrade.connectToRedstone();
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		((BlockCartAssembler) ModBlocks.CART_ASSEMBLER.getBlock()).addUpgrade(world, pos);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode) {
			world.setBlockState(pos, state, 0);
		}
		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof TileEntityUpgrade) {
			TileEntityUpgrade upgrade = (TileEntityUpgrade) te;
			upgrade.removed();
			if (upgrade.getType() != 1) {
				Upgrade assemblerUpgrade = getUpgrade(world, pos);
				if (assemblerUpgrade != null) {
					spawnAsEntity(world, pos, assemblerUpgrade.getItemStack());
				}
			}
		}
		super.breakBlock(world, pos, state);
		((BlockCartAssembler) ModBlocks.CART_ASSEMBLER.getBlock()).removeUpgrade(world, pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return getUpgradeBounds(source, pos, state);
	}

	public final AxisAlignedBB getUpgradeBounds(IBlockAccess world, BlockPos pos, IBlockState state) {
		EnumFacing side = state.getValue(FACING).getOpposite();
		final float margin = 0.1875f; // 3 pixels
		final float width = 0.125f; // 2 pixels
		if (side == EnumFacing.DOWN) {
			return new AxisAlignedBB(margin, 0.0f, margin, 1.0f - margin, width, 1.0f - margin);
		} else if (side == EnumFacing.UP) {
			return new AxisAlignedBB(margin, 1.0f - width, margin, 1.0f - margin, 1.0f, 1.0f - margin);
		} else if (side == EnumFacing.WEST) {
			return new AxisAlignedBB(0.0f, margin, margin, width, 1.0f - margin, 1.0f - margin);
		} else if (side == EnumFacing.EAST) {
			return new AxisAlignedBB(1.0f - width, margin, margin, 1.0f, 1.0f - margin, 1.0f - margin);
		} else if (side == EnumFacing.NORTH) {
			return new AxisAlignedBB(margin, margin, 0.0f, 1.0f - margin, 1.0f - margin, width);
		} else if (side == EnumFacing.SOUTH) {
			return new AxisAlignedBB(margin, margin, 1.0f - width, 1.0f - margin, 1.0f - margin, 1.0f);
		}
		return FULL_BLOCK_AABB;
	}

	public AxisAlignedBB getIdleBlockBounds() {
		float margin = 0.1875F; // 3 pixels
		float width = 0.125F; // 2 pixels
		return new AxisAlignedBB(margin, width, margin, 1.0f - margin, 1.0f - width, 1.0f - margin);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityUpgrade) {
			TileEntityUpgrade upgrade = (TileEntityUpgrade) tile;
			if (upgrade.useStandardInterface()) {
				if (upgrade.getMaster() != null) {
					return ModBlocks.CART_ASSEMBLER.getBlock().onBlockActivated(world, upgrade.getMaster().getPos(), state, player, hand, side, hitX, hitY, hitZ);
				} else {
					return false;
				}
			} else {
				return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
			}
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityUpgrade();
	}
}
