package vswe.stevescarts.arcade;

import vswe.stevescarts.guis.GuiMinecart;

public class NoteAnimation {
	private Note note;
	private int animation;
	private boolean isNew;

	public NoteAnimation(final Note note, final int start, final boolean isNew) {
		this.note = note;
		this.animation = start;
		this.isNew = isNew;
	}

	public boolean draw(final ArcadeMonopoly game, final GuiMinecart gui, final int x, final int y) {
		if (this.animation >= 0) {
			if (this.isNew) {
				this.note.draw(game, gui, x, y - 10 + this.animation / 2);
			} else {
				this.note.draw(game, gui, x, y + this.animation);
			}
		}
		return ++this.animation > 20;
	}

	public Note getNote() {
		return this.note;
	}

	public int getAnimation() {
		return this.animation;
	}

	public boolean isNew() {
		return this.isNew;
	}
}
