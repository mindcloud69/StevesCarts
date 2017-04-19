package vswe.stevescarts.modules.realtimers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotCakeDynamite;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.items.ModItems;

import javax.annotation.Nonnull;

public class ModuleCakeServerDynamite extends ModuleCakeServer {
	private int dynamiteCount;

	private int getMaxDynamiteCount() {
		return Math.min(StevesCarts.instance.maxDynamites, 25);
	}

	public ModuleCakeServerDynamite(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotCakeDynamite(getCart(), slotId, 8 + x * 18, 38 + y * 18);
	}

	@Override
	public boolean dropOnDeath() {
		return dynamiteCount == 0;
	}

	@Override
	public void onDeath() {
		if (dynamiteCount > 0) {
			explode();
		}
	}

	private void explode() {
		getCart().world.createExplosion(null, getCart().posX, getCart().posY, getCart().posZ, dynamiteCount * 0.8f, true);
	}

	@Override
	public void update() {
		super.update();
		if (!getCart().world.isRemote) {
			@Nonnull
			ItemStack item = getStack(0);
			if (!item.isEmpty() && item.getItem().equals(ModItems.component) && item.getItemDamage() == 6 && dynamiteCount < getMaxDynamiteCount()) {
				final int count = Math.min(getMaxDynamiteCount() - dynamiteCount, item.getCount());
				dynamiteCount += count;
				@Nonnull
				ItemStack itemStack = item;
				itemStack.shrink(count);
				if (item.getCount() == 0) {
					setStack(0, ItemStack.EMPTY);
				}
			}
		}
	}

	@Override
	public boolean onInteractFirst(final EntityPlayer entityplayer) {
		if (dynamiteCount > 0) {
			explode();
			getCart().setDead();
			return true;
		}
		return super.onInteractFirst(entityplayer);
	}
}
