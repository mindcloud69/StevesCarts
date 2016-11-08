package vswe.stevescarts.renders;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.items.ModItems;
import vswe.stevescarts.models.ModelCartbase;
import vswe.stevescarts.modules.data.ModuleData;

import java.util.HashMap;

public class ItemStackRenderer extends TileEntityItemStackRenderer {

	TileEntityItemStackRenderer renderer;

	public ItemStackRenderer(TileEntityItemStackRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void renderByItem(ItemStack itemStack) {
		if(itemStack.getItem() != ModItems.carts){
			renderer.renderByItem(itemStack);
			return;
		}
		GL11.glPushMatrix();
		GL11.glScalef(-1.0f, -1.0f, 1.0f);
//		if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
//			GL11.glTranslatef(0.0f, -1.0f, 1.0f);
//		} else if (type == IItemRenderer.ItemRenderType.INVENTORY) {
//			GL11.glTranslatef(0.0f, 0.1f, 0.0f);
//		}
		final NBTTagCompound info = itemStack.getTagCompound();
		if (info != null) {
			final NBTTagByteArray moduleIDTag = (NBTTagByteArray) info.getTag("Modules");
			final byte[] bytes = moduleIDTag.getByteArray();
			final HashMap<String, ModelCartbase> models = new HashMap<String, ModelCartbase>();
			float lowestMult = 1.0f;
			for (final byte id : bytes) {
				final ModuleData module = ModuleData.getList().get(id);
				if (module != null && module.haveModels(true)) {
					if (module.getModelMult() < lowestMult) {
						lowestMult = module.getModelMult();
					}
					models.putAll(module.getModels(true));
				}
			}
			for (final byte id : bytes) {
				final ModuleData module = ModuleData.getList().get(id);
				if (module != null && module.haveRemovedModels()) {
					for (final String str : module.getRemovedModels()) {
						models.remove(str);
					}
				}
			}
		//	if (type == IItemRenderer.ItemRenderType.INVENTORY) {
				GL11.glScalef(lowestMult, lowestMult, lowestMult);
		//	}
			for (final ModelCartbase model : models.values()) {
				model.render(null, null, 0.0f, 0.0f, 0.0f, 0.0625f, 0.0f);
			}
		}
		GL11.glPopMatrix();
	}
}
