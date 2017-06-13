package vswe.stevescarts.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Constants;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.entitys.EntityEasterEgg;
import vswe.stevescarts.helpers.ComponentTypes;
import vswe.stevescarts.renders.model.ItemModelManager;
import vswe.stevescarts.renders.model.TexturedItem;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemCartComponent extends Item implements TexturedItem {
	//	private IIcon[] icons;
	//	private IIcon unknownIcon;

	public static int size() {
		return ComponentTypes.values().length;
	}

	public ItemCartComponent() {
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(StevesCarts.tabsSC2Components);
		ItemModelManager.registerItem(this);
	}

	private String getName(final int dmg) {
		return ComponentTypes.values()[dmg].getName();
	}

	public String getName(
		@Nonnull
			ItemStack par1ItemStack) {
		if (par1ItemStack.isEmpty() || par1ItemStack.getItemDamage() < 0 || par1ItemStack.getItemDamage() >= size() || getName(par1ItemStack.getItemDamage()) == null) {
			return "Unknown SC2 Component";
		}
		return getName(par1ItemStack.getItemDamage());
	}

	private String getRawName(final int i) {
		if (getName(i) == null) {
			return null;
		}
		return getName(i).replace(":", "").replace(" ", "_").toLowerCase();
	}


	@Override
	public String getUnlocalizedName(
		@Nonnull
			ItemStack item) {
		if (item.isEmpty() || item.getItemDamage() < 0 || item.getItemDamage() >= size() || getName(item.getItemDamage()) == null) {
			return getUnlocalizedName();
		}
		return "item.SC2:" + getRawName(item.getItemDamage());
	}

	@Override
	public String getUnlocalizedName() {
		return "item.SC2:unknowncomponent";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(
		@Nonnull
			ItemStack par1ItemStack, final World world, final List<String> par3List, final ITooltipFlag par4) {
		if (par1ItemStack.isEmpty() || par1ItemStack.getItemDamage() < 0 || par1ItemStack.getItemDamage() >= size() || getName(par1ItemStack.getItemDamage()) == null) {
			if (!par1ItemStack.isEmpty() && par1ItemStack.getItem() instanceof ItemCartComponent) {
				par3List.add("Component id " + par1ItemStack.getItemDamage());
			} else {
				par3List.add("Unknown component id");
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(final CreativeTabs par2CreativeTabs, final NonNullList<ItemStack> par3List) {
		if(!isInCreativeTab(par2CreativeTabs)){
			return;
		}
		for (int i = 0; i < size(); ++i) {
			@Nonnull
			ItemStack iStack = new ItemStack(this, 1, i);
			if (isValid(iStack)) {
				par3List.add(iStack);
			}
		}
	}

	public boolean isValid(
		@Nonnull
			ItemStack item) {
		if (item.isEmpty() || !(item.getItem() instanceof ItemCartComponent) || getName(item.getItemDamage()) == null) {
			return false;
		}
		if (item.getItemDamage() >= 50 && item.getItemDamage() < 58) {
			return Constants.isChristmas;
		}
		if (item.getItemDamage() >= 66 && item.getItemDamage() < 72) {
			return Constants.isEaster;
		}
		return item.getItemDamage() < 72 || item.getItemDamage() >= 80;
	}

	public static ItemStack getWood(final int type, final boolean isLog) {
		return getWood(type, isLog, 1);
	}

	public static ItemStack getWood(final int type, final boolean isLog, final int count) {
		return new ItemStack(ModItems.component, count, 72 + type * 2 + (isLog ? 0 : 1));
	}

	public static boolean isWoodLog(
		@Nonnull
			ItemStack item) {
		return !item.isEmpty() && item.getItemDamage() >= 72 && item.getItemDamage() < 80 && (item.getItemDamage() - 72) % 2 == 0;
	}

	public static boolean isWoodTwig(
		@Nonnull
			ItemStack item) {
		return !item.isEmpty() && item.getItemDamage() >= 72 && item.getItemDamage() < 80 && (item.getItemDamage() - 72) % 2 == 1;
	}

	private boolean isEdibleEgg(
		@Nonnull
			ItemStack item) {
		return !item.isEmpty() && item.getItemDamage() >= 66 && item.getItemDamage() < 70;
	}

	private boolean isThrowableEgg(
		@Nonnull
			ItemStack item) {
		return !item.isEmpty() && item.getItemDamage() == 70;
	}

	@Override
	@Nonnull
	public ItemStack onItemUseFinish(
		@Nonnull
			ItemStack item, World world, EntityLivingBase entity) {
		if (entity instanceof EntityPlayer && isEdibleEgg(item)) {
			EntityPlayer player = (EntityPlayer) entity;
			if (item.getItemDamage() == 66) {
				world.createExplosion(null, entity.posX, entity.posY, entity.posZ, 0.1f, false);
			} else if (item.getItemDamage() == 67) {
				entity.setFire(5);
				if (!world.isRemote) {
					entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 600, 0));
				}
			} else if (item.getItemDamage() == 68) {
				if (!world.isRemote) {
					entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 50, 2));
				}
			} else if (item.getItemDamage() == 69) {
				if (!world.isRemote) {
					entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 300, 4));
				}
			} else if (item.getItemDamage() == 70) {}
			if (!player.capabilities.isCreativeMode) {
				item.shrink(1);
			}
			world.playSound((EntityPlayer) entity, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			player.getFoodStats().addStats(2, 0.0f);
			return item;
		}
		return super.onItemUseFinish(item, world, entity);
	}

	@Override
	public int getMaxItemUseDuration(
		@Nonnull
			ItemStack item) {
		return isEdibleEgg(item) ? 32 : super.getMaxItemUseDuration(item);
	}

	@Override
	public EnumAction getItemUseAction(
		@Nonnull
			ItemStack item) {
		return isEdibleEgg(item) ? EnumAction.EAT : super.getItemUseAction(item);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		@Nonnull
		ItemStack item = player.getHeldItem(hand);
		if (isEdibleEgg(item)) {
			player.setActiveHand(hand);
			return ActionResult.newResult(EnumActionResult.SUCCESS, item);
		}
		if (isThrowableEgg(item)) {
			if (!player.capabilities.isCreativeMode) {
				item.shrink(1);
			}
			world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			if (!world.isRemote) {
				world.spawnEntity(new EntityEasterEgg(world, player));
			}
			return ActionResult.newResult(EnumActionResult.SUCCESS, item);
		}
		return super.onItemRightClick(world, player, hand);
	}

	@Override
	public String getTextureName(int damage) {
		if (getRawName(damage) == null) {
			return "stevescarts:items/unknown_icon";
		}
		return "stevescarts:items/" + getRawName(damage).toLowerCase() + "_icon";
	}

	@Override
	public int getMaxMeta() {
		return size();
	}
}
