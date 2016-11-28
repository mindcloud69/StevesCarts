package stevesvehicles.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import stevesvehicles.common.blocks.tileentitys.TileEntityDistributor;
import stevesvehicles.common.core.tabs.CreativeTabLoader;

public class BlockDistributor extends BlockContainerBase {
	public BlockDistributor() {
		super(Material.ROCK);
		setCreativeTab(CreativeTabLoader.blocks);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityDistributor();
	}
}
