package vswe.stevesvehicles.vehicle.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.network.datasync.EntityDataManager.DataEntry;

public class LockableEntityDataManager extends EntityDataManager {
	private boolean isLocked;
	private List lockedList;

	public LockableEntityDataManager(Entity entity) {
		super(entity);
		for (DataEntry entry : entity.getDataManager().getAll()) {
			register(entry.getKey(), entry.getValue());
		}
		isLocked = true;
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
