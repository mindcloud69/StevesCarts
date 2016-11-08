package vswe.stevescarts.guis;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.blocks.tileentities.TileEntityDistributor;
import vswe.stevescarts.blocks.tileentities.TileEntityManager;
import vswe.stevescarts.containers.ContainerDistributor;
import vswe.stevescarts.helpers.DistributorSetting;
import vswe.stevescarts.helpers.DistributorSide;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;

@SideOnly(Side.CLIENT)
public class GuiDistributor extends GuiBase {
	private String mouseOverText;
	private static ResourceLocation texture;
	private int activeId;
	TileEntityDistributor distributor;
	InventoryPlayer invPlayer;

	public GuiDistributor(final InventoryPlayer invPlayer, final TileEntityDistributor distributor) {
		super(new ContainerDistributor(invPlayer, distributor));
		this.activeId = -1;
		this.invPlayer = invPlayer;
		this.setXSize(255);
		this.setYSize(186);
		this.distributor = distributor;
	}

	@Override
	public void drawGuiForeground(final int x, final int y) {
		GL11.glDisable(2896);
		this.getFontRenderer().drawString(Localization.GUI.DISTRIBUTOR.TITLE.translate(), 8, 6, 4210752);
		final TileEntityManager[] invs = this.distributor.getInventories();
		if (invs.length == 0) {
			this.getFontRenderer().drawString(Localization.GUI.DISTRIBUTOR.NOT_CONNECTED.translate(), 30, 40, 16728128);
		}
		if (this.mouseOverText != null && !this.mouseOverText.equals("")) {
			this.drawMouseOver(this.mouseOverText, x - this.getGuiLeft(), y - this.getGuiTop());
		}
		this.mouseOverText = null;
		GL11.glEnable(2896);
	}

	private void drawMouseMover(final String str, final int x, final int y, final int[] rect) {
		if (this.inRect(x, y, rect)) {
			this.mouseOverText = str;
		}
	}

	@Override
	public void drawGuiBackground(final float f, int x, int y) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		final int j = this.getGuiLeft();
		final int k = this.getGuiTop();
		ResourceHelper.bindResource(GuiDistributor.texture);
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
		x -= this.getGuiLeft();
		y -= this.getGuiTop();
		final TileEntityManager[] invs = this.distributor.getInventories();
		final ArrayList<DistributorSide> sides = this.distributor.getSides();
		int id = 0;
		for (final DistributorSide side : sides) {
			if (side.isEnabled(this.distributor)) {
				final int[] box = this.getSideBoxRect(id);
				int srcX = 0;
				if (this.inRect(x, y, box)) {
					srcX = box[2];
				}
				this.drawTexturedModalRect(j + box[0], k + box[1], srcX, this.ySize, box[2], box[3]);
				this.drawTexturedModalRect(j + box[0] + 2, k + box[1] + 2, box[2] * 2 + (box[2] - 4) * side.getId(), this.ySize, box[2] - 4, box[3] - 4);
				this.drawMouseMover(Localization.GUI.DISTRIBUTOR.SIDE.translate(side.getName()) + ((this.activeId != -1)
						? ("\n[" + Localization.GUI.DISTRIBUTOR.DROP_INSTRUCTION.translate() + "]")
								: ""), x, y, box);
				int settingCount = 0;
				for (final DistributorSetting setting : DistributorSetting.settings) {
					if (setting.isEnabled(this.distributor) && side.isSet(setting.getId())) {
						final int[] settingbox = this.getActiveSettingBoxRect(id, settingCount++);
						this.drawSetting(setting, settingbox, this.inRect(x, y, settingbox));
						this.drawMouseMover(setting.getName(invs) + "\n[" + Localization.GUI.DISTRIBUTOR.REMOVE_INSTRUCTION.translate() + "]", x, y, settingbox);
					}
				}
				++id;
			}
		}
		for (final DistributorSetting setting2 : DistributorSetting.settings) {
			if (setting2.isEnabled(this.distributor)) {
				final int[] box = this.getSettingBoxRect(setting2.getImageId(), setting2.getIsTop());
				this.drawSetting(setting2, box, this.inRect(x, y, box));
				this.drawMouseMover(setting2.getName(invs), x, y, box);
			}
		}
		if (this.activeId != -1) {
			final DistributorSetting setting3 = DistributorSetting.settings.get(this.activeId);
			this.drawSetting(setting3, new int[] { x - 8, y - 8, 16, 16 }, true);
		}
	}

	private void drawSetting(final DistributorSetting setting, final int[] box, final boolean hover) {
		final int j = this.getGuiLeft();
		final int k = this.getGuiTop();
		int srcX = 0;
		if (!setting.getIsTop()) {
			srcX += box[2] * 2;
		}
		if (hover) {
			srcX += box[2];
		}
		this.drawTexturedModalRect(j + box[0], k + box[1], srcX, this.ySize + this.getSideBoxRect(0)[3], box[2], box[3]);
		this.drawTexturedModalRect(j + box[0] + 1, k + box[1] + 1, box[2] * 4 + (box[2] - 2) * setting.getImageId(), this.ySize + this.getSideBoxRect(0)[3], box[2] - 2, box[3] - 2);
	}

	private int[] getSideBoxRect(final int i) {
		return new int[] { 20, 18 + i * 24, 22, 22 };
	}

	private int[] getSettingBoxRect(final int i, final boolean topRow) {
		return new int[] { 20 + i * 18, 143 + (topRow ? 0 : 18), 16, 16 };
	}

	private int[] getActiveSettingBoxRect(final int side, final int setting) {
		final int[] sideCoords = this.getSideBoxRect(side);
		return new int[] { sideCoords[0] + sideCoords[2] + 5 + setting * 18, sideCoords[1] + (sideCoords[3] - 16) / 2, 16, 16 };
	}

	@Override
	public void mouseClick(int x, int y, final int button) {
		super.mouseClick(x, y, button);
		x -= this.getGuiLeft();
		y -= this.getGuiTop();
		if (button == 0) {
			for (final DistributorSetting setting : DistributorSetting.settings) {
				if (setting.isEnabled(this.distributor)) {
					final int[] box = this.getSettingBoxRect(setting.getImageId(), setting.getIsTop());
					if (!this.inRect(x, y, box)) {
						continue;
					}
					this.activeId = setting.getId();
				}
			}
		}
	}

	@Override
	public void mouseMoved(int x, int y, final int button) {
		super.mouseMoved(x, y, button);
		x -= this.getGuiLeft();
		y -= this.getGuiTop();
		if (button == 0 && this.activeId != -1) {
			int id = 0;
			for (final DistributorSide side : this.distributor.getSides()) {
				if (side.isEnabled(this.distributor)) {
					final int[] box = this.getSideBoxRect(id++);
					if (this.inRect(x, y, box)) {
						this.distributor.sendPacket(0, new byte[] { (byte) this.activeId, (byte) side.getId() });
						break;
					}
					continue;
				}
			}
			this.activeId = -1;
		} else if (button == 1) {
			int id = 0;
			for (final DistributorSide side : this.distributor.getSides()) {
				if (side.isEnabled(this.distributor)) {
					int settingCount = 0;
					for (final DistributorSetting setting : DistributorSetting.settings) {
						if (setting.isEnabled(this.distributor) && side.isSet(setting.getId())) {
							final int[] settingbox = this.getActiveSettingBoxRect(id, settingCount++);
							if (!this.inRect(x, y, settingbox)) {
								continue;
							}
							this.distributor.sendPacket(1, new byte[] { (byte) setting.getId(), (byte) side.getId() });
						}
					}
					++id;
				}
			}
		}
	}

	static {
		GuiDistributor.texture = ResourceHelper.getResource("/gui/distributor.png");
	}
}
