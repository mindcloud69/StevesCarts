package vswe.stevescarts.Modules.Workers;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.ISuppliesModule;
import vswe.stevescarts.Slots.SlotBase;
import vswe.stevescarts.Slots.SlotBuilder;

public class ModuleRailer extends ModuleWorker implements ISuppliesModule {
	private boolean hasGeneratedAngles;
	private float[] railAngles;
	private static DataParameter<Byte> RAILS = createDw(DataSerializers.BYTE);

	public ModuleRailer(final MinecartModular cart) {
		super(cart);
		this.hasGeneratedAngles = false;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotBuilder(this.getCart(), slotId, 8 + x * 18, 23 + y * 18);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ATTACHMENTS.RAILER.translate(), 8, 6, 4210752);
	}

	@Override
	public byte getWorkPriority() {
		return 100;
	}

	@Override
	public boolean work() {
		BlockPos next = this.getNextblock();
		final int x = next.getX();
		final int y = next.getY();
		final int z = next.getZ();
		final ArrayList<Integer[]> pos = this.getValidRailPositions(x, y, z);
		if (this.doPreWork()) {
			boolean valid = false;
			for (int i = 0; i < pos.size(); ++i) {
				if (this.tryPlaceTrack(pos.get(i)[0], pos.get(i)[1], pos.get(i)[2], false)) {
					valid = true;
					break;
				}
			}
			if (valid) {
				this.startWorking(12);
			} else {
				boolean front = false;
				for (int j = 0; j < pos.size(); ++j) {
					if (BlockRailBase.isRailBlock(this.getCart().worldObj, new BlockPos(pos.get(j)[0], pos.get(j)[1], pos.get(j)[2]))) {
						front = true;
						break;
					}
				}
				if (!front) {
					this.turnback();
				}
			}
			return true;
		}
		this.stopWorking();
		for (int k = 0; k < pos.size() && !this.tryPlaceTrack(pos.get(k)[0], pos.get(k)[1], pos.get(k)[2], true); ++k) {}
		return false;
	}

	protected ArrayList<Integer[]> getValidRailPositions(final int x, final int y, final int z) {
		final ArrayList<Integer[]> lst = new ArrayList<Integer[]>();
		if (y >= this.getCart().y()) {
			lst.add(new Integer[] { x, y + 1, z });
		}
		lst.add(new Integer[] { x, y, z });
		lst.add(new Integer[] { x, y - 1, z });
		return lst;
	}

	protected boolean validRail(final Item item) {
		return Block.getBlockFromItem(item) == Blocks.RAIL;
	}

	private boolean tryPlaceTrack(final int i, final int j, final int k, final boolean flag) {
		if (this.isValidForTrack(new BlockPos(i, j, k), true)) {
			for (int l = 0; l < this.getInventorySize(); ++l) {
				if (this.getStack(l) != null && this.validRail(this.getStack(l).getItem())) {
					if (flag) {
						this.getCart().worldObj.setBlockState(new BlockPos(i, j, k), Block.getBlockFromItem(this.getStack(l).getItem()).getStateFromMeta(getStack(l).getItemDamage()));
						if (!this.getCart().hasCreativeSupplies()) {
							final ItemStack stack = this.getStack(l);
							--stack.stackSize;
							if (this.getStack(l).stackSize == 0) {
								this.setStack(l, null);
							}
							this.getCart().markDirty();
						}
					}
					return true;
				}
			}
			this.turnback();
			return true;
		}
		return false;
	}

	@Override
	public void initDw() {
		registerDw(RAILS, (byte)0);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		this.calculateRails();
	}

	private void calculateRails() {
		if (this.getCart().worldObj.isRemote) {
			return;
		}
		byte valid = 0;
		for (int i = 0; i < this.getInventorySize(); ++i) {
			if (this.getStack(i) != null && this.validRail(this.getStack(i).getItem())) {
				++valid;
			}
		}
		this.updateDw(RAILS, valid);
	}

	public int getRails() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getRailCount();
		}
		return this.getDw(RAILS);
	}

	public float getRailAngle(final int i) {
		if (!this.hasGeneratedAngles) {
			this.railAngles = new float[this.getInventorySize()];
			for (int j = 0; j < this.getInventorySize(); ++j) {
				this.railAngles[j] = this.getCart().rand.nextFloat() / 2.0f - 0.25f;
			}
			this.hasGeneratedAngles = true;
		}
		return this.railAngles[i];
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.calculateRails();
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < this.getInventorySize(); ++i) {
			final ItemStack item = this.getStack(i);
			if (item != null && this.validRail(item.getItem())) {
				return true;
			}
		}
		return false;
	}
}
