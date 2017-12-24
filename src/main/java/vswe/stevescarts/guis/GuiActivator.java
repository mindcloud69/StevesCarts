package vswe.stevescarts.guis;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vswe.stevescarts.blocks.tileentities.TileEntityActivator;
import vswe.stevescarts.containers.ContainerActivator;
import vswe.stevescarts.helpers.ActivatorOption;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.packet.PacketStevesCarts;

@SideOnly(Side.CLIENT)
public class GuiActivator extends GuiBase {
	private static ResourceLocation texture;
	TileEntityActivator activator;
	InventoryPlayer invPlayer;

	public GuiActivator(final InventoryPlayer invPlayer, final TileEntityActivator activator) {
		super(new ContainerActivator(invPlayer, activator));
		this.invPlayer = invPlayer;
		setXSize(255);
		setYSize(222);
		this.activator = activator;
	}

	@Override
	public void drawGuiForeground(final int x, final int y) {
		GL11.glDisable(2896);
		getFontRenderer().drawString(Localization.GUI.TOGGLER.TITLE.translate(), 8, 6, 4210752);
		for (int i = 0; i < activator.getOptions().size(); ++i) {
			final ActivatorOption option = activator.getOptions().get(i);
			final int[] box = getBoxRect(i);
			getFontRenderer().drawString(option.getName(), box[0] + box[2] + 6, box[1] + 4, 4210752);
		}
		for (int i = 0; i < activator.getOptions().size(); ++i) {
			final ActivatorOption option = activator.getOptions().get(i);
			final int[] box = getBoxRect(i);
			drawMouseMover(option.getInfo(), x, y, box);
		}
		GL11.glEnable(2896);
	}

	private void drawMouseMover(final String str, final int x, final int y, final int[] rect) {
		if (inRect(x - getGuiLeft(), y - getGuiTop(), rect)) {
			drawMouseOver(str, x - getGuiLeft(), y - getGuiTop());
		}
	}

	@Override
	public void drawGuiBackground(final float f, int x, int y) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		final int j = getGuiLeft();
		final int k = getGuiTop();
		ResourceHelper.bindResource(GuiActivator.texture);
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		x -= getGuiLeft();
		y -= getGuiTop();
		for (int i = 0; i < activator.getOptions().size(); ++i) {
			final ActivatorOption option = activator.getOptions().get(i);
			final int[] box = getBoxRect(i);
			int srcX = 0;
			if (inRect(x, y, box)) {
				srcX = 16;
			}
			drawTexturedModalRect(j + box[0], k + box[1], srcX, ySize, box[2], box[3]);
			drawTexturedModalRect(j + box[0] + 1, k + box[1] + 1, (box[2] - 2) * option.getOption(), ySize + box[3], box[2] - 2, box[3] - 2);
		}
	}

	private int[] getBoxRect(final int i) {
		return new int[] { 20, 22 + i * 20, 16, 16 };
	}

	@Override
	public void mouseClick(int x, int y, final int button) {
		super.mouseClick(x, y, button);
		x -= getGuiLeft();
		y -= getGuiTop();
		for (int i = 0; i < activator.getOptions().size(); ++i) {
			final int[] box = getBoxRect(i);
			if (inRect(x, y, box)) {
				byte data = (byte) ((button != 0) ? 1 : 0);
				data |= (byte) (i << 1);
				PacketStevesCarts.sendPacket(0, new byte[] { data });
			}
		}
	}

	static {
		GuiActivator.texture = ResourceHelper.getResource("/gui/activator.png");
	}
}
