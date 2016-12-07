package stevesvehicles.api.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.SVApi;
import stevesvehicles.api.modules.data.IModuleData;
import stevesvehicles.api.modules.handlers.ContentHandler;
import stevesvehicles.api.modules.handlers.IModuleHandler;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.api.network.DataWriter;
import stevesvehicles.api.network.IStreamable;
import stevesvehicles.client.gui.assembler.SimulationInfo;
import stevesvehicles.client.gui.assembler.SimulationInfoBoolean;
import stevesvehicles.client.gui.assembler.SimulationInfoInteger;
import stevesvehicles.client.gui.assembler.SimulationInfoMultiBoolean;
import stevesvehicles.client.rendering.models.ModelVehicle;
import stevesvehicles.common.container.slots.SlotBase;

public class Module<P extends ICapabilityProvider> extends Impl<Module<P>> implements IStreamable {

	// the vehicle this module is part of
	private IModuleHandler<P> parent;
	// the inventory this module is using, could be an empty array.
	// getInventorySize is controlling the size of this
	private NonNullList<ItemStack> cargo;
	// the slot global start index for this module, used to transfer the local
	// indices to global ones
	protected int slotGlobalStart;
	// the id of this module, this is assigned on creation. The id is the same
	// as the ModuleData which created the module.
	private int moduleId;
	// the of this module, this is the position among modules that this module
	// has.
	private int positionId;
	// the models this module is using, this is generated from the ModuleData
	// creating this module
	private ArrayList<ModelVehicle> models;

	private boolean wasInitialized;
	private ItemStack itemParent;
	private ResourceLocation dataParent;
	private List<ContentHandler> handlers;
	private World world;

	/**
	 * @return The ItemStack from that the module was created.
	 */
	public ItemStack getItemParent(){
		return itemParent;
	}

	public IModuleData getDataParent(){
		return GameRegistry.findRegistry(IModuleData.class).getValue(dataParent);
	}

	/**
	 * Returns true if the module was already initialized.
	 */
	public boolean wasInitialized(){
		return wasInitialized;
	}

	/**
	 * Add a IContentHandler to this module. This will only work before the
	 * module is initialized.
	 */
	public void addContendHandler(ContentHandler handler){
		if(!handlers.contains(handler)){
			handlers.add(handler);
		}
	}

	@Nullable
	<H> List<H> getHandlers(Class<? extends H> handlerClass){
		List<H> handlers = new ArrayList<>();
		for(ContentHandler handler : this.handlers){
			if(handlerClass.isAssignableFrom(handler.getClass())){
				handlers.add((H) handler);
			}
		}
		return  handlers;
	}

	/**
	 * @return A list with all {@link ContentHandler}s that this module have.
	 */
	public List<ContentHandler> getHandlers(){
		return handlers;
	}

	/**
	 * Creates a new instance of this module, the module will be created at the
	 * given vehicle.
	 * 
	 * @param parent
	 *            The vehicle this module is created on
	 */
	public Module(IModuleHandler<P> parent, World world) {
		// save the vehicle
		this.parent = parent;
		this.world = world;
		// initialize the inventory of this module
		cargo = NonNullList.withSize(getInventorySize(), ItemStack.EMPTY);
	}

	/**
	 * Initializes the modules, this is done after all modules has been added to
	 * the vehicle, and given proper IDs and everything.
	 */
	public void init() {
	}

	/**
	 * Initializes the modules, this is done after all modules has been added to
	 * the vehicle but before most of the initializing code
	 */
	public void preInit() {
	}

	/**
	 * The hander that handle this module.
	 * 
	 * @return The vehicle this module was created at
	 */
	public IModuleHandler<P> getParent() {
		return parent;
	}

	/**
	 * If this module is part of a placeholder vehicle, a placeholder vehicle is
	 * a client side only vehicle used in the vehicle assembler.
	 * 
	 * @return If this module is a placeholder module
	 */
	public boolean isPlaceholder() {
		return getParent().isPlaceholder();
	}

	/**
	 * Sets the modular id of this module, this is basically the id of the
	 * {@link ModuleData} used to create this module.
	 * 
	 * @param val
	 *            The module id
	 */
	public void setModuleId(int val) {
		moduleId = val;
	}

	/**
	 * Returns which modular id this module is associated with
	 * 
	 * @return The module id
	 */
	public int getModuleId() {
		return moduleId;
	}

	public int getPositionId() {
		return positionId;
	}

	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}

	/**
	 * Is called when the vehicle's inventory has been changed
	 */
	public void onInventoryChanged() {
	}

	/**
	 * Returns the amount of stacks that this module can store. This will use
	 * hasSlots, getInventoryWidth and getInventoryHeight to calculate the size,
	 * this can however be overridden for more advanced usages.
	 * 
	 * @return The size of the inventory of this module
	 */
	public int getInventorySize() {
		if (!hasSlots()) {
			return 0;
		} else {
			return getInventoryWidth() * getInventoryHeight();
		}
	}

	/**
	 * The width of slots in the basic slot allocation. Used by the default
	 * getInventorySize to make standard slot allocation easier
	 * 
	 * @return The number of slots next to each other
	 */
	protected int getInventoryWidth() {
		return 3;
	}

	/**
	 * The height of slots in the basic slot allocation. Used by the default
	 * getInventorySize to make standard slot allocation easier
	 * 
	 * @return The number of slots on top of each other
	 */
	protected int getInventoryHeight() {
		return 1;
	}

	// the list of the slots used by this module
	protected ArrayList<SlotBase> slotList;

	/**
	 * Get the list of slots used by this module. These have already been
	 * generated by generateSlots
	 * 
	 * @return The ArrayList of SlotBase with the slots
	 */
	public ArrayList<SlotBase> getSlots() {
		return slotList;
	}

	/**
	 * Generates the slots used for this module, this is used both for the
	 * Container and the Interface. For most modules just leave this and use
	 * getSlot instead (as well as setting getInventoryWidth and
	 * getInventoryHeight)
	 * 
	 * @param slotCount
	 *            The number of slots that has already been added to the
	 *            vehicle. This is for generating the corred slot id
	 * @return The number of slots that the vehicle have added after this module
	 *         has generated its slots.
	 */
	public int generateSlots(int slotCount) {
		slotGlobalStart = slotCount;
		slotList = new ArrayList<>();
		for (int j = 0; j < getInventoryHeight(); j++) {
			for (int i = 0; i < getInventoryWidth(); i++) {
				slotList.add(getSlot(slotCount++, i, j));
			}
		}
		return slotCount;
	}

	/**
	 * Returns a new slot with the given id, x and y coordinate. This is used to
	 * generate the slots easier. Just override this function and return a new
	 * slots depending on where it's located. Shouldn't be used if you're
	 * overriding generateSlots
	 * 
	 * @param slotId
	 *            The id of the slot to be created
	 * @param x
	 *            The x value of the slot, this is not the interface coordinate
	 *            but just which column it's in.
	 * @param y
	 *            The y value of the slot, this is not hte interface coordinate
	 *            but just which row it's in.
	 * @return The created SlotBase
	 */
	protected SlotBase getSlot(int slotId, int x, int y) {
		return null;
	}

	/**
	 * Whether this module has slots or not. By default a module is thought to
	 * have slots if it has an interface. This is however overridden if it's not
	 * the case.
	 * 
	 * @return If it should use slots or not
	 */
	public boolean hasSlots() {
		return false;
	}

	/**
	 * Called every time the vehicle is being updated.
	 */
	public void update() {
	}

	/**
	 * Returns if this module has enough fuel to keep the vehicle going one tick
	 * more. This should however be moved to engineModuleBase
	 * 
	 * @param consumption
	 *            The amount of fuel units the vehicle wants to consume
	 * @return If it has fuel or not
	 */
	public boolean hasFuel(int consumption) {
		return false;
	}

	/**
	 * Returns the Y value this vehicle should try to be on. By returning -1
	 * this module won't care about where the vehicle should be. If no modules
	 * do care about this the vehicle will just continue where it already is.
	 * 
	 * @return The Y value
	 */
	public int getYTarget() {
		return -1;
	}

	/**
	 * Used to get the ItemStack in a specific slot of this module
	 * 
	 * @param slot
	 *            The slot id, this is the local id for this module.
	 * @return The ItemStack in the slot, could of course be null
	 */
	public ItemStack getStack(int slot) {
		return cargo.get(slot);
	}

	/**
	 * Used to set the ItemStack in specific slot of this module.
	 * 
	 * @param slot
	 *            The slot id, this is the local id for this module.
	 * @param item
	 *            The ItemStack to be set.
	 */
	public void setStack(int slot, ItemStack item) {
		cargo.set(slot, item);
	}

	/**
	 * Used to try to merge/add the ItemStack in specific slots of this module.
	 * 
	 * @param slotStart
	 *            The slot start id, this is the local id for this module.
	 * @param slotEnd
	 *            The slot end id, this is the local id for this module.
	 * @param item
	 *            The ItemStack to be set.
	 */
	public void addStack(int slotStart, int slotEnd, ItemStack item) {
		//TODO: Move TransferHandler into the api.
		//getParent().addItemToChest(item, slotGlobalStart + slotStart, slotGlobalStart + slotEnd);
	}

	/**
	 * Used to try to merge/add the ItemStack in a specific slot of this module.
	 * 
	 * @param slot
	 *            The slot id, this is the local id for this module.
	 * @param item
	 *            The ItemStack to be set.
	 */
	public void addStack(int slot, ItemStack item) {
		addStack(slot, slot, item);
	}

	/**
	 * Used to prevent the vehicle to drop things when it breaks. If any module
	 * returns false the vehicle won't drop anything.
	 * 
	 * @return If this module allows the vehicle to drop on death
	 */
	public boolean dropOnDeath() {
		return true;
	}

	/**
	 * Called when the vehicle breaks
	 */
	public void onDeath() {
	}

	public void openInventory(EntityPlayer player) {
	}

	public void closeInventory(EntityPlayer player) {
	}

	/**
	 * Handles the writing of the NBT data when the world is being saved
	 * 
	 * @param tagCompound
	 *            The tag compound to write the data to
	 */
	public final void writeToNBT(NBTTagCompound tagCompound) {
		// write the content of the slots to the tag compound
		ItemStackHelper.saveAllItems(tagCompound, cargo);
		// writes module specific data
		save(tagCompound);
	}

	/**
	 * Allows a module to save specific data when world is saved
	 * 
	 * @param tagCompound
	 *            The NBT tag compound to write to
	 *
	 */
	protected void save(NBTTagCompound tagCompound) {
	}

	/**
	 * Handles the reading of the NBT data when the world is being loaded
	 * 
	 * @param tagCompound
	 *            The tag compound to read the data from
	 */
	public final void readFromNBT(NBTTagCompound tagCompound) {
		// read the content of the slots to the tag compound
		ItemStackHelper.loadAllItems(tagCompound, cargo);
		// reads module specific data
		load(tagCompound);
	}

	/**
	 * Allows a module to load specific data when world is loaded
	 * 
	 * @param tagCompound
	 *            The NBT tag compound to read from
	 *
	 */
	protected void load(NBTTagCompound tagCompound) {
	}

	protected DataWriter getDataWriter(boolean hasInterfaceOpen) throws IOException {
		return SVApi.packetHandler.createWriter(this, hasInterfaceOpen);
	}

	protected DataWriter getDataWriter() throws IOException {
		return getDataWriter(true);
	}

	protected void sendPacketToServer() {
		sendPacketToServer(true);
	}

	protected void sendPacketToServer(boolean hasInterfaceOpen) {
		try {
			SVApi.packetHandler.sendPacketVehicle(this, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void sendPacketToServer(DataWriter dw) throws IOException {
		SVApi.packetHandler.sendPacketVehicle(this, dw);
	}

	protected void sendPacketToPlayer(DataWriter dw, EntityPlayer player) throws IOException {
		SVApi.packetHandler.sendPacketVehicle(this, dw, player);
	}

	@Override
	public void writeData(DataWriter data) throws IOException {
	}

	@Override
	public void readData(DataReader data, EntityPlayer player) throws IOException {
	}

	/**
	 * Get the consumption for this module
	 * 
	 * @param isMoving
	 *            A flag telling you if the vehicle is moving or not
	 * @return The consumption
	 */
	public int getConsumption(boolean isMoving) {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public void setModels(ArrayList<ModelVehicle> models) {
		this.models = models;
	}

	@SideOnly(Side.CLIENT)
	public ArrayList<ModelVehicle> getModels() {
		return models;
	}

	public boolean haveModels() {
		return models != null;
	}

	/**
	 * Allows a module to stop the engines, won't stop modules using the engine
	 * though
	 * 
	 * @return True if the module is forcing the engines to stop
	 */
	public boolean stopEngines() {
		return false;
	}

	/**
	 * Allows a module to stop the vehicle from being rendered
	 * 
	 * @return False if the vehicle sohuldn't be rendered
	 */
	public boolean shouldVehicleRender() {
		return true;
	}

	/**
	 * Allows a module to change the color of the vehicle
	 * 
	 * @return The color of the vehicle {Red 0.0F to 1.0F, Green 0.0F to 1.0F,
	 *         Blue 0.0F to 1.0F}
	 */
	public float[] getColor() {
		return new float[] { 1F, 1F, 1F };
	}
	protected FakePlayer getFakePlayer() {
		return FakePlayerFactory.getMinecraft((WorldServer) world);
	}

	public boolean disableStandardKeyFunctionality() {
		return false;
	}

	public void addToLabel(ArrayList<String> label) {
	}

	public boolean onInteractFirst(EntityPlayer entityplayer) {
		return false;
	}

	public void postUpdate() {
	}

	public String getModuleName() {
		IModuleData data = getDataParent();
		return data == null ? null : data.getName();
	}

	private boolean hasSimulationInfoBeenLoaded;
	private List<SimulationInfo> simulationInfo;

	public final void initSimulationInfo() {
		if (!hasSimulationInfoBeenLoaded) {
			simulationInfo = new ArrayList<>();
			loadSimulationInfo(simulationInfo);
			hasSimulationInfoBeenLoaded = true;
		}
	}

	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
	}

	public SimulationInfo getSimulationInfo(int id) {
		return simulationInfo.get(id);
	}

	public SimulationInfo getSimulationInfo() {
		return getSimulationInfo(0);
	}

	public void addSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.addAll(this.simulationInfo);
	}

	public boolean getBooleanSimulationInfo() {
		return ((SimulationInfoBoolean) getSimulationInfo()).getValue();
	}

	public int getIntegerSimulationInfo() {
		return ((SimulationInfoInteger) getSimulationInfo()).getValue();
	}

	public int getMultiBooleanIntegerSimulationInfo() {
		return ((SimulationInfoMultiBoolean) getSimulationInfo()).getIntegerValue();
	}

}
