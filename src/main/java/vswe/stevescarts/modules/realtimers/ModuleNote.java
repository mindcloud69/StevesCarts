package vswe.stevescarts.modules.realtimers;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.ModuleBase;

public class ModuleNote extends ModuleBase {
	private final int maximumTracksPerModuleBitCount = 4;
	private final int maximumNotesPerTrackBitCount = 12;
	private int veryLongTrackLimit;
	private int notesInView;
	private int tracksInView;
	private int[] instrumentColors;
	private String[] pitchNames;
	private Localization.MODULES.ATTACHMENTS[] instrumentNames;
	private ArrayList<Track> tracks;
	private int notemapX;
	private int notemapY;
	private int trackHeight;
	private ArrayList<Button> buttons;
	private ArrayList<Button> instrumentbuttons;
	private int currentInstrument;
	private Button createTrack;
	private Button removeTrack;
	private Button speedButton;
	private boolean isScrollingX;
	private boolean isScrollingXTune;
	private int scrollX;
	private boolean isScrollingY;
	private int scrollY;
	private int pixelScrollX;
	private int pixelScrollXTune;
	private int generatedScrollX;
	private int pixelScrollY;
	private int generatedScrollY;
	private int[] scrollXrect;
	private int[] scrollYrect;
	private final int maximumNotesPerTrack;
	private final int maximumTracksPerModule;
	private int currentTick;
	private int playProgress;
	private boolean tooLongTrack;
	private boolean tooTallModule;
	private boolean veryLongTrack;
	private int speedSetting;
	private short lastModuleHeader;

	private static DataParameter<Boolean> PLAYING = createDw(DataSerializers.BOOLEAN);

	public ModuleNote(final EntityMinecartModular cart) {
		super(cart);
		this.veryLongTrackLimit = 1024;
		this.notesInView = 13;
		this.tracksInView = 5;
		this.instrumentColors = new int[] { 4210752, 16711680, 65280, 255, 16776960, 65535 };
		this.pitchNames = new String[] { "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#" };
		this.instrumentNames = new Localization.MODULES.ATTACHMENTS[] { Localization.MODULES.ATTACHMENTS.PIANO, Localization.MODULES.ATTACHMENTS.BASS_DRUM, Localization.MODULES.ATTACHMENTS.SNARE_DRUM,
				Localization.MODULES.ATTACHMENTS.STICKS, Localization.MODULES.ATTACHMENTS.BASS_GUITAR };
		this.notemapX = 70;
		this.notemapY = 40;
		this.trackHeight = 20;
		this.currentInstrument = -1;
		this.scrollXrect = new int[] { this.notemapX + 120, this.notemapY - 20, 100, 16 };
		this.scrollYrect = new int[] { this.notemapX + 220, this.notemapY, 16, 100 };
		this.currentTick = 0;
		this.playProgress = 0;
		this.tooLongTrack = false;
		this.tooTallModule = false;
		this.veryLongTrack = false;
		this.speedSetting = 5;
		this.maximumNotesPerTrack = (int) Math.pow(2.0, 12.0) - 1;
		this.maximumTracksPerModule = (int) Math.pow(2.0, 4.0) - 1;
		this.tracks = new ArrayList<>();
		if (this.getCart().worldObj.isRemote) {
			this.buttons = new ArrayList<>();
			this.createTrack = new Button(this.notemapX - 60, this.notemapY - 20);
			this.createTrack.text = Localization.MODULES.ATTACHMENTS.CREATE_TRACK.translate();
			this.createTrack.imageID = 0;
			this.removeTrack = new Button(this.notemapX - 40, this.notemapY - 20);
			this.removeTrack.text = Localization.MODULES.ATTACHMENTS.REMOVE_TRACK.translate();
			this.removeTrack.imageID = 1;
			this.speedButton = new Button(this.notemapX - 20, this.notemapY - 20);
			this.updateSpeedButton();
			this.instrumentbuttons = new ArrayList<>();
			for (int i = 0; i < 6; ++i) {
				final Button tempButton = new Button(this.notemapX - 20 + (i + 1) * 20, this.notemapY - 20);
				this.instrumentbuttons.add(tempButton);
				if (i > 0) {
					tempButton.text = Localization.MODULES.ATTACHMENTS.ACTIVATE_INSTRUMENT.translate(this.instrumentNames[i - 1].translate(new String[0]));
				} else {
					tempButton.text = Localization.MODULES.ATTACHMENTS.DEACTIVATE_INSTRUMENT.translate();
				}
				tempButton.color = this.instrumentColors[i];
			}
		}
	}

	private void updateSpeedButton() {
		if (this.getCart().worldObj.isRemote) {
			this.speedButton.imageID = 14 - this.speedSetting;
			this.speedButton.text = Localization.MODULES.ATTACHMENTS.NOTE_DELAY.translate(String.valueOf(this.getTickDelay()));
		}
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
		for (int i = this.getScrollY(); i < Math.min(this.tracks.size(), this.getScrollY() + this.tracksInView); ++i) {
			final Track track = this.tracks.get(i);
			for (int j = this.getScrollX(); j < Math.min(track.notes.size(), this.getScrollX() + this.notesInView); ++j) {
				final Note note = track.notes.get(j);
				note.drawText(gui, i - this.getScrollY(), j - this.getScrollX());
			}
		}
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void activatedByRail(final int x, final int y, final int z, final boolean active) {
		if (active && !this.isPlaying()) {
			this.setPlaying(true);
		}
	}

	private int getTickDelay() {
		switch (this.speedSetting) {
			case 6: {
				return 1;
			}
			case 5: {
				return 2;
			}
			case 4: {
				return 3;
			}
			case 3: {
				return 5;
			}
			case 2: {
				return 7;
			}
			case 1: {
				return 11;
			}
			case 0: {
				return 13;
			}
			default: {
				return 0;
			}
		}
	}

	@Override
	public void update() {
		super.update();
		if (this.getCart().worldObj.isRemote) {
			this.tooLongTrack = false;
			this.veryLongTrack = false;
			for (int i = 0; i < this.tracks.size(); ++i) {
				final Track track = this.tracks.get(i);
				if (track.notes.size() > this.notesInView) {
					this.tooLongTrack = true;
					if (track.notes.size() > this.veryLongTrackLimit) {
						this.veryLongTrack = true;
					}
				}
				int trackPacketID = -1;
				if (track.addButton.down) {
					track.addButton.down = false;
					trackPacketID = 0;
				} else if (track.removeButton.down) {
					track.removeButton.down = false;
					trackPacketID = 1;
				} else if (track.volumeButton.down) {
					track.volumeButton.down = false;
					trackPacketID = 2;
				}
				if (trackPacketID != -1) {
					final byte info = (byte) (i | trackPacketID << 4);
					this.sendPacket(1, info);
				}
			}
			if (!this.tooLongTrack) {
				this.pixelScrollX = 0;
				this.isScrollingX = false;
			}
			if (!this.veryLongTrack) {
				this.pixelScrollXTune = 0;
				this.isScrollingXTune = false;
			}
			if (!(this.tooTallModule = (this.tracks.size() > this.tracksInView))) {
				this.pixelScrollY = 0;
				this.isScrollingY = false;
			}
			this.generateScrollX();
			this.generateScrollY();
			if (this.createTrack.down) {
				this.createTrack.down = false;
				this.sendPacket(0, (byte) 0);
			}
			if (this.removeTrack.down) {
				this.removeTrack.down = false;
				this.sendPacket(0, (byte) 1);
			}
			if (this.speedButton.down) {
				this.speedButton.down = false;
				this.sendPacket(0, (byte) 2);
			}
			for (int i = 0; i < this.instrumentbuttons.size(); ++i) {
				if (this.instrumentbuttons.get(i).down && i != this.currentInstrument) {
					this.currentInstrument = i;
					break;
				}
			}
			for (int i = 0; i < this.instrumentbuttons.size(); ++i) {
				if (this.instrumentbuttons.get(i).down && i != this.currentInstrument) {
					this.instrumentbuttons.get(i).down = false;
				}
			}
			if (this.currentInstrument != -1 && !this.instrumentbuttons.get(this.currentInstrument).down) {
				this.currentInstrument = -1;
			}
		}
		if (this.isPlaying()) {
			if (this.currentTick <= 0) {
				boolean found = false;
				for (final Track track2 : this.tracks) {
					if (track2.notes.size() > this.playProgress) {
						final Note note = track2.notes.get(this.playProgress);
						float volume = 0.0f;
						switch (track2.volume) {
							case 0: {
								volume = 0.0f;
								break;
							}
							case 1: {
								volume = 0.33f;
								break;
							}
							case 2: {
								volume = 0.67f;
								break;
							}
							default: {
								volume = 1.0f;
								break;
							}
						}
						note.play(volume);
						found = true;
					}
				}
				if (!found) {
					if (!this.getCart().worldObj.isRemote) {
						this.setPlaying(false);
					}
					this.playProgress = 0;
				} else {
					++this.playProgress;
				}
				this.currentTick = this.getTickDelay() - 1;
			} else {
				--this.currentTick;
			}
		}
	}

	@Override
	public int guiWidth() {
		return 310;
	}

	@Override
	public int guiHeight() {
		return 150;
	}

	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/note.png");
		for (int i = this.getScrollY(); i < Math.min(this.tracks.size(), this.getScrollY() + this.tracksInView); ++i) {
			final Track track = this.tracks.get(i);
			for (int j = this.getScrollX(); j < Math.min(track.notes.size(), this.getScrollX() + this.notesInView); ++j) {
				final Note note = track.notes.get(j);
				note.draw(gui, x, y, i - this.getScrollY(), j - this.getScrollX());
			}
		}
		for (final Button button : this.buttons) {
			button.draw(gui, x, y);
		}
		if (this.tooLongTrack) {
			this.drawImage(gui, this.scrollXrect, 48, 0);
			int[] marker = this.getMarkerX();
			this.drawImage(gui, marker, 148, 1);
			if (this.veryLongTrack) {
				marker = this.getMarkerXTune();
				this.drawImage(gui, marker, 153, 1);
			}
		} else {
			this.drawImage(gui, this.scrollXrect, 48, 16);
		}
		if (this.tooTallModule) {
			this.drawImage(gui, this.scrollYrect, 0, 48);
			final int[] marker = this.getMarkerY();
			this.drawImage(gui, marker, 1, 148);
		} else {
			this.drawImage(gui, this.scrollYrect, 16, 48);
		}
	}

	private int[] getMarkerX() {
		return this.generateMarkerX(this.pixelScrollX);
	}

	private int[] getMarkerXTune() {
		return this.generateMarkerX(this.pixelScrollXTune);
	}

	private int[] generateMarkerX(final int x) {
		return new int[] { this.scrollXrect[0] + x, this.scrollXrect[1] + 1, 5, 14 };
	}

	private void setMarkerX(final int x) {
		this.pixelScrollX = this.generateNewMarkerX(x);
	}

	private void setMarkerXTune(final int x) {
		this.pixelScrollXTune = this.generateNewMarkerX(x);
	}

	private int generateNewMarkerX(final int x) {
		int temp = x - this.scrollXrect[0];
		if (temp < 0) {
			temp = 0;
		} else if (temp > this.scrollXrect[2] - 5) {
			temp = this.scrollXrect[2] - 5;
		}
		return temp;
	}

	private int getScrollX() {
		return this.generatedScrollX;
	}

	private void generateScrollX() {
		if (this.tooLongTrack) {
			int maxNotes = -1;
			for (int i = 0; i < this.tracks.size(); ++i) {
				maxNotes = Math.max(maxNotes, this.tracks.get(i).notes.size());
			}
			maxNotes -= this.notesInView;
			final float widthOfBlockInScrollArea = (this.scrollXrect[2] - 5) / maxNotes;
			this.generatedScrollX = Math.round(this.pixelScrollX / widthOfBlockInScrollArea);
			if (this.veryLongTrack) {
				this.generatedScrollX += (int) (this.pixelScrollXTune / (this.scrollXrect[2] - 5) * 50.0f);
			}
		} else {
			this.generatedScrollX = 0;
		}
	}

	private int[] getMarkerY() {
		return new int[] { this.scrollYrect[0] + 1, this.scrollYrect[1] + this.pixelScrollY, 14, 5 };
	}

	private void setMarkerY(final int y) {
		this.pixelScrollY = y - this.scrollYrect[1];
		if (this.pixelScrollY < 0) {
			this.pixelScrollY = 0;
		} else if (this.pixelScrollY > this.scrollYrect[3] - 5) {
			this.pixelScrollY = this.scrollYrect[3] - 5;
		}
	}

	private int getScrollY() {
		return this.generatedScrollY;
	}

	private void generateScrollY() {
		if (this.tooTallModule) {
			final int maxTracks = this.tracks.size() - this.tracksInView;
			final float heightOfBlockInScrollArea = (this.scrollYrect[3] - 5) / maxTracks;
			this.generatedScrollY = Math.round(this.pixelScrollY / heightOfBlockInScrollArea);
		} else {
			this.generatedScrollY = 0;
		}
	}

	@Override
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		for (int i = this.getScrollY(); i < Math.min(this.tracks.size(), this.getScrollY() + this.tracksInView); ++i) {
			final Track track = this.tracks.get(i);
			for (int j = this.getScrollX(); j < Math.min(track.notes.size(), this.getScrollX() + this.notesInView); ++j) {
				final Note note = track.notes.get(j);
				if (note.instrumentId != 0) {
					this.drawStringOnMouseOver(gui, note.toString(), x, y, note.getBounds(i - this.getScrollY(), j - this.getScrollX()));
				}
			}
		}
		for (final Button button : this.buttons) {
			if (button.text != null && button.text.length() > 0) {
				button.overlay(gui, x, y);
			}
		}
	}

	@Override
	public void mouseMovedOrUp(final GuiMinecart gui, final int x, final int y, final int button) {
		if (this.isScrollingX) {
			this.setMarkerX(x);
			if (button != -1) {
				this.isScrollingX = false;
			}
		}
		if (this.isScrollingXTune) {
			this.setMarkerXTune(x);
			if (button != -1) {
				this.isScrollingXTune = false;
			}
		}
		if (this.isScrollingY) {
			this.setMarkerY(y + this.getCart().getRealScrollY());
			if (button != -1) {
				this.isScrollingY = false;
			}
		}
	}

	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int buttonId) {
		if (buttonId == 0) {
			for (final Button button : this.buttons) {
				button.clicked(x, y);
			}
			if (!this.isScrollingX && this.inRect(x, y, this.scrollXrect)) {
				this.isScrollingX = true;
			} else if (!this.isScrollingY && this.inRect(x, y, this.scrollYrect)) {
				this.isScrollingY = true;
			}
		} else if (buttonId == 1 && !this.isScrollingXTune && this.inRect(x, y, this.scrollXrect)) {
			this.isScrollingXTune = true;
		}
		if (buttonId == 0 || buttonId == 1) {
			for (int i = this.getScrollY(); i < Math.min(this.tracks.size(), this.getScrollY() + this.tracksInView); ++i) {
				final Track track = this.tracks.get(i);
				for (int j = this.getScrollX(); j < Math.min(track.notes.size(), this.getScrollX() + this.notesInView); ++j) {
					final Note note = track.notes.get(j);
					if (this.inRect(x, y, note.getBounds(i - this.getScrollY(), j - this.getScrollX()))) {
						int instrumentInfo = this.currentInstrument;
						if (instrumentInfo == -1) {
							if (buttonId == 0) {
								instrumentInfo = 6;
							} else {
								instrumentInfo = 7;
							}
						}
						if (this.currentInstrument != -1 || note.instrumentId != 0) {
							byte info = (byte) i;
							info |= (byte) (instrumentInfo << 4);
							this.sendPacket(2, new byte[] { info, (byte) j });
						}
					}
				}
			}
		}
	}

	@Override
	public int numberOfGuiData() {
		return 1 + (this.maximumNotesPerTrack + 1) * this.maximumTracksPerModule;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		short moduleHeader = (short) this.tracks.size();
		moduleHeader |= (short) (this.speedSetting << 4);
		this.updateGuiData(info, 0, moduleHeader);
		for (int i = 0; i < this.tracks.size(); ++i) {
			final Track track = this.tracks.get(i);
			this.updateGuiData(info, 1 + (this.maximumNotesPerTrack + 1) * i, track.getInfo());
			for (int j = 0; j < track.notes.size(); ++j) {
				final Note note = track.notes.get(j);
				this.updateGuiData(info, 1 + (this.maximumNotesPerTrack + 1) * i + 1 + j, note.getInfo());
			}
		}
	}

	@Override
	public void receiveGuiData(int id, final short data) {
		if (id == 0) {
			final int trackCount = data & this.maximumTracksPerModule;
			this.speedSetting = (data & ~this.maximumTracksPerModule) >> 4;
			this.updateSpeedButton();
			while (this.tracks.size() < trackCount) {
				new Track();
			}
			while (this.tracks.size() > trackCount) {
				this.tracks.get(this.tracks.size() - 1).unload();
				this.tracks.remove(this.tracks.size() - 1);
			}
		} else {
			final int trackId = --id / (this.maximumNotesPerTrack + 1);
			int noteId = id % (this.maximumNotesPerTrack + 1);
			final Track track = this.tracks.get(trackId);
			if (noteId == 0) {
				track.setInfo(data);
			} else {
				--noteId;
				final Note note = track.notes.get(noteId);
				note.setInfo(data);
			}
		}
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		registerDw(PLAYING, false);
	}

	private boolean isPlaying() {
		return !this.isPlaceholder() && (this.getDw(PLAYING) || this.playProgress > 0);
	}

	private void setPlaying(final boolean val) {
		this.updateDw(PLAYING, val);
	}

	@Override
	public int numberOfPackets() {
		return 3;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			if (data[0] == 0) {
				if (this.tracks.size() < this.maximumTracksPerModule) {
					new Track();
				}
			} else if (data[0] == 1) {
				if (this.tracks.size() > 0) {
					this.tracks.remove(this.tracks.size() - 1);
				}
			} else if (data[0] == 2) {
				++this.speedSetting;
				if (this.speedSetting >= 7) {
					this.speedSetting = 0;
				}
			}
		} else if (id == 1) {
			final int trackID = data[0] & this.maximumTracksPerModule;
			final int trackPacketID = (data[0] & ~this.maximumTracksPerModule) >> 4;
							if (trackID < this.tracks.size()) {
								final Track track = this.tracks.get(trackID);
								if (trackPacketID == 0) {
									if (track.notes.size() < this.maximumNotesPerTrack) {
										new Note(track);
									}
								} else if (trackPacketID == 1) {
									if (track.notes.size() > 0) {
										track.notes.remove(track.notes.size() - 1);
									}
								} else if (trackPacketID == 2) {
									track.volume = (track.volume + 1) % 4;
								}
							}
		} else if (id == 2) {
			final byte info = data[0];
			final byte noteID = data[1];
			final byte trackID2 = (byte) (info & this.maximumTracksPerModule);
			final byte instrumentInfo = (byte) ((byte) (info & ~(byte) this.maximumTracksPerModule) >> 4);
			if (trackID2 < this.tracks.size()) {
				final Track track2 = this.tracks.get(trackID2);
				if (noteID < track2.notes.size()) {
					final Note note = track2.notes.get(noteID);
					if (instrumentInfo < 6) {
						note.instrumentId = instrumentInfo;
					} else if (instrumentInfo == 6) {
						final Note note2 = note;
						++note2.pitch;
						if (note.pitch > 24) {
							note.pitch = 0;
						}
					} else {
						final Note note3 = note;
						--note3.pitch;
						if (note.pitch < 0) {
							note.pitch = 24;
						}
					}
				}
			}
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		short headerInfo = (short) this.tracks.size();
		headerInfo |= (short) (this.speedSetting << 4);
		tagCompound.setShort(this.generateNBTName("Header", id), headerInfo);
		for (int i = 0; i < this.tracks.size(); ++i) {
			final Track track = this.tracks.get(i);
			tagCompound.setShort(this.generateNBTName("Track" + i, id), track.getInfo());
			for (int j = 0; j < track.notes.size(); ++j) {
				final Note note = track.notes.get(j);
				tagCompound.setShort(this.generateNBTName("Note" + i + ":" + j, id), note.getInfo());
			}
		}
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		final short headerInfo = tagCompound.getShort(this.generateNBTName("Header", id));
		this.receiveGuiData(0, headerInfo);
		for (int i = 0; i < this.tracks.size(); ++i) {
			final short trackInfo = tagCompound.getShort(this.generateNBTName("Track" + i, id));
			this.receiveGuiData(1 + (this.maximumNotesPerTrack + 1) * i, trackInfo);
			final Track track = this.tracks.get(i);
			for (int j = 0; j < track.notes.size(); ++j) {
				final short noteInfo = tagCompound.getShort(this.generateNBTName("Note" + i + ":" + j, id));
				this.receiveGuiData(1 + (this.maximumNotesPerTrack + 1) * i + 1 + j, noteInfo);
			}
		}
	}

	private class TrackButton extends Button {
		private int trackID;
		private int x;

		public TrackButton(final int x, final int trackID) {
			super(0, 0);
			this.trackID = trackID;
			this.x = x;
		}

		@Override
		public int[] getRect() {
			return new int[] { this.x, ModuleNote.this.notemapY + (this.trackID - ModuleNote.this.getScrollY()) * ModuleNote.this.trackHeight, 16, 16 };
		}

		private boolean isValid() {
			return ModuleNote.this.getScrollY() <= this.trackID && this.trackID < ModuleNote.this.getScrollY() + ModuleNote.this.tracksInView;
		}

		@Override
		public void draw(final GuiMinecart gui, final int x, final int y) {
			if (this.isValid()) {
				super.draw(gui, x, y);
			}
		}

		@Override
		public void overlay(final GuiMinecart gui, final int x, final int y) {
			if (this.isValid()) {
				super.overlay(gui, x, y);
			}
		}

		@Override
		public void clicked(final int x, final int y) {
			if (this.isValid()) {
				super.clicked(x, y);
			}
		}
	}

	private class Button {
		public int[] rect;
		public boolean down;
		public String text;
		public int color;
		public int imageID;

		public Button(final int x, final int y) {
			this.down = false;
			this.rect = new int[] { x, y, 16, 16 };
			this.color = 0;
			this.imageID = -1;
			ModuleNote.this.buttons.add(this);
		}

		public int[] getRect() {
			return this.rect;
		}

		public void overlay(final GuiMinecart gui, final int x, final int y) {
			ModuleNote.this.drawStringOnMouseOver(gui, this.text, x, y, this.getRect());
		}

		public void clicked(final int x, final int y) {
			if (ModuleNote.this.inRect(x, y, this.getRect())) {
				this.down = !this.down;
			}
		}

		public void draw(final GuiMinecart gui, final int x, final int y) {
			if (!ModuleNote.this.inRect(x, y, this.getRect())) {
				GL11.glColor4f((this.color >> 16) / 255.0f, (this.color >> 8 & 0xFF) / 255.0f, (this.color & 0xFF) / 255.0f, 1.0f);
			}
			ModuleNote.this.drawImage(gui, this.getRect(), 32, 0);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			int srcX = 0;
			final int srcY = 16;
			if (this.down) {
				srcX += 16;
			}
			ModuleNote.this.drawImage(gui, this.getRect(), srcX, srcY);
			if (this.imageID != -1) {
				ModuleNote.this.drawImage(gui, this.getRect(), this.imageID * 16, 32);
			}
		}
	}

	private class Note {
		public int instrumentId;
		public int pitch;

		public Note(final Track track) {
			track.notes.add(this);
		}

		public void drawText(final GuiMinecart gui, final int trackID, final int noteID) {
			if (this.instrumentId == 0) {
				return;
			}
			final int[] rect = this.getBounds(trackID, noteID);
			String str = String.valueOf(this.pitch);
			if (str.length() < 2) {
				str = "0" + str;
			}
			ModuleNote.this.drawString(gui, str, rect[0] + 3, rect[1] + 6, ModuleNote.this.instrumentColors[this.instrumentId]);
		}

		public void draw(final GuiMinecart gui, final int x, final int y, final int trackID, final int noteID) {
			int srcX = 0;
			if (this.instrumentId == 0) {
				srcX += 16;
			}
			final int[] rect = this.getBounds(trackID, noteID);
			if (this.instrumentId != 0 && ModuleNote.this.playProgress == noteID + ModuleNote.this.getScrollX() && ModuleNote.this.isPlaying()) {
				GL11.glColor4f(0.3f, 0.3f, 0.3f, 1.0f);
			}
			ModuleNote.this.drawImage(gui, rect, srcX, 0);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			if (ModuleNote.this.inRect(x, y, rect)) {
				ModuleNote.this.drawImage(gui, rect, 32, 0);
			}
		}

		public int[] getBounds(final int trackID, final int noteID) {
			return new int[] { ModuleNote.this.notemapX + noteID * 16, ModuleNote.this.notemapY + trackID * ModuleNote.this.trackHeight, 16, 16 };
		}

		public short getInfo() {
			short info = 0;
			info |= (short) this.instrumentId;
			info |= (short) (this.pitch << 3);
			return info;
		}

		public void setInfo(final short val) {
			this.instrumentId = (val & 0x7);
			this.pitch = (val & 0xF8) >> 3;
		}

		public void play(final float volume) {
			if (this.instrumentId == 0) {
				return;
			}
			if (!ModuleNote.this.getCart().worldObj.isRemote) {
				if (volume > 0.0f) {
					final float calculatedPitch = (float) Math.pow(2.0, (this.pitch - 12) / 12.0);
					SoundEvent event = SoundEvents.BLOCK_NOTE_HARP;
					if (this.instrumentId == 2) {
						event = SoundEvents.BLOCK_NOTE_BASEDRUM;
					} else if (this.instrumentId == 3) {
						event = SoundEvents.BLOCK_NOTE_SNARE;
					} else if (this.instrumentId == 4) {
						event = SoundEvents.BLOCK_NOTE_HAT;
					} else if (this.instrumentId == 5) {
						event = SoundEvents.BLOCK_NOTE_BASS;
					}
					getCart().worldObj.playSound(null, getCart().getPosition(), event, SoundCategory.RECORDS, volume, calculatedPitch);
				}
			} else {
				double oX = 0.0;
				double oZ = 0.0;
				if (ModuleNote.this.getCart().motionX != 0.0) {
					oX = ((ModuleNote.this.getCart().motionX > 0.0) ? -1 : 1);
				}
				if (ModuleNote.this.getCart().motionZ != 0.0) {
					oZ = ((ModuleNote.this.getCart().motionZ > 0.0) ? -1 : 1);
				}
				ModuleNote.this.getCart().worldObj.spawnParticle(EnumParticleTypes.NOTE, ModuleNote.this.getCart().x() + oZ * 1.0 + 0.5, ModuleNote.this.getCart().y() + 1.2, ModuleNote.this.getCart().z() + oX * 1.0 + 0.5, this.pitch / 24.0, 0.0, 0.0);
				ModuleNote.this.getCart().worldObj.spawnParticle(EnumParticleTypes.NOTE, ModuleNote.this.getCart().x() + oZ * -1.0 + 0.5, ModuleNote.this.getCart().y() + 1.2, ModuleNote.this.getCart().z() + oX * -1.0 + 0.5, this.pitch / 24.0, 0.0, 0.0);
			}
		}

		@Override
		public String toString() {
			if (this.instrumentId == 0) {
				return "Unknown instrument";
			}
			return ModuleNote.this.instrumentNames[this.instrumentId - 1].translate() + " " + ModuleNote.this.pitchNames[this.pitch];
		}
	}

	private class Track {
		public ArrayList<Note> notes;
		public Button addButton;
		public Button removeButton;
		public Button volumeButton;
		public int volume;
		public int lastNoteCount;

		public Track() {
			this.notes = new ArrayList<>();
			this.volume = 3;
			if (ModuleNote.this.getCart().worldObj.isRemote) {
				final int ID = ModuleNote.this.tracks.size() + 1;
				this.addButton = new TrackButton(ModuleNote.this.notemapX - 60, ID - 1);
				this.addButton.text = Localization.MODULES.ATTACHMENTS.ADD_NOTE.translate(String.valueOf(ID));
				this.addButton.imageID = 2;
				this.removeButton = new TrackButton(ModuleNote.this.notemapX - 40, ID - 1);
				this.removeButton.text = Localization.MODULES.ATTACHMENTS.REMOVE_NOTE.translate(String.valueOf(ID));
				this.removeButton.imageID = 3;
				this.volumeButton = new TrackButton(ModuleNote.this.notemapX - 20, ID - 1);
				this.volumeButton.text = this.getVolumeText();
				this.volumeButton.imageID = 4;
			}
			ModuleNote.this.tracks.add(this);
		}

		private String getVolumeText() {
			return Localization.MODULES.ATTACHMENTS.VOLUME.translate(String.valueOf(this.volume));
		}

		public void unload() {
			ModuleNote.this.buttons.remove(this.addButton);
			ModuleNote.this.buttons.remove(this.removeButton);
			ModuleNote.this.buttons.remove(this.volumeButton);
		}

		public short getInfo() {
			short info = 0;
			info |= (short) this.notes.size();
			info |= (short) (this.volume << 12);
			return info;
		}

		public void setInfo(final short val) {
			final int numberofNotes = val & ModuleNote.this.maximumNotesPerTrack;
			while (this.notes.size() < numberofNotes) {
				new Note(this);
			}
			while (this.notes.size() > numberofNotes) {
				this.notes.remove(this.notes.size() - 1);
			}
			this.volume = (val & ~ModuleNote.this.maximumNotesPerTrack) >> 12;
			if (ModuleNote.this.getCart().worldObj.isRemote) {
				this.volumeButton.imageID = 4 + this.volume;
				this.volumeButton.text = this.getVolumeText();
			}
		}
	}
}
