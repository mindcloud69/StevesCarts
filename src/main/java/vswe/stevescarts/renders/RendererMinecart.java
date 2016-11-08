package vswe.stevescarts.renders;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.models.ModelCartbase;
import vswe.stevescarts.modules.ModuleBase;

public class RendererMinecart extends Render {
	public RendererMinecart(RenderManager renderManager) {
		super(renderManager);
		this.shadowSize = 0.5f;
	}

	public void renderCart(final EntityMinecartModular cart, double x, double y, double z, float yaw, final float partialTickTime) {
		if (cart.getModules() != null) {
			for (final ModuleBase module : cart.getModules()) {
				if (!module.shouldCartRender()) {
					return;
				}
			}
		}
		GL11.glPushMatrix();
		final double partialPosX = cart.lastTickPosX + (cart.posX - cart.lastTickPosX) * partialTickTime;
		final double partialPosY = cart.lastTickPosY + (cart.posY - cart.lastTickPosY) * partialTickTime;
		final double partialPosZ = cart.lastTickPosZ + (cart.posZ - cart.lastTickPosZ) * partialTickTime;
		float partialRotPitch = cart.prevRotationPitch + (cart.rotationPitch - cart.prevRotationPitch) * partialTickTime;
		final Vec3d posFromRail = cart.getPos(partialPosX, partialPosY, partialPosZ);
		if (posFromRail != null && cart.canUseRail()) {
			final double predictionLength = 0.30000001192092896;
			Vec3d lastPos = cart.getPosOffset(partialPosX, partialPosY, partialPosZ, predictionLength);
			Vec3d nextPos = cart.getPosOffset(partialPosX, partialPosY, partialPosZ, -predictionLength);
			if (lastPos == null) {
				lastPos = posFromRail;
			}
			if (nextPos == null) {
				nextPos = posFromRail;
			}
			x += posFromRail.xCoord - partialPosX;
			y += (lastPos.yCoord + nextPos.yCoord) / 2.0 - partialPosY;
			z += posFromRail.zCoord - partialPosZ;
			Vec3d difference = nextPos.addVector(-lastPos.xCoord, -lastPos.yCoord, -lastPos.zCoord);
			if (difference.lengthVector() != 0.0) {
				difference = difference.normalize();
				yaw = (float) (Math.atan2(difference.zCoord, difference.xCoord) * 180.0 / 3.141592653589793);
				partialRotPitch = (float) (Math.atan(difference.yCoord) * 73.0);
			}
		}
		yaw = 180.0f - yaw;
		partialRotPitch *= -1.0f;
		float damageRot = cart.getRollingAmplitude() - partialTickTime;
		float damageTime = cart.getDamage() - partialTickTime;
		final float damageDir = cart.getRollingDirection();
		if (damageTime < 0.0f) {
			damageTime = 0.0f;
		}
		boolean flip = cart.motionX > 0.0 != cart.motionZ > 0.0;
		if (cart.cornerFlip) {
			flip = !flip;
		}
		if (cart.getRenderFlippedYaw(yaw + (flip ? 0.0f : 180.0f))) {
			flip = !flip;
		}
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(partialRotPitch, 0.0f, 0.0f, 1.0f);
		if (damageRot > 0.0f) {
			damageRot = MathHelper.sin(damageRot) * damageRot * damageTime / 10.0f * damageDir;
			GL11.glRotatef(damageRot, 1.0f, 0.0f, 0.0f);
		}
		yaw += (flip ? 0.0f : 180.0f);
		GL11.glRotatef(flip ? 0.0f : 180.0f, 0.0f, 1.0f, 0.0f);
		GL11.glScalef(-1.0f, -1.0f, 1.0f);
		this.renderModels(cart, (float) (3.141592653589793 * yaw / 180.0), partialRotPitch, damageRot, 0.0625f, partialTickTime);
		GL11.glPopMatrix();
		this.renderLabel(cart, x, y, z);
	}

	public void renderModels(final EntityMinecartModular cart, final float yaw, final float pitch, final float roll, final float mult, final float partialtime) {
		if (cart.getModules() != null) {
			for (final ModuleBase module : cart.getModules()) {
				if (module.haveModels()) {
					for (final ModelCartbase model : module.getModels()) {
						model.render(this, module, yaw, pitch, roll, mult, partialtime);
					}
				}
			}
		}
	}

	public void renderLiquidCuboid(final FluidStack liquid, final int tankSize, final float x, final float y, final float z, final float sizeX, final float sizeY, final float sizeZ, float mult) {
		//		final IconData data = Tank.getIconAndTexture(liquid);
		//		if (data == null || data.getIcon() == null) {
		//			return;
		//		}
		//		if (liquid.amount > 0) {
		//			final float filled = liquid.amount / tankSize;
		//			GL11.glPushMatrix();
		//			GL11.glTranslatef(x * mult, (y + sizeY * (1.0f - filled) / 2.0f) * mult, z * mult);
		//			ResourceHelper.bindResource(data.getResource());
		//			Tank.applyColorFilter(liquid);
		//			final float scale = 0.5f;
		//			GL11.glScalef(scale, scale, scale);
		//			GL11.glDisable(2896);
		//			mult /= scale;
		//			this.renderCuboid(data.getIcon(), sizeX * mult, sizeY * mult * filled, sizeZ * mult);
		//			GL11.glEnable(2896);
		//			GL11.glDisable(3042);
		//			GL11.glDisable(32826);
		//			GL11.glPopMatrix();
		//			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		//		}
	}

	//	private void renderCuboid(final IIcon icon, final double sizeX, final double sizeY, final double sizeZ) {
	//		this.renderFace(icon, sizeX, sizeZ, 0.0f, 90.0f, 0.0f, -(float) (sizeY / 2.0), 0.0f);
	//		this.renderFace(icon, sizeX, sizeZ, 0.0f, -90.0f, 0.0f, (float) (sizeY / 2.0), 0.0f);
	//		this.renderFace(icon, sizeX, sizeY, 0.0f, 0.0f, 0.0f, 0.0f, (float) (sizeZ / 2.0));
	//		this.renderFace(icon, sizeX, sizeY, 180.0f, 0.0f, 0.0f, 0.0f, -(float) (sizeZ / 2.0));
	//		this.renderFace(icon, sizeZ, sizeY, 90.0f, 0.0f, (float) (sizeX / 2.0), 0.0f, 0.0f);
	//		this.renderFace(icon, sizeZ, sizeY, -90.0f, 0.0f, -(float) (sizeX / 2.0), 0.0f, 0.0f);
	//	}

	//	private void renderFace(final IIcon icon, final double totalTargetW, final double totalTargetH, final float yaw, final float roll, final float offX, final float offY, final float offZ) {
	//		GL11.glPushMatrix();
	//		GL11.glTranslatef(offX, offY, offZ);
	//		GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
	//		GL11.glRotatef(roll, 1.0f, 0.0f, 0.0f);
	//		final Tessellator tessellator = Tessellator.instance;
	//		final double srcX = icon.getMinU();
	//		final double srcY = icon.getMinV();
	//		final double srcW = icon.getMaxU() - srcX;
	//		final double srcH = icon.getMaxV() - srcY;
	//		double currentTargetW;
	//		for (double d = 0.001, currentTargetX = 0.0; totalTargetW - currentTargetX > d * 2.0; currentTargetX += currentTargetW - d) {
	//			currentTargetW = Math.min(totalTargetW - currentTargetX, 1.0);
	//			double currentTargetH;
	//			for (double currentTargetY = 0.0; totalTargetH - currentTargetY > d * 2.0; currentTargetY += currentTargetH - d) {
	//				currentTargetH = Math.min(totalTargetH - currentTargetY, 1.0);
	//				tessellator.startDrawingQuads();
	//				tessellator.setNormal(0.0f, 1.0f, 0.0f);
	//				tessellator.addVertexWithUV(currentTargetX - totalTargetW / 2.0, currentTargetY - totalTargetH / 2.0, 0.0, srcX, srcY);
	//				tessellator.addVertexWithUV(currentTargetX + currentTargetW - totalTargetW / 2.0, currentTargetY - totalTargetH / 2.0, 0.0, srcX + srcW * currentTargetW, srcY);
	//				tessellator.addVertexWithUV(currentTargetX + currentTargetW - totalTargetW / 2.0, currentTargetY + currentTargetH - totalTargetH / 2.0, 0.0, srcX + srcW * currentTargetW, srcY + srcH * currentTargetH);
	//				tessellator.addVertexWithUV(currentTargetX - totalTargetW / 2.0, currentTargetY + currentTargetH - totalTargetH / 2.0, 0.0, srcX, srcY + srcH * currentTargetH);
	//				tessellator.draw();
	//			}
	//		}
	//		GL11.glPopMatrix();
	//	}

	protected void renderLabel(final EntityMinecartModular cart, final double x, final double y, final double z) {
		final ArrayList<String> labels = cart.getLabel();
		if (labels != null && labels.size() > 0) {
			final float distance = cart.getDistanceToEntity(this.renderManager.renderViewEntity);
			if (distance <= 64.0f) {
				final FontRenderer frend = this.getFontRendererFromRenderManager();
				final float var12 = 1.6f;
				final float var13 = 0.016666668f * var12;
				GL11.glPushMatrix();
				GL11.glTranslatef((float) x + 0.0f, (float) y + 1.0f + (labels.size() - 1) * 0.12f, (float) z);
				GL11.glNormal3f(0.0f, 1.0f, 0.0f);
				GL11.glRotatef(-this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
				GL11.glRotatef(this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
				GL11.glScalef(-var13, -var13, var13);
				GL11.glDisable(2896);
				GL11.glDepthMask(false);
				GL11.glDisable(2929);
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				int boxwidth = 0;
				int boxheight = 0;
				for (final String label : labels) {
					boxwidth = Math.max(boxwidth, frend.getStringWidth(label));
					boxheight += frend.FONT_HEIGHT;
				}
				final int halfW = boxwidth / 2;
				final int halfH = boxheight / 2;
				final Tessellator tes = Tessellator.getInstance();
				VertexBuffer buffer = tes.getBuffer();
				GL11.glDisable(3553);
				buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				buffer.pos(-halfW - 1, -halfH - 1, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
				buffer.pos(-halfW - 1, halfH + 1, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
				buffer.pos(halfW + 1, halfH + 1, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
				buffer.pos(halfW + 1, -halfH - 1, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
				tes.draw();
				GL11.glEnable(3553);
				int yPos = -halfH;
				for (final String label2 : labels) {
					frend.drawString(label2, -frend.getStringWidth(label2) / 2, yPos, 553648127);
					yPos += frend.FONT_HEIGHT;
				}
				GL11.glEnable(2929);
				GL11.glDepthMask(true);
				yPos = -halfH;
				for (final String label2 : labels) {
					frend.drawString(label2, -frend.getStringWidth(label2) / 2, yPos, -1);
					yPos += frend.FONT_HEIGHT;
				}
				GL11.glEnable(2896);
				GL11.glDisable(3042);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				GL11.glPopMatrix();
			}
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(final Entity par1Entity) {
		return null;
	}

	@Override
	public void doRender(final Entity par1Entity, final double x, final double y, final double z, final float yaw, final float partialTickTime) {
		this.renderCart((EntityMinecartModular) par1Entity, x, y, z, yaw, partialTickTime);
	}
}
