package stevesvehicles.api;

import stevesvehicles.api.network.IPacketHandler;

public interface ISVPlugin {
	void register(ISVRegistry registry);

	void onRuntimeAvailable(IPacketHandler handler);
}
