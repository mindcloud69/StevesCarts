package stevesvehicles.common.upgrades.effects;

import net.minecraft.nbt.NBTTagCompound;
import stevesvehicles.common.blocks.tileentitys.TileEntityUpgrade;

public abstract class BaseEffect {
	protected final TileEntityUpgrade upgrade;

	public BaseEffect(TileEntityUpgrade upgrade) {
		this.upgrade = upgrade;
	}

	public void update() {
	}

	public void init() {
	}

	public void removed() {
	}

	public void load(NBTTagCompound compound) {
	}

	public void save(NBTTagCompound compound) {
	}

	public final TileEntityUpgrade getUpgrade() {
		return upgrade;
	}
}
