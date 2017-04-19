package vswe.stevescarts.containers.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.blocks.tileentities.TileEntityCartAssembler;
import vswe.stevescarts.modules.data.ModuleData;

import javax.annotation.Nonnull;

public class SlotAssembler extends Slot {
	private int groupID;
	private int x;
	private int y;
	private TileEntityCartAssembler assembler;
	private int openingAnimation;
	private int id;
	private boolean isValid;
	private boolean useLarge;
	private boolean reloadOnUpdate;

	public SlotAssembler(final TileEntityCartAssembler assembler, final int i, final int j, final int k, final int groupID, final boolean useLarge, final int id) {
		super(assembler, i, j, k);
		this.assembler = assembler;
		this.useLarge = useLarge;
		this.groupID = groupID;
		x = j;
		y = k;
		isValid = true;
		this.id = id;
	}

	public boolean useLargeInterface() {
		return useLarge;
	}

	@Override
	public boolean isItemValid(
		@Nonnull
			ItemStack itemstack) {
		return itemstack != null && isValid && ModuleData.isValidModuleItem(groupID, itemstack) && (!getHasStack() || (getStack().getCount() > 0 && itemstack.getCount() > 0));
	}

	public void invalidate() {
		isValid = false;
		invalidationCheck();
	}

	public void validate() {
		isValid = true;
	}

	public boolean isValid() {
		return isValid;
	}

	private void invalidationCheck() {
		xPos = -3000;
		yPos = -3000;
		if (openingAnimation > 8) {
			openingAnimation = 8;
		}
	}

	public void update() {
		if (!assembler.getWorld().isRemote) {
			if (!isValid() && getHasStack()) {
				assembler.puke(getStack());
				putStack(null);
			}
		} else if (isValid()) {
			if (openingAnimation == 8) {
				xPos = x;
				yPos = y;
				++openingAnimation;
			} else if (openingAnimation < 8) {
				++openingAnimation;
			}
		} else if (openingAnimation > 0) {
			--openingAnimation;
		} else {
			openingAnimation = id * -3;
		}
	}

	public int getAnimationTick() {
		return openingAnimation;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public TileEntityCartAssembler getAssembler() {
		return assembler;
	}

	public boolean shouldUpdatePlaceholder() {
		return true;
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		if (shouldUpdatePlaceholder()) {
			assembler.updatePlaceholder();
		} else {
			assembler.isErrorListOutdated = true;
		}
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean canTakeStack(final EntityPlayer player) {
		return !getStack().isEmpty() && getStack().getCount() > 0;
	}
}
