package vswe.stevesvehicles.client.rendering.models.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ModeledObject {
	
	@SideOnly(Side.CLIENT)
	void registerModels(Item item);
}
