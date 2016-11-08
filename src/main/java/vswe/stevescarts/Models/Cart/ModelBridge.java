package vswe.stevescarts.Models.Cart;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Workers.ModuleBridge;

@SideOnly(Side.CLIENT)
public class ModelBridge extends ModelCartbase {
	private static ResourceLocation normal;
	private static ResourceLocation down;
	private static ResourceLocation up;
	private static ResourceLocation normalWarning;
	private static ResourceLocation downWarning;
	private static ResourceLocation upWarning;
	private ModelRenderer drillAnchor;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		if (module == null) {
			return ModelBridge.normal;
		}
		final boolean needBridge = ((ModuleBridge) module).needBridge();
		BlockPos next = ((ModuleBridge) module).getNextblock();
		final int y = next.getY();
		final int yDif = module.getCart().getYTarget() - y;
		if (needBridge) {
			if (yDif > 0) {
				return ModelBridge.upWarning;
			}
			if (yDif < 0) {
				return ModelBridge.downWarning;
			}
			return ModelBridge.normalWarning;
		} else {
			if (yDif > 0) {
				return ModelBridge.up;
			}
			if (yDif < 0) {
				return ModelBridge.down;
			}
			return ModelBridge.normal;
		}
	}

	@Override
	protected int getTextureWidth() {
		return 8;
	}

	@Override
	protected int getTextureHeight() {
		return 8;
	}

	public ModelBridge() {
		final ModelRenderer side1 = new ModelRenderer(this, 0, 0);
		this.AddRenderer(side1);
		side1.addBox(1.0f, 3.0f, 0.5f, 2, 6, 1, 0.0f);
		side1.setRotationPoint(-11.5f, -6.0f, 8.0f);
		side1.rotateAngleY = 1.5707964f;
		final ModelRenderer side2 = new ModelRenderer(this, 0, 0);
		this.AddRenderer(side2);
		side2.addBox(1.0f, 3.0f, 0.5f, 2, 6, 1, 0.0f);
		side2.setRotationPoint(-11.5f, -6.0f, -4.0f);
		side2.rotateAngleY = 1.5707964f;
	}

	static {
		ModelBridge.normal = ResourceHelper.getResource("/models/aiModelNormal.png");
		ModelBridge.down = ResourceHelper.getResource("/models/aiModelDown.png");
		ModelBridge.up = ResourceHelper.getResource("/models/aiModelUp.png");
		ModelBridge.normalWarning = ResourceHelper.getResource("/models/aiModelNormalWarning.png");
		ModelBridge.downWarning = ResourceHelper.getResource("/models/aiModelDownWarning.png");
		ModelBridge.upWarning = ResourceHelper.getResource("/models/aiModelUpWarning.png");
	}
}
