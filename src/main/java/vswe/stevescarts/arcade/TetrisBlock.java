package vswe.stevescarts.arcade;

import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.guis.GuiMinecart;

public class TetrisBlock {
	private int u;
	private int v;
	private GuiBase.RENDER_ROTATION r;

	public TetrisBlock(final int u, final int v) {
		this.u = u;
		this.v = v;
		this.r = GuiBase.RENDER_ROTATION.NORMAL;
	}

	public void render(final ArcadeTetris game, final GuiMinecart gui, final int x, final int y) {
		if (y >= 0) {
			game.getModule().drawImage(gui, 189 + x * 10, 9 + y * 10, this.u, this.v, 10, 10, this.r);
		}
	}

	public void rotate() {
		this.r = this.r.getNextRotation();
	}
}
