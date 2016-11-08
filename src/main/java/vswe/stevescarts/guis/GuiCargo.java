package vswe.stevescarts.guis;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
		this.setXSize(305);
		this.setYSize(222);
	}

	@Override
	protected String getMaxSizeOverlay(final int id) {
		final int amount = this.getCargo().getAmount(id);
		final int type = this.getCargo().getAmountType(id);
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
		final int type = this.getCargo().getAmountType(id);
		String s;
		if (type == 0) {
			s = Localization.GUI.CARGO.TRANSFER_ALL_SHORT.translate();
		} else {
			final int amount = this.getCargo().getAmount(id);
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
		if (this.getManager().layoutType == 0) {
			version = 0;
		} else {
			version = 1;
		}
		ResourceHelper.bindResource(GuiCargo.texturesLeft[version]);
		this.drawTexturedModalRect(left, top, 0, 0, 256, this.ySize);
		ResourceHelper.bindResource(GuiCargo.texturesRight[version]);
		this.drawTexturedModalRect(left + 256, top, 0, 0, this.xSize - 256, this.ySize);
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
		if (this.getManager().layoutType == 2) {
			final int[] coords = this.getInvCoords(id);
			this.drawTexturedModalRect(left + coords[0] - 2, top + coords[1] - 2, 125, 56 * color, 92, 56);
		}
	}

	@Override
	protected void drawItems(final int id, final RenderItem renderitem, final int left, final int top) {
		ItemStack cartIcon = null;
		Label_0103:
		{
			if (this.getCargo().target[id] >= 0) {
				final int n = this.getCargo().target[id];
				this.getCargo();
				if (n < TileEntityCargo.itemSelections.size()) {
					this.getCargo();
					if (TileEntityCargo.itemSelections.get(this.getCargo().target[id]).getIcon() != null) {
						cartIcon = TileEntityCargo.itemSelections.get(this.getCargo().target[id]).getIcon();
						break Label_0103;
					}
				}
			}
			cartIcon = new ItemStack(Items.MINECART, 1);
		}
		final int[] coords = this.getBoxCoords(id);
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
		if (this.inRect(x, y, this.getBoxCoords(id))) {
			this.getManager().sendPacket(1, data);
			return true;
		}
		return false;
	}

	@Override
	protected void drawExtraOverlay(final int id, final int x, final int y) {
		if (this.getCargo().target[id] >= 0) {
			final int n = this.getCargo().target[id];
			this.getCargo();
			if (n < TileEntityCargo.itemSelections.size()) {
				this.getCargo();
				final CargoItemSelection item = TileEntityCargo.itemSelections.get(this.getCargo().target[id]);
				if (item.getName() != null) {
					this.drawMouseOver(Localization.GUI.CARGO.CHANGE_STORAGE_AREA.translate() + "\n" + Localization.GUI.MANAGER.CURRENT_SETTING.translate() + ": " + item.getName(), x, y, this.getBoxCoords(id));
				} else {
					this.drawMouseOver(Localization.GUI.CARGO.CHANGE_STORAGE_AREA.translate() + "\n" + Localization.GUI.MANAGER.CURRENT_SETTING.translate() + ": " + Localization.GUI.CARGO.UNKNOWN_AREA.translate(), x, y, this.getBoxCoords(id));
				}
				return;
			}
		}
		this.drawMouseOver(Localization.GUI.CARGO.CHANGE_STORAGE_AREA.translate() + "\n" + Localization.GUI.MANAGER.CURRENT_SETTING.translate() + ": " + Localization.GUI.CARGO.UNKNOWN_AREA.translate(), x, y, this.getBoxCoords(id));
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
		return (TileEntityCargo) this.getManager();
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
