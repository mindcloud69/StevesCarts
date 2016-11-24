package stevesvehicles.common.container.slots;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import stevesvehicles.common.transfer.TransferHandler;

public class SlotBridge extends SlotBase implements ISpecialItemTransferValidator {
	public SlotBridge(IInventory inventory, int id, int x, int y) {
		super(inventory, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return isBridgeMaterial(itemstack);
	}

	public static boolean isBridgeMaterial(ItemStack itemstack) {
		Block b = Block.getBlockFromItem(itemstack.getItem());
		return b == Blocks.PLANKS || b == Blocks.BRICK_BLOCK || b == Blocks.STONE || (b == Blocks.STONEBRICK && itemstack.getItemDamage() == 0);
	}

	// don't allow the bridge builder to use picked up materials
	@Override
	public boolean isItemValidForTransfer(ItemStack item, TransferHandler.TransferType type) {
		return isItemValid(item) && type != TransferHandler.TransferType.OTHER;
	}
}
