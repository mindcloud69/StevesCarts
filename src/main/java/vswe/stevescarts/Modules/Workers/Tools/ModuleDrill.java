package vswe.stevescarts.Modules.Workers.Tools;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Blocks.ModBlocks;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.Addons.ModuleDrillIntelligence;
import vswe.stevescarts.Modules.Addons.ModuleIncinerator;
import vswe.stevescarts.Modules.Addons.ModuleLiquidSensors;
import vswe.stevescarts.Modules.Addons.ModuleOreTracker;
import vswe.stevescarts.Modules.IActivatorModule;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Storages.Chests.ModuleChest;

import java.util.ArrayList;

public abstract class ModuleDrill extends ModuleTool implements IActivatorModule {
	private ModuleDrillIntelligence intelligence;
	private ModuleLiquidSensors liquidsensors;
	private ModuleOreTracker tracker;
	private byte sensorLight;
	private float drillRotation;
	private int miningCoolDown;
	private int[] buttonRect;
	private static DataParameter<Boolean> IS_MINING = createDw(DataSerializers.BOOLEAN);
	private static DataParameter<Boolean> IS_ENABLED = createDw(DataSerializers.BOOLEAN);

	public ModuleDrill(final MinecartModular cart) {
		super(cart);
		this.sensorLight = 1;
		this.buttonRect = new int[] { 15, 30, 24, 12 };
	}

	@Override
	public byte getWorkPriority() {
		return 50;
	}

	@Override
	public void init() {
		super.init();
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module instanceof ModuleDrillIntelligence) {
				this.intelligence = (ModuleDrillIntelligence) module;
			}
			if (module instanceof ModuleLiquidSensors) {
				this.liquidsensors = (ModuleLiquidSensors) module;
			}
			if (module instanceof ModuleOreTracker) {
				this.tracker = (ModuleOreTracker) module;
			}
		}
	}

	@Override
	public boolean work() {
		if (!this.isDrillEnabled()) {
			this.stopDrill();
			this.stopWorking();
			return false;
		}
		if (!this.doPreWork()) {
			this.stopDrill();
			this.stopWorking();
		}
		if (this.isBroken()) {
			return false;
		}
		BlockPos next = this.getNextblock();
		final int[] range = this.mineRange();
		for (int holeY = range[1]; holeY >= range[0]; --holeY) {
			for (int holeX = -this.blocksOnSide(); holeX <= this.blocksOnSide(); ++holeX) {
				if (this.intelligence == null || this.intelligence.isActive(holeX + this.blocksOnSide(), holeY, range[2], next.getX() > this.getCart().x() || next.getZ() < this.getCart().z())) {
					if (this.mineBlockAndRevive(next.add(((this.getCart().z() != next.getZ()) ? holeX : 0), holeY,  ((this.getCart().x() != next.getX()) ? holeX : 0)), next, holeX, holeY)) {
						return true;
					}
				}
			}
		}
		BlockPos pos = next.add(0, range[0], 0);
		if (this.countsAsAir(pos) && !this.isValidForTrack(pos, true) && this.mineBlockAndRevive(pos.down(), next, 0, range[0] - 1)) {
			return true;
		}
		this.stopWorking();
		this.stopDrill();
		return false;
	}

	protected int[] mineRange() {
		BlockPos next = this.getNextblock();
		final int yTarget = this.getCart().getYTarget();
		if (BlockRailBase.isRailBlock(this.getCart().worldObj, next) || BlockRailBase.isRailBlock(this.getCart().worldObj, next.down())) {
			return new int[] { 0, this.blocksOnTop() - 1, 1 };
		}
		if (next.getY() > yTarget) {
			return new int[] { -1, this.blocksOnTop() - 1, 1 };
		}
		if (next.getY() < yTarget) {
			return new int[] { 1, this.blocksOnTop() + 1, 0 };
		}
		return new int[] { 0, this.blocksOnTop() - 1, 1 };
	}

	protected abstract int blocksOnTop();

	protected abstract int blocksOnSide();

	public int getAreaWidth() {
		return this.blocksOnSide() * 2 + 1;
	}

	public int getAreaHeight() {
		return this.blocksOnTop();
	}

	private boolean mineBlockAndRevive(BlockPos coord, BlockPos next, final int holeX, final int holeY) {
		if (this.mineBlock(coord, next, holeX, holeY, false)) {
			return true;
		}
		if (this.isDead()) {
			this.revive();
			return true;
		}
		return false;
	}

	protected boolean mineBlock(BlockPos coord, BlockPos next, final int holeX, final int holeY, final boolean flag) {
		if (this.tracker != null) {
			final BlockPos target = this.tracker.findBlockToMine(this, coord);
			if (target != null) {
				coord = target;
			}
		}
		final Object valid = this.isValidBlock(coord, holeX, holeY, flag);
		TileEntity storage = null;
		if (valid instanceof TileEntity) {
			storage = (TileEntity) valid;
		} else if (valid == null) {
			return false;
		}
		IBlockState blockState = getCart().worldObj.getBlockState(coord);
		final Block block = blockState.getBlock();
		float h = blockState.getBlockHardness(getCart().worldObj, coord);
		if (h < 0.0f) {
			h = 0.0f;
		}
		if (storage != null) {
			for (int i = 0; i < ((IInventory) storage).getSizeInventory(); ++i) {
				final ItemStack iStack = ((IInventory) storage).getStackInSlot(i);
				if (iStack != null) {
					if (!this.minedItem(iStack, next)) {
						return false;
					}
					((IInventory) storage).setInventorySlotContents(i, null);
				}
			}
		}
		final int fortune = (this.enchanter != null) ? this.enchanter.getFortuneLevel() : 0;
		if (this.shouldSilkTouch(blockState, coord)) {
			final ItemStack item = this.getSilkTouchedItem(blockState);
			if (item != null && !this.minedItem(item, next)) {
				return false;
			}
			this.getCart().worldObj.setBlockToAir(coord);
		} else if (block.getDrops(this.getCart().worldObj, coord, blockState, fortune).size() != 0) {
			final ArrayList<ItemStack> stacks = (ArrayList<ItemStack>) block.getDrops(this.getCart().worldObj, coord, blockState, fortune);
			boolean shouldRemove = false;
			for (int j = 0; j < stacks.size(); ++j) {
				if (!this.minedItem(stacks.get(j), next)) {
					return false;
				}
				shouldRemove = true;
			}
			if (shouldRemove) {
				this.getCart().worldObj.setBlockToAir(coord);
			}
		} else {
			this.getCart().worldObj.setBlockToAir(coord);
		}
		this.damageTool(1 + (int) h);
		this.startWorking(this.getTimeToMine(h));
		this.startDrill();
		return true;
	}

	protected boolean minedItem(final ItemStack iStack, BlockPos Coords) {
		if (iStack == null || iStack.stackSize <= 0) {
			return true;
		}
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module instanceof ModuleIncinerator) {
				((ModuleIncinerator) module).incinerate(iStack);
				if (iStack.stackSize <= 0) {
					return true;
				}
				continue;
			}
		}
		final int size = iStack.stackSize;
		this.getCart().addItemToChest(iStack);
		if (iStack.stackSize == 0) {
			return true;
		}
		boolean hasChest = false;
		for (final ModuleBase module2 : this.getCart().getModules()) {
			if (module2 instanceof ModuleChest) {
				hasChest = true;
				break;
			}
		}
		if (!hasChest) {
			final EntityItem entityitem = new EntityItem(this.getCart().worldObj, this.getCart().posX, this.getCart().posY, this.getCart().posZ, iStack);
			entityitem.motionX = (float) (this.getCart().x() - Coords.getX()) / 10.0f;
			entityitem.motionY = 0.15000000596046448;
			entityitem.motionZ = (float) (this.getCart().z() - Coords.getZ()) / 10.0f;
			this.getCart().worldObj.spawnEntityInWorld(entityitem);
			return true;
		}
		if (iStack.stackSize != size) {
			final EntityItem entityitem = new EntityItem(this.getCart().worldObj, this.getCart().posX, this.getCart().posY, this.getCart().posZ, iStack);
			entityitem.motionX = (float) (this.getCart().z() - Coords.getZ()) / 10.0f;
			entityitem.motionY = 0.15000000596046448;
			entityitem.motionZ = (float) (this.getCart().x() - Coords.getX()) / 10.0f;
			this.getCart().worldObj.spawnEntityInWorld(entityitem);
			return true;
		}
		return false;
	}

	private int getTimeToMine(final float hardness) {
		final int efficiency = (this.enchanter != null) ? this.enchanter.getEfficiencyLevel() : 0;
		return (int) (this.getTimeMult() * hardness / Math.pow(1.2999999523162842, efficiency)) + ((this.liquidsensors != null) ? 2 : 0);
	}

	protected abstract float getTimeMult();

	public Object isValidBlock(BlockPos pos, final int holeX, final int holeY, final boolean flag) {
		if ((!flag && BlockRailBase.isRailBlock(this.getCart().worldObj, pos)) || BlockRailBase.isRailBlock(this.getCart().worldObj, pos.up())) {
			return null;
		}
		IBlockState blockState = getCart().worldObj.getBlockState(pos);
		final Block block = blockState.getBlock();
		if (block == null) {
			return null;
		}
		if (block == Blocks.AIR) {
			return null;
		}
		if (block == Blocks.BEDROCK) {
			return null;
		}
		if (block instanceof BlockLiquid) {
			return null;
		}
		if (blockState.getBlockHardness(this.getCart().worldObj, pos) < 0.0f) {
			return null;
		}
		if ((holeX != 0 || holeY > 0) && (block == Blocks.TORCH || block == Blocks.REDSTONE_WIRE || block == Blocks.REDSTONE_TORCH || block == Blocks.UNLIT_REDSTONE_TORCH || block == Blocks.POWERED_REPEATER || block == Blocks.UNPOWERED_REPEATER || block == Blocks.POWERED_COMPARATOR || block == Blocks.UNPOWERED_COMPARATOR || block == ModBlocks.MODULE_TOGGLER.getBlock())) {
			return null;
		}
		if (block instanceof BlockContainer) {
			final TileEntity tileentity = this.getCart().worldObj.getTileEntity(pos);
			if (tileentity != null && IInventory.class.isInstance(tileentity)) {
				if (holeX != 0 || holeY > 0) {
					return null;
				}
				return tileentity;
			}
		}
		if (this.liquidsensors != null) {
			if (this.liquidsensors.isDangerous(this, pos.add(0, 1, 0), true) || this.liquidsensors.isDangerous(this, pos.add(1, 0, 0), false) || this.liquidsensors.isDangerous(this, pos.add(-1, 0, 0), false) || this.liquidsensors.isDangerous(this, pos.add(0, 0, 1), false) || this.liquidsensors.isDangerous(this, pos.add(0, 0, -1), false)) {
				this.sensorLight = 3;
				return null;
			}
			this.sensorLight = 2;
		}
		return false;
	}

	@Override
	public void update() {
		super.update();
		if ((this.getCart().hasFuel() && this.isMining()) || this.miningCoolDown < 10) {
			this.drillRotation = (float) ((this.drillRotation + 0.03f * (10 - this.miningCoolDown)) % 6.283185307179586);
			if (this.isMining()) {
				this.miningCoolDown = 0;
			} else {
				++this.miningCoolDown;
			}
		}
		if (!this.getCart().worldObj.isRemote && this.liquidsensors != null) {
			byte data = this.sensorLight;
			if (this.isDrillSpinning()) {
				data |= 0x4;
			}
			this.liquidsensors.getInfoFromDrill(data);
			this.sensorLight = 1;
		}
	}

	protected void startDrill() {
		this.updateDw(IS_MINING, true);
	}

	protected void stopDrill() {
		this.updateDw(IS_MINING, false);
	}

	protected boolean isMining() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getDrillSpinning();
		}
		return this.getDw(IS_MINING);
	}

	protected boolean isDrillSpinning() {
		return this.isMining() || this.miningCoolDown < 10;
	}

	@Override
	public void initDw() {
		registerDw(IS_MINING, false);
		registerDw(IS_ENABLED, true);
	}

	@Override
	public int numberOfDataWatchers() {
		return 2;
	}

	public float getDrillRotation() {
		return this.drillRotation;
	}

	private boolean isDrillEnabled() {
		return this.getDw(IS_ENABLED);
	}

	public void setDrillEnabled(final boolean val) {
		this.updateDw(IS_ENABLED, val);
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0 && this.inRect(x, y, this.buttonRect)) {
			this.sendPacket(0);
		}
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.setDrillEnabled(!this.isDrillEnabled());
		}
	}

	public int numberOfPackets() {
		return 1;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.TOOLS.DRILL.translate(), 8, 6, 4210752);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		super.drawBackground(gui, x, y);
		ResourceHelper.bindResource("/gui/drill.png");
		final int imageID = this.isDrillEnabled() ? 1 : 0;
		int borderID = 0;
		if (this.inRect(x, y, this.buttonRect)) {
			borderID = 1;
		}
		this.drawImage(gui, this.buttonRect, 0, this.buttonRect[3] * borderID);
		final int srcY = this.buttonRect[3] * 2 + imageID * (this.buttonRect[3] - 2);
		this.drawImage(gui, this.buttonRect[0] + 1, this.buttonRect[1] + 1, 0, srcY, this.buttonRect[2] - 2, this.buttonRect[3] - 2);
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		super.drawMouseOver(gui, x, y);
		this.drawStringOnMouseOver(gui, this.getStateName(), x, y, this.buttonRect);
	}

	private String getStateName() {
		return Localization.MODULES.TOOLS.TOGGLE.translate(this.isDrillEnabled() ? "1" : "0");
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		super.Save(tagCompound, id);
		tagCompound.setBoolean(this.generateNBTName("DrillEnabled", id), this.isDrillEnabled());
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		super.Load(tagCompound, id);
		this.setDrillEnabled(tagCompound.getBoolean(this.generateNBTName("DrillEnabled", id)));
	}

	@Override
	public void doActivate(final int id) {
		this.setDrillEnabled(true);
	}

	@Override
	public void doDeActivate(final int id) {
		this.setDrillEnabled(false);
	}

	@Override
	public boolean isActive(final int id) {
		return this.isDrillEnabled();
	}
}
