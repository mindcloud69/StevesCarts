package vswe.stevescarts.blocks;

import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.entitys.EntityMinecartModular;

public class BlockRailJunction extends BlockRail {

	public static PropertyEnum<EnumRailDirection> SHAPE = BlockRail.SHAPE;

	public BlockRailJunction() {
		super();
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
		this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { SHAPE });
	}

	@Override
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public EnumRailDirection getRailDirection(IBlockAccess world, BlockPos pos, IBlockState state, EntityMinecart cart) {
		if (cart instanceof EntityMinecartModular) {
			final EntityMinecartModular modularCart = (EntityMinecartModular) cart;
			EnumRailDirection direction = modularCart.getRailDirection(pos);
			if (direction != null) {
				return direction;
			}
		}
		return super.getRailDirection(world, pos, state, cart);
	}

}
