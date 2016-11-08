package vswe.stevescarts.arcade.monopoly;

import java.util.EnumSet;

import vswe.stevescarts.guis.GuiMinecart;

public abstract class Property extends Place {
	private String name;
	private int cost;
	private Piece owner;
	private PropertyGroup group;
	private boolean mortgaged;

	public Property(final ArcadeMonopoly game, final PropertyGroup group, final String name, final int cost) {
		super(game);
		(this.group = group).add(this);
		this.name = name;
		this.cost = cost;
	}

	public void drawValue(final GuiMinecart gui) {
		Note.drawValue(this.game, gui, 10, 103, 2, this.cost);
	}

	@Override
	public void drawText(final GuiMinecart gui, final EnumSet<PLACE_STATE> states) {
		this.game.getModule().drawSplitString(gui, this.name, 3 + gui.getGuiLeft(), this.getTextY() + gui.getGuiTop(), 70, true, 4210752);
	}

	protected abstract int getTextY();

	public int getCost() {
		return this.cost;
	}

	public void setOwner(final Piece val) {
		this.owner = val;
	}

	public Piece getOwner() {
		return this.owner;
	}

	public boolean hasOwner() {
		return this.owner != null;
	}

	@Override
	public boolean onPieceStop(final Piece piece) {
		return this.owner == null || this.owner == piece || this.mortgaged;
	}

	public PropertyGroup getGroup() {
		return this.group;
	}

	public abstract int getRentCost();

	public int getMortgageValue() {
		return this.getCost() / 2;
	}

	public int getOwnedInGroup() {
		int owned = 0;
		for (final Property property : this.getGroup().getProperties()) {
			if (property.getOwner() == this.getOwner() && !property.isMortgaged()) {
				++owned;
			}
		}
		return owned;
	}

	public boolean isMortgaged() {
		return this.mortgaged;
	}

	public boolean canMortgage() {
		return true;
	}

	public void mortgage() {
		this.mortgaged = true;
	}

	public int getUnMortgagePrice() {
		return (int) (this.getMortgageValue() * 1.1f);
	}

	public void unMortgage() {
		this.mortgaged = false;
	}
}
