package vswe.stevescarts.modules.workers.tools;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotRepair;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.EnchantmentInfo;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.addons.ModuleEnchants;
import vswe.stevescarts.modules.workers.ModuleWorker;

public abstract class ModuleTool extends ModuleWorker {
	private int currentDurability;
	private int remainingRepairUnits;
	private int maximumRepairUnits;
	protected ModuleEnchants enchanter;
	private int[] durabilityRect;

	public ModuleTool(final EntityMinecartModular cart) {
		super(cart);
		this.maximumRepairUnits = 1;
		this.durabilityRect = new int[] { 10, 15, 52, 8 };
		this.currentDurability = this.getMaxDurability();
	}

	public abstract int getMaxDurability();

	public abstract String getRepairItemName();

	public abstract int getRepairItemUnits(final ItemStack p0);

	public abstract int getRepairSpeed();

	public abstract boolean useDurability();

	@Override
	public void init() {
		super.init();
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module instanceof ModuleEnchants) {
				(this.enchanter = (ModuleEnchants) module).addType(EnchantmentInfo.ENCHANTMENT_TYPE.TOOL);
				break;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/tool.png");
		this.drawBox(gui, 0, 0, 1.0f);
		this.drawBox(gui, 0, 8, this.useDurability() ? (this.currentDurability / this.getMaxDurability()) : 1.0f);
		this.drawBox(gui, 0, 16, this.remainingRepairUnits / this.maximumRepairUnits);
		if (this.inRect(x, y, this.durabilityRect)) {
			this.drawBox(gui, 0, 24, 1.0f);
		}
	}

	private void drawBox(final GuiMinecart gui, final int u, final int v, final float mult) {
		final int w = (int) (this.durabilityRect[2] * mult);
		if (w > 0) {
			this.drawImage(gui, this.durabilityRect[0], this.durabilityRect[1], u, v, w, this.durabilityRect[3]);
		}
	}

	public boolean isValidRepairMaterial(final ItemStack item) {
		return this.getRepairItemUnits(item) > 0;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotRepair(this, this.getCart(), slotId, 76, 8);
	}

	@Override
	protected int getInventoryWidth() {
		return 1;
	}

	@Override
	public int guiWidth() {
		return 100;
	}

	@Override
	public int guiHeight() {
		return 50;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		String str;
		if (this.useDurability()) {
			str = Localization.MODULES.TOOLS.DURABILITY.translate() + ": " + this.currentDurability + "/" + this.getMaxDurability();
			if (this.isBroken()) {
				str = str + " [" + Localization.MODULES.TOOLS.BROKEN.translate() + "]";
			} else {
				str = str + " [" + 100 * this.currentDurability / this.getMaxDurability() + "%]";
			}
			str += "\n";
			if (this.isRepairing()) {
				if (this.isActuallyRepairing()) {
					str = str + " [" + this.getRepairPercentage() + "%]";
				} else {
					str += Localization.MODULES.TOOLS.DECENT.translate();
				}
			} else {
				str += Localization.MODULES.TOOLS.INSTRUCTION.translate(this.getRepairItemName());
			}
		} else {
			str = Localization.MODULES.TOOLS.UNBREAKABLE.translate();
			if (this.isRepairing() && !this.isActuallyRepairing()) {
				str = str + " " + Localization.MODULES.TOOLS.UNBREAKABLE_REPAIR.translate();
			}
		}
		this.drawStringOnMouseOver(gui, str, x, y, this.durabilityRect);
	}

	@Override
	public void update() {
		super.update();
		if (!this.getCart().worldObj.isRemote && this.useDurability()) {
			if (this.isActuallyRepairing()) {
				final int dif = 1;
				this.remainingRepairUnits -= dif;
				this.currentDurability += dif * this.getRepairSpeed();
				if (this.currentDurability > this.getMaxDurability()) {
					this.currentDurability = this.getMaxDurability();
				}
			}
			if (!this.isActuallyRepairing()) {
				final int units = this.getRepairItemUnits(this.getStack(0));
				if (units > 0 && units <= this.getMaxDurability() - this.currentDurability) {
					final int n = units / this.getRepairSpeed();
					this.remainingRepairUnits = n;
					this.maximumRepairUnits = n;
					final ItemStack stack = this.getStack(0);
					--stack.stackSize;
					if (this.getStack(0).stackSize <= 0) {
						this.setStack(0, null);
					}
				}
			}
		}
	}

	@Override
	public boolean stopEngines() {
		return this.isRepairing();
	}

	public boolean isRepairing() {
		return this.getStack(0) != null || this.isActuallyRepairing();
	}

	public boolean isActuallyRepairing() {
		return this.remainingRepairUnits > 0;
	}

	public boolean isBroken() {
		return this.currentDurability == 0 && this.useDurability();
	}

	public void damageTool(final int val) {
		final int unbreaking = (this.enchanter != null) ? this.enchanter.getUnbreakingLevel() : 0;
		if (this.getCart().rand.nextInt(100) < 100 / (unbreaking + 1)) {
			this.currentDurability -= val;
			if (this.currentDurability < 0) {
				this.currentDurability = 0;
			}
		}
		if (this.enchanter != null) {
			this.enchanter.damageEnchant(EnchantmentInfo.ENCHANTMENT_TYPE.TOOL, val);
		}
	}

	@Override
	public int numberOfGuiData() {
		return 4;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		this.updateGuiData(info, 0, (short) (this.currentDurability & 0xFFFF));
		this.updateGuiData(info, 1, (short) (this.currentDurability >> 16 & 0xFFFF));
		this.updateGuiData(info, 2, (short) this.remainingRepairUnits);
		this.updateGuiData(info, 3, (short) this.maximumRepairUnits);
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		int dataint = data;
		if (dataint < 0) {
			dataint += 65536;
		}
		if (id == 0) {
			this.currentDurability = ((this.currentDurability & 0xFFFF0000) | dataint);
		} else if (id == 1) {
			this.currentDurability = ((this.currentDurability & 0xFFFF) | dataint << 16);
		} else if (id == 2) {
			this.remainingRepairUnits = data;
		} else if (id == 3) {
			this.maximumRepairUnits = data;
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setInteger(this.generateNBTName("Durability", id), this.currentDurability);
		tagCompound.setShort(this.generateNBTName("Repair", id), (short) this.remainingRepairUnits);
		tagCompound.setShort(this.generateNBTName("MaxRepair", id), (short) this.maximumRepairUnits);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.currentDurability = tagCompound.getInteger(this.generateNBTName("Durability", id));
		this.remainingRepairUnits = tagCompound.getShort(this.generateNBTName("Repair", id));
		this.maximumRepairUnits = tagCompound.getShort(this.generateNBTName("MaxRepair", id));
	}

	@Override
	public boolean hasExtraData() {
		return true;
	}

	@Override
	public byte getExtraData() {
		return (byte) (100 * this.currentDurability / this.getMaxDurability());
	}

	@Override
	public void setExtraData(final byte b) {
		this.currentDurability = b * this.getMaxDurability() / 100;
	}

	public boolean shouldSilkTouch(IBlockState blockState, BlockPos pos) {
		final boolean doSilkTouch = false;
		try {
			if (this.enchanter != null && this.enchanter.useSilkTouch() && blockState.getBlock().canSilkHarvest(this.getCart().worldObj, pos, blockState, null)) {
				return true;
			}
		} catch (Exception ex) {}
		return false;
	}

	public ItemStack getSilkTouchedItem(IBlockState blockState) {
		Block block = blockState.getBlock();
		ItemStack stack = new ItemStack(block, 1, 0);
		if (stack.getItem() != null && stack.getItem().getHasSubtypes()) {
			return new ItemStack(block, 1, block.getMetaFromState(blockState));
		}
		return stack;
	}

	public int getCurrentDurability() {
		return this.currentDurability;
	}

	public int getRepairPercentage() {
		return 100 - 100 * this.remainingRepairUnits / this.maximumRepairUnits;
	}
}
