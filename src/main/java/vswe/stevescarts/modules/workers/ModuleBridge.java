package vswe.stevescarts.modules.workers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockRailBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotBridge;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.modules.ISuppliesModule;

import javax.annotation.Nonnull;

public class ModuleBridge extends ModuleWorker implements ISuppliesModule {
	private DataParameter<Boolean> BRIDGE;

	public ModuleBridge(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public int guiWidth() {
		return 80;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotBridge(getCart(), slotId, 8 + x * 18, 23 + y * 18);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		drawString(gui, getModuleName(), 8, 6, 4210752);
	}

	@Override
	public byte getWorkPriority() {
		return 98;
	}

	@Override
	public boolean work() {
		World world = getCart().world;
		BlockPos next = getNextblock();
		if (getCart().getYTarget() < next.getY()) {
			next = next.down(2);
		} else {
			next = next.down(1);
		}
		if (!BlockRailBase.isRailBlock(world, next) && !BlockRailBase.isRailBlock(world, next.down())) {
			if (doPreWork()) {
				if (tryBuildBridge(world, next, false)) {
					startWorking(22);
					setBridge(true);
					return true;
				}
			} else if (tryBuildBridge(world, next, true)) {
				stopWorking();
			}
		}
		setBridge(false);
		return false;
	}

	private boolean tryBuildBridge(World world, BlockPos pos, final boolean flag) {
		final Block b = world.getBlockState(pos).getBlock();
		if ((countsAsAir(pos) || b instanceof BlockLiquid) && isValidForTrack(world, pos.up(), false)) {
			for (int m = 0; m < getInventorySize(); ++m) {
				if (!getStack(m).isEmpty() && SlotBridge.isBridgeMaterial(getStack(m))) {
					if (flag) {
						world.setBlockState(pos, Block.getBlockFromItem(getStack(m).getItem()).getStateFromMeta(getStack(m).getItemDamage()), 3);
						if (!getCart().hasCreativeSupplies()) {
							@Nonnull
							ItemStack stack = getStack(m);
							stack.shrink(1);
							if (getStack(m).getCount() == 0) {
								setStack(m, ItemStack.EMPTY);
							}
							getCart().markDirty();
						}
					}
					return true;
				}
			}
			if (isValidForTrack(world, pos, true) || isValidForTrack(world, pos.up(), true) || !isValidForTrack(world, pos.up(2), true)) {
			}
		}
		return false;
	}

	@Override
	public void initDw() {
		BRIDGE = createDw(DataSerializers.BOOLEAN);
		registerDw(BRIDGE, false);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	private void setBridge(final boolean val) {
		updateDw(BRIDGE, val);
	}

	public boolean needBridge() {
		if (isPlaceholder()) {
			return getSimInfo().getNeedBridge();
		}
		return getDw(BRIDGE);
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < getInventorySize(); ++i) {
			@Nonnull
			ItemStack item = getStack(i);
			if (!item.isEmpty() && SlotBridge.isBridgeMaterial(item)) {
				return true;
			}
		}
		return false;
	}
}
