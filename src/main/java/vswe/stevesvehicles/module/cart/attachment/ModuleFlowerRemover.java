package vswe.stevesvehicles.module.cart.attachment;

import java.util.List;

import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleFlowerRemover extends ModuleAttachment {
	public ModuleFlowerRemover(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	private static final float MAX_BLADE_SPEED = 1F;
	private static final float MIN_BLADE_SPEED = 0F;
	private static final float BLADE_ACCELERATION = 0.005F;

	// called to update the module's actions. Called by the cart's update code.
	@Override
	public void update() {
		super.update();
		World world = getVehicle().getWorld();
		if (world.isRemote) {
			bladeAngle += getBladeSpindSpeed();
			if (getVehicle().hasFuel()) {
				bladeSpeed = Math.min(MAX_BLADE_SPEED, bladeSpeed + BLADE_ACCELERATION);
			} else {
				bladeSpeed = Math.max(MIN_BLADE_SPEED, bladeSpeed - BLADE_ACCELERATION);
			}
			return;
		}
		if (getVehicle().hasFuel()) {
			if (tick >= getInterval()) {
				tick = 0;
				mowTheLawn(world);
				shearEntities(world);
			} else {
				tick++;
			}
		}
	}

	private int tick;

	protected int getInterval() {
		return 70;
	}

	protected int getBlocksOnSide() {
		return 7;
	}

	protected int getBlocksFromLevel() {
		return 1;
	}

	private void mowTheLawn(World world) {
		for (int offsetX = -getBlocksOnSide(); offsetX <= getBlocksOnSide(); offsetX++) {
			for (int offsetZ = -getBlocksOnSide(); offsetZ <= getBlocksOnSide(); offsetZ++) {
				for (int offsetY = -getBlocksFromLevel(); offsetY <= getBlocksFromLevel(); offsetY++) {
					BlockPos target = getVehicle().pos().add(offsetX, offsetY, offsetZ);
					if (isFlower(world, target)) {
						IBlockState state = world.getBlockState(target);
						if (state != null) {
							addStuff(state.getBlock().getDrops(getVehicle().getWorld(), target, state, 0));
							getVehicle().getWorld().setBlockToAir(target);
						}
					}
				}
			}
		}
	}

	private void shearEntities(World world) {
		List entities = world.getEntitiesWithinAABB(EntityLiving.class, getVehicle().getEntity().getEntityBoundingBox().expand(getBlocksOnSide(), getBlocksFromLevel() + 2F, getBlocksOnSide()));
		for (Object entity : entities) {
			EntityLiving target = (EntityLiving) entity;
			if (target instanceof IShearable) {
				IShearable shearable = (IShearable) target;
				if (shearable.isShearable(null, world, target.getPosition())) {
					addStuff(shearable.onSheared(null, world, target.getPosition(), 0));
				}
			}
		}
	}

	private boolean isFlower(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return state.getBlock() instanceof BlockBush;
	}

	private void addStuff(List<ItemStack> stuff) {
		for (ItemStack item : stuff) {
			getVehicle().addItemToChest(item);
			if (item.func_190916_E() != 0) {
				EntityItem entityitem = new EntityItem(getVehicle().getWorld(), getVehicle().getEntity().posX, getVehicle().getEntity().posY, getVehicle().getEntity().posZ, item);
				entityitem.motionX = 0;
				entityitem.motionY = 0.15F;
				entityitem.motionZ = 0;
				getVehicle().getWorld().spawnEntityInWorld(entityitem);
			}
		}
	}

	private float bladeAngle;
	private float bladeSpeed = 0;

	public float getBladeAngle() {
		return bladeAngle;
	}

	public float getBladeSpindSpeed() {
		return bladeSpeed;
	}
}
