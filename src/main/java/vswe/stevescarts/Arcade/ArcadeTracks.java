package vswe.stevescarts.arcade;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.realtimers.ModuleArcade;

public class ArcadeTracks extends ArcadeGame {
	private TrackLevel currentMap;
	private boolean isMenuOpen;
	private boolean isRunning;
	private int currentStory;
	private int currentLevel;
	private int[] unlockedLevels;
	ArrayList<Cart> carts;
	private Cart player;
	private Cart enderman;
	private int playerStartX;
	private int playerStartY;
	private TrackOrientation.DIRECTION playerStartDirection;
	private int itemX;
	private int itemY;
	private boolean isItemTaken;
	private ArrayList<Track> tracks;
	private Track[][] trackMap;
	private int tick;
	private int currentMenuTab;
	private ArrayList<ScrollableList> lists;
	private boolean storySelected;
	private ScrollableList storyList;
	private ScrollableList mapList;
	private ScrollableList userList;
	private ArrayList<TrackLevel> userMaps;
	private boolean isUsingEditor;
	private boolean isSaveMenuOpen;
	private boolean failedToSave;
	private String saveName;
	private String lastSavedName;
	public static final int LEFT_MARGIN = 5;
	public static final int TOP_MARGIN = 5;
	private static String textureMenu;
	private static String textureGame;
	private final int BUTTON_COUNT = 14;
	private TrackEditor editorTrack;
	private TrackDetector editorDetectorTrack;
	private Track hoveringTrack;
	private boolean isEditorTrackDraging;
	private String validSaveNameCharacters;

	public ArcadeTracks(final ModuleArcade module) {
		super(module, Localization.ARCADE.OPERATOR);
		this.isMenuOpen = true;
		this.isRunning = false;
		this.currentStory = -1;
		this.currentLevel = -1;
		this.currentMenuTab = 0;
		this.saveName = "";
		this.lastSavedName = "";
		this.validSaveNameCharacters = "abcdefghijklmnopqrstuvwxyz0123456789 ";
		(this.carts = new ArrayList<Cart>()).add(this.player = new Cart(0) {
			@Override
			public void onItemPickUp() {
				ArcadeTracks.this.completeLevel();
				ArcadeGame.playSound("win", 1.0f, 1.0f);
			}

			@Override
			public void onCrash() {
				if (ArcadeTracks.this.isPlayingFinalLevel() && ArcadeTracks.this.currentStory < ArcadeTracks.this.unlockedLevels.length - 1 && ArcadeTracks.this.unlockedLevels[ArcadeTracks.this.currentStory + 1] == -1) {
					ArcadeTracks.this.getModule().sendPacket(0, new byte[] { (byte) (ArcadeTracks.this.currentStory + 1), 0 });
				}
			}
		});
		this.carts.add(this.enderman = new Cart(1));
		(this.lists = new ArrayList<ScrollableList>()).add(this.storyList = new ScrollableList(this, 5, 40) {
			@Override
			public boolean isVisible() {
				return ArcadeTracks.this.currentMenuTab == 0 && !ArcadeTracks.this.storySelected;
			}
		});
		this.lists.add(this.mapList = new ScrollableList(this, 5, 40) {
			@Override
			public boolean isVisible() {
				return ArcadeTracks.this.currentMenuTab == 0 && ArcadeTracks.this.storySelected;
			}
		});
		this.lists.add(this.userList = new ScrollableList(this, 5, 40) {
			@Override
			public boolean isVisible() {
				return ArcadeTracks.this.currentMenuTab == 1;
			}
		});
		(this.unlockedLevels = new int[TrackStory.stories.size()])[0] = 0;
		for (int i = 1; i < this.unlockedLevels.length; ++i) {
			this.unlockedLevels[i] = -1;
		}
		this.loadStories();
		if (this.getModule().getCart().worldObj.isRemote) {
			this.loadUserMaps();
		}
	}

	private void loadStories() {
		this.storyList.clearList();
		for (int i = 0; i < TrackStory.stories.size(); ++i) {
			if (this.unlockedLevels[i] > -1) {
				this.storyList.add(TrackStory.stories.get(i).getName());
			} else {
				this.storyList.add(null);
			}
		}
	}

	private void loadMaps() {
		final int story = this.storyList.getSelectedIndex();
		if (story != -1) {
			final ArrayList<TrackLevel> levels = TrackStory.stories.get(story).getLevels();
			this.mapList.clearList();
			for (int i = 0; i < levels.size(); ++i) {
				if (this.unlockedLevels[story] >= i) {
					this.mapList.add(levels.get(i).getName());
				} else {
					this.mapList.add(null);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void loadUserMaps() {
		this.userList.clearList();
		this.userMaps = TrackLevel.loadMapsFromFolder();
		if (StevesCarts.arcadeDevOperator) {
			for (int i = 0; i < TrackStory.stories.size(); ++i) {
				for (int j = 0; j < TrackStory.stories.get(i).getLevels().size(); ++j) {
					this.userMaps.add(TrackStory.stories.get(i).getLevels().get(j));
				}
			}
		}
		for (int i = 0; i < this.userMaps.size(); ++i) {
			this.userList.add(this.userMaps.get(i).getName());
		}
	}

	private void loadMap(final int story, final int level) {
		this.currentStory = story;
		this.currentLevel = level;
		this.loadMap(TrackStory.stories.get(story).getLevels().get(level));
	}

	private void loadMap(final TrackLevel map) {
		this.isUsingEditor = false;
		this.trackMap = new Track[27][10];
		this.tracks = new ArrayList<Track>();
		for (final Track track : map.getTracks()) {
			final Track newtrack = track.copy();
			this.tracks.add(newtrack);
			if (newtrack.getX() >= 0 && newtrack.getX() < this.trackMap.length && newtrack.getY() >= 0 && newtrack.getY() < this.trackMap[0].length) {
				this.trackMap[newtrack.getX()][newtrack.getY()] = newtrack;
			}
		}
		this.hoveringTrack = null;
		this.editorTrack = null;
		this.editorDetectorTrack = null;
		this.currentMap = map;
		this.isRunning = false;
		this.playerStartX = this.currentMap.getPlayerStartX();
		this.playerStartY = this.currentMap.getPlayerStartY();
		this.playerStartDirection = this.currentMap.getPlayerStartDirection();
		this.itemX = this.currentMap.getItemX();
		this.itemY = this.currentMap.getItemY();
		this.resetPosition();
	}

	private void resetPosition() {
		this.tick = 0;
		this.player.setX(this.playerStartX);
		this.player.setY(this.playerStartY);
		this.isItemTaken = false;
		this.player.setDirection(TrackOrientation.DIRECTION.STILL);
		this.enderman.setAlive(false);
	}

	public Track[][] getTrackMap() {
		return this.trackMap;
	}

	public Cart getEnderman() {
		return this.enderman;
	}

	private boolean isPlayingFinalLevel() {
		return this.isPlayingNormalLevel() && this.currentLevel == TrackStory.stories.get(this.currentStory).getLevels().size() - 1;
	}

	private boolean isUsingEditor() {
		return this.isUsingEditor;
	}

	private boolean isPlayingUserLevel() {
		return this.currentStory == -1;
	}

	private boolean isPlayingNormalLevel() {
		return !this.isUsingEditor() && !this.isPlayingUserLevel();
	}

	@Override
	public void update() {
		super.update();
		if (this.isRunning) {
			if (this.tick == 3) {
				for (final Cart cart : this.carts) {
					cart.move(this);
				}
				this.tick = 0;
			} else {
				++this.tick;
			}
		}
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		if (this.isSaveMenuOpen) {
			final int[] menu = this.getSaveMenuArea();
			if (this.failedToSave) {
				this.getModule().drawString(gui, Localization.ARCADE.SAVE_ERROR.translate(), menu[0] + 3, menu[1] + 3, 16711680);
			} else {
				this.getModule().drawString(gui, Localization.ARCADE.SAVE.translate(), menu[0] + 3, menu[1] + 3, 4210752);
			}
			this.getModule().drawString(gui, this.saveName + ((this.saveName.length() < 15 && this.getModule().getCart().worldObj.getWorldTime() % 20L < 10L) ? "|"
					: ""), menu[0] + 5, menu[1] + 16, 16777215);
		} else if (this.isMenuOpen) {
			for (final ScrollableList list : this.lists) {
				list.drawForeground(gui);
			}
			if (this.currentMenuTab == 0 || this.currentMenuTab == 1) {
				final int[] menu = this.getMenuArea();
				String str;
				if (this.currentMenuTab == 1) {
					str = Localization.ARCADE.USER_MAPS.translate();
				} else if (this.storySelected) {
					str = TrackStory.stories.get(this.storyList.getSelectedIndex()).getName();
				} else {
					str = Localization.ARCADE.STORIES.translate();
				}
				this.getModule().drawString(gui, str, menu[0] + 5, menu[1] + 32, 4210752);
			} else {
				final int[] menu = this.getMenuArea();
				this.getModule().drawSplitString(gui, Localization.ARCADE.HELP.translate(), menu[0] + 10, menu[1] + 20, menu[2] - 20, 4210752);
			}
		} else {
			for (final LevelMessage message : this.currentMap.getMessages()) {
				if (message.isVisible(this.isRunning, this.isRunning && this.player.getDireciotn() == TrackOrientation.DIRECTION.STILL, this.isRunning && this.isItemTaken)) {
					this.getModule().drawSplitString(gui, message.getMessage(), 9 + message.getX() * 16, 9 + message.getY() * 16, message.getW() * 16, 4210752);
				}
			}
			if (this.isUsingEditor()) {
				this.getModule().drawString(gui, "1-5 - " + Localization.ARCADE.INSTRUCTION_SHAPE.translate(), 10, 180, 4210752);
				this.getModule().drawString(gui, "R - " + Localization.ARCADE.INSTRUCTION_ROTATE_TRACK.translate(), 10, 190, 4210752);
				this.getModule().drawString(gui, "F - " + Localization.ARCADE.INSTRUCTION_FLIP_TRACK.translate(), 10, 200, 4210752);
				this.getModule().drawString(gui, "A - " + Localization.ARCADE.INSTRUCTION_DEFAULT_DIRECTION.translate(), 10, 210, 4210752);
				this.getModule().drawString(gui, "T - " + Localization.ARCADE.INSTRUCTION_TRACK_TYPE.translate(), 10, 220, 4210752);
				this.getModule().drawString(gui, "D - " + Localization.ARCADE.INSTRUCTION_DELETE_TRACK.translate(), 10, 230, 4210752);
				this.getModule().drawString(gui, "C - " + Localization.ARCADE.INSTRUCTION_COPY_TRACK.translate(), 10, 240, 4210752);
				this.getModule().drawString(gui, "S - " + Localization.ARCADE.INSTRUCTION_STEVE.translate(), 330, 180, 4210752);
				this.getModule().drawString(gui, "X - " + Localization.ARCADE.INSTRUCTION_MAP.translate(), 330, 190, 4210752);
				this.getModule().drawString(gui, Localization.ARCADE.LEFT_MOUSE.translate() + " - " + Localization.ARCADE.INSTRUCTION_PLACE_TRACK.translate(), 330, 200, 4210752);
				this.getModule().drawString(gui, Localization.ARCADE.RIGHT_MOUSE.translate() + " - " + Localization.ARCADE.INSTRUCTION_DESELECT_TRACK.translate(), 330, 210, 4210752);
			}
		}
	}

	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		if (!this.isSaveMenuOpen && this.isMenuOpen) {
			ResourceHelper.bindResource(ArcadeTracks.textureMenu);
			this.getModule().drawImage(gui, this.getMenuArea(), 0, 0);
			for (int i = 0; i < 3; ++i) {
				final int[] rect = this.getMenuTabArea(i);
				final boolean active = this.getModule().inRect(x, y, rect);
				final boolean hidden = !active && i == this.currentMenuTab;
				if (!hidden) {
					this.getModule().drawImage(gui, rect[0], rect[1] + rect[3], 0, active ? 114 : 113, rect[2], 1);
				}
			}
			for (final ScrollableList list : this.lists) {
				list.drawBackground(gui, x, y);
			}
		} else if (this.currentMap != null) {
			ResourceHelper.bindResource(ArcadeTracks.textureGame);
			if (this.isUsingEditor() && !this.isRunning) {
				for (int i = 0; i < this.trackMap.length; ++i) {
					for (int j = 0; j < this.trackMap[0].length; ++j) {
						this.getModule().drawImage(gui, 5 + i * 16, 5 + j * 16, 16, 128, 16, 16);
					}
				}
			}
			for (final Track track : this.tracks) {
				this.getModule().drawImage(gui, getTrackArea(track.getX(), track.getY()), 16 * track.getU(), 16 * track.getV(), track.getRotation());
			}
			if (this.isUsingEditor()) {
				if (this.editorDetectorTrack != null && !this.isRunning) {
					this.editorDetectorTrack.drawOverlay(this.getModule(), gui, this.editorDetectorTrack.getX() * 16 + 8, this.editorDetectorTrack.getY() * 16 + 8, this.isRunning);
					this.getModule().drawImage(gui, 5 + this.editorDetectorTrack.getX() * 16, 5 + this.editorDetectorTrack.getY() * 16, 32, 128, 16, 16);
				}
			} else {
				for (final Track track : this.tracks) {
					track.drawOverlay(this.getModule(), gui, x, y, this.isRunning);
				}
			}
			if (!this.isItemTaken) {
				int itemIndex = 0;
				if (this.isPlayingFinalLevel()) {
					itemIndex = 1;
				}
				this.getModule().drawImage(gui, 5 + this.itemX * 16, 5 + this.itemY * 16, 16 * itemIndex, 240, 16, 16);
			}
			for (final Cart cart : this.carts) {
				cart.render(this, gui, this.tick);
			}
			if (this.isUsingEditor() && !this.isRunning) {
				this.getModule().drawImage(gui, 5 + this.playerStartX * 16, 5 + this.playerStartY * 16, 162, 212, 8, 8, this.playerStartDirection.getRenderRotation());
			}
			if (!this.isMenuOpen && this.editorTrack != null) {
				this.getModule().drawImage(gui, x - 8, y - 8, 16 * this.editorTrack.getU(), 16 * this.editorTrack.getV(), 16, 16, this.editorTrack.getRotation());
			}
			if (this.isSaveMenuOpen) {
				final int[] rect2 = this.getSaveMenuArea();
				this.getModule().drawImage(gui, rect2, 0, 144);
			}
		}
		ResourceHelper.bindResource(ArcadeTracks.textureGame);
		for (int i = 0; i < 14; ++i) {
			if (this.isButtonVisible(i)) {
				final int[] rect = this.getButtonArea(i);
				final int srcX = this.isButtonDisabled(i) ? 208 : (this.getModule().inRect(x, y, rect) ? 224 : 240);
				final int srcY = i * 16;
				this.getModule().drawImage(gui, rect, srcX, srcY);
			}
		}
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		for (int i = 0; i < 14; ++i) {
			if (!this.isButtonDisabled(i) && this.isButtonVisible(i)) {
				this.getModule().drawStringOnMouseOver(gui, this.getButtonText(i), x, y, this.getButtonArea(i));
			}
		}
	}

	@Override
	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
		if (this.isSaveMenuOpen) {
			return;
		}
		if (this.isMenuOpen) {
			for (final ScrollableList list : this.lists) {
				list.mouseMovedOrUp(gui, x, y, button);
			}
		}
		if (this.currentMap != null && this.isUsingEditor()) {
			final int x2 = x - 5;
			final int y2 = y - 5;
			final int gridX = x2 / 16;
			final int gridY = y2 / 16;
			if (gridX >= 0 && gridX < this.trackMap.length && gridY >= 0 && gridY < this.trackMap[0].length) {
				this.hoveringTrack = this.trackMap[gridX][gridY];
			} else {
				this.hoveringTrack = null;
			}
		}
		this.handleEditorTrack(x, y, button, false);
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (!this.isSaveMenuOpen) {
			if (this.isMenuOpen) {
				if (!this.getModule().inRect(x, y, this.getMenuArea())) {
					if (this.currentMap != null) {
						this.isMenuOpen = false;
					}
				} else {
					for (int i = 0; i < 3; ++i) {
						if (i != this.currentMenuTab && this.getModule().inRect(x, y, this.getMenuTabArea(i))) {
							this.currentMenuTab = i;
							break;
						}
					}
					for (final ScrollableList list : this.lists) {
						list.mouseClicked(gui, x, y, button);
					}
				}
			} else {
				if (!this.isRunning) {
					for (final Track track : this.tracks) {
						if (this.getModule().inRect(x, y, getTrackArea(track.getX(), track.getY()))) {
							if (this.isUsingEditor()) {
								if (this.editorTrack != null) {
									continue;
								}
								track.onEditorClick(this);
							} else {
								track.onClick(this);
							}
						}
					}
				}
				this.handleEditorTrack(x, y, button, true);
			}
		}
		for (int i = 0; i < 14; ++i) {
			final int[] rect = this.getButtonArea(i);
			if (this.getModule().inRect(x, y, rect) && this.isButtonVisible(i) && !this.isButtonDisabled(i)) {
				this.buttonClicked(i);
				break;
			}
		}
	}

	public void completeLevel() {
		if (this.isPlayingNormalLevel()) {
			final int nextLevel = this.currentLevel + 1;
			if (nextLevel > this.unlockedLevels[this.currentStory]) {
				this.getModule().sendPacket(0, new byte[] { (byte) this.currentStory, (byte) nextLevel });
			}
		}
	}

	public int[] getMenuArea() {
		return new int[] { 93, 27, 256, 113 };
	}

	private int[] getMenuTabArea(final int id) {
		final int[] menu = this.getMenuArea();
		return new int[] { menu[0] + 1 + id * 85, menu[1] + 1, 84, 12 };
	}

	private int[] getSaveMenuArea() {
		return new int[] { 172, 60, 99, 47 };
	}

	private int[] getButtonArea(int id) {
		if (id == 4 || id == 5) {
			final int[] menu = this.getMenuArea();
			return new int[] { menu[0] + 235 - 18 * (id - 4), menu[1] + 20, 16, 16 };
		}
		if (id > 5 && id < 10) {
			final int[] menu = this.getMenuArea();
			return new int[] { menu[0] + 235, menu[1] + 20 + (id - 6) * 18, 16, 16 };
		}
		if (id >= 12 && id < 14) {
			final int[] menu = this.getSaveMenuArea();
			return new int[] { menu[0] + menu[2] - 18 * (id - 11) - 2, menu[1] + menu[3] - 18, 16, 16 };
		}
		if (id >= 10 && id < 12) {
			id -= 6;
		}
		return new int[] { 455, 26 + id * 18, 16, 16 };
	}

	private boolean isButtonVisible(final int id) {
		if (id == 4 || id == 5) {
			return this.isMenuOpen && this.currentMenuTab == 0;
		}
		if (id > 5 && id < 10) {
			return this.isMenuOpen && this.currentMenuTab == 1;
		}
		if (id >= 10 && id < 12) {
			return this.isUsingEditor();
		}
		return id < 12 || id >= 14 || this.isSaveMenuOpen;
	}

	private boolean isButtonDisabled(final int id) {
		switch (id) {
			case 0: {
				return this.isRunning || this.isMenuOpen || this.isSaveMenuOpen;
			}
			case 1: {
				return this.isRunning || this.isMenuOpen || this.isSaveMenuOpen;
			}
			case 2: {
				return !this.isRunning || this.isSaveMenuOpen;
			}
			case 3: {
				return this.isMenuOpen || this.isSaveMenuOpen || !this.isPlayingNormalLevel() || this.currentLevel + 1 > this.unlockedLevels[this.currentStory];
			}
			case 4: {
				return (this.storySelected ? this.mapList : this.storyList).getSelectedIndex() == -1;
			}
			case 5: {
				return !this.storySelected;
			}
			case 6:
			case 8: {
				return this.userList.getSelectedIndex() == -1;
			}
			case 7:
			case 9:
			case 12: {
				return false;
			}
			case 10:
			case 11: {
				return this.isMenuOpen || this.isSaveMenuOpen || this.isRunning;
			}
			case 13: {
				return this.saveName.length() == 0;
			}
			default: {
				return true;
			}
		}
	}

	private void buttonClicked(final int id) {
		switch (id) {
			case 0: {
				for (final Track track : this.tracks) {
					track.saveBackup();
				}
				this.player.setDirection(this.playerStartDirection);
				this.isRunning = true;
				break;
			}
			case 1: {
				this.isMenuOpen = true;
				this.editorTrack = null;
				break;
			}
			case 2: {
				for (final Track track : this.tracks) {
					track.loadBackup();
				}
				this.resetPosition();
				this.isRunning = false;
				break;
			}
			case 3: {
				this.loadMap(this.currentStory, this.currentLevel + 1);
				break;
			}
			case 4: {
				if (this.storySelected) {
					this.loadMap(this.storyList.getSelectedIndex(), this.mapList.getSelectedIndex());
					this.isMenuOpen = false;
					break;
				}
				this.storySelected = true;
				this.mapList.clear();
				this.loadMaps();
				break;
			}
			case 5: {
				this.storySelected = false;
				break;
			}
			case 6: {
				this.currentStory = -1;
				this.loadMap(this.userMaps.get(this.userList.getSelectedIndex()));
				this.isMenuOpen = false;
				break;
			}
			case 7: {
				this.loadMap(TrackLevel.editor);
				this.isMenuOpen = false;
				this.lastSavedName = "";
				this.isUsingEditor = true;
				break;
			}
			case 8: {
				final TrackLevel mapToEdit = this.userMaps.get(this.userList.getSelectedIndex());
				this.loadMap(mapToEdit);
				this.lastSavedName = mapToEdit.getName();
				this.isMenuOpen = false;
				this.isUsingEditor = true;
				break;
			}
			case 9: {
				this.userList.clear();
				if (this.getModule().getCart().worldObj.isRemote) {
					this.loadUserMaps();
					break;
				}
				break;
			}
			case 10: {
				if (this.lastSavedName.length() == 0) {
					this.isSaveMenuOpen = true;
					this.failedToSave = false;
					break;
				}
				this.save(this.lastSavedName);
				break;
			}
			case 11: {
				this.isSaveMenuOpen = true;
				this.failedToSave = false;
				break;
			}
			case 13: {
				if (this.save(this.saveName)) {
					this.saveName = "";
					this.isSaveMenuOpen = false;
					break;
				}
				break;
			}
			case 12: {
				this.isSaveMenuOpen = false;
				break;
			}
		}
	}

	private String getButtonText(final int id) {
		switch (id) {
			case 0: {
				return Localization.ARCADE.BUTTON_START.translate();
			}
			case 1: {
				return Localization.ARCADE.BUTTON_MENU.translate();
			}
			case 2: {
				return Localization.ARCADE.BUTTON_STOP.translate();
			}
			case 3: {
				return Localization.ARCADE.BUTTON_NEXT.translate();
			}
			case 4: {
				return this.storySelected ? Localization.ARCADE.BUTTON_START_LEVEL.translate() : Localization.ARCADE.BUTTON_SELECT_STORY.translate();
			}
			case 5: {
				return Localization.ARCADE.BUTTON_SELECT_OTHER_STORY.translate();
			}
			case 6: {
				return Localization.ARCADE.BUTTON_START_LEVEL.translate();
			}
			case 7: {
				return Localization.ARCADE.BUTTON_CREATE_LEVEL.translate();
			}
			case 8: {
				return Localization.ARCADE.BUTTON_EDIT_LEVEL.translate();
			}
			case 9: {
				return Localization.ARCADE.BUTTON_REFRESH.translate();
			}
			case 10: {
				return Localization.ARCADE.BUTTON_START.translate();
			}
			case 11: {
				return Localization.ARCADE.BUTTON_SAVE_AS.translate();
			}
			case 12: {
				return Localization.ARCADE.BUTTON_CANCEL.translate();
			}
			case 13: {
				return Localization.ARCADE.BUTTON_SAVE.translate();
			}
			default: {
				return "Hello, I'm a button";
			}
		}
	}

	public static int[] getTrackArea(final int x, final int y) {
		return new int[] { 5 + 16 * x, 5 + 16 * y, 16, 16 };
	}

	public boolean isItemOnGround() {
		return !this.isItemTaken;
	}

	public void pickItemUp() {
		this.isItemTaken = true;
	}

	public int getItemX() {
		return this.itemX;
	}

	public int getItemY() {
		return this.itemY;
	}

	@Override
	public void Save(final NBTTagCompound tagCompound, final int id) {
		for (int i = 0; i < this.unlockedLevels.length; ++i) {
			tagCompound.setByte(this.getModule().generateNBTName("Unlocked" + i, id), (byte) this.unlockedLevels[i]);
		}
	}

	@Override
	public void Load(final NBTTagCompound tagCompound, final int id) {
		for (int i = 0; i < this.unlockedLevels.length; ++i) {
			this.unlockedLevels[i] = tagCompound.getByte(this.getModule().generateNBTName("Unlocked" + i, id));
		}
		this.loadStories();
	}

	@Override
	public void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.unlockedLevels[data[0]] = data[1];
			if (this.unlockedLevels[data[0]] > TrackStory.stories.get(data[0]).getLevels().size() - 1) {
				this.unlockedLevels[data[0]] = TrackStory.stories.get(data[0]).getLevels().size() - 1;
			}
		}
	}

	@Override
	public void checkGuiData(final Object[] info) {
		for (int i = 0; i < this.unlockedLevels.length; ++i) {
			this.getModule().updateGuiData(info, i, (short) this.unlockedLevels[i]);
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id >= 0 && id < this.unlockedLevels.length) {
			if ((this.unlockedLevels[id] = data) != 0) {
				this.loadMaps();
			} else {
				this.loadStories();
			}
		}
	}

	public void setEditorTrack(final TrackEditor track) {
		if (this.editorTrack != null) {
			track.setType(this.editorTrack.getType());
		}
		this.editorTrack = track;
	}

	public void setEditorDetectorTrack(final TrackDetector track) {
		if (track.equals(this.editorDetectorTrack)) {
			this.editorDetectorTrack = null;
		} else {
			this.editorDetectorTrack = track;
		}
	}

	public TrackDetector getEditorDetectorTrack() {
		return this.editorDetectorTrack;
	}

	@Override
	public void keyPress(final GuiMinecart gui, final char character, final int extraInformation) {
		if (this.isSaveMenuOpen) {
			if (this.saveName.length() < 15 && this.validSaveNameCharacters.indexOf(Character.toLowerCase(character)) != -1) {
				this.saveName += character;
			} else if (extraInformation == 14 && this.saveName.length() > 0) {
				this.saveName = this.saveName.substring(0, this.saveName.length() - 1);
			}
		} else {
			if (!this.isUsingEditor() || this.isRunning) {
				return;
			}
			Track track;
			if (this.editorTrack != null) {
				track = this.editorTrack;
			} else {
				track = this.hoveringTrack;
			}
			switch (Character.toLowerCase(character)) {
				case 'a': {
					if (track != null && track.getOrientation().getOpposite() != null) {
						track.setOrientation(track.getOrientation().getOpposite());
						break;
					}
					break;
				}
				case 'r': {
					if (track != null) {
						for (final TrackOrientation orientation : TrackOrientation.ALL) {
							if (orientation.getV() == track.getV() && ((orientation.getV() == 1 && orientation.getRotation() != track.getRotation()) || orientation.getRotation() == track.getRotation().getNextRotation())) {
								track.setOrientation(orientation);
								break;
							}
						}
						break;
					}
					break;
				}
				case 'f': {
					if (track != null) {
						for (final TrackOrientation orientation : TrackOrientation.ALL) {
							if (orientation.getV() == track.getV() && (orientation.getV() == 2 || orientation.getV() == 3) && orientation.getRotation() == track.getRotation().getFlippedRotation()) {
								track.setOrientation(orientation);
								break;
							}
						}
						break;
					}
					break;
				}
				case 't': {
					if (this.editorTrack != null) {
						this.editorTrack.nextType();
						break;
					}
					break;
				}
				case '1': {
					this.setEditorTrack(new TrackEditor(TrackOrientation.CORNER_DOWN_RIGHT));
					break;
				}
				case '2': {
					this.setEditorTrack(new TrackEditor(TrackOrientation.STRAIGHT_VERTICAL));
					break;
				}
				case '3': {
					this.setEditorTrack(new TrackEditor(TrackOrientation.JUNCTION_3WAY_STRAIGHT_FORWARD_VERTICAL_CORNER_DOWN_RIGHT));
					break;
				}
				case '4': {
					this.setEditorTrack(new TrackEditor(TrackOrientation.JUNCTION_3WAY_CORNER_RIGHT_ENTRANCE_DOWN));
					break;
				}
				case '5': {
					this.setEditorTrack(new TrackEditor(TrackOrientation.JUNCTION_4WAY));
					break;
				}
				case 'd': {
					if (this.hoveringTrack != null) {
						this.tracks.remove(this.hoveringTrack);
						if (this.hoveringTrack.getX() >= 0 && this.hoveringTrack.getX() < this.trackMap.length && this.hoveringTrack.getY() >= 0 && this.hoveringTrack.getY() < this.trackMap[0].length) {
							this.trackMap[this.hoveringTrack.getX()][this.hoveringTrack.getY()] = null;
						}
						this.hoveringTrack = null;
						break;
					}
					break;
				}
				case 'c': {
					if (this.editorTrack == null && this.hoveringTrack != null) {
						this.setEditorTrack(new TrackEditor(this.hoveringTrack.getOrientation()));
						this.editorTrack.setType(this.hoveringTrack.getU());
						break;
					}
					break;
				}
				case 's': {
					if (this.hoveringTrack != null) {
						if (this.playerStartX == this.hoveringTrack.getX() && this.playerStartY == this.hoveringTrack.getY()) {
							this.playerStartDirection = this.playerStartDirection.getLeft();
						} else {
							this.playerStartX = this.hoveringTrack.getX();
							this.playerStartY = this.hoveringTrack.getY();
						}
						this.resetPosition();
						break;
					}
					break;
				}
				case 'x': {
					if (this.hoveringTrack != null) {
						this.itemX = this.hoveringTrack.getX();
						this.itemY = this.hoveringTrack.getY();
						break;
					}
					break;
				}
			}
		}
	}

	private void handleEditorTrack(final int x, final int y, final int button, final boolean clicked) {
		if (this.isRunning) {
			this.isEditorTrackDraging = false;
			return;
		}
		if (this.editorTrack != null) {
			if ((clicked && button == 0) || (!clicked && button == -1 && this.isEditorTrackDraging)) {
				final int x2 = x - 5;
				final int y2 = y - 5;
				final int gridX = x2 / 16;
				final int gridY = y2 / 16;
				if (gridX >= 0 && gridX < this.trackMap.length && gridY >= 0 && gridY < this.trackMap[0].length) {
					if (this.trackMap[gridX][gridY] == null) {
						final Track newtrack = this.editorTrack.getRealTrack(gridX, gridY);
						this.trackMap[gridX][gridY] = newtrack;
						this.tracks.add(newtrack);
					}
					this.isEditorTrackDraging = true;
				}
			} else if (button == 1 || (!clicked && this.isEditorTrackDraging)) {
				if (clicked) {
					this.editorTrack = null;
				}
				this.isEditorTrackDraging = false;
			}
		}
	}

	@Override
	public boolean disableStandardKeyFunctionality() {
		return this.isSaveMenuOpen;
	}

	@SideOnly(Side.CLIENT)
	private boolean save(String name) {
		if (StevesCarts.arcadeDevOperator) {
			if (!name.startsWith(" ")) {
				final String result = TrackLevel.saveMapToString(name, this.playerStartX, this.playerStartY, this.playerStartDirection, this.itemX, this.itemY, this.tracks);
				System.out.println(result);
				return true;
			}
			name = name.substring(1);
		}
		if (TrackLevel.saveMap(name, this.playerStartX, this.playerStartY, this.playerStartDirection, this.itemX, this.itemY, this.tracks)) {
			this.lastSavedName = name;
			this.loadUserMaps();
			return true;
		}
		this.saveName = name;
		this.failedToSave = true;
		this.isSaveMenuOpen = true;
		return false;
	}

	static {
		ArcadeTracks.textureMenu = "/gui/trackgamemenu.png";
		ArcadeTracks.textureGame = "/gui/trackgame.png";
	}
}
