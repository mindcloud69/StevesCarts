package vswe.stevescarts.arcade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.realtimers.ModuleArcade;

public class ArcadeTetris extends ArcadeGame {
	private TetrisBlock[][] board;
	private TetrisPiece piece;
	private static String[] removalSounds;
	private int ticks;
	private boolean isPlaying;
	private boolean quickMove;
	private int gameOverTicks;
	private int highscore;
	private int score;
	private int removed;
	private int[] removedByAmount;
	private int delay;
	private int piecesSinceDelayChange;
	private boolean newHighScore;
	public static final int BOARD_START_X = 189;
	public static final int BOARD_START_Y = 9;
	private static String texture;

	public ArcadeTetris(final ModuleArcade module) {
		super(module, Localization.ARCADE.STACKER);
		this.ticks = 0;
		this.isPlaying = true;
		this.quickMove = false;
		this.delay = 10;
		this.newgame();
	}

	private void newgame() {
		this.board = new TetrisBlock[10][15];
		this.generatePiece();
		this.isPlaying = true;
		this.ticks = 0;
		this.quickMove = false;
		this.score = 0;
		this.removed = 0;
		this.removedByAmount = new int[4];
		this.delay = 10;
		this.piecesSinceDelayChange = 0;
		this.newHighScore = false;
	}

	private void generatePiece() {
		this.piece = TetrisPiece.createPiece(this.getModule().getCart().rand.nextInt(7));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void update() {
		super.update();
		if (this.isPlaying) {
			if (this.ticks == 0 || this.quickMove) {
				if (this.piece != null) {
					final TetrisPiece.MOVE_RESULT result = this.piece.move(this, this.board, 0, 1, true);
					if (result == TetrisPiece.MOVE_RESULT.FAIL) {
						this.piece = null;
						int removedCount = 0;
						for (int y = 0; y < this.board[0].length; ++y) {
							boolean valid = true;
							for (int x = 0; x < this.board.length; ++x) {
								if (this.board[x][y] == null) {
									valid = false;
									break;
								}
							}
							if (valid) {
								for (int y2 = y; y2 >= 0; --y2) {
									for (int x2 = 0; x2 < this.board.length; ++x2) {
										final TetrisBlock value = (y2 == 0) ? null : this.board[x2][y2 - 1];
										this.board[x2][y2] = value;
									}
								}
								++removedCount;
							}
						}
						if (removedCount > 0) {
							this.removed += removedCount;
							final int[] removedByAmount = this.removedByAmount;
							final int n = removedCount - 1;
							++removedByAmount[n];
							this.score += removedCount * removedCount * 100;
							ArcadeGame.playSound(ArcadeTetris.removalSounds[removedCount - 1], 1.0f, 1.0f);
						}
						this.quickMove = false;
						++this.piecesSinceDelayChange;
						if (this.piecesSinceDelayChange == 8) {
							this.piecesSinceDelayChange = 0;
							if (this.delay > 0) {
								--this.delay;
							}
						}
					} else if (result == TetrisPiece.MOVE_RESULT.GAME_OVER) {
						this.piece = null;
						this.isPlaying = false;
						this.quickMove = false;
						this.gameOverTicks = 0;
						this.newHighScore();
						ArcadeGame.playSound("gameover", 1.0f, 1.0f);
					}
				} else {
					this.generatePiece();
				}
				this.ticks = this.delay;
			} else {
				--this.ticks;
			}
		} else if (this.gameOverTicks < 170) {
			this.gameOverTicks = Math.min(170, this.gameOverTicks + 5);
		} else if (this.newHighScore) {
			ArcadeGame.playSound("highscore", 1.0f, 1.0f);
			this.newHighScore = false;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource(ArcadeTetris.texture);
		this.getModule().drawImage(gui, 187, 7, 0, 40, 104, 154);
		for (int i = 0; i < this.board.length; ++i) {
			for (int j = 0; j < this.board[0].length; ++j) {
				final TetrisBlock b = this.board[i][j];
				if (b != null) {
					b.render(this, gui, i, j);
				}
			}
		}
		if (this.piece != null) {
			this.piece.render(this, gui);
		}
		if (!this.isPlaying) {
			final int graphicalValue = Math.min(this.gameOverTicks, 150);
			this.getModule().drawImage(gui, 189, 159 - graphicalValue, 104, 40, 100, graphicalValue);
			if (graphicalValue == 150 && this.getModule().inRect(x, y, new int[] { 189, 9, 100, 150 })) {
				this.getModule().drawImage(gui, 213, 107, 0, 194, 54, 34);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void keyPress(final GuiMinecart gui, final char character, final int extraInformation) {
		if (this.piece != null) {
			if (Character.toLowerCase(character) == 'w') {
				this.piece.rotate(this.board);
			} else if (Character.toLowerCase(character) == 'a') {
				this.piece.move(this, this.board, -1, 0, false);
			} else if (Character.toLowerCase(character) == 'd') {
				this.piece.move(this, this.board, 1, 0, false);
			} else if (Character.toLowerCase(character) == 's') {
				this.quickMove = true;
			}
		}
		if (Character.toLowerCase(character) == 'r') {
			this.newgame();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (button == 0 && !this.isPlaying && this.gameOverTicks >= 150 && this.getModule().inRect(x, y, new int[] { 189, 9, 100, 150 })) {
			this.newgame();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.getModule().drawString(gui, Localization.ARCADE.HIGH_SCORE.translate(String.valueOf(this.highscore)), 10, 20, 4210752);
		this.getModule().drawString(gui, Localization.ARCADE.SCORE.translate(String.valueOf(this.score)), 10, 40, 4210752);
		this.getModule().drawString(gui, Localization.ARCADE.REMOVED_LINES.translate(String.valueOf(this.removed)), 10, 60, 4210752);
		for (int i = 0; i < 4; ++i) {
			this.getModule().drawString(gui, Localization.ARCADE.REMOVED_LINES_COMBO.translate(String.valueOf(i), String.valueOf(this.removedByAmount[i])), 10, 80 + i * 10, 4210752);
		}
		this.getModule().drawString(gui, "W - " + Localization.ARCADE.INSTRUCTION_ROTATE.translate(), 340, 20, 4210752);
		this.getModule().drawString(gui, "A - " + Localization.ARCADE.INSTRUCTION_LEFT.translate(), 340, 30, 4210752);
		this.getModule().drawString(gui, "S - " + Localization.ARCADE.INSTRUCTION_DROP.translate(), 340, 40, 4210752);
		this.getModule().drawString(gui, "D - " + Localization.ARCADE.INSTRUCTION_RIGHT.translate(), 340, 50, 4210752);
		this.getModule().drawString(gui, "R - " + Localization.ARCADE.INSTRUCTION_RESTART.translate(), 340, 70, 4210752);
	}

	private void newHighScore() {
		if (this.score > this.highscore) {
			final int val = this.score / 100;
			final byte byte1 = (byte) (val & 0xFF);
			final byte byte2 = (byte) ((val & 0xFF00) >> 8);
			this.getModule().sendPacket(1, new byte[] { byte1, byte2 });
			this.newHighScore = true;
		}
	}

	@Override
	public void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 1) {
			short data2 = data[0];
			short data3 = data[1];
			if (data2 < 0) {
				data2 += 256;
			}
			if (data3 < 0) {
				data3 += 256;
			}
			this.highscore = (data2 | data3 << 8) * 100;
		}
	}

	@Override
	public void checkGuiData(final Object[] info) {
		this.getModule().updateGuiData(info, TrackStory.stories.size(), (short) (this.highscore / 100));
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == TrackStory.stories.size()) {
			this.highscore = data * 100;
		}
	}

	@Override
	public void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setShort(this.getModule().generateNBTName("Highscore", id), (short) this.highscore);
	}

	@Override
	public void Load(final NBTTagCompound tagCompound, final int id) {
		this.highscore = tagCompound.getShort(this.getModule().generateNBTName("Highscore", id));
	}

	static {
		ArcadeTetris.removalSounds = new String[] { "1lines", "2lines", "3lines", "4lines" };
		ArcadeTetris.texture = "/gui/tetris.png";
	}
}
