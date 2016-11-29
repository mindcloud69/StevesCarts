package stevesvehicles.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import stevesvehicles.common.core.tabs.CreativeTabLoader;

public class BlockMetalStorage extends Block implements IBlockBase {
	public BlockMetalStorage() {
		super(Material.IRON);
		this.setCreativeTab(CreativeTabLoader.blocks);
		setHardness(5.0F);
		setResistance(10.0F);
		setSoundType(SoundType.METAL);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	private String unlocalizedName;

	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public BlockMetalStorage setUnlocalizedName(String name) {
		this.unlocalizedName = name;
		return this;
	}
}
