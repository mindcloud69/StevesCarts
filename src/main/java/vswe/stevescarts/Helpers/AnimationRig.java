package vswe.stevescarts.Helpers;

import java.util.ArrayList;

public class AnimationRig {
	private ArrayList<AnimationRigVal> rigs;

	public AnimationRig() {
		this.rigs = new ArrayList<AnimationRigVal>();
	}

	public boolean update(final boolean goDown) {
		if (goDown) {
			for (int i = this.rigs.size() - 1; i >= 0; --i) {
				if (this.rigs.get(i).update(goDown)) {
					return false;
				}
			}
			return false;
		}
		for (int i = 0; i < this.rigs.size(); ++i) {
			if (this.rigs.get(i).update(goDown)) {
				return false;
			}
		}
		return true;
	}

	public void addVal(final AnimationRigVal val) {
		this.rigs.add(val);
	}
}
