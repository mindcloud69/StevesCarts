package stevesvehicles.common.upgrades.effects;

import net.minecraft.nbt.NBTTagCompound;
import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;

public abstract class BaseEffect {
	protected final UpgradeContainer upgrade;

	public BaseEffect(UpgradeContainer upgrade) {
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

	public final UpgradeContainer getUpgrade() {
		return upgrade;
	}
}
