package vswe.stevescarts.arcade;

import java.util.ArrayList;
import java.util.EnumSet;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.modules.realtimers.ModuleArcade;

public class ArcadeMonopoly extends ArcadeGame {
	private Die die;
	private Die die2;
	private ArrayList<Piece> pieces;
	private int currentPiece;
	private Place[] places;
	private int selectedPlace;
	private int diceTimer;
	private int diceCount;
	private int diceDelay;
	private ArrayList<Button> buttons;
	private Button roll;
	private Button end;
	private Button purchase;
	private Button rent;
	private Button bankrupt;
	private Button bed;
	private Button card;
	private Button jail;
	private Button mortgage;
	private Button unmortgage;
	private Button sellbed;
	private boolean rolled;
	private boolean controllable;
	private boolean endable;
	private boolean openedCard;
	private Card currentCard;
	private float cardScale;
	private int cardRotation;
	public static final int PLACE_WIDTH = 76;
	public static final int PLACE_HEIGHT = 122;
	public static final int BOARD_WIDTH = 14;
	public static final int BOARD_HEIGHT = 10;
	public static final float SCALE = 0.17f;
	private static final int CARD_WIDTH = 142;
	private static final int CARD_HEIGHT = 80;
	private static String[] textures;

	protected Place getSelectedPlace() {
		return (this.selectedPlace == -1) ? null : this.places[this.selectedPlace];
	}

	protected Piece getCurrentPiece() {
		return this.pieces.get(this.currentPiece);
	}

	public ArcadeMonopoly(final ModuleArcade module) {
		super(module, Localization.ARCADE.MADNESS);
		this.selectedPlace = -1;
		(this.pieces = new ArrayList<Piece>()).add(new Piece(this, 0, Piece.CONTROLLED_BY.PLAYER));
		this.pieces.add(new Piece(this, 1, Piece.CONTROLLED_BY.COMPUTER));
		this.pieces.add(new Piece(this, 2, Piece.CONTROLLED_BY.COMPUTER));
		this.pieces.add(new Piece(this, 3, Piece.CONTROLLED_BY.COMPUTER));
		this.pieces.add(new Piece(this, 4, Piece.CONTROLLED_BY.COMPUTER));
		final StreetGroup streetGroup1 = new StreetGroup(50, new int[] { 89, 12, 56 });
		final StreetGroup streetGroup2 = new StreetGroup(50, new int[] { 102, 45, 145 });
		final StreetGroup streetGroup3 = new StreetGroup(50, new int[] { 135, 166, 213 });
		final StreetGroup streetGroup4 = new StreetGroup(100, new int[] { 239, 56, 120 });
		final StreetGroup streetGroup5 = new StreetGroup(100, new int[] { 245, 128, 45 });
		final StreetGroup streetGroup6 = new StreetGroup(150, new int[] { 238, 58, 35 });
		final StreetGroup streetGroup7 = new StreetGroup(150, new int[] { 252, 231, 4 });
		final StreetGroup streetGroup8 = new StreetGroup(200, new int[] { 19, 165, 92 });
		final StreetGroup streetGroup9 = new StreetGroup(200, new int[] { 40, 78, 161 });
		final PropertyGroup stationGroup = new PropertyGroup();
		final PropertyGroup utilGroup = new PropertyGroup();
		this.places = new Place[] { new Go(this), new Street(this, streetGroup1, "Soaryn Chest", 30, 2), new Community(this), new Street(this, streetGroup1, "Eddie's Cobble Stairs", 30, 2),
				new Place(this), new Utility(this, utilGroup, 0, "Test"), new Street(this, streetGroup2, "Ecu's Eco Escape", 60, 4), new Station(this, stationGroup, 0, "Wooden Station"),
				new Street(this, streetGroup2, "Test", 60, 4), new Villager(this), new Street(this, streetGroup3, "Direwolf's 9x9", 100, 6), new Chance(this),
				new Street(this, streetGroup3, "Greg's Forest", 100, 6), new Street(this, streetGroup3, "Alice's Tunnel", 110, 8), new Jail(this),
				new Street(this, streetGroup4, "Flora's Alveary", 140, 10), new Utility(this, utilGroup, 1, "Test"), new Street(this, streetGroup4, "Sengir's Greenhouse", 140, 10),
				new Street(this, streetGroup4, "Test", 160, 12), new Station(this, stationGroup, 1, "Standard Station"), new Street(this, streetGroup5, "Muse's Moon Base", 200, 14), new Community(this),
				new Street(this, streetGroup5, "Algorithm's Crafting CPU", 200, 14), new Street(this, streetGroup5, "Pink Lemmingaide Stand", 240, 16), new CornerPlace(this, 2),
				new Street(this, streetGroup6, "Covert's Railyard", 250, 18), new Chance(this), new Street(this, streetGroup6, "Test", 250, 18), new Street(this, streetGroup6, "Test", 270, 20),
				new Community(this), new Street(this, streetGroup6, "Test", 270, 20), new Station(this, stationGroup, 2, "Reinforced Station"),
				new Street(this, streetGroup7, "Player's Industrial Warehouse", 320, 22), new Villager(this), new Street(this, streetGroup7, "Dan's Computer Repair", 320, 22),
				new Street(this, streetGroup7, "iChun's Hat Shop", 350, 24), new Utility(this, utilGroup, 2, "Test"), new Street(this, streetGroup7, "Lex's Forge", 350, 24), new GoToJail(this),
				new Street(this, streetGroup8, "Morvelaira's Pretty Wall", 400, 26), new Street(this, streetGroup8, "Rorax's Tower of Doom", 400, 26), new Community(this),
				new Street(this, streetGroup8, "Jaded's Crash Lab", 440, 30), new Station(this, stationGroup, 3, "Galgadorian Station"), new Chance(this), new Street(this, streetGroup9, "Test", 500, 40),
				new Place(this), new Street(this, streetGroup9, "Vswe's Redstone Tower", 600, 50) };
		((Property) this.places[1]).setOwner(this.pieces.get(0));
		((Property) this.places[3]).setOwner(this.pieces.get(0));
		this.die = new Die(this, 0);
		this.die2 = new Die(this, 1);
		(this.buttons = new ArrayList<Button>()).add(this.roll = new Button() {
			@Override
			public String getName() {
				return "Roll";
			}

			@Override
			public boolean isVisible() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return ArcadeMonopoly.this.diceCount == 0 && ArcadeMonopoly.this.diceTimer == 0 && !ArcadeMonopoly.this.rolled;
			}

			@Override
			public void onClick() {
				ArcadeMonopoly.this.rolled = true;
				ArcadeMonopoly.this.throwDice();
			}
		});
		this.buttons.add(this.end = new Button() {
			@Override
			public String getName() {
				return "End Turn";
			}

			@Override
			public boolean isVisible() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return ArcadeMonopoly.this.controllable && ArcadeMonopoly.this.endable;
			}

			@Override
			public void onClick() {
				ArcadeMonopoly.this.rolled = false;
				ArcadeMonopoly.this.controllable = false;
				ArcadeMonopoly.this.nextPiece();
				ArcadeMonopoly.this.endable = false;
				ArcadeMonopoly.this.openedCard = false;
				if (ArcadeMonopoly.this.useAI()) {
					ArcadeMonopoly.this.roll.onClick();
				}
			}
		});
		this.buttons.add(this.purchase = new Button() {
			@Override
			public String getName() {
				return "Purchase";
			}

			@Override
			public boolean isVisible() {
				return ArcadeMonopoly.this.controllable && ArcadeMonopoly.this.places[ArcadeMonopoly.this.getCurrentPiece().getPosition()] instanceof Property && !((Property) ArcadeMonopoly.this.places[ArcadeMonopoly.this.getCurrentPiece().getPosition()]).hasOwner();
			}

			@Override
			public boolean isEnabled() {
				final Property property = (Property) ArcadeMonopoly.this.places[ArcadeMonopoly.this.getCurrentPiece().getPosition()];
				return ArcadeMonopoly.this.getCurrentPiece().canAffordProperty(property);
			}

			@Override
			public boolean isVisibleForPlayer() {
				return ArcadeMonopoly.this.getSelectedPlace() == null;
			}

			@Override
			public void onClick() {
				ArcadeMonopoly.this.getCurrentPiece().purchaseProperty((Property) ArcadeMonopoly.this.places[ArcadeMonopoly.this.getCurrentPiece().getPosition()]);
			}
		});
		this.buttons.add(this.rent = new Button() {
			@Override
			public String getName() {
				return "Pay Rent";
			}

			@Override
			public boolean isVisible() {
				if (ArcadeMonopoly.this.controllable && ArcadeMonopoly.this.places[ArcadeMonopoly.this.getCurrentPiece().getPosition()] instanceof Property) {
					final Property property = (Property) ArcadeMonopoly.this.places[ArcadeMonopoly.this.getCurrentPiece().getPosition()];
					return property.hasOwner() && property.getOwner() != ArcadeMonopoly.this.getCurrentPiece() && !property.isMortgaged();
				}
				return false;
			}

			@Override
			public boolean isEnabled() {
				final Property property = (Property) ArcadeMonopoly.this.places[ArcadeMonopoly.this.getCurrentPiece().getPosition()];
				return !ArcadeMonopoly.this.endable && ArcadeMonopoly.this.getCurrentPiece().canAffordRent(property);
			}

			@Override
			public boolean isVisibleForPlayer() {
				return ArcadeMonopoly.this.getSelectedPlace() == null;
			}

			@Override
			public void onClick() {
				ArcadeMonopoly.this.getCurrentPiece().payPropertyRent((Property) ArcadeMonopoly.this.places[ArcadeMonopoly.this.getCurrentPiece().getPosition()]);
				ArcadeMonopoly.this.endable = true;
			}
		});
		this.buttons.add(this.bankrupt = new Button() {
			@Override
			public String getName() {
				return "Go Bankrupt";
			}

			@Override
			public boolean isVisible() {
				return !ArcadeMonopoly.this.endable && ArcadeMonopoly.this.rent.isVisible() && !ArcadeMonopoly.this.rent.isEnabled();
			}

			@Override
			public boolean isEnabled() {
				return true;
			}

			@Override
			public boolean isVisibleForPlayer() {
				return ArcadeMonopoly.this.getSelectedPlace() == null;
			}

			@Override
			public void onClick() {
				ArcadeMonopoly.this.getCurrentPiece().bankrupt(((Property) ArcadeMonopoly.this.places[ArcadeMonopoly.this.getCurrentPiece().getPosition()]).getOwner());
				ArcadeMonopoly.this.endable = true;
			}
		});
		this.buttons.add(this.bed = new Button() {
			@Override
			public String getName() {
				return "Buy Bed";
			}

			@Override
			public boolean isVisible() {
				return ArcadeMonopoly.this.getSelectedPlace() != null && ArcadeMonopoly.this.getSelectedPlace() instanceof Street;
			}

			@Override
			public boolean isEnabled() {
				final Street street = (Street) ArcadeMonopoly.this.getSelectedPlace();
				return ArcadeMonopoly.this.controllable && street.ownsAllInGroup(ArcadeMonopoly.this.getCurrentPiece()) && street.getStructureCount() < 5 && ArcadeMonopoly.this.getCurrentPiece().canAffordStructure(street) && !street.isMortgaged();
			}

			@Override
			public void onClick() {
				ArcadeMonopoly.this.getCurrentPiece().buyStructure((Street) ArcadeMonopoly.this.getSelectedPlace());
			}
		});
		this.buttons.add(this.card = new Button() {
			@Override
			public String getName() {
				return "Pick a Card";
			}

			@Override
			public boolean isVisible() {
				return ArcadeMonopoly.this.controllable && ArcadeMonopoly.this.places[ArcadeMonopoly.this.getCurrentPiece().getPosition()] instanceof CardPlace && (!ArcadeMonopoly.this.openedCard || ArcadeMonopoly.this.currentCard != null);
			}

			@Override
			public boolean isEnabled() {
				return !ArcadeMonopoly.this.openedCard;
			}

			@Override
			public boolean isVisibleForPlayer() {
				return ArcadeMonopoly.this.getSelectedPlace() == null;
			}

			@Override
			public void onClick() {
				ArcadeMonopoly.this.openCard(((CardPlace) ArcadeMonopoly.this.places[ArcadeMonopoly.this.getCurrentPiece().getPosition()]).getCard());
			}
		});
		this.buttons.add(this.jail = new Button() {
			@Override
			public String getName() {
				return "Pay for /tpx";
			}

			@Override
			public boolean isVisible() {
				return ArcadeMonopoly.this.controllable && ArcadeMonopoly.this.getCurrentPiece().isInJail();
			}

			@Override
			public boolean isEnabled() {
				return ArcadeMonopoly.this.getCurrentPiece().canAffordFine();
			}

			@Override
			public boolean isVisibleForPlayer() {
				return ArcadeMonopoly.this.getSelectedPlace() == null;
			}

			@Override
			public void onClick() {
				ArcadeMonopoly.this.getCurrentPiece().payFine();
				ArcadeMonopoly.this.endable = true;
			}
		});
		this.buttons.add(this.mortgage = new Button() {
			@Override
			public String getName() {
				return "Mortgage";
			}

			@Override
			public boolean isVisible() {
				return ArcadeMonopoly.this.controllable && ArcadeMonopoly.this.getSelectedPlace() != null && ArcadeMonopoly.this.getSelectedPlace() instanceof Property && ((Property) ArcadeMonopoly.this.getSelectedPlace()).getOwner() == ArcadeMonopoly.this.getCurrentPiece() && !((Property) ArcadeMonopoly.this.getSelectedPlace()).isMortgaged();
			}

			@Override
			public boolean isEnabled() {
				return ((Property) ArcadeMonopoly.this.getSelectedPlace()).canMortgage();
			}

			@Override
			public void onClick() {
				ArcadeMonopoly.this.getCurrentPiece().getMoneyFromMortgage((Property) ArcadeMonopoly.this.getSelectedPlace());
			}
		});
		this.buttons.add(this.unmortgage = new Button() {
			@Override
			public String getName() {
				return "Unmortage";
			}

			@Override
			public boolean isVisible() {
				return ArcadeMonopoly.this.controllable && ArcadeMonopoly.this.getSelectedPlace() != null && ArcadeMonopoly.this.getSelectedPlace() instanceof Property && ((Property) ArcadeMonopoly.this.getSelectedPlace()).getOwner() == ArcadeMonopoly.this.getCurrentPiece() && ((Property) ArcadeMonopoly.this.getSelectedPlace()).isMortgaged();
			}

			@Override
			public boolean isEnabled() {
				return ArcadeMonopoly.this.getCurrentPiece().canAffordUnMortgage((Property) ArcadeMonopoly.this.getSelectedPlace());
			}

			@Override
			public void onClick() {
				ArcadeMonopoly.this.getCurrentPiece().payUnMortgage((Property) ArcadeMonopoly.this.getSelectedPlace());
			}
		});
		this.buttons.add(this.sellbed = new Button() {
			@Override
			public String getName() {
				return "Sell Bed";
			}

			@Override
			public boolean isVisible() {
				return ArcadeMonopoly.this.getSelectedPlace() != null && ArcadeMonopoly.this.getSelectedPlace() instanceof Street;
			}

			@Override
			public boolean isEnabled() {
				final Street street = (Street) ArcadeMonopoly.this.getSelectedPlace();
				return ArcadeMonopoly.this.controllable && street.getStructureCount() > 0;
			}

			@Override
			public void onClick() {
				ArcadeMonopoly.this.getCurrentPiece().sellStructure((Street) ArcadeMonopoly.this.getSelectedPlace());
			}
		});
		if (this.getCurrentPiece().getController() == Piece.CONTROLLED_BY.COMPUTER) {
			this.roll.onClick();
		}
	}

	private boolean useAI() {
		return this.getCurrentPiece().getController() == Piece.CONTROLLED_BY.COMPUTER;
	}

	private void nextPiece() {
		this.currentPiece = (this.currentPiece + 1) % this.pieces.size();
		if (this.getCurrentPiece().isBankrupt()) {
			this.nextPiece();
		}
	}

	private void throwDice() {
		if (this.diceCount == 0) {
			if (this.diceTimer == 0) {
				this.diceTimer = 20;
			}
			this.die.randomize();
			this.die2.randomize();
		}
	}

	public void movePiece(final int steps) {
		this.diceCount = steps;
	}

	public int getTotalDieEyes() {
		return this.die.getNumber() + this.die2.getNumber();
	}

	public boolean hasDoubleDice() {
		return this.die.getNumber() == this.die2.getNumber();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void update() {
		super.update();
		if (this.diceDelay == 0) {
			if (this.diceTimer > 0) {
				this.throwDice();
				if (--this.diceTimer == 0) {
					if (this.getCurrentPiece().isInJail()) {
						this.controllable = true;
						if (this.hasDoubleDice()) {
							this.getCurrentPiece().releaseFromJail();
							this.endable = true;
							if (this.useAI()) {
								this.end.onClick();
							}
						} else {
							this.getCurrentPiece().increaseTurnsInJail();
							if (this.getCurrentPiece().getTurnsInJail() < 3) {
								this.endable = true;
								if (this.useAI()) {
									this.end.onClick();
								}
							} else if (this.useAI()) {
								if (this.jail.isVisible() && this.jail.isEnabled()) {
									this.jail.onClick();
								} else {
									this.bankrupt.onClick();
								}
								this.end.onClick();
							}
						}
					} else {
						this.movePiece(this.getTotalDieEyes());
					}
				}
			} else if (this.diceCount != 0) {
				if (this.diceCount > 0) {
					this.getCurrentPiece().move(1);
					this.places[this.getCurrentPiece().getPosition()].onPiecePass(this.getCurrentPiece());
					--this.diceCount;
				} else {
					this.getCurrentPiece().move(-1);
					++this.diceCount;
				}
				if (this.diceCount == 0) {
					if (this.places[this.getCurrentPiece().getPosition()].onPieceStop(this.getCurrentPiece())) {
						this.endable = true;
					}
					this.controllable = true;
					if (this.useAI()) {
						if (this.purchase.isVisible() && this.purchase.isEnabled()) {
							this.purchase.onClick();
						} else if (this.card.isVisible() && this.card.isEnabled()) {
							this.card.onClick();
						} else if (this.rent.isVisible()) {
							if (this.rent.isEnabled()) {
								this.rent.onClick();
							} else {
								this.bankrupt.onClick();
							}
						}
						if (this.end.isVisible() && this.end.isEnabled()) {
							this.end.onClick();
						}
					}
				}
			}
			this.diceDelay = 3;
		} else {
			--this.diceDelay;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		this.loadTexture(gui, 1);
		this.die.draw(gui, 20, 20);
		this.die2.draw(gui, 50, 20);
		final float smallgridX = x / 0.17f - 686.94116f;
		final float smallgridY = y / 0.17f - 30.117645f;
		boolean foundHover = false;
		if (this.selectedPlace != -1) {
			this.drawPropertyOnBoardWithPositionRotationAndScale(gui, this.places[this.selectedPlace], this.selectedPlace, true, false, (int) ((590.6666666666666 - (
					(this.getId(this.selectedPlace) == 0) ? 122 : 76)) / 2.0), 51, 0, 0.75f);
		}
		for (int i = 0; i < this.places.length; ++i) {
			if (!foundHover && this.getModule().inRect((int) smallgridX, (int) smallgridY, this.getSmallgridPlaceArea(i))) {
				if (this.selectedPlace == -1) {
					this.drawPropertyOnBoardWithPositionRotationAndScale(gui, this.places[i], i, true, false, (int) ((590.6666666666666 - ((this.getId(i) == 0) ? 122 : 76)) / 2.0), 51, 0, 0.75f);
				}
				foundHover = true;
				this.drawPropertyOnBoard(gui, this.places[i], i, this.getSide(i), this.getId(i), true);
			} else {
				this.drawPropertyOnBoard(gui, this.places[i], i, this.getSide(i), this.getId(i), false);
			}
		}
		for (int i = 0; i < this.pieces.size(); ++i) {
			final Piece piece = this.pieces.get(i);
			this.loadTexture(gui, 1);
			final int[] menu = piece.getMenuRect(i);
			this.getModule().drawImage(gui, menu, 0, 122);
			for (int j = 0; j < 3; ++j) {
				int v = 0;
				switch (j) {
					case 0: {
						v = ((piece.getController() == Piece.CONTROLLED_BY.PLAYER) ? 0 : ((piece.getController() == Piece.CONTROLLED_BY.COMPUTER) ? 1 : 2));
						break;
					}
					case 1: {
						v = (this.pieces.get(i).isBankrupt() ? 4 : ((this.currentPiece == i) ? ((this.diceCount == 0) ? ((this.diceTimer > 0) ? 3 : 2) : 1) : 0));
						break;
					}
					case 2: {
						v = ((this.getSelectedPlace() != null && this.getSelectedPlace() instanceof Property && ((Property) this.getSelectedPlace()).getOwner() == this.pieces.get(i)) ? (
								((Property) this.getSelectedPlace()).isMortgaged() ? 2 : 1) : 0);
						break;
					}
				}
				this.getModule().drawImage(gui, menu[0] + 3, menu[1] + 3 + j * 9, j * 12, 152 + 6 * v, 12, 6);
			}
			final int[] player = piece.getPlayerMenuRect(i);
			this.getModule().drawImage(gui, player, 232, 24 * piece.getV());
			Note.drawPlayerValue(this, gui, menu[0] + 50, menu[1] + 2, piece.getNoteCount());
			for (int k = piece.getAnimationNotes().size() - 1; k >= 0; --k) {
				final NoteAnimation animation = piece.getAnimationNotes().get(k);
				int animX = menu[0] + 50 + (6 - animation.getNote().getId()) * 20;
				if (animX + 16 > 443) {
					animX = player[0] + (player[2] - 16) / 2;
				}
				if (animation.draw(this, gui, animX, menu[1] + 2)) {
					piece.removeNewNoteAnimation(k);
				}
			}
			piece.updateExtending(this.getModule().inRect(x, y, menu));
		}
		this.loadTexture(gui, 1);
		int id = 0;
		for (final Button button : this.buttons) {
			if (button.isReallyVisible(this)) {
				final int[] rect = this.getButtonRect(id++);
				int v = 0;
				if (!button.isReallyEnabled(this)) {
					v = 1;
				} else if (this.getModule().inRect(x, y, rect)) {
					v = 2;
				}
				this.getModule().drawImage(gui, rect, 152, v * 18);
			}
		}
		if (this.getSelectedPlace() != null) {
			if (this.getSelectedPlace() instanceof Street) {
				final Street street = (Street) this.getSelectedPlace();
				this.getModule().drawImage(gui, 32, 185, 76, 22, 16, 16);
				if (street.getOwner() != null && !street.isMortgaged()) {
					if (street.getStructureCount() == 0) {
						this.getModule().drawImage(gui, 7, street.ownsAllInGroup(street.getOwner()) ? 241 : 226, 124, 22, 5, 10);
					} else {
						this.getModule().drawImage(gui, 323, 172 + (street.getStructureCount() - 1) * 17, 124, 22, 5, 10);
					}
				}
				for (int l = 1; l <= 5; ++l) {
					this.drawStreetRent(gui, street, l);
				}
				Note.drawValue(this, gui, 62, 170, 3, street.getMortgageValue());
				Note.drawValue(this, gui, 62, 185, 3, street.getStructureCost());
				Note.drawValue(this, gui, 62, 222, 3, street.getRentCost(false));
				Note.drawValue(this, gui, 62, 237, 3, street.getRentCost(true));
			} else if (this.getSelectedPlace() instanceof Station) {
				final Station station = (Station) this.getSelectedPlace();
				if (station.getOwner() != null && !station.isMortgaged()) {
					this.getModule().drawImage(gui, 323, 184 + (station.getOwnedInGroup() - 1) * 17, 124, 22, 5, 10);
				}
				Note.drawValue(this, gui, 62, 170, 3, station.getMortgageValue());
				for (int l = 1; l <= 4; ++l) {
					this.drawStationRent(gui, station, l);
				}
			} else if (this.getSelectedPlace() instanceof Utility) {
				final Utility utility = (Utility) this.getSelectedPlace();
				if (utility.getOwner() != null && !utility.isMortgaged()) {
					this.getModule().drawImage(gui, 323, 184 + (utility.getOwnedInGroup() - 1) * 17, 124, 22, 5, 10);
				}
				Note.drawValue(this, gui, 62, 170, 3, utility.getMortgageValue());
				for (int l = 1; l <= 3; ++l) {
					this.drawUtilityRent(gui, utility, l);
				}
			}
		}
		if (this.currentCard != null) {
			this.cardScale = Math.min(this.cardScale + 0.02f, 1.0f);
			this.cardRotation = Math.max(0, this.cardRotation - 6);
			this.drawCard(gui, true);
			this.drawCard(gui, false);
			if (this.cardScale == 1.0f && this.useAI()) {
				this.removeCard();
			}
		}
	}

	private void openCard(final Card card) {
		this.openedCard = true;
		this.currentCard = card;
		this.cardScale = 0.0f;
		this.cardRotation = 540;
	}

	private void drawCard(final GuiMinecart gui, final boolean isFront) {
		GL11.glPushMatrix();
		final int x = 150;
		final int y = 44;
		final float s = this.cardScale;
		final float posX = gui.getGuiLeft() + 71;
		final float posY = gui.getGuiTop() + 40;
		GL11.glTranslatef(0.0f, 0.0f, 100.0f);
		GL11.glTranslatef(posX + x, posY + y, 0.0f);
		GL11.glScalef(s, s, 1.0f);
		GL11.glRotatef(this.cardRotation + (isFront ? 0 : 180), 0.0f, 1.0f, 0.0f);
		GL11.glTranslatef(-posX, -posY, 0.0f);
		this.loadTexture(gui, 0);
		final int[] rect = { 0, 0, 142, 80 };
		this.currentCard.render(this, gui, rect, isFront);
		GL11.glPopMatrix();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawForeground(final GuiMinecart gui) {
		int id = 0;
		for (final Button button : this.buttons) {
			if (button.isReallyVisible(this)) {
				this.getModule().drawString(gui, button.getName(), this.getButtonRect(id++), 4210752);
			}
		}
		if (this.getSelectedPlace() != null) {
			if (this.getSelectedPlace() instanceof Street) {
				this.getModule().drawString(gui, "Mortgage", 10, 175, 4210752);
				this.getModule().drawString(gui, "Buy", 10, 190, 4210752);
				this.getModule().drawString(gui, "Rents", 10, 215, 4210752);
				this.getModule().drawString(gui, "Normal", 14, 227, 4210752);
				this.getModule().drawString(gui, "Group", 14, 242, 4210752);
			} else if (this.getSelectedPlace() instanceof Station) {
				this.getModule().drawString(gui, "Mortgage", 10, 175, 4210752);
				this.getModule().drawString(gui, "Rents", 330, 170, 4210752);
			} else if (this.getSelectedPlace() instanceof Utility) {
				this.getModule().drawString(gui, "Mortgage", 10, 175, 4210752);
				this.getModule().drawSplitString(gui, "The rent depends on the eye count of the dice, if you own one Utility it's " + Utility.getMultiplier(1) + "x the eye count, if you own two it's " + Utility.getMultiplier(2) + "x and if you own them all it's " + Utility.getMultiplier(3) + "x.", 10, 195, 145, 4210752);
				this.getModule().drawString(gui, "Rents", 330, 170, 4210752);
			}
		}
	}

	private void drawStreetRent(final GuiMinecart gui, final Street street, final int structures) {
		this.loadTexture(gui, 1);
		int graphicalStructures = structures;
		int u = 0;
		if (graphicalStructures == 5) {
			graphicalStructures = 1;
			u = 1;
		}
		final int yPos = 169 + (structures - 1) * 17;
		for (int i = 0; i < graphicalStructures; ++i) {
			this.getModule().drawImage(gui, 330 + i * 6, yPos, 76 + u * 16, 22, 16, 16);
		}
		Note.drawValue(this, gui, 370, yPos, 3, street.getRentCost(structures));
	}

	private void drawStationRent(final GuiMinecart gui, final Station station, final int ownedStations) {
		this.loadTexture(gui, 1);
		final int yPos = 181 + (ownedStations - 1) * 17;
		for (int i = 0; i < ownedStations; ++i) {
			this.getModule().drawImage(gui, 330 + i * 16, yPos, 76 + i * 16, 70, 16, 16);
		}
		Note.drawValue(this, gui, 410, yPos, 2, station.getRentCost(ownedStations));
	}

	private void drawUtilityRent(final GuiMinecart gui, final Utility utility, final int utils) {
		this.loadTexture(gui, 1);
		final int yPos = 181 + (utils - 1) * 17;
		for (int i = 0; i < utils; ++i) {
			this.getModule().drawImage(gui, 330 + i * 16, yPos, 76 + i * 16, 86, 16, 16);
		}
		Note.drawValue(this, gui, 400, yPos, 2, utility.getRentCost(utils));
	}

	private int[] getButtonRect(final int i) {
		return new int[] { 10, 50 + i * 22, 80, 18 };
	}

	private int getSide(final int i) {
		if (i < 14) {
			return 0;
		}
		if (i < 24) {
			return 1;
		}
		if (i < 38) {
			return 2;
		}
		return 3;
	}

	private int getId(final int i) {
		if (i < 14) {
			return i;
		}
		if (i < 24) {
			return i - 14;
		}
		if (i < 38) {
			return i - 24;
		}
		return i - 38;
	}

	private int[] getSmallgridPlaceArea(final int id) {
		final int side = this.getSide(id);
		int i = this.getId(id);
		if (i == 0) {
			switch (side) {
				case 0: {
					return new int[] { 1110, 806, 122, 122 };
				}
				case 1: {
					return new int[] { 0, 806, 122, 122 };
				}
				case 2: {
					return new int[] { 0, 0, 122, 122 };
				}
				default: {
					return new int[] { 1110, 0, 122, 122 };
				}
			}
		} else {
			--i;
			switch (side) {
				case 0: {
					return new int[] { 122 + (13 - i) * 76 - 76, 806, 76, 122 };
				}
				case 1: {
					return new int[] { 0, 122 + (9 - i) * 76 - 76, 122, 76 };
				}
				case 2: {
					return new int[] { 122 + i * 76, 0, 76, 122 };
				}
				default: {
					return new int[] { 1110, 122 + i * 76, 122, 76 };
				}
			}
		}
	}

	private void drawPropertyOnBoard(final GuiMinecart gui, final Place place, final int id, final int side, int i, final boolean hover) {
		int offX = 0;
		int offY = 0;
		int rotation = 0;
		if (i == 0) {
			switch (side) {
				case 0: {
					offX = 1110;
					offY = 806;
					rotation = 0;
					break;
				}
				case 1: {
					offX = 122;
					offY = 806;
					rotation = 90;
					break;
				}
				case 2: {
					offX = 122;
					offY = 122;
					rotation = 180;
					break;
				}
				default: {
					offX = 1110;
					offY = 122;
					rotation = 270;
					break;
				}
			}
		} else {
			--i;
			switch (side) {
				case 0: {
					offX = 122 + (13 - i) * 76 - 76;
					offY = 806;
					rotation = 0;
					break;
				}
				case 1: {
					offX = 122;
					offY = 122 + (9 - i) * 76 - 76;
					rotation = 90;
					break;
				}
				case 2: {
					offX = 122 + i * 76 + 76;
					offY = 122;
					rotation = 180;
					break;
				}
				default: {
					offX = 1110;
					offY = 122 + i * 76 + 76;
					rotation = 270;
					break;
				}
			}
		}
		offX += 686;
		offY += 30;
		this.drawPropertyOnBoardWithPositionRotationAndScale(gui, place, id, false, hover, offX, offY, rotation, 0.17f);
	}

	private void drawPropertyOnBoardWithPositionRotationAndScale(final GuiMinecart gui,
			final Place place,
			final int id,
			final boolean zoom,
			final boolean hover,
			final int x,
			final int y,
			final int r,
			final float s) {
		GL11.glPushMatrix();
		final EnumSet<Place.PLACE_STATE> states = EnumSet.noneOf(Place.PLACE_STATE.class);
		if (zoom) {
			states.add(Place.PLACE_STATE.ZOOMED);
		} else if (hover) {
			states.add(Place.PLACE_STATE.HOVER);
		}
		if (this.selectedPlace == id) {
			states.add(Place.PLACE_STATE.SELECTED);
		}
		if (place instanceof Property) {
			final Property property = (Property) place;
			if (property.hasOwner() && property.getOwner().showProperties()) {
				states.add(Place.PLACE_STATE.MARKED);
			}
		}
		final float posX = gui.getGuiLeft();
		final float posY = gui.getGuiTop();
		GL11.glTranslatef(posX + x * s, posY + y * s, 0.0f);
		GL11.glScalef(s, s, 1.0f);
		GL11.glRotatef(r, 0.0f, 0.0f, 1.0f);
		GL11.glTranslatef(-posX, -posY, 0.0f);
		place.draw(gui, states);
		final int[] total = new int[place.getPieceAreaCount()];
		for (int i = 0; i < this.pieces.size(); ++i) {
			if (!this.pieces.get(i).isBankrupt() && this.pieces.get(i).getPosition() == id) {
				final int[] array = total;
				final int pieceAreaForPiece = place.getPieceAreaForPiece(this.pieces.get(i));
				++array[pieceAreaForPiece];
			}
		}
		final int[] pos = new int[place.getPieceAreaCount()];
		for (int j = 0; j < this.pieces.size(); ++j) {
			if (!this.pieces.get(j).isBankrupt() && this.pieces.get(j).getPosition() == id) {
				this.loadTexture(gui, 1);
				final int area = place.getPieceAreaForPiece(this.pieces.get(j));
				place.drawPiece(gui, this.pieces.get(j), total[area], pos[area]++, area, states);
			}
		}
		place.drawText(gui, states);
		GL11.glPopMatrix();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int b) {
		final float smallgridX = x / 0.17f - 686.94116f;
		final float smallgridY = y / 0.17f - 30.117645f;
		int i = 0;
		while (i < this.places.length) {
			if (this.getModule().inRect((int) smallgridX, (int) smallgridY, this.getSmallgridPlaceArea(i))) {
				if (this.places[i] instanceof Property) {
					if (i == this.selectedPlace) {
						this.selectedPlace = -1;
					} else {
						this.selectedPlace = i;
					}
					return;
				}
				break;
			} else {
				++i;
			}
		}
		int id = 0;
		for (final Button button : this.buttons) {
			if (button.isReallyVisible(this) && this.getModule().inRect(x, y, this.getButtonRect(id++))) {
				if (button.isReallyEnabled(this)) {
					button.onClick();
				}
				return;
			}
		}
		if (this.currentCard != null && this.cardScale == 1.0f) {
			final int[] rect = { 150, 44, 142, 80 };
			if (this.getModule().inRect(x, y, rect)) {
				this.removeCard();
			}
		}
		this.selectedPlace = -1;
	}

	public void loadTexture(final GuiMinecart gui, final int number) {
		ResourceHelper.bindResource(ArcadeMonopoly.textures[number]);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public Place[] getPlaces() {
		return this.places;
	}

	private void removeCard() {
		this.currentCard.doStuff(this, this.getCurrentPiece());
		this.currentCard = null;
		this.endable = true;
		if (this.diceCount == 0 && this.useAI()) {
			this.end.onClick();
		}
	}

	static {
		ArcadeMonopoly.textures = new String[5];
		for (int i = 0; i < ArcadeMonopoly.textures.length; ++i) {
			ArcadeMonopoly.textures[i] = "/gui/monopoly_" + i + ".png";
		}
	}
}
