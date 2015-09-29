package indigo.Manager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Handles all of the keyboard and mouse input for the application.
 */
public class InputManager implements KeyListener, MouseListener, MouseMotionListener
{
	public static final int NUM_KEYS = 13;

	private boolean keyState[] = new boolean[NUM_KEYS];
	private boolean prevKeyState[] = new boolean[NUM_KEYS];
	private boolean mouseLeftState = false;
	private boolean prevMouseLeftState = false;
	private boolean mouseRightState = false;
	private boolean prevMouseRightState = false;

	private int mouseX = 0;
	private int mouseY = 0;

	public static final int K1 = 0;
	public static final int K2 = 1;
	public static final int K3 = 2;
	public static final int K4 = 3;
	public static final int W = 4;
	public static final int A = 5;
	public static final int S = 6;
	public static final int D = 7;
	public static final int Q = 8;
	public static final int E = 9;
	public static final int SPACE = 10;
	public static final int SHIFT = 11;
	public static final int ESCAPE = 12;

	/**
	 * Default constructor.
	 */
	public InputManager()
	{
	}
	
	/**
	 * Updates mouse and key states. Previous mouse position is tracked to help check for changes in mouse state.
	 */
	public void update()
	{
		prevMouseLeftState = mouseLeftState;
		prevMouseRightState = mouseRightState;
		for(int count = 0; count < NUM_KEYS; count++)
		{
			prevKeyState[count] = keyState[count];
		}
	}

	/**
	 * Changes boolean array based on which keys are pressed.
	 * 
	 * @param key The key in question.
	 * @param state Whether the key is pressed.
	 */
	public void keySet(int key, boolean state)
	{
		if(key == KeyEvent.VK_1)
			keyState[K1] = state;
		else if(key == KeyEvent.VK_2)
			keyState[K2] = state;
		else if(key == KeyEvent.VK_3)
			keyState[K3] = state;
		else if(key == KeyEvent.VK_4)
			keyState[K4] = state;
		else if(key == KeyEvent.VK_W)
			keyState[W] = state;
		else if(key == KeyEvent.VK_A)
			keyState[A] = state;
		else if(key == KeyEvent.VK_S)
			keyState[S] = state;
		else if(key == KeyEvent.VK_D)
			keyState[D] = state;
		else if(key == KeyEvent.VK_Q)
			keyState[Q] = state;
		else if(key == KeyEvent.VK_E)
			keyState[E] = state;
		else if(key == KeyEvent.VK_SPACE)
			keyState[SPACE] = state;
		else if(key == KeyEvent.VK_SHIFT)
			keyState[SHIFT] = state;
		else if(key == KeyEvent.VK_ESCAPE)
			keyState[ESCAPE] = state;
	}
	
	/**
	 * Not used.
	 * 
	 * @param key The key pressed.
	 */
	@Override
	public void keyTyped(KeyEvent key){}

	/**
	 * Detects key press.
	 * 
	 * @param key The key being pressed.
	 */
	@Override
	public void keyPressed(KeyEvent key)
	{
		this.keySet(key.getKeyCode(), true);
	}

	/**
	 * Detects key release.
	 * 
	 * @param key The key being released.
	 */
	@Override
	public void keyReleased(KeyEvent key)
	{
		this.keySet(key.getKeyCode(), false);
	}
	
	/**
	 * Checks if key is pressed.
	 * 
	 * @param i The id of the key in question.
	 * @return Whether that key is pressed.
	 */
	public boolean keyDown(int i)
	{
		return keyState[i];
	}

	/**
	 * Checks if a key was pressed recently.
	 * 
	 * @param i The id of the key in question.
	 * @return Whether that key was pressed recently.
	 */
	public boolean keyPress(int i)
	{
		return keyState[i] && !prevKeyState[i];
	}

	/**
	 * Checks if a key was released recently.
	 * 
	 * @param i The id of the key in question.
	 * @return Whether that key was released recently.
	 */
	public boolean keyRelease(int i)
	{
		return !keyState[i] && prevKeyState[i];
	}
	
	/**
	 * Not used.
	 * 
	 * @param e The current mouse action.
	 */
	@Override
	public void mouseEntered(MouseEvent e){}

	/**
	 * Not used.
	 * 
	 * @param e The current mouse action.
	 */
	@Override
	public void mouseExited(MouseEvent e){}

	/**
	 * Detects mouse click.
	 * 
	 * @param e The current mouse action.
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			this.mouseRightSet(true);
		}
		else
		{
			this.mouseLeftSet(true);
		}
	}

	/**
	 * Detects mouse release.
	 * 
	 * @param e The current mouse action.
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			this.mouseRightSet(false);
		}
		else
		{
			this.mouseLeftSet(false);
		}
	}

	/**
	 * Not used.
	 * 
	 * @param e The current mouse action.
	 */
	@Override
	public void mouseClicked(MouseEvent e){}

	/**
	 * Handles the mouse being dragged. Interpreted same as moving.
	 * 
	 * @param e The current mouse action.
	 */
	@Override
	public void mouseDragged(MouseEvent e)
	{
		this.mouseSet(e.getX(), e.getY());
	}

	/**
	 * Handles the mouse being moved. Interpreted same as dragging.
	 * 
	 * @param e The current mouse action.
	 */
	@Override
	public void mouseMoved(MouseEvent e)
	{
		this.mouseSet(e.getX(), e.getY());
	}

	/**
	 * Tracks whether mouse-left is pressed.
	 * 
	 * @param state Whether mouse-left is pressed.
	 */
	public void mouseLeftSet(boolean state)
	{
		mouseLeftState = state;
	}

	/**
	 * Tracks whether mouse-right is pressed.
	 * 
	 * @param state Whether mouse-right is pressed.
	 */
	public void mouseRightSet(boolean state)
	{
		mouseRightState = state;
	}

	/**
	 * Tracks mouse position.
	 * 
	 * @param x The current x-value of the mouse.
	 * @param y The current y-value of the mouse.
	 */
	public void mouseSet(int x, int y)
	{
		mouseX = x;
		mouseY = y;
	}

	/**
	 * @return Whether the left mouse button is currently pressed.
	 */
	public boolean mouseLeftDown()
	{
		return mouseLeftState;
	}

	/**
	 * @return Whether the left mouse button has been recently pressed.
	 */
	public boolean mouseLeftPress()
	{
		return mouseLeftState && !prevMouseLeftState;
	}

	/**
	 * @return Whether the left mouse button has been recently released.
	 */
	public boolean mouseLeftRelease()
	{
		return !mouseLeftState && prevMouseLeftState;
	}

	/**
	 * @return Whether the right mouse button is currently pressed.
	 */
	public boolean mouseRightDown()
	{
		return mouseRightState;
	}

	/**
	 * @return Whether the right mouse button has been recently pressed.
	 */
	public boolean mouseRightPress()
	{
		return mouseRightState && !prevMouseRightState;
	}

	/**
	 * @return Whether the right mouse button has been recently released.
	 */
	public boolean mouseRightRelease()
	{
		return !mouseRightState && prevMouseRightState;
	}

	/**
	 * @return Whether either mouse button is currently pressed.
	 */
	public boolean mouseDown()
	{
		return mouseLeftDown() || mouseRightDown();
	}

	/**
	 * @return Whether either mouse button has been recently pressed.
	 */
	public boolean mousePress()
	{
		return mouseLeftPress() || mouseRightPress();
	}

	/**
	 * @return The x-position of the mouse.
	 */
	public int mouseX()
	{
		return mouseX;
	}

	/**
	 * @return The y-position of the mouse.
	 */
	public int mouseY()
	{
		return mouseY;
	}
}