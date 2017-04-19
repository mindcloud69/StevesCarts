package vswe.stevescarts.guis;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.blocks.ModBlocks;
import vswe.stevescarts.blocks.tileentities.TileEntityLiquid;
import vswe.stevescarts.containers.ContainerLiquid;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;

@SideOnly(Side.CLIENT)
public class GuiLiquid extends GuiManager {
	private static ResourceLocation texture;
	private static ResourceLocation textureExtra;

	public GuiLiquid(final InventoryPlayer invPlayer, final TileEntityLiquid liquid) {
		super(invPlayer, liquid, new ContainerLiquid(invPlayer, liquid));
		setXSize(230);
		setYSize(222);
	}

	@Override
	protected String getMaxSizeOverlay(final int id) {
		final int amount = getLiquid().getMaxAmount(id);
		if (!getLiquid().hasMaxAmount(id)) {
			return Localization.GUI.LIQUID.TRANSFER_ALL.translate();
		}
		return Localization.GUI.LIQUID.TRANSFER_BUCKETS.translate(getMaxSizeText(id));
	}

	@Override
	protected String getMaxSizeText(final int id) {
		if (!getLiquid().hasMaxAmount(id)) {
			return Localization.GUI.LIQUID.TRANSFER_ALL_SHORT.translate();
		}
		String B = String.valueOf(getLiquid().getMaxAmount(id) / 1000.0f);
		if (B.charAt(B.length() - 1) == '0') {
			B = B.substring(0, B.length() - 2);
		} else if (B.charAt(0) == '0') {
			B = B.substring(1);
		}
		return B + Localization.GUI.LIQUID.TRANSFER_BUCKET_SHORT.translate();
	}

	@Override
	protected void drawBackground(final int left, final int top) {
		ResourceHelper.bindResource(GuiLiquid.texture);
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		if (getLiquid().getTanks() != null) {
			for (int i = 0; i < 4; ++i) {
				final int[] coords = getTankCoords(i);
				getLiquid().getTanks()[i].drawFluid(this, coords[0], coords[1]);
			}
		}
		ResourceHelper.bindResource(GuiLiquid.textureExtra);
		int version;
		if (getManager().layoutType == 0) {
			version = 0;
		} else {
			version = 1;
		}
		for (int j = 0; j < 2; ++j) {
			drawTexturedModalRect(left + ((j == 0) ? 27 : 171), top + 63, 0, 102 + version * 12, 32, 12);
		}
		for (int j = 0; j < 4; ++j) {
			final int[] coords2 = getTankCoords(j);
			final int type = j % 2;
			drawTexturedModalRect(left + coords2[0], top + coords2[1], 0, 51 * type, 36, 51);
		}
	}

	@Override
	protected int getArrowSourceX() {
		return 72;
	}

	@Override
	protected int getColorSourceX() {
		return 128;
	}

	@Override
	protected int getCenterTargetX() {
		return 62;
	}

	@Override
	protected void drawColors(final int id, final int color, final int left, final int top) {
		super.drawColors(id, color, left, top);
		if (getManager().layoutType == 2) {
			final int[] coords = getTankCoords(id);
			drawTexturedModalRect(left + coords[0], top + coords[1], 36, 51 * color, 36, 51);
		}
	}

	@Override
	protected int offsetObjectY(final int layout, final int x, final int y) {
		return -5 + y * 10;
	}

	@Override
	protected void drawExtraOverlay(final int id, final int x, final int y) {
		drawMouseOver(getLiquid().getTanks()[id].getMouseOver(), x, y, getTankCoords(id));
	}

	@Override
	protected Block getBlock() {
		return ModBlocks.LIQUID_MANAGER.getBlock();
	}

	@Override
	protected String getManagerName() {
		return Localization.GUI.LIQUID.TITLE.translate();
	}

	private int[] getTankCoords(final int id) {
		final int x = id % 2;
		final int y = id / 2;
		final int xCoord = 25 + x * 144;
		final int yCoord = 12 + y * 63;
		return new int[] { xCoord, yCoord, 36, 51 };
	}

	private TileEntityLiquid getLiquid() {
		return (TileEntityLiquid) getManager();
	}

	@Override
	protected String getLayoutString() {
		return Localization.GUI.LIQUID.CHANGE_LAYOUT.translate();
	}

	@Override
	protected String getLayoutOption(final int id) {
		switch (id) {
			default: {
				return Localization.GUI.LIQUID.LAYOUT_ALL.translate();
			}
			case 1: {
				return Localization.GUI.LIQUID.LAYOUT_SIDE.translate();
			}
			case 2: {
				return Localization.GUI.LIQUID.LAYOUT_COLOR.translate();
			}
		}
	}

	static {
		GuiLiquid.texture = ResourceHelper.getResource("/gui/liquidmanager.png");
		GuiLiquid.textureExtra = ResourceHelper.getResource("/gui/liquidmanagerExtra.png");
	}
}
