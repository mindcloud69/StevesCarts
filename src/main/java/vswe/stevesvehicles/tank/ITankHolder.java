package vswe.stevesvehicles.tank;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.gui.screen.GuiBase;

public interface ITankHolder {


	public ItemStack getInputContainer(int tankId);
	public void clearInputContainer(int tankId);
	public void addToOutputContainer(int tankId, ItemStack item);
	public void onFluidUpdated(int tankId);
	@SideOnly(Side.CLIENT)
	public void drawImage(int tankId, GuiBase gui, IIcon icon, int targetX, int targetY, int srcX, int srcY, int sizeX, int sizeY);


}