package stevesvehicles.api;

public interface ISVPlugin {
	void register(ISVRegistry registry);

	/**
	 * For register things like module containers
	 */
	void postRegister(ISVRegistry registry);
}
