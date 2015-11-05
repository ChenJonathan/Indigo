package indigo.GameState;

import indigo.Manager.ContentManager;
import indigo.Manager.GameStateManager;
import indigo.Manager.Manager;

import java.awt.Graphics2D;

// TODO Everything
/**
 * The state where the game options are displayed. Accessible through the game menu (MenuState).
 */
public class OptionState extends GameState
{
	/**
	 * Sets up the options menu.
	 * 
	 * @param gsm The game state manager.
	 */
	public OptionState(GameStateManager gsm)
	{
		super(gsm);
	}

	@Override
	public void update()
	{
		handleInput();
	}

	/**
	 * Renders the options menu.
	 * 
	 * @param g The graphics to be rendered.
	 */
	@Override
	public void render(Graphics2D g)
	{
		g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, 1920, 1080, null);
		g.drawImage(ContentManager.getImage(ContentManager.BUTTON_BACK), 100, 873, 360, 107, null);
	}

	/**
	 * INCOMPLETE
	 */
	@Override
	public void handleInput()
	{
		if(Manager.input.mouseLeftRelease())
		{
			if(Manager.input.mouseInRect(100, 873, 360, 107))
			{
				gsm.setOptions(false);
			}
		}
	}
}