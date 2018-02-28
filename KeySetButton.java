import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

public class KeySetButton extends JButton {

	private KeySetDialog setDialog;
	private int[] keys;
	private int index;

	public KeySetButton(JFrame parent, int[] keys, int index) {

		super("set");
		
		this.keys = keys;
		this.index = index;
		
		setDialog = new KeySetDialog(parent, keys, index);

	}

	public void setKey(int key) {

		String keyText = KeyEvent.getKeyText(key);

		if(key==KeyEvent.VK_ESCAPE || key==KeyEvent.VK_SPACE){
			keys[index]=0;
			setText("set");
		} else {
			keys[index]=key;
			setText(keyText);
		}

	}

	public KeySetDialog getDialog() {
		return setDialog;
	}

}
