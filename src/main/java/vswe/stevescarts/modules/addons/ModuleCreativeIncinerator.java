package vswe.stevescarts.modules.addons;

import net.minecraft.item.ItemStack;
import vswe.stevescarts.entitys.EntityMinecartModular;

public class ModuleCreativeIncinerator extends ModuleIncinerator {
	public ModuleCreativeIncinerator(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	protected int getIncinerationCost() {
		return 0;
	}

	@Override
	protected boolean isItemValid(@Nonnull ItemStack item) {
		return item != null;
	}

	@Override
	public boolean hasGui() {
		return false;
	}
}
