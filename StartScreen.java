import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class StartScreen extends JPanel {

	private JFrame parent;

	private JPanel settingPanel;
	private JPanel upperPane;
	private JPanel lowerPane;
	private JComboBox<Integer> humanCombo;
	private JComboBox<Integer> botCombo;
	private JLabel playerLabel;
	private JLabel botLabel;
	private JButton startGameBtn;

	private KeySetButton[] playerLeftKeyBtn;
	private KeySetButton[] playerRightKeyBtn;
	private JTextField[] playerNameLabel;

	private Integer humanCount;
	private Integer botCount;

	private int[] playerLeftKey;
	private int[] playerRightKey;

	private GameScreen demoScreen;

	private StartScreenListener listener;

	public StartScreen(JFrame parent) {

		this.parent = parent;

		playerLeftKey = new int[6];
		playerRightKey = new int[6];

		settingPanel = new JPanel();
		upperPane = new JPanel();
		lowerPane = new JPanel();
		humanCombo = new JComboBox<Integer>();
		botCombo = new JComboBox<Integer>();
		playerLabel = new JLabel("Human: ");
		botLabel = new JLabel("Bots: ");
		startGameBtn = new JButton("Play");

		playerLeftKeyBtn = new KeySetButton[6];
		playerRightKeyBtn = new KeySetButton[6];
		playerNameLabel = new JTextField[6];

		demoScreen = new GameScreen(null, true);

		humanCombo.setFocusable(false);
		botCombo.setFocusable(false);
		demoScreen.setFocusable(false);

		startGameBtn.setFocusable(true);

		for (int i = 0; i < 6; i++) {

			playerNameLabel[i] = new JTextField("Player " + (i + 1));
			playerNameLabel[i].setPreferredSize(new Dimension(70, 27));
			playerNameLabel[i].setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
			playerNameLabel[i].setCaretPosition(playerNameLabel[i].getText().length());

			playerLeftKeyBtn[i] = new KeySetButton(parent, playerLeftKey, i);
			playerRightKeyBtn[i] = new KeySetButton(parent, playerRightKey, i);

			playerLeftKeyBtn[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					KeySetButton button = (KeySetButton) e.getSource();
					KeySetDialog dialog = button.getDialog();

					dialog.setListener(new KeySetListener() {
						public void keySet(KeyEvent e) {
							button.setKey(e.getKeyCode());
						}
					});

					dialog.setVisible(true);
				}
			});

			playerRightKeyBtn[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					KeySetButton button = (KeySetButton) e.getSource();
					KeySetDialog dialog = button.getDialog();

					dialog.setListener(new KeySetListener() {
						public void keySet(KeyEvent e) {
							button.setKey(e.getKeyCode());
						}
					});

					dialog.setVisible(true);
				}
			});

		}

		startGameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean check = fireStartGame();
				if (check) {
					demoScreen.stop();
					demoScreen.reset();
				}
			}
		});

		playerLeftKeyBtn[0].setKey(KeyEvent.VK_LEFT);
		playerRightKeyBtn[0].setKey(KeyEvent.VK_RIGHT);
		playerLeftKeyBtn[1].setKey(KeyEvent.VK_A);
		playerRightKeyBtn[1].setKey(KeyEvent.VK_D);

		setLayout(new BorderLayout());

		settingPanel.setPreferredSize(new Dimension(400, 800));
		upperPane.setPreferredSize(new Dimension(250, 100));
		lowerPane.setPreferredSize(new Dimension(350, 350));

		Border inner = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Curve Fever",
				TitledBorder.CENTER, TitledBorder.TOP, new Font(Font.DIALOG, Font.BOLD, 50), Color.red);
		Border outer = BorderFactory.createEmptyBorder(30, 10, 10, 10);
		settingPanel.setBorder(BorderFactory.createCompoundBorder(outer, inner));

		upperPane.setBorder(BorderFactory.createTitledBorder("player setup"));

		lowerPane.setBorder(BorderFactory.createTitledBorder("key setup"));

		settingPanel.setLayout(new GridBagLayout());

		////// Setup ComboBox models///////////////////////

		DefaultComboBoxModel<Integer> humanComboBoxModel = new DefaultComboBoxModel<Integer>();
		DefaultComboBoxModel<Integer> botComboBoxModel = new DefaultComboBoxModel<Integer>();

		for (Integer i = 1; i <= 6; i++) {
			humanComboBoxModel.addElement(i);
		}

		for (Integer i = 0; i <= 5; i++) {
			botComboBoxModel.addElement(i);
		}

		humanCombo.setModel(humanComboBoxModel);
		botCombo.setModel(botComboBoxModel);

		humanCombo.setPreferredSize(new Dimension(40, 20));
		botCombo.setPreferredSize(new Dimension(40, 20));

		humanCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				humanCount = (Integer) humanCombo.getSelectedItem();

				Integer start = (humanCount == 1) ? 1 : 0;

				botComboBoxModel.removeAllElements();
				for (Integer i = start; i <= 6 - humanCount; i++) {
					botComboBoxModel.addElement(i);
				}
				
				

				for (int i = 0; i < 6; i++) {
					playerLeftKeyBtn[i].setEnabled((i < humanCount) ? true : false);
					playerRightKeyBtn[i].setEnabled((i < humanCount) ? true : false);
					playerNameLabel[i].setEnabled((i < humanCount) ? true : false);
				}

			}
		});

		botCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botCount = (Integer) botCombo.getSelectedItem();
			}
		});

		humanCombo.setSelectedIndex(0);
		humanCount = (Integer) humanCombo.getSelectedItem();
		botCount = (Integer) botCombo.getSelectedItem();

		layoutComponents();

		add(settingPanel, BorderLayout.EAST);

		demoScreen.setListener(new GameListener() {
			public void gameOver() {
				demoScreen.stop();
			}

			public void scoresUpdated() {
				// TODO Auto-generated method stub
			}

			public void roundOver(Player winner) {
				demoScreen.start();
			}

		});

		add(demoScreen, BorderLayout.CENTER);

		demoScreen.start();

		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				startGameBtn.requestFocusInWindow();
				demoScreen.start();
			}
		});

	}

	public void layoutComponents() {

		GridBagConstraints gc = new GridBagConstraints();

		////////// SettingPanel////////////////////////////////////////////

		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;

		settingPanel.add(upperPane, gc);

		gc.gridy++;

		settingPanel.add(lowerPane, gc);

		gc.gridy++;

		settingPanel.add(startGameBtn, gc);

		////////// UpperPane///////////////////////////////////////////////

		upperPane.setLayout(new GridBagLayout());
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;

		////////// First Row//////////////////////////////////

		gc.gridy = 0;
		gc.weighty = 1;

		gc.gridx = 0;
		gc.weightx = 1;
		gc.anchor = GridBagConstraints.LINE_END;
		upperPane.add(playerLabel, gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.anchor = GridBagConstraints.LINE_START;
		upperPane.add(humanCombo, gc);

		////////// Next Row//////////////////////////////////

		gc.gridy++;
		gc.weighty = 1;

		gc.gridx = 0;
		gc.weightx = 1;
		gc.anchor = GridBagConstraints.LINE_END;
		upperPane.add(botLabel, gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.anchor = GridBagConstraints.LINE_START;
		upperPane.add(botCombo, gc);

		////////// LowerPane///////////////////////////////////////////////

		lowerPane.setLayout(new GridBagLayout());
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;

		////////// First Row//////////////////////////////////

		gc.gridy = 0;
		gc.weighty = 1;

		gc.gridx = 0;
		gc.weightx = 1;
		gc.anchor = GridBagConstraints.SOUTH;
		gc.insets = new Insets(0, 20, 0, 0);
		lowerPane.add(new JLabel("name"), gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.anchor = GridBagConstraints.SOUTH;
		gc.insets = new Insets(0, 0, 0, 0);
		lowerPane.add(new JLabel("left"), gc);

		gc.gridx = 2;
		gc.weightx = 1;
		gc.anchor = GridBagConstraints.SOUTH;
		gc.insets = new Insets(0, 0, 0, 0);
		lowerPane.add(new JLabel("right"), gc);

		////////// Next Rows//////////////////////////////////

		gc.insets = new Insets(0, 0, 0, 0);

		for (int i = 0; i < 6; i++) {
			gc.gridy++;
			gc.weighty = 1;

			gc.gridx = 0;
			gc.weightx = 1;
			gc.anchor = GridBagConstraints.LINE_END;
			gc.insets = new Insets(2, 0, 0, 0);
			lowerPane.add(playerNameLabel[i], gc);

			gc.gridx = 1;
			gc.weightx = 1;
			gc.anchor = GridBagConstraints.CENTER;
			gc.insets = new Insets(0, 0, 0, 0);
			lowerPane.add(playerLeftKeyBtn[i], gc);

			gc.gridx = 2;
			gc.weightx = 1;
			gc.anchor = GridBagConstraints.CENTER;
			gc.insets = new Insets(0, 0, 0, 0);
			lowerPane.add(playerRightKeyBtn[i], gc);

			if (i > humanCount - botCount + 1) {
				playerLeftKeyBtn[i].setEnabled(false);
				playerRightKeyBtn[i].setEnabled(false);
				playerNameLabel[i].setEnabled(false);
			}

		}

	}

	public boolean check() {

		for (int i = 0; i < playerLeftKey.length; i++) {
			if (playerNameLabel[i].isEnabled() && playerLeftKey[i] != 0) {
				for (int j = i + 1; j < playerLeftKey.length; j++) {
					if (playerNameLabel[j].isEnabled() && playerLeftKey[i] == playerLeftKey[j])
						return false;
				}
				for (int j = 0; j < playerRightKey.length; j++) {
					if (playerNameLabel[j].isEnabled() && playerLeftKey[i] == playerRightKey[j])
						return false;
				}
			} else if (playerNameLabel[i].isEnabled()) {
				return false;
			}
		}

		for (int i = 0; i < playerRightKey.length; i++) {
			if (playerNameLabel[i].isEnabled() && playerRightKey[i] != 0) {
				for (int j = 0; j < playerLeftKey.length; j++) {
					if (playerNameLabel[j].isEnabled() && playerRightKey[i] == playerLeftKey[j])
						return false;
				}
				for (int j = i + 1; j < playerLeftKey.length; j++) {
					if (playerNameLabel[j].isEnabled() && playerRightKey[i] == playerRightKey[j])
						return false;
				}
			} else if (playerNameLabel[i].isEnabled()) {
				return false;
			}
		}

		return true;

	}

	public boolean fireStartGame() {
		boolean check = check();

		if (check) {

			int playerCount = humanCount + botCount;
			Player[] player = new Player[playerCount];

			boolean[] colorChosen = new boolean[6];

			for (int i = 0; i < playerCount; i++) {
				Color c = Color.white;
				int code = -1;
				Random rnd = new Random();
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

				String name = playerNameLabel[i].getText();
				if (i < humanCount) {
					player[i] = new Player(false, name, c, playerLeftKey[i], playerRightKey[i]);
				} else {
					player[i] = new Player(true, "Bot " + (i + 1 - humanCount), c, 0, 0);
				}
			}

			listener.gameStarted(player);

		} else {
			JOptionPane.showMessageDialog(parent, "Empty or duplicate key found!", "Error",
					JOptionPane.OK_OPTION | JOptionPane.WARNING_MESSAGE);
		}
		return check;
	}

	public void setListener(StartScreenListener listener) {
		this.listener = listener;
	}

}
