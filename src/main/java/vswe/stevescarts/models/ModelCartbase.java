package vswe.stevescarts.models;

import java.util.ArrayList;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

@SideOnly(Side.CLIENT)
public abstract class ModelCartbase extends ModelBase {
	protected final byte cartLength = 20;
	protected final byte cartHeight = 8;
	protected final byte cartWidth = 16;
	protected final byte cartOnGround = 4;
	private ArrayList<ModelRenderer> renderers;

	@SideOnly(Side.CLIENT)
	public abstract ResourceLocation getResource(final ModuleBase p0);

	public ModelCartbase() {
		this.renderers = new ArrayList<ModelRenderer>();
	}

	public void render(final Render render, final ModuleBase module, final float yaw, final float pitch, final float roll, final float mult, final float partialtime) {
		final ResourceLocation resource = this.getResource(module);
		if (resource == null) {
			return;
		}
		ResourceHelper.bindResource(resource);
		this.applyEffects(module, yaw, pitch, roll, partialtime);
		this.do_render(mult);
	}

	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll, final float partialtime) {
		this.applyEffects(module, yaw, pitch, roll);
	}

	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
	}

	protected void AddRenderer(final ModelRenderer renderer) {
		this.renderers.add(this.fixSize(renderer));
	}

	public ModelRenderer fixSize(final ModelRenderer renderer) {
		return renderer.setTextureSize(this.getTextureWidth(), this.getTextureHeight());
	}

	protected int getTextureWidth() {
		return 64;
	}

	protected int getTextureHeight() {
		return 64;
	}

	public float extraMult() {
		return 1.0f;
	}

	protected void do_render(final float mult) {
		for (final ModelRenderer renderer : this.renderers) {
			renderer.render(mult * this.extraMult());
		}
	}
}
