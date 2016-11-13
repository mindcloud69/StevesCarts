package vswe.stevesvehicles.vehicle.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LockableEntityDataManager extends EntityDataManager {
	private boolean isLocked = true;
	private List lockedList;

	public LockableEntityDataManager(Entity entity) {
		super(entity);
	}

	public void release() {
		isLocked = false;
		if (lockedList != null) {
			setEntryValues(lockedList);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setEntryValues(List<DataEntry<?>> entries) {
		if (isLocked) {
			lockedList = entries;
		} else {
			super.setEntryValues(entries);
		}
	}
}
