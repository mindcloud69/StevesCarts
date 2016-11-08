package vswe.stevescarts.Upgrades;

import net.minecraft.nbt.NBTTagCompound;
import vswe.stevescarts.TileEntities.TileEntityUpgrade;

public abstract class BaseEffect {
	public void update(final TileEntityUpgrade upgrade) {
	}

	public void init(final TileEntityUpgrade upgrade) {
	}

	public void removed(final TileEntityUpgrade upgrade) {
	}

	public void load(final TileEntityUpgrade upgrade, final NBTTagCompound compound) {
	}

	public void save(final TileEntityUpgrade upgrade, final NBTTagCompound compound) {
	}

	public abstract String getName();
}
