package vswe.stevescarts.handlers;

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
	public static void playDefaultSound(final String name, SoundCategory category, final float volume, final float pitch) {
		final ISound soundObj = new PlayerSound(Minecraft.getMinecraft().player, category, name, volume, pitch);
		Minecraft.getMinecraft().getSoundHandler().playSound(soundObj);
	}

	public static void playSound(final String name, SoundCategory category, final float volume, final float pitch) {
		playDefaultSound("stevescarts:" + name, category, volume, pitch);
	}

	private static class PlayerSound extends PositionedSound {
		private EntityPlayer player;

		protected PlayerSound(final EntityPlayer player, SoundCategory category, final String name, final float volume, final float pitch) {
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
