package vswe.stevescarts.renders.model;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ModelGenerator {

	private FaceBakery faceBakery = new FaceBakery();

	public List<ItemIconInfo> itemIcons = new ArrayList<>();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre event) {
		itemIcons.clear();
		TextureMap textureMap = event.getMap();
		for (TexturedItem item : ItemModelManager.items) {
			for (int i = 0; i < item.getMaxMeta(); i++) {
				String name = item.getTextureName(i);
				TextureAtlasSprite texture = textureMap.getTextureExtry(name);
				if (texture == null) {
					texture = new CustomTexture(name);
					textureMap.setTextureEntry(name, texture);
				}
				ItemIconInfo info = new ItemIconInfo((Item) item, i, texture, name);
				itemIcons.add(info);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void bakeModels(ModelBakeEvent event) {
		if (Minecraft.getMinecraft().getRenderItem() == null || Minecraft.getMinecraft().getRenderItem().getItemModelMesher() == null) {
			return;
		}
		ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		for (Object object : ItemModelManager.items) {
			if (object instanceof Item && object instanceof TexturedItem) {
				TexturedItem iTexturedItem = (TexturedItem) object;
				Item item = (Item) object;
				for (int i = 0; i < iTexturedItem.getMaxMeta(); i++) {
					TextureAtlasSprite texture = null;
					ItemIconInfo itemIconInfo = null;
					for (ItemIconInfo info : itemIcons) {
						if (info.damage == i && info.getItem() == item && info.isBucket == false) {
							texture = info.getSprite();
							itemIconInfo = info;
							break;
						}
					}
					if (texture == null) {
						break;
					}

					ModelResourceLocation inventory;
					inventory = getItemInventoryResourceLocation(item);

					if (iTexturedItem.getMaxMeta() != 1) {
						if (getModel(new ItemStack(item, 1, i)) != null) {
							inventory = getModel(new ItemStack(item, 1, i));
							ModelBakery.registerItemVariants(item, inventory);
						}
					}

					final TextureAtlasSprite finalTexture = texture;
					Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> finalTexture;
					ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
					builder.add(new ResourceLocation(itemIconInfo.textureName));
					ItemLayerModel itemLayerModel = new ItemLayerModel(builder.build());
					IBakedModel model = itemLayerModel.bake(ItemLayerModel.INSTANCE.getDefaultState(), DefaultVertexFormats.ITEM, textureGetter);

					ItemCameraTransforms transforms = null;
					try {
						transforms = loadTransformFromJson(new ResourceLocation("minecraft:models/item/generated"));
					} catch (IOException e) {
						e.printStackTrace();
					}

					ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> map = IPerspectiveAwareModel.MapWrapper.getTransforms(transforms);
					IPerspectiveAwareModel iPerspectiveAwareModel = new IPerspectiveAwareModel.MapWrapper(model, map);
					itemModelMesher.register(item, i, inventory);
					event.getModelRegistry().putObject(inventory, iPerspectiveAwareModel);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static ModelResourceLocation getItemInventoryResourceLocation(Item block) {
		return new ModelResourceLocation(Item.REGISTRY.getNameForObject(block), "inventory");
	}

	public static ItemCameraTransforms loadTransformFromJson(ResourceLocation location) throws IOException {
		return ModelBlock.deserialize(getReaderForResource(location)).getAllTransforms();
	}

	public ModelResourceLocation getModel(ItemStack stack) {
		return new ModelResourceLocation(Constants.MOD_ID + ":" + stack.getItem().getUnlocalizedName(stack).substring(5), "inventory");
	}

	public static Reader getReaderForResource(ResourceLocation location) throws IOException {
		ResourceLocation file = new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".json");
		IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(file);
		return new BufferedReader(new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8));
	}

	class CustomTexture extends TextureAtlasSprite {
		public CustomTexture(String spriteName) {
			super(spriteName);
		}
	}

	//Item
	class ItemIconInfo {

		Item item;
		int damage;
		TextureAtlasSprite sprite;
		String textureName;

		public boolean isBucket = false;

		public Item getItem() {
			return item;
		}

		public TextureAtlasSprite getSprite() {
			return sprite;
		}

		public ItemIconInfo(Item item, int damage, TextureAtlasSprite sprite, String textureName) {
			this.item = item;
			this.damage = damage;
			this.sprite = sprite;
			this.textureName = textureName;
		}

	}
}
