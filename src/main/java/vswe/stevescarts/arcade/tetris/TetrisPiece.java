package vswe.stevescarts.arcade.tetris;

import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.arcade.ArcadeGame;
import vswe.stevescarts.guis.GuiMinecart;

public class TetrisPiece {
	private TetrisPiecePart[] parts;
	private int x;
	private int y;
	private String sound;
	private float volume;
	private int rotationOffset;

	private TetrisPiece(final TetrisPiecePart[] parts) {
		this.parts = parts;
		x = 5;
		y = -2;
	}

	public static TetrisPiece createPiece(final int type) {
		String sound = null;
		float volume = 0.5f;
		int rotationOffset = 0;
		TetrisPiecePart[] parts = null;
		switch (type) {
			case 0: {
				parts = createEndermanParts();
				sound = "mob.endermen.hit";
				break;
			}
			case 1: {
				parts = createSlimeParts();
				sound = "mob.slime.big";
				rotationOffset = 1;
				break;
			}
			case 2: {
				parts = createWitherParts();
				sound = "mob.wither.hurt";
				volume = 0.25f;
				break;
			}
			case 3: {
				parts = createWitchParts();
				sound = "mob.cat.hitt";
				break;
			}
			case 4: {
				parts = createPigParts();
				sound = "mob.pig.say";
				break;
			}
			case 5: {
				parts = createSteveParts();
				sound = "damage.hit";
				break;
			}
			case 6: {
				parts = createSheepParts();
				sound = "mob.sheep.say";
				break;
			}
			default: {
				return null;
			}
		}
		final TetrisPiece piece = new TetrisPiece(parts);
		piece.sound = sound;
		piece.rotationOffset = rotationOffset;
		piece.volume = volume;
		return piece;
	}

	private static TetrisPiecePart[] createEndermanParts() {
		final TetrisPiecePart[] parts = { new TetrisPiecePart(new TetrisBlock(0, 0), 0, -1), new TetrisPiecePart(new TetrisBlock(0, 10), 0, 0), new TetrisPiecePart(new TetrisBlock(0, 20), 0, 1),
			new TetrisPiecePart(new TetrisBlock(0, 30), 0, 2) };
		return parts;
	}

	private static TetrisPiecePart[] createSlimeParts() {
		final TetrisPiecePart[] parts = { new TetrisPiecePart(new TetrisBlock(10, 0), 0, 0), new TetrisPiecePart(new TetrisBlock(20, 0), 1, 0), new TetrisPiecePart(new TetrisBlock(10, 10), 0, 1),
			new TetrisPiecePart(new TetrisBlock(20, 10), 1, 1) };
		return parts;
	}

	private static TetrisPiecePart[] createWitherParts() {
		final TetrisPiecePart[] parts = { new TetrisPiecePart(new TetrisBlock(30, 0), -1, 0), new TetrisPiecePart(new TetrisBlock(40, 0), 0, 0), new TetrisPiecePart(new TetrisBlock(50, 0), 1, 0),
			new TetrisPiecePart(new TetrisBlock(40, 10), 0, 1) };
		return parts;
	}

	private static TetrisPiecePart[] createWitchParts() {
		final TetrisPiecePart[] parts = { new TetrisPiecePart(new TetrisBlock(70, 0), 0, -1), new TetrisPiecePart(new TetrisBlock(70, 10), 0, 0), new TetrisPiecePart(new TetrisBlock(70, 20), 0, 1),
			new TetrisPiecePart(new TetrisBlock(60, 20), -1, 1) };
		return parts;
	}

	private static TetrisPiecePart[] createPigParts() {
		final TetrisPiecePart[] parts = { new TetrisPiecePart(new TetrisBlock(80, 0), 0, -1), new TetrisPiecePart(new TetrisBlock(80, 10), 0, 0), new TetrisPiecePart(new TetrisBlock(80, 20), 0, 1),
			new TetrisPiecePart(new TetrisBlock(90, 20), 1, 1) };
		return parts;
	}

	private static TetrisPiecePart[] createSteveParts() {
		final TetrisPiecePart[] parts = { new TetrisPiecePart(new TetrisBlock(100, 0), -1, -1), new TetrisPiecePart(new TetrisBlock(110, 0), 0, -1),
			new TetrisPiecePart(new TetrisBlock(110, 10), 0, 0), new TetrisPiecePart(new TetrisBlock(120, 10), 1, 0) };
		return parts;
	}

	private static TetrisPiecePart[] createSheepParts() {
		final TetrisPiecePart[] parts = { new TetrisPiecePart(new TetrisBlock(130, 10), -1, 1), new TetrisPiecePart(new TetrisBlock(140, 10), 0, 1), new TetrisPiecePart(new TetrisBlock(140, 0), 0, 0),
			new TetrisPiecePart(new TetrisBlock(150, 0), 1, 0) };
		return parts;
	}

	public void render(final ArcadeTetris game, final GuiMinecart gui) {
		for (int i = 0; i < parts.length; ++i) {
			parts[i].render(game, gui, x, y);
		}
	}

	public void rotate(final TetrisBlock[][] board) {
		for (int i = 0; i < parts.length; ++i) {
			if (!parts[i].canRotate(board, x, y, rotationOffset)) {
				return;
			}
		}
		for (int i = 0; i < parts.length; ++i) {
			parts[i].rotate(rotationOffset);
		}
	}

	public MOVE_RESULT move(final ArcadeTetris game, final TetrisBlock[][] board, final int offX, final int offY, final boolean placeOnFail) {
		for (int i = 0; i < parts.length; ++i) {
			if (!parts[i].canMoveTo(board, x + offX, y + offY)) {
				boolean isGameOver = false;
				if (placeOnFail) {
					for (int j = 0; j < parts.length; ++j) {
						if (parts[j].canPlaceInBoard(y)) {
							parts[j].placeInBoard(board, x, y);
						} else {
							isGameOver = true;
						}
					}
					if (StevesCarts.instance.useArcadeMobSounds) {
						if (sound != null) {
							ArcadeGame.playDefaultSound(sound, volume, (game.getModule().getCart().rand.nextFloat() - game.getModule().getCart().rand.nextFloat()) * 0.2f + 1.0f);
						}
					} else {
						ArcadeGame.playSound("boop", 1.0f, 1.0f);
					}
				}
				return isGameOver ? MOVE_RESULT.GAME_OVER : MOVE_RESULT.FAIL;
			}
		}
		x += offX;
		y += offY;
		return MOVE_RESULT.SUCCESS;
	}

	public enum MOVE_RESULT {
		SUCCESS,
		FAIL,
		GAME_OVER
	}
}
