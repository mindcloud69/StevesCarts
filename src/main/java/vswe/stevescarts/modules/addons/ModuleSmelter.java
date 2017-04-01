package vswe.stevescarts.modules.addons;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotCartCrafterResult;
import vswe.stevescarts.containers.slots.SlotFurnaceInput;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;

public class ModuleSmelter extends ModuleRecipe {
	private int energyBuffer;
	private int cooldown;

	public ModuleSmelter(final EntityMinecartModular cart) {
		super(cart);
		this.cooldown = 0;
	}

	@Override
	public void update() {
		if (this.getCart().world.isRemote) {
			return;
		}
		if (this.getCart().hasFuelForModule() && this.energyBuffer < 10) {
			++this.energyBuffer;
		}
		if (this.cooldown <= 0) {
			if (this.energyBuffer == 10) {
				@Nonnull ItemStack recipe = this.getStack(0);
				ItemStack result = null;
				if (recipe != null) {
					result = FurnaceRecipes.instance().getSmeltingResult(recipe);
				}
				if (result != null) {
					result = result.copy();
				}
				if (result != null && this.getCart().getModules() != null) {
					this.prepareLists();
					if (this.canCraftMoreOfResult(result)) {
						final ArrayList<ItemStack> originals = new ArrayList<>();
						for (int i = 0; i < this.allTheSlots.size(); ++i) {
							@Nonnull ItemStack item = this.allTheSlots.get(i).getStack();
							originals.add((item == null) ? null : item.copy());
						}
						int i = 0;
						while (i < this.inputSlots.size()) {
							@Nonnull ItemStack item = this.inputSlots.get(i).getStack();
							if (item != null && item.isItemEqual(recipe) && ItemStack.areItemStackTagsEqual(item, recipe)) {
								@Nonnull ItemStack itemStack = item;
								if (--itemStack.stackSize <= 0) {
									this.inputSlots.get(i).putStack(null);
								}
								this.getCart().addItemToChest(result, this.getValidSlot(), null);
								if (result.stackSize != 0) {
									for (int j = 0; j < this.allTheSlots.size(); ++j) {
										this.allTheSlots.get(j).putStack(originals.get(j));
									}
									break;
								}
								this.energyBuffer = 0;
								break;
							} else {
								++i;
							}
						}
					}
				}
			}
			this.cooldown = 40;
		} else {
			--this.cooldown;
		}
	}

	@Override
	public int getConsumption(final boolean isMoving) {
		if (this.energyBuffer < 10) {
			return 15;
		}
		return super.getConsumption(isMoving);
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected int getInventoryWidth() {
		return 1;
	}

	@Override
	protected int getInventoryHeight() {
		return 2;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		if (y == 0) {
			return new SlotFurnaceInput(this.getCart(), slotId, 10 + 18 * x, 15 + 18 * y);
		}
		return new SlotCartCrafterResult(this.getCart(), slotId, 10 + 18 * x, 15 + 18 * y);
	}

	@Override
	public int numberOfGuiData() {
		return super.numberOfGuiData() + 1;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		super.checkGuiData(info);
		this.updateGuiData(info, super.numberOfGuiData() + 0, (short) this.energyBuffer);
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		super.receiveGuiData(id, data);
		if (id == super.numberOfGuiData() + 0) {
			this.energyBuffer = data;
		}
	}

	@Override
	public void onInventoryChanged() {
		if (this.getCart().world.isRemote) {
			if (this.getStack(0) != null) {
				this.setStack(1, FurnaceRecipes.instance().getSmeltingResult(this.getStack(0)));
			} else {
				this.setStack(1, null);
			}
		}
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		super.drawForeground(gui);
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	@Override
	public int guiWidth() {
		return this.canUseAdvancedFeatures() ? 100 : 45;
	}

	@Override
	protected int[] getArea() {
		return new int[] { 32, 25, 16, 16 };
	}

	@Override
	protected boolean canUseAdvancedFeatures() {
		return false;
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		super.Load(tagCompound, id);
		this.energyBuffer = tagCompound.getByte(this.generateNBTName("Buffer", id));
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		super.Save(tagCompound, id);
		tagCompound.setByte(this.generateNBTName("Buffer", id), (byte) this.energyBuffer);
	}

	@Override
	protected int getLimitStartX() {
		return 55;
	}

	@Override
	protected int getLimitStartY() {
		return 15;
	}
}
