package vswe.stevescarts.modules.engines;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumParticleTypes;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotFuel;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;

import javax.annotation.Nonnull;

public abstract class ModuleCoalBase extends ModuleEngine {
	private int fireCoolDown;
	private int fireIndex;

	private DataParameter<Integer> PRIORITY;

	public ModuleCoalBase(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	protected DataParameter<Integer> getPriorityDw() {
		return PRIORITY;
	}

	@Override
	public void initDw() {
		PRIORITY = createDw(DataSerializers.VARINT);
		super.initDw();
	}

	@Override
	protected void loadFuel() {
		final int consumption = getCart().getConsumption(true) * 2;
		if (getFuelLevel() <= consumption) {
			int i = 0;
			while (i < getInventorySize()) {
				setFuelLevel(getFuelLevel() + SlotFuel.getItemBurnTime(this, getStack(i)));
				if (getFuelLevel() > consumption) {
					if (getStack(i).isEmpty()) {
						break;
					}
					if (getStack(i).getItem().hasContainerItem(getStack(i))) {
						setStack(i, new ItemStack(getStack(i).getItem().getContainerItem()));
					} else {
						@Nonnull
						ItemStack stack = getStack(i);
						stack.shrink(1);
					}
					if (getStack(i).getCount() == 0) {
						setStack(i, ItemStack.EMPTY);
						break;
					}
					break;
				} else {
					++i;
				}
			}
		}
	}

	@Override
	public int getTotalFuel() {
		int totalfuel = getFuelLevel();
		for (int i = 0; i < getInventorySize(); ++i) {
			if (!getStack(i).isEmpty()) {
				totalfuel += SlotFuel.getItemBurnTime(this, getStack(i)) * getStack(i).getCount();
			}
		}
		return totalfuel;
	}

	@Override
	public float[] getGuiBarColor() {
		return new float[] { 0.0f, 0.0f, 0.0f };
	}

	@Override
	public void smoke() {
		double oX = 0.0;
		double oZ = 0.0;
		if (getCart().motionX != 0.0) {
			oX = ((getCart().motionX > 0.0) ? -1 : 1);
		}
		if (getCart().motionZ != 0.0) {
			oZ = ((getCart().motionZ > 0.0) ? -1 : 1);
		}
		if (getCart().rand.nextInt(2) == 0) {
			getCart().world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, getCart().posX + oX * 0.85, getCart().posY + 0.12, getCart().posZ + oZ * 0.85, 0.0, 0.0, 0.0);
		}
		if (getCart().rand.nextInt(30) == 0) {
			getCart().world.spawnParticle(EnumParticleTypes.FLAME, getCart().posX + oX * 0.75, getCart().posY + 0.15, getCart().posZ + oZ * 0.75, getCart().motionX, getCart().motionY, getCart().motionZ);
		}
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotFuel(getCart(), slotId, 8 + x * 18, 23 + 18 * y);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		drawString(gui, Localization.MODULES.ENGINES.COAL.translate(), 8, 6, 4210752);
		String strfuel = Localization.MODULES.ENGINES.NO_FUEL.translate();
		if (getFuelLevel() > 0) {
			strfuel = Localization.MODULES.ENGINES.FUEL.translate(String.valueOf(getFuelLevel()));
		}
		drawString(gui, strfuel, 8, 42, 4210752);
	}

	@Override
	public int numberOfGuiData() {
		return 1;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		updateGuiData(info, 0, (short) getFuelLevel());
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			setFuelLevel(data);
			if (getFuelLevel() < 0) {
				setFuelLevel(getFuelLevel() + 65536);
			}
		}
	}

	@Override
	public void update() {
		super.update();
		if (fireCoolDown <= 0) {
			fireIndex = getCart().rand.nextInt(4) + 1;
			fireCoolDown = 2;
		} else {
			--fireCoolDown;
		}
	}

	public int getFireIndex() {
		if (getCart().isEngineBurning()) {
			return fireIndex;
		}
		return 0;
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		super.Save(tagCompound, id);
		tagCompound.setShort(generateNBTName("Fuel", id), (short) getFuelLevel());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		super.Load(tagCompound, id);
		setFuelLevel(tagCompound.getShort(generateNBTName("Fuel", id)));
		if (getFuelLevel() < 0) {
			setFuelLevel(getFuelLevel() + 65536);
		}
	}

	public abstract double getFuelMultiplier();
}
