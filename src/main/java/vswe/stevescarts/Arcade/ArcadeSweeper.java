package vswe.stevescarts.arcade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.realtimers.ModuleArcade;

public class ArcadeSweeper extends ArcadeGame {
	private Tile[][] tiles;
	protected boolean isPlaying;
	protected boolean hasFinished;
	private int currentGameType;
	private int ticks;
	protected int creepersLeft;
	protected int emptyLeft;
	private boolean hasStarted;
	private int[] highscore;
	private int highscoreTicks;
	private static String textureMenu;

	public ArcadeSweeper(final ModuleArcade module) {
		super(module, Localization.ARCADE.CREEPER);
		this.highscore = new int[] { 999, 999, 999 };
		this.newGame(this.currentGameType);
	}

	private void newGame(final int size) {
		switch (size) {
			case 0: {
				this.newGame(9, 9, 10);
				break;
			}
			case 1: {
				this.newGame(16, 16, 40);
				break;
			}
			case 2: {
				this.newGame(30, 16, 99);
				break;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void update() {
		super.update();
		if (this.hasStarted && this.isPlaying && !this.hasFinished && this.ticks < 19980) {
			++this.ticks;
		}
		if (this.highscoreTicks > 0) {
			++this.highscoreTicks;
			if (this.highscoreTicks == 78) {
				this.highscoreTicks = 0;
				ArcadeGame.playSound("highscore", 1.0f, 1.0f);
			}
		}
	}

	private void newGame(final int width, final int height, final int totalCreepers) {
		this.isPlaying = true;
		this.ticks = 0;
		this.creepersLeft = totalCreepers;
		this.emptyLeft = width * height - totalCreepers;
		this.hasStarted = false;
		this.hasFinished = false;
		this.highscoreTicks = 0;
		this.tiles = new Tile[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				this.tiles[x][y] = new Tile(this);
			}
		}
		for (int creepers = 0; creepers < totalCreepers; ++creepers) {
			final int x2 = this.getModule().getCart().rand.nextInt(width);
			final int y2 = this.getModule().getCart().rand.nextInt(height);
			if (!this.tiles[x2][y2].isCreeper()) {
				this.tiles[x2][y2].setCreeper();
			}
		}
		for (int x2 = 0; x2 < width; ++x2) {
			for (int y2 = 0; y2 < height; ++y2) {
				if (!this.tiles[x2][y2].isCreeper()) {
					int count = 0;
					for (int i = -1; i <= 1; ++i) {
						for (int j = -1; j <= 1; ++j) {
							if (i != 0 || j != 0) {
								final int x3 = x2 + i;
								final int y3 = y2 + j;
								if (x3 >= 0 && y3 >= 0 && x3 < width && y3 < height && this.tiles[x3][y3].isCreeper()) {
									++count;
								}
							}
						}
					}
					this.tiles[x2][y2].setNearbyCreepers(count);
				}
			}
		}
	}

	private int getMarginLeft() {
		return (443 - this.tiles.length * 10) / 2;
	}

	private int getMarginTop() {
		return (168 - this.tiles[0].length * 10) / 2;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource(ArcadeSweeper.textureMenu);
		for (int i = 0; i < this.tiles.length; ++i) {
			for (int j = 0; j < this.tiles[0].length; ++j) {
				this.tiles[i][j].draw(this, gui, this.getMarginLeft() + i * 10, this.getMarginTop() + j * 10, x, y);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void mouseClicked(final GuiMinecart gui, int x, int y, final int button) {
		if (!this.isPlaying) {
			return;
		}
		x -= this.getMarginLeft();
		y -= this.getMarginTop();
		final int xc = x / 10;
		final int yc = y / 10;
		if (button == 0) {
			this.openTile(xc, yc, true);
		} else if (button == 1 && this.isValidCoordinate(xc, yc)) {
			this.hasStarted = true;
			ArcadeGame.playSound("flagclick", 1.0f, 1.0f);
			this.tiles[xc][yc].mark();
		} else if (button == 2 && this.isValidCoordinate(xc, yc) && this.tiles[xc][yc].getState() == Tile.TILE_STATE.OPENED) {
			ArcadeGame.playSound("click", 1.0f, 1.0f);
			int nearby = this.tiles[xc][yc].getNearbyCreepers();
			if (nearby != 0) {
				for (int i = -1; i <= 1; ++i) {
					for (int j = -1; j <= 1; ++j) {
						if ((i != 0 || j != 0) && this.isValidCoordinate(xc + i, yc + j) && this.tiles[xc + i][yc + j].getState() == Tile.TILE_STATE.FLAGGED) {
							--nearby;
						}
					}
				}
				if (nearby == 0) {
					for (int i = -1; i <= 1; ++i) {
						for (int j = -1; j <= 1; ++j) {
							if (i != 0 || j != 0) {
								this.openTile(xc + i, yc + j, false);
							}
						}
					}
				}
			}
		}
	}

	private boolean isValidCoordinate(final int x, final int y) {
		return x >= 0 && y >= 0 && x < this.tiles.length && y < this.tiles[0].length;
	}

	private void openTile(final int x, final int y, final boolean first) {
		if (this.isValidCoordinate(x, y)) {
			this.hasStarted = true;
			final Tile.TILE_OPEN_RESULT result = this.tiles[x][y].open();
			if (this.emptyLeft == 0) {
				this.hasFinished = true;
				this.isPlaying = false;
				ArcadeGame.playSound("goodjob", 1.0f, 1.0f);
				if (this.highscore[this.currentGameType] > this.ticks / 20) {
					this.highscoreTicks = 1;
					final int val = this.ticks / 20;
					final byte byte1 = (byte) (val & 0xFF);
					final byte byte2 = (byte) ((val & 0xFF00) >> 8);
					this.getModule().sendPacket(3, new byte[] { (byte) this.currentGameType, byte1, byte2 });
				}
			} else if (result == Tile.TILE_OPEN_RESULT.BLOB) {
				if (first) {
					ArcadeGame.playSound("blobclick", 1.0f, 1.0f);
				}
				for (int i = -1; i <= 1; ++i) {
					for (int j = -1; j <= 1; ++j) {
						this.openTile(x + i, y + j, false);
					}
				}
			} else if (result == Tile.TILE_OPEN_RESULT.DEAD) {
				this.isPlaying = false;
				ArcadeGame.playDefaultSound("random.explode", 1.0f, (1.0f + (this.getModule().getCart().rand.nextFloat() - this.getModule().getCart().rand.nextFloat()) * 0.2f) * 0.7f);
			} else if (result == Tile.TILE_OPEN_RESULT.OK && first) {
				ArcadeGame.playSound("click", 1.0f, 1.0f);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void keyPress(final GuiMinecart gui, final char character, final int extraInformation) {
		if (Character.toLowerCase(character) == 'r') {
			this.newGame(this.currentGameType);
		} else if (Character.toLowerCase(character) == 't') {
			this.newGame(this.currentGameType = (this.currentGameType + 1) % 3);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawForeground(final GuiMinecart gui) {
		final String[] mapnames = { Localization.ARCADE.MAP_1.translate(), Localization.ARCADE.MAP_2.translate(), Localization.ARCADE.MAP_3.translate() };
		this.getModule().drawString(gui, Localization.ARCADE.LEFT.translate(String.valueOf(this.creepersLeft)), 10, 180, 4210752);
		this.getModule().drawString(gui, Localization.ARCADE.TIME.translate(String.valueOf(this.ticks / 20)), 10, 190, 4210752);
		this.getModule().drawString(gui, "R - " + Localization.ARCADE.INSTRUCTION_RESTART.translate(), 10, 210, 4210752);
		this.getModule().drawString(gui, "T - " + Localization.ARCADE.INSTRUCTION_CHANGE_MAP.translate(), 10, 230, 4210752);
		this.getModule().drawString(gui, Localization.ARCADE.MAP.translate(mapnames[this.currentGameType]), 10, 240, 4210752);
		this.getModule().drawString(gui, Localization.ARCADE.HIGH_SCORES.translate(), 330, 180, 4210752);
		for (int i = 0; i < 3; ++i) {
			this.getModule().drawString(gui, Localization.ARCADE.HIGH_SCORE_ENTRY.translate(mapnames[i], String.valueOf(this.highscore[i])), 330, 190 + i * 10, 4210752);
		}
	}

	@Override
	public void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 3) {
			short data2 = data[1];
			short data3 = data[2];
			if (data2 < 0) {
				data2 += 256;
			}
			if (data3 < 0) {
				data3 += 256;
			}
			this.highscore[data[0]] = (data2 | data3 << 8);
		}
	}

	@Override
	public void checkGuiData(final Object[] info) {
		for (int i = 0; i < 3; ++i) {
			this.getModule().updateGuiData(info, TrackStory.stories.size() + 2 + i, (short) this.highscore[i]);
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id >= TrackStory.stories.size() + 2 && id < TrackStory.stories.size() + 5) {
			this.highscore[id - (TrackStory.stories.size() + 2)] = data;
		}
	}

	@Override
	public void Save(final NBTTagCompound tagCompound, final int id) {
		for (int i = 0; i < 3; ++i) {
			tagCompound.setShort(this.getModule().generateNBTName("HighscoreSweeper" + i, id), (short) this.highscore[i]);
		}
	}

	@Override
	public void Load(final NBTTagCompound tagCompound, final int id) {
		for (int i = 0; i < 3; ++i) {
			this.highscore[i] = tagCompound.getShort(this.getModule().generateNBTName("HighscoreSweeper" + i, id));
		}
	}

	static {
		ArcadeSweeper.textureMenu = "/gui/sweeper.png";
	}
}
