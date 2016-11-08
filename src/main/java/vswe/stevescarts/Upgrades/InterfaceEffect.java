package vswe.stevescarts.Upgrades;

import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Containers.ContainerUpgrade;
import vswe.stevescarts.Interfaces.GuiUpgrade;
import vswe.stevescarts.TileEntities.TileEntityUpgrade;

public abstract class InterfaceEffect extends BaseEffect {
	@SideOnly(Side.CLIENT)
	public void drawForeground(final TileEntityUpgrade upgrade, final GuiUpgrade gui) {
	}

	@SideOnly(Side.CLIENT)
	public void drawBackground(final TileEntityUpgrade upgrade, final GuiUpgrade gui, final int x, final int y) {
	}

	@SideOnly(Side.CLIENT)
	public void drawMouseOver(final TileEntityUpgrade upgrade, final GuiUpgrade gui, final int x, final int y) {
	}

	public void checkGuiData(final TileEntityUpgrade upgrade, final ContainerUpgrade con, final IContainerListener crafting, final boolean isNew) {
	}

	public void receiveGuiData(final TileEntityUpgrade upgrade, final int id, final short data) {
	}

	protected void drawMouseOver(final GuiUpgrade gui, final String str, final int x, final int y, final int[] rect) {
		if (gui.inRect(x - gui.getGuiLeft(), y - gui.getGuiTop(), rect)) {
			gui.drawMouseOver(str, x - gui.getGuiLeft(), y - gui.getGuiTop());
		}
	}
}
