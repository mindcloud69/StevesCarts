package vswe.stevescarts.modules.engines;

import vswe.stevescarts.entitys.MinecartModular;

public abstract class ModuleSolarTop extends ModuleSolarBase {
	private float minVal;
	private float maxVal;
	private float minAngle;
	private float maxAngle;
	private float innerRotation;
	private float movingLevel;

	public ModuleSolarTop(final MinecartModular cart) {
		super(cart);
		this.minVal = -4.0f;
		this.maxVal = -13.0f;
		this.minAngle = 0.0f;
		this.maxAngle = 1.5707964f;
		this.innerRotation = 0.0f;
		this.movingLevel = this.minVal;
	}

	public float getInnerRotation() {
		return this.innerRotation;
	}

	public float getMovingLevel() {
		return this.movingLevel;
	}

	@Override
	public boolean updatePanels() {
		if (this.movingLevel > this.minVal) {
			this.movingLevel = this.minVal;
		}
		if (this.innerRotation < this.minAngle) {
			this.innerRotation = this.minAngle;
		} else if (this.innerRotation > this.maxAngle) {
			this.innerRotation = this.maxAngle;
		}
		final float targetAngle = this.isGoingDown() ? this.minAngle : this.maxAngle;
		if (this.movingLevel > this.maxVal && this.innerRotation != targetAngle) {
			this.movingLevel -= 0.2f;
			if (this.movingLevel <= this.maxVal) {
				this.movingLevel = this.maxVal;
			}
		} else if (this.innerRotation != targetAngle) {
			this.innerRotation += (this.isGoingDown() ? -0.05f : 0.05f);
			if ((!this.isGoingDown() && this.innerRotation >= targetAngle) || (this.isGoingDown() && this.innerRotation <= targetAngle)) {
				this.innerRotation = targetAngle;
			}
		} else if (this.movingLevel < this.minVal) {
			this.movingLevel += 0.2f;
			if (this.movingLevel >= this.minVal) {
				this.movingLevel = this.minVal;
			}
		}
		return this.innerRotation == this.maxAngle;
	}

	protected abstract int getPanelCount();
}
