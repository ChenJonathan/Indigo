package indigo.Main;

import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

@SuppressWarnings("serial")
public class Game extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener
{
	public static final int WIDTH = 1366;//1920;
	public static final int HEIGHT = 768;//WIDTH / 16 * 9;
	public static final int CURSOR_WIDTH = 32;
	public static final int CURSOR_HEIGHT = 32;
	
	public final int FPS = 30;
	private final int TARGET_TIME = 1000 / FPS;
	private Thread thread;
	private boolean running = false;
	
	private BufferedImage image;
	private Graphics2D g;
	
	private GameStateManager gsm;
	private InputManager input;
	
	// Constructs game panel
	public Game()
	{
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
		
		running = true;
		image = new BufferedImage(WIDTH, HEIGHT, 1);
		g = (Graphics2D)image.getGraphics();
		input = new InputManager();
		gsm = new GameStateManager(this);
	}
	
	// Called when Game object is added to a container
	public void addNotify()
	{
		super.addNotify();
		if(thread == null)
		{
			addKeyListener(this);
			addMouseListener(this);
			addMouseMotionListener(this);
			thread = new Thread(this);
			thread.start();
		}
	}
	
	// Main game loop that aims for 30 FPS
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
	
	// Updates based on current game state and checks for inputs
	private void update()
	{
		gsm.update();
		input.update();
	}
	
	// Draws based on current game state
	private void render()
	{
		gsm.render(g);
	}
	
	// Buffer strategy
	private void draw()
	{
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
		g2.dispose();
	}
	
	// Not used
	public void keyTyped(KeyEvent key)
	{
	}
	
	// Detects key press
	public void keyPressed(KeyEvent key)
	{
		input.keySet(key.getKeyCode(), true);
	}
	
	// Detects key release
	public void keyReleased(KeyEvent key)
	{
		input.keySet(key.getKeyCode(), false);
	}
	
	// Not used
	public void mouseEntered(MouseEvent e)
	{}
	
	// Not used
	public void mouseExited(MouseEvent e)
	{}
	
	// Detects mouse click
	public void mousePressed(MouseEvent e)
	{
		input.mouseSet(true);
		if(e.getButton() == MouseEvent.BUTTON3){
			input.rightClickSet(true);
		}else{
			input.rightClickSet(false);
		}
	}
	
	// Detects mouse release
	public void mouseReleased(MouseEvent e)
	{
		input.mouseSet(false);
	}
	
	// Not used
	public void mouseClicked(MouseEvent e)
	{}
	
	public void mouseDragged(MouseEvent e)
	{
		input.mouseSet(e.getX(), e.getY());
	}
	
	public void mouseMoved(MouseEvent e)
	{
		input.mouseSet(e.getX(), e.getY());
	}
	
	public void setCursor(BufferedImage image)
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Cursor c = toolkit.createCustomCursor(image, new Point(CURSOR_WIDTH / 2, CURSOR_HEIGHT / 2), "Cursor");
		setCursor(c);
	}
	
	public InputManager getInput()
	{
		return input;
	}
}