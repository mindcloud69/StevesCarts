package vswe.stevescarts.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.storages.chests.ModuleEggBasket;

@SideOnly(Side.CLIENT)
public class ModelEggBasket extends ModelCartbase {
	private static ResourceLocation texture;
	ModelRenderer chesttop;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelEggBasket.texture;
	}

	@Override
	protected int getTextureHeight() {
		return 128;
	}

	public ModelEggBasket() {
		for (int i = 0; i < 2; ++i) {
			final ModelRenderer chestside = new ModelRenderer(this, 0, 13);
			this.AddRenderer(chestside);
			chestside.addBox(-8.0f, -2.5f, -0.5f, 16, 5, 1, 0.0f);
			chestside.setRotationPoint(0.0f, -8.5f, -5.5f + i * 11);
			final ModelRenderer chestfrontback = new ModelRenderer(this, 0, 19);
			this.AddRenderer(chestfrontback);
			chestfrontback.addBox(-5.0f, -2.5f, -0.5f, 10, 5, 1, 0.0f);
			chestfrontback.setRotationPoint(-7.5f + i * 15, -8.5f, 0.0f);
			chestfrontback.rotateAngleY = 1.5707964f;
			final ModelRenderer chesthandle = new ModelRenderer(this, 0, 36);
			this.AddRenderer(chesthandle);
			chesthandle.addBox(-1.0f, -1.5f, -0.5f, 2, 3, 1, 0.0f);
			chesthandle.setRotationPoint(0.0f, -12.5f, -5.5f + i * 11);
			final ModelRenderer chesthandlesmall = new ModelRenderer(this, 0, 40);
			this.AddRenderer(chesthandlesmall);
			chesthandlesmall.addBox(-1.0f, -0.5f, -0.5f, 2, 1, 1, 0.0f);
			chesthandlesmall.setRotationPoint(0.0f, -14.5f, -4.5f + i * 9);
		}
		this.AddRenderer(this.chesttop = new ModelRenderer(this, 0, 0));
		this.chesttop.addBox(-7.0f, -5.0f, -0.5f, 14, 10, 1, 0.0f);
		this.chesttop.setRotationPoint(0.0f, -11.5f, 0.0f);
		this.chesttop.rotateAngleX = 1.5707964f;
		this.chesttop.rotateAngleY = 0.1f;
		final ModelRenderer chestbot = new ModelRenderer(this, 0, 25);
		this.AddRenderer(chestbot);
		chestbot.addBox(-7.0f, -5.0f, -0.5f, 14, 10, 1, 0.0f);
		chestbot.setRotationPoint(0.0f, -5.5f, 0.0f);
		chestbot.rotateAngleX = 1.5707964f;
		final ModelRenderer chesthandletop = new ModelRenderer(this, 0, 42);
		this.AddRenderer(chesthandletop);
		chesthandletop.addBox(-1.0f, -4.0f, -0.5f, 2, 8, 1, 0.0f);
		chesthandletop.setRotationPoint(0.0f, -15.5f, 0.0f);
		chesthandletop.rotateAngleX = 1.5707964f;
		for (int j = 0; j < 12; ++j) {
			this.addEgg(j);
		}
	}

	private void addEgg(final int id) {
		final int x = id % 3;
		final int y = id / 3;
		final float xCoord = -3.0f + x * 3.3333333f;
		final float yCoord = -5.0f + y * 3.5f;
		final int textureY = 19 + id * 5;
		final ModelRenderer eggbot = new ModelRenderer(this, 30, textureY);
		this.AddRenderer(eggbot);
		eggbot.addBox(-1.0f, -0.5f, -1.0f, 2, 1, 2, 0.0f);
		eggbot.setRotationPoint(yCoord, -6.5f, xCoord);
		final ModelRenderer eggbase = new ModelRenderer(this, 38, textureY);
		this.AddRenderer(eggbase);
		eggbase.addBox(-1.5f, -1.0f, -1.5f, 3, 2, 3, 0.0f);
		eggbase.setRotationPoint(yCoord, -7.5f, xCoord);
		final ModelRenderer eggmiddle = new ModelRenderer(this, 50, textureY);
		this.AddRenderer(eggmiddle);
		eggmiddle.addBox(-1.0f, -0.5f, -1.0f, 2, 1, 2, 0.0f);
		eggmiddle.setRotationPoint(yCoord, -8.75f, xCoord);
		final ModelRenderer eggtip = new ModelRenderer(this, 58, textureY);
		this.AddRenderer(eggtip);
		eggtip.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
		eggtip.setRotationPoint(yCoord, -9.25f, xCoord);
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		if (module != null) {
			this.chesttop.rotateAngleY = 0.1f + ((ModuleEggBasket) module).getChestAngle();
		}
	}

	static {
		ModelEggBasket.texture = ResourceHelper.getResource("/models/chestModelEaster.png");
	}
}
