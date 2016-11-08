package vswe.stevescarts.modules.realtimers;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

public abstract class ModuleCommand extends ModuleBase implements ICommandSender {
	private String command;
	private int[] textbox;

	public ModuleCommand(final EntityMinecartModular cart) {
		super(cart);
		this.command = "say HI";
		this.textbox = new int[] { 10, 10, 145, 90 };
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		final List lines = gui.getFontRenderer().listFormattedStringToWidth(this.command, this.textbox[2] - 4);
		for (int i = 0; i < lines.size(); ++i) {
			final String line = lines.get(i).toString();
			this.drawString(gui, line, this.textbox[0] + 2, this.textbox[1] + 2 + i * 8, 16777215);
		}
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/command.png");
		this.drawImage(gui, this.textbox, 0, 0);
	}

	public void keyPress(final char character, final int extraInformation) {
		if (extraInformation == 14) {
			if (this.command.length() > 0) {
				this.command = this.command.substring(0, this.command.length() - 1);
			}
		} else {
			this.command += Character.toString(character);
		}
	}

	public String getCommandSenderName() {
		return "@";
	}

	public void sendChatToPlayer(final String var1) {
	}

	@Override
	public boolean canCommandSenderUseCommand(final int var1, final String var2) {
		return var1 <= 2;
	}

	public String translateString(final String var1, final Object... var2) {
		return var1;
	}

	public BlockPos getPlayerCoordinates() {
		return this.getCart().getPosition();
	}

	private void executeCommand() {
		if (!this.getCart().worldObj.isRemote) {}
	}

	@Override
	public void moveMinecartOnRail(BlockPos pos) {
		if (this.getCart().worldObj.getBlockState(pos).getBlock() == Blocks.DETECTOR_RAIL) {
			this.executeCommand();
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setString(this.generateNBTName("Command", id), this.command);
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.command = tagCompound.getString(this.generateNBTName("Command", id));
	}
}
