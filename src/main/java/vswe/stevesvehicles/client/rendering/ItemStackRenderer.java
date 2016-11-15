package vswe.stevesvehicles.client.rendering;

import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vswe.stevesvehicles.buoy.BuoyType;
import vswe.stevesvehicles.buoy.EntityBuoy;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.rendering.models.ModelBuoy;
import vswe.stevesvehicles.client.rendering.models.ModelVehicle;
import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleDataItemHandler;

public class ItemStackRenderer extends TileEntityItemStackRenderer {
	
	private ModelBase model = new ModelBuoy();
	TileEntityItemStackRenderer renderer;

	public ItemStackRenderer(TileEntityItemStackRenderer renderer) {
		this.renderer = renderer;
	}
	
	@Override
	public void renderByItem(ItemStack itemStack) {
		if(itemStack.getItem() == ModItems.buoys){
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glScalef(-1.0F, -1.0F, 1.0F);
			/*if (type == ItemRenderType.EQUIPPED) {
				GL11.glTranslatef(0, -1, 1);
			} else if (type == ItemRenderType.INVENTORY) {
				GL11.glTranslatef(0, 0.5F, 0);
				GL11.glScalef(0.65F, 0.65F, 0.65F);
			}*/
			ResourceHelper.bindResource(BuoyType.getType(itemStack.getItemDamage()).getTexture());
			model.render(null, 0, 0, 0, 0, 0, 0.0625F);
			GL11.glPopMatrix();
		}else if(itemStack.getItem() == ModItems.vehicles){
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glScalef(-1.0F, -1.0F, 1.0F);
			/*if (type == ItemRenderType.EQUIPPED) {
				GL11.glTranslatef(0, -1, 1);
			} else if (type == ItemRenderType.INVENTORY) {
				GL11.glTranslatef(0, 0.1F, 0);
			}*/
			List<ModuleData> modules = ModuleDataItemHandler.getModulesFromItem(itemStack);
			if (modules != null) {
				HashMap<String, ModelVehicle> models = new HashMap<>();
				float lowestMultiplier = 1.0F;
				for (ModuleData module : modules) {
					if (module.haveModels(true)) {
						float multiplier = module.getModelMultiplier();
						if (multiplier < lowestMultiplier) {
							lowestMultiplier = multiplier;
						}
						models.putAll(module.getModels(true));
					}
				}
				for (ModuleData module : modules) {
					if (module.haveRemovedModels()) {
						for (String str : module.getRemovedModels()) {
							models.remove(str);
						}
					}
				}
				/*if (type == ItemRenderType.INVENTORY) {
					GL11.glScalef(lowestMultiplier, lowestMultiplier, lowestMultiplier);
				}*/
				for (ModelVehicle model : models.values()) {
					model.render(null, 0, 0, 0, 0.0625F, 0);
				}
			}
			// GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glPopMatrix();
		}else{
			renderer.renderByItem(itemStack);
			return;
		}
	}
}
