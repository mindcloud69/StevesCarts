package vswe.stevescarts.blocks.tileentities;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.containers.ContainerActivator;
import vswe.stevescarts.containers.ContainerBase;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiActivator;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.helpers.ActivatorOption;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.modules.addons.ModuleChunkLoader;
import vswe.stevescarts.modules.addons.ModuleInvisible;
import vswe.stevescarts.modules.addons.ModuleShield;
import vswe.stevescarts.modules.realtimers.ModuleCage;
import vswe.stevescarts.modules.workers.tools.ModuleDrill;

public class TileEntityActivator extends TileEntityBase {
	private ArrayList<ActivatorOption> options;

	@SideOnly(Side.CLIENT)
	@Override
	public GuiBase getGui(final InventoryPlayer inv) {
		return new GuiActivator(inv, this);
	}

	@Override
	public ContainerBase getContainer(final InventoryPlayer inv) {
		return new ContainerActivator(inv, this);
	}

	public TileEntityActivator() {
		this.loadOptions();
	}

	private void loadOptions() {
		(this.options = new ArrayList<ActivatorOption>()).add(new ActivatorOption(Localization.GUI.TOGGLER.OPTION_DRILL, ModuleDrill.class));
		this.options.add(new ActivatorOption(Localization.GUI.TOGGLER.OPTION_SHIELD, ModuleShield.class));
		this.options.add(new ActivatorOption(Localization.GUI.TOGGLER.OPTION_INVISIBILITY, ModuleInvisible.class));
		this.options.add(new ActivatorOption(Localization.GUI.TOGGLER.OPTION_CHUNK, ModuleChunkLoader.class));
		this.options.add(new ActivatorOption(Localization.GUI.TOGGLER.OPTION_CAGE_AUTO, ModuleCage.class, 0));
		this.options.add(new ActivatorOption(Localization.GUI.TOGGLER.OPTION_CAGE, ModuleCage.class, 1));
	}

	public ArrayList<ActivatorOption> getOptions() {
		return this.options;
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		for (final ActivatorOption option : this.options) {
			option.setOption(nbttagcompound.getByte(option.getName()));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		for (final ActivatorOption option : this.options) {
			nbttagcompound.setByte(option.getName(), (byte) option.getOption());
		}
		return nbttagcompound;
	}

	@Override
	public void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			final boolean leftClick = (data[0] & 0x1) == 0x0;
			final int optionId = (data[0] & 0xFFFFFFFE) >> 1;
			if (optionId >= 0 && optionId < this.options.size()) {
				this.options.get(optionId).changeOption(leftClick);
			}
		}
	}

	@Override
	public void initGuiData(final Container con, final IContainerListener crafting) {
		for (int i = 0; i < this.options.size(); ++i) {
			this.updateGuiData(con, crafting, i, (short) this.options.get(i).getOption());
		}
	}

	@Override
	public void checkGuiData(final Container con, final IContainerListener crafting) {
		for (int i = 0; i < this.options.size(); ++i) {
			final int option = this.options.get(i).getOption();
			final int lastoption = ((ContainerActivator) con).lastOptions.get(i);
			if (option != lastoption) {
				this.updateGuiData(con, crafting, i, (short) option);
				((ContainerActivator) con).lastOptions.set(i, option);
			}
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id >= 0 && id < this.options.size()) {
			this.options.get(id).setOption(data);
		}
	}

	public void handleCart(final EntityMinecartModular cart, final boolean isOrange) {
		for (final ActivatorOption option : this.options) {
			if (!option.isDisabled()) {
				cart.handleActivator(option, isOrange);
			}
		}
	}
}
