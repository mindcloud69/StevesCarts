package stevesvehicles.common.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config {
	
	private Configuration config;
	
	public Config(Configuration config) {
		this.config = config;
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (event.getModID().equals(Constants.MOD_ID)) {
			Log.info("Updating config...");
			loadConfig(false);
		}
	}
	
	public void loadConfig(boolean load) {
		try {
			if (load) {
				load();
			}
		} catch (Exception e) {
			Log.err("Steve's Vehicles has a problem loading it's configuration", e);
		} finally {
			save();
		}
	}
	
	private void save(){
		if (config.hasChanged()) {
			config.save();
		}
	}
	
	private void load(){
		config.load();
	}
}
