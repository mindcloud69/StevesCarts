package vswe.stevescarts.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.items.ItemBlockStorage;

public class BlockMetalStorage extends Block  {

	public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, ItemBlockStorage.blocks.length);

	public BlockMetalStorage() {
		super(Material.IRON);
		this.setCreativeTab(StevesCarts.tabsSC2Blocks);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
	}


	public int damageDropped(final int meta) {
		return meta;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(TYPE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(TYPE));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

}
