package vswe.stevescarts.Modules.Workers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockRailBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.ISuppliesModule;
import vswe.stevescarts.Slots.SlotBase;
import vswe.stevescarts.Slots.SlotBridge;

public class ModuleBridge extends ModuleWorker implements ISuppliesModule {
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
		this.addDw(0, 0);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	private void setBridge(final boolean val) {
		this.updateDw(0, val ? 1 : 0);
	}

	public boolean needBridge() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getNeedBridge();
		}
		return this.getDw(0) != 0;
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
