package vswe.stevescarts.Interfaces;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Containers.ContainerUpgrade;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.TileEntities.TileEntityUpgrade;
import vswe.stevescarts.Upgrades.InterfaceEffect;
import vswe.stevescarts.Upgrades.InventoryEffect;

@SideOnly(Side.CLIENT)
public class GuiUpgrade extends GuiBase {
	private static ResourceLocation texture;
	private TileEntityUpgrade upgrade;
	private InventoryPlayer invPlayer;

	public GuiUpgrade(final InventoryPlayer invPlayer, final TileEntityUpgrade upgrade) {
		super(new ContainerUpgrade(invPlayer, upgrade));
		this.upgrade = upgrade;
		this.invPlayer = invPlayer;
		this.setXSize(256);
		this.setYSize(190);
	}

	@Override
	public void drawGuiForeground(final int x, final int y) {
		GL11.glDisable(2896);
		if (this.upgrade.getUpgrade() != null) {
			this.getFontRenderer().drawString(this.upgrade.getUpgrade().getName(), 8, 6, 4210752);
			final InterfaceEffect gui = this.upgrade.getUpgrade().getInterfaceEffect();
			if (gui != null) {
				gui.drawForeground(this.upgrade, this);
				gui.drawMouseOver(this.upgrade, this, x, y);
			}
		}
		GL11.glEnable(2896);
	}

	@Override
	public void drawGuiBackground(final float f, final int x, final int y) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		final int j = this.getGuiLeft();
		final int k = this.getGuiTop();
		ResourceHelper.bindResource(GuiUpgrade.texture);
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
		if (this.upgrade.getUpgrade() != null) {
			final InventoryEffect inventory = this.upgrade.getUpgrade().getInventoryEffect();
			if (inventory != null) {
				for (int i = 0; i < inventory.getInventorySize(); ++i) {
					this.drawTexturedModalRect(j + inventory.getSlotX(i) - 1, k + inventory.getSlotY(i) - 1, 0, this.ySize, 18, 18);
				}
			}
			final InterfaceEffect gui = this.upgrade.getUpgrade().getInterfaceEffect();
			if (gui != null) {
				gui.drawBackground(this.upgrade, this, x, y);
			}
		}
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	static {
		GuiUpgrade.texture = ResourceHelper.getResource("/gui/upgrade.png");
	}
}
