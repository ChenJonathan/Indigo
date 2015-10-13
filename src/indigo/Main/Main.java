package indigo.Main;

import javax.swing.JFrame;

/**
 * Main class to run the application; sets up swing stuff to make the game run.
 */
public class Main
{
	/**
	 * The main method.
	 * 
	 * @param args Currently unused.
	 */
	public static void main(String[] args)
	{
	    
		JFrame window = new JFrame("Indigo");

		window.setUndecorated(true);
		window.add(new Game());

		window.setResizable(false);
		window.pack();

		window.setLocationRelativeTo(null);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}