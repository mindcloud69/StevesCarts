package stevesvehicles.common.items;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.gui.ColorHelper;
import stevesvehicles.client.localization.entry.info.LocalizationLabel;
import stevesvehicles.client.rendering.models.items.ItemModelManager;
import stevesvehicles.client.rendering.models.items.TexturedItem;
import stevesvehicles.common.core.Constants;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.ModuleDataItemHandler;
import stevesvehicles.common.modules.datas.ModuleDataPair;
import stevesvehicles.common.utils.Tuple;
import stevesvehicles.common.vehicles.VehicleBase;
import stevesvehicles.common.vehicles.VehicleRegistry;
import stevesvehicles.common.vehicles.VehicleType;
import stevesvehicles.common.vehicles.entitys.IVehicleEntity;
import stevesvehicles.common.vehicles.version.VehicleVersion;

public class ItemVehicles extends Item implements TexturedItem {
	public ItemVehicles() {
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		setCreativeTab(null);
		ItemModelManager.registerItem(this);
	}

	/*
	 * @Override
	 * @SideOnly(Side.CLIENT) public void registerIcons(IIconRegister register)
	 * { for (VehicleType vehicleType :
	 * VehicleRegistry.getInstance().getAllVehicles()) {
	 * vehicleType.registerIcons(register); } fallbackFallbackIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":unknown"); }
	 * @SideOnly(Side.CLIENT) private IIcon fallbackFallbackIcon; //if it fails
	 * to use a fallback icon :P //this will only be used if the 3d rendering
	 * fails for some reason
	 * @Override
	 * @SideOnly(Side.CLIENT) public IIcon getIconFromDamage(int dmg) {
	 * VehicleType type = VehicleRegistry.getInstance().getTypeFromId(dmg); if
	 * (type != null) { return type.getFallbackIcon(); }else{ return
	 * fallbackFallbackIcon; } }
	 */
	private VehicleType getVehicleType(ItemStack item) {
		VehicleVersion.updateItemStack(item);
		return VehicleRegistry.getInstance().getTypeFromId(item.getItemDamage());
	}

	// TODO let the registered elements decide when they should be placed
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		VehicleType vehicle = getVehicleType(stack);
		if (vehicle != null && vehicle == VehicleRegistry.CART) {
			if (BlockRailBase.isRailBlock(world, pos)) {
				return (placeVehicle(vehicle, player, stack, world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) != null || world.isRemote) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
			}
		}
		return EnumActionResult.FAIL;
	}

	// TODO let the registered elements decide when they should be placed
	// TODO clean this up
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		VehicleType vehicle = getVehicleType(itemStack);
		if (vehicle != null && vehicle == VehicleRegistry.BOAT) {
			float f = 1.0F;
			float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
			float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
			double d0 = player.prevPosX + (player.posX - player.prevPosX) * f;
			double d1 = player.prevPosY + (player.posY - player.prevPosY) * f + 1.62D - player.getYOffset();
			double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
			Vec3d vec3 = new Vec3d(d0, d1, d2);
			float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
			float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
			float f5 = -MathHelper.cos(-f1 * 0.017453292F);
			float f6 = MathHelper.sin(-f1 * 0.017453292F);
			float f7 = f4 * f5;
			float f8 = f3 * f5;
			double d3 = 5.0D;
			Vec3d vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
			RayTraceResult result = world.rayTraceBlocks(vec3, vec31, true);
			if (result != null) {
				Vec3d vec32 = player.getLook(f);
				boolean flag = false;
				float f9 = 1.0F;
				List list = world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().addCoord(vec32.xCoord * d3, vec32.yCoord * d3, vec32.zCoord * d3).expand(f9, f9, f9));
				int i;
				for (i = 0; i < list.size(); ++i) {
					Entity entity = (Entity) list.get(i);
					if (entity.canBeCollidedWith()) {
						float f10 = entity.getCollisionBorderSize();
						AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(f10, f10, f10);
						if (axisalignedbb.isVecInside(vec3)) {
							flag = true;
						}
					}
				}
				if (!flag) {
					if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
						BlockPos pos = result.getBlockPos();
						if (world.getBlockState(pos).getBlock() == Blocks.SNOW_LAYER) {
							pos = pos.down();
						}
						Entity boat = placeVehicle(vehicle, player, itemStack, world, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F);
						if (boat != null) {
							boat.rotationYaw = ((MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3) - 1) * 90;
							if (!world.getCollisionBoxes(boat, boat.getEntityBoundingBox().expand(-0.1D, -0.1D, -0.1D)).isEmpty()) {
								boat.setDead();
							}
						}
					}
				}
			}
			return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
		}
		return ActionResult.newResult(EnumActionResult.PASS, itemStack);
	}

	private Entity placeVehicle(VehicleType vehicle, EntityPlayer player, ItemStack item, World world, double x, double y, double z) {
		if (!world.isRemote) {
			try {
				NBTTagCompound info = item.getTagCompound();
				if (info != null) {
					if (!info.hasKey(VehicleBase.NBT_INTERRUPT_MAX_TIME)) {
						Class<? extends IVehicleEntity> clazz = vehicle.getClazz();
						Constructor<? extends IVehicleEntity> constructor = clazz.getConstructor(World.class, double.class, double.class, double.class, NBTTagCompound.class, String.class);
						Object obj = constructor.newInstance(world, x, y, z, info, item.hasDisplayName() ? item.getDisplayName() : null);
						world.spawnEntity((Entity) obj);
						if (!player.capabilities.isCreativeMode) {
							item.shrink(1);
						}
						return (Entity) obj;
					}
				}
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			if (!player.capabilities.isCreativeMode) {
				item.shrink(1);
			}
			return null;
		}
	}

	/**
	 * allows items to add custom lines of information to the mouse over
	 * description
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack item, EntityPlayer player, List list, boolean useExtraInfo) {
		VehicleVersion.updateItemStack(item);
		NBTTagCompound info = item.getTagCompound();
		if (info != null) {
			addInfo(ModuleDataItemHandler.getModulesAndCompoundsFromItem(item), list, null);
			addInfo(ModuleDataItemHandler.getSpareModulesAndCompoundsFromItem(item), list, ColorHelper.ORANGE);
			if (info.hasKey(VehicleBase.NBT_INTERRUPT_MAX_TIME)) {
				list.add(ColorHelper.RED + LocalizationLabel.INCOMPLETE.translate());
				list.add(ColorHelper.RED + LocalizationLabel.INTERRUPT_INSTRUCTION.translate());
				int maxTime = info.getInteger(VehicleBase.NBT_INTERRUPT_MAX_TIME);
				int currentTime = info.getInteger(VehicleBase.NBT_INTERRUPT_TIME);
				int timeLeft = maxTime - currentTime;
				list.add(ColorHelper.RED + LocalizationLabel.TIME_LEFT.translate() + ": " + formatTime(timeLeft));
			}
			if (Constants.inDev) {
				// dev version only, no localization required
				list.add(ColorHelper.WHITE + "Version: " + (info.hasKey("CartVersion") ? info.getByte("CartVersion") : 0));
			}
		} else {
			list.add(LocalizationLabel.NO_MODULES.translate());
		}
	}

	private void addInfo(List<Tuple<ModuleData, NBTTagCompound>> modules, List list, ColorHelper color) {
		if (modules != null) {
			ArrayList<ModuleDataPair> counts = new ArrayList<>();
			for (Tuple<ModuleData, NBTTagCompound> moduleTuple : modules) {
				ModuleData module = moduleTuple.getFirstObject();
				NBTTagCompound moduleCompound = moduleTuple.getSecondObject();
				boolean found = false;
				if (module.hasExtraData()) {
					for (ModuleDataPair count : counts) {
						if (count.isContainingData(module)) {
							count.increase();
							found = true;
							break;
						}
					}
				}
				if (!found) {
					ModuleDataPair count = new ModuleDataPair(module);
					if (module.hasExtraData()) {
						count.setExtraData(moduleCompound);
					}
					counts.add(count);
				}
			}
			for (ModuleDataPair count : counts) {
				if (color != null) {
					list.add(color + count.toString());
				} else {
					list.add(count.toString());
				}
			}
		}
	}

	private String formatTime(int ticks) {
		int seconds = ticks / 20;
		ticks -= seconds * 20;
		int minutes = seconds / 60;
		seconds -= minutes * 60;
		int hours = minutes / 60;
		minutes -= hours * 60;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		int dmg = item.getItemDamage();
		VehicleType type = VehicleRegistry.getInstance().getTypeFromId(dmg);
		if (type != null) {
			return type.getUnlocalizedNameForItem();
		} else {
			return Constants.UNKNOWN_ITEM;
		}
	}

	@Override
	public boolean useMeshDefinition() {
		return true;
	}

	private VehicleType getModelType(int dmg) {
		VehicleType type = VehicleRegistry.getInstance().getTypeFromId(dmg);
		if (type == null) {
			type = VehicleRegistry.getInstance().getAllVehicles().get(dmg);
		}
		return type;
	}

	@Override
	public String getCustomModelLocation(ItemStack stack) {
		VehicleType type = getModelType(stack.getItemDamage());
		if (type != null) {
			return type.getUnlocalizedNameForItem();
		}
		return getUnlocalizedName();
	}

	@Override
	public String getTextureName(int damage) {
		VehicleType type = VehicleRegistry.getInstance().getAllVehicles().get(damage);
		if (type != null) {
			if (type.getIcon() == null) {
				type.setIcon(Constants.MOD_ID + ":items/vehicles/" + type.getRawUnlocalizedName());
			}
			return type.getIcon();
		}
		return Constants.UNKNOWN_SPRITE;
	}

	@Override
	public int getMaxMeta() {
		return VehicleRegistry.getInstance().getAllVehicles().size();
	}
}
