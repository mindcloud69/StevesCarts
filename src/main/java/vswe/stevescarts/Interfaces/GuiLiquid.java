package vswe.stevescarts.Interfaces;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Blocks.ModBlocks;
import vswe.stevescarts.Containers.ContainerLiquid;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.TileEntities.TileEntityLiquid;

@SideOnly(Side.CLIENT)
public class GuiLiquid extends GuiManager {
	private static ResourceLocation texture;
	private static ResourceLocation textureExtra;

	public GuiLiquid(final InventoryPlayer invPlayer, final TileEntityLiquid liquid) {
		super(invPlayer, liquid, new ContainerLiquid(invPlayer, liquid));
		this.setXSize(230);
		this.setYSize(222);
	}

	@Override
	protected String getMaxSizeOverlay(final int id) {
		final int amount = this.getLiquid().getMaxAmount(id);
		if (!this.getLiquid().hasMaxAmount(id)) {
			return Localization.GUI.LIQUID.TRANSFER_ALL.translate();
		}
		return Localization.GUI.LIQUID.TRANSFER_BUCKETS.translate(this.getMaxSizeText(id));
	}

	@Override
	protected String getMaxSizeText(final int id) {
		if (!this.getLiquid().hasMaxAmount(id)) {
			return Localization.GUI.LIQUID.TRANSFER_ALL_SHORT.translate();
		}
		String B = String.valueOf(this.getLiquid().getMaxAmount(id) / 1000.0f);
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
		this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);
		if (this.getLiquid().getTanks() != null) {
			for (int i = 0; i < 4; ++i) {
				final int[] coords = this.getTankCoords(i);
				this.getLiquid().getTanks()[i].drawFluid(this, coords[0], coords[1]);
			}
		}
		ResourceHelper.bindResource(GuiLiquid.textureExtra);
		int version;
		if (this.getManager().layoutType == 0) {
			version = 0;
		} else {
			version = 1;
		}
		for (int j = 0; j < 2; ++j) {
			this.drawTexturedModalRect(left + ((j == 0) ? 27 : 171), top + 63, 0, 102 + version * 12, 32, 12);
		}
		for (int j = 0; j < 4; ++j) {
			final int[] coords2 = this.getTankCoords(j);
			final int type = j % 2;
			this.drawTexturedModalRect(left + coords2[0], top + coords2[1], 0, 51 * type, 36, 51);
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
		if (this.getManager().layoutType == 2) {
			final int[] coords = this.getTankCoords(id);
			this.drawTexturedModalRect(left + coords[0], top + coords[1], 36, 51 * color, 36, 51);
		}
	}

	@Override
	protected int offsetObjectY(final int layout, final int x, final int y) {
		return -5 + y * 10;
	}

	@Override
	protected void drawExtraOverlay(final int id, final int x, final int y) {
		this.drawMouseOver(this.getLiquid().getTanks()[id].getMouseOver(), x, y, this.getTankCoords(id));
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
		return (TileEntityLiquid) this.getManager();
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
