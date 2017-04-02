package vswe.stevescarts.arcade.monopoly;

import org.lwjgl.opengl.GL11;
import vswe.stevescarts.guis.GuiMinecart;

import java.util.EnumSet;

public class Street extends Property {
	private float[] color;
	private int structures;
	private int baseRent;

	public Street(final ArcadeMonopoly game, final StreetGroup group, final String name, final int cost, final int baseRent) {
		super(game, group, name, cost);
		this.color = group.getColor();
		this.baseRent = baseRent;
	}

	@Override
	public void draw(final GuiMinecart gui, final EnumSet<PLACE_STATE> states) {
		super.draw(gui, states);
		GL11.glColor4f(this.color[0], this.color[1], this.color[2], 1.0f);
		this.game.getModule().drawImage(gui, 0, 0, 76, 0, 76, 22);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		if (this.structures > 0 && this.structures < 5) {
			for (int i = 0; i < this.structures; ++i) {
				this.game.getModule().drawImage(gui, 3 + i * 18, 3, 76, 22, 16, 16);
			}
		} else if (this.structures == 5) {
			this.game.getModule().drawImage(gui, 3, 3, 92, 22, 16, 16);
		}
		this.drawValue(gui);
	}

	public void increaseStructure() {
		++this.structures;
	}

	@Override
	protected int getTextY() {
		return 30;
	}

	public int getRentCost(final int structureCount) {
		switch (structureCount) {
			default: {
				return this.baseRent;
			}
			case 1: {
				return this.baseRent * 5;
			}
			case 2: {
				return this.baseRent * 15;
			}
			case 3: {
				return this.baseRent * 40;
			}
			case 4: {
				return this.baseRent * 70;
			}
			case 5: {
				return this.baseRent * 100;
			}
		}
	}

	public int getRentCost(final boolean ownsAll) {
		if (ownsAll) {
			return this.baseRent * 2;
		}
		return this.baseRent;
	}

	@Override
	public int getRentCost() {
		if (this.structures == 0) {
			return this.getRentCost(this.ownsAllInGroup(this.getOwner()));
		}
		return this.getRentCost(this.structures);
	}

	public int getStructureCount() {
		return this.structures;
	}

	public int getStructureCost() {
		return ((StreetGroup) this.getGroup()).getStructureCost();
	}

	public boolean ownsAllInGroup(final Piece currentPiece) {
		for (final Property property : this.getGroup().getProperties()) {
			if (property.getOwner() != currentPiece || property.isMortgaged()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canMortgage() {
		return super.canMortgage() && this.structures == 0;
	}

	public int getStructureSellPrice() {
		return this.getStructureCost() / 2;
	}

	public void decreaseStructures() {
		--this.structures;
	}
}
