package vswe.stevescarts.Models.Cart;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Modules.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelLiquidDrainer extends ModelCleaner {
	public String modelTexture(final ModuleBase module) {
		return "/models/cleanerModelLiquid.png";
	}
}
