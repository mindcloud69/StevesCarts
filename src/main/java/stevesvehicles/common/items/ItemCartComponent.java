package stevesvehicles.common.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.localization.ILocalizedText;
import stevesvehicles.client.localization.LocalizedTextAdvanced;
import stevesvehicles.client.rendering.models.items.ItemModelManager;
import stevesvehicles.client.rendering.models.items.TexturedItem;
import stevesvehicles.common.core.Constants;
import stevesvehicles.common.core.StevesVehicles;
import stevesvehicles.common.core.tabs.CreativeTabLoader;
import stevesvehicles.common.holiday.EntityEasterEgg;

public class ItemCartComponent extends Item implements TexturedItem {
	/*
	 * private IIcon icons[]; private IIcon unknownIcon;
	 */
	public static int size() {
		return ComponentTypes.values().length;
	}

	public ItemCartComponent() {
		super();
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CreativeTabLoader.components);
		ItemModelManager.registerItem(this);
	}

	private String getName(int dmg) {
		return ComponentTypes.values()[dmg].getUnlocalizedName();
	}

	/*
	 * @Override
	 * @SideOnly(Side.CLIENT) public IIcon getIconFromDamage(int dmg) { if (dmg
	 * < 0 || dmg >= icons.length || icons[dmg] == null) { return unknownIcon;
	 * }else{ return icons[dmg]; } }
	 * @Override
	 * @SideOnly(Side.CLIENT) public void registerIcons(IIconRegister register)
	 * { icons = new IIcon[size()]; for (int i = 0; i < icons.length; i++) { if
	 * (getName(i) != null) { icons[i] =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":components/" + getName(i)); } } unknownIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":unknown"); }
	 */
	private String getRawName(final int i) {
		if (getName(i) == null) {
			return null;
		}
		return this.getName(i).replace(":", "").replace(" ", "_").toLowerCase();
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		if (item == null || item.getItemDamage() < 0 || item.getItemDamage() >= size() || getName(item.getItemDamage()) == null) {
			return getUnlocalizedName();
		} else {
			return "steves_vehicles:item.component:" + getName(item.getItemDamage());
		}
	}

	@Override
	public String getUnlocalizedName() {
		return "steves_vehicles:item.component:unknown_component.name";
	}

	private static final ILocalizedText UNKNOWN_MODULE = new LocalizedTextAdvanced("steves_vehicles:item.component:unknown_component");
	private static final ILocalizedText UNKNOWN_MODULE_ID = new LocalizedTextAdvanced("steves_vehicles:item.component:unknown_component_id");

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List lst, boolean useExtraInfo) {
		if (item == null || item.getItemDamage() < 0 || item.getItemDamage() >= size() || getName(item.getItemDamage()) == null) {
			if (item != null && item.getItem() instanceof ItemCartComponent) {
				lst.add(UNKNOWN_MODULE.translate(String.valueOf(item.getItemDamage())));
			} else {
				lst.add(UNKNOWN_MODULE_ID.translate());
			}
		}
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList lst) {
		for (int i = 0; i < size(); i++) {
			ItemStack iStack = new ItemStack(item, 1, i);
			if (isValid(iStack)) {
				lst.add(iStack);
			}
		}
	}

	public boolean isValid(ItemStack item) {
		if (item == null || !(item.getItem() instanceof ItemCartComponent) || getName(item.getItemDamage()) == null) {
			return false;
		}
		ComponentTypes type = ComponentTypes.values()[item.getItemDamage()];
		return type.getRequiredHoliday() == null || StevesVehicles.holidays.contains(type.getRequiredHoliday());
	}

	// EASTER STUFF
	private boolean isEdibleEgg(ItemStack item) {
		return item != null && (item.getItemDamage() == ComponentTypes.EXPLOSIVE_EASTER_EGG.getId() || item.getItemDamage() == ComponentTypes.BURNING_EASTER_EGG.getId() || item.getItemDamage() == ComponentTypes.GLISTERING_EASTER_EGG.getId()
				|| item.getItemDamage() == ComponentTypes.CHOCOLATE_EASTER_EGG.getId());
	}

	private boolean isThrowableEgg(ItemStack item) {
		return item != null && item.getItemDamage() == ComponentTypes.PAINTED_EASTER_EGG.getId();
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
		if (isEdibleEgg(stack) && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (stack.getItemDamage() == ComponentTypes.EXPLOSIVE_EASTER_EGG.getId()) {
				// Explosive Easter Egg
				world.createExplosion(null, entity.posX, entity.posY, entity.posZ, 0.1F, false);
			} else if (stack.getItemDamage() == ComponentTypes.BURNING_EASTER_EGG.getId()) {
				// Burning Easter Egg
				entity.setFire(5);
				if (!world.isRemote) {
					entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 600, 0));
				}
			} else if (stack.getItemDamage() == ComponentTypes.GLISTERING_EASTER_EGG.getId()) {
				// Glistering Easter Egg
				if (!world.isRemote) {
					entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 50, 2));
				}
			} else if (stack.getItemDamage() == ComponentTypes.CHOCOLATE_EASTER_EGG.getId()) {
				// Chocolate Easter Egg
				if (!world.isRemote) {
					entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 300, 4));
				}
			}
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
			world.playSound((EntityPlayer) entity, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			player.getFoodStats().addStats(2, 0);
			return stack;
		} else {
			return super.onItemUseFinish(stack, world, entity);
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack item) {
		return isEdibleEgg(item) ? 32 : super.getMaxItemUseDuration(item);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack item) {
		return isEdibleEgg(item) ? EnumAction.EAT : super.getItemUseAction(item);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		if (isEdibleEgg(itemStack)) {
			player.setActiveHand(hand);
			return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
		} else if (isThrowableEgg(itemStack)) {
			if (!player.capabilities.isCreativeMode) {
				itemStack.shrink(1);
			}
			world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			if (!world.isRemote) {
				world.spawnEntity(new EntityEasterEgg(world, player));
			}
			return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
		} else {
			return super.onItemRightClick(world, player, hand);
		}
	}

	@Override
	public String getTextureName(int damage) {
		if (getRawName(damage) == null) {
			return Constants.UNKNOWN_SPRITE;
		}
		return Constants.MOD_ID + ":items/components/" + getName(damage);
	}

	@Override
	public int getMaxMeta() {
		return size();
	}
}
