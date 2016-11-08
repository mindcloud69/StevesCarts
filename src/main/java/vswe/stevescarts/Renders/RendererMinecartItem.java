package vswe.stevescarts.renders;
//package vswe.stevescarts.Renders;
//
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagByteArray;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraftforge.client.MinecraftForgeClient;
//import org.lwjgl.opengl.GL11;
//import vswe.stevescarts.Items.ModItems;
//import vswe.stevescarts.models.ModelCartbase;
//import vswe.stevescarts.ModuleData.ModuleData;
//
//import java.util.HashMap;
//
//public class RendererMinecartItem implements IItemRenderer {
//	public RendererMinecartItem() {
//		MinecraftForgeClient.registerItemRenderer((Item) ModItems.carts, (IItemRenderer) this);
//	}
//
//	public boolean handleRenderType(final ItemStack item, final IItemRenderer.ItemRenderType type) {
//		return true;
//	}
//
//	public boolean shouldUseRenderHelper(final IItemRenderer.ItemRenderType type, final ItemStack item, final IItemRenderer.ItemRendererHelper helper) {
//		return true;
//	}
//
//	public void renderItem(final IItemRenderer.ItemRenderType type, final ItemStack item, final Object... data) {
//		GL11.glPushMatrix();
//		GL11.glScalef(-1.0f, -1.0f, 1.0f);
//		if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
//			GL11.glTranslatef(0.0f, -1.0f, 1.0f);
//		} else if (type == IItemRenderer.ItemRenderType.INVENTORY) {
//			GL11.glTranslatef(0.0f, 0.1f, 0.0f);
//		}
//		final NBTTagCompound info = item.getTagCompound();
//		if (info != null) {
//			final NBTTagByteArray moduleIDTag = (NBTTagByteArray) info.getTag("Modules");
//			final byte[] bytes = moduleIDTag.func_150292_c();
//			final HashMap<String, ModelCartbase> models = new HashMap<String, ModelCartbase>();
//			float lowestMult = 1.0f;
//			for (final byte id : bytes) {
//				final ModuleData module = ModuleData.getList().get(id);
//				if (module != null && module.haveModels(true)) {
//					if (module.getModelMult() < lowestMult) {
//						lowestMult = module.getModelMult();
//					}
//					models.putAll(module.getModels(true));
//				}
//			}
//			for (final byte id : bytes) {
//				final ModuleData module = ModuleData.getList().get(id);
//				if (module != null && module.haveRemovedModels()) {
//					for (final String str : module.getRemovedModels()) {
//						models.remove(str);
//					}
//				}
//			}
//			if (type == IItemRenderer.ItemRenderType.INVENTORY) {
//				GL11.glScalef(lowestMult, lowestMult, lowestMult);
//			}
//			for (final ModelCartbase model : models.values()) {
//				model.render(null, null, 0.0f, 0.0f, 0.0f, 0.0625f, 0.0f);
//			}
//		}
//		GL11.glPopMatrix();
//	}
//}
