package vswe.stevesvehicles.block;

import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;

import net.minecraft.block.BlockRailBase.EnumRailDirection;

public class BlockSpecialRailBase extends BlockRailBase implements IBlockBase {
	private String unlocalizedName;

	protected BlockSpecialRailBase(boolean p_i45389_1_) {
		super(p_i45389_1_);
		setSoundType(SoundType.METAL);
	}

	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public BlockSpecialRailBase setUnlocalizedName(String name) {
		this.unlocalizedName = name;
		return this;
	}

	@Override
	public IProperty<EnumRailDirection> getShapeProperty() {
		return BlockRail.SHAPE;
	}
}
