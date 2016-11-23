package vswe.stevesvehicles.upgrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.recipe.IRecipeOutput;
import vswe.stevesvehicles.recipe.ShapedModuleRecipe;
import vswe.stevesvehicles.upgrade.effect.BaseEffect;
import vswe.stevesvehicles.upgrade.registry.UpgradeRegistry;

public class Upgrade implements IRecipeOutput, Comparable<Upgrade> {
	// private static HashMap<Byte, IIcon> sides;
	private static HashMap<Class<? extends BaseEffect>, IEffectInfo> effectInfo;
	private String icon;

	public static void registerInfo(Class<? extends BaseEffect> clazz, IEffectInfo info) {
		if (effectInfo == null) {
			effectInfo = new HashMap<>();
		}
		effectInfo.put(clazz, info);
	}

	public void addInfo(List<String> info) {
		for (EffectType effect : effects) {
			IEffectInfo entry = effectInfo.get(effect.getClazz());
			if (entry != null) {
				String name = entry.getName(effect.getParams());
				if (name != null) {
					info.add(name);
				}
			}
		}
	}

	private String fullUnlocalizedName;

	public final String getFullRawUnlocalizedName() {
		return fullUnlocalizedName;
	}

	public final void setFullRawUnlocalizedName(String val) {
		fullUnlocalizedName = val;
	}

	private final String unlocalizedName;
	private ArrayList<EffectType> effects;
	private final boolean connectToRedstone;

	public Upgrade(String unlocalizedName) {
		this(unlocalizedName, false);
	}
	
	public Upgrade(String unlocalizedName, boolean connectToRedstone) {
		this.unlocalizedName = unlocalizedName;
		this.connectToRedstone = connectToRedstone;
		effects = new ArrayList<>();
	}

	public String getUnlocalizedNameForItem() {
		return "steves_vehicles:tile.upgrade." + getFullRawUnlocalizedName();
	}

	public String getUnlocalizedName() {
		return getUnlocalizedNameForItem() + ".name";
	}

	public String getTranslatedName() {
		return I18n.translateToLocal(getUnlocalizedName());
	}

	public String getName() {
		return unlocalizedName;
	}

	public Upgrade addEffect(Class<? extends BaseEffect> effect, Object... params) {
		effects.add(new EffectType(effect, params));
		return this;
	}

	public static boolean disableRecipes;

	public Upgrade addRecipe(IRecipe recipe) {
		if (!disableRecipes) {
			GameRegistry.addRecipe(recipe);
		}
		return this;
	}

	public Upgrade addShapedRecipeWithSize(int width, int height, Object... recipe) {
		addRecipe(new ShapedModuleRecipe(this, width, height, recipe));
		return this;
	}

	public Upgrade addShapedRecipe(Object... recipe) {
		if (recipe.length == 9) {
			addShapedRecipeWithSize(3, 3, recipe);
		} else if (recipe.length == 4) {
			addShapedRecipeWithSize(2, 2, recipe);
		}
		return this;
	}

	@Override
	public ItemStack getItemStack() {
		return getItemStack(1);
	}

	protected ItemStack getItemStack(int count) {
		return new ItemStack(ModItems.upgrades, count, UpgradeRegistry.getIdFromUpgrade(this));
	}

	public ArrayList<EffectType> getEffectTypes() {
		return effects;
	}

	public final String getRawUnlocalizedName() {
		return unlocalizedName;
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean connectToRedstone() {
		return connectToRedstone;
	}

	@Override
	public int compareTo(Upgrade o) {
		if (o == null) {
			return 1;
		} else if (o == this) {
			return 0;
		}
		return UpgradeRegistry.getIdFromUpgrade(this) > UpgradeRegistry.getIdFromUpgrade(o) ? 1 : -1;
	}
}
