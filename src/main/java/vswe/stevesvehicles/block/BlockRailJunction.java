package vswe.stevesvehicles.block;

import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import vswe.stevesvehicles.tab.CreativeTabLoader;
import vswe.stevesvehicles.vehicle.entity.EntityModularCart;

import net.minecraft.block.BlockRailBase.EnumRailDirection;

public class BlockRailJunction extends BlockSpecialRailBase {
	// private IIcon normalIcon;
	// private IIcon cornerIcon;
	public static PropertyEnum<EnumRailDirection> SHAPE = BlockRail.SHAPE;

	public BlockRailJunction() {
		super(false);
		setCreativeTab(CreativeTabLoader.blocks);
		setSoundType(SoundType.METAL);
	}

	/*
	 * @Override public IIcon getIcon(int side, int meta) { return meta >= 6 ?
	 * cornerIcon : normalIcon; }
	 * @Override
	 * @SideOnly(Side.CLIENT) public void registerBlockIcons(IIconRegister
	 * register) { normalIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":rails/junction"); cornerIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":rails/junction_corner"); }
	 */
	@Override
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public EnumRailDirection getRailDirection(IBlockAccess world, BlockPos pos, IBlockState state, EntityMinecart cart) {
		if (cart instanceof EntityModularCart) {
			EntityModularCart modularCart = (EntityModularCart) cart;
			EnumRailDirection direction = modularCart.getRailDirection(pos);
			if (direction != null) {
				return direction;
			}
		}
		return super.getRailDirection(world, pos, state, cart);
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
		return new BlockStateContainer(this, SHAPE);
	}
}
