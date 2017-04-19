package vswe.stevescarts.guis;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.blocks.tileentities.TileEntityUpgrade;
import vswe.stevescarts.containers.ContainerUpgrade;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.upgrades.InterfaceEffect;
import vswe.stevescarts.upgrades.InventoryEffect;

@SideOnly(Side.CLIENT)
public class GuiUpgrade extends GuiBase {
	private static ResourceLocation texture;
	private TileEntityUpgrade upgrade;
	private InventoryPlayer invPlayer;

	public GuiUpgrade(final InventoryPlayer invPlayer, final TileEntityUpgrade upgrade) {
		super(new ContainerUpgrade(invPlayer, upgrade));
		this.upgrade = upgrade;
		this.invPlayer = invPlayer;
		setXSize(256);
		setYSize(190);
	}

	@Override
	public void drawGuiForeground(final int x, final int y) {
		GL11.glDisable(2896);
		if (upgrade.getUpgrade() != null) {
			getFontRenderer().drawString(upgrade.getUpgrade().getName(), 8, 6, 4210752);
			final InterfaceEffect gui = upgrade.getUpgrade().getInterfaceEffect();
			if (gui != null) {
				gui.drawForeground(upgrade, this);
				gui.drawMouseOver(upgrade, this, x, y);
			}
		}
		GL11.glEnable(2896);
	}

	@Override
	public void drawGuiBackground(final float f, final int x, final int y) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		final int j = getGuiLeft();
		final int k = getGuiTop();
		ResourceHelper.bindResource(GuiUpgrade.texture);
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		if (upgrade.getUpgrade() != null) {
			final InventoryEffect inventory = upgrade.getUpgrade().getInventoryEffect();
			if (inventory != null) {
				for (int i = 0; i < inventory.getInventorySize(); ++i) {
					drawTexturedModalRect(j + inventory.getSlotX(i) - 1, k + inventory.getSlotY(i) - 1, 0, ySize, 18, 18);
				}
			}
			final InterfaceEffect gui = upgrade.getUpgrade().getInterfaceEffect();
			if (gui != null) {
				gui.drawBackground(upgrade, this, x, y);
			}
		}
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	static {
		GuiUpgrade.texture = ResourceHelper.getResource("/gui/upgrade.png");
	}
}
