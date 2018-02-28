import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Utils {

	public static void sort(Player[] player) {

		for (int i = 0; i < player.length - 1; i++) {
			for (int j = 0; j < player.length - i - 1; j++) {
				int val1 = player[j].score;
				int val2 = player[j + 1].score;
				if (val2 > val1) {
					swap(player, j, j + 1);
				}
			}
		}

	}

	public static void swap(Player[] arr, int i, int j) {
		Player tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}

	public static BufferedImage createImage(String path) {
		URL url = Object.class.getResource(path);

		if (url == null) {
			System.out.println("unable to load image");
		}

		BufferedImage img = null;
		try {
			img = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return img;
	}

}
