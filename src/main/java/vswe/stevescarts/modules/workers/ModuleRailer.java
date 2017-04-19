package vswe.stevescarts.modules.workers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotBuilder;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.modules.ISuppliesModule;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class ModuleRailer extends ModuleWorker implements ISuppliesModule {
	private boolean hasGeneratedAngles;
	private float[] railAngles;
	private DataParameter<Byte> RAILS;

	public ModuleRailer(final EntityMinecartModular cart) {
		super(cart);
		hasGeneratedAngles = false;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotBuilder(getCart(), slotId, 8 + x * 18, 23 + y * 18);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		drawString(gui, Localization.MODULES.ATTACHMENTS.RAILER.translate(), 8, 6, 4210752);
	}

	@Override
	public byte getWorkPriority() {
		return 100;
	}

	@Override
	public boolean work() {
		World world = getCart().world;
		BlockPos next = getNextblock();
		final int x = next.getX();
		final int y = next.getY();
		final int z = next.getZ();
		final ArrayList<Integer[]> pos = getValidRailPositions(x, y, z);
		if (doPreWork()) {
			boolean valid = false;
			for (int i = 0; i < pos.size(); ++i) {
				if (tryPlaceTrack(world, pos.get(i)[0], pos.get(i)[1], pos.get(i)[2], false)) {
					valid = true;
					break;
				}
			}
			if (valid) {
				startWorking(12);
			} else {
				boolean front = false;
				for (int j = 0; j < pos.size(); ++j) {
					if (BlockRailBase.isRailBlock(world, new BlockPos(pos.get(j)[0], pos.get(j)[1], pos.get(j)[2]))) {
						front = true;
						break;
					}
				}
				if (!front) {
					turnback();
				}
			}
			return true;
		}
		stopWorking();
		for (int k = 0; k < pos.size() && !tryPlaceTrack(world, pos.get(k)[0], pos.get(k)[1], pos.get(k)[2], true); ++k) {}
		return false;
	}

	protected ArrayList<Integer[]> getValidRailPositions(final int x, final int y, final int z) {
		final ArrayList<Integer[]> lst = new ArrayList<>();
		if (y >= getCart().y()) {
			lst.add(new Integer[] { x, y + 1, z });
		}
		lst.add(new Integer[] { x, y, z });
		lst.add(new Integer[] { x, y - 1, z });
		return lst;
	}

	protected boolean validRail(final Item item) {
		return Block.getBlockFromItem(item) == Blocks.RAIL;
	}

	private boolean tryPlaceTrack(World world, final int i, final int j, final int k, final boolean flag) {
		if (isValidForTrack(world, new BlockPos(i, j, k), true)) {
			for (int l = 0; l < getInventorySize(); ++l) {
				if (!getStack(l).isEmpty() && validRail(getStack(l).getItem())) {
					if (flag) {
						getCart().world.setBlockState(new BlockPos(i, j, k), Block.getBlockFromItem(getStack(l).getItem()).getStateFromMeta(getStack(l).getItemDamage()));
						if (!getCart().hasCreativeSupplies()) {
							@Nonnull
							ItemStack stack = getStack(l);
							stack.shrink(1);
							if (getStack(l).getCount() == 0) {
								setStack(l, ItemStack.EMPTY);
							}
							getCart().markDirty();
						}
					}
					return true;
				}
			}
			turnback();
			return true;
		}
		return false;
	}

	@Override
	public void initDw() {
		RAILS = createDw(DataSerializers.BYTE);
		registerDw(RAILS, (byte) 0);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		calculateRails();
	}

	private void calculateRails() {
		if (getCart().world.isRemote) {
			return;
		}
		byte valid = 0;
		for (int i = 0; i < getInventorySize(); ++i) {
			if (!getStack(i).isEmpty() && validRail(getStack(i).getItem())) {
				++valid;
			}
		}
		updateDw(RAILS, valid);
	}

	public int getRails() {
		if (isPlaceholder()) {
			return getSimInfo().getRailCount();
		}
		return getDw(RAILS);
	}

	public float getRailAngle(final int i) {
		if (!hasGeneratedAngles) {
			railAngles = new float[getInventorySize()];
			for (int j = 0; j < getInventorySize(); ++j) {
				railAngles[j] = getCart().rand.nextFloat() / 2.0f - 0.25f;
			}
			hasGeneratedAngles = true;
		}
		return railAngles[i];
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		calculateRails();
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < getInventorySize(); ++i) {
			@Nonnull
			ItemStack item = getStack(i);
			if (!item.isEmpty() && validRail(item.getItem())) {
				return true;
			}
		}
		return false;
	}
}
