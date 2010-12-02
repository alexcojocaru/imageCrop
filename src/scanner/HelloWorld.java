package scanner;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import SK.gnome.morena.Morena;
import SK.gnome.morena.MorenaException;
import SK.gnome.morena.MorenaImage;
import SK.gnome.morena.MorenaSource;
import SK.gnome.twain.TwainManager;

public class HelloWorld {
	public static void main(String[] args) throws MorenaException {
		// initialize the morena source
		MorenaSource source = TwainManager.selectSource(null);
							//Morena.selectSource(null);
		
		System.out.printf("Selected source is %1s\n", source);
		if (source != null) {
			// hide the scanner interface
			source.setVisible(false);
			
			// and set the source parameters to get the most out of it
			source.setColorMode();
			source.setBitDepth(24);
			source.setContrast(0);
			source.setResolution(300);
			
			System.out.printf("Resolution: %1.0f; bit depth: %2d;" +
					" contrast: %3.2f\n",
					source.getResolution(),
					source.getBitDepth(),
					source.getContrast());
			
			// and scan
			MorenaImage morenaImage = new MorenaImage(source);
			
			// now that we have the scanned image, lets convert it
			if (morenaImage.getWidth() > 0 || morenaImage.getHeight() > 0) {
				Image image = Toolkit.getDefaultToolkit().createImage(morenaImage);
				BufferedImage bImage = new BufferedImage(
						image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
				bImage.createGraphics().drawImage(image, 0, 0, null);
			}
			
		}
		
		Morena.close();
	}

}
