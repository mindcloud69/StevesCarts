package vswe.stevescarts.modules.storages.chests;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import vswe.stevescarts.entitys.MinecartModular;
import vswe.stevescarts.helpers.GiftItem;

public class ModuleGiftStorage extends ModuleChest {
	public ModuleGiftStorage(final MinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getInventoryWidth() {
		return 9;
	}

	@Override
	protected int getInventoryHeight() {
		return 4;
	}

	@Override
	public byte getExtraData() {
		return 0;
	}

	@Override
	public boolean hasExtraData() {
		return true;
	}

	@Override
	public void setExtraData(final byte b) {
		if (b == 0) {
			return;
		}
		final ArrayList<ItemStack> items = GiftItem.generateItems(this.getCart().rand, GiftItem.ChristmasList, 50 + this.getCart().rand.nextInt(700), 1 + this.getCart().rand.nextInt(5));
		for (int i = 0; i < items.size(); ++i) {
			this.setStack(i, items.get(i));
		}
	}
}
