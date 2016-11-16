package vswe.stevesvehicles.client.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundHandler {
	public static void playDefaultSound(String name, SoundCategory category, float volume, float pitch) {
		ISound soundObj = new PlayerSound(Minecraft.getMinecraft().player, category, name, volume, pitch);
		Minecraft.getMinecraft().getSoundHandler().playSound(soundObj);
	}

	@SuppressWarnings("SpellCheckingInspection")
	public static void playSound(String name, SoundCategory category, float volume, float pitch) {
		playDefaultSound("stevescarts:" + name, category, volume, pitch);
	}

	private static class PlayerSound extends PositionedSound {
		private EntityPlayer player;

		protected PlayerSound(EntityPlayer player, SoundCategory category, String name, float volume, float pitch) {
			super(new ResourceLocation(name), category);
			this.player = player;
			this.volume = volume;
			this.pitch = pitch;
			update();
		}

		private void update() {
			xPosF = (float) player.posX;
			yPosF = (float) player.posY;
			zPosF = (float) player.posZ;
		}
	}
}
