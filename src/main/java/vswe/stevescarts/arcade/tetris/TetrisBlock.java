package vswe.stevescarts.arcade.tetris;

import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.guis.GuiMinecart;

public class TetrisBlock {
	private int u;
	private int v;
	private GuiBase.RENDER_ROTATION r;

	public TetrisBlock(final int u, final int v) {
		this.u = u;
		this.v = v;
		r = GuiBase.RENDER_ROTATION.NORMAL;
	}

	public void render(final ArcadeTetris game, final GuiMinecart gui, final int x, final int y) {
		if (y >= 0) {
			game.getModule().drawImage(gui, 189 + x * 10, 9 + y * 10, u, v, 10, 10, r);
		}
	}

	public void rotate() {
		r = r.getNextRotation();
	}
}
