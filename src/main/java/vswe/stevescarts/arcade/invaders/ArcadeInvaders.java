package vswe.stevescarts.arcade.invaders;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.arcade.ArcadeGame;
import vswe.stevescarts.arcade.invaders.Unit.UPDATE_RESULT;
import vswe.stevescarts.arcade.tracks.TrackStory;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.realtimers.ModuleArcade;

public class ArcadeInvaders extends ArcadeGame {
	protected ArrayList<Unit> invaders;
	private ArrayList<Player> lives;
	private ArrayList<Unit> buildings;
	protected ArrayList<Projectile> projectiles;
	private Player player;
	protected int moveDirection;
	protected int moveSpeed;
	protected int moveDown;
	private int fireDelay;
	private int score;
	private int highscore;
	protected boolean hasPahighast;
	protected boolean canSpawnPahighast;
	private boolean newHighscore;
	private int gameoverCounter;
	private static String texture;
	private static final String[][] numbers;

	public ArcadeInvaders(final ModuleArcade module) {
		super(module, Localization.ARCADE.GHAST);
		this.invaders = new ArrayList<Unit>();
		this.buildings = new ArrayList<Unit>();
		this.lives = new ArrayList<Player>();
		this.projectiles = new ArrayList<Projectile>();
		this.start();
	}

	private void start() {
		this.buildings.clear();
		this.lives.clear();
		this.projectiles.clear();
		this.player = new Player(this);
		for (int i = 0; i < 3; ++i) {
			this.lives.add(new Player(this, 10 + i * 20, 190));
		}
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.buildings.add(new Building(this, 48 + i * 96 + j * 16, 120));
			}
		}
		this.moveSpeed = 0;
		this.fireDelay = 0;
		this.score = 0;
		this.canSpawnPahighast = false;
		this.newHighscore = false;
		this.spawnInvaders();
	}

	private void spawnInvaders() {
		this.invaders.clear();
		this.hasPahighast = false;
		for (int j = 0; j < 3; ++j) {
			for (int i = 0; i < 14; ++i) {
				this.invaders.add(new InvaderGhast(this, 20 + i * 20, 10 + 25 * j));
			}
		}
		++this.moveSpeed;
		this.moveDirection = 1;
		this.moveDown = 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void update() {
		super.update();
		if (this.player != null) {
			if (this.player.ready) {
				boolean flag = false;
				boolean flag2 = false;
				for (int i = this.invaders.size() - 1; i >= 0; --i) {
					final Unit invader = this.invaders.get(i);
					final Unit.UPDATE_RESULT result = invader.update();
					if (result == Unit.UPDATE_RESULT.DEAD) {
						if (((InvaderGhast) invader).isPahighast) {
							this.hasPahighast = false;
						}
						ArcadeGame.playDefaultSound("mob.ghast.scream", 0.03f, 1.0f);
						this.invaders.remove(i);
						++this.score;
					} else if (result == Unit.UPDATE_RESULT.TURN_BACK) {
						flag = true;
					} else if (result == Unit.UPDATE_RESULT.GAME_OVER) {
						flag2 = true;
					}
				}
				if (this.moveDown > 0) {
					--this.moveDown;
				}
				if (flag) {
					this.moveDirection *= -1;
					this.moveDown = 5;
				}
				if (this.invaders.size() == 0 || (this.hasPahighast && this.invaders.size() == 1)) {
					this.score += (this.hasPahighast ? 200 : 50);
					this.canSpawnPahighast = true;
					this.spawnInvaders();
				}
				if (flag2) {
					this.lives.clear();
					this.projectiles.clear();
					this.player = null;
					this.newHighScore();
					return;
				}
				for (int i = this.buildings.size() - 1; i >= 0; --i) {
					if (this.buildings.get(i).update() == Unit.UPDATE_RESULT.DEAD) {
						this.buildings.remove(i);
					}
				}
				for (int i = this.projectiles.size() - 1; i >= 0; --i) {
					if (this.projectiles.get(i).update() == Unit.UPDATE_RESULT.DEAD) {
						this.projectiles.remove(i);
					}
				}
				if (Keyboard.isKeyDown(30)) {
					this.player.move(-1);
				} else if (Keyboard.isKeyDown(32)) {
					this.player.move(1);
				}
				if (this.fireDelay == 0 && Keyboard.isKeyDown(17)) {
					this.projectiles.add(new Projectile(this, this.player.x + 8 - 2, this.player.y - 15, true));
					ArcadeGame.playDefaultSound("random.bow", 0.8f, 1.0f / (this.getModule().getCart().rand.nextFloat() * 0.4f + 1.2f) + 0.5f);
					this.fireDelay = 10;
				} else if (this.fireDelay > 0) {
					--this.fireDelay;
				}
			}
			if (this.player.update() == Unit.UPDATE_RESULT.DEAD) {
				this.projectiles.clear();
				ArcadeGame.playSound("hit", 1.0f, 1.0f);
				if (this.lives.size() != 0) {
					this.lives.get(0).setTarget(this.player.x, this.player.y);
					this.player = this.lives.get(0);
					this.lives.remove(0);
				} else {
					this.player = null;
					this.newHighScore();
				}
			}
		} else if (this.gameoverCounter == 0) {
			boolean flag = false;
			for (int j = this.invaders.size() - 1; j >= 0; --j) {
				final Unit invader2 = this.invaders.get(j);
				if (invader2.update() == Unit.UPDATE_RESULT.TARGET) {
					flag = true;
				}
			}
			if (!flag) {
				this.gameoverCounter = 1;
			}
		} else if (this.newHighscore && this.gameoverCounter < 5) {
			++this.gameoverCounter;
			if (this.gameoverCounter == 5) {
				ArcadeGame.playSound("highscore", 1.0f, 1.0f);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource(ArcadeInvaders.texture);
		for (int i = 0; i < 27; ++i) {
			this.getModule().drawImage(gui, 5 + i * 16, 150, 16, 32, 16, 16);
		}
		for (int i = 0; i < 5; ++i) {
			this.getModule().drawImage(gui, 3 + i * 16, 190, 16, 32, 16, 16);
		}
		for (final Unit invader : this.invaders) {
			invader.draw(gui);
		}
		if (this.player != null) {
			this.player.draw(gui);
		}
		for (final Unit player : this.lives) {
			player.draw(gui);
		}
		for (final Unit projectile : this.projectiles) {
			projectile.draw(gui);
		}
		for (final Unit building : this.buildings) {
			building.draw(gui);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.getModule().drawString(gui, Localization.ARCADE.EXTRA_LIVES.translate() + ":", 10, 180, 4210752);
		this.getModule().drawString(gui, Localization.ARCADE.HIGH_SCORE.translate(String.valueOf(this.highscore)), 10, 210, 4210752);
		this.getModule().drawString(gui, Localization.ARCADE.SCORE.translate(String.valueOf(this.score)), 10, 220, 4210752);
		this.getModule().drawString(gui, "W - " + Localization.ARCADE.INSTRUCTION_SHOOT.translate(), 330, 180, 4210752);
		this.getModule().drawString(gui, "A - " + Localization.ARCADE.INSTRUCTION_LEFT.translate(), 330, 190, 4210752);
		this.getModule().drawString(gui, "D - " + Localization.ARCADE.INSTRUCTION_RIGHT.translate(), 330, 200, 4210752);
		this.getModule().drawString(gui, "R - " + Localization.ARCADE.INSTRUCTION_RESTART.translate(), 330, 220, 4210752);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void keyPress(final GuiMinecart gui, final char character, final int extraInformation) {
		if (Character.toLowerCase(character) == 'r') {
			this.start();
		}
	}

	private void newHighScore() {
		this.buildings.clear();
		int digits;
		if (this.score == 0) {
			digits = 1;
		} else {
			digits = (int) Math.floor(Math.log10(this.score)) + 1;
		}
		this.canSpawnPahighast = false;
		int currentGhast = 0;
		for (int i = 0; i < digits; ++i) {
			final int digit = this.score / (int) Math.pow(10.0, digits - i - 1) % 10;
			final String[] number = ArcadeInvaders.numbers[digit];
			for (int j = 0; j < number.length; ++j) {
				final String line = number[j];
				for (int k = 0; k < line.length(); ++k) {
					if (line.charAt(k) == 'X') {
						final int x = (443 - (digits * 90 - 10)) / 2 + i * 90 + k * 20;
						final int y = 5 + j * 20;
						InvaderGhast ghast;
						if (currentGhast >= this.invaders.size()) {
							this.invaders.add(ghast = new InvaderGhast(this, x, -20));
							++currentGhast;
						} else {
							ghast = (InvaderGhast) this.invaders.get(currentGhast++);
						}
						ghast.setTarget(x, y);
					}
				}
			}
		}
		for (int i = currentGhast; i < this.invaders.size(); ++i) {
			final InvaderGhast ghast2 = (InvaderGhast) this.invaders.get(i);
			ghast2.setTarget(ghast2.x, -25);
		}
		this.gameoverCounter = 0;
		if (this.score > this.highscore) {
			this.newHighscore = true;
			final int val = this.score;
			final byte byte1 = (byte) (val & 0xFF);
			final byte byte2 = (byte) ((val & 0xFF00) >> 8);
			this.getModule().sendPacket(2, new byte[] { byte1, byte2 });
		}
	}

	@Override
	public void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 2) {
			short data2 = data[0];
			short data3 = data[1];
			if (data2 < 0) {
				data2 += 256;
			}
			if (data3 < 0) {
				data3 += 256;
			}
			this.highscore = (data2 | data3 << 8);
		}
	}

	@Override
	public void checkGuiData(final Object[] info) {
		this.getModule().updateGuiData(info, TrackStory.stories.size() + 1, (short) this.highscore);
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == TrackStory.stories.size() + 1) {
			this.highscore = data;
		}
	}

	@Override
	public void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setShort(this.getModule().generateNBTName("HighscoreGhast", id), (short) this.highscore);
	}

	@Override
	public void Load(final NBTTagCompound tagCompound, final int id) {
		this.highscore = tagCompound.getShort(this.getModule().generateNBTName("HighscoreGhast", id));
	}

	static {
		ArcadeInvaders.texture = "/gui/invaders.png";
		numbers = new String[][] { { "XXXX", "X  X", "X  X", "X  X", "X  X", "X  X", "XXXX" }, { "   X", "   X", "   X", "   X", "   X", "   X", "   X" },
			{ "XXXX", "   X", "   X", "XXXX", "X   ", "X   ", "XXXX" }, { "XXXX", "   X", "   X", "XXXX", "   X", "   X", "XXXX" }, { "X  X", "X  X", "X  X", "XXXX", "   X", "   X", "   X" },
			{ "XXXX", "X   ", "X   ", "XXXX", "   X", "   X", "XXXX" }, { "XXXX", "X   ", "X   ", "XXXX", "X  X", "X  X", "XXXX" }, { "XXXX", "   X", "   X", "   X", "   X", "   X", "   X" },
			{ "XXXX", "X  X", "X  X", "XXXX", "X  X", "X  X", "XXXX" }, { "XXXX", "X  X", "X  X", "XXXX", "   X", "   X", "XXXX" } };
	}
}
