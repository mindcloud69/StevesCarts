package vswe.stevescarts.Arcade;

import java.util.ArrayList;

public class Piece {
	private ArcadeMonopoly game;
	private int pos;
	private int u;
	private int extended;
	private int[] money;
	private CONTROLLED_BY control;
	private ArrayList<NoteAnimation> animationNotes;
	private ArrayList<NoteAnimation> oldnotes;
	private boolean bankrupt;
	private int turnsInJail;

	public Piece(final ArcadeMonopoly game, final int u, final CONTROLLED_BY control) {
		this.game = game;
		this.pos = 0;
		this.u = u;
		this.money = new int[] { 30, 30, 30, 30, 30, 30, 30 };
		this.control = control;
		this.animationNotes = new ArrayList<NoteAnimation>();
		this.oldnotes = new ArrayList<NoteAnimation>();
		this.turnsInJail = -1;
	}

	public void move(final int dif) {
		this.pos = (this.pos + dif) % 48;
	}

	public int getPosition() {
		return this.pos;
	}

	public int getV() {
		return this.u;
	}

	public int[] getNoteCount() {
		return this.money;
	}

	public int getNoteCount(final Note note) {
		int money = this.money[note.getId()];
		for (int i = 0; i < this.oldnotes.size(); ++i) {
			if (note == this.oldnotes.get(i).getNote()) {
				--money;
			}
		}
		return money;
	}

	public int getTotalMoney() {
		int money = 0;
		for (int i = 0; i < Note.notes.size(); ++i) {
			money += Note.notes.get(i).getUnits() * this.money[i];
		}
		for (int i = 0; i < this.oldnotes.size(); ++i) {
			money -= this.oldnotes.get(i).getNote().getUnits();
		}
		return money;
	}

	public void addMoney(int money, final boolean useAnimation) {
		for (int i = Note.notes.size() - 1; i >= 0; --i) {
			final Note note = Note.notes.get(i);
			final int notesToAdd = money / note.getUnits();
			if (notesToAdd > 0) {
				this.addMoney(note, notesToAdd, true);
				money -= notesToAdd * note.getUnits();
			}
			if (money == 0) {
				return;
			}
		}
	}

	public void addMoney(final Note note, final int amount, final boolean useAnimation) {
		if (useAnimation) {
			int min = 10;
			for (final NoteAnimation animation : this.animationNotes) {
				if (animation.getAnimation() < min) {
					min = animation.getAnimation();
				}
			}
			for (int i = 0; i < amount; ++i) {
				this.animationNotes.add(0, new NoteAnimation(note, min - 10, true));
				min -= 10;
			}
		} else {
			final int[] money = this.money;
			final int id = note.getId();
			money[id] += amount;
		}
	}

	public void removeNewNoteAnimation(final int i) {
		if (this.animationNotes.get(i).isNew()) {
			this.addMoney(this.animationNotes.get(i).getNote(), 1, false);
		} else {
			final Note note = this.animationNotes.get(i).getNote();
			for (int j = this.oldnotes.size() - 1; j >= 0; --j) {
				if (note == this.oldnotes.get(j).getNote()) {
					this.oldnotes.remove(j);
					break;
				}
			}
			this.removeMoney(note, 1, false);
		}
		this.animationNotes.remove(i);
	}

	public ArrayList<NoteAnimation> getAnimationNotes() {
		return this.animationNotes;
	}

	public boolean removeMoney(int money, final boolean useAnimation) {
		final int[] noteCounts = new int[Note.notes.size()];
		final int[] moneyBelowThisLevel = new int[Note.notes.size()];
		int totalmoney = 0;
		for (int i = 0; i < noteCounts.length; ++i) {
			noteCounts[i] = this.getNoteCount(Note.notes.get(i));
			moneyBelowThisLevel[i] = totalmoney;
			totalmoney += noteCounts[i] * Note.notes.get(i).getUnits();
		}
		if (totalmoney >= money) {
			for (int i = Note.notes.size() - 1; i >= 0; --i) {
				final Note note = Note.notes.get(i);
				int notesToRemove = money / note.getUnits();
				notesToRemove = Math.min(notesToRemove, noteCounts[i]);
				this.removeMoney(note, notesToRemove, useAnimation);
				money -= note.getUnits() * notesToRemove;
				if (money == 0) {
					return true;
				}
				if (moneyBelowThisLevel[i] < money) {
					this.removeMoney(note, 1, useAnimation);
					money -= note.getUnits();
					this.addMoney(-money, useAnimation);
					return true;
				}
			}
		}
		return false;
	}

	private void removeMoney(final Note note, final int amount, final boolean useAnimation) {
		if (useAnimation) {
			int min = 10;
			for (final NoteAnimation animation : this.animationNotes) {
				if (animation.getAnimation() < min) {
					min = animation.getAnimation();
				}
			}
			for (int i = 0; i < amount; ++i) {
				final NoteAnimation animation = new NoteAnimation(note, min - 10, false);
				this.animationNotes.add(0, animation);
				this.oldnotes.add(0, animation);
				min -= 10;
			}
		} else {
			final int[] money = this.money;
			final int id = note.getId();
			money[id] -= amount;
		}
	}

	public int[] getMenuRect(final int i) {
		final int w = 50 + this.extended;
		return new int[] { 443 - w, 10 + i * 30, w, 30 };
	}

	public int[] getPlayerMenuRect(final int i) {
		final int[] menu = this.getMenuRect(i);
		return new int[] { menu[0] + 19, menu[1] + 3, 24, 24 };
	}

	public void updateExtending(final boolean inRect) {
		if (inRect && this.extended < 175) {
			this.extended = Math.min(175, this.extended + 20);
		} else if (!inRect && this.extended > 0) {
			this.extended = Math.max(0, this.extended - 50);
		}
	}

	public CONTROLLED_BY getController() {
		return this.control;
	}

	public boolean showProperties() {
		return this == this.game.getCurrentPiece();
	}

	public boolean canAffordProperty(final Property property) {
		return this.getTotalMoney() >= property.getCost();
	}

	public void purchaseProperty(final Property property) {
		if (this.removeMoney(property.getCost(), true)) {
			property.setOwner(this);
		} else {
			System.out.println("Couldn't remove the resources, this is very weird :S");
		}
	}

	public void bankrupt(final Piece owesMoneyToThis) {
		final int money = this.getTotalMoney();
		this.removeMoney(money, true);
		if (owesMoneyToThis != null) {
			owesMoneyToThis.addMoney(money, true);
		}
		for (final Place place : this.game.getPlaces()) {
			if (place instanceof Property) {
				final Property property = (Property) place;
				if (property.getOwner() == this) {
					property.setOwner(owesMoneyToThis);
				}
			}
		}
		this.bankrupt = true;
	}

	public boolean canAffordRent(final Property property) {
		return this.getTotalMoney() >= property.getRentCost();
	}

	public void payPropertyRent(final Property property) {
		if (this.removeMoney(property.getRentCost(), true)) {
			property.getOwner().addMoney(property.getRentCost(), true);
		} else {
			System.out.println("Couldn't remove the resources, this is very weird :S");
		}
	}

	public boolean isBankrupt() {
		return this.bankrupt;
	}

	public boolean canAffordStructure(final Street street) {
		return this.getTotalMoney() >= street.getStructureCost();
	}

	public void buyStructure(final Street street) {
		if (this.removeMoney(street.getStructureCost(), true)) {
			street.increaseStructure();
		} else {
			System.out.println("Couldn't remove the resources, this is very weird :S");
		}
	}

	public boolean isInJail() {
		return this.turnsInJail >= 0;
	}

	public void goToJail() {
		this.turnsInJail = 0;
		this.pos = 14;
	}

	public void releaseFromJail() {
		this.turnsInJail = -1;
	}

	public void increaseTurnsInJail() {
		++this.turnsInJail;
	}

	public int getTurnsInJail() {
		return this.turnsInJail;
	}

	public void payFine() {
		if (this.removeMoney(50, true)) {
			this.releaseFromJail();
		} else {
			System.out.println("Couldn't remove the resources, this is very weird :S");
		}
	}

	public boolean canAffordFine() {
		return this.getTotalMoney() >= 50;
	}

	public void getMoneyFromMortgage(final Property selectedPlace) {
		this.addMoney(selectedPlace.getMortgageValue(), true);
		selectedPlace.mortgage();
	}

	public boolean canAffordUnMortgage(final Property selectedPlace) {
		return this.getTotalMoney() >= selectedPlace.getUnMortgagePrice();
	}

	public void payUnMortgage(final Property selectedPlace) {
		if (this.removeMoney(selectedPlace.getUnMortgagePrice(), true)) {
			selectedPlace.unMortgage();
		} else {
			System.out.println("Couldn't remove the resources, this is very weird :S");
		}
	}

	public void sellStructure(final Street selectedPlace) {
		this.addMoney(selectedPlace.getStructureSellPrice(), true);
		selectedPlace.decreaseStructures();
	}

	public enum CONTROLLED_BY {
		PLAYER,
		COMPUTER,
		OTHER
	}
}
