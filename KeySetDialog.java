import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class KeySetDialog extends JDialog {

	private int[] keys;
	private int index;

	private KeySetListener listener;

	public KeySetDialog(JFrame frame, int[] keys, int index) {

		super(frame, "set key", true);

		this.keys = keys;
		this.index = index;

		setSize(170, 100);
		setResizable(false);

		setLocationRelativeTo(frame);

		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.CENTER;

		add(new JLabel("press a key"), gc);

		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			public void keyPressed(KeyEvent e) {
				listener.keySet(e);
				setVisible(false);
			}
		});

	}

	public void setListener(KeySetListener listener) {
		this.listener = listener;
	}

}
