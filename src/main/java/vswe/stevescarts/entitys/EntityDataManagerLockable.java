package vswe.stevescarts.entitys;

import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class EntityDataManagerLockable extends EntityDataManager {
	private boolean isLocked;
	private List lockedList;

	public EntityDataManagerLockable(Entity entity) {
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
	public void setEntryValues(List<DataEntry<?>> entriesIn) {
		if (isLocked) {
			lockedList = entriesIn;
		} else {
			super.setEntryValues(entriesIn);
		}
	}

}
