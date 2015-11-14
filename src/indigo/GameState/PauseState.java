package indigo.GameState;

import indigo.Manager.ContentManager;
import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;
import indigo.Manager.Manager;
import indigo.Manager.SoundManager;

import java.awt.Graphics2D;

/**
 * The state where the game is paused. Accessible during play.
 */
public class PauseState extends GameState
{
	/**
	 * Sets up the paused game state.
	 * 
	 * @param gsm The game state manager.
	 */
	public PauseState(GameStateManager gsm)
	{
		super(gsm);
	}

	@Override
	public void update()
	{
		handleInput();
	}

	@Override
	public void render(Graphics2D g)
	{
		// Draw pause menu and buttons
		g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, null);
		g.drawImage(ContentManager.getImage(ContentManager.PAUSE), 675, 150, null);
		if(Manager.input.mouseInRect(373, 500, 358, 106))
		{
			g.drawImage(ContentManager.getImage(Manager.input.mouseLeftDown()? ContentManager.GLOW_RECTANGLE_CLICK
					: ContentManager.GLOW_RECTANGLE_HOVER), 343, 470, null);
		}
		else if(Manager.input.mouseInRect(781, 500, 358, 106))
		{
			g.drawImage(ContentManager.getImage(Manager.input.mouseLeftDown()? ContentManager.GLOW_RECTANGLE_CLICK
					: ContentManager.GLOW_RECTANGLE_HOVER), 751, 470, null);
		}
		else if(Manager.input.mouseInRect(1189, 500, 358, 106))
		{
			g.drawImage(ContentManager.getImage(Manager.input.mouseLeftDown()? ContentManager.GLOW_RECTANGLE_CLICK
					: ContentManager.GLOW_RECTANGLE_HOVER), 1159, 470, null);
		}
		g.drawImage(ContentManager.getImage(ContentManager.BUTTON_RESUME), 373, 500, null);
		g.drawImage(ContentManager.getImage(ContentManager.BUTTON_OPTIONS), 781, 500, null);
		g.drawImage(ContentManager.getImage(ContentManager.BUTTON_QUIT), 1189, 500, null);
	}

	@Override
	public void handleInput()
	{
		if(Manager.input.mouseLeftRelease())
		{
			if(Manager.input.mouseInRect(373, 500, 358, 106))
			{
				gsm.setPaused(false);
			}
			else if(Manager.input.mouseInRect(781, 500, 358, 106))
			{
				gsm.setOptions(true);
			}
			else if(Manager.input.mouseInRect(1189, 500, 358, 106))
			{
				gsm.setState(GameStateManager.MENU);
				gsm.setPaused(false);

				SoundManager.stopAll();
				SoundManager.play(ContentManager.TITLE_THEME);
			}
		}
		if(Manager.input.keyPress(InputManager.ESCAPE))
		{
			gsm.setPaused(false);
		}
	}
}