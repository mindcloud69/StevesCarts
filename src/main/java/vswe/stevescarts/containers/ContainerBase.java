package vswe.stevescarts.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.blocks.tileentities.TileEntityBase;
import vswe.stevescarts.helpers.storages.TransferHandler;

import javax.annotation.Nonnull;

public abstract class ContainerBase extends Container {
	public abstract IInventory getMyInventory();

	public abstract TileEntityBase getTileEntity();

	@Override
	@Nonnull
	public ItemStack transferStackInSlot(final EntityPlayer player, final int i) {
		if (this.getMyInventory() == null) {
			return ItemStack.EMPTY;
		}
		ItemStack itemstack = ItemStack.EMPTY;
		final Slot slot = this.inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			@Nonnull
			ItemStack itemstack2 = slot.getStack();
			itemstack = itemstack2.copy();
			if (i < this.getMyInventory().getSizeInventory()) {
				if (!this.mergeItemStack(itemstack2, this.getMyInventory().getSizeInventory() + 28, this.getMyInventory().getSizeInventory() + 36, false) && !this.mergeItemStack(itemstack2, this.getMyInventory().getSizeInventory(), this.getMyInventory().getSizeInventory() + 28, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack2, 0, this.getMyInventory().getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}
			if (itemstack2.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (itemstack2.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, itemstack2);
		}
		return itemstack;
	}

	@Override
	protected boolean mergeItemStack(
		@Nonnull
			ItemStack par1ItemStack, final int par2, final int par3, final boolean par4) {
		if (this.getMyInventory() == null) {
			return false;
		}
		boolean var5 = false;
		int var6 = par2;
		if (par4) {
			var6 = par3 - 1;
		}
		if (par1ItemStack.isStackable()) {
			while (par1ItemStack.getCount() > 0 && ((!par4 && var6 < par3) || (par4 && var6 >= par2))) {
				final Slot var7 = this.inventorySlots.get(var6);
				@Nonnull
				ItemStack var8 = var7.getStack();
				if (!var8.isEmpty() && var8.getCount() > 0 && var8.getItem() == par1ItemStack.getItem() && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == var8.getItemDamage()) && ItemStack.areItemStackTagsEqual(par1ItemStack, var8)) {
					final int var9 = var8.getCount() + par1ItemStack.getCount();
					final int maxLimit = Math.min(par1ItemStack.getMaxStackSize(), var7.getSlotStackLimit());
					if (var9 <= maxLimit) {
						par1ItemStack.setCount(0);
						var8.setCount(var9);
						var7.onSlotChanged();
						var5 = true;
					} else if (var8.getCount() < maxLimit) {
						par1ItemStack.shrink(maxLimit - var8.getCount());
						var8.setCount(maxLimit);
						var7.onSlotChanged();
						var5 = true;
					}
				}
				if (par4) {
					--var6;
				} else {
					++var6;
				}
			}
		}
		if (par1ItemStack.getCount() > 0) {
			if (par4) {
				var6 = par3 - 1;
			} else {
				var6 = par2;
			}
			while ((!par4 && var6 < par3) || (par4 && var6 >= par2)) {
				final Slot var7 = this.inventorySlots.get(var6);
				@Nonnull
				ItemStack var8 = var7.getStack();
				if (var8.isEmpty() && TransferHandler.isItemValidForTransfer(var7, par1ItemStack, TransferHandler.TRANSFER_TYPE.SHIFT)) {
					final int stackSize = Math.min(var7.getSlotStackLimit(), par1ItemStack.getCount());
					@Nonnull
					ItemStack newItem = par1ItemStack.copy();
					newItem.setCount(stackSize);
					par1ItemStack.shrink(stackSize);
					var7.putStack(newItem);
					var7.onSlotChanged();
					var5 = (par1ItemStack.getCount() == 0);
					break;
				}
				if (par4) {
					--var6;
				} else {
					++var6;
				}
			}
		}
		return var5;
	}

	@Override
	public boolean canInteractWith(final EntityPlayer entityplayer) {
		return this.getTileEntity() != null && this.getTileEntity().isUsableByPlayer(entityplayer);
	}

	@Override
	public void addListener(final IContainerListener par1ICrafting) {
		super.addListener(par1ICrafting);
		if (this.getTileEntity() != null) {
			this.getTileEntity().initGuiData(this, par1ICrafting);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(final int par1, int par2) {
		par2 &= 0xFFFF;
		if (this.getTileEntity() != null) {
			this.getTileEntity().receiveGuiData(par1, (short) par2);
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (this.getTileEntity() != null) {
			for (final IContainerListener var2 : this.listeners) {
				this.getTileEntity().checkGuiData(this, var2);
			}
		}
	}
}
