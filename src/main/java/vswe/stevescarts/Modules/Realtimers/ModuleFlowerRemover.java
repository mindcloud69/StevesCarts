package vswe.stevescarts.modules.realtimers;

import java.util.List;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IShearable;
import vswe.stevescarts.entitys.MinecartModular;
import vswe.stevescarts.modules.ModuleBase;

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
		BlockPos cartPos = getCart().getPosition();
		for (int x = -this.getBlocksOnSide(); x <= this.getBlocksOnSide(); ++x) {
			for (int z = -this.getBlocksOnSide(); z <= this.getBlocksOnSide(); ++z) {
				for (int y = -this.getBlocksFromLevel(); y <= this.getBlocksFromLevel(); ++y) {
					BlockPos pos = cartPos.add(x, y, z);
					if (this.isFlower(pos)) {
						IBlockState state = getCart().worldObj.getBlockState(pos);
						if (state != null) {
							this.addStuff(state.getBlock().getDrops(this.getCart().worldObj, pos, state, 0));
							this.getCart().worldObj.setBlockToAir(pos);
						}
					}
				}
			}
		}
	}

	private void shearEntities() {
		final List<EntityLiving> entities = this.getCart().worldObj.getEntitiesWithinAABB(EntityLiving.class, this.getCart().getEntityBoundingBox().expand(this.getBlocksOnSide(), this.getBlocksFromLevel() + 2.0f, this.getBlocksOnSide()));
		for (EntityLiving target : entities) {
			if (target instanceof IShearable) {
				BlockPos pos = target.getPosition();
				final IShearable shearable = (IShearable) target;
				if (!shearable.isShearable((ItemStack) null, this.getCart().worldObj, pos)) {
					continue;
				}
				this.addStuff(shearable.onSheared((ItemStack) null, this.getCart().worldObj, pos, 0));
			}
		}
	}

	private boolean isFlower(BlockPos pos) {
		IBlockState state = this.getCart().worldObj.getBlockState(pos);
		return state != null && state.getBlock() instanceof BlockFlower;
	}

	private void addStuff(final List<ItemStack> stuff) {
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
