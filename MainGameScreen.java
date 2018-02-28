import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class MainGameScreen extends JPanel {

	private Player[] player;

	private JPanel scorePane;
	private JPanel scoreBoard;
	private JPanel bottomPane;

	private JLabel goalLabel;
	private JLabel[] playerNameRanked;
	private JLabel[] playerScoreRanked;

	private JLabel winnerTextLabel;
	private JLabel winnerLabel;

	private GameScreen gameScreen;
	private JLabel pauseLabel;
	private JLabel startLabel;

	private JFrame parent;

	private MainGameScreenListener listener;

	public MainGameScreen(JFrame parent) {

		this.parent = parent;

		setBackground(Color.black);

		setLayout(new BorderLayout());

		scorePane = new JPanel();
		bottomPane = new JPanel();

		pauseLabel = new JLabel("Game Paused", JLabel.CENTER);
		pauseLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 40));
		pauseLabel.setForeground(Color.yellow);
		pauseLabel.setVisible(false);

		winnerTextLabel = new JLabel("won last round! ");
		winnerTextLabel.setFont(new Font(Font.SERIF, Font.ITALIC, 22));
		winnerTextLabel.setForeground(Color.lightGray);
		winnerTextLabel.setVisible(false);

		winnerLabel = new JLabel();
		winnerLabel.setFont(new Font(Font.SERIF, Font.ITALIC, 25));
		winnerLabel.setPreferredSize(new Dimension(150, 30));
		winnerLabel.setVisible(false);

		startLabel = new JLabel("press space to start", JLabel.CENTER);
		startLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 20));
		startLabel.setForeground(Color.yellow);

		scorePane.setPreferredSize(new Dimension(400, 800));
		scorePane.setBackground(Color.darkGray);

		bottomPane.setPreferredSize(new Dimension(400, 300));
		bottomPane.setBackground(Color.darkGray);

		Border outer = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		Border inner = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.white), "Scoreboard",
				TitledBorder.LEFT, TitledBorder.TOP, new Font(Font.DIALOG, Font.BOLD, 30), Color.yellow);
		scorePane.setBorder(BorderFactory.createCompoundBorder(outer, inner));

		scorePane.setLayout(new BorderLayout());

		bottomPane.setLayout(new FlowLayout(FlowLayout.LEFT));

		bottomPane.add(winnerLabel);
		bottomPane.add(winnerTextLabel);

		scorePane.add(bottomPane, BorderLayout.SOUTH);

		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				if (gameScreen != null) {
					gameScreen.requestFocusInWindow();
					startLabel.setVisible(true);
					pauseLabel.setVisible(false);
				}
			}
		});

	}

	public void layoutScoreBoard() {
		scoreBoard.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.NONE;

		gc.gridy = 0;
		gc.weighty = 4;

		gc.gridx = 0;
		gc.weightx = 1;
		gc.insets = new Insets(0, 20, 10, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		JLabel label = new JLabel(" First to reach ");
		label.setFont(new Font(Font.SERIF, Font.BOLD, 30));
		label.setPreferredSize(new Dimension(250, 40));
		label.setOpaque(true);
		label.setForeground(Color.white);
		label.setBackground(Color.gray);
		scoreBoard.add(label, gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.insets = new Insets(0, 0, 10, 70);
		gc.anchor = GridBagConstraints.LINE_END;
		scoreBoard.add(goalLabel, gc);

		for (int i = 0; i < player.length; i++) {

			gc.gridy++;
			gc.weighty = 1;

			gc.gridx = 0;
			gc.weightx = 1;
			gc.insets = new Insets(5, 50, 0, 0);
			gc.anchor = GridBagConstraints.FIRST_LINE_START;
			scoreBoard.add(playerNameRanked[i], gc);

			gc.gridx = 1;
			gc.weightx = 1;
			gc.insets = new Insets(5, 0, 0, 50);
			gc.anchor = GridBagConstraints.FIRST_LINE_START;
			scoreBoard.add(playerScoreRanked[i], gc);

		}

	}

	public void setPlayers(Player[] player) {
		this.player = player;

		if (scoreBoard == null) {
			scoreBoard = new JPanel();
		} else {
			scoreBoard.removeAll();
			scorePane.removeAll();
			scorePane.add(bottomPane, BorderLayout.SOUTH);
		}
		removeAll();

		goalLabel = new JLabel("" + (5 * (player.length - 1)), JLabel.CENTER);
		goalLabel.setFont(new Font(Font.SERIF, Font.BOLD, 30));
		goalLabel.setPreferredSize(new Dimension(40, 40));
		goalLabel.setForeground(Color.white);
		goalLabel.setOpaque(true);
		goalLabel.setBackground(player[0].getBodyColor());

		playerNameRanked = new JLabel[player.length];
		playerScoreRanked = new JLabel[player.length];

		for (int i = 0; i < player.length; i++) {
			playerNameRanked[i] = new JLabel((i + 1) + ". " + player[i].getName());
			playerNameRanked[i].setFont(new Font("sanserif", Font.BOLD, 25));
			playerNameRanked[i].setForeground(player[i].getBodyColor());

			playerScoreRanked[i] = new JLabel("" + player[i].score);
			playerScoreRanked[i].setFont(new Font("sanserif", Font.BOLD, 25));
			playerScoreRanked[i].setForeground(Color.white);
		}

		scoreBoard.setPreferredSize(new Dimension(400, player.length * 30 + 100));
		scoreBoard.setBackground(Color.darkGray);

		layoutScoreBoard();

		scorePane.add(scoreBoard, BorderLayout.NORTH);

		gameScreen = new GameScreen(player, false);

		gameScreen.setVisible(false);

		gameScreen.setLayout(new BorderLayout());

		gameScreen.add(pauseLabel, BorderLayout.CENTER);
		gameScreen.add(startLabel, BorderLayout.SOUTH);

		gameScreen.setListener(new GameListener() {
			public void scoresUpdated() {
				Utils.sort(player);
				for (int i = 0; i < player.length; i++) {
					playerNameRanked[i].setText((i + 1) + ". " + player[i].getName());
					playerScoreRanked[i].setText("" + player[i].score);
					playerNameRanked[i].setForeground(player[i].getBodyColor());
				}
				goalLabel.setBackground(player[0].getBodyColor());
				if (player.length > 1) {
					int goal = Integer.parseInt(goalLabel.getText());
					int score1 = Integer.parseInt(playerScoreRanked[0].getText());
					int score2 = Integer.parseInt(playerScoreRanked[1].getText());
					if (score1 >= goal - 1 && score1 - score2 <= 1) {
						goalLabel.setText("" + (score2 + 2));
					}
				}
			}

			public void gameOver() {
				startLabel.setVisible(false);
				JOptionPane.showMessageDialog(parent, player[0].getName() + " Won!", "Game Over",
						JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
				listener.gameOver();
			}

			public void roundOver(Player winner) {
				startLabel.setVisible(true);
				if (winner != null) {
					winnerLabel.setText("     " + winner.getName());
					winnerLabel.setForeground(winner.getBodyColor());
					winnerLabel.setVisible(true);
					winnerTextLabel.setVisible(true);
					Timer winnerTextTimer = new Timer(10, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							winnerLabel.setVisible(false);
							winnerTextLabel.setVisible(false);
						}
					});
					winnerTextTimer.setRepeats(false);
					winnerTextTimer.setInitialDelay(3000);
					winnerTextTimer.start();
				}
				Timer startTimer = new Timer(10, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						gameScreen.stop();
					}
				});
				startTimer.setRepeats(false);
				startTimer.setInitialDelay(50);
				startTimer.start();
				gameScreen.start();
			}
		});

		gameScreen.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {

				int key = e.getKeyCode();

				if (key == KeyEvent.VK_SPACE) {
					startLabel.setVisible(false);
					if (gameScreen.isRunning()) {
						gameScreen.stop();
						pauseLabel.setVisible(true);
					} else {
						pauseLabel.setVisible(false);
						gameScreen.start();
					}
				} else if (key == KeyEvent.VK_ESCAPE) {
					gameScreen.stop();
					listener.gameOver();
				}
			}
		});

		add(gameScreen, BorderLayout.CENTER);
		add(scorePane, BorderLayout.EAST);

		gameScreen.setVisible(true);

	}

	public void setListener(MainGameScreenListener listener) {
		this.listener = listener;
	}

}
