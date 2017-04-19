package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.realtimers.ModuleDynamite;

@SideOnly(Side.CLIENT)
public class ModelDynamite extends ModelCartbase {
	private static ResourceLocation texture;
	private ModelRenderer anchor;
	private ModelRenderer[] dynamites;
	private float sizemult;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelDynamite.texture;
	}

	@Override
	public float extraMult() {
		return 0.25f;
	}

	public ModelDynamite() {
		AddRenderer(anchor = new ModelRenderer(this, 0, 0));
		(dynamites = new ModelRenderer[54])[0] = createDynamite(0.0f, 0.0f, 0.0f);
		dynamites[3] = createDynamite(-1.0f, 0.0f, 0.0f);
		dynamites[4] = createDynamite(1.0f, 0.0f, 0.0f);
		dynamites[18] = createDynamite(-2.0f, 0.0f, 0.0f);
		dynamites[19] = createDynamite(2.0f, 0.0f, 0.0f);
		dynamites[9] = createDynamite(-0.5f, 1.0f, 0.0f);
		dynamites[10] = createDynamite(0.5f, 1.0f, 0.0f);
		dynamites[24] = createDynamite(-1.5f, 1.0f, 0.0f);
		dynamites[25] = createDynamite(1.5f, 1.0f, 0.0f);
		dynamites[15] = createDynamite(0.0f, 2.0f, 0.0f);
		dynamites[30] = createDynamite(-1.0f, 2.0f, 0.0f);
		dynamites[31] = createDynamite(1.0f, 2.0f, 0.0f);
		dynamites[36] = createDynamite(-3.0f, 0.0f, 0.0f);
		dynamites[37] = createDynamite(3.0f, 0.0f, 0.0f);
		dynamites[42] = createDynamite(-2.5f, 1.0f, 0.0f);
		dynamites[43] = createDynamite(2.5f, 1.0f, 0.0f);
		dynamites[48] = createDynamite(-2.0f, 2.0f, 0.0f);
		dynamites[49] = createDynamite(2.0f, 2.0f, 0.0f);
		dynamites[1] = createDynamite(0.0f, 0.0f, -1.0f);
		dynamites[5] = createDynamite(-1.0f, 0.0f, -1.0f);
		dynamites[7] = createDynamite(1.0f, 0.0f, -1.0f);
		dynamites[20] = createDynamite(-2.0f, 0.0f, -1.0f);
		dynamites[22] = createDynamite(2.0f, 0.0f, -1.0f);
		dynamites[11] = createDynamite(-0.5f, 1.0f, -1.0f);
		dynamites[13] = createDynamite(0.5f, 1.0f, -1.0f);
		dynamites[26] = createDynamite(-1.5f, 1.0f, -1.0f);
		dynamites[28] = createDynamite(1.5f, 1.0f, -1.0f);
		dynamites[16] = createDynamite(0.0f, 2.0f, -1.0f);
		dynamites[32] = createDynamite(-1.0f, 2.0f, -1.0f);
		dynamites[34] = createDynamite(1.0f, 2.0f, -1.0f);
		dynamites[38] = createDynamite(-3.0f, 0.0f, -1.0f);
		dynamites[40] = createDynamite(3.0f, 0.0f, -1.0f);
		dynamites[44] = createDynamite(-2.5f, 1.0f, -1.0f);
		dynamites[46] = createDynamite(2.5f, 1.0f, -1.0f);
		dynamites[50] = createDynamite(-2.0f, 2.0f, -1.0f);
		dynamites[52] = createDynamite(2.0f, 2.0f, -1.0f);
		dynamites[2] = createDynamite(0.0f, 0.0f, 1.0f);
		dynamites[8] = createDynamite(-1.0f, 0.0f, 1.0f);
		dynamites[6] = createDynamite(1.0f, 0.0f, 1.0f);
		dynamites[21] = createDynamite(-2.0f, 0.0f, 1.0f);
		dynamites[23] = createDynamite(2.0f, 0.0f, 1.0f);
		dynamites[14] = createDynamite(-0.5f, 1.0f, 1.0f);
		dynamites[12] = createDynamite(0.5f, 1.0f, 1.0f);
		dynamites[29] = createDynamite(-1.5f, 1.0f, 1.0f);
		dynamites[27] = createDynamite(1.5f, 1.0f, 1.0f);
		dynamites[17] = createDynamite(0.0f, 2.0f, 1.0f);
		dynamites[35] = createDynamite(-1.0f, 2.0f, 1.0f);
		dynamites[33] = createDynamite(1.0f, 2.0f, 1.0f);
		dynamites[41] = createDynamite(-3.0f, 0.0f, 1.0f);
		dynamites[39] = createDynamite(3.0f, 0.0f, 1.0f);
		dynamites[47] = createDynamite(-2.5f, 1.0f, 1.0f);
		dynamites[45] = createDynamite(2.5f, 1.0f, 1.0f);
		dynamites[53] = createDynamite(-2.0f, 2.0f, 1.0f);
		dynamites[51] = createDynamite(2.0f, 2.0f, 1.0f);
	}

	private ModelRenderer createDynamite(final float x, final float y, final float z) {
		final ModelRenderer dynamite = new ModelRenderer(this, 0, 0);
		anchor.addChild(dynamite);
		fixSize(dynamite);
		dynamite.addBox(-8.0f, -4.0f, -4.0f, 16, 8, 8, 0.0f);
		dynamite.setRotationPoint(x * 10.0f, y * -8.0f, z * 18.0f);
		dynamite.rotateAngleY = 1.5707964f;
		return dynamite;
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		if (module == null) {
			for (int i = 0; i < dynamites.length; ++i) {
				dynamites[i].isHidden = false;
			}
		} else {
			final float size = ((ModuleDynamite) module).explosionSize();
			final float max = 44.0f;
			final float perModel = max / dynamites.length;
			for (int j = 0; j < dynamites.length; ++j) {
				dynamites[j].isHidden = (j * perModel >= size);
			}
		}
		anchor.setRotationPoint(0.0f, -24.0f / sizemult, 0.0f);
	}

	@Override
	public void render(final Render render, final ModuleBase module, final float yaw, final float pitch, final float roll, final float mult, final float partialtime) {
		if (module == null) {
			sizemult = 1.0f;
			super.render(render, module, yaw, pitch, roll, mult, partialtime);
		} else {
			final float fusemult = (float) Math.abs(Math.sin(((ModuleDynamite) module).getFuse() / ((ModuleDynamite) module).getFuseLength() * 3.141592653589793 * 6.0));
			GL11.glScalef(sizemult = fusemult * 0.5f + 1.0f, sizemult, sizemult);
			super.render(render, module, yaw, pitch, roll, mult, partialtime);
			GL11.glDisable(3553);
			GL11.glDisable(2896);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 772);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, fusemult);
			super.render(render, module, yaw, pitch, roll, mult, partialtime);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glDisable(3042);
			GL11.glEnable(2896);
			GL11.glEnable(3553);
			GL11.glScalef(1.0f / sizemult, 1.0f / sizemult, 1.0f / sizemult);
		}
	}

	static {
		ModelDynamite.texture = ResourceHelper.getResource("/models/tntModel.png");
	}
}
