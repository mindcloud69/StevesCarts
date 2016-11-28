package stevesvehicles.common.blocks;

import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockRailSpecialBase extends BlockRailBase implements IBlockBase {
	public static PropertyEnum<EnumRailDirection> SHAPE = BlockRail.SHAPE;
	private String unlocalizedName;

	protected BlockRailSpecialBase(boolean p_i45389_1_) {
		super(p_i45389_1_);
		setSoundType(SoundType.METAL);
	}

	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public BlockRailSpecialBase setUnlocalizedName(String name) {
		this.unlocalizedName = name;
		return this;
	}

	@Override
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
		return SHAPE;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(SHAPE).getMetadata();
	}

	@Override
	@SuppressWarnings("incomplete-switch")
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		switch (rot) {
			case CLOCKWISE_180:
				switch (state.getValue(SHAPE)) {
					case ASCENDING_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST);
					case ASCENDING_NORTH:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_WEST);
					case SOUTH_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_EAST);
					case NORTH_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.SOUTH_EAST);
					case NORTH_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.SOUTH_WEST);
				}
			case COUNTERCLOCKWISE_90:
				switch (state.getValue(SHAPE)) {
					case ASCENDING_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
					case ASCENDING_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
					case ASCENDING_NORTH:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST);
					case ASCENDING_SOUTH:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST);
					case SOUTH_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_EAST);
					case SOUTH_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.SOUTH_EAST);
					case NORTH_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.SOUTH_WEST);
					case NORTH_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_WEST);
					case NORTH_SOUTH:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.EAST_WEST);
					case EAST_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH);
				}
			case CLOCKWISE_90:
				switch (state.getValue(SHAPE)) {
					case ASCENDING_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
					case ASCENDING_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
					case ASCENDING_NORTH:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST);
					case ASCENDING_SOUTH:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST);
					case SOUTH_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.SOUTH_WEST);
					case SOUTH_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_WEST);
					case NORTH_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_EAST);
					case NORTH_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.SOUTH_EAST);
					case NORTH_SOUTH:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.EAST_WEST);
					case EAST_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH);
				}
			default:
				return state;
		}
	}

	@Override
	@SuppressWarnings("incomplete-switch")
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		BlockRailBase.EnumRailDirection raildirection = state.getValue(SHAPE);
		switch (mirrorIn) {
			case LEFT_RIGHT:
				switch (raildirection) {
					case ASCENDING_NORTH:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_EAST);
					case SOUTH_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_WEST);
					case NORTH_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.SOUTH_WEST);
					case NORTH_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.SOUTH_EAST);
					default:
						return super.withMirror(state, mirrorIn);
				}
			case FRONT_BACK:
				switch (raildirection) {
					case ASCENDING_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST);
					case ASCENDING_NORTH:
					case ASCENDING_SOUTH:
					default:
						break;
					case SOUTH_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.SOUTH_WEST);
					case SOUTH_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.SOUTH_EAST);
					case NORTH_WEST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_EAST);
					case NORTH_EAST:
						return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_WEST);
				}
		}
		return super.withMirror(state, mirrorIn);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { SHAPE });
	}
}
