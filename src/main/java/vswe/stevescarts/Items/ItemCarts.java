package vswe.stevescarts.Items;

import net.minecraft.block.BlockRailBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.CartVersion;
import vswe.stevescarts.Helpers.ColorHelper;
import vswe.stevescarts.Helpers.ModuleCountPair;
import vswe.stevescarts.ModuleData.ModuleData;
import vswe.stevescarts.StevesCarts;

import java.util.ArrayList;
import java.util.List;

public class ItemCarts extends ItemMinecart {
	public ItemCarts() {
		super(EntityMinecart.Type.RIDEABLE);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(null);
	}

	public String getName() {
		return "Modular cart";
	}

//	@SideOnly(Side.CLIENT)
//	public void registerIcons(final IIconRegister register) {
//		final StringBuilder sb = new StringBuilder();
//		StevesCarts.instance.getClass();
//		this.itemIcon = register.registerIcon(sb.append("stevescarts").append(":").append("modular_cart").append("_icon").toString());
//	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		CartVersion.updateItemStack(stack);
		if (!world.isRemote) {
			if (BlockRailBase.isRailBlock(world, pos)) {
				try {
					final NBTTagCompound info = stack.getTagCompound();
					if (info != null && !info.hasKey("maxTime")) {
						final MinecartModular cart = new MinecartModular(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, info, stack.getDisplayName());
						world.spawnEntityInWorld(cart);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return EnumActionResult.FAIL;
				}
			}
			--stack.stackSize;
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(final ItemStack item, final EntityPlayer player, final List list, final boolean useExtraInfo) {
		CartVersion.updateItemStack(item);
		final NBTTagCompound info = item.getTagCompound();
		if (info != null) {
			final NBTTagByteArray moduleIDTag = (NBTTagByteArray) info.getTag("Modules");
			final byte[] bytes = moduleIDTag.getByteArray();
			final ArrayList<ModuleCountPair> counts = new ArrayList<ModuleCountPair>();
			for (int i = 0; i < bytes.length; ++i) {
				final byte id = bytes[i];
				final ModuleData module = ModuleData.getList().get(id);
				if (module != null) {
					boolean found = false;
					if (!info.hasKey("Data" + i)) {
						for (final ModuleCountPair count : counts) {
							if (count.isContainingData(module)) {
								count.increase();
								found = true;
								break;
							}
						}
					}
					if (!found) {
						final ModuleCountPair count2 = new ModuleCountPair(module);
						if (info.hasKey("Data" + i)) {
							count2.setExtraData(info.getByte("Data" + i));
						}
						counts.add(count2);
					}
				}
			}
			for (final ModuleCountPair count3 : counts) {
				list.add(count3.toString());
			}
			if (info.hasKey("Spares")) {
				final byte[] spares = info.getByteArray("Spares");
				for (int j = 0; j < spares.length; ++j) {
					final byte id2 = spares[j];
					final ModuleData module2 = ModuleData.getList().get(id2);
					if (module2 != null) {
						String name = module2.getName();
						if (info.hasKey("Data" + (bytes.length + j))) {
							name = module2.getCartInfoText(name, info.getByte("Data" + (bytes.length + j)));
						}
						list.add(ColorHelper.ORANGE + name);
					}
				}
			}
			if (info.hasKey("maxTime")) {
				list.add(ColorHelper.RED + "Incomplete cart!");
				final int maxTime = info.getInteger("maxTime");
				final int currentTime = info.getInteger("currentTime");
				final int timeLeft = maxTime - currentTime;
				list.add(ColorHelper.RED + "Time left: " + this.formatTime(timeLeft));
			}
		} else {
			list.add("No modules loaded");
		}
	}

	private String formatTime(int ticks) {
		int seconds = ticks / 20;
		ticks -= seconds * 20;
		int minutes = seconds / 60;
		seconds -= minutes * 60;
		final int hours = minutes / 60;
		minutes -= hours * 60;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public boolean getShareTag() {
		return true;
	}
}
