package vswe.stevescarts.modules.workers;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotFertilizer;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ISuppliesModule;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.workers.tools.ModuleFarmer;

import java.util.Random;

public class ModuleFertilizer extends ModuleWorker implements ISuppliesModule {
	private int tankPosX;
	private int tankPosY;
	private int range;
	private int fert;
	private final int fertPerBonemeal = 4;
	private final int maxStacksOfBones = 1;
	private final Random random = new Random();

	public ModuleFertilizer(final EntityMinecartModular cart) {
		super(cart);
		this.tankPosX = this.guiWidth() - 21;
		this.tankPosY = 20;
		this.range = 1;
		this.fert = 0;
	}

	@Override
	public byte getWorkPriority() {
		return 127;
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
	public void init() {
		super.init();
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module instanceof ModuleFarmer) {
				this.range = ((ModuleFarmer) module).getExternalRange();
				break;
			}
		}
	}

	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/fertilize.png");
		this.drawImage(gui, this.tankPosX, this.tankPosY, 0, 0, 18, 27);
		final float percentage = this.fert / this.getMaxFert();
		final int size = (int) (percentage * 23.0f);
		this.drawImage(gui, this.tankPosX + 2, this.tankPosY + 2 + (23 - size), 18, 23 - size, 14, size);
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, Localization.MODULES.ATTACHMENTS.FERTILIZERS.translate() + ": " + this.fert + " / " + this.getMaxFert(), x, y, this.tankPosX, this.tankPosY, 18, 27);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	@Override
	public int guiWidth() {
		return super.guiWidth() + 25;
	}

	@Override
	public int guiHeight() {
		return Math.max(super.guiHeight(), 50);
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotFertilizer(this.getCart(), slotId, 8 + x * 18, 23 + y * 18);
	}

	@Override
	public boolean work() {
		World world = getCart().worldObj;
		BlockPos next = this.getNextblock();
		for (int i = -this.range; i <= this.range; ++i) {
			for (int j = -this.range; j <= this.range; ++j) {
				if(random.nextInt(25) == 0 && this.fertilize(world, next.add(i, 0, j))){
					break;
				}
			}
		}
		return false;
	}

	private boolean fertilize(World world, BlockPos pos) {
		IBlockState stateOfTopBlock = world.getBlockState(pos);
		Block blockTop = stateOfTopBlock.getBlock();
		if (this.fert > 0) {
			if(blockTop instanceof IGrowable){
				IGrowable growable = (IGrowable) blockTop;
				if(growable.canGrow(world, pos, stateOfTopBlock, false)){
					if(growable.canUseBonemeal(world, this.getCart().rand, pos, stateOfTopBlock)){
						growable.grow(world, this.getCart().rand, pos, stateOfTopBlock);
						this.fert -= 2;
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public int numberOfGuiData() {
		return 1;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		this.updateGuiData(info, 0, (short) this.fert);
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.fert = data;
		}
	}

	@Override
	public void update() {
		super.update();
		this.loadSupplies();
	}

	private void loadSupplies() {
		if (this.getCart().worldObj.isRemote) {
			return;
		}
		if (this.getStack(0) != null) {
			final boolean isBone = this.getStack(0).getItem() == Items.BONE;
			final boolean isBoneMeal = this.getStack(0).getItem() == Items.DYE && this.getStack(0).getItemDamage() == 15;
			if (isBone || isBoneMeal) {
				int amount;
				if (isBoneMeal) {
					amount = 1;
				} else {
					amount = 3;
				}
				if (this.fert <= 4 * (192 - amount) && this.getStack(0).stackSize > 0) {
					final ItemStack stack = this.getStack(0);
					--stack.stackSize;
					this.fert += amount * 4;
				}
				if (this.getStack(0).stackSize == 0) {
					this.setStack(0, null);
				}
			}
		}
	}

	private int getMaxFert() {
		return 768;
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setShort(this.generateNBTName("Fert", id), (short) this.fert);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.fert = tagCompound.getShort(this.generateNBTName("Fert", id));
	}

	@Override
	public boolean haveSupplies() {
		return this.fert > 0;
	}
}
