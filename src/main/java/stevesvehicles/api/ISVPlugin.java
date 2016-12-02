package stevesvehicles.api;

import stevesvehicles.api.network.IPacketHandler;

public interface ISVPlugin {
	void register(ISVRegistry registry);

	/**
	 * For register things like module containers
	 */
	void postRegister(ISVRegistry registry);

	void onRuntimeAvailable(IPacketHandler handler);
}
