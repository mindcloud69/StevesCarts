package stevesvehicles.api.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockVine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.EntityDataManager.DataEntry;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.modules.handlers.IModuleHandler;
import stevesvehicles.common.vehicles.entitys.EntityModularCart;

public class ModuleEntity<P extends Entity> extends Module<P> {

	// offsets for Data Watchers and Packets. These offsets works as a
	// header when
	// sending values between the client and the server
	private int dataWatcherOffset;

	public ModuleEntity(IModuleHandler<P> parent, World world) {
		super(parent, world);
	}

	/**
	 * The maximum speed this module allows the vehicle to move in. The maximum
	 * speed of the vehicle will therefore be set to the lowest value all of
	 * it's modules allow.
	 * 
	 * @return The maximum speed of the vehicle
	 */
	public float getMaxSpeed() {
		return 1.1F;
	}

	/**
	 * Called when the vehicle travels over a rail. Used to allow modules to
	 * react to specific rails.
	 * 
	 * @param x
	 *            X coordinate in the world
	 * @param y
	 *            Y coordinate in the world
	 * @param z
	 *            Z coordinate in the world
	 */
	public void moveMinecartOnRail(BlockPos pos) {
	}

	/**
	 * Allows the module to override the direction the vehicle is going. This
	 * mechanic is not finished and hence won't work perfectly.
	 * 
	 * @param pos
	 *            The coordinates in the world
	 * @return The direction to go, default means that the module won't chane it
	 */
	public RailDirection getSpecialRailDirection(BlockPos pos) {
		return RailDirection.DEFAULT;
	}

	/**
	 * Handles the different directions that the module can force a vehicle to
	 * go in. {@see getSpecialRailDirection}
	 * 
	 * @author Vswe
	 *
	 */
	public enum RailDirection {
		DEFAULT, NORTH, WEST, SOUTH, EAST, LEFT, FORWARD, RIGHT
	}

	/**
	 * Let's the module handle when damage is caused to the vehicle
	 * 
	 * @param source
	 *            The source of the damage
	 * @param val
	 *            The damage
	 * @return True if the vehicle should take the damage, False to prevent the
	 *         damage
	 */
	public boolean receiveDamage(DamageSource source, float val) {
		return true;
	}

	/**
	 * Tells the vehicle to turn around, if this module is allowed to tell the
	 * vehicle to do so.
	 */
	protected void turnback() {
		P provider = getParent().getProvider();
		if (provider instanceof EntityModularCart) {
			// check if this module is allowed to tell the vehicle
			for (Module module : getParent().getModules()) {
				if (module != this && module instanceof ModuleEntity && ((ModuleEntity)module).preventTurnBack()) {
					return;
				}
			}
			// if so, turn bakc
			((EntityModularCart) provider).turnback();
		}
	}

	/**
	 * Allows a module to take all control of a vehicle's turn back condition
	 * 
	 * @return True to prevent other modules from turning the vehicle around
	 */
	protected boolean preventTurnBack() {
		return false;
	}

	/**
	 * The number of datawatchers this module wants to use
	 * 
	 * @return The amount of datawatchers
	 */
	public int numberOfDataWatchers() {
		return 0;
	}

	/**
	 * Sets the offset of the datawatchers, this is used as a header to know
	 * which module owns the datawatcher. This is set by the vehicle.
	 * 
	 * @return The datawatcher offset
	 */
	public int getDataWatcherStart() {
		return dataWatcherOffset;
	}

	/**
	 * Gets the offset of the datawatchers, this is used as a header to know
	 * which module owns the datawatcher.
	 * 
	 * @param val
	 *            The datawatcher offset
	 */
	public void setDataWatcherStart(int val) {
		dataWatcherOffset = val;
	}

	/**
	 * Used to initiate the datawatchers
	 */
	public void initDw() {
	}

	/**
	 * Generate a free datawatcher id to use
	 * 
	 * @param id
	 *            The local datawatcher id
	 * @return The datawatcher id
	 */
	private int getDwId(int id) {
		id += this.getDataWatcherStart();
		return id;
	}

	/**
	 * Register a data parameter
	 * 
	 * @param id
	 *            The local datawatcher id
	 * @param val
	 *            The value to add
	 */
	protected final <T> void registerDw(DataParameter<T> key, T value) {
		for (DataEntry entry : getParent().getProvider().getDataManager().getAll()) {
			if (entry.getKey() == key) {
				return;
			}
		}
		getParent().getProvider().getDataManager().register(key, value);
	}

	/**
	 * Updates a datawatcher
	 * 
	 * @param id
	 *            The local datawatcher id
	 * @param val
	 *            The value to update it to
	 */
	protected final <T> void updateDw(DataParameter<T> key, T value) {
		getParent().getProvider().getDataManager().set(key, value);
	}

	/**
	 * Get a datawatcher
	 * 
	 * @param id
	 *            The local datawatcher id
	 * @return The value of the datawatcher
	 */
	protected <T> T getDw(DataParameter<T> key) {
		return getParent().getProvider().getDataManager().get(key);
	}

	private int ids = 0;

	protected <T> DataParameter<T> createDw(DataSerializer<T> serializer) {
		return serializer.createKey(getDwId(ids++));
	}

	/**
	 * Gets the player using the client. Used for example to check if a player
	 * is the active player.
	 * 
	 * @return The palyer
	 */
	@SideOnly(Side.CLIENT)
	protected EntityPlayer getClientPlayer() {
		if (net.minecraft.client.Minecraft.getMinecraft() != null) {
			return net.minecraft.client.Minecraft.getMinecraft().player;
		}
		return null;
	}

	/**
	 * Used to render graphical overlays on the screen
	 * 
	 * @param minecraft
	 *            The mincraft instance to use with the rendering
	 */
	@SideOnly(Side.CLIENT)
	public void renderOverlay(net.minecraft.client.Minecraft minecraft) {
	}

	/**
	 * Allows a module to tell the vehicle to use a specific push factor
	 * 
	 * @return the push factor, or -1 to use the default value
	 */
	public double getPushFactor() {
		return -1;
	}

	/**
	 * Allows a module to change the y offset the mounted entity should be at
	 * 
	 * @param rider
	 *            The mounted entity
	 * @return The offset, or 0 if this module don't wish to change the offset.
	 */
	public float mountedOffset(Entity rider) {
		return 0F;
	}

	/**
	 * Determines if a block counts as air by the modules, for example a vehicle
	 * will count snow as air, or long grass or the like
	 * 
	 * @param pos
	 *            The coordinates of the block
	 * @return If this block counts as air by the modules
	 */
	protected boolean countsAsAir(BlockPos pos) {
		if (getParent().getProvider().getEntityWorld().isAirBlock(pos)) {
			return true;
		}
		Block b = getParent().getProvider().getEntityWorld().getBlockState(pos).getBlock();
		if (b instanceof BlockSnow) {
			return true;
		} else if (b instanceof BlockFlower) {
			return true;
		} else if (b instanceof BlockVine) {
			return true;
		}
		return false;
	}

	/**
	 * Called when the vehicle is passing a vanilla activator rail
	 * 
	 * @param x
	 *            The X coordinate of the rail
	 * @param y
	 *            The Y coordinate of the rail
	 * @param z
	 *            The Z coordinate of the rail
	 * @param active
	 *            If the rail is active or not
	 */
	public void activatedByRail(int x, int y, int z, boolean active) {
	}

}