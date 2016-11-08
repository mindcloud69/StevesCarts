package vswe.stevescarts.Helpers;

public class EnchantmentData {
	private EnchantmentInfo type;
	private int value;

	public EnchantmentData(final EnchantmentInfo type) {
		this.type = type;
		this.value = 0;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(final int val) {
		this.value = val;
	}

	public EnchantmentInfo getEnchantment() {
		return this.type;
	}

	public void setEnchantment(final EnchantmentInfo info) {
		this.type = info;
	}

	public void damageEnchant(final int dmg) {
		this.damageEnchantLevel(dmg, this.getValue(), 1);
	}

	private boolean damageEnchantLevel(final int dmg, final int value, final int level) {
		if (level > this.type.getEnchantment().getMaxLevel() || value <= 0) {
			return false;
		}
		final int levelvalue = this.getEnchantment().getValue(level);
		if (!this.damageEnchantLevel(dmg, value - levelvalue, level + 1)) {
			int dmgdealt = dmg * (int) Math.pow(2.0, level - 1);
			if (dmgdealt > value) {
				dmgdealt = value;
			}
			this.setValue(this.getValue() - dmgdealt);
		}
		return true;
	}

	public int getLevel() {
		int value = this.getValue();
		for (int i = 0; i < this.type.getEnchantment().getMaxLevel(); ++i) {
			if (value <= 0) {
				return i;
			}
			value -= this.getEnchantment().getValue(i + 1);
		}
		return this.type.getEnchantment().getMaxLevel();
	}

	public String getInfoText() {
		int value = this.getValue();
		int level = 0;
		int percentage = 0;
		for (level = 1; level <= this.type.getEnchantment().getMaxLevel(); ++level) {
			if (value > 0) {
				final int levelvalue = this.getEnchantment().getValue(level);
				percentage = 100 * value / levelvalue;
				value -= levelvalue;
				if (value < 0) {
					break;
				}
			}
		}
		return "�E" + this.getEnchantment().getEnchantment().getTranslatedName(this.getLevel()) + "\n" + percentage + "% left of this tier";
	}
}
