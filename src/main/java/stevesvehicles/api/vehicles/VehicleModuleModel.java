package stevesvehicles.api.vehicles;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.modules.Module;

@SideOnly(Side.CLIENT)
public abstract class VehicleModuleModel<P extends Entity> extends ModelBase {
	// Compared to the center of the cart(before the object has been rotated)
	// X > 0 Back
	// X < 0 Forward
	// Y > 0 Down
	// Y < 0 Up
	// Z > 0 Right
	// Z < 0 Left
	// {0,0,0} is in the middle of the floor of the cart
	// positive size means that the opposing corner of the object has a higher
	// x/y/z value
	// TODO get rid of these
	protected final byte cartLength = 20;
	protected final byte cartHeight = 8;
	protected final byte cartWidth = 16;
	protected final byte cartOnGround = 4; // lower values makes the cart float
	protected final static Minecraft mc = Minecraft.getMinecraft();
	// higher

	@SideOnly(Side.CLIENT)
	public abstract ResourceLocation getResource(Module<P> module);

	private ArrayList<ModelRenderer> renderers;

	public VehicleModuleModel() {
		renderers = new ArrayList<>();
	}

	public void render(Module<P> module, float yaw, float pitch, float roll, float multiplier, float partialTime) {
		ResourceLocation resource = getResource(module);
		if (resource == null) {
			return;
		}
		mc.getTextureManager().bindTexture(resource);
		applyEffects(module, yaw, pitch, roll, partialTime);
		do_render(multiplier);
	}

	public void applyEffects(Module<P> module, float yaw, float pitch, float roll, float partialTime) {
		applyEffects(module, yaw, pitch, roll);
	}

	public void applyEffects(Module<P> module, float yaw, float pitch, float roll) {
	}

	// has to be called before anything is added to the renderer
	protected void addRenderer(ModelRenderer renderer) {
		renderers.add(fixSize(renderer));
	}

	public ModelRenderer fixSize(ModelRenderer renderer) {
		return renderer.setTextureSize(getTextureWidth(), getTextureHeight());
	}

	protected int getTextureWidth() {
		return 64;
	}

	protected int getTextureHeight() {
		return 64;
	}

	public float extraMultiplier() {
		return 1F;
	}

	protected void do_render(float multiplier) {
		for (ModelRenderer renderer : renderers) {
			renderer.render(multiplier * extraMultiplier());
		}
	}
}
