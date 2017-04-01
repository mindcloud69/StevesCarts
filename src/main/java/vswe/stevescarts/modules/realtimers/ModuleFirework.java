package vswe.stevescarts.modules.realtimers;

import java.util.ArrayList;

import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotFirework;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.modules.ModuleBase;

public class ModuleFirework extends ModuleBase {
	private int fireCooldown;

	public ModuleFirework(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public void update() {
		if (this.fireCooldown > 0) {
			--this.fireCooldown;
		}
	}

	@Override
	public void activatedByRail(final int x, final int y, final int z, final boolean active) {
		if (active && this.fireCooldown == 0 && this.getCart().hasFuel()) {
			this.fire();
			this.fireCooldown = 20;
		}
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotFirework(this.getCart(), slotId, 8 + x * 18, 16 + y * 18);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	@Override
	public int guiWidth() {
		return 15 + this.getInventoryWidth() * 18;
	}

	@Override
	public int guiHeight() {
		return 20 + this.getInventoryHeight() * 18;
	}

	@Override
	protected int getInventoryWidth() {
		return 8;
	}

	@Override
	protected int getInventoryHeight() {
		return 3;
	}

	public void fire() {
		if (this.getCart().world.isRemote) {
			return;
		}
		@Nonnull ItemStack firework = this.getFirework();
		if (firework != null) {
			this.launchFirework(firework);
		}
	}

	private ItemStack getFirework() {
		boolean hasGunpowder = false;
		boolean hasPaper = false;
		for (int i = 0; i < this.getInventorySize(); ++i) {
			@Nonnull ItemStack item = this.getStack(i);
			if (item != null) {
				if (item.getItem() == Items.FIREWORKS) {
					@Nonnull ItemStack firework = item.copy();
					this.removeItemStack(item, firework.stackSize = 1, i);
					return firework;
				}
				if (item.getItem() == Items.PAPER) {
					hasPaper = true;
				} else if (item.getItem() == Items.GUNPOWDER) {
					hasGunpowder = true;
				}
			}
		}
		if (hasPaper && hasGunpowder) {
			@Nonnull ItemStack firework2 = new ItemStack(Items.FIREWORKS);
			final int maxGunpowder = this.getCart().rand.nextInt(3) + 1;
			int countGunpowder = 0;
			boolean removedPaper = false;
			for (int j = 0; j < this.getInventorySize(); ++j) {
				@Nonnull ItemStack item2 = this.getStack(j);
				if (item2 != null) {
					if (item2.getItem() == Items.PAPER && !removedPaper) {
						this.removeItemStack(item2, 1, j);
						removedPaper = true;
					} else if (item2.getItem() == Items.GUNPOWDER && countGunpowder < maxGunpowder) {
						while (item2.stackSize > 0 && countGunpowder < maxGunpowder) {
							++countGunpowder;
							this.removeItemStack(item2, 1, j);
						}
					}
				}
			}
			int chargeCount;
			for (chargeCount = 1; chargeCount < 7 && this.getCart().rand.nextInt(3 + chargeCount / 3) == 0; ++chargeCount) {}
			final NBTTagCompound itemstackNBT = new NBTTagCompound();
			final NBTTagCompound fireworksNBT = new NBTTagCompound();
			final NBTTagList explosionsNBT = new NBTTagList();
			for (int k = 0; k < chargeCount; ++k) {
				@Nonnull ItemStack charge = this.getCharge();
				if (charge == null) {
					break;
				}
				if (charge.hasTagCompound() && charge.getTagCompound().hasKey("Explosion")) {
					explosionsNBT.appendTag(charge.getTagCompound().getCompoundTag("Explosion"));
				}
			}
			fireworksNBT.setTag("Explosions", explosionsNBT);
			fireworksNBT.setByte("Flight", (byte) countGunpowder);
			itemstackNBT.setTag("Fireworks", fireworksNBT);
			firework2.setTagCompound(itemstackNBT);
			return firework2;
		}
		return null;
	}

	private ItemStack getCharge() {
		for (int i = 0; i < this.getInventorySize(); ++i) {
			@Nonnull ItemStack item = this.getStack(i);
			if (item != null && item.getItem() == Items.FIREWORK_CHARGE) {
				@Nonnull ItemStack charge = item.copy();
				this.removeItemStack(item, charge.stackSize = 1, i);
				return charge;
			}
		}
		@Nonnull ItemStack charge2 = new ItemStack(Items.FIREWORK_CHARGE);
		final NBTTagCompound itemNBT = new NBTTagCompound();
		final NBTTagCompound explosionNBT = new NBTTagCompound();
		byte type = 0;
		boolean removedGunpowder = false;
		final boolean canHasTrail = this.getCart().rand.nextInt(16) == 0;
		final boolean canHasFlicker = this.getCart().rand.nextInt(8) == 0;
		final boolean canHasModifier = this.getCart().rand.nextInt(4) == 0;
		final byte modifierType = (byte) (this.getCart().rand.nextInt(4) + 1);
		boolean removedModifier = false;
		boolean removedDiamond = false;
		boolean removedGlow = false;
		for (int j = 0; j < this.getInventorySize(); ++j) {
			@Nonnull ItemStack item2 = this.getStack(j);
			if (item2 != null) {
				if (item2.getItem() == Items.GUNPOWDER && !removedGunpowder) {
					this.removeItemStack(item2, 1, j);
					removedGunpowder = true;
				} else if (item2.getItem() == Items.GLOWSTONE_DUST && canHasFlicker && !removedGlow) {
					this.removeItemStack(item2, 1, j);
					removedGlow = true;
					explosionNBT.setBoolean("Flicker", true);
				} else if (item2.getItem() == Items.DIAMOND && canHasTrail && !removedDiamond) {
					this.removeItemStack(item2, 1, j);
					removedDiamond = true;
					explosionNBT.setBoolean("Trail", true);
				} else if (canHasModifier && !removedModifier && ((item2.getItem() == Items.FIREWORK_CHARGE && modifierType == 1) || (item2.getItem() == Items.GOLD_NUGGET && modifierType == 2) || (item2.getItem() == Items.SKULL && modifierType == 3) || (item2.getItem() == Items.FEATHER && modifierType == 4))) {
					this.removeItemStack(item2, 1, j);
					removedModifier = true;
					type = modifierType;
				}
			}
		}
		final int[] colors = this.generateColors((type != 0) ? 7 : 8);
		if (colors == null) {
			return null;
		}
		explosionNBT.setIntArray("Colors", colors);
		if (this.getCart().rand.nextInt(4) == 0) {
			final int[] fade = this.generateColors(8);
			if (fade != null) {
				explosionNBT.setIntArray("FadeColors", fade);
			}
		}
		explosionNBT.setByte("Type", type);
		itemNBT.setTag("Explosion", explosionNBT);
		charge2.setTagCompound(itemNBT);
		return charge2;
	}

	private int[] generateColors(final int maxColorCount) {
		final int[] maxColors = new int[16];
		final int[] currentColors = new int[16];
		for (int i = 0; i < this.getInventorySize(); ++i) {
			@Nonnull ItemStack item = this.getStack(i);
			if (item != null && item.getItem() == Items.DYE) {
				final int[] array = maxColors;
				final int itemDamage = item.getItemDamage();
				array[itemDamage] += item.stackSize;
			}
		}
		int colorCount;
		for (colorCount = this.getCart().rand.nextInt(2) + 1; colorCount <= maxColorCount - 2 && this.getCart().rand.nextInt(2) == 0; colorCount += 2) {}
		final ArrayList<Integer> colorPointers = new ArrayList<>();
		for (int j = 0; j < 16; ++j) {
			if (maxColors[j] > 0) {
				colorPointers.add(j);
			}
		}
		if (colorPointers.size() == 0) {
			return null;
		}
		final ArrayList<Integer> usedColors = new ArrayList<>();
		while (colorCount > 0 && colorPointers.size() > 0) {
			final int pointerId = this.getCart().rand.nextInt(colorPointers.size());
			final int colorId = colorPointers.get(pointerId);
			final int[] array2 = currentColors;
			final int n = colorId;
			++array2[n];
			final int[] array3 = maxColors;
			final int n2 = colorId;
			if (--array3[n2] <= 0) {
				colorPointers.remove(pointerId);
			}
			usedColors.add(colorId);
			--colorCount;
		}
		final int[] colors = new int[usedColors.size()];
		for (int k = 0; k < colors.length; ++k) {
			colors[k] = ItemDye.DYE_COLORS[usedColors.get(k)];
		}
		for (int k = 0; k < this.getInventorySize(); ++k) {
			@Nonnull ItemStack item2 = this.getStack(k);
			if (item2 != null && item2.getItem() == Items.DYE && currentColors[item2.getItemDamage()] > 0) {
				final int count = Math.min(currentColors[item2.getItemDamage()], item2.stackSize);
				final int[] array4 = currentColors;
				final int itemDamage2 = item2.getItemDamage();
				array4[itemDamage2] -= count;
			}
		}
		return colors;
	}

	private void removeItemStack(@Nonnull ItemStack item, final int count, final int id) {
		if (!this.getCart().hasCreativeSupplies()) {
			item.stackSize -= count;
			if (item.stackSize <= 0) {
				this.setStack(id, null);
			}
		}
	}

	private void launchFirework(@Nonnull ItemStack firework) {
		final EntityFireworkRocket rocket = new EntityFireworkRocket(this.getCart().world, this.getCart().posX, this.getCart().posY + 1.0, this.getCart().posZ, firework);
		this.getCart().world.spawnEntity(rocket);
	}
}
