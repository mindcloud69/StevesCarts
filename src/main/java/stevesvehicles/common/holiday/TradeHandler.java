package stevesvehicles.common.holiday;

import java.util.Random;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.common.core.Constants;
import stevesvehicles.common.items.ComponentTypes;

public class TradeHandler implements ITradeList {
	public static VillagerProfession santaProfession;

	public TradeHandler() {
		santaProfession = new VillagerProfession(Constants.MOD_ID + ":santa", ResourceHelper.getResource("/models/santa.png").toString(), ResourceHelper.getResource("/models/santa_zombie.png").toString());
		VillagerCareer career = new VillagerCareer(santaProfession, "santa");
		VillagerRegistry.instance().register(santaProfession);
		career.addTrade(1, this);
	}

	@Override
	public void addMerchantRecipe(IMerchant p_190888_1_, MerchantRecipeList recipeList, Random p_190888_3_) {
		recipeList.add(new MerchantRecipe(ComponentTypes.STOLEN_PRESENT.getItemStack(3), ComponentTypes.GREEN_WRAPPING_PAPER.getItemStack()));
	}
}
