package vswe.stevescarts.helpers;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDataManagerLockable extends EntityDataManager {
	private boolean isLocked;
	private List lockedList;

	public EntityDataManagerLockable(Entity entity) {
		super(entity);
		for(DataEntry entry : entity.getDataManager().getAll()){
			register(entry.getKey(), entry.getValue());
		}
		this.isLocked = true;
	}

	public void release() {
		this.isLocked = false;
		if (this.lockedList != null) {
			setEntryValues(lockedList);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setEntryValues(List<DataEntry<?>> entriesIn) {
		if (this.isLocked) {
			this.lockedList = entriesIn;
		} else {
			super.setEntryValues(entriesIn);
		}
	}


}
