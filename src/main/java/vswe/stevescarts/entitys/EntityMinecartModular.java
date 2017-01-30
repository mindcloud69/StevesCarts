package vswe.stevescarts.entitys;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.blocks.ModBlocks;
import vswe.stevescarts.blocks.tileentities.TileEntityCartAssembler;
import vswe.stevescarts.containers.ContainerMinecart;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.ActivatorOption;
import vswe.stevescarts.helpers.CartVersion;
import vswe.stevescarts.helpers.DetectorType;
import vswe.stevescarts.helpers.GuiAllocationHelper;
import vswe.stevescarts.helpers.ModuleCountPair;
import vswe.stevescarts.helpers.storages.TransferHandler;
import vswe.stevescarts.items.ModItems;
import vswe.stevescarts.models.ModelCartbase;
import vswe.stevescarts.modules.IActivatorModule;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.addons.ModuleCreativeSupplies;
import vswe.stevescarts.modules.data.ModuleData;
import vswe.stevescarts.modules.engines.ModuleEngine;
import vswe.stevescarts.modules.storages.chests.ModuleChest;
import vswe.stevescarts.modules.storages.tanks.ModuleTank;
import vswe.stevescarts.modules.workers.CompWorkModule;
import vswe.stevescarts.modules.workers.ModuleWorker;
import vswe.stevescarts.modules.workers.tools.ModuleTool;

public class EntityMinecartModular extends EntityMinecart implements IInventory, IEntityAdditionalSpawnData, IFluidHandler {

	public BlockPos disabledPos;
	protected boolean wasDisabled;
	public double pushX;
	public double pushZ;
	public double temppushX;
	public double temppushZ;
	protected boolean engineFlag;
	private int motorRotation;
	public boolean cornerFlip;
	private EnumRailDirection fixedRailDirection;
	private BlockPos fixedRailPos;
	private byte[] moduleLoadingData;
	private ForgeChunkManager.Ticket cartTicket;
	private int wrongRender;
	private boolean oldRender;
	private float lastRenderYaw;
	private double lastMotionX;
	private double lastMotionZ;
	private int workingTime;
	private ModuleWorker workingComponent;
	public TileEntityCartAssembler placeholderAsssembler;
	public boolean isPlaceholder;
	public int keepAlive;
	public static final int MODULAR_SPACE_WIDTH = 443;
	public static final int MODULAR_SPACE_HEIGHT = 168;
	protected int modularSpaceHeight;
	public boolean canScrollModules;
	private ArrayList<ModuleCountPair> moduleCounts;
	public static final int[][][] railDirectionCoordinates;
	private ArrayList<ModuleBase> modules;
	private ArrayList<ModuleWorker> workModules;
	private ArrayList<ModuleEngine> engineModules;
	private ArrayList<ModuleTank> tankModules;
	private ModuleCreativeSupplies creativeSupplies;
	public Random rand;
	protected String name;
	public byte cartVersion;
	private int scrollY;
	@SideOnly(Side.CLIENT)
	private MovingSound sound;
	@SideOnly(Side.CLIENT)
	private MovingSound soundRiding;
	@SideOnly(Side.CLIENT)
	private int keepSilent;

	private static final DataParameter<Boolean> IS_BURNING = EntityDataManager.createKey(EntityMinecartModular.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_DISANABLED = EntityDataManager.createKey(EntityMinecartModular.class, DataSerializers.BOOLEAN);

	public ArrayList<ModuleBase> getModules() {
		return this.modules;
	}

	public ArrayList<ModuleWorker> getWorkers() {
		return this.workModules;
	}

	public ArrayList<ModuleEngine> getEngines() {
		return this.engineModules;
	}

	public ArrayList<ModuleTank> getTanks() {
		return this.tankModules;
	}

	public ArrayList<ModuleCountPair> getModuleCounts() {
		return this.moduleCounts;
	}

	public EntityMinecartModular(final World world, final double x, final double y, final double z, final NBTTagCompound info, final String name) {
		super(world, x, y, z);
		this.engineFlag = false;
		this.fixedRailDirection = null;
		this.rand = new Random();
		this.cartVersion = info.getByte("CartVersion");
		this.loadModules(info);
		this.name = name;
		for (int i = 0; i < this.modules.size(); ++i) {
			if (this.modules.get(i).hasExtraData() && info.hasKey("Data" + i)) {
				this.modules.get(i).setExtraData(info.getByte("Data" + i));
			}
		}
	}

	public EntityMinecartModular(final World world) {
		super(world);
		this.engineFlag = false;
		this.fixedRailDirection = null;
		this.rand = new Random();
	}

	public EntityMinecartModular(final World world, final TileEntityCartAssembler assembler, final byte[] data) {
		this(world);
		this.setPlaceholder(assembler);
		this.loadPlaceholderModules(data);
	}

	private void overrideDatawatcher() {
		this.dataManager = new EntityDataManagerLockable(this);
	}

	private void loadPlaceholderModules(final byte[] data) {
		if (this.modules == null) {
			this.modules = new ArrayList<>();
			this.doLoadModules(data);
		} else {
			final ArrayList<Byte> modulesToAdd = new ArrayList<>();
			final ArrayList<Byte> oldModules = new ArrayList<>();
			for (int i = 0; i < this.moduleLoadingData.length; ++i) {
				oldModules.add(this.moduleLoadingData[i]);
			}
			for (int i = 0; i < data.length; ++i) {
				boolean found = false;
				for (int j = 0; j < oldModules.size(); ++j) {
					if (data[i] == oldModules.get(j)) {
						found = true;
						oldModules.remove(j);
						break;
					}
				}
				if (!found) {
					modulesToAdd.add(data[i]);
				}
			}
			for (final byte id : oldModules) {
				for (int k = 0; k < this.modules.size(); ++k) {
					if (id == this.modules.get(k).getModuleId()) {
						this.modules.remove(k);
						break;
					}
				}
			}
			final byte[] newModuleData = new byte[modulesToAdd.size()];
			for (int l = 0; l < modulesToAdd.size(); ++l) {
				newModuleData[l] = modulesToAdd.get(l);
			}
			this.doLoadModules(newModuleData);
		}
		this.initModules();
		this.moduleLoadingData = data;
	}

	private void loadModules(final NBTTagCompound info) {
		final NBTTagByteArray moduleIDTag = (NBTTagByteArray) info.getTag("Modules");
		if (moduleIDTag == null) {
			return;
		}
		if (this.world.isRemote) {
			this.moduleLoadingData = moduleIDTag.getByteArray();
		} else {
			this.moduleLoadingData = CartVersion.updateCart(this, moduleIDTag.getByteArray());
		}
		this.loadModules(this.moduleLoadingData);
	}

	public void updateSimulationModules(final byte[] bytes) {
		if (!this.isPlaceholder) {
			System.out.println("You're stupid! This is not a placeholder cart.");
		} else {
			this.loadPlaceholderModules(bytes);
		}
	}

	protected void loadModules(final byte[] bytes) {
		this.modules = new ArrayList<>();
		this.doLoadModules(bytes);
		this.initModules();
	}

	private void doLoadModules(final byte[] bytes) {
		for (final byte id : bytes) {
			try {
				final Class<? extends ModuleBase> moduleClass = ModuleData.getList().get(id).getModuleClass();
				final Constructor moduleConstructor = moduleClass.getConstructor(EntityMinecartModular.class);
				final Object moduleObject = moduleConstructor.newInstance(this);
				final ModuleBase module = (ModuleBase) moduleObject;
				module.setModuleId(id);
				this.modules.add(module);
			} catch (Exception e) {
				System.out.println("Failed to load module with ID " + id + "! More info below.");
				e.printStackTrace();
			}
		}
	}

	private void initModules() {
		this.moduleCounts = new ArrayList<>();
		for (final ModuleBase module : this.modules) {
			final ModuleData data = ModuleData.getList().get(module.getModuleId());
			boolean found = false;
			for (final ModuleCountPair count : this.moduleCounts) {
				if (count.isContainingData(data)) {
					count.increase();
					found = true;
					break;
				}
			}
			if (!found) {
				this.moduleCounts.add(new ModuleCountPair(data));
			}
		}
		for (final ModuleBase module : this.modules) {
			module.preInit();
		}
		this.workModules = new ArrayList<>();
		this.engineModules = new ArrayList<>();
		this.tankModules = new ArrayList<>();
		final int x = 0;
		final int y = 0;
		final int maxH = 0;
		int guidata = 0;
		int packets = 0;
		if (this.world.isRemote) {
			this.generateModels();
		}
		for (final ModuleBase module2 : this.modules) {
			if (module2 instanceof ModuleWorker) {
				this.workModules.add((ModuleWorker) module2);
			} else if (module2 instanceof ModuleEngine) {
				this.engineModules.add((ModuleEngine) module2);
			} else if (module2 instanceof ModuleTank) {
				this.tankModules.add((ModuleTank) module2);
			} else {
				if (!(module2 instanceof ModuleCreativeSupplies)) {
					continue;
				}
				this.creativeSupplies = (ModuleCreativeSupplies) module2;
			}
		}
		final CompWorkModule sorter = new CompWorkModule();
		Collections.sort(this.workModules, sorter);
		if (!this.isPlaceholder) {
			final ArrayList<GuiAllocationHelper> lines = new ArrayList<>();
			int slots = 0;
			for (final ModuleBase module3 : this.modules) {
				if (module3.hasGui()) {
					boolean foundLine = false;
					for (final GuiAllocationHelper line : lines) {
						if (line.width + module3.guiWidth() <= 443) {
							module3.setX(line.width);
							final GuiAllocationHelper guiAllocationHelper = line;
							guiAllocationHelper.width += module3.guiWidth();
							line.maxHeight = Math.max(line.maxHeight, module3.guiHeight());
							line.modules.add(module3);
							foundLine = true;
							break;
						}
					}
					if (!foundLine) {
						final GuiAllocationHelper line2 = new GuiAllocationHelper();
						module3.setX(0);
						line2.width = module3.guiWidth();
						line2.maxHeight = module3.guiHeight();
						line2.modules.add(module3);
						lines.add(line2);
					}
					module3.setGuiDataStart(guidata);
					guidata += module3.numberOfGuiData();
					if (module3.hasSlots()) {
						slots = module3.generateSlots(slots);
					}
				}
				if (module3.numberOfDataWatchers() > 0) {
					module3.initDw();
				}
				module3.setPacketStart(packets);
				packets += module3.totalNumberOfPackets();
			}
			int currentY = 0;
			for (final GuiAllocationHelper line3 : lines) {
				for (final ModuleBase module4 : line3.modules) {
					module4.setY(currentY);
				}
				currentY += line3.maxHeight;
			}
			if (currentY > 168) {
				this.canScrollModules = true;
			}
			this.modularSpaceHeight = currentY;
		}
		for (final ModuleBase module5 : this.modules) {
			module5.init();
		}
	}

	@Override
	public void setDead() {
		if (this.world.isRemote) {
			for (int var1 = 0; var1 < this.getSizeInventory(); ++var1) {
				this.setInventorySlotContents(var1, null);
			}
		}
		super.setDead();
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				module.onDeath();
			}
		}
		this.dropChunkLoading();
	}

	@SideOnly(Side.CLIENT)
	public void renderOverlay(final Minecraft minecraft) {
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				module.renderOverlay(minecraft);
			}
		}
	}

	@Override
	protected void entityInit() {
		if(this.world.isRemote && !(dataManager instanceof EntityDataManagerLockable)){
			this.overrideDatawatcher();
		}
		super.entityInit();
		dataManager.register(IS_BURNING, false);
		dataManager.register(IS_DISANABLED, false);
	}

	public void updateFuel() {
		final int consumption = this.getConsumption();
		if (consumption > 0) {
			final ModuleEngine engine = this.getCurrentEngine();
			if (engine != null) {
				engine.consumeFuel(consumption);
				if (!this.isPlaceholder && this.world.isRemote && this.hasFuel() && !this.isDisabled()) {
					engine.smoke();
				}
			}
		}
		if (this.hasFuel()) {
			if (!this.engineFlag) {
				this.pushX = this.temppushX;
				this.pushZ = this.temppushZ;
			}
		} else if (this.engineFlag) {
			this.temppushX = this.pushX;
			this.temppushZ = this.pushZ;
			final double n = 0.0;
			this.pushZ = n;
			this.pushX = n;
		}
		this.setEngineBurning(this.hasFuel() && !this.isDisabled());
	}

	public boolean isEngineBurning() {
		return dataManager.get(IS_BURNING);
	}

	public void setEngineBurning(final boolean on) {
		dataManager.set(IS_BURNING, on);
	}

	private ModuleEngine getCurrentEngine() {
		if (this.modules == null) {
			return null;
		}
		for (final ModuleBase module : this.modules) {
			if (module.stopEngines()) {
				return null;
			}
		}
		final int consumption = this.getConsumption(true);
		final ArrayList<ModuleEngine> priority = new ArrayList<>();
		int mostImportant = -1;
		for (final ModuleEngine engine : this.engineModules) {
			if (engine.hasFuel(consumption) && (mostImportant == -1 || mostImportant >= engine.getPriority())) {
				if (engine.getPriority() < mostImportant) {
					priority.clear();
				}
				mostImportant = engine.getPriority();
				priority.add(engine);
			}
		}
		if (priority.size() > 0) {
			if (this.motorRotation >= priority.size()) {
				this.motorRotation = 0;
			}
			this.motorRotation = (this.motorRotation + 1) % priority.size();
			return priority.get(this.motorRotation);
		}
		return null;
	}

	public int getConsumption() {
		return this.getConsumption(!this.isDisabled() && this.isEngineBurning());
	}

	public int getConsumption(final boolean isMoving) {
		int consumption = isMoving ? 1 : 0;
		if (this.modules != null && !this.isPlaceholder) {
			for (final ModuleBase module : this.modules) {
				consumption += module.getConsumption(isMoving);
			}
		}
		return consumption;
	}

	@Override
	public float getEyeHeight() {
		return 0.9f;
	}

	@Override
	public double getMountedYOffset() {
		if (this.modules != null && !getPassengers().isEmpty()) {
			for (final ModuleBase module : this.modules) {
				final float offset = module.mountedOffset(getPassengers().get(0));
				if (offset != 0.0f) {
					return offset;
				}
			}
		}
		return super.getMountedYOffset();
	}

	@Override
	public ItemStack getCartItem() {
		if (this.modules != null) {
			final ItemStack cart = ModuleData.createModularCart(this);
			if (this.name != null && !this.name.equals("") && !this.name.equals(ModItems.carts.getName())) {
				cart.setStackDisplayName(this.name);
			}
			return cart;
		}
		return new ItemStack(ModItems.carts);
	}

	@Override
	public void killMinecart(final DamageSource dmg) {
		this.setDead();
		if (this.dropOnDeath()) {
			this.entityDropItem(this.getCartItem(), 0.0f);
			for (int i = 0; i < this.getSizeInventory(); ++i) {
				final ItemStack itemstack = this.getStackInSlot(i);
				if (itemstack != null) {
					final float f = this.rand.nextFloat() * 0.8f + 0.1f;
					final float f2 = this.rand.nextFloat() * 0.8f + 0.1f;
					final float f3 = this.rand.nextFloat() * 0.8f + 0.1f;
					while (itemstack.stackSize > 0) {
						int j = this.rand.nextInt(21) + 10;
						if (j > itemstack.stackSize) {
							j = itemstack.stackSize;
						}
						final ItemStack itemStack = itemstack;
						itemStack.stackSize -= j;
						final EntityItem entityitem = new EntityItem(this.world, this.posX + f, this.posY + f2, this.posZ + f3, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));
						final float f4 = 0.05f;
						entityitem.motionX = (float) this.rand.nextGaussian() * f4;
						entityitem.motionY = (float) this.rand.nextGaussian() * f4 + 0.2f;
						entityitem.motionZ = (float) this.rand.nextGaussian() * f4;
						this.world.spawnEntity(entityitem);
					}
				}
			}
		}
	}

	public boolean dropOnDeath() {
		if (this.isPlaceholder) {
			return false;
		}
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				if (!module.dropOnDeath()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public float getMaxCartSpeedOnRail() {
		float maxSpeed = super.getMaxCartSpeedOnRail();
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				final float tempMax = module.getMaxSpeed();
				if (tempMax < maxSpeed) {
					maxSpeed = tempMax;
				}
			}
		}
		return maxSpeed;
	}

	@Override
	public boolean isPoweredCart() {
		return this.engineModules.size() > 0;
	}

	public int getDefaultDisplayTileData() {
		return -1;
	}

	public int getMinecartType() {
		return -1;
	}

	public float[] getColor() {
		if (this.modules != null) {
			for (final ModuleBase module : this.getModules()) {
				final float[] color = module.getColor();
				if (color[0] != 1.0f || color[1] != 1.0f || color[2] != 1.0f) {
					return color;
				}
			}
		}
		return new float[] { 1.0f, 1.0f, 1.0f };
	}

	public int getYTarget() {
		if (this.modules != null) {
			for (final ModuleBase module : this.getModules()) {
				final int yTarget = module.getYTarget();
				if (yTarget != -1) {
					return yTarget;
				}
			}
		}
		return (int) this.posY;
	}

	public ModuleBase getInterfaceThief() {
		if (this.modules != null) {
			for (final ModuleBase module : this.getModules()) {
				if (module.doStealInterface()) {
					return module;
				}
			}
		}
		return null;
	}

	@Override
	public boolean attackEntityFrom(final DamageSource dmg, final float par2) {
		if (this.isPlaceholder) {
			return false;
		}
		if (this.modules != null) {
			for (final ModuleBase module : this.getModules()) {
				if (!module.receiveDamage(dmg, par2)) {
					return false;
				}
			}
		}
		return super.attackEntityFrom(dmg, par2);
	}

	@Override
	public void onActivatorRailPass(final int x, final int y, final int z, final boolean active) {
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				module.activatedByRail(x, y, z, active);
			}
		}
	}


	@Override
	public void moveMinecartOnRail(BlockPos pos) {
		super.moveMinecartOnRail(pos);
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				module.moveMinecartOnRail(pos);
			}
		}
		IBlockState blockState = world.getBlockState(pos);
		IBlockState stateBelow = world.getBlockState(pos.down());
		int metaBelow = stateBelow.getBlock().getMetaFromState(stateBelow);
		EnumRailDirection railDirection = ((BlockRailBase) blockState.getBlock()).getRailDirection(world, pos, blockState, this);
		this.cornerFlip = ((railDirection == EnumRailDirection.SOUTH_EAST || railDirection == EnumRailDirection.SOUTH_WEST) && this.motionX < 0.0)
				|| ((railDirection == EnumRailDirection.NORTH_EAST || railDirection == EnumRailDirection.NORTH_WEST) && this.motionX > 0.0);
		if (blockState.getBlock() != ModBlocks.ADVANCED_DETECTOR.getBlock() && this.isDisabled()) {
			this.releaseCart();
		}
		boolean canBeDisabled = blockState.getBlock() == ModBlocks.ADVANCED_DETECTOR.getBlock()
				&& (stateBelow.getBlock() != ModBlocks.DETECTOR_UNIT.getBlock() || !DetectorType.getTypeFromSate(stateBelow).canInteractWithCart() || DetectorType.getTypeFromSate(stateBelow).shouldStopCart());
		final boolean forceUnDisable = this.wasDisabled && disabledPos != null && this.disabledPos.equals(pos);
		if (!forceUnDisable && this.wasDisabled) {
			this.wasDisabled = false;
		}
		canBeDisabled = (!forceUnDisable && canBeDisabled);
		if (canBeDisabled && !this.isDisabled()) {
			this.setIsDisabled(true);
			if (this.pushX != 0.0 || this.pushZ != 0.0) {
				this.temppushX = this.pushX;
				this.temppushZ = this.pushZ;
				final double n = 0.0;
				this.pushZ = n;
				this.pushX = n;
			}
			this.disabledPos = new BlockPos(pos);
		}
		if (fixedRailPos != null && !fixedRailPos.equals(pos)) {
			this.fixedRailDirection = null;
			fixedRailPos = new BlockPos(fixedRailPos.getX(), -1, fixedRailPos.getZ());
		}
	}

	public EnumRailDirection getRailDirection(final BlockPos pos) {
		ModuleBase.RAILDIRECTION dir = ModuleBase.RAILDIRECTION.DEFAULT;
		for (final ModuleBase module : this.getModules()) {
			dir = module.getSpecialRailDirection(pos);
			if (dir != ModuleBase.RAILDIRECTION.DEFAULT) {
				break;
			}
		}
		if (dir == ModuleBase.RAILDIRECTION.DEFAULT) {
			return null;
		}
		int Yaw = (int) (this.rotationYaw % 180.0f);
		if (Yaw < 0) {
			Yaw += 180;
		}
		final boolean flag = Yaw >= 45 && Yaw <= 135;
		if (this.fixedRailDirection == null) {
			switch (dir) {
				case FORWARD: {
					if (flag) {
						this.fixedRailDirection = EnumRailDirection.NORTH_SOUTH;
						break;
					}
					this.fixedRailDirection = EnumRailDirection.EAST_WEST;
					break;
				}
				case LEFT: {
					if (flag) {
						if (this.motionZ > 0.0) {
							this.fixedRailDirection = EnumRailDirection.NORTH_EAST;
							break;
						}
						if (this.motionZ <= 0.0) {
							this.fixedRailDirection = EnumRailDirection.SOUTH_WEST;
							break;
						}
						break;
					} else {
						if (this.motionX > 0.0) {
							this.fixedRailDirection = EnumRailDirection.NORTH_WEST;
							break;
						}
						if (this.motionX < 0.0) {
							this.fixedRailDirection = EnumRailDirection.SOUTH_EAST;
							break;
						}
						break;
					}
				}
				case RIGHT: {
					if (flag) {
						if (this.motionZ > 0.0) {
							this.fixedRailDirection = EnumRailDirection.NORTH_WEST;
							break;
						}
						if (this.motionZ <= 0.0) {
							this.fixedRailDirection = EnumRailDirection.SOUTH_EAST;
							break;
						}
						break;
					} else {
						if (this.motionX > 0.0) {
							this.fixedRailDirection = EnumRailDirection.SOUTH_WEST;
							break;
						}
						if (this.motionX < 0.0) {
							this.fixedRailDirection = EnumRailDirection.NORTH_EAST;
							break;
						}
						break;
					}
				}
				case NORTH: {
					if (flag) {
						if (this.motionZ > 0.0) {
							this.fixedRailDirection = EnumRailDirection.NORTH_SOUTH;
							break;
						}
						break;
					} else {
						if (this.motionX > 0.0) {
							this.fixedRailDirection = EnumRailDirection.SOUTH_WEST;
							break;
						}
						if (this.motionX < 0.0) {
							this.fixedRailDirection = EnumRailDirection.SOUTH_EAST;
							break;
						}
						break;
					}
				}
				default: {
					this.fixedRailDirection = null;
					break;
				}
			}
			if (this.fixedRailDirection == null) {
				return null;
			}
			fixedRailPos = new BlockPos(pos);
		}
		return this.fixedRailDirection;
	}

	public void resetRailDirection() {
		this.fixedRailDirection = null;
	}

	public void turnback() {
		this.pushX *= -1.0;
		this.pushZ *= -1.0;
		this.temppushX *= -1.0;
		this.temppushZ *= -1.0;
		this.motionX *= -1.0;
		this.motionY *= -1.0;
		this.motionZ *= -1.0;
	}

	public void releaseCart() {
		this.wasDisabled = true;
		this.setIsDisabled(false);
		this.pushX = this.temppushX;
		this.pushZ = this.temppushZ;
	}

	@Override
	public void markDirty() {
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				module.onInventoryChanged();
			}
		}
	}

	@Override
	public int getSizeInventory() {
		int slotCount = 0;
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				slotCount += module.getInventorySize();
			}
		}
		return slotCount;
	}

	@Override
	protected void moveAlongTrack(BlockPos pos, IBlockState state) {
		if (!getPassengers().isEmpty()) {
			Entity riddenByEntity = getPassengers().get(0);
			if (riddenByEntity instanceof EntityLivingBase) {
				final float move = ((EntityLivingBase) riddenByEntity).moveForward;
				((EntityLivingBase) riddenByEntity).moveForward = 0.0f;
				super.moveAlongTrack(pos, state);
				((EntityLivingBase) riddenByEntity).moveForward = move;
			} else {
				super.moveAlongTrack(pos, state);
			}
		} else {
			super.moveAlongTrack(pos, state);
		}
		double d2 = this.pushX * this.pushX + this.pushZ * this.pushZ;
		if (d2 > 1.0E-4 && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001) {
			d2 = MathHelper.sqrt(d2);
			this.pushX /= d2;
			this.pushZ /= d2;
			if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0) {
				this.pushX = 0.0;
				this.pushZ = 0.0;
			} else {
				this.pushX = this.motionX;
				this.pushZ = this.motionZ;
			}
		}
	}

	@Override
	protected void applyDrag() {
		double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
		this.engineFlag = (d0 > 1.0E-4);
		if (this.isDisabled()) {
			this.motionX = 0.0;
			this.motionY = 0.0;
			this.motionZ = 0.0;
		} else if (this.engineFlag) {
			d0 = MathHelper.sqrt(d0);
			this.pushX /= d0;
			this.pushZ /= d0;
			final double d2 = this.getPushFactor();
			this.motionX *= 0.800000011920929;
			this.motionY *= 0.0;
			this.motionZ *= 0.800000011920929;
			this.motionX += this.pushX * d2;
			this.motionZ += this.pushZ * d2;
		} else {
			this.motionX *= 0.9800000190734863;
			this.motionY *= 0.0;
			this.motionZ *= 0.9800000190734863;
		}
		super.applyDrag();
	}

	protected double getPushFactor() {
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				final double factor = module.getPushFactor();
				if (factor >= 0.0) {
					return factor;
				}
			}
		}
		return 0.05;
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setString("cartName", this.name);
		tagCompound.setDouble("pushX", this.pushX);
		tagCompound.setDouble("pushZ", this.pushZ);
		tagCompound.setDouble("temppushX", this.temppushX);
		tagCompound.setDouble("temppushZ", this.temppushZ);
		tagCompound.setShort("workingTime", (short) this.workingTime);
		tagCompound.setByteArray("Modules", this.moduleLoadingData);
		tagCompound.setByte("CartVersion", this.cartVersion);
		if (this.modules != null) {
			for (int i = 0; i < this.modules.size(); ++i) {
				final ModuleBase module = this.modules.get(i);
				module.writeToNBT(tagCompound, i);
			}
		}
		return tagCompound;
	}

	@Override
	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		this.name = tagCompound.getString("cartName");
		this.pushX = tagCompound.getDouble("pushX");
		this.pushZ = tagCompound.getDouble("pushZ");
		this.temppushX = tagCompound.getDouble("temppushX");
		this.temppushZ = tagCompound.getDouble("temppushZ");
		this.workingTime = tagCompound.getShort("workingTime");
		this.cartVersion = tagCompound.getByte("CartVersion");
		final int oldVersion = this.cartVersion;
		this.loadModules(tagCompound);
		if (this.modules != null) {
			for (int i = 0; i < this.modules.size(); ++i) {
				final ModuleBase module = this.modules.get(i);
				module.readFromNBT(tagCompound, i);
			}
		}
		if (oldVersion < 2) {
			int newSlot = -1;
			int slotCount = 0;
			for (final ModuleBase module2 : this.modules) {
				if (module2 instanceof ModuleTool) {
					newSlot = slotCount;
					break;
				}
				slotCount += module2.getInventorySize();
			}
			if (newSlot != -1) {
				ItemStack lastitem = null;
				for (int j = newSlot; j < this.getSizeInventory(); ++j) {
					final ItemStack thisitem = this.getStackInSlot(j);
					this.setInventorySlotContents(j, lastitem);
					lastitem = thisitem;
				}
			}
		}
	}

	public boolean isDisabled() {
		return dataManager.get(IS_DISANABLED);
	}

	public void setIsDisabled(final boolean disabled) {
		dataManager.set(IS_DISANABLED, disabled);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.onCartUpdate();
		if (this.world.isRemote) {
			this.updateSounds();
		}
	}

	public void onCartUpdate() {
		if (this.modules != null) {
			this.updateFuel();
			for (final ModuleBase module : this.modules) {
				module.update();
			}
			for (final ModuleBase module : this.modules) {
				module.postUpdate();
			}
			this.work();
			this.setCurrentCartSpeedCapOnRail(this.getMaxCartSpeedOnRail());
		}
		if (this.isPlaceholder && this.keepAlive++ > 20) {
			this.kill();
			this.placeholderAsssembler.resetPlaceholder();
		}
	}

	public boolean hasFuel() {
		if (this.isDisabled()) {
			return false;
		}
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				if (module.stopEngines()) {
					return false;
				}
			}
		}
		return this.hasFuelForModule();
	}

	public boolean hasFuelForModule() {
		if (this.isPlaceholder) {
			return true;
		}
		final int consumption = this.getConsumption(true);
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				if (module.hasFuel(consumption)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isUsableByPlayer(final EntityPlayer entityplayer) {
		return entityplayer.getDistanceSq(this.x(), this.y(), this.z()) <= 64.0;
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer entityplayer,
			Vec3d vec,
			@Nullable
			ItemStack stack,
			EnumHand hand) {
		if(MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, entityplayer, stack, hand))){
			return EnumActionResult.SUCCESS;
		}
		if (this.isPlaceholder) {
			return EnumActionResult.FAIL;
		}
		if (this.modules != null && !entityplayer.isSneaking()) {
			boolean interupt = false;
			for (final ModuleBase module : this.modules) {
				if (module.onInteractFirst(entityplayer)) {
					interupt = true;
				}
			}
			if (interupt) {
				return EnumActionResult.SUCCESS;
			}
		}
		if (!this.world.isRemote) {
			if (!this.isDisabled() && !isPassenger(entityplayer)) {
				this.temppushX = this.posX - entityplayer.posX;
				this.temppushZ = this.posZ - entityplayer.posZ;
			}
			if (!this.isDisabled() && this.hasFuel() && this.pushX == 0.0 && this.pushZ == 0.0) {
				this.pushX = this.temppushX;
				this.pushZ = this.temppushZ;
			}
			FMLNetworkHandler.openGui(entityplayer, StevesCarts.instance, 0, this.world, this.getEntityId(), 0, 0);
			this.openInventory(entityplayer);
		}
		return EnumActionResult.SUCCESS;
	}


	public void loadChunks() {
		this.loadChunks(this.cartTicket, this.x() >> 4, this.z() >> 4);
	}

	public void loadChunks(final int chunkX, final int chunkZ) {
		this.loadChunks(this.cartTicket, chunkX, chunkZ);
	}

	public void loadChunks(final ForgeChunkManager.Ticket ticket) {
		this.loadChunks(ticket, this.x() >> 4, this.z() >> 4);
	}

	public void loadChunks(final ForgeChunkManager.Ticket ticket, final int chunkX, final int chunkZ) {
		if (this.world.isRemote || ticket == null) {
			return;
		}
		if (this.cartTicket == null) {
			this.cartTicket = ticket;
		}
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				ForgeChunkManager.forceChunk(ticket, new ChunkPos(chunkX + i, chunkZ + j));
			}
		}
	}

	public void initChunkLoading() {
		if (this.world.isRemote || this.cartTicket != null) {
			return;
		}
		this.cartTicket = ForgeChunkManager.requestTicket(StevesCarts.instance, this.world, ForgeChunkManager.Type.ENTITY);
		if (this.cartTicket != null) {
			this.cartTicket.bindEntity(this);
			this.cartTicket.setChunkListDepth(9);
			this.loadChunks();
		}
	}

	public void dropChunkLoading() {
		if (this.world.isRemote) {
			return;
		}
		if (this.cartTicket != null) {
			ForgeChunkManager.releaseTicket(this.cartTicket);
			this.cartTicket = null;
		}
	}

	public void setWorker(final ModuleWorker worker) {
		if (this.workingComponent != null && worker != null) {
			this.workingComponent.stopWorking();
		}
		if ((this.workingComponent = worker) == null) {
			this.setWorkingTime(0);
		}
	}

	public ModuleWorker getWorker() {
		return this.workingComponent;
	}

	public void setWorkingTime(final int val) {
		this.workingTime = val;
	}

	private void work() {
		if (this.isPlaceholder) {
			return;
		}
		if (!this.world.isRemote && this.hasFuel()) {
			if (this.workingTime <= 0) {
				final ModuleWorker oldComponent = this.workingComponent;
				if (this.workingComponent != null) {
					final boolean result = this.workingComponent.work();
					if (this.workingComponent != null && oldComponent == this.workingComponent && this.workingTime <= 0 && !this.workingComponent.preventAutoShutdown()) {
						this.workingComponent.stopWorking();
					}
					if (result) {
						this.work();
						return;
					}
				}
				if (this.workModules != null) {
					for (final ModuleWorker module : this.workModules) {
						if (module.work()) {
							return;
						}
					}
				}
			} else {
				--this.workingTime;
			}
		}
	}

	public void handleActivator(final ActivatorOption option, final boolean isOrange) {
		for (final ModuleBase module : this.modules) {
			if (module instanceof IActivatorModule && option.getModule().isAssignableFrom(module.getClass())) {
				final IActivatorModule iactivator = (IActivatorModule) module;
				if (option.shouldActivate(isOrange)) {
					iactivator.doActivate(option.getId());
				} else if (option.shouldDeactivate(isOrange)) {
					iactivator.doDeActivate(option.getId());
				} else {
					if (!option.shouldToggle()) {
						continue;
					}
					if (iactivator.isActive(option.getId())) {
						iactivator.doDeActivate(option.getId());
					} else {
						iactivator.doActivate(option.getId());
					}
				}
			}
		}
	}

	public boolean getRenderFlippedYaw(float yaw) {
		yaw %= 360.0f;
		if (yaw < 0.0f) {
			yaw += 360.0f;
		}
		if (!this.oldRender || Math.abs(yaw - this.lastRenderYaw) < 90.0f || Math.abs(yaw - this.lastRenderYaw) > 270.0f || (this.motionX > 0.0 && this.lastMotionX < 0.0) || (this.motionZ > 0.0 && this.lastMotionZ < 0.0)
				|| (this.motionX < 0.0 && this.lastMotionX > 0.0) || (this.motionZ < 0.0 && this.lastMotionZ > 0.0) || this.wrongRender >= 50) {
			this.lastMotionX = this.motionX;
			this.lastMotionZ = this.motionZ;
			this.lastRenderYaw = yaw;
			this.oldRender = true;
			this.wrongRender = 0;
			return false;
		}
		++this.wrongRender;
		return true;
	}

	public ArrayList<String> getLabel() {
		final ArrayList<String> label = new ArrayList<>();
		if (this.getModules() != null) {
			for (final ModuleBase module : this.getModules()) {
				module.addToLabel(label);
			}
		}
		return label;
	}

	public int x() {
		return MathHelper.floor(this.posX);
	}

	public int y() {
		return MathHelper.floor(this.posY);
	}

	public int z() {
		return MathHelper.floor(this.posZ);
	}

	public void addItemToChest(final ItemStack iStack) {
		TransferHandler.TransferItem(iStack, this, this.getCon(null), Slot.class, null, -1);
	}

	public void addItemToChest(final ItemStack iStack, final int start, final int end) {
		TransferHandler.TransferItem(iStack, this, start, end, this.getCon(null), Slot.class, null, -1);
	}

	public void addItemToChest(final ItemStack iStack, final Class validSlot, final Class invalidSlot) {
		TransferHandler.TransferItem(iStack, this, this.getCon(null), validSlot, invalidSlot, -1);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (this.getStackInSlot(index) != null) {
			final ItemStack var2 = this.getStackInSlot(index);
			this.setInventorySlotContents(index, null);
			return var2;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				if (i < module.getInventorySize()) {
					return module.getStack(i);
				}
				i -= module.getInventorySize();
			}
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, final ItemStack item) {
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				if (i < module.getInventorySize()) {
					module.setStack(i, item);
					break;
				}
				i -= module.getInventorySize();
			}
		}
	}

	@Override
	public ItemStack decrStackSize(final int i, final int n) {
		if (this.modules == null) {
			return null;
		}
		if (this.getStackInSlot(i) == null) {
			return null;
		}
		if (this.getStackInSlot(i).stackSize <= n) {
			final ItemStack item = this.getStackInSlot(i);
			this.setInventorySlotContents(i, null);
			return item;
		}
		final ItemStack item = this.getStackInSlot(i).splitStack(n);
		if (this.getStackInSlot(i).stackSize == 0) {
			this.setInventorySlotContents(i, null);
		}
		return item;
	}

	public Container getCon(final InventoryPlayer player) {
		return new ContainerMinecart(player, this);
	}

	@Override
	public void openInventory(EntityPlayer player) {
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				if (module instanceof ModuleChest) {
					((ModuleChest) module).openChest();
				}
			}
		}
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				if (module instanceof ModuleChest) {
					((ModuleChest) module).closeChest();
				}
			}
		}
	}

	public void setPlaceholder(final TileEntityCartAssembler assembler) {
		this.isPlaceholder = true;
		this.placeholderAsssembler = assembler;
	}

	@Override
	public AxisAlignedBB getEntityBoundingBox() {
		if (this.isPlaceholder) {
			return null;
		}
		return super.getEntityBoundingBox();
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isPlaceholder && super.canBeCollidedWith();
	}

	@Override
	public boolean canBePushed() {
		return !this.isPlaceholder && super.canBePushed();
	}

	@SideOnly(Side.CLIENT)
	private void generateModels() {
		if (this.modules != null) {
			final ArrayList<String> invalid = new ArrayList<>();
			for (final ModuleBase module : this.modules) {
				final ModuleData data = module.getData();
				if (data.haveRemovedModels()) {
					for (final String remove : data.getRemovedModels()) {
						invalid.add(remove);
					}
				}
			}
			for (int i = this.modules.size() - 1; i >= 0; --i) {
				final ModuleBase module = this.modules.get(i);
				final ModuleData data = module.getData();
				if (data != null && data.haveModels(this.isPlaceholder)) {
					final ArrayList<ModelCartbase> models = new ArrayList<>();
					for (final String str : data.getModels(this.isPlaceholder).keySet()) {
						if (!invalid.contains(str)) {
							models.add(data.getModels(this.isPlaceholder).get(str));
							invalid.add(str);
						}
					}
					if (models.size() > 0) {
						module.setModels(models);
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(final EntityPlayer player) {
		return new GuiMinecart(player.inventory, this);
	}

	@Override
	public void writeSpawnData(final ByteBuf data) {
		data.writeByte(this.moduleLoadingData.length);
		for (final byte b : this.moduleLoadingData) {
			data.writeByte(b);
		}
		data.writeByte(this.name.getBytes().length);
		for (final byte b : this.name.getBytes()) {
			data.writeByte(b);
		}
	}

	@Override
	public void readSpawnData(final ByteBuf data) {
		final byte length = data.readByte();
		final byte[] bytes = new byte[length];
		data.readBytes(bytes);
		this.loadModules(bytes);
		final int nameLength = data.readByte();
		final byte[] nameBytes = new byte[nameLength];
		for (int i = 0; i < nameLength; ++i) {
			nameBytes[i] = data.readByte();
		}
		this.name = new String(nameBytes);
		if (getDataManager() instanceof EntityDataManagerLockable) {
			((EntityDataManagerLockable) getDataManager()).release();
		}
	}

	public void setScrollY(final int val) {
		if (this.canScrollModules) {
			this.scrollY = val;
		}
	}

	public int getScrollY() {
		if (this.getInterfaceThief() != null) {
			return 0;
		}
		return this.scrollY;
	}

	public int getRealScrollY() {
		return (int) ((this.modularSpaceHeight - 168) / 198.0f * this.getScrollY());
	}

	@Override
	public int fill(final FluidStack resource, final boolean doFill) {
		int amount = 0;
		if (resource != null && resource.amount > 0) {
			final FluidStack fluid = resource.copy();
			for (int i = 0; i < this.tankModules.size(); ++i) {
				final int tempAmount = this.tankModules.get(i).fill(fluid, doFill);
				amount += tempAmount;
				final FluidStack fluidStack = fluid;
				fluidStack.amount -= tempAmount;
				if (fluid.amount <= 0) {
					break;
				}
			}
		}
		return amount;
	}

	@Override
	public FluidStack drain(final int maxDrain, final boolean doDrain) {
		return this.drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(final FluidStack resource, final boolean doDrain) {
		return this.drain(resource, (resource == null) ? 0 : resource.amount, doDrain);
	}

	private FluidStack drain(final FluidStack resource, int maxDrain, final boolean doDrain) {
		FluidStack ret = resource;
		if (ret != null) {
			ret = ret.copy();
			ret.amount = 0;
		}
		for (int i = 0; i < this.tankModules.size(); ++i) {
			FluidStack temp = null;
			temp = this.tankModules.get(i).drain(maxDrain, doDrain);
			if (temp != null && (ret == null || ret.isFluidEqual(temp))) {
				if (ret == null) {
					ret = temp;
				} else {
					final FluidStack fluidStack = ret;
					fluidStack.amount += temp.amount;
				}
				maxDrain -= temp.amount;
				if (maxDrain <= 0) {
					break;
				}
			}
		}
		if (ret != null && ret.amount == 0) {
			return null;
		}
		return ret;
	}

	public int drain(final Fluid type, int maxDrain, final boolean doDrain) {
		int amount = 0;
		if (type != null && maxDrain > 0) {
			for (final ModuleTank tank : this.tankModules) {
				final FluidStack drained = tank.drain(maxDrain, false);
				if (drained != null && type.equals(drained.getFluid())) {
					amount += drained.amount;
					maxDrain -= drained.amount;
					if (doDrain) {
						tank.drain(drained.amount, true);
					}
					if (maxDrain <= 0) {
						break;
					}
					continue;
				}
			}
		}
		return amount;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		IFluidTankProperties[] ret = new IFluidTankProperties[this.tankModules.size()];
		for (int i = 0; i < ret.length; ++i) {
			ret[i] = new FluidTankProperties(this.tankModules.get(i).getFluid(), this.tankModules.get(i).getCapacity());
		}
		return ret;
	}

	@Override
	public boolean isItemValidForSlot(int slot, final ItemStack item) {
		if (this.modules != null) {
			for (final ModuleBase module : this.modules) {
				if (slot < module.getInventorySize()) {
					return module.getSlots().get(slot).isItemValid(item);
				}
				slot -= module.getInventorySize();
			}
		}
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public String getInventoryName() {
		return "container.modularcart";
	}

	public String getCartName() {
		if (this.name == null || this.name.length() == 0) {
			return "Modular Cart";
		}
		return this.name;
	}

	public boolean hasCreativeSupplies() {
		return this.creativeSupplies != null;
	}

	@Override
	public boolean canRiderInteract() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void setSound(final MovingSound sound, final boolean riding) {
		if (riding) {
			this.soundRiding = sound;
		} else {
			this.sound = sound;
		}
	}

	@SideOnly(Side.CLIENT)
	public void silent() {
		this.keepSilent = 6;
	}

	@SideOnly(Side.CLIENT)
	private void updateSounds() {
		if (this.keepSilent > 1) {
			--this.keepSilent;
			this.stopSound(this.sound);
			this.stopSound(this.soundRiding);
			this.sound = null;
			this.soundRiding = null;
		} else if (this.keepSilent == 1) {
			this.keepSilent = 0;
			Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundMinecart(this));
			Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundMinecartRiding(Minecraft.getMinecraft().player, this));
		}
	}

	@SideOnly(Side.CLIENT)
	private void stopSound(final MovingSound sound) {
		if (sound != null) {
			ReflectionHelper.setPrivateValue(MovingSound.class, sound, true, 0);
		}
	}

	static {
		railDirectionCoordinates = new int[][][] { { { 0, 0, -1 }, { 0, 0, 1 } }, { { -1, 0, 0 }, { 1, 0, 0 } }, { { -1, -1, 0 }, { 1, 0, 0 } }, { { -1, 0, 0 }, { 1, -1, 0 } }, { { 0, 0, -1 }, { 0, -1, 1 } }, { { 0, -1, -1 }, { 0, 0, 1 } },
			{ { 0, 0, 1 }, { 1, 0, 0 } }, { { 0, 0, 1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { 1, 0, 0 } } };
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}

	@Override
	public Type getType() {
		return null;
	}

	public Entity getCartRider(){
		return getPassengers().isEmpty() ? null : getPassengers().get(0);
	}

	@Nullable
	@Override
	public Entity getControllingPassenger() {
		return  null; //Works when returning null, not sure why
	}

	public EntityDataManager getDataManager(){
		return dataManager;
	}

	int base = 0;
	public int getNextDataWatcher(){
		base++;
		return getDataManager().getAll().size() + base + 1;
	}
}
