package vswe.stevescarts.Arcade;

import vswe.stevescarts.Interfaces.GuiMinecart;

public class TetrisPiecePart {
	private TetrisBlock block;
	private int offX;
	private int offY;

	public TetrisPiecePart(final TetrisBlock block, final int offX, final int offY) {
		this.block = block;
		this.offX = offX;
		this.offY = offY;
	}

	public void render(final ArcadeTetris game, final GuiMinecart gui, final int x, final int y) {
		this.block.render(game, gui, x + this.offX, y + this.offY);
	}

	public void rotate(final int offSet) {
		this.block.rotate();
		final int temp = this.offX;
		this.offX = -this.offY + offSet;
		this.offY = temp;
	}

	public void placeInBoard(final TetrisBlock[][] board, final int x, final int y) {
		board[x + this.offX][y + this.offY] = this.block;
	}

	public boolean canMoveTo(final TetrisBlock[][] board, final int x, final int y) {
		return this.isValidAt(board, x + this.offX, y + this.offY);
	}

	public boolean isValidAt(final TetrisBlock[][] board, final int x, final int y) {
		return x >= 0 && x < board.length && y < board[0].length && (y < 0 || board[x][y] == null);
	}

	public boolean canRotate(final TetrisBlock[][] board, final int x, final int y, final int offSet) {
		return this.isValidAt(board, x - this.offY + offSet, y + this.offX);
	}

	public boolean canPlaceInBoard(final int y) {
		return y + this.offY >= 0;
	}
}
