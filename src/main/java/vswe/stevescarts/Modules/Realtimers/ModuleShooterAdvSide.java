package vswe.stevescarts.Modules.Realtimers;

import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.AnimationRig;
import vswe.stevescarts.Helpers.AnimationRigVal;

public class ModuleShooterAdvSide extends ModuleShooterAdv {
	private AnimationRig rig;
	private AnimationRigVal handlePos;
	private AnimationRigVal basePos;
	private AnimationRigVal handleRot;
	private AnimationRigVal gunRot;
	private AnimationRigVal backPos;
	private AnimationRigVal backRot;
	private AnimationRigVal attacherRot;
	private AnimationRigVal stabalizerOut;
	private AnimationRigVal stabalizerDown;
	private AnimationRigVal standOut;
	private AnimationRigVal standUp;
	private AnimationRigVal standSlide;
	private AnimationRigVal armBasePos;
	private AnimationRigVal armPos;
	private AnimationRigVal armRot;
	private AnimationRigVal missilePos;
	private AnimationRigVal missileRot;
	private AnimationRigVal armBasePos2;
	private AnimationRigVal armPos2;
	private AnimationRigVal armRot2;

	public ModuleShooterAdvSide(final MinecartModular cart) {
		super(cart);
		this.rig = new AnimationRig();
		this.handlePos = new AnimationRigVal(this.rig, 8.55f, 9.4f, 0.0f);
		this.basePos = new AnimationRigVal(this.rig, 1.05f, 4.0f, 0.05f);
		this.handleRot = new AnimationRigVal(this.rig, 3.1415927f, 4.712389f, 0.075f);
		this.gunRot = new AnimationRigVal(this.rig, 0.0f, -1.5707964f, 0.0f);
		this.backPos = new AnimationRigVal(this.rig, 4.5f, -3.0f, 0.3f);
		this.backRot = new AnimationRigVal(this.rig, 0.0f, -1.5707964f, 0.2f);
		this.attacherRot = new AnimationRigVal(this.rig, 0.0f, -3.1415927f, 0.2f);
		this.stabalizerOut = new AnimationRigVal(this.rig, 0.001f, 0.8f, 0.1f);
		this.stabalizerDown = new AnimationRigVal(this.rig, 0.0f, -2.0f, 0.1f);
		this.standOut = new AnimationRigVal(this.rig, 0.001f, 0.8f, 0.1f);
		this.standUp = new AnimationRigVal(this.rig, 0.0f, 2.0f, 0.1f);
		this.standSlide = new AnimationRigVal(this.rig, 0.0f, 0.25f, 0.01f);
		this.armBasePos = new AnimationRigVal(this.rig, 0.5f, 10.0f, 0.3f);
		this.armPos = new AnimationRigVal(this.rig, -2.25f, 2.5f, 0.0f);
		this.armRot = new AnimationRigVal(this.rig, 0.0f, 1.5707964f, 0.2f);
		this.missilePos = new AnimationRigVal(this.rig, 0.0f, 3.0f, 0.1f);
		this.missileRot = new AnimationRigVal(this.rig, 0.0f, -0.2f, 0.0f);
		this.armRot2 = new AnimationRigVal(this.rig, 0.0f, 1.5707964f, 0.2f);
		this.armBasePos2 = new AnimationRigVal(this.rig, 0.0f, 9.5f, 0.3f);
		this.armPos2 = new AnimationRigVal(this.rig, 0.0f, 5.0f, 0.0f);
		this.handlePos.setUpAndDown(this.basePos);
		this.handlePos.setSpeedToSync(this.basePos, false);
		this.handleRot.setUpAndDown(this.gunRot);
		this.gunRot.setSpeedToSync(this.handleRot, true);
		this.armPos.setSpeedToSync(this.armBasePos, false);
		this.armBasePos.setUpAndDown(this.armPos);
		this.missilePos.setUpAndDown(this.missileRot);
		this.missileRot.setSpeedToSync(this.missilePos, true);
		this.armPos2.setSpeedToSync(this.armBasePos2, false);
		this.armBasePos2.setUpAndDown(this.armPos2);
	}

	@Override
	public void update() {
		super.update();
		this.rig.update(!this.isPipeActive(0));
	}

	public float getHandlePos(final int mult) {
		return this.handlePos.getVal() * mult;
	}

	public float getBasePos(final int mult) {
		return this.basePos.getVal() * mult;
	}

	public float getHandleRot(final int mult) {
		return this.handleRot.getVal();
	}

	public float getGunRot(final int mult) {
		return this.gunRot.getVal();
	}

	public float getBackPos(final int mult) {
		return this.backPos.getVal();
	}

	public float getBackRot(final int mult) {
		return this.backRot.getVal() * mult;
	}

	public float getAttacherRot(final int mult) {
		return this.attacherRot.getVal() * mult;
	}

	public float getStabalizerOut(final int mult) {
		return this.stabalizerOut.getVal() * mult;
	}

	public float getStabalizerDown(final int mult) {
		return this.stabalizerDown.getVal();
	}

	public float getStandOut(final int mult, final int i, final int j) {
		return this.standOut.getVal() * j + mult * i * 0.5f + 0.003f;
	}

	public float getStandUp(final int mult, final int i, final int j) {
		return this.standUp.getVal() - this.standSlide.getVal() * (i * 2 - 1) * j * mult;
	}

	public float getArmBasePos(final int mult, final boolean fake) {
		return this.armBasePos.getVal() - (fake ? 0.0f : this.armBasePos2.getVal());
	}

	public float getArmRot(final int mult, final boolean fake) {
		return (this.armRot.getVal() - (fake ? 0.0f : this.armRot2.getVal())) * mult;
	}

	public float getArmPos(final int mult, final boolean fake) {
		return this.armPos.getVal() - (fake ? 0.0f : this.armPos2.getVal());
	}

	public float getMissilePos(final int mult) {
		return this.missilePos.getVal();
	}

	public float getMissileRot(final int mult) {
		return this.missileRot.getVal() * mult;
	}
}
