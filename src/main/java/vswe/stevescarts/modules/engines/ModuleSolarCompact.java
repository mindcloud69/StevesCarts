package vswe.stevescarts.modules.engines;

import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.helpers.AnimationRig;
import vswe.stevescarts.helpers.AnimationRigVal;

public class ModuleSolarCompact extends ModuleSolarBase {
	private AnimationRig rig;
	private AnimationRigVal extraction;
	private AnimationRigVal topbot;
	private AnimationRigVal leftright;
	private AnimationRigVal corner;
	private AnimationRigVal angle;
	private AnimationRigVal extraction2;
	private AnimationRigVal innerextraction;

	public ModuleSolarCompact(final EntityMinecartModular cart) {
		super(cart);
		this.rig = new AnimationRig();
		this.extraction = new AnimationRigVal(this.rig, 0.4f, 2.0f, 0.1f);
		this.topbot = new AnimationRigVal(this.rig, 0.1f, 4.0f, 0.25f);
		this.leftright = new AnimationRigVal(this.rig, 0.01f, 6.0f, 0.2f);
		this.corner = new AnimationRigVal(this.rig, 0.1f, 4.0f, 0.25f);
		this.extraction2 = new AnimationRigVal(this.rig, 0.0f, 1.8f, 0.1f);
		this.innerextraction = new AnimationRigVal(this.rig, 0.4f, 3.0f, 0.2f);
		this.angle = new AnimationRigVal(this.rig, 0.0f, 1.5707964f, 0.1f);
		this.innerextraction.setUpAndDown(this.angle);
	}

	@Override
	protected int getMaxCapacity() {
		return 25000;
	}

	@Override
	protected int getGenSpeed() {
		return 5;
	}

	@Override
	public boolean updatePanels() {
		return this.rig.update(this.isGoingDown());
	}

	public float getExtractionDist() {
		return this.extraction.getVal() + this.extraction2.getVal();
	}

	public float getTopBotExtractionDist() {
		return this.topbot.getVal();
	}

	public float getLeftRightExtractionDist() {
		return this.leftright.getVal();
	}

	public float getCornerExtractionDist() {
		return this.corner.getVal();
	}

	public float getPanelAngle() {
		return this.angle.getVal();
	}

	public float getInnerExtraction() {
		return this.innerextraction.getVal();
	}
}
