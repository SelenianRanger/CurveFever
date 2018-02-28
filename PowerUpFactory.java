import java.util.Random;

public class PowerUpFactory {

	private static Random rnd = new Random();

	public static PowerUp newPowerUp() {
		int code = rnd.nextInt(11);
		int x = rnd.nextInt(600) + 100;
		int y = rnd.nextInt(600) + 100;
		PowerUp powerUp = null;
		switch (code) {
		case 0:
			powerUp = new SpeedPowerUp(x, y, rnd.nextBoolean(), rnd.nextBoolean());
			break;
		case 1:
			powerUp = new SizePowerUp(x, y, rnd.nextBoolean());
			break;
		case 2:
			powerUp = new BorderPowerUp(x, y, rnd.nextBoolean());
			break;
		case 3:
			powerUp = new GhostPowerUp(x, y);
			break;
		case 4:
			powerUp = new ClearScreenPowerUp(x, y);
			break;
		case 5:
			powerUp = new ReversePowerUp(x, y);
			break;
		case 6:
			powerUp = new ChancePowerUp(x, y);
			break;
		case 7:
			powerUp = new FingerPowerUp(x, y);
			break;
		case 8:
			powerUp = new HolePowerUp(x, y);
			break;
		case 9:
			powerUp = new MovePowerUp(x, y);
			break;
		case 10:
			powerUp = new TurnPowerUp(x, y, rnd.nextBoolean());
			break;
		}
		return powerUp;
	}

}
