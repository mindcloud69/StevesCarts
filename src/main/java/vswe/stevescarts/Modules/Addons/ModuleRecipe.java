package vswe.stevescarts.Modules.Addons;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Slots.SlotBase;
import vswe.stevescarts.Slots.SlotChest;
import vswe.stevescarts.TileEntities.TileEntityCargo;

import java.util.ArrayList;

public abstract class ModuleRecipe extends ModuleAddon {
	private int target;
	protected boolean dirty;
	protected ArrayList<SlotBase> inputSlots;
	protected ArrayList<SlotBase> outputSlots;
	protected ArrayList<SlotBase> allTheSlots;
	private int maxItemCount;
	private int mode;

	public ModuleRecipe(final MinecartModular cart) {
		super(cart);
		this.maxItemCount = 1;
		this.target = 3;
		this.dirty = true;
		this.allTheSlots = new ArrayList<SlotBase>();
		this.outputSlots = new ArrayList<SlotBase>();
	}

	protected abstract int getLimitStartX();

	protected abstract int getLimitStartY();

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		if (this.canUseAdvancedFeatures()) {
			final int[] area = this.getArea();
			ResourceHelper.bindResource("/gui/recipe.png");
			this.drawImage(gui, area[0] - 2, area[1] - 2, 0, 0, 20, 20);
			if (this.mode == 1) {
				for (int i = 0; i < 3; ++i) {
					this.drawControlRect(gui, x, y, i);
				}
			} else {
				this.drawControlRect(gui, x, y, 1);
			}
		}
	}

	private void drawControlRect(final GuiMinecart gui, final int x, final int y, final int i) {
		final int v = i * 11;
		final int[] rect = this.getControlRect(i);
		this.drawImage(gui, rect, 20 + (this.inRect(x, y, rect) ? 22 : 0), v);
	}

	private int[] getControlRect(final int i) {
		return new int[] { this.getLimitStartX(), this.getLimitStartY() + 12 * i, 22, 11 };
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawForeground(final GuiMinecart gui) {
		if (this.canUseAdvancedFeatures()) {
			String str = null;
			switch (this.mode) {
				case 0: {
					str = "Inf";
					break;
				}
				case 1: {
					str = String.valueOf(this.maxItemCount);
					break;
				}
				default: {
					str = "X";
					break;
				}
			}
			this.drawString(gui, str, this.getControlRect(1), 4210752);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackgroundItems(final GuiMinecart gui, final int x, final int y) {
		if (this.canUseAdvancedFeatures()) {
			ItemStack icon;
			if (this.isTargetInvalid()) {
				icon = new ItemStack(Items.MINECART, 1);
			} else {
				icon = TileEntityCargo.itemSelections.get(this.target).getIcon();
			}
			final int[] area = this.getArea();
			this.drawItemInInterface(gui, icon, area[0], area[1]);
		}
	}

	private boolean isTargetInvalid() {
		return this.target < 0 || this.target >= TileEntityCargo.itemSelections.size() || TileEntityCargo.itemSelections.get(this.target).getValidSlot() == null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		if (this.canUseAdvancedFeatures()) {
			String str = Localization.MODULES.ADDONS.RECIPE_OUTPUT.translate() + "\n" + Localization.MODULES.ADDONS.CURRENT.translate() + ": ";
			if (this.isTargetInvalid()) {
				str += Localization.MODULES.ADDONS.INVALID_OUTPUT.translate();
			} else {
				str += TileEntityCargo.itemSelections.get(this.target).getName();
			}
			this.drawStringOnMouseOver(gui, str, x, y, this.getArea());
			for (int i = 0; i < 3; ++i) {
				if (i == 1) {
					str = Localization.MODULES.ADDONS.RECIPE_MODE.translate() + "\n" + Localization.MODULES.ADDONS.CURRENT.translate() + ": ";
					switch (this.mode) {
						case 0: {
							str += Localization.MODULES.ADDONS.RECIPE_NO_LIMIT.translate();
							break;
						}
						case 1: {
							str += Localization.MODULES.ADDONS.RECIPE_LIMIT.translate();
							break;
						}
						default: {
							str += Localization.MODULES.ADDONS.RECIPE_DISABLED.translate();
							break;
						}
					}
				} else if (this.mode != 1) {
					str = null;
				} else {
					str = Localization.MODULES.ADDONS.RECIPE_CHANGE_AMOUNT.translate((i == 0) ? "0"
					                                                                          : "1") + "\n" + Localization.MODULES.ADDONS.RECIPE_CHANGE_AMOUNT_10.translate() + "\n" + Localization.MODULES.ADDONS.RECIPE_CHANGE_AMOUNT_64.translate();
				}
				if (str != null) {
					this.drawStringOnMouseOver(gui, str, x, y, this.getControlRect(i));
				}
			}
		}
	}

	protected abstract int[] getArea();

	@Override
	public int numberOfGuiData() {
		return this.canUseAdvancedFeatures() ? 3 : 0;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		if (this.canUseAdvancedFeatures()) {
			this.updateGuiData(info, 0, (short) this.target);
			this.updateGuiData(info, 1, (short) this.mode);
			this.updateGuiData(info, 2, (short) this.maxItemCount);
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (this.canUseAdvancedFeatures()) {
			if (id == 0) {
				this.target = data;
			} else if (id == 1) {
				this.mode = data;
			} else if (id == 2) {
				this.maxItemCount = data;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (this.canUseAdvancedFeatures()) {
			if (this.inRect(x, y, this.getArea())) {
				this.sendPacket(0, (byte) button);
			}
			int i = 0;
			while (i < 3) {
				if ((this.mode == 1 || i == 1) && this.inRect(x, y, this.getControlRect(i))) {
					if (i == 1) {
						this.sendPacket(1, (byte) button);
						break;
					}
					byte encodedData = (byte) ((i != 0) ? 1 : 0);
					if (GuiScreen.isCtrlKeyDown()) {
						encodedData |= 0x2;
					} else if (GuiScreen.isShiftKeyDown()) {
						encodedData |= 0x4;
					}
					this.sendPacket(2, encodedData);
					break;
				} else {
					++i;
				}
			}
		}
	}

	@Override
	protected int numberOfPackets() {
		return this.canUseAdvancedFeatures() ? 3 : 0;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (this.canUseAdvancedFeatures()) {
			if (id == 0) {
				this.dirty = true;
				this.changeTarget(data[0] == 0);
			} else if (id == 1) {
				if (data[0] == 0) {
					if (++this.mode > 2) {
						this.mode = 0;
					}
				} else if (--this.mode < 0) {
					this.mode = 2;
				}
			} else if (id == 2) {
				int dif = ((data[0] & 0x1) == 0x0) ? 1 : -1;
				if ((data[0] & 0x2) != 0x0) {
					dif *= 64;
				} else if ((data[0] & 0x4) != 0x0) {
					dif *= 10;
				}
				this.maxItemCount = Math.min(Math.max(1, this.maxItemCount + dif), 999);
			}
		}
	}

	private void changeTarget(final boolean up) {
		if (!up) {
			if (--this.target < 0) {
				this.target = TileEntityCargo.itemSelections.size() - 1;
			}
		} else if (++this.target >= TileEntityCargo.itemSelections.size()) {
			this.target = 0;
		}
		if (this.isTargetInvalid()) {
			this.changeTarget(up);
		}
	}

	protected abstract boolean canUseAdvancedFeatures();

	protected Class getValidSlot() {
		if (this.isTargetInvalid()) {
			return null;
		}
		return TileEntityCargo.itemSelections.get(this.target).getValidSlot();
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		if (this.canUseAdvancedFeatures()) {
			this.target = tagCompound.getByte(this.generateNBTName("Target", id));
			this.mode = tagCompound.getByte(this.generateNBTName("Mode", id));
			this.maxItemCount = tagCompound.getShort(this.generateNBTName("MaxItems", id));
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		if (this.canUseAdvancedFeatures()) {
			tagCompound.setByte(this.generateNBTName("Target", id), (byte) this.target);
			tagCompound.setByte(this.generateNBTName("Mode", id), (byte) this.mode);
			tagCompound.setShort(this.generateNBTName("MaxItems", id), (short) this.maxItemCount);
		}
	}

	protected void prepareLists() {
		if (this.inputSlots == null) {
			this.inputSlots = new ArrayList<SlotBase>();
			for (final ModuleBase module : this.getCart().getModules()) {
				if (module.getSlots() != null) {
					for (final SlotBase slot : module.getSlots()) {
						if (slot instanceof SlotChest) {
							this.inputSlots.add(slot);
						}
					}
				}
			}
		}
		if (this.dirty) {
			this.allTheSlots.clear();
			this.outputSlots.clear();
			final Class validSlot = this.getValidSlot();
			for (final ModuleBase module2 : this.getCart().getModules()) {
				if (module2.getSlots() != null) {
					for (final SlotBase slot2 : module2.getSlots()) {
						if (validSlot.isInstance(slot2)) {
							this.outputSlots.add(slot2);
							this.allTheSlots.add(slot2);
						} else {
							if (!(slot2 instanceof SlotChest)) {
								continue;
							}
							this.allTheSlots.add(slot2);
						}
					}
				}
			}
			this.dirty = false;
		}
	}

	protected boolean canCraftMoreOfResult(final ItemStack result) {
		if (this.mode == 0) {
			return true;
		}
		if (this.mode == 2) {
			return false;
		}
		int count = 0;
		for (int i = 0; i < this.outputSlots.size(); ++i) {
			final ItemStack item = this.outputSlots.get(i).getStack();
			if (item != null && item.isItemEqual(result) && ItemStack.areItemStackTagsEqual(item, result)) {
				count += item.stackSize;
				if (count >= this.maxItemCount) {
					return false;
				}
			}
		}
		return true;
	}
}
