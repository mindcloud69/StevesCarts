package vswe.stevescarts.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.StevesCarts;

public class BlockRailJunction extends BlockRail {
//	private IIcon normalIcon;
//	private IIcon cornerIcon;

	public static PropertyEnum<EnumRailDirection> SHAPE = BlockRail.SHAPE;
	
	public BlockRailJunction() {
		super();
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
    }
	
	//
//	public IIcon getIcon(final int side, final int meta) {
//		return (meta >= 6) ? this.cornerIcon : this.normalIcon;
//	}
//
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(final IIconRegister register) {
//		final StringBuilder sb = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.normalIcon = register.registerIcon(sb.append("stevescarts").append(":").append("junction_rail").toString());
//		final StringBuilder sb2 = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.cornerIcon = register.registerIcon(sb2.append("stevescarts").append(":").append("junction_rail").append("_corner").toString());
//	}

    public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty(){
        return SHAPE;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta));
    }

    public int getMetaFromState(IBlockState state) {
        return ((BlockRailBase.EnumRailDirection)state.getValue(SHAPE)).getMetadata();
    }

    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, new IProperty[] {SHAPE});
    }
	
	@Override
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public EnumRailDirection getRailDirection(IBlockAccess world, BlockPos pos, IBlockState state, EntityMinecart cart) {
		if (cart instanceof MinecartModular) {
			final MinecartModular modularCart = (MinecartModular) cart;
			EnumRailDirection direction = modularCart.getRailDirection(pos);
			if (direction != null) {
				return direction;
			}
		}
		return super.getRailDirection(world, pos, state, cart);
	}

}
