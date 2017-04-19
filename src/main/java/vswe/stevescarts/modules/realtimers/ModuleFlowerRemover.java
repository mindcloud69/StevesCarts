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
		bladespeed = 0.0f;
	}

	@Override
	public void update() {
		super.update();
		if (getCart().world.isRemote) {
			bladeangle += getBladeSpindSpeed();
			if (getCart().hasFuel()) {
				bladespeed = Math.min(1.0f, bladespeed + 0.005f);
			} else {
				bladespeed = Math.max(0.0f, bladespeed - 0.005f);
			}
			return;
		}
		if (getCart().hasFuel()) {
			if (tick >= getInterval()) {
				tick = 0;
				mownTheLawn();
				shearEntities();
			} else {
				++tick;
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
		for (int x = -getBlocksOnSide(); x <= getBlocksOnSide(); ++x) {
			for (int z = -getBlocksOnSide(); z <= getBlocksOnSide(); ++z) {
				for (int y = -getBlocksFromLevel(); y <= getBlocksFromLevel(); ++y) {
					BlockPos pos = cartPos.add(x, y, z);
					if (isFlower(pos)) {
						IBlockState state = getCart().world.getBlockState(pos);
						if (state != null) {
							addStuff((NonNullList<ItemStack>) state.getBlock().getDrops(getCart().world, pos, state, 0));
							getCart().world.setBlockToAir(pos);
						}
					}
				}
			}
		}
	}

	private void shearEntities() {
		final List<EntityLiving> entities = getCart().world.getEntitiesWithinAABB(EntityLiving.class, getCart().getEntityBoundingBox().expand(getBlocksOnSide(), getBlocksFromLevel() + 2.0f, getBlocksOnSide()));
		for (EntityLiving target : entities) {
			if (target instanceof IShearable) {
				BlockPos pos = target.getPosition();
				final IShearable shearable = (IShearable) target;
				if (!shearable.isShearable(ItemStack.EMPTY, getCart().world, pos)) {
					continue;
				}
				addStuff((NonNullList<ItemStack>) shearable.onSheared(ItemStack.EMPTY, getCart().world, pos, 0));
			}
		}
	}

	private boolean isFlower(BlockPos pos) {
		IBlockState state = getCart().world.getBlockState(pos);
		return state != null && state.getBlock() instanceof IPlantable;
	}

	private void addStuff(final NonNullList<ItemStack> stuff) {
		for (
			@Nonnull
				ItemStack iStack : stuff) {
			getCart().addItemToChest(iStack);
			if (iStack.getCount() != 0) {
				final EntityItem entityitem = new EntityItem(getCart().world, getCart().posX, getCart().posY, getCart().posZ, iStack);
				entityitem.motionX = 0.0;
				entityitem.motionY = 0.15000000596046448;
				entityitem.motionZ = 0.0;
				getCart().world.spawnEntity(entityitem);
			}
		}
	}

	public float getBladeAngle() {
		return bladeangle;
	}

	public float getBladeSpindSpeed() {
		return bladespeed;
	}
}
