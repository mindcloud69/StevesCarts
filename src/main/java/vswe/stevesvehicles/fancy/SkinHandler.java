package vswe.stevesvehicles.fancy;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkinHandler extends FancyPancyHandler {
	public SkinHandler() {
		super("Skin");
	}

	@Override
	public String getDefaultUrl(AbstractClientPlayer player) {
		return null;
		//return AbstractClientPlayer.getSkinUrl(StringUtils.stripControlCodes(player.getDisplayName()));
	}

	@Override
	public ResourceLocation getDefaultResource(AbstractClientPlayer player) {
		return null;
		//return AbstractClientPlayer.getLocationSkin(StringUtils.stripControlCodes(player.getDisplayName()));
	}

	@Override
	public ThreadDownloadImageData getCurrentTexture(AbstractClientPlayer player) {
		return null;
		//return player.getTextureSkin();
	}

	@Override
	public ResourceLocation getCurrentResource(AbstractClientPlayer player) {
		return player.getLocationSkin();
	}

	@Override
	public void setCurrentResource(AbstractClientPlayer player, ResourceLocation resource, String url) {
		ReflectionHelper.setPrivateValue(AbstractClientPlayer.class, player, resource, 3);
		//TODO: Fix
		//ReflectionHelper.setPrivateValue(AbstractClientPlayer.class, player, tryToDownloadFancy(resource, url, AbstractClientPlayer.locationStevePng, new ImageBufferDownload()), 1);
	}

	@Override
	public LoadType getDefaultLoadType() {
		return LoadType.OVERRIDE;
	}

	@Override
	public String getDefaultUrl() {
		return "http://skins.minecraft.net/MinecraftSkins/";
	}


}
