package indigo.Manager;
import java.awt.event.KeyEvent;
// Change to non-static class
public class InputManager
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
	
	public InputManager() { }
	
	// Previous mouse position is tracked to help check for changes in mouse state
	public void update()
	{
		prevMouseLeftState = mouseLeftState;
		prevMouseRightState = mouseRightState;
		for(int count = 0; count < NUM_KEYS; count++) 
		{
			prevKeyState[count] = keyState[count];
		}
	}
	
	// Changes boolean array based on which keys are pressed
	public void keySet(int key, boolean state)
	{
		if(key == KeyEvent.VK_1) keyState[K1] = state;
		else if(key == KeyEvent.VK_2) keyState[K2] = state;
		else if(key == KeyEvent.VK_3) keyState[K3] = state;
		else if(key == KeyEvent.VK_4) keyState[K4] = state;
		else if(key == KeyEvent.VK_W) keyState[W] = state;
		else if(key == KeyEvent.VK_A) keyState[A] = state;
		else if(key == KeyEvent.VK_S) keyState[S] = state;
		else if(key == KeyEvent.VK_D) keyState[D] = state;
		else if(key == KeyEvent.VK_Q) keyState[Q] = state;
		else if(key == KeyEvent.VK_E) keyState[E] = state;
		else if(key == KeyEvent.VK_SPACE) keyState[SPACE] = state;
		else if(key == KeyEvent.VK_SHIFT) keyState[SHIFT] = state;
		else if(key == KeyEvent.VK_ESCAPE) keyState[ESCAPE] = state;
	}
	
	// Changes boolean based on whether mouse is pressed
	public void mouseLeftSet(boolean state)
	{
		mouseLeftState = state;
	}
	
	// Changes boolean based on whether mouse is pressed
	public void mouseRightSet(boolean state)
	{
		mouseRightState = state;
	}
	
	// Changes int values representing mouse position
	public void mouseSet(int x, int y)
	{
		mouseX = x;
		mouseY = y;
	}
	
	// Checks if the mouse is currently pressed
	public boolean mouseLeftDown()
	{
		return mouseLeftState;
	}
	
	// Checks if mouse has been recently pressed
	public boolean mouseLeftPress()
	{
		return mouseLeftState && !prevMouseLeftState;
	}
	
	// Checks if mouse has been recently released
	public boolean mouseLeftRelease()
	{
		return !mouseLeftState && prevMouseLeftState;
	}
	
	// Checks if the mouse is currently pressed
	public boolean mouseRightDown()
	{
		return mouseRightState;
	}
	
	// Checks if mouse has been recently pressed
	public boolean mouseRightPress()
	{
		return mouseRightState && !prevMouseRightState;
	}
	
	// Checks if mouse has been recently released
	public boolean mouseRightRelease()
	{
		return !mouseRightState && prevMouseRightState;
	}
	
	// Returns x position of mouse
	public int mouseX()
	{
		return mouseX;
	}
	
	// Returns y position of mouse
	public int mouseY()
	{
		return mouseY;
	}
	
	// Checks if key is pressed
	public boolean keyDown(int i)
	{
		return keyState[i];
	}
	
	// Checks if a key was pressed recently
	public boolean keyPress(int i)
	{
		return keyState[i] && !prevKeyState[i];
	}
	
	// Checks if a key was pressed recently
	public boolean keyRelease(int i)
	{
		return !keyState[i] && prevKeyState[i];
	}
}