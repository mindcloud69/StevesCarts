package stevesvehicles.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import stevesvehicles.common.core.tabs.CreativeTabLoader;
import stevesvehicles.common.vehicles.entitys.EntityModularCart;

public class BlockRailJunction extends BlockRailSpecialBase {
	public BlockRailJunction() {
		super(false);
		setCreativeTab(CreativeTabLoader.blocks);
		setSoundType(SoundType.METAL);
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
}
