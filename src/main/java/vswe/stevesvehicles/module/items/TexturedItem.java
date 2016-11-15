package vswe.stevesvehicles.module.items;

/**
 * Use this to apply a texture to an item
 */
public interface TexturedItem {

	/**
	 * @param damage the damage of the item that the texture is being loaded for
	 * @return the Texture name in the form of "modid:items/textureName"
	 */
	String getTextureName(int damage);

	/**
	 * @return Return 1 if basic item, else return the max damage
	 */
	int getMaxMeta();
}