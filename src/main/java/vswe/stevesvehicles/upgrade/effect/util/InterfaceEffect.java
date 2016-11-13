package vswe.stevesvehicles.upgrade.effect.util;

import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.gui.screen.GuiUpgrade;
import vswe.stevesvehicles.container.ContainerUpgrade;
import vswe.stevesvehicles.tileentity.TileEntityUpgrade;
import vswe.stevesvehicles.upgrade.effect.BaseEffect;


public abstract class InterfaceEffect extends BaseEffect {


	public InterfaceEffect(TileEntityUpgrade upgrade) {
		super(upgrade);
	}

	@SideOnly(Side.CLIENT)
	public void drawForeground(GuiUpgrade gui) {}

	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiUpgrade gui, int x, int y) {}

	@SideOnly(Side.CLIENT)
	public void drawMouseOver(GuiUpgrade gui, int x, int y) {}

	public void checkGuiData(ContainerUpgrade con, IContainerListener crafting, boolean isNew) {}

	public void receiveGuiData(int id, short data) {}

	@SideOnly(Side.CLIENT)
	protected void drawMouseOver(GuiUpgrade gui, String str, int x, int y, int[] rect) {
		if (gui.inRect(x-gui.getGuiLeft(),y-gui.getGuiTop(),rect)) {
			gui.drawMouseOver(str, x-gui.getGuiLeft(), y-gui.getGuiTop());
		}	
	}	




}