package vswe.stevescarts.modules.storages.chests;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotChest;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.modules.storages.ModuleStorage;

public abstract class ModuleChest extends ModuleStorage {
	private float chestAngle;
	private DataParameter<Boolean> IS_OPEN;

	public ModuleChest(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	public void update() {
		super.update();
		handleChest();
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotChest(getCart(), slotId, 8 + x * 18, 16 + y * 18);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		drawString(gui, getModuleName(), 8, 6, 4210752);
	}

	@Override
	public int guiWidth() {
		return 15 + getInventoryWidth() * 18;
	}

	@Override
	public int guiHeight() {
		return 20 + getInventoryHeight() * 18;
	}

	public float getChestAngle() {
		return chestAngle;
	}

	protected boolean lidClosed() {
		return chestAngle <= 0.0f;
	}

	protected float getLidSpeed() {
		return 0.15707964f;
	}

	protected float chestFullyOpenAngle() {
		return 1.3744469f;
	}

	protected boolean hasVisualChest() {
		return true;
	}

	protected boolean playChestSound() {
		return hasVisualChest();
	}

	@Override
	public int numberOfDataWatchers() {
		if (hasVisualChest()) {
			return 1;
		}
		return 0;
	}

	@Override
	public void initDw() {
		if (hasVisualChest()) {
			IS_OPEN = createDw(DataSerializers.BOOLEAN);
			registerDw(IS_OPEN, false);
		}
	}

	public void openChest() {
		if (hasVisualChest()) {
			updateDw(IS_OPEN, true);
		}
	}

	public void closeChest() {
		if (hasVisualChest()) {
			updateDw(IS_OPEN, false);
		}
	}

	protected boolean isChestActive() {
		if (!hasVisualChest()) {
			return false;
		}
		if (isPlaceholder()) {
			return getSimInfo().getChestActive();
		}
		return getDw(IS_OPEN);
	}

	protected void handleChest() {
		if (!hasVisualChest()) {
			return;
		}
		if (isChestActive() && lidClosed() && playChestSound()) {
			//			this.getCart().world.playSoundEffect(this.getCart().posX, this.getCart().posY, this.getCart().posZ, "random.chestopen", 0.5f, this.getCart().world.rand.nextFloat() * 0.1f + 0.9f);
		}
		if (isChestActive() && chestAngle < chestFullyOpenAngle()) {
			chestAngle += getLidSpeed();
			if (chestAngle > chestFullyOpenAngle()) {
				chestAngle = chestFullyOpenAngle();
			}
		} else if (!isChestActive() && !lidClosed()) {
			final float lastAngle = chestAngle;
			chestAngle -= getLidSpeed();
			if (chestAngle < 1.1780972450961724 && lastAngle >= 1.1780972450961724 && playChestSound()) {
				//				this.getCart().world.playSoundEffect(this.getCart().posX, this.getCart().posY, this.getCart().posZ, "random.chestclosed", 0.5f, this.getCart().world.rand.nextFloat() * 0.1f + 0.9f);
			}
			if (chestAngle < 0.0f) {
				chestAngle = 0.0f;
			}
		}
	}

	public boolean isCompletelyFilled() {
		for (int i = 0; i < getInventorySize(); ++i) {
			if (getStack(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public boolean isCompletelyEmpty() {
		for (int i = 0; i < getInventorySize(); ++i) {
			if (!getStack(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}
}
