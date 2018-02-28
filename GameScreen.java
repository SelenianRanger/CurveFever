import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.Timer;

public class GameScreen extends JComponent {

	private boolean isDemo;

	private Player[] player;

	private ArrayList<PowerUp> powerUp;

	private int passThroughBorderCounter;
	private double chance;

	private BufferedImage buffer;

	private Graphics2D g2;
	private Graphics2D screen;

	private GameListener listener;

	Timer timer;

	Random rnd;

	public GameScreen(Player[] plyr, boolean demo) {

		rnd = new Random();

		this.isDemo = demo;

		powerUp = new ArrayList<PowerUp>();

		passThroughBorderCounter = 0;
		chance = 0.3;

		timer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});

		if (isDemo) {

			player = new Player[6];

			boolean[] colorChosen = new boolean[6];

			for (int i = 0; i < player.length; i++) {
				Color c = Color.white;
				int code = -1;
				while (code < 0 || colorChosen[code]) {
					code = rnd.nextInt(6);
				}
				colorChosen[code] = true;
				switch (code) {
				case 0:
					c = Color.BLUE;
					break;
				case 1:
					c = Color.RED;
					break;
				case 2:
					c = Color.GREEN;
					break;
				case 3:
					c = Color.LIGHT_GRAY;
					break;
				case 4:
					c = Color.MAGENTA;
					break;
				case 5:
					c = Color.YELLOW;
					break;
				}
				player[i] = new Player(true, null, c, 0, 0);
			}

		} else {

			this.player = plyr;

			addKeyListener(new KeyAdapter() {

				public void keyReleased(KeyEvent e) {

					int keyReleased = e.getKeyCode();

					for (Player p : player) {
						if (!p.isKeyLocked()) {
							if (keyReleased == p.leftKey || keyReleased == p.rightKey) {
								p.setTurningDirection(0);
							}
						} else {
							if (keyReleased == p.lockedKey && (keyReleased == p.leftKey || keyReleased == p.rightKey)) {
								p.setTurningDirection(0);
							}
						}
					}

				}

				public void keyPressed(KeyEvent e) {

					int keyPressed = e.getKeyCode();

					for (Player p : player) {
						if (!p.isKeyLocked()) {
							if (keyPressed == p.leftKey) {
								p.setTurningDirection(-1);
							} else if (keyPressed == p.rightKey) {
								p.setTurningDirection(1);
							}
						} else {

							if (p.lockedKey == 0) {
								if (keyPressed == p.leftKey || keyPressed == p.rightKey) {
									p.lockedKey = keyPressed;
								}
							}

							if (keyPressed == p.lockedKey && keyPressed == p.leftKey) {
								p.setTurningDirection(-1);
							} else if (keyPressed == p.lockedKey && keyPressed == p.rightKey) {
								p.setTurningDirection(1);
							}

						}
					}

				}

			});

			addComponentListener(new ComponentAdapter() {
				public void componentShown(ComponentEvent e) {
					Timer startTimer = new Timer(10, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							timer.stop();
						}
					});
					startTimer.setRepeats(false);
					startTimer.setInitialDelay(50);
					startTimer.start();
					timer.start();
				}
			});
		}

		buffer = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
		g2 = (Graphics2D) buffer.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setColor(Color.black);
		g2.fillRect(0, 0, 800, 800);

	}

	protected void paintComponent(Graphics g) {

		for (Player p : player) {
			if (!p.isGhost()) {
				g2.setColor(p.getBodyColor());
				g2.fill(p);
			}
		}
		g.drawImage(buffer, 0, 0, null);

		screen = (Graphics2D) g;
		screen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		screen.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
		screen.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		screen.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		for (Player p : player) {

			double radius = p.width / 2;
			double frontX = p.getFront(-radius / 3).getX() - radius + 0.15 * radius;
			double frontY = p.getFront(-radius / 3).getY() - radius + 0.15 * radius;
			Ellipse2D.Double head = new Ellipse2D.Double(frontX, frontY, 0.85 * radius * 2, 0.85 * radius * 2);

			if (p.isGhost()) {
				screen.setColor(p.getBodyColor());
				screen.fill(p);
			}

			if (p.isDrawHead()) {
				screen.setColor(p.getHeadColor());
				screen.fill(head);
			}

			// drawHitZone(p);
			// drawBotHitCheck(p);
			// drawBotChoiceCheck(p);

		}

		for (PowerUp power : powerUp) {
			screen.drawImage(power.getImage(), power.x, power.y, 36, 36, null);
		}

	}

	protected void paintBorder(Graphics g) {
		if (!canPassThroughBorder()) {
			g.setColor(Color.yellow.brighter());
			for (int i = 1; i <= 2; i++) {
				g.drawRect(i, i, getWidth() - i * 2 - 1, getHeight() - i * 2 - 1);
			}
		}
	}

	public void update() {

		if (rnd.nextDouble() * 100 < chance) {
			powerUp.add(PowerUpFactory.newPowerUp());
		}

		for (PowerUp p : powerUp) {
			if (p.isMoving()) {
				p.update();
			}
		}

		for (Player p : player) {
			if (p.isAlive()) {
				drawGap(p);
				if (p.isBot()) {
					updateBot(p);
				}
				p.move();
			}
		}
		checkForHits();
		repaint();
	}

	public void drawGap(Player p) {
		if (!p.isGhost()) {
			if (rnd.nextDouble() < p.getGapChance()) {
				p.setGhost(true);
				Timer tmp = new Timer(10, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						p.setGhost(false);
					}
				});
				tmp.setRepeats(false);
				tmp.setInitialDelay((int) (p.width / (8 * p.getV().getValue()) * 300));
				tmp.start();
			}
		}
	}

	public void updateBot(Player p) {

		boolean frontHit = checkFrontHit(p);
		boolean sideHit = checkSideHit(p);
		double choice = compareSides(p);
		double pointDifference = Math.abs(choice);

		// printParameters(p);

		if (frontHit || sideHit) {
			if (!p.isForcedToTurn()) {
				p.setTurningDirection(0);
				if (frontHit) {
					if (pointDifference < 1.2) {

						p.setForcedToTurn(true);
						if (choice < 0) {
							p.setTurningDirection(-1);
						} else if (choice > 0) {
							p.setTurningDirection(1);
						}
						Timer tmp = new Timer(10, new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								p.setForcedToTurn(false);
								p.setTurningDirection(0);
							}
						});
						tmp.setRepeats(false);
						tmp.setInitialDelay(120);
						tmp.start();

					} else if (choice < 0) {
						p.turnLeft();
					} else {
						p.turnRight();
					}
				} else if (sideHit) {
					if (choice < 0) {
						p.turnLeft();
					} else if (choice > 0) {
						p.turnRight();
					}
				}
			}
		} else {
			if (!p.isForcedToTurn()) {
				if (rnd.nextDouble() < 0.03) {
					if (pointDifference > 0.5) {
						if (choice < 0) {
							p.setTurningDirection(-1);
						} else {
							p.setTurningDirection(1);
						}
					} else {
						if (rnd.nextDouble() < 0.5) {
							p.setTurningDirection(-1);
						} else {
							p.setTurningDirection(1);
						}
					}
					Timer tmp = new Timer(10, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (!p.isForcedToTurn()) {
								p.setTurningDirection(0);
							}
						}
					});
					tmp.setRepeats(false);
					tmp.setInitialDelay(rnd.nextInt(300) + 100);
					tmp.start();
				}
			}
		}

	}

	public double formula(double x, double angle) {
		return Math.pow(1.05, x - 40) * Math.pow(1.03, 20 - angle);
	}

	public void printParameters(Player p) {
		System.out.println("-----------------------\n" + p.getBodyColor() + "\n-----------------------\n"
				+ "isFrontHit: " + checkFrontHit(p) + "\t" + "isSideHit: " + checkSideHit(p) + "\nSideDifference: "
				+ compareSides(p) + "\n-----------------------");
	}

	public boolean checkFrontHit(Player p) {
		double radius = p.width / 2;
		for (int angle = -10; angle <= 10; angle += 2) {
			for (int x = 1; x <= 50; x += 5) {
				Vector leftVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() + angle);
				int frontX = (int) (p.getFront(-radius / 6 * 4).getX() + leftVector.getX());
				int frontY = (int) (p.getFront(-radius / 6 * 4).getY() + leftVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontX < 0) {
						frontX = getWidth() + frontX;
					} else if (frontX >= getWidth()) {
						frontX = frontX - getWidth();
					}
					if (frontY < 0) {
						frontY = getHeight() + frontY;
					} else if (frontY >= getHeight()) {
						frontY = frontY - getHeight();
					}
				}
				if ((frontX < 0 || frontX >= getWidth()) || (frontY < 0 || frontY >= getHeight())
						|| (!new Color(buffer.getRGB(frontX, frontY)).equals(Color.black))) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean checkSideHit(Player p) {
		double radius = p.width / 2;
		for (int angle = 10; angle <= 60; angle += 10) {
			for (int x = 1; x <= 45; x += 5) {
				/////////// LeftSide////////////////////////
				Vector leftVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() - angle);
				int frontLeftX = (int) (p.getFront(-radius / 6 * 4).getX() + leftVector.getX());
				int frontLeftY = (int) (p.getFront(-radius / 6 * 4).getY() + leftVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontLeftX < 0) {
						frontLeftX = getWidth() + frontLeftX;
					} else if (frontLeftX >= getWidth()) {
						frontLeftX = frontLeftX - getWidth();
					}
					if (frontLeftY < 0) {
						frontLeftY = getHeight() + frontLeftY;
					} else if (frontLeftY >= getHeight()) {
						frontLeftY = frontLeftY - getHeight();
					}
				}
				if ((frontLeftX < 0 || frontLeftX >= getWidth()) || (frontLeftY < 0 || frontLeftY >= getHeight())
						|| (!new Color(buffer.getRGB(frontLeftX, frontLeftY)).equals(Color.black))) {
					return true;
				}
				/////////// RightSide////////////////////////
				Vector rightVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() + angle);
				int frontRightX = (int) (p.getFront(-radius / 6 * 4).getX() + rightVector.getX());
				int frontRightY = (int) (p.getFront(-radius / 6 * 4).getY() + rightVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontRightX < 0) {
						frontRightX = getWidth() + frontRightX;
					} else if (frontRightX >= getWidth()) {
						frontRightX = frontRightX - getWidth();
					}
					if (frontRightY < 0) {
						frontRightY = getHeight() + frontRightY;
					} else if (frontRightY >= getHeight()) {
						frontRightY = frontRightY - getHeight();
					}
				}
				if ((frontRightX < 0 || frontRightX >= getWidth()) || (frontRightY < 0 || frontRightY >= getHeight())
						|| (!new Color(buffer.getRGB(frontRightX, frontRightY)).equals(Color.black))) {
					return true;
				}
			}

		}
		for (int angle = 60; angle <= 100; angle += 4) {
			for (int x = 1; x <= 35; x += 5) {
				/////////// LeftSide////////////////////////
				Vector leftVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() - angle);
				int frontLeftX = (int) (p.getFront(-radius / 6 * 4).getX() + leftVector.getX());
				int frontLeftY = (int) (p.getFront(-radius / 6 * 4).getY() + leftVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontLeftX < 0) {
						frontLeftX = getWidth() + frontLeftX;
					} else if (frontLeftX >= getWidth()) {
						frontLeftX = frontLeftX - getWidth();
					}
					if (frontLeftY < 0) {
						frontLeftY = getHeight() + frontLeftY;
					} else if (frontLeftY >= getHeight()) {
						frontLeftY = frontLeftY - getHeight();
					}
				}
				if ((frontLeftX < 0 || frontLeftX >= getWidth()) || (frontLeftY < 0 || frontLeftY >= getHeight())
						|| (!new Color(buffer.getRGB(frontLeftX, frontLeftY)).equals(Color.black))) {
					return true;
				}
				/////////// RightSide////////////////////////
				Vector rightVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() + angle);
				int frontRightX = (int) (p.getFront(-radius / 6 * 4).getX() + rightVector.getX());
				int frontRightY = (int) (p.getFront(-radius / 6 * 4).getY() + rightVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontRightX < 0) {
						frontRightX = getWidth() + frontRightX;
					} else if (frontRightX >= getWidth()) {
						frontRightX = frontRightX - getWidth();
					}
					if (frontRightY < 0) {
						frontRightY = getHeight() + frontRightY;
					} else if (frontRightY >= getHeight()) {
						frontRightY = frontRightY - getHeight();
					}
				}
				if ((frontRightX < 0 || frontRightX >= getWidth()) || (frontRightY < 0 || frontRightY >= getHeight())
						|| (!new Color(buffer.getRGB(frontRightX, frontRightY)).equals(Color.black))) {
					return true;
				}
			}
		}
		return false;
	}

	public double compareSides(Player p) {
		double left = 0;
		double right = 0;
		double radius = p.width / 2;
		boolean b = false;
		for (int angle = 5; angle <= 45; angle += 10) {
			for (int x = 1; x <= 65; x += 5) {
				b = false;
				/////////// LeftSide////////////////////////
				Vector vector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() - angle);
				int frontLeftX = (int) (p.getFront(-radius / 6 * 4).getX() + vector.getX());
				int frontLeftY = (int) (p.getFront(-radius / 6 * 4).getY() + vector.getY());
				if (p.canPassThroughBorder()) {
					if (frontLeftX < 0) {
						frontLeftX = getWidth() + frontLeftX;
					} else if (frontLeftX >= getWidth()) {
						frontLeftX = frontLeftX - getWidth();
					}
					if (frontLeftY < 0) {
						frontLeftY = getHeight() + frontLeftY;
					} else if (frontLeftY >= getHeight()) {
						frontLeftY = frontLeftY - getHeight();
					}
				}
				if ((frontLeftX < 0 || frontLeftX >= getWidth()) || (frontLeftY < 0 || frontLeftY >= getHeight())
						|| (!new Color(buffer.getRGB(frontLeftX, frontLeftY)).equals(Color.black))) {
					left += formula(x, angle);
					b = true;
				}
				/////////// RightSide////////////////////////
				Vector rightVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() + angle);
				int frontRightX = (int) (p.getFront(-radius / 6 * 4).getX() + rightVector.getX());
				int frontRightY = (int) (p.getFront(-radius / 6 * 4).getY() + rightVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontRightX < 0) {
						frontRightX = getWidth() + frontRightX;
					} else if (frontRightX >= getWidth()) {
						frontRightX = frontRightX - getWidth();
					}
					if (frontRightY < 0) {
						frontRightY = getHeight() + frontRightY;
					} else if (frontRightY >= getHeight()) {
						frontRightY = frontRightY - getHeight();
					}
				}
				if ((frontRightX < 0 || frontRightX >= getWidth()) || (frontRightY < 0 || frontRightY >= getHeight())
						|| (!new Color(buffer.getRGB(frontRightX, frontRightY)).equals(Color.black))) {
					right += formula(x, angle);
					b = true;
				}

				if (b)
					break;
			}
		}
		for (int angle = 50; angle <= 80; angle += 6) {
			for (int x = 1; x <= 90; x += 5) {
				b = false;
				/////////// LeftSide////////////////////////
				Vector leftVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() - angle);
				int frontLeftX = (int) (p.getFront(-radius / 6 * 4).getX() + leftVector.getX());
				int frontLeftY = (int) (p.getFront(-radius / 6 * 4).getY() + leftVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontLeftX < 0) {
						frontLeftX = getWidth() + frontLeftX;
					} else if (frontLeftX >= getWidth()) {
						frontLeftX = frontLeftX - getWidth();
					}
					if (frontLeftY < 0) {
						frontLeftY = getHeight() + frontLeftY;
					} else if (frontLeftY >= getHeight()) {
						frontLeftY = frontLeftY - getHeight();
					}
				}
				if ((frontLeftX < 0 || frontLeftX >= getWidth()) || (frontLeftY < 0 || frontLeftY >= getHeight())
						|| (!new Color(buffer.getRGB(frontLeftX, frontLeftY)).equals(Color.black))) {
					left += formula(x, angle);
					b = true;
				}
				/////////// RightSide////////////////////////
				Vector rightVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() + angle);
				int frontRightX = (int) (p.getFront(-radius / 6 * 4).getX() + rightVector.getX());
				int frontRightY = (int) (p.getFront(-radius / 6 * 4).getY() + rightVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontRightX < 0) {
						frontRightX = getWidth() + frontRightX;
					} else if (frontRightX >= getWidth()) {
						frontRightX = frontRightX - getWidth();
					}
					if (frontRightY < 0) {
						frontRightY = getHeight() + frontRightY;
					} else if (frontRightY >= getHeight()) {
						frontRightY = frontRightY - getHeight();
					}
				}
				if ((frontRightX < 0 || frontRightX >= getWidth()) || (frontRightY < 0 || frontRightY >= getHeight())
						|| (!new Color(buffer.getRGB(frontRightX, frontRightY)).equals(Color.black))) {
					right += formula(x, angle);
					b = true;
				}

				if (b)
					break;
			}
		}
		for (int angle = 80; angle <= 110; angle += 6) {
			for (int x = 1; x <= 40; x += 5) {
				b = false;
				/////////// LeftSide////////////////////////
				Vector leftVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() - angle);
				int frontLeftX = (int) (p.getFront(-radius / 6 * 4).getX() + leftVector.getX());
				int frontLeftY = (int) (p.getFront(-radius / 6 * 4).getY() + leftVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontLeftX < 0) {
						frontLeftX = getWidth() + frontLeftX;
					} else if (frontLeftX >= getWidth()) {
						frontLeftX = frontLeftX - getWidth();
					}
					if (frontLeftY < 0) {
						frontLeftY = getHeight() + frontLeftY;
					} else if (frontLeftY >= getHeight()) {
						frontLeftY = frontLeftY - getHeight();
					}
				}
				if ((frontLeftX < 0 || frontLeftX >= getWidth()) || (frontLeftY < 0 || frontLeftY >= getHeight())
						|| (!new Color(buffer.getRGB(frontLeftX, frontLeftY)).equals(Color.black))) {
					left += formula(x, angle);
					b = true;
				}
				/////////// RightSide////////////////////////
				Vector rightVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() + angle);
				int frontRightX = (int) (p.getFront(-radius / 6 * 4).getX() + rightVector.getX());
				int frontRightY = (int) (p.getFront(-radius / 6 * 4).getY() + rightVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontRightX < 0) {
						frontRightX = getWidth() + frontRightX;
					} else if (frontRightX >= getWidth()) {
						frontRightX = frontRightX - getWidth();
					}
					if (frontRightY < 0) {
						frontRightY = getHeight() + frontRightY;
					} else if (frontRightY >= getHeight()) {
						frontRightY = frontRightY - getHeight();
					}
				}
				if ((frontRightX < 0 || frontRightX >= getWidth()) || (frontRightY < 0 || frontRightY >= getHeight())
						|| (!new Color(buffer.getRGB(frontRightX, frontRightY)).equals(Color.black))) {
					right += formula(x, angle);
					b = true;
				}

				if (b)
					break;
			}
		}

		return left - right;
	}

	public void drawHitZone(Player p) {
		double radius = p.width / 2;
		for (int i = -60; i <= 60; i += 30) {
			Vector vector = new Vector(10 * radius, p.getV().getAngle());
			vector.rotate(i);
			double startX = p.getFront(-radius / 5 * 3).getX();
			double startY = p.getFront(-radius / 5 * 3).getY();
			double endX = startX + vector.getX();
			double endY = startY + vector.getY();
			screen.setColor(Color.magenta);
			screen.drawLine((int) startX, (int) startY, (int) endX, (int) endY);
		}
	}

	public void drawBotHitCheck(Player p) {
		double radius = p.width / 2;
		for (int j = -10; j <= 10; j += 2) {
			for (int i = 1; i <= 50; i += 5) {
				Vector vector = new Vector(10 * (1.5 * radius + i), p.getV().getAngle() + j);
				int frontX = (int) (p.getFront(-radius / 6 * 4).getX() + vector.getX());
				int frontY = (int) (p.getFront(-radius / 6 * 4).getY() + vector.getY());
				if (p.canPassThroughBorder()) {
					if (frontX < 0) {
						frontX = getWidth() - 1 + frontX;
					} else if (frontX >= getWidth()) {
						frontX = frontX - getWidth();
					}
					if (frontY < 0) {
						frontY = getHeight() - 1 + frontY;
					} else if (frontY >= getHeight()) {
						frontY = frontY - getHeight();
					}
				}
				screen.setColor(Color.orange);
				screen.fillOval((int) frontX, (int) frontY, 1, 1);
			}
		}
		for (int j = 10; j <= 60; j += 10) {
			for (int i = 1; i <= 45; i += 5) {
				/////////// LeftSide////////////////////////
				Vector leftVector = new Vector(10 * (1.5 * radius + i), p.getV().getAngle() - j);
				int frontLeftX = (int) (p.getFront(-radius / 6 * 4).getX() + leftVector.getX());
				int frontLeftY = (int) (p.getFront(-radius / 6 * 4).getY() + leftVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontLeftX < 0) {
						frontLeftX = getWidth() - 1 + frontLeftX;
					} else if (frontLeftX >= getWidth()) {
						frontLeftX = frontLeftX - getWidth();
					}
					if (frontLeftY < 0) {
						frontLeftY = getHeight() - 1 + frontLeftY;
					} else if (frontLeftY >= getHeight()) {
						frontLeftY = frontLeftY - getHeight();
					}
				}
				screen.setColor(Color.orange);
				screen.fillOval((int) frontLeftX, (int) frontLeftY, 1, 1);
				/////////// RightSide////////////////////////
				Vector rightVector = new Vector(10 * (1.5 * radius + i), p.getV().getAngle() + j);
				int frontRightX = (int) (p.getFront(-radius / 6 * 4).getX() + rightVector.getX());
				int frontRightY = (int) (p.getFront(-radius / 6 * 4).getY() + rightVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontRightX < 0) {
						frontRightX = getWidth() - 1 + frontRightX;
					} else if (frontRightX >= getWidth()) {
						frontRightX = frontRightX - getWidth();
					}
					if (frontRightY < 0) {
						frontRightY = getHeight() - 1 + frontRightY;
					} else if (frontRightY >= getHeight()) {
						frontRightY = frontRightY - getHeight();
					}
				}
				screen.setColor(Color.orange);
				screen.fillOval((int) frontRightX, (int) frontRightY, 1, 1);
			}
		}
		for (int j = 60; j <= 100; j += 4) {
			for (int i = 1; i <= 35; i += 5) {
				/////////// LeftSide////////////////////////
				Vector leftVector = new Vector(10 * (1.5 * radius + i), p.getV().getAngle() - j);
				int frontLeftX = (int) (p.getFront(-radius / 6 * 4).getX() + leftVector.getX());
				int frontLeftY = (int) (p.getFront(-radius / 6 * 4).getY() + leftVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontLeftX < 0) {
						frontLeftX = getWidth() - 1 + frontLeftX;
					} else if (frontLeftX >= getWidth()) {
						frontLeftX = frontLeftX - getWidth();
					}
					if (frontLeftY < 0) {
						frontLeftY = getHeight() - 1 + frontLeftY;
					} else if (frontLeftY >= getHeight()) {
						frontLeftY = frontLeftY - getHeight();
					}
				}
				screen.setColor(Color.orange);
				screen.fillOval((int) frontLeftX, (int) frontLeftY, 1, 1);
				/////////// RightSide////////////////////////
				Vector rightVector = new Vector(10 * (1.5 * radius + i), p.getV().getAngle() + j);
				int frontRightX = (int) (p.getFront(-radius / 6 * 4).getX() + rightVector.getX());
				int frontRightY = (int) (p.getFront(-radius / 6 * 4).getY() + rightVector.getY());
				if (p.canPassThroughBorder()) {
					if (frontRightX < 0) {
						frontRightX = getWidth() - 1 + frontRightX;
					} else if (frontRightX >= getWidth()) {
						frontRightX = frontRightX - getWidth();
					}
					if (frontRightY < 0) {
						frontRightY = getHeight() - 1 + frontRightY;
					} else if (frontRightY >= getHeight()) {
						frontRightY = frontRightY - getHeight();
					}
				}
				screen.setColor(Color.orange);
				screen.fillOval((int) frontRightX, (int) frontRightY, 1, 1);
			}
		}
	}

	public void drawBotChoiceCheck(Player p) {
		double radius = p.width / 2;
		for (int angle = 5; angle <= 45; angle += 10) {
			for (int x = 1; x <= 65; x += 5) {
				/////////// LeftSide////////////////////////
				Vector leftVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() - angle);
				int leftX = (int) (p.getFront(-radius / 6 * 4).getX() + leftVector.getX());
				int leftY = (int) (p.getFront(-radius / 6 * 4).getY() + leftVector.getY());
				if (p.canPassThroughBorder()) {
					if (leftX < 0) {
						leftX = getWidth() - 1 + leftX;
					} else if (leftX >= getWidth()) {
						leftX = leftX - getWidth();
					}
					if (leftY < 0) {
						leftY = getHeight() - 1 + leftY;
					} else if (leftY >= getHeight()) {
						leftY = leftY - getHeight();
					}
				}
				screen.setColor(Color.magenta);
				screen.fillOval(leftX, leftY, 1, 1);
				/////////// RightSide////////////////////////
				Vector rightVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() + angle);
				int rightX = (int) (p.getFront(-radius / 6 * 4).getX() + rightVector.getX());
				int rightY = (int) (p.getFront(-radius / 6 * 4).getY() + rightVector.getY());
				if (p.canPassThroughBorder()) {
					if (rightX < 0) {
						rightX = getWidth() - 1 + rightX;
					} else if (rightX >= getWidth()) {
						rightX = rightX - getWidth();
					}
					if (rightY < 0) {
						rightY = getHeight() - 1 + rightY;
					} else if (rightY >= getHeight()) {
						rightY = rightY - getHeight();
					}
				}
				screen.setColor(Color.magenta);
				screen.fillOval(rightX, rightY, 1, 1);
			}
		}
		for (int angle = 50; angle <= 80; angle += 6) {
			for (int x = 1; x <= 90; x += 5) {
				/////////// LeftSide////////////////////////
				Vector leftVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() - angle);
				int leftX = (int) (p.getFront(-radius / 6 * 4).getX() + leftVector.getX());
				int leftY = (int) (p.getFront(-radius / 6 * 4).getY() + leftVector.getY());
				if (p.canPassThroughBorder()) {
					if (leftX < 0) {
						leftX = getWidth() - 1 + leftX;
					} else if (leftX >= getWidth()) {
						leftX = leftX - getWidth();
					}
					if (leftY < 0) {
						leftY = getHeight() - 1 + leftY;
					} else if (leftY >= getHeight()) {
						leftY = leftY - getHeight();
					}
				}
				screen.setColor(Color.magenta);
				screen.fillOval(leftX, leftY, 1, 1);
				/////////// RightSide////////////////////////
				Vector rightVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() + angle);
				int rightX = (int) (p.getFront(-radius / 6 * 4).getX() + rightVector.getX());
				int rightY = (int) (p.getFront(-radius / 6 * 4).getY() + rightVector.getY());
				if (p.canPassThroughBorder()) {
					if (rightX < 0) {
						rightX = getWidth() - 1 + rightX;
					} else if (rightX >= getWidth()) {
						rightX = rightX - getWidth();
					}
					if (rightY < 0) {
						rightY = getHeight() - 1 + rightY;
					} else if (rightY >= getHeight()) {
						rightY = rightY - getHeight();
					}
				}
				screen.setColor(Color.magenta);
				screen.fillOval(rightX, rightY, 1, 1);
			}
		}
		for (int angle = 80; angle <= 110; angle += 6) {
			for (int x = 1; x <= 40; x += 5) {
				/////////// LeftSide////////////////////////
				Vector leftVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() - angle);
				int leftX = (int) (p.getFront(-radius / 6 * 4).getX() + leftVector.getX());
				int leftY = (int) (p.getFront(-radius / 6 * 4).getY() + leftVector.getY());
				if (p.canPassThroughBorder()) {
					if (leftX < 0) {
						leftX = getWidth() - 1 + leftX;
					} else if (leftX >= getWidth()) {
						leftX = leftX - getWidth();
					}
					if (leftY < 0) {
						leftY = getHeight() - 1 + leftY;
					} else if (leftY >= getHeight()) {
						leftY = leftY - getHeight();
					}
				}
				screen.setColor(Color.magenta);
				screen.fillOval(leftX, leftY, 1, 1);
				/////////// RightSide////////////////////////
				Vector rightVector = new Vector(10 * (1.5 * radius + x), p.getV().getAngle() + angle);
				int rightX = (int) (p.getFront(-radius / 6 * 4).getX() + rightVector.getX());
				int rightY = (int) (p.getFront(-radius / 6 * 4).getY() + rightVector.getY());
				if (p.canPassThroughBorder()) {
					if (rightX < 0) {
						rightX = getWidth() - 1 + rightX;
					} else if (rightX >= getWidth()) {
						rightX = rightX - getWidth();
					}
					if (rightY < 0) {
						rightY = getHeight() - 1 + rightY;
					} else if (rightY >= getHeight()) {
						rightY = rightY - getHeight();
					}
				}
				screen.setColor(Color.magenta);
				screen.fillOval(rightX, rightY, 1, 1);
			}
		}
	}

	public void checkForHits() {
		Player winner = player[0];
		for (Player p : player) {
			if (p.isAlive()) {
				if (checkBorderHit(p) || checkPlayerHit(p)) {
					p.setAlive(false);
					updateScores();
					for (Player plyr : player) {
						if (plyr.isAlive()) {
							winner = plyr;
						}
					}
				}
			}
			if (checkRoundOver()) {
				reset();
				if (!isDemo) {
					timer.stop();
					if (player.length >= 2 && (player[0].score >= 5 * (player.length - 1))
							&& (player[0].score - player[1].score >= 2)) {
						listener.gameOver();
					} else {
						listener.roundOver(winner);
					}
				}
			}
		}
	}

	public boolean checkPlayerHit(Player p) {
		double radius = p.width / 2;
		for (int i = -70; i <= 70; i += 35) {
			Vector vector = new Vector(10 * radius, p.getV().getAngle());
			vector.rotate(i);
			int frontX = (int) (p.getFront(-radius / 5 * 3).getX() + vector.getX());
			int frontY = (int) (p.getFront(-radius / 5 * 3).getY() + vector.getY());
			if (!(new Color(buffer.getRGB(frontX, frontY)).equals(Color.black))) {
				if (!p.isGhost()) {
					return true;
				}
			}
			for (int j = 0; j < powerUp.size(); j++) {
				PowerUp power = powerUp.get(j);
				double x = power.x + 18;
				double y = power.y + 18;
				double distance = Math.sqrt(Math.pow(x - frontX, 2) + Math.pow(y - frontY, 2));
				if (distance < 18) {
					power.activate(this, player, p);
					powerUp.remove(j);
				}
			}
		}
		return false;
	}

	public boolean checkBorderHit(Player p) {
		double radius = p.width / 2;

		for (int i = -70; i <= 70; i += 35) {
			Vector vector = new Vector(11 * radius, p.getV().getAngle());
			vector.rotate(i);
			int frontX = (int) (p.getFront(-radius / 6 * 4).getX() + vector.getX());
			int frontY = (int) (p.getFront(-radius / 6 * 4).getY() + vector.getY());
			if (frontX <= 1 || frontX >= getWidth() - 2) {
				if (p.canPassThroughBorder()) {
					p.setX(Math.abs(getWidth() - p.x - p.width));
				} else {
					return true;
				}
			}
			if (frontY <= 1 || frontY >= getHeight() - 2) {
				if (p.canPassThroughBorder()) {
					p.setY(Math.abs(getHeight() - p.y - p.height));
				} else {
					return true;
				}
			}
		}
		return false;
	}

	public void updateScores() {
		for (Player p : player) {
			if (p.isAlive()) {
				p.score++;
			}
		}
		listener.scoresUpdated();
	}

	public boolean checkRoundOver() {
		int count = player.length;
		for (Player p : player) {
			if (!p.isAlive())
				count--;
		}
		return (count <= 1) ? true : false;
	}

	public void reset() {
		clear();
		passThroughBorderCounter = 0;
		for (Player plyr : player) {
			plyr.reset();
		}
		powerUp.clear();
	}

	public void clear() {
		g2.setColor(Color.black);
		g2.fillRect(0, 0, 800, 800);
	}

	public ArrayList<PowerUp> getPowerUps() {
		return powerUp;
	}

	public void setPowerUpSpawnChance(double val) {
		chance = val;
	}

	public boolean canPassThroughBorder() {
		return passThroughBorderCounter != 0;
	}

	public void setPassThroughBorder(boolean val) {
		this.passThroughBorderCounter += val ? 1 : (canPassThroughBorder()) ? -1 : 0;
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	public boolean isRunning() {
		return timer.isRunning();
	}

	public void setPlayers(Player[] player) {
		this.player = player;
	}

	public void setListener(GameListener listener) {
		this.listener = listener;
	}

}