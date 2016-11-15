package vswe.stevesvehicles.client.rendering.models.items;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemVehicleModuleMeshDefinition implements ItemMeshDefinition {

	ItemVehicleModuleMeshDefinition() {
	}
	
	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack) {
		return ItemModelManager.modelGenerator.getModel(stack);
	}
}
