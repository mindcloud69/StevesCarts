package vswe.stevescarts.Interfaces;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.PacketHandler;
import vswe.stevescarts.Containers.ContainerActivator;
import vswe.stevescarts.Helpers.ActivatorOption;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.TileEntities.TileEntityActivator;

@SideOnly(Side.CLIENT)
public class GuiActivator extends GuiBase {
	private static ResourceLocation texture;
	TileEntityActivator activator;
	InventoryPlayer invPlayer;

	public GuiActivator(final InventoryPlayer invPlayer, final TileEntityActivator activator) {
		super(new ContainerActivator(invPlayer, activator));
		this.invPlayer = invPlayer;
		this.setXSize(255);
		this.setYSize(222);
		this.activator = activator;
	}

	@Override
	public void drawGuiForeground(final int x, final int y) {
		GL11.glDisable(2896);
		this.getFontRenderer().drawString(Localization.GUI.TOGGLER.TITLE.translate(), 8, 6, 4210752);
		for (int i = 0; i < this.activator.getOptions().size(); ++i) {
			final ActivatorOption option = this.activator.getOptions().get(i);
			final int[] box = this.getBoxRect(i);
			this.getFontRenderer().drawString(option.getName(), box[0] + box[2] + 6, box[1] + 4, 4210752);
		}
		for (int i = 0; i < this.activator.getOptions().size(); ++i) {
			final ActivatorOption option = this.activator.getOptions().get(i);
			final int[] box = this.getBoxRect(i);
			this.drawMouseMover(option.getInfo(), x, y, box);
		}
		GL11.glEnable(2896);
	}

	private void drawMouseMover(final String str, final int x, final int y, final int[] rect) {
		if (this.inRect(x - this.getGuiLeft(), y - this.getGuiTop(), rect)) {
			this.drawMouseOver(str, x - this.getGuiLeft(), y - this.getGuiTop());
		}
	}

	@Override
	public void drawGuiBackground(final float f, int x, int y) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		final int j = this.getGuiLeft();
		final int k = this.getGuiTop();
		ResourceHelper.bindResource(GuiActivator.texture);
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
		x -= this.getGuiLeft();
		y -= this.getGuiTop();
		for (int i = 0; i < this.activator.getOptions().size(); ++i) {
			final ActivatorOption option = this.activator.getOptions().get(i);
			final int[] box = this.getBoxRect(i);
			int srcX = 0;
			if (this.inRect(x, y, box)) {
				srcX = 16;
			}
			this.drawTexturedModalRect(j + box[0], k + box[1], srcX, this.ySize, box[2], box[3]);
			this.drawTexturedModalRect(j + box[0] + 1, k + box[1] + 1, (box[2] - 2) * option.getOption(), this.ySize + box[3], box[2] - 2, box[3] - 2);
		}
	}

	private int[] getBoxRect(final int i) {
		return new int[] { 20, 22 + i * 20, 16, 16 };
	}

	@Override
	public void mouseClick(int x, int y, final int button) {
		super.mouseClick(x, y, button);
		x -= this.getGuiLeft();
		y -= this.getGuiTop();
		for (int i = 0; i < this.activator.getOptions().size(); ++i) {
			final int[] box = this.getBoxRect(i);
			if (this.inRect(x, y, box)) {
				byte data = (byte) ((button != 0) ? 1 : 0);
				data |= (byte) (i << 1);
				PacketHandler.sendPacket(0, new byte[] { data });
			}
		}
	}

	static {
		GuiActivator.texture = ResourceHelper.getResource("/gui/activator.png");
	}
}
