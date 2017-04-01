package vswe.stevescarts.modules.workers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import vswe.stevescarts.containers.slots.SlotBase;
import vswe.stevescarts.containers.slots.SlotTorch;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ISuppliesModule;

public class ModuleTorch extends ModuleWorker implements ISuppliesModule {
	private int light;
	private int lightLimit;
	private int[] boxRect;
	boolean markerMoving;
	private DataParameter<Integer> TORCHES;

	public ModuleTorch(final EntityMinecartModular cart) {
		super(cart);
		this.lightLimit = 8;
		this.boxRect = new int[] { 12, this.guiHeight() - 10, 46, 9 };
		this.markerMoving = false;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public int guiWidth() {
		return 80;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotTorch(this.getCart(), slotId, 8 + x * 18, 23 + y * 18);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	@Override
	public byte getWorkPriority() {
		return 95;
	}

	@Override
	public boolean work() {
		final BlockPos next = this.getLastblock();
		final EntityMinecartModular cart = getCart();
		final World world = cart.world;
		final int x = next.getX();
		final int y = next.getY();
		final int z = next.getZ();
		final int cartX = cart.x();
		final int cartZ = cart.z();
		if (this.light <= this.lightLimit) {
			for (int side = -1; side <= 1; side += 2) {
				final int xTorch = x + ((cartZ != z) ? side : 0);
				final int zTorch = z + ((cartX != x) ? side : 0);
				for (int level = 2; level >= -2; --level) {
					BlockPos pos = new BlockPos(xTorch, y + level, zTorch);
					if (world.isAirBlock(pos) && Blocks.TORCH.canPlaceBlockAt(world, pos)) {
						int i = 0;
						while (i < this.getInventorySize()) {
							if (!this.getStack(i).isEmpty() && Block.getBlockFromItem(this.getStack(i).getItem()) == Blocks.TORCH) {
								if (this.doPreWork()) {
									this.startWorking(3);
									return true;
								}
								IBlockState state = Blocks.TORCH.getStateForPlacement(world, pos, EnumFacing.DOWN, 0, 0, 0, 0, getFakePlayer(), EnumHand.MAIN_HAND);
								world.setBlockState(new BlockPos(xTorch, y + level, zTorch), state);
								if (!cart.hasCreativeSupplies()) {
									@Nonnull ItemStack stack = this.getStack(i);
									stack.shrink(1);
									if (this.getStack(i).getCount() == 0) {
										this.setStack(i, ItemStack.EMPTY);
									}
									this.onInventoryChanged();
									break;
								}
								break;
							} else {
								++i;
							}
						}
						break;
					}
					if (world.getBlockState(pos).getBlock() == Blocks.TORCH) {
						break;
					}
				}
			}
		}
		this.stopWorking();
		return false;
	}

	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/torch.png");
		int barLength = 3 * this.light;
		if (this.light == 15) {
			--barLength;
		}
		int srcX = 0;
		if (this.inRect(x, y, this.boxRect)) {
			srcX += this.boxRect[2];
		}
		this.drawImage(gui, this.boxRect, srcX, 0);
		this.drawImage(gui, 13, this.guiHeight() - 10 + 1, 0, 9, barLength, 7);
		this.drawImage(gui, 12 + 3 * this.lightLimit, this.guiHeight() - 10, 0, 16, 1, 9);
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, "Threshold: " + this.lightLimit + " Current: " + this.light, x, y, this.boxRect);
	}

	@Override
	public int guiHeight() {
		return super.guiHeight() + 10;
	}

	@Override
	public int numberOfGuiData() {
		return 2;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		short data = (short) (this.light & 0xF);
		data |= (short) ((this.lightLimit & 0xF) << 4);
		this.updateGuiData(info, 0, data);
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.light = (data & 0xF);
			this.lightLimit = (data & 0xF0) >> 4;
		}
	}

	@Override
	public int numberOfPackets() {
		return 1;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.lightLimit = data[0];
			if (this.lightLimit < 0) {
				this.lightLimit = 0;
			} else if (this.lightLimit > 15) {
				this.lightLimit = 15;
			}
		}
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0 && this.inRect(x, y, this.boxRect)) {
			this.generatePacket(x, y);
			this.markerMoving = true;
		}
	}

	@Override
	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
		if (this.markerMoving) {
			this.generatePacket(x, y);
		}
		if (button != -1) {
			this.markerMoving = false;
		}
	}

	private void generatePacket(final int x, final int y) {
		final int xInBox = x - this.boxRect[0];
		int val = xInBox / 3;
		if (val < 0) {
			val = 0;
		} else if (val > 15) {
			val = 15;
		}
		this.sendPacket(0, (byte) val);
	}

	public void setThreshold(final byte val) {
		this.lightLimit = val;
	}

	public int getThreshold() {
		return this.lightLimit;
	}

	public int getLightLevel() {
		return this.light;
	}

	@Override
	public void update() {
		super.update();
		this.light = this.getCart().world.getLightFor(EnumSkyBlock.BLOCK, new BlockPos(this.getCart().x(), this.getCart().y() + 1, this.getCart().z()));
	}

	@Override
	public void initDw() {
		TORCHES = createDw(DataSerializers.VARINT);
		registerDw(TORCHES, 0);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		this.calculateTorches();
	}

	private void calculateTorches() {
		if (this.getCart().world.isRemote) {
			return;
		}
		int val = 0;
		for (int i = 0; i < 3; ++i) {
			val |= ((this.getStack(i) != null) ? 1 : 0) << i;
		}
		this.updateDw(TORCHES, val);
	}

	public int getTorches() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getTorchInfo();
		}
		return this.getDw(TORCHES);
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setByte(this.generateNBTName("lightLimit", id), (byte) this.lightLimit);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.lightLimit = tagCompound.getByte(this.generateNBTName("lightLimit", id));
		this.calculateTorches();
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < this.getInventorySize(); ++i) {
			@Nonnull ItemStack item = this.getStack(i);
			if (item != null && Block.getBlockFromItem(item.getItem()) == Blocks.TORCH) {
				return true;
			}
		}
		return false;
	}
}
