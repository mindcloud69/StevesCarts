package vswe.stevesvehicles.item;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vswe.stevesvehicles.Constants;
import vswe.stevesvehicles.buoy.BuoyType;
import vswe.stevesvehicles.buoy.EntityBuoy;
import vswe.stevesvehicles.tab.CreativeTabLoader;

public class ItemBuoy extends Item {
	public ItemBuoy() {
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CreativeTabLoader.blocks);
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		if (item != null && item.getItemDamage() >= 0 && item.getItemDamage() < BuoyType.values().length) {
			BuoyType buoy = BuoyType.getType(item.getItemDamage());
			return buoy.getUnlocalizedName();
		}
		return Constants.UNKNOWN_ITEM;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList list) {
		for (BuoyType buoyType : BuoyType.values()) {
			list.add(new ItemStack(item, 1, buoyType.getMeta()));
		}
	}

	private static final int VIEW_MULTIPLIER = 5;
	private static final double AREA_EXPANSION = 1.0;

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		double x = player.posX;
		double y = player.posY + 1.62D - player.getYOffset();
		double z = player.posZ;
		Vec3d camera = new Vec3d(x, y, z);
		Vec3d look = player.getLook(1.0F);
		look = new Vec3d(look.xCoord * VIEW_MULTIPLIER, look.yCoord * VIEW_MULTIPLIER, look.zCoord * VIEW_MULTIPLIER);
		Vec3d target = camera.addVector(look.xCoord, look.yCoord, look.zCoord);
		RayTraceResult object = world.rayTraceBlocks(camera, target, true);
		if (object != null && object.typeOfHit == RayTraceResult.Type.BLOCK) {
			// noinspection unchecked
			List<Entity> list = world.getEntitiesWithinAABB(Entity.class, player.getEntityBoundingBox().addCoord(look.xCoord, look.yCoord, look.zCoord).expand(AREA_EXPANSION, AREA_EXPANSION, AREA_EXPANSION));
			boolean valid = true;
			for (Entity entity : list) {
				if (entity.canBeCollidedWith()) {
					float borderSize = entity.getCollisionBorderSize();
					AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(borderSize, borderSize, borderSize);
					if (axisalignedbb.isVecInside(camera)) {
						valid = false;
						break;
					}
				}
			}
			if (valid) {
				IBlockState state = world.getBlockState(object.getBlockPos());
				if (state.getMaterial() == Material.WATER) { // TODO allow lava?
					BlockPos pos = object.getBlockPos().add(0, 1, 0);
					if (world.isAirBlock(pos)) {
						if (!world.isRemote) {
							EntityBuoy buoy = new EntityBuoy(world, pos, BuoyType.getType(itemStack.getItemDamage()));
							world.spawnEntityInWorld(buoy);
						}
						if (!player.capabilities.isCreativeMode) {
							itemStack.func_190918_g(1);
						}
					}
				}
			}
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
	}
}
