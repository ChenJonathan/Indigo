package indigo.Main;

import indigo.Manager.GameStateManager;
import indigo.Manager.Manager;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * Sets up the application as a game. Mostly does scary swing stuff.
 */
@SuppressWarnings("serial")
public class Game extends JPanel implements Runnable
{
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	public static final int DEFAULT_WIDTH = 1920;
	public static final int DEFAULT_HEIGHT = 1080;
	public static final int CURSOR_WIDTH = 32;
	public static final int CURSOR_HEIGHT = 32;

	public final int FPS = 30;
	private final int TARGET_TIME = 1000 / FPS;
	private Thread thread;
	private boolean running = false;

	private BufferedImage image;
	private Graphics2D g;

	private GameStateManager gsm;

	/**
	 * Constructs game panel.
	 */
	public Game()
	{
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();

		running = true;
		image = new BufferedImage(WIDTH, HEIGHT, 1);
		g = (Graphics2D)image.getGraphics();
		g.scale((double)WIDTH / DEFAULT_WIDTH, (double)HEIGHT / DEFAULT_HEIGHT);
		gsm = new GameStateManager(this);
	}

	/**
	 * Called when Game object is added to a container.
	 */
	@Override
	public void addNotify()
	{
		super.addNotify();
		if(thread == null)
		{
			addKeyListener(Manager.input);
			addMouseListener(Manager.input);
			addMouseMotionListener(Manager.input);
			thread = new Thread(this);
			thread.start();
		}
	}

	/**
	 * Main game loop that aims for 30 FPS.
	 */
	@Override
	public void run()
	{
		long elapsedTotal = 0; // For counter
		long ticks = 0; // For counter
		long skips = 0; // For counter

		long start;
		long elapsed;
		long wait;

		while(running)
		{
			start = System.currentTimeMillis();

			update();
			render();
			draw();

			elapsed = System.currentTimeMillis() - start;
			elapsedTotal += elapsed;

			wait = TARGET_TIME - elapsed;
			while(wait < 0)
			{
				wait += TARGET_TIME;
				skips++;
				System.out.println("* Frameskip " + skips + " *");
			}

			try
			{
				Thread.sleep(wait);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			ticks++; // For counter
			if(ticks % FPS == 0) // For counter
			{
				System.out.println("Resources used: " + (100 * elapsedTotal / (TARGET_TIME * FPS)) + "%");
				elapsedTotal = 0;
			}
		}
	}

	/**
	 * Updates based on current game state and checks for inputs.
	 */
	private void update()
	{
		gsm.update();
		Manager.input.update();
	}

	/**
	 * Draws based on current game state.
	 */
	private void render()
	{
		gsm.render(g);
	}

	/**
	 * Buffer strategy.
	 */
	private void draw()
	{
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
		g2.dispose();
	}

	/**
	 * Creates the custom cursor to be used.
	 * 
	 * @param image The image for the cursor.
	 */
	public void setCursor(BufferedImage image)
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Cursor c = toolkit.createCustomCursor(image, new Point(CURSOR_WIDTH / 2, CURSOR_HEIGHT / 2), "Cursor");
		setCursor(c);
	}
}