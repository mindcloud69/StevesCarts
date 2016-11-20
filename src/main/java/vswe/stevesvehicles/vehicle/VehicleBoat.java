package vswe.stevesvehicles.vehicle;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import vswe.stevesvehicles.vehicle.entity.EntityModularBoat;

public class VehicleBoat extends VehicleBase {
	public static final DataParameter<Boolean> IS_WORKING = EntityDataManager.createKey(EntityModularBoat.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> IS_DISANABLED = EntityDataManager.createKey(EntityModularBoat.class, DataSerializers.BOOLEAN);

	public VehicleBoat(EntityModularBoat entity) {
		super(entity);
	}

	public VehicleBoat(EntityModularBoat entity, NBTTagCompound info, String name) {
		super(entity, info, name);
	}

	private EntityModularBoat getBoat() {
		return (EntityModularBoat) getEntity();
	}

	@Override
	protected DataParameter<Boolean> isWorkingParameter() {
		return IS_WORKING;
	}

	@Override
	protected DataParameter<Boolean> isDisanabledParameter() {
		return IS_DISANABLED;
	}
}
