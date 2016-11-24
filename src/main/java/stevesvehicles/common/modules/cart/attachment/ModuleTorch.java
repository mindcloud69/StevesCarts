package stevesvehicles.common.modules.cart.attachment;

import java.util.List;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.gui.assembler.SimulationInfo;
import stevesvehicles.client.gui.assembler.SimulationInfoMultiBoolean;
import stevesvehicles.client.gui.screen.GuiVehicle;
import stevesvehicles.client.localization.entry.block.LocalizationAssembler;
import stevesvehicles.client.localization.entry.module.cart.LocalizationCartRails;
import stevesvehicles.common.container.slots.SlotBase;
import stevesvehicles.common.container.slots.SlotTorch;
import stevesvehicles.common.modules.ISuppliesModule;
import stevesvehicles.common.modules.cart.ModuleWorker;
import stevesvehicles.common.network.DataReader;
import stevesvehicles.common.network.DataWriter;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleTorch extends ModuleWorker implements ISuppliesModule {
	private DataParameter<Integer> TORCHES;

	public ModuleTorch(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.add(new SimulationInfoMultiBoolean(LocalizationAssembler.INFO_TORCHES, "torch", 3, true));
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
	protected SlotBase getSlot(int slotId, int x, int y) {
		return new SlotTorch(getVehicle().getVehicleEntity(), slotId, 8 + x * 18, 23 + y * 18);
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, getModuleName(), 8, 6, 0x404040);
	}

	// lower numbers are prioritized
	@Override
	public byte getWorkPriority() {
		return 95;
	}

	@Override
	public boolean work() {
		World world = getVehicle().getWorld();
		// get the next block
		BlockPos next = getLastBlock();
		// if it's too dark, try to place torches
		if (light <= lightLimit) {
			// try to place it at both sides
			for (int side = -1; side <= 1; side += 2) {
				// calculate the x and z coordinates, this depends on which
				// direction the cart is going
				BlockPos torch = next.add(getVehicle().z() != next.getZ() ? side : 0, 0, getVehicle().x() != next.getX() ? side : 0);
				// now it's time to find a proper y value
				for (int level = 2; level >= -2; level--) {
					// check if this coordinate is a valid place to place a
					// torch
					BlockPos target = torch.add(0, level, 0);
					if (world.isAirBlock(target) && Blocks.TORCH.canPlaceBlockAt(world, target)) {
						// check if there's any torches to place
						for (int i = 0; i < getInventorySize(); i++) {
							// check if the slot contains torches
							if (getStack(i) != null) {
								if (Block.getBlockFromItem(getStack(i).getItem()) == Blocks.TORCH) {
									if (doPreWork()) {
										startWorking(3);
										return true;
									}
									// if so place it and remove one torch from
									// the cart's inventory
									IBlockState state = Blocks.TORCH.getStateForPlacement(world, target, EnumFacing.DOWN, 0, 0, 0, 0, null, EnumHand.MAIN_HAND);
									getVehicle().getWorld().setBlockState(torch, state);
									if (!getVehicle().hasCreativeSupplies()) {
										getStack(i).shrink(1);
										if (getStack(i).getCount() == 0) {
											setStack(i, null);
										}
										this.onInventoryChanged();
									}
									break;
								}
							}
						}
						break;
						// if it isn't valid but there's already a torch there
						// this side is already done. This shouldn't really
						// happen since then it wouldn't be dark enough in the
						// first place.
					} else if (getVehicle().getWorld().getBlockState(target).getBlock() == Blocks.TORCH) {
						break;
					}
				}
			}
		}
		stopWorking();
		return false;
	}

	private int light;
	private int lightLimit = 8;
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/torch.png");

	@Override
	public void drawBackground(GuiVehicle gui, int x, int y) {
		ResourceHelper.bindResource(TEXTURE);
		int barLength = 3 * light;
		if (light == 15) {
			barLength--;
		}
		int srcX = 1;
		if (inRect(x, y, boxRect)) {
			srcX += boxRect[2] + 1;
		}
		drawImage(gui, boxRect, srcX, 1);
		drawImage(gui, 12 + 1, guiHeight() - 10 + 1, 1, 11, barLength, 7);
		drawImage(gui, 12 + 3 * lightLimit, guiHeight() - 10, 1, 19, 1, 9);
	}

	private final int[] boxRect = new int[] { 12, guiHeight() - 10, 46, 9 };

	@Override
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		drawStringOnMouseOver(gui, LocalizationCartRails.TORCH.translate(String.valueOf(lightLimit), String.valueOf(light)), x, y, boxRect);
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
	protected void checkGuiData(Object[] info) {
		short data = (short) (light & 15);
		data |= (short) ((lightLimit & 15) << 4);
		updateGuiData(info, 0, data);
	}

	@Override
	public void receiveGuiData(int id, short data) {
		if (id == 0) {
			light = data & 15;
			lightLimit = (data & 240) >> 4;
		}
	}

	@Override
	protected void receivePacket(DataReader dr, EntityPlayer player) {
		lightLimit = dr.readByte();
		if (lightLimit < 0) {
			lightLimit = 0;
		} else if (lightLimit > 15) {
			lightLimit = 15;
		}
	}

	boolean markerMoving = false;

	@Override
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) {
		if (button == 0) {
			if (inRect(x, y, boxRect)) {
				generatePacket(x);
				markerMoving = true;
			}
		}
	}

	@Override
	public void mouseMovedOrUp(GuiVehicle gui, int x, int y, int button) {
		if (markerMoving) {
			generatePacket(x);
		}
		if (button != -1) {
			markerMoving = false;
		}
	}

	private void generatePacket(int x) {
		int xInBox = x - boxRect[0];
		int val = xInBox / 3;
		if (val < 0) {
			val = 0;
		} else if (val > 15) {
			val = 15;
		}
		DataWriter dw = getDataWriter();
		dw.writeByte(val);
		sendPacketToServer(dw);
	}

	@Override
	public void update() {
		super.update();
		light = getVehicle().getWorld().getLightFor(EnumSkyBlock.BLOCK, getVehicle().pos().up());
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
		calculateTorches();
	}

	private void calculateTorches() {
		if (getVehicle().getWorld().isRemote) {
			return;
		}
		int val = 0;
		for (int i = 0; i < 3; i++) {
			val |= ((getStack(i) != null ? 1 : 0) << i);
		}
		updateDw(TORCHES, val);
	}

	public int getTorches() {
		if (isPlaceholder()) {
			return getMultiBooleanIntegerSimulationInfo();
		} else {
			return getDw(TORCHES);
		}
	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		tagCompound.setByte("lightLimit", (byte) lightLimit);
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		lightLimit = tagCompound.getByte("lightLimit");
		calculateTorches();
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < getInventorySize(); i++) {
			ItemStack item = getStack(i);
			if (item != null && Block.getBlockFromItem(item.getItem()) == Blocks.TORCH) {
				return true;
			}
		}
		return false;
	}
}
