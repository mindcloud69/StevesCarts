package stevesvehicles.common.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config {
	public static int maxDynamites = 50;
	public static boolean useArcadeSounds;
	public static boolean useArcadeMobSounds;
	//Categories
	public static final String SETTINGS = "Settings";

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
			processConfig();
		} catch (Exception e) {
			Log.err("Steve's Vehicles has a problem loading it's configuration", e);
		} finally {
			save();
		}
	}

	public void processConfig() {
		maxDynamites = Math.min(maxDynamites, config.get(SETTINGS, "MaximumNumberOfDynamites", maxDynamites).getInt(maxDynamites));
		useArcadeSounds = config.get(SETTINGS, "useArcadeSounds", true).getBoolean(true);
		useArcadeMobSounds = config.get(SETTINGS, "useTetrisMobSounds", true).getBoolean(true);
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
