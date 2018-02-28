import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import javax.swing.Timer;

public class Player extends Ellipse2D.Double {

	private String name;
	private boolean bot;
	private Color bodyColor;
	private Color headColor;

	private boolean drawHead;
	private int headFlashCounter;
	Timer headColorFlash;

	private double turnDegree;
	private double gapChance;

	int rightKey;
	int leftKey;

	private boolean keyLocked;
	int lockedKey;

	private boolean alive;
	private int turning;

	private boolean forcedToTurn;

	private int passThroughBorderCounter;
	private int ghostCounter;
	private int reverseCounter;
	private int sizeCounter;
	private int speedCounter;

	private Point pos;
	private Vector v;

	int score;

	Random rnd;

	public Player(boolean isBot, String name, Color c, int left, int right) {

		this.name = name;
		this.bot = isBot;
		this.bodyColor = c;
		headColor = Color.cyan;

		turnDegree = 1.9;
		gapChance = 0.004;

		rightKey = right;
		leftKey = left;

		keyLocked = false;
		lockedKey = 0;

		score = 0;

		alive = true;
		drawHead = true;

		turning = 0;
		forcedToTurn = false;

		passThroughBorderCounter = 0;
		ghostCounter = 0;
		reverseCounter = 0;
		sizeCounter = 0;
		speedCounter = 0;

		this.width = 8;
		this.height = 8;

		rnd = new Random();

		v = new Vector(12, rnd.nextInt(360));
		pos = new Point(rnd.nextInt(600) + 100, rnd.nextInt(600) + 100);

		this.x = pos.getX();
		this.y = pos.getY();

		headColorFlash = new Timer(300, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawHead = !drawHead;
			}
		});

	}

	public void move() {
		if (isReverse() && !isBot()) {
			v.rotate(-turning * turnDegree);
		} else {
			v.rotate(turning * turnDegree);
		}
		v.setValue(12 + speedCounter * 5);
		pos.translate(v);
		super.x = pos.getX();
		super.y = pos.getY();
	}

	public void setTurningDirection(int dir) {
		this.turning = dir;
	}

	public int getTurningDirection() {
		return turning;
	}

	public void setForcedToTurn(boolean val) {
		this.forcedToTurn = val;
	}

	public boolean isForcedToTurn() {
		return forcedToTurn;
	}

	public void turnLeft() {
		v.rotate(-turnDegree);
	}

	public void turnRight() {
		v.rotate(turnDegree);
	}

	public void setSize(int val) {
		double radius = height / 2;
		switch (val) {
		case -1:
			setX(getFront(-radius / 3).getX() - radius / 1.5);
			setY(getFront(-radius / 3).getY() - radius / 1.5);
			width /= 1.5;
			height /= 1.5;
			sizeCounter++;
			break;
		case 0:
			if (sizeCounter != 0) {
				sizeCounter--;
			}
			if (sizeCounter == 0) {
				width = 8;
				height = 8;
			}
			break;
		case 1:
			setX(getFront(-radius / 3).getX() - radius * 1.5);
			setY(getFront(-radius / 3).getY() - radius * 1.5);
			width *= 1.5;
			height *= 1.5;
			sizeCounter++;
			break;
		}
	}

	public void speedUp() {
		speedCounter++;
	}

	public void slowDown() {
		speedCounter--;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean isAlive) {
		this.alive = isAlive;
	}

	public boolean isKeyLocked() {
		return keyLocked;
	}

	public void setKeyLocked(boolean val) {
		keyLocked = val;
	}

	public boolean isReverse() {
		return reverseCounter != 0;
	}

	public void setReverse(boolean val) {
		this.reverseCounter += val ? 1 : (isReverse()) ? -1 : 0;
		if (isReverse()) {
			headColor = Color.magenta.darker();
		} else {
			headColor = Color.cyan;
		}
	}

	public boolean canPassThroughBorder() {
		return passThroughBorderCounter != 0;
	}

	public void setPassThroughBorder(boolean val) {
		this.passThroughBorderCounter += val ? 1 : (canPassThroughBorder()) ? -1 : 0;
	}

	public boolean isGhost() {
		return ghostCounter != 0;
	}

	public void setGhost(boolean val) {
		this.ghostCounter += val ? 1 : (isGhost()) ? -1 : 0;
	}

	public void setHeadFlash(boolean val) {
		headFlashCounter += val ? 1 : (headFlashCounter != 0) ? -1 : 0;

		if (headFlashCounter != 0) {
			headColorFlash.start();
		} else {
			headColorFlash.stop();
			drawHead = true;
		}
	}

	public void setGapChance(double val) {
		gapChance = val;
	}

	public double getGapChance() {
		return gapChance;
	}

	public void setTurnDegree(double val) {
		turnDegree = val;
	}

	public String getName() {
		return name;
	}

	public Color getBodyColor() {
		return bodyColor;
	}

	public void setHeadColor(Color c) {
		headColor = c;
	}

	public Color getHeadColor() {
		return headColor;
	}

	public boolean isDrawHead() {
		return drawHead;
	}

	public boolean isBot() {
		return bot;
	}

	public Vector getV() {
		return v;
	}

	public Point getPosition() {
		return pos;
	}

	public void setX(double x) {
		pos.setX(x);
		this.x = x;
	}

	public void setY(double y) {
		pos.setY(y);
		this.y = y;
	}

	public Point getFront(double distanceOffset, int angleOffset) {
		double radius = width / 2;
		Vector unit = v.getUnit();
		unit.setValue((radius + distanceOffset) * 10);
		unit.rotate(angleOffset);
		double x = this.x + radius + unit.getX();
		double y = this.y + radius + unit.getY();
		return new Point(x, y);
	}

	public Point getFront(double distanceOffset) {
		double radius = width / 2;
		Vector unit = v.getUnit();
		unit.setValue((radius + distanceOffset) * 10);
		double x = this.x + radius + unit.getX();
		double y = this.y + radius + unit.getY();
		return new Point(x, y);
	}

	public void reset() {
		pos.setX(rnd.nextInt(600) + 100);
		pos.setY(rnd.nextInt(600) + 100);
		v.setAngle(rnd.nextInt(360));
		v.setValue(12);
		headColor = Color.cyan;

		keyLocked = false;
		lockedKey = 0;

		x = pos.getX();
		y = pos.getY();
		width = 8;
		height = 8;

		forcedToTurn = false;

		turnDegree = 1.9;
		gapChance = 0.004;

		alive = true;
		passThroughBorderCounter = 0;
		ghostCounter = 0;
		reverseCounter = 0;
		speedCounter = 0;
		headFlashCounter = 0;
		drawHead = true;
		headColorFlash.stop();
	}

}

class Point {

	private double x;
	private double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void translate(Vector v) {
		x += v.getX();
		y += v.getY();
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

}

class Vector {

	private double value;
	private double angle;

	public Vector(double val, double degree) {
		this.value = val / 10;
		this.angle = Math.toRadians(degree);
	}

	public double getX() {
		return value * Math.cos(angle);
	}

	public double getY() {
		return value * Math.sin(angle);
	}

	public void rotate(double degree) {
		angle += Math.toRadians(degree);
	}

	public void setValue(double val) {
		this.value = val / 10;
	}

	public double getValue() {
		return value;
	}

	public void setAngle(int degree) {
		this.angle = (double) degree / 180 * Math.PI;
	}

	public int getAngle() {
		return (int) (angle / Math.PI * 180);
	}

	public Vector getUnit() {
		return new Vector(10, Math.toDegrees(angle));
	}

}