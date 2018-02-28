
//Sina Kamali 610394126

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

	private static JFrame frame;
	private static StartScreen startScreen;
	private static MainGameScreen mainGameScreen;

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame("Sina Kamali [610394126]");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(1200, 800);
				frame.setResizable(false);

				CardLayout cards = new CardLayout();
				frame.setLayout(cards);

				startScreen = new StartScreen(frame);
				mainGameScreen = new MainGameScreen(frame);

				startScreen.setVisible(false);
				mainGameScreen.setVisible(false);

				frame.add(startScreen, "start");
				frame.add(mainGameScreen, "game");

				startScreen.setListener(new StartScreenListener() {
					public void gameStarted(Player[] players) {
						mainGameScreen.setPlayers(players);
						cards.show(frame.getContentPane(), "game");
					}
				});

				mainGameScreen.setListener(new MainGameScreenListener() {
					public void gameOver() {
						cards.show(frame.getContentPane(), "start");
					}
				});

				frame.setVisible(true);
			}
		});

	}

}
