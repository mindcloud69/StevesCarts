package stevesvehicles.common.upgrades;

import stevesvehicles.common.upgrades.effects.BaseEffect;

public class EffectType {
	private Class<? extends BaseEffect> clazz;
	private Object[] params;

	public EffectType(Class<? extends BaseEffect> clazz, Object[] params) {
		this.clazz = clazz;
		this.params = params;
	}

	public Class<? extends BaseEffect> getClazz() {
		return clazz;
	}

	public Object[] getParams() {
		return params;
	}
}
