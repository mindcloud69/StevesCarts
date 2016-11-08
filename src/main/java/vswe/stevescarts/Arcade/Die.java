package vswe.stevescarts.arcade;

import vswe.stevescarts.guis.GuiMinecart;

public class Die {
	private ArcadeMonopoly game;
	private int number;
	private int graphicalId;

	public Die(final ArcadeMonopoly game, final int graphicalId) {
		this.game = game;
		this.graphicalId = graphicalId;
		this.randomize();
	}

	public void draw(final GuiMinecart gui, final int x, final int y) {
		this.game.getModule().drawImage(gui, x, y, 256 - 24 * (this.graphicalId + 1), 232, 24, 24);
		switch (this.number) {
			case 5: {
				this.drawEye(gui, x + 15, y + 3);
				this.drawEye(gui, x + 3, y + 15);
			}
			case 3: {
				this.drawEye(gui, x + 3, y + 3);
				this.drawEye(gui, x + 15, y + 15);
			}
			case 1: {
				this.drawEye(gui, x + 9, y + 9);
				break;
			}
			case 4: {
				this.drawEye(gui, x + 3, y + 3);
				this.drawEye(gui, x + 15, y + 15);
			}
			case 2: {
				this.drawEye(gui, x + 15, y + 3);
				this.drawEye(gui, x + 3, y + 15);
				break;
			}
			case 6: {
				this.drawEye(gui, x + 3, y + 2);
				this.drawEye(gui, x + 3, y + 9);
				this.drawEye(gui, x + 3, y + 16);
				this.drawEye(gui, x + 15, y + 2);
				this.drawEye(gui, x + 15, y + 9);
				this.drawEye(gui, x + 15, y + 16);
				break;
			}
		}
	}

	private void drawEye(final GuiMinecart gui, final int x, final int y) {
		this.game.getModule().drawImage(gui, x, y, 256 - 6 * (this.graphicalId + 1), 226, 6, 6);
	}

	public int getNumber() {
		return this.number;
	}

	public void randomize() {
		this.number = this.game.getModule().getCart().rand.nextInt(6) + 1;
	}
}
