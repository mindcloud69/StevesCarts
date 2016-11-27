package stevesvehicles.client.rendering.models.blocks;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.common.blocks.BlockCartAssembler;
import stevesvehicles.common.blocks.PropertyUpgrades.Upgrades;
import stevesvehicles.common.blocks.tileentitys.assembler.UpgradeContainer;
import stevesvehicles.common.core.Constants;
import stevesvehicles.common.upgrades.Upgrade;
import stevesvehicles.common.upgrades.registries.UpgradeRegistry;

public class ModelAssembler implements IBakedModel {

	private static IModel upgradeModel;
	private static IModel upgradeTopModel;
	private static IModel upgradeBotModel;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void bakeModels(ModelBakeEvent event) {
		upgradeModel = null;
		upgradeTopModel = null;
		upgradeBotModel = null;
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		if(upgradeModel == null){
			upgradeModel = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(Constants.MOD_ID, "block/upgrade"));
			upgradeTopModel = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(Constants.MOD_ID, "block/upgrade_top"));
			upgradeBotModel = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(Constants.MOD_ID, "block/upgrade_bot"));
		}
		BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		Function<ResourceLocation, TextureAtlasSprite> textureGetter = (ResourceLocation r) -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(r.toString());
		IBakedModel assemblerModel = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(Constants.MOD_ID, "block/assembler")).bake(ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, textureGetter);
		List<BakedQuad> quads = assemblerModel.getQuads(state, side, rand);
		if(state instanceof IExtendedBlockState){
			Upgrades upgrades = ((IExtendedBlockState)state).getValue(BlockCartAssembler.UPGRADES);
			for(EnumFacing facing : EnumFacing.VALUES){
				Integer type = upgrades.upgrades.get(facing);
				Upgrade upgrade = type == null ? null : UpgradeRegistry.getUpgradeFromId(type);
				addUpgradeQuads(quads, facing, upgrade, state, side, rand);
			}
		}
		return quads;
	}
	
	private void addUpgradeQuads(List<BakedQuad> quads, EnumFacing upgradeSide, Upgrade upgrade, IBlockState state, EnumFacing side, long rand){
		Function<ResourceLocation, TextureAtlasSprite> textureGetter;
		if(upgrade == null){
			textureGetter = (ResourceLocation r) -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(r.toString());
		}else{
			textureGetter = (ResourceLocation r) -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(upgrade.getIcon());
		}
		switch (upgradeSide) {
			case UP:
				quads.addAll(upgradeTopModel.bake(ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, textureGetter).getQuads(state, side, rand));
				break;
			case DOWN:
				quads.addAll(upgradeBotModel.bake(ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, textureGetter).getQuads(state, side, rand));
				break;
			case EAST:
				quads.addAll(upgradeModel.bake(ModelRotation.X0_Y180, DefaultVertexFormats.BLOCK, textureGetter).getQuads(state, side, rand));
				break;
			case WEST:
				quads.addAll(upgradeModel.bake(ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, textureGetter).getQuads(state, side, rand));
				break;
			case NORTH:
				quads.addAll(upgradeModel.bake(ModelRotation.X0_Y90, DefaultVertexFormats.BLOCK, textureGetter).getQuads(state, side, rand));
				break;
			case SOUTH:
				quads.addAll(upgradeModel.bake(ModelRotation.X0_Y270, DefaultVertexFormats.BLOCK, textureGetter).getQuads(state, side, rand));
				break;
			default:
				break;
		}
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Constants.MOD_ID + ":blocks/assembler/bot");
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}
}
