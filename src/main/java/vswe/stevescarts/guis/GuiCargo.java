package vswe.stevescarts.guis;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.blocks.ModBlocks;
import vswe.stevescarts.blocks.tileentities.TileEntityCargo;
import vswe.stevescarts.containers.ContainerCargo;
import vswe.stevescarts.helpers.CargoItemSelection;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;

@SideOnly(Side.CLIENT)
public class GuiCargo extends GuiManager {
	private static ResourceLocation[] texturesLeft;
	private static ResourceLocation[] texturesRight;

	public GuiCargo(final InventoryPlayer invPlayer, final TileEntityCargo cargo) {
		super(invPlayer, cargo, new ContainerCargo(invPlayer, cargo));
		setXSize(305);
		setYSize(222);
	}

	@Override
	protected String getMaxSizeOverlay(final int id) {
		final int amount = getCargo().getAmount(id);
		final int type = getCargo().getAmountType(id);
		if (type == 0) {
			return Localization.GUI.CARGO.TRANSFER_ALL.translate();
		}
		if (type == 1) {
			return Localization.GUI.CARGO.TRANSFER_ITEMS.translate(String.valueOf(amount), String.valueOf(amount));
		}
		return Localization.GUI.CARGO.TRANSFER_STACKS.translate(String.valueOf(amount), String.valueOf(amount));
	}

	@Override
	protected String getMaxSizeText(final int id) {
		final int type = getCargo().getAmountType(id);
		String s;
		if (type == 0) {
			s = Localization.GUI.CARGO.TRANSFER_ALL_SHORT.translate();
		} else {
			final int amount = getCargo().getAmount(id);
			s = String.valueOf(amount);
			if (type == 1) {
				s = s + " " + Localization.GUI.CARGO.TRANSFER_ITEMS_SHORT.translate();
			} else {
				s = s + " " + Localization.GUI.CARGO.TRANSFER_STACKS_SHORT.translate();
			}
		}
		return s;
	}

	@Override
	protected void drawBackground(final int left, final int top) {
		int version;
		if (getManager().layoutType == 0) {
			version = 0;
		} else {
			version = 1;
		}
		ResourceHelper.bindResource(GuiCargo.texturesLeft[version]);
		drawTexturedModalRect(left, top, 0, 0, 256, ySize);
		ResourceHelper.bindResource(GuiCargo.texturesRight[version]);
		drawTexturedModalRect(left + 256, top, 0, 0, xSize - 256, ySize);
	}

	@Override
	protected int getArrowSourceX() {
		return 49;
	}

	@Override
	protected int getColorSourceX() {
		return 105;
	}

	@Override
	protected int getCenterTargetX() {
		return 98;
	}

	@Override
	protected void drawColors(final int id, final int color, final int left, final int top) {
		super.drawColors(id, color, left, top);
		if (getManager().layoutType == 2) {
			final int[] coords = getInvCoords(id);
			drawTexturedModalRect(left + coords[0] - 2, top + coords[1] - 2, 125, 56 * color, 92, 56);
		}
	}

	@Override
	protected void drawItems(final int id, final RenderItem renderitem, final int left, final int top) {
		ItemStack cartIcon = null;
		Label_0103:
		{
			if (getCargo().target[id] >= 0) {
				final int n = getCargo().target[id];
				getCargo();
				if (n < TileEntityCargo.itemSelections.size()) {
					getCargo();
					if (TileEntityCargo.itemSelections.get(getCargo().target[id]).getIcon() != null) {
						cartIcon = TileEntityCargo.itemSelections.get(getCargo().target[id]).getIcon();
						break Label_0103;
					}
				}
			}
			cartIcon = new ItemStack(Items.MINECART, 1);
		}
		final int[] coords = getBoxCoords(id);
		GL11.glDisable(2896);
		renderitem.renderItemIntoGUI(cartIcon, left + coords[0], top + coords[1]);
		GL11.glEnable(2896);
	}

	@Override
	protected int offsetObjectY(final int layout, final int x, final int y) {
		if (layout != 0) {
			return -5 + y * 10;
		}
		return super.offsetObjectY(layout, x, y);
	}

	@Override
	protected boolean sendOnClick(final int id, final int x, final int y, final byte data) {
		if (inRect(x, y, getBoxCoords(id))) {
			getManager().sendPacket(1, data);
			return true;
		}
		return false;
	}

	@Override
	protected void drawExtraOverlay(final int id, final int x, final int y) {
		if (getCargo().target[id] >= 0) {
			final int n = getCargo().target[id];
			getCargo();
			if (n < TileEntityCargo.itemSelections.size()) {
				getCargo();
				final CargoItemSelection item = TileEntityCargo.itemSelections.get(getCargo().target[id]);
				if (item.getName() != null) {
					drawMouseOver(Localization.GUI.CARGO.CHANGE_STORAGE_AREA.translate() + "\n" + Localization.GUI.MANAGER.CURRENT_SETTING.translate() + ": " + item.getName(), x, y, getBoxCoords(id));
				} else {
					drawMouseOver(Localization.GUI.CARGO.CHANGE_STORAGE_AREA.translate() + "\n" + Localization.GUI.MANAGER.CURRENT_SETTING.translate() + ": " + Localization.GUI.CARGO.UNKNOWN_AREA.translate(), x, y, getBoxCoords(id));
				}
				return;
			}
		}
		drawMouseOver(Localization.GUI.CARGO.CHANGE_STORAGE_AREA.translate() + "\n" + Localization.GUI.MANAGER.CURRENT_SETTING.translate() + ": " + Localization.GUI.CARGO.UNKNOWN_AREA.translate(), x, y, getBoxCoords(id));
	}

	@Override
	protected Block getBlock() {
		return ModBlocks.CARGO_MANAGER.getBlock();
	}

	@Override
	protected String getManagerName() {
		return Localization.GUI.CARGO.TITLE.translate();
	}

	private int[] getInvCoords(final int id) {
		final int x = id % 2;
		final int y = id / 2;
		final int xCoord = 8 + x * 198;
		final int yCoord = 11 + y * 64;
		return new int[] { xCoord, yCoord };
	}

	private TileEntityCargo getCargo() {
		return (TileEntityCargo) getManager();
	}

	@Override
	protected String getLayoutString() {
		return Localization.GUI.CARGO.CHANGE_SLOT_LAYOUT.translate();
	}

	@Override
	protected String getLayoutOption(final int id) {
		switch (id) {
			default: {
				return Localization.GUI.CARGO.LAYOUT_SHARED.translate();
			}
			case 1: {
				return Localization.GUI.CARGO.LAYOUT_SIDE.translate();
			}
			case 2: {
				return Localization.GUI.CARGO.LAYOUT_COLOR.translate();
			}
		}
	}

	static {
		GuiCargo.texturesLeft = new ResourceLocation[] { ResourceHelper.getResource("/gui/cargoVersion0Part1.png"), ResourceHelper.getResource("/gui/cargoVersion1Part1.png") };
		GuiCargo.texturesRight = new ResourceLocation[] { ResourceHelper.getResource("/gui/cargoVersion0Part2.png"), ResourceHelper.getResource("/gui/cargoVersion1Part2.png") };
	}
}
