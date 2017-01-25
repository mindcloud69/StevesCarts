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

public class ModuleCakeServer extends ModuleBase implements ISuppliesModule {
	private int cooldown;
	private static final int MAX_CAKES = 10;
	private static final int SLICES_PER_CAKE = 6;
	private static final int MAX_TOTAL_SLICES = 66;
	private int[] rect;
	private DataParameter<Integer> BUFFER;

	public ModuleCakeServer(final EntityMinecartModular cart) {
		super(cart);
		this.cooldown = 0;
		this.rect = new int[] { 40, 20, 13, 36 };
	}

	@Override
	public void update() {
		super.update();
		if (!this.getCart().world.isRemote) {
			if (this.getCart().hasCreativeSupplies()) {
				if (this.cooldown >= 20) {
					if (this.getCakeBuffer() < 66) {
						this.setCakeBuffer(this.getCakeBuffer() + 1);
					}
					this.cooldown = 0;
				} else {
					++this.cooldown;
				}
			}
			final ItemStack item = this.getStack(0);
			if (item != null && item.getItem().equals(Items.CAKE) && this.getCakeBuffer() + 6 <= 66) {
				this.setCakeBuffer(this.getCakeBuffer() + 6);
				this.setStack(0, null);
			}
		}
	}

	private void setCakeBuffer(final int i) {
		this.updateDw(BUFFER, i);
	}

	private int getCakeBuffer() {
		if (this.isPlaceholder()) {
			return 6;
		}
		return this.getDw(BUFFER);
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
		return new SlotCake(this.getCart(), slotId, 8 + x * 18, 38 + y * 18);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ATTACHMENTS.CAKE_SERVER.translate(), 8, 6, 4210752);
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setShort(this.generateNBTName("Cake", id), (short) this.getCakeBuffer());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.setCakeBuffer(tagCompound.getShort(this.generateNBTName("Cake", id)));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, Localization.MODULES.ATTACHMENTS.CAKES.translate(String.valueOf(this.getCakes()), String.valueOf(10)) + "\n" + Localization.MODULES.ATTACHMENTS.SLICES.translate(String.valueOf(this.getSlices()), String.valueOf(6)), x, y, this.rect);
	}

	private int getCakes() {
		if (this.getCakeBuffer() == 66) {
			return 10;
		}
		return this.getCakeBuffer() / 6;
	}

	private int getSlices() {
		if (this.getCakeBuffer() == 66) {
			return 6;
		}
		return this.getCakeBuffer() % 6;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/cake.png");
		this.drawImage(gui, this.rect, 0, this.inRect(x, y, this.rect) ? this.rect[3] : 0);
		final int maxHeight = this.rect[3] - 2;
		int height = (int) (this.getCakes() / 10.0f * maxHeight);
		if (height > 0) {
			this.drawImage(gui, this.rect[0] + 1, this.rect[1] + 1 + maxHeight - height, this.rect[2], maxHeight - height, 7, height);
		}
		height = (int) (this.getSlices() / 6.0f * maxHeight);
		if (height > 0) {
			this.drawImage(gui, this.rect[0] + 9, this.rect[1] + 1 + maxHeight - height, this.rect[2] + 7, maxHeight - height, 3, height);
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
		if (this.getCakeBuffer() > 0) {
			if (!this.getCart().world.isRemote && entityplayer.canEat(false)) {
				this.setCakeBuffer(this.getCakeBuffer() - 1);
				entityplayer.getFoodStats().addStats(2, 0.1f);
			}
			return true;
		}
		return false;
	}

	public int getRenderSliceCount() {
		int count = this.getSlices();
		if (count == 0 && this.getCakes() > 0) {
			count = 6;
		}
		return count;
	}

	@Override
	public boolean haveSupplies() {
		return this.getCakeBuffer() > 0;
	}
}
