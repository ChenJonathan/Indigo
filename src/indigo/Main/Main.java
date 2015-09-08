package indigo.Main;

import javax.swing.JFrame;

public class Main {
	
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