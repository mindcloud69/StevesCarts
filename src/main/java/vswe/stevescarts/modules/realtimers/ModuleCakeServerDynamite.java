package vswe.stevescarts.modules.realtimers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotCakeDynamite;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.items.ModItems;

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
		return new SlotCakeDynamite(this.getCart(), slotId, 8 + x * 18, 38 + y * 18);
	}

	@Override
	public boolean dropOnDeath() {
		return this.dynamiteCount == 0;
	}

	@Override
	public void onDeath() {
		if (this.dynamiteCount > 0) {
			this.explode();
		}
	}

	private void explode() {
		this.getCart().world.createExplosion(null, this.getCart().posX, this.getCart().posY, this.getCart().posZ, this.dynamiteCount * 0.8f, true);
	}

	@Override
	public void update() {
		super.update();
		if (!this.getCart().world.isRemote) {
			@Nonnull ItemStack item = this.getStack(0);
			if (item != null && item.getItem().equals(ModItems.component) && item.getItemDamage() == 6 && this.dynamiteCount < this.getMaxDynamiteCount()) {
				final int count = Math.min(this.getMaxDynamiteCount() - this.dynamiteCount, item.stackSize);
				this.dynamiteCount += count;
				@Nonnull ItemStack itemStack = item;
				itemStack.stackSize -= count;
				if (item.stackSize == 0) {
					this.setStack(0, null);
				}
			}
		}
	}

	@Override
	public boolean onInteractFirst(final EntityPlayer entityplayer) {
		if (this.dynamiteCount > 0) {
			this.explode();
			this.getCart().setDead();
			return true;
		}
		return super.onInteractFirst(entityplayer);
	}
}
