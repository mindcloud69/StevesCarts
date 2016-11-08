package vswe.stevescarts.Helpers;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import vswe.stevescarts.Carts.MinecartModular;

public class MinecartSoundMuter {
	public MinecartSoundMuter() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void soundPlay(final PlaySoundEvent event) {
		final ISound sound = event.getSound();
		if (sound instanceof MovingSoundMinecartRiding) {
			final MovingSoundMinecartRiding cartSound = (MovingSoundMinecartRiding) sound;
			final EntityMinecart cart = (EntityMinecart) ReflectionHelper.getPrivateValue((Class) MovingSoundMinecartRiding.class, cartSound, 1);
			if (cart instanceof MinecartModular) {
				final MinecartModular modular = (MinecartModular) cart;
				modular.setSound(cartSound, true);
			}
		} else if (sound instanceof MovingSoundMinecart) {
			final MovingSoundMinecart cartSound2 = (MovingSoundMinecart) sound;
			final EntityMinecart cart = (EntityMinecart) ReflectionHelper.getPrivateValue((Class) MovingSoundMinecart.class, cartSound2, 0);
			if (cart instanceof MinecartModular) {
				final MinecartModular modular = (MinecartModular) cart;
				modular.setSound(cartSound2, false);
			}
		}
	}
}
