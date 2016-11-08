package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Realtimers.ModuleDynamite;

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
		this.AddRenderer(this.anchor = new ModelRenderer(this, 0, 0));
		(this.dynamites = new ModelRenderer[54])[0] = this.createDynamite(0.0f, 0.0f, 0.0f);
		this.dynamites[3] = this.createDynamite(-1.0f, 0.0f, 0.0f);
		this.dynamites[4] = this.createDynamite(1.0f, 0.0f, 0.0f);
		this.dynamites[18] = this.createDynamite(-2.0f, 0.0f, 0.0f);
		this.dynamites[19] = this.createDynamite(2.0f, 0.0f, 0.0f);
		this.dynamites[9] = this.createDynamite(-0.5f, 1.0f, 0.0f);
		this.dynamites[10] = this.createDynamite(0.5f, 1.0f, 0.0f);
		this.dynamites[24] = this.createDynamite(-1.5f, 1.0f, 0.0f);
		this.dynamites[25] = this.createDynamite(1.5f, 1.0f, 0.0f);
		this.dynamites[15] = this.createDynamite(0.0f, 2.0f, 0.0f);
		this.dynamites[30] = this.createDynamite(-1.0f, 2.0f, 0.0f);
		this.dynamites[31] = this.createDynamite(1.0f, 2.0f, 0.0f);
		this.dynamites[36] = this.createDynamite(-3.0f, 0.0f, 0.0f);
		this.dynamites[37] = this.createDynamite(3.0f, 0.0f, 0.0f);
		this.dynamites[42] = this.createDynamite(-2.5f, 1.0f, 0.0f);
		this.dynamites[43] = this.createDynamite(2.5f, 1.0f, 0.0f);
		this.dynamites[48] = this.createDynamite(-2.0f, 2.0f, 0.0f);
		this.dynamites[49] = this.createDynamite(2.0f, 2.0f, 0.0f);
		this.dynamites[1] = this.createDynamite(0.0f, 0.0f, -1.0f);
		this.dynamites[5] = this.createDynamite(-1.0f, 0.0f, -1.0f);
		this.dynamites[7] = this.createDynamite(1.0f, 0.0f, -1.0f);
		this.dynamites[20] = this.createDynamite(-2.0f, 0.0f, -1.0f);
		this.dynamites[22] = this.createDynamite(2.0f, 0.0f, -1.0f);
		this.dynamites[11] = this.createDynamite(-0.5f, 1.0f, -1.0f);
		this.dynamites[13] = this.createDynamite(0.5f, 1.0f, -1.0f);
		this.dynamites[26] = this.createDynamite(-1.5f, 1.0f, -1.0f);
		this.dynamites[28] = this.createDynamite(1.5f, 1.0f, -1.0f);
		this.dynamites[16] = this.createDynamite(0.0f, 2.0f, -1.0f);
		this.dynamites[32] = this.createDynamite(-1.0f, 2.0f, -1.0f);
		this.dynamites[34] = this.createDynamite(1.0f, 2.0f, -1.0f);
		this.dynamites[38] = this.createDynamite(-3.0f, 0.0f, -1.0f);
		this.dynamites[40] = this.createDynamite(3.0f, 0.0f, -1.0f);
		this.dynamites[44] = this.createDynamite(-2.5f, 1.0f, -1.0f);
		this.dynamites[46] = this.createDynamite(2.5f, 1.0f, -1.0f);
		this.dynamites[50] = this.createDynamite(-2.0f, 2.0f, -1.0f);
		this.dynamites[52] = this.createDynamite(2.0f, 2.0f, -1.0f);
		this.dynamites[2] = this.createDynamite(0.0f, 0.0f, 1.0f);
		this.dynamites[8] = this.createDynamite(-1.0f, 0.0f, 1.0f);
		this.dynamites[6] = this.createDynamite(1.0f, 0.0f, 1.0f);
		this.dynamites[21] = this.createDynamite(-2.0f, 0.0f, 1.0f);
		this.dynamites[23] = this.createDynamite(2.0f, 0.0f, 1.0f);
		this.dynamites[14] = this.createDynamite(-0.5f, 1.0f, 1.0f);
		this.dynamites[12] = this.createDynamite(0.5f, 1.0f, 1.0f);
		this.dynamites[29] = this.createDynamite(-1.5f, 1.0f, 1.0f);
		this.dynamites[27] = this.createDynamite(1.5f, 1.0f, 1.0f);
		this.dynamites[17] = this.createDynamite(0.0f, 2.0f, 1.0f);
		this.dynamites[35] = this.createDynamite(-1.0f, 2.0f, 1.0f);
		this.dynamites[33] = this.createDynamite(1.0f, 2.0f, 1.0f);
		this.dynamites[41] = this.createDynamite(-3.0f, 0.0f, 1.0f);
		this.dynamites[39] = this.createDynamite(3.0f, 0.0f, 1.0f);
		this.dynamites[47] = this.createDynamite(-2.5f, 1.0f, 1.0f);
		this.dynamites[45] = this.createDynamite(2.5f, 1.0f, 1.0f);
		this.dynamites[53] = this.createDynamite(-2.0f, 2.0f, 1.0f);
		this.dynamites[51] = this.createDynamite(2.0f, 2.0f, 1.0f);
	}

	private ModelRenderer createDynamite(final float x, final float y, final float z) {
		final ModelRenderer dynamite = new ModelRenderer(this, 0, 0);
		this.anchor.addChild(dynamite);
		this.fixSize(dynamite);
		dynamite.addBox(-8.0f, -4.0f, -4.0f, 16, 8, 8, 0.0f);
		dynamite.setRotationPoint(x * 10.0f, y * -8.0f, z * 18.0f);
		dynamite.rotateAngleY = 1.5707964f;
		return dynamite;
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		if (module == null) {
			for (int i = 0; i < this.dynamites.length; ++i) {
				this.dynamites[i].isHidden = false;
			}
		} else {
			final float size = ((ModuleDynamite) module).explosionSize();
			final float max = 44.0f;
			final float perModel = max / this.dynamites.length;
			for (int j = 0; j < this.dynamites.length; ++j) {
				this.dynamites[j].isHidden = (j * perModel >= size);
			}
		}
		this.anchor.setRotationPoint(0.0f, -24.0f / this.sizemult, 0.0f);
	}

	@Override
	public void render(final Render render, final ModuleBase module, final float yaw, final float pitch, final float roll, final float mult, final float partialtime) {
		if (module == null) {
			this.sizemult = 1.0f;
			super.render(render, module, yaw, pitch, roll, mult, partialtime);
		} else {
			final float fusemult = (float) Math.abs(Math.sin(((ModuleDynamite) module).getFuse() / ((ModuleDynamite) module).getFuseLength() * 3.141592653589793 * 6.0));
			GL11.glScalef(this.sizemult = fusemult * 0.5f + 1.0f, this.sizemult, this.sizemult);
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
			GL11.glScalef(1.0f / this.sizemult, 1.0f / this.sizemult, 1.0f / this.sizemult);
		}
	}

	static {
		ModelDynamite.texture = ResourceHelper.getResource("/models/tntModel.png");
	}
}
