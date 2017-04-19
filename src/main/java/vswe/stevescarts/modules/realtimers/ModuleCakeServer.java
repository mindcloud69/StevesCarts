package vswe.stevescarts.modules.realtimers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotCake;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ISuppliesModule;
import vswe.stevescarts.modules.ModuleBase;

import javax.annotation.Nonnull;

public class ModuleCakeServer extends ModuleBase implements ISuppliesModule {
	private int cooldown;
	private static final int MAX_CAKES = 10;
	private static final int SLICES_PER_CAKE = 6;
	private static final int MAX_TOTAL_SLICES = 66;
	private int[] rect;
	private DataParameter<Integer> BUFFER;

	public ModuleCakeServer(final EntityMinecartModular cart) {
		super(cart);
		cooldown = 0;
		rect = new int[] { 40, 20, 13, 36 };
	}

	@Override
	public void update() {
		super.update();
		if (!getCart().world.isRemote) {
			if (getCart().hasCreativeSupplies()) {
				if (cooldown >= 20) {
					if (getCakeBuffer() < 66) {
						setCakeBuffer(getCakeBuffer() + 1);
					}
					cooldown = 0;
				} else {
					++cooldown;
				}
			}
			@Nonnull
			ItemStack item = getStack(0);
			if (!item.isEmpty() && item.getItem().equals(Items.CAKE) && getCakeBuffer() + 6 <= 66) {
				setCakeBuffer(getCakeBuffer() + 6);
				setStack(0, ItemStack.EMPTY);
			}
		}
	}

	private void setCakeBuffer(final int i) {
		updateDw(BUFFER, i);
	}

	private int getCakeBuffer() {
		if (isPlaceholder()) {
			return 6;
		}
		return getDw(BUFFER);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		BUFFER = createDw(DataSerializers.VARINT);
		registerDw(BUFFER, 0);
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected int getInventoryWidth() {
		return 1;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotCake(getCart(), slotId, 8 + x * 18, 38 + y * 18);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		drawString(gui, Localization.MODULES.ATTACHMENTS.CAKE_SERVER.translate(), 8, 6, 4210752);
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setShort(generateNBTName("Cake", id), (short) getCakeBuffer());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		setCakeBuffer(tagCompound.getShort(generateNBTName("Cake", id)));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		drawStringOnMouseOver(gui, Localization.MODULES.ATTACHMENTS.CAKES.translate(String.valueOf(getCakes()), String.valueOf(10)) + "\n" + Localization.MODULES.ATTACHMENTS.SLICES.translate(String.valueOf(getSlices()), String.valueOf(6)), x, y, rect);
	}

	private int getCakes() {
		if (getCakeBuffer() == 66) {
			return 10;
		}
		return getCakeBuffer() / 6;
	}

	private int getSlices() {
		if (getCakeBuffer() == 66) {
			return 6;
		}
		return getCakeBuffer() % 6;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/cake.png");
		drawImage(gui, rect, 0, inRect(x, y, rect) ? rect[3] : 0);
		final int maxHeight = rect[3] - 2;
		int height = (int) (getCakes() / 10.0f * maxHeight);
		if (height > 0) {
			drawImage(gui, rect[0] + 1, rect[1] + 1 + maxHeight - height, rect[2], maxHeight - height, 7, height);
		}
		height = (int) (getSlices() / 6.0f * maxHeight);
		if (height > 0) {
			drawImage(gui, rect[0] + 9, rect[1] + 1 + maxHeight - height, rect[2] + 7, maxHeight - height, 3, height);
		}
	}

	@Override
	public int guiWidth() {
		return 75;
	}

	@Override
	public int guiHeight() {
		return 60;
	}

	@Override
	public boolean onInteractFirst(final EntityPlayer entityplayer) {
		if (getCakeBuffer() > 0) {
			if (!getCart().world.isRemote && entityplayer.canEat(false)) {
				setCakeBuffer(getCakeBuffer() - 1);
				entityplayer.getFoodStats().addStats(2, 0.1f);
			}
			return true;
		}
		return false;
	}

	public int getRenderSliceCount() {
		int count = getSlices();
		if (count == 0 && getCakes() > 0) {
			count = 6;
		}
		return count;
	}

	@Override
	public boolean haveSupplies() {
		return getCakeBuffer() > 0;
	}
}
