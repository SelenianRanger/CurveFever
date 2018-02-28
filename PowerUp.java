import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;

public abstract class PowerUp {

	static final BufferedImage SPEED_12 = Utils.createImage("/images/speed_12.png");
	static final BufferedImage SPEED_11 = Utils.createImage("/images/speed_11.png");
	static final BufferedImage SPEED_22 = Utils.createImage("/images/speed_22.png");
	static final BufferedImage SPEED_21 = Utils.createImage("/images/speed_21.png");

	static final BufferedImage SIZE_2 = Utils.createImage("/images/size_2.png");
	static final BufferedImage SIZE_1 = Utils.createImage("/images/size_1.png");

	static final BufferedImage BORDER_1 = Utils.createImage("/images/border_1.png");
	static final BufferedImage BORDER_2 = Utils.createImage("/images/border_2.png");

	static final BufferedImage GHOST = Utils.createImage("/images/ghost.png");

	static final BufferedImage CLEAR = Utils.createImage("/images/clear.png");

	static final BufferedImage REVERSE = Utils.createImage("/images/reverse.png");

	static final BufferedImage CHANCE = Utils.createImage("/images/chance.png");

	static final BufferedImage FINGER = Utils.createImage("/images/finger.png");

	static final BufferedImage HOLE = Utils.createImage("/images/hole.png");

	static final BufferedImage MOVE = Utils.createImage("/images/move.png");

	static final BufferedImage TURN_2 = Utils.createImage("/images/turn_2.png");
	static final BufferedImage TURN_1 = Utils.createImage("/images/turn_1.png");

	int x;
	int y;

	private Point pos;
	private Vector v;

	private int movingCounter;

	protected BufferedImage img;

	public PowerUp(int x, int y) {

		pos = new Point(x, y);

		this.x = (int) pos.getX();
		this.y = (int) pos.getY();

		movingCounter = 0;

	}

	public abstract void activate(GameScreen screen, Player[] players, Player currentPlayer);

	public void update() {
		if (v != null) {
			pos.translate(v);
		}

		double x = pos.getX();
		double y = pos.getY();

		if (x < 0 || x > 765) {
			if (x < 0) {
				x = 765;
			} else {
				x = 0;
			}
		}
		if (y < 0 || y > 765) {
			if (y < 0) {
				y = 765;
			} else {
				y = 0;
			}
		}

		pos.setX(x);
		pos.setY(y);
		this.x = (int) pos.getX();
		this.y = (int) pos.getY();
	}

	public void setMoving(boolean val) {
		movingCounter += val ? 1 : (isMoving()) ? -1 : 0;
	}

	public boolean isMoving() {
		return movingCounter != 0;
	}

	public void setSpeedVector(Vector v) {
		this.v = v;
	}

	public BufferedImage getImage() {
		return img;
	}

}

class SpeedPowerUp extends PowerUp {

	private boolean speedUp;
	private boolean forOthers;

	public SpeedPowerUp(int x, int y, boolean speedUp, boolean forOthers) {

		super(x, y);

		this.speedUp = speedUp;
		this.forOthers = forOthers;

		if (speedUp) {
			if (forOthers) {
				img = PowerUp.SPEED_12;
			} else {
				img = PowerUp.SPEED_11;
			}
		} else {
			if (forOthers) {
				img = PowerUp.SPEED_22;
			} else {
				img = PowerUp.SPEED_21;
			}
		}

	}

	public void activate(GameScreen screen, Player[] players, Player currentPlayer) {

		if (speedUp) {
			if (forOthers) {
				for (Player p : players) {
					if (p != currentPlayer) {
						p.speedUp();
						Timer timer = new Timer(10, new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								if (p.getV().getValue() != 1.2) {
									p.slowDown();
								}
							}
						});
						timer.setRepeats(false);
						timer.setInitialDelay(3000);
						timer.start();
					}
				}
			} else {
				currentPlayer.speedUp();
				Timer timer = new Timer(10, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (currentPlayer.getV().getValue() != 1.2) {
							currentPlayer.slowDown();
						}
					}
				});
				timer.setRepeats(false);
				timer.setInitialDelay(3000);
				timer.start();
			}
		} else {
			if (forOthers) {
				for (Player p : players) {
					if (p != currentPlayer) {
						p.slowDown();
						Timer timer = new Timer(10, new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								if (p.getV().getValue() != 1.2) {
									p.speedUp();
								}
							}
						});
						timer.setRepeats(false);
						timer.setInitialDelay(5000);
						timer.start();
					}
				}
			} else {
				currentPlayer.slowDown();
				Timer timer = new Timer(10, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (currentPlayer.getV().getValue() != 1.2) {
							currentPlayer.speedUp();
						}
					}
				});
				timer.setRepeats(false);
				timer.setInitialDelay(10000);
				timer.start();
			}
		}

	}

}

class SizePowerUp extends PowerUp {

	private boolean bigger;

	public SizePowerUp(int x, int y, boolean bigger) {

		super(x, y);

		this.bigger = bigger;

		if (bigger) {
			img = PowerUp.SIZE_2;
		} else {
			img = PowerUp.SIZE_1;
		}

	}

	public void activate(GameScreen screen, Player[] players, Player currentPlayer) {

		if (bigger) {
			for (Player p : players) {
				if (p != currentPlayer) {
					p.setSize(1);
					Timer timer = new Timer(10, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							p.setSize(0);
						}
					});
					timer.setRepeats(false);
					timer.setInitialDelay(7000);
					timer.start();
				}
			}
		} else {
			currentPlayer.setSize(-1);
			Timer timer = new Timer(10, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					currentPlayer.setSize(0);
				}
			});
			timer.setRepeats(false);
			timer.setInitialDelay(15000);
			timer.start();
		}

	}

}

class BorderPowerUp extends PowerUp {

	private boolean forAll;

	public BorderPowerUp(int x, int y, boolean forAll) {

		super(x, y);

		this.forAll = forAll;

		if (forAll) {
			img = PowerUp.BORDER_2;
		} else {
			img = PowerUp.BORDER_1;
		}

	}

	public void activate(GameScreen screen, Player[] players, Player currentPlayer) {

		if (forAll) {
			for (Player p : players) {
				p.setPassThroughBorder(true);
				screen.setPassThroughBorder(true);
				Timer timer = new Timer(10, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						p.setPassThroughBorder(false);
						screen.setPassThroughBorder(false);
					}
				});
				timer.setRepeats(false);
				timer.setInitialDelay(10000);
				timer.start();
			}
		} else {
			currentPlayer.setPassThroughBorder(true);
			currentPlayer.setHeadFlash(true);
			Timer timer = new Timer(10, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					currentPlayer.setPassThroughBorder(false);
					currentPlayer.setHeadFlash(false);
				}
			});
			timer.setRepeats(false);
			timer.setInitialDelay(15000);
			timer.start();
		}

	}

}

class GhostPowerUp extends PowerUp {

	public GhostPowerUp(int x, int y) {
		super(x, y);
		img = PowerUp.GHOST;
	}

	public void activate(GameScreen screen, Player[] players, Player currentPlayer) {

		currentPlayer.setGhost(true);
		Timer timer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentPlayer.setGhost(false);
			}
		});
		timer.setRepeats(false);
		timer.setInitialDelay(6000);
		timer.start();

	}

}

class ClearScreenPowerUp extends PowerUp {

	public ClearScreenPowerUp(int x, int y) {
		super(x, y);
		img = PowerUp.CLEAR;
	}

	public void activate(GameScreen screen, Player[] players, Player currentPlayer) {
		screen.clear();
	}

}

class ReversePowerUp extends PowerUp {

	public ReversePowerUp(int x, int y) {
		super(x, y);
		img = PowerUp.REVERSE;
	}

	public void activate(GameScreen screen, Player[] players, Player currentPlayer) {
		for (Player p : players) {
			if (p != currentPlayer) {
				p.setReverse(true);
				Timer timer = new Timer(10, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						p.setReverse(false);
					}
				});
				timer.setRepeats(false);
				timer.setInitialDelay(5000);
				timer.start();
			}
		}
	}

}

class ChancePowerUp extends PowerUp {

	public ChancePowerUp(int x, int y) {
		super(x, y);
		img = CHANCE;
	}

	public void activate(GameScreen screen, Player[] players, Player currentPlayer) {
		screen.setPowerUpSpawnChance(0.9);
		Timer timer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				screen.setPowerUpSpawnChance(0.3);
			}
		});
		timer.setRepeats(false);
		timer.setInitialDelay(5000);
		timer.start();
	}

}

class FingerPowerUp extends PowerUp {

	public FingerPowerUp(int x, int y) {
		super(x, y);
		img = FINGER;
	}

	public void activate(GameScreen screen, Player[] players, Player currentPlayer) {
		for (Player p : players) {
			if (p != currentPlayer) {
				p.setKeyLocked(true);
				Timer timer = new Timer(10, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						p.setKeyLocked(false);
						p.lockedKey = 0;
					}
				});
				timer.setRepeats(false);
				timer.setInitialDelay(3000);
				timer.start();
			}
		}
	}

}

class HolePowerUp extends PowerUp {

	public HolePowerUp(int x, int y) {
		super(x, y);
		img = HOLE;
	}

	public void activate(GameScreen screen, Player[] players, Player currentPlayer) {
		for (Player p : players) {
			if (p != currentPlayer) {
				p.setGapChance(0.08);
			}
			Timer timer = new Timer(10, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					p.setGapChance(0.004);
				}
			});
			timer.setRepeats(false);
			timer.setInitialDelay(7000);
			timer.start();
		}
	}

}

class MovePowerUp extends PowerUp {

	public MovePowerUp(int x, int y) {
		super(x, y);
		img = MOVE;
	}

	public void activate(GameScreen screen, Player[] players, Player currentPlayer) {
		Random rnd = new Random();
		ArrayList<PowerUp> powerUps = screen.getPowerUps();
		for (PowerUp p : powerUps) {
			Vector v = new Vector(3, rnd.nextInt(360));
			p.setSpeedVector(v);
			p.setMoving(true);
			Timer timer = new Timer(10, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					p.setMoving(false);
				}
			});
			timer.setRepeats(false);
			timer.setInitialDelay(10000);
			timer.start();
		}
	}

}

class TurnPowerUp extends PowerUp {

	private boolean wider;

	public TurnPowerUp(int x, int y, boolean wider) {

		super(x, y);

		this.wider = wider;

		if (wider) {
			img = TURN_1;
		} else {
			img = TURN_2;
		}
	}

	public void activate(GameScreen screen, Player[] players, Player currentPlayer) {
		if (wider) {
			for (Player p : players) {
				if (p != currentPlayer) {
					p.setTurnDegree(1.4);
					Timer timer = new Timer(10, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							p.setTurnDegree(1.9);
						}
					});
					timer.setRepeats(false);
					timer.setInitialDelay(7000);
					timer.start();
				}
			}
		} else {
			currentPlayer.setTurnDegree(3.2);
			Timer timer = new Timer(10, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					currentPlayer.setTurnDegree(1.9);
				}
			});
			timer.setRepeats(false);
			timer.setInitialDelay(15000);
			timer.start();
		}
	}

}