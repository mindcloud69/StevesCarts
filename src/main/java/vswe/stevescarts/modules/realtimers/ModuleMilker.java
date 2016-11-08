package vswe.stevescarts.modules.realtimers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotMilker;
import vswe.stevescarts.entitys.MinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.modules.ModuleBase;

public class ModuleMilker extends ModuleBase {
	int cooldown;
	int milkbuffer;

	public ModuleMilker(final MinecartModular cart) {
		super(cart);
		this.cooldown = 0;
		this.milkbuffer = 0;
	}

	@Override
	public void update() {
		super.update();
		if (this.cooldown <= 0) {
			if (!this.getCart().worldObj.isRemote && this.getCart().hasFuel()) {
				this.generateMilk();
				this.depositeMilk();
			}
			this.cooldown = 20;
		} else {
			--this.cooldown;
		}
	}

	private void depositeMilk() {
		if (this.milkbuffer > 0) {
			final FluidStack ret = FluidContainerRegistry.getFluidForFilledItem(new ItemStack(Items.MILK_BUCKET));
			if (ret != null) {
				ret.amount = this.milkbuffer;
				this.milkbuffer -= this.getCart().fill(ret, true);
			}
			if (this.milkbuffer == 1000) {
				for (int i = 0; i < this.getInventorySize(); ++i) {
					final ItemStack bucket = this.getStack(i);
					if (bucket != null && bucket.getItem() == Items.BUCKET) {
						final ItemStack milk = new ItemStack(Items.MILK_BUCKET);
						this.getCart().addItemToChest(milk);
						if (milk.stackSize <= 0) {
							this.milkbuffer = 0;
							final ItemStack itemStack = bucket;
							if (--itemStack.stackSize <= 0) {
								this.setStack(i, null);
							}
						}
					}
				}
			}
		}
	}

	private void generateMilk() {
		if (this.milkbuffer < 1000) {
			if(!this.getCart().getPassengers().isEmpty()){
				final Entity rider = this.getCart().getPassengers().get(0);
				if (rider != null && rider instanceof EntityCow) {
					this.milkbuffer = Math.min(this.milkbuffer + 75, 1000);
				}
			}
		}
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected int getInventoryWidth() {
		return 2;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotMilker(this.getCart(), slotId, 8 + x * 18, 23 + y * 18);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setShort(this.generateNBTName("Milk", id), (short) this.milkbuffer);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.milkbuffer = tagCompound.getShort(this.generateNBTName("Milk", id));
	}
}
