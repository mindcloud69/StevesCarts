package vswe.stevesvehicles.client.sounds;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import vswe.stevesvehicles.vehicle.entity.EntityModularCart;

public class MinecartSoundMuter {
	public MinecartSoundMuter() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void soundPlay(PlaySoundEvent event) {
		ISound sound = event.getSound();
		if (sound instanceof MovingSoundMinecartRiding) {
			MovingSoundMinecartRiding cartSound = (MovingSoundMinecartRiding) sound;
			EntityMinecart cart = ReflectionHelper.getPrivateValue(MovingSoundMinecartRiding.class, cartSound, 1);
			if (cart instanceof EntityModularCart) {
				EntityModularCart modular = (EntityModularCart) cart;
				modular.setSound(cartSound, true);
			}
		} else if (sound instanceof MovingSoundMinecart) {
			MovingSoundMinecart cartSound = (MovingSoundMinecart) sound;
			EntityMinecart cart = ReflectionHelper.getPrivateValue(MovingSoundMinecart.class, cartSound, 0);
			if (cart instanceof EntityModularCart) {
				EntityModularCart modular = (EntityModularCart) cart;
				modular.setSound(cartSound, false);
			}
		}
	}
}
