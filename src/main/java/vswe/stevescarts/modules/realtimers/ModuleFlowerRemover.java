package vswe.stevescarts.modules.realtimers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.modules.ModuleBase;

import javax.annotation.Nonnull;
import java.util.List;

public class ModuleFlowerRemover extends ModuleBase {
	private int tick;
	private float bladeangle;
	private float bladespeed;

	public ModuleFlowerRemover(final EntityMinecartModular cart) {
		super(cart);
		this.bladespeed = 0.0f;
	}

	@Override
	public void update() {
		super.update();
		if (this.getCart().world.isRemote) {
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
						IBlockState state = getCart().world.getBlockState(pos);
						if (state != null) {
							this.addStuff((NonNullList<ItemStack>) state.getBlock().getDrops(this.getCart().world, pos, state, 0));
							this.getCart().world.setBlockToAir(pos);
						}
					}
				}
			}
		}
	}

	private void shearEntities() {
		final List<EntityLiving> entities = this.getCart().world.getEntitiesWithinAABB(EntityLiving.class, this.getCart().getEntityBoundingBox().expand(this.getBlocksOnSide(), this.getBlocksFromLevel() + 2.0f, this.getBlocksOnSide()));
		for (EntityLiving target : entities) {
			if (target instanceof IShearable) {
				BlockPos pos = target.getPosition();
				final IShearable shearable = (IShearable) target;
				if (!shearable.isShearable(ItemStack.EMPTY, this.getCart().world, pos)) {
					continue;
				}
				this.addStuff((NonNullList<ItemStack>) shearable.onSheared(ItemStack.EMPTY, this.getCart().world, pos, 0));
			}
		}
	}

	private boolean isFlower(BlockPos pos) {
		IBlockState state = this.getCart().world.getBlockState(pos);
		return state != null && state.getBlock() instanceof IPlantable;
	}

	private void addStuff(final NonNullList<ItemStack> stuff) {
		for (
			@Nonnull
				ItemStack iStack : stuff) {
			this.getCart().addItemToChest(iStack);
			if (iStack.getCount() != 0) {
				final EntityItem entityitem = new EntityItem(this.getCart().world, this.getCart().posX, this.getCart().posY, this.getCart().posZ, iStack);
				entityitem.motionX = 0.0;
				entityitem.motionY = 0.15000000596046448;
				entityitem.motionZ = 0.0;
				this.getCart().world.spawnEntity(entityitem);
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
