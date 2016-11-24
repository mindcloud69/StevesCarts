package stevesvehicles.common.blocks.tileentitys;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.gui.screen.GuiActivator;
import stevesvehicles.client.gui.screen.GuiBase;
import stevesvehicles.client.localization.entry.block.LocalizationToggler;
import stevesvehicles.common.blocks.tileentitys.toggler.TogglerOption;
import stevesvehicles.common.container.ContainerActivator;
import stevesvehicles.common.container.ContainerBase;
import stevesvehicles.common.modules.cart.tool.ModuleDrill;
import stevesvehicles.common.modules.common.addon.ModuleInvisible;
import stevesvehicles.common.modules.common.addon.ModuleShield;
import stevesvehicles.common.modules.common.addon.chunk.ModuleChunkLoader;
import stevesvehicles.common.modules.common.attachment.ModuleCage;
import stevesvehicles.common.network.DataReader;
import stevesvehicles.common.vehicles.entitys.EntityModularCart;

/**
 * The tile entity used by the Module Toggler
 * 
 * @author Vswe
 *
 */
public class TileEntityActivator extends TileEntityBase {
	@SideOnly(Side.CLIENT)
	@Override
	public GuiBase getGui(InventoryPlayer inv) {
		return new GuiActivator(inv, this);
	}

	@Override
	public ContainerBase getContainer(InventoryPlayer inv) {
		return new ContainerActivator(this);
	}

	/**
	 * The different settings the toggler can toggle
	 */
	private ArrayList<TogglerOption> options;

	public TileEntityActivator() {
		loadOptions();
	}

	// TODO let mods register these?
	/**
	 * Load the different settings the player can toggle and change. For example
	 * the drill.
	 */
	private void loadOptions() {
		options = new ArrayList<>();
		options.add(new TogglerOption(LocalizationToggler.DRILL_OPTION, ModuleDrill.class));
		options.add(new TogglerOption(LocalizationToggler.SHIELD_OPTION, ModuleShield.class));
		options.add(new TogglerOption(LocalizationToggler.INVISIBILITY_OPTION, ModuleInvisible.class));
		options.add(new TogglerOption(LocalizationToggler.CHUNK_OPTION, ModuleChunkLoader.class));
		options.add(new TogglerOption(LocalizationToggler.AUTO_CAGE_OPTION, ModuleCage.class, 0));
		options.add(new TogglerOption(LocalizationToggler.CAGE_OPTION, ModuleCage.class, 1));
	}

	/**
	 * Get the different settings the toggler can toggle
	 * 
	 * @return A list of the settings
	 */
	public ArrayList<TogglerOption> getOptions() {
		return options;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		// load all the options
		for (TogglerOption option : options) {
			option.setOption(nbttagcompound.getByte(option.getName()));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		// save all the options
		for (TogglerOption option : options) {
			nbttagcompound.setByte(option.getName(), (byte) option.getOption());
		}
		return nbttagcompound;
	}

	@Override
	public void receivePacket(DataReader dr, EntityPlayer player) {
		boolean leftClick = dr.readBoolean();
		int optionId = dr.readByte();
		if (optionId >= 0 && optionId < options.size()) {
			options.get(optionId).changeOption(leftClick);
		}
	}

	@Override
	public void initGuiData(Container con, IContainerListener crafting) {
		for (int i = 0; i < options.size(); i++) {
			updateGuiData(con, crafting, i, (short) options.get(i).getOption());
		}
	}

	@Override
	public void checkGuiData(Container con, IContainerListener crafting) {
		for (int i = 0; i < options.size(); i++) {
			int option = options.get(i).getOption();
			int lastOption = ((ContainerActivator) con).lastOptions.get(i);
			// if an update has been made, send the new data
			if (option != lastOption) {
				updateGuiData(con, crafting, i, (short) option);
				((ContainerActivator) con).lastOptions.set(i, option);
			}
		}
	}

	@Override
	public void receiveGuiData(int id, short data) {
		// if it's a valid id, update the option associated with that id
		if (id >= 0 && id < options.size()) {
			options.get(id).setOption(data);
		}
	}

	/**
	 * Handles a cart that is passing an advanced detector rail "in front" of
	 * this toggler
	 * 
	 * @param cart
	 *            The cart that is passing
	 * @param isOrange
	 *            Whether the cart is passing in the orange direction or not
	 */
	public void handleCart(EntityModularCart cart, boolean isOrange) {
		// tell the cart to update with any option that is not disabled
		for (TogglerOption option : options) {
			if (!option.isDisabled()) {
				cart.getVehicle().handleActivator(option, isOrange);
			}
		}
	}
}
