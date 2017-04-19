package vswe.stevescarts.modules.addons;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotCartCrafterResult;
import vswe.stevescarts.containers.slots.SlotFurnaceInput;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;

import javax.annotation.Nonnull;

public class ModuleSmelter extends ModuleRecipe {
	private int energyBuffer;
	private int cooldown;

	public ModuleSmelter(final EntityMinecartModular cart) {
		super(cart);
		cooldown = 0;
	}

	@Override
	public void update() {
		if (getCart().world.isRemote) {
			return;
		}
		if (getCart().hasFuelForModule() && energyBuffer < 10) {
			++energyBuffer;
		}
		if (cooldown <= 0) {
			if (energyBuffer == 10) {
				@Nonnull
				ItemStack recipe = getStack(0);
				@Nonnull
				ItemStack result = ItemStack.EMPTY;
				if (!recipe.isEmpty()) {
					result = FurnaceRecipes.instance().getSmeltingResult(recipe);
				}
				if (!result.isEmpty()) {
					result = result.copy();
				}
				if (!result.isEmpty() && getCart().getModules() != null) {
					prepareLists();
					if (canCraftMoreOfResult(result)) {
						final NonNullList<ItemStack> originals = NonNullList.create();
						for (int i = 0; i < allTheSlots.size(); ++i) {
							@Nonnull
							ItemStack item = allTheSlots.get(i).getStack();
							originals.add((item.isEmpty()) ? ItemStack.EMPTY : item.copy());
						}
						int i = 0;
						while (i < inputSlots.size()) {
							@Nonnull
							ItemStack item = inputSlots.get(i).getStack();
							if (!item.isEmpty() && item.isItemEqual(recipe) && ItemStack.areItemStackTagsEqual(item, recipe)) {
								@Nonnull
								ItemStack itemStack = item;
								itemStack.shrink(1);
								if (itemStack.getCount() <= 0) {
									inputSlots.get(i).putStack(ItemStack.EMPTY);
								}
								getCart().addItemToChest(result, getValidSlot(), null);
								if (result.getCount() != 0) {
									for (int j = 0; j < allTheSlots.size(); ++j) {
										allTheSlots.get(j).putStack(originals.get(j));
									}
									break;
								}
								energyBuffer = 0;
								break;
							} else {
								++i;
							}
						}
					}
				}
			}
			cooldown = 40;
		} else {
			--cooldown;
		}
	}

	@Override
	public int getConsumption(final boolean isMoving) {
		if (energyBuffer < 10) {
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
			return new SlotFurnaceInput(getCart(), slotId, 10 + 18 * x, 15 + 18 * y);
		}
		return new SlotCartCrafterResult(getCart(), slotId, 10 + 18 * x, 15 + 18 * y);
	}

	@Override
	public int numberOfGuiData() {
		return super.numberOfGuiData() + 1;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		super.checkGuiData(info);
		updateGuiData(info, super.numberOfGuiData() + 0, (short) energyBuffer);
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		super.receiveGuiData(id, data);
		if (id == super.numberOfGuiData() + 0) {
			energyBuffer = data;
		}
	}

	@Override
	public void onInventoryChanged() {
		if (getCart().world.isRemote) {
			if (getStack(0) != null) {
				setStack(1, FurnaceRecipes.instance().getSmeltingResult(getStack(0)));
			} else {
				setStack(1, null);
			}
		}
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		super.drawForeground(gui);
		drawString(gui, getModuleName(), 8, 6, 4210752);
	}

	@Override
	public int guiWidth() {
		return canUseAdvancedFeatures() ? 100 : 45;
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
		energyBuffer = tagCompound.getByte(generateNBTName("Buffer", id));
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		super.Save(tagCompound, id);
		tagCompound.setByte(generateNBTName("Buffer", id), (byte) energyBuffer);
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
