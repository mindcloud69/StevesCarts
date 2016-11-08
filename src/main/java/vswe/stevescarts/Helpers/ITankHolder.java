package vswe.stevescarts.Helpers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Interfaces.GuiBase;

public interface ITankHolder {
	ItemStack getInputContainer(final int p0);

	void clearInputContainer(final int p0);

	void addToOutputContainer(final int p0, final ItemStack p1);

	void onFluidUpdated(final int p0);

	@SideOnly(Side.CLIENT)
	void drawImage(final int p0, final GuiBase p1, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8);
}
