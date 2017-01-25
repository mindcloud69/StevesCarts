package vswe.stevescarts.modules.addons;

import java.util.ArrayList;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotEnchantment;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.EnchantmentData;
import vswe.stevescarts.helpers.EnchantmentInfo;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;

public class ModuleEnchants extends ModuleAddon {
	private EnchantmentData[] enchants;
	private ArrayList<EnchantmentInfo.ENCHANTMENT_TYPE> enabledTypes;

	public ModuleEnchants(final EntityMinecartModular cart) {
		super(cart);
		this.enchants = new EnchantmentData[3];
		this.enabledTypes = new ArrayList<>();
	}

	public int getFortuneLevel() {
		if (this.useSilkTouch()) {
			return 0;
		}
		return this.getEnchantLevel(EnchantmentInfo.fortune);
	}

	public boolean useSilkTouch() {
		return false;
	}

	public int getUnbreakingLevel() {
		return this.getEnchantLevel(EnchantmentInfo.unbreaking);
	}

	public int getEfficiencyLevel() {
		return this.getEnchantLevel(EnchantmentInfo.efficiency);
	}

	public int getPowerLevel() {
		return this.getEnchantLevel(EnchantmentInfo.power);
	}

	public int getPunchLevel() {
		return this.getEnchantLevel(EnchantmentInfo.punch);
	}

	public boolean useFlame() {
		return this.getEnchantLevel(EnchantmentInfo.flame) > 0;
	}

	public boolean useInfinity() {
		return this.getEnchantLevel(EnchantmentInfo.infinity) > 0;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	@Override
	protected int getInventoryWidth() {
		return 1;
	}

	@Override
	protected int getInventoryHeight() {
		return 3;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotEnchantment(this.getCart(), this.enabledTypes, slotId, 8, 14 + y * 20);
	}

	@Override
	public void update() {
		super.update();
		if (!this.getCart().world.isRemote) {
			for (int i = 0; i < 3; ++i) {
				if (this.getStack(i) != null && this.getStack(i).stackSize > 0) {
					final int stacksize = this.getStack(i).stackSize;
					this.enchants[i] = EnchantmentInfo.addBook(this.enabledTypes, this.enchants[i], this.getStack(i));
					if (this.getStack(i).stackSize != stacksize) {
						boolean valid = true;
						for (int j = 0; j < 3; ++j) {
							if (i != j && this.enchants[i] != null && this.enchants[j] != null && this.enchants[i].getEnchantment() == this.enchants[j].getEnchantment()) {
								this.enchants[i] = null;
								final ItemStack stack = this.getStack(i);
								++stack.stackSize;
								valid = false;
								break;
							}
						}
						if (valid && this.getStack(i).stackSize <= 0) {
							this.setStack(i, null);
						}
					}
				}
			}
		}
	}

	public void damageEnchant(final EnchantmentInfo.ENCHANTMENT_TYPE type, final int dmg) {
		for (int i = 0; i < 3; ++i) {
			if (this.enchants[i] != null && this.enchants[i].getEnchantment().getType() == type) {
				this.enchants[i].damageEnchant(dmg);
				if (this.enchants[i].getValue() <= 0) {
					this.enchants[i] = null;
				}
			}
		}
	}

	private int getEnchantLevel(final EnchantmentInfo info) {
		if (info != null) {
			for (int i = 0; i < 3; ++i) {
				if (this.enchants[i] != null && this.enchants[i].getEnchantment() == info) {
					return this.enchants[i].getLevel();
				}
			}
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/enchant.png");
		for (int i = 0; i < 3; ++i) {
			final int[] box = this.getBoxRect(i);
			if (this.inRect(x, y, box)) {
				this.drawImage(gui, box, 65, 0);
			} else {
				this.drawImage(gui, box, 0, 0);
			}
			final EnchantmentData data = this.enchants[i];
			if (data != null) {
				final int maxlevel = data.getEnchantment().getEnchantment().getMaxLevel();
				int value = data.getValue();
				for (int j = 0; j < maxlevel; ++j) {
					final int[] bar = this.getBarRect(i, j, maxlevel);
					if (j != maxlevel - 1) {
						this.drawImage(gui, bar[0] + bar[2], bar[1], 61 + j, 1, 1, bar[3]);
					}
					final int levelmaxvalue = data.getEnchantment().getValue(j + 1);
					if (value > 0) {
						float mult = value / levelmaxvalue;
						if (mult > 1.0f) {
							mult = 1.0f;
						}
						final int[] array = bar;
						final int n = 2;
						array[n] *= (int) mult;
						this.drawImage(gui, bar, 1, 13 + 11 * j);
					}
					value -= levelmaxvalue;
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		for (int i = 0; i < 3; ++i) {
			final EnchantmentData data = this.enchants[i];
			String str;
			if (data != null) {
				str = data.getInfoText();
			} else {
				str = Localization.MODULES.ADDONS.ENCHANT_INSTRUCTION.translate();
			}
			this.drawStringOnMouseOver(gui, str, x, y, this.getBoxRect(i));
		}
	}

	private int[] getBoxRect(final int id) {
		return new int[] { 40, 17 + id * 20, 61, 12 };
	}

	private int[] getBarRect(final int id, final int barid, final int maxlevel) {
		final int width = (59 - (maxlevel - 1)) / maxlevel;
		return new int[] { 41 + (width + 1) * barid, 18 + id * 20, width, 10 };
	}

	@Override
	public int numberOfGuiData() {
		return 9;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		for (int i = 0; i < 3; ++i) {
			final EnchantmentData data = this.enchants[i];
			if (data == null) {
				this.updateGuiData(info, i * 3 + 0, (short) (-1));
			} else {
				this.updateGuiData(info, i * 3 + 0, (short) Enchantment.getEnchantmentID(data.getEnchantment().getEnchantment()));
				this.updateGuiData(info, i * 3 + 1, (short) (data.getValue() & 0xFFFF));
				this.updateGuiData(info, i * 3 + 2, (short) (data.getValue() >> 16 & 0xFFFF));
			}
		}
	}

	@Override
	public void receiveGuiData(int id, final short data) {
		int dataint = data;
		if (dataint < 0) {
			dataint += 65536;
		}
		final int enchantId = id / 3;
		id %= 3;
		if (id == 0) {
			if (data == -1) {
				this.enchants[enchantId] = null;
			} else {
				this.enchants[enchantId] = EnchantmentInfo.createDataFromEffectId(this.enchants[enchantId], data);
			}
		} else if (this.enchants[enchantId] != null) {
			if (id == 1) {
				this.enchants[enchantId].setValue((this.enchants[enchantId].getValue() & 0xFFFF0000) | dataint);
			} else if (id == 2) {
				this.enchants[enchantId].setValue((this.enchants[enchantId].getValue() & 0xFFFF) | dataint << 16);
			}
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		super.Save(tagCompound, id);
		for (int i = 0; i < 3; ++i) {
			if (this.enchants[i] == null) {
				tagCompound.setShort(this.generateNBTName("EffectId" + i, id), (short) (-1));
			} else {
				tagCompound.setShort(this.generateNBTName("EffectId" + i, id), (short) Enchantment.getEnchantmentID(this.enchants[i].getEnchantment().getEnchantment()));
				tagCompound.setInteger(this.generateNBTName("Value" + i, id), this.enchants[i].getValue());
			}
		}
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		super.Load(tagCompound, id);
		for (int i = 0; i < 3; ++i) {
			final short effect = tagCompound.getShort(this.generateNBTName("EffectId" + i, id));
			if (effect == -1) {
				this.enchants[i] = null;
			} else {
				this.enchants[i] = EnchantmentInfo.createDataFromEffectId(this.enchants[i], effect);
				if (this.enchants[i] != null) {
					this.enchants[i].setValue(tagCompound.getInteger(this.generateNBTName("Value" + i, id)));
				}
			}
		}
	}

	@Override
	public int guiWidth() {
		return 110;
	}

	public void addType(final EnchantmentInfo.ENCHANTMENT_TYPE type) {
		this.enabledTypes.add(type);
	}
}
