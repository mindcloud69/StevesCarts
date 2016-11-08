package vswe.stevescarts.Modules.Realtimers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Modules.ModuleBase;

import java.util.ArrayList;
import java.util.List;

public class ModuleFlowerRemover extends ModuleBase {
	private int tick;
	private float bladeangle;
	private float bladespeed;

	public ModuleFlowerRemover(final MinecartModular cart) {
		super(cart);
		this.bladespeed = 0.0f;
	}

	@Override
	public void update() {
		super.update();
		if (this.getCart().worldObj.isRemote) {
			this.bladeangle += this.getBladeSpindSpeed();
			if (this.getCart().hasFuel()) {
				this.bladespeed = Math.min(1.0f, this.bladespeed + 0.005f);
			} else {
				this.bladespeed = Math.max(0.0f, this.bladespeed - 0.005f);
			}
			return;
		}
		if (this.getCart().hasFuel()) {
			if (this.tick >= this.getInterval()) {
				this.tick = 0;
				this.mownTheLawn();
				this.shearEntities();
			} else {
				++this.tick;
			}
		}
	}

	protected int getInterval() {
		return 70;
	}

	protected int getBlocksOnSide() {
		return 7;
	}

	protected int getBlocksFromLevel() {
		return 1;
	}

	private void mownTheLawn() {
		for (int x = -this.getBlocksOnSide(); x <= this.getBlocksOnSide(); ++x) {
			for (int z = -this.getBlocksOnSide(); z <= this.getBlocksOnSide(); ++z) {
				for (int y = -this.getBlocksFromLevel(); y <= this.getBlocksFromLevel(); ++y) {
					final int x2 = x + this.getCart().x();
					final int y2 = y + this.getCart().y();
					final int z2 = z + this.getCart().z();
					if (this.isFlower(x2, y2, z2)) {
						final Block block = this.getCart().worldObj.getBlock(x2, y2, z2);
						final int m = this.getCart().worldObj.getBlockMetadata(x2, y2, z2);
						if (block != null) {
							this.addStuff(block.getDrops(this.getCart().worldObj, x2, y2, z2, m, 0));
							this.getCart().worldObj.setBlockToAir(x2, y2, z2);
						}
					}
				}
			}
		}
	}

	private void shearEntities() {
		final List entities = this.getCart().worldObj.getEntitiesWithinAABB((Class) EntityLiving.class, this.getCart().boundingBox.expand((double) this.getBlocksOnSide(), (double) (this.getBlocksFromLevel() + 2.0f), (double) this.getBlocksOnSide()));
		for (final EntityLiving target : entities) {
			if (target instanceof IShearable) {
				final IShearable shearable = (IShearable) target;
				if (!shearable.isShearable((ItemStack) null, (IBlockAccess) this.getCart().worldObj, (int) target.posX, (int) target.posY, (int) target.posZ)) {
					continue;
				}
				this.addStuff(shearable.onSheared((ItemStack) null, (IBlockAccess) this.getCart().worldObj, (int) target.posX, (int) target.posY, (int) target.posZ, 0));
			}
		}
	}

	private boolean isFlower(final int x, final int y, final int z) {
		final Block block = this.getCart().worldObj.getBlock(x, y, z);
		return block != null && block instanceof BlockFlower;
	}

	private void addStuff(final ArrayList<ItemStack> stuff) {
		for (final ItemStack iStack : stuff) {
			this.getCart().addItemToChest(iStack);
			if (iStack.stackSize != 0) {
				final EntityItem entityitem = new EntityItem(this.getCart().worldObj, this.getCart().posX, this.getCart().posY, this.getCart().posZ, iStack);
				entityitem.motionX = 0.0;
				entityitem.motionY = 0.15000000596046448;
				entityitem.motionZ = 0.0;
				this.getCart().worldObj.spawnEntityInWorld(entityitem);
			}
		}
	}

	public float getBladeAngle() {
		return this.bladeangle;
	}

	public float getBladeSpindSpeed() {
		return this.bladespeed;
	}
}
