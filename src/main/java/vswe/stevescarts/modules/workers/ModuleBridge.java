package vswe.stevescarts.modules.workers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockRailBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotBridge;
import vswe.stevescarts.entitys.MinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.modules.ISuppliesModule;

public class ModuleBridge extends ModuleWorker implements ISuppliesModule {
	private static DataParameter<Boolean> BRIDGE = createDw(DataSerializers.BOOLEAN);

	public ModuleBridge(final MinecartModular cart) {
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
		return new SlotBridge(this.getCart(), slotId, 8 + x * 18, 23 + y * 18);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	@Override
	public byte getWorkPriority() {
		return 98;
	}

	@Override
	public boolean work() {
		BlockPos next = this.getNextblock();
		if (this.getCart().getYTarget() < next.getY()) {
			next = next.down(2);
		} else {
			next = next.down(1);
		}
		if (!BlockRailBase.isRailBlock(this.getCart().worldObj, next) && !BlockRailBase.isRailBlock(this.getCart().worldObj, next.down())) {
			if (this.doPreWork()) {
				if (this.tryBuildBridge(next, false)) {
					this.startWorking(22);
					this.setBridge(true);
					return true;
				}
			} else if (this.tryBuildBridge(next, true)) {
				this.stopWorking();
			}
		}
		this.setBridge(false);
		return false;
	}

	private boolean tryBuildBridge(BlockPos pos, final boolean flag) {
		final Block b = this.getCart().worldObj.getBlockState(pos).getBlock();
		if ((this.countsAsAir(pos) || b instanceof BlockLiquid) && this.isValidForTrack(pos.up(), false)) {
			for (int m = 0; m < this.getInventorySize(); ++m) {
				if (this.getStack(m) != null && SlotBridge.isBridgeMaterial(this.getStack(m))) {
					if (flag) {
						this.getCart().worldObj.setBlockState(pos, Block.getBlockFromItem(this.getStack(m).getItem()).getStateFromMeta(getStack(m).getItemDamage()), 3);
						if (!this.getCart().hasCreativeSupplies()) {
							final ItemStack stack = this.getStack(m);
							--stack.stackSize;
							if (this.getStack(m).stackSize == 0) {
								this.setStack(m, null);
							}
							this.getCart().markDirty();
						}
					}
					return true;
				}
			}
			if (this.isValidForTrack(pos, true) || this.isValidForTrack(pos.up(), true) || !this.isValidForTrack(pos.up(2), true)) {}
		}
		return false;
	}

	@Override
	public void initDw() {
		registerDw(BRIDGE, false);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	private void setBridge(final boolean val) {
		this.updateDw(BRIDGE, val);
	}

	public boolean needBridge() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getNeedBridge();
		}
		return this.getDw(BRIDGE);
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < this.getInventorySize(); ++i) {
			final ItemStack item = this.getStack(i);
			if (item != null && SlotBridge.isBridgeMaterial(item)) {
				return true;
			}
		}
		return false;
	}
}
