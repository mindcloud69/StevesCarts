package stevesvehicles.common.vehicles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import stevesvehicles.common.vehicles.entitys.EntityModularCart;

public class VehicleCart extends VehicleBase {
	public static final DataParameter<Boolean> IS_WORKING = EntityDataManager.createKey(EntityModularCart.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> IS_DISANABLED = EntityDataManager.createKey(EntityModularCart.class, DataSerializers.BOOLEAN);

	public VehicleCart(EntityModularCart entity) {
		super(entity);
	}

	public VehicleCart(EntityModularCart entity, NBTTagCompound info, String name) {
		super(entity, info, name);
	}

	private EntityModularCart getCart() {
		return (EntityModularCart) getEntity();
	}

	@Override
	public void updateFuel() {
		super.updateFuel();
		// if a cart is not moving but has fuel for it, start it
		if (hasFuel()) {
			if (!engineFlag) {
				getCart().pushX = getCart().temppushX;
				getCart().pushZ = getCart().temppushZ;
			}
			// if the cart doesn't have fuel but is moving, stop it
		} else if (engineFlag) {
			getCart().temppushX = getCart().pushX;
			getCart().temppushZ = getCart().pushZ;
			getCart().pushX = getCart().pushZ = 0.0D;
		}
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
