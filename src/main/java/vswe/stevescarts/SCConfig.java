package vswe.stevescarts;

import net.minecraftforge.common.config.Configuration;

/**
 * Created by modmuss50 on 31/01/2017.
 */
public class SCConfig {

	public static boolean disableTimedCrafting = false;

	public static void load(Configuration config) {
		disableTimedCrafting = config.get("Settings", "DisableCartAssemberTime", false, "Set to true to disable the timer in the cart assember, it will still require fuel").getBoolean(false);
	}

}
