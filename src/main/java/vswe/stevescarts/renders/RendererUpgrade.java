package vswe.stevescarts.renders;
//package vswe.stevescarts.Renders;
//
//import net.minecraft.block.Block;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.world.IBlockAccess;
//import net.minecraftforge.fml.client.registry.RenderingRegistry;
//import vswe.stevescarts.Blocks.BlockCartAssembler;
//import vswe.stevescarts.Blocks.BlockUpgrade;
//import vswe.stevescarts.TileEntities.TileEntityCartAssembler;
//import vswe.stevescarts.TileEntities.TileEntityUpgrade;
//
//public class RendererUpgrade implements ISimpleBlockRenderingHandler {
//	private int id;
//
//	public RendererUpgrade() {
//		this.id = RenderingRegistry.getNextAvailableRenderId();
//		RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler) this);
//	}
//
//	public void renderInventoryBlock(final Block block, final int metadata, final int modelID, final RenderBlocks renderer) {
//	}
//
//	public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelId, final RenderBlocks renderer) {
//		final TileEntity te = world.getTileEntity(x, y, z);
//		if (te instanceof TileEntityCartAssembler) {
//			final TileEntityCartAssembler assembler = (TileEntityCartAssembler) te;
//			final BlockCartAssembler b = (BlockCartAssembler) block;
//			renderer.renderStandardBlock(block, x, y, z);
//			return true;
//		}
//		if (te instanceof TileEntityUpgrade) {
//			final TileEntityUpgrade upgrade = (TileEntityUpgrade) te;
//			final BlockUpgrade b2 = (BlockUpgrade) block;
//			final int side = b2.setUpgradeBounds(world, x, y, z);
//			Tessellator.instance.setColorOpaque_F(1.0f, 1.0f, 1.0f);
//			if (side != -1) {
//				renderer.renderFaceYPos(block, (double) x, (double) y, (double) z, upgrade.getTexture(side == 0));
//				renderer.renderFaceYNeg(block, (double) x, (double) y, (double) z, upgrade.getTexture(side == 1));
//				renderer.renderFaceZNeg(block, (double) x, (double) y, (double) z, upgrade.getTexture(side == 2));
//				renderer.renderFaceXPos(block, (double) x, (double) y, (double) z, upgrade.getTexture(side == 3));
//				renderer.renderFaceZPos(block, (double) x, (double) y, (double) z, upgrade.getTexture(side == 4));
//				renderer.renderFaceXNeg(block, (double) x, (double) y, (double) z, upgrade.getTexture(side == 5));
//			}
//			return true;
//		}
//		return false;
//	}
//
//	public boolean shouldRender3DInInventory(final int modelId) {
//		return false;
//	}
//
//	public int getRenderId() {
//		return this.id;
//	}
//}
