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
		final int consumption = this.getCart().getConsumption(true) * 2;
		if (this.getFuelLevel() <= consumption) {
			int i = 0;
			while (i < this.getInventorySize()) {
				this.setFuelLevel(this.getFuelLevel() + SlotFuel.getItemBurnTime(this, this.getStack(i)));
				if (this.getFuelLevel() > consumption) {
					if (this.getStack(i) == null) {
						break;
					}
					if (this.getStack(i).getItem().hasContainerItem(this.getStack(i))) {
						this.setStack(i, new ItemStack(this.getStack(i).getItem().getContainerItem()));
					} else {
						final ItemStack stack = this.getStack(i);
						--stack.stackSize;
					}
					if (this.getStack(i).stackSize == 0) {
						this.setStack(i, null);
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
		int totalfuel = this.getFuelLevel();
		for (int i = 0; i < this.getInventorySize(); ++i) {
			if (this.getStack(i) != null) {
				totalfuel += SlotFuel.getItemBurnTime(this, this.getStack(i)) * this.getStack(i).stackSize;
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
		if (this.getCart().motionX != 0.0) {
			oX = ((this.getCart().motionX > 0.0) ? -1 : 1);
		}
		if (this.getCart().motionZ != 0.0) {
			oZ = ((this.getCart().motionZ > 0.0) ? -1 : 1);
		}
		if (this.getCart().rand.nextInt(2) == 0) {
			this.getCart().world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.getCart().posX + oX * 0.85, this.getCart().posY + 0.12, this.getCart().posZ + oZ * 0.85, 0.0, 0.0, 0.0);
		}
		if (this.getCart().rand.nextInt(30) == 0) {
			this.getCart().world.spawnParticle(EnumParticleTypes.FLAME, this.getCart().posX + oX * 0.75, this.getCart().posY + 0.15, this.getCart().posZ + oZ * 0.75, this.getCart().motionX, this.getCart().motionY, this.getCart().motionZ);
		}
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotFuel(this.getCart(), slotId, 8 + x * 18, 23 + 18 * y);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ENGINES.COAL.translate(), 8, 6, 4210752);
		String strfuel = Localization.MODULES.ENGINES.NO_FUEL.translate();
		if (this.getFuelLevel() > 0) {
			strfuel = Localization.MODULES.ENGINES.FUEL.translate(String.valueOf(this.getFuelLevel()));
		}
		this.drawString(gui, strfuel, 8, 42, 4210752);
	}

	@Override
	public int numberOfGuiData() {
		return 1;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		this.updateGuiData(info, 0, (short) this.getFuelLevel());
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.setFuelLevel(data);
			if (this.getFuelLevel() < 0) {
				this.setFuelLevel(this.getFuelLevel() + 65536);
			}
		}
	}

	@Override
	public void update() {
		super.update();
		if (this.fireCoolDown <= 0) {
			this.fireIndex = this.getCart().rand.nextInt(4) + 1;
			this.fireCoolDown = 2;
		} else {
			--this.fireCoolDown;
		}
	}

	public int getFireIndex() {
		if (this.getCart().isEngineBurning()) {
			return this.fireIndex;
		}
		return 0;
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		super.Save(tagCompound, id);
		tagCompound.setShort(this.generateNBTName("Fuel", id), (short) this.getFuelLevel());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		super.Load(tagCompound, id);
		this.setFuelLevel(tagCompound.getShort(this.generateNBTName("Fuel", id)));
		if (this.getFuelLevel() < 0) {
			this.setFuelLevel(this.getFuelLevel() + 65536);
		}
	}

	public abstract double getFuelMultiplier();
}
