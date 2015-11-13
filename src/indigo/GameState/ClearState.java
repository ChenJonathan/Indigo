package indigo.GameState;

import indigo.Manager.ContentManager;
import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;
import indigo.Manager.Manager;
import indigo.Manager.SoundManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * The state present when the player has ended a state. Shown after each level. Can display a variety of information
 * based on the stage; retrieves information from Data.
 */
public class ClearState extends GameState
{
	/**
	 * Sets up the clear stage state.
	 * 
	 * @param gsm The game state manager.
	 */
	public ClearState(GameStateManager gsm)
	{
		super(gsm);

		if(data.getAutosave())
		{
			data.save(data.getCurrentSlot());
		}
	}

	@Override
	public void update()
	{
		handleInput();
	}

	@Override
	public void render(Graphics2D g)
	{
		g.drawImage(ContentManager.getImage(ContentManager.MENU_BACKGROUND), 0, 0, null);
		
		String message = "";
		if(data.getVictory())
		{
			message = "STAGE CLEAR";
		}
		else
		{
			message = data.getDeathMessage();
		}
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 72));
		g.drawString(message, 400, 500);
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
		g.drawString("Time elapsed: " + Math.round(data.getClearTime() / 30.0) + " s", 400, 600);
	}

	@Override
	public void handleInput()
	{
		// Change to whatever
		if(Manager.input.keyPress(InputManager.ESCAPE))
		{
			gsm.setState(GameStateManager.MENU);

			SoundManager.stopAll();
			SoundManager.play(ContentManager.TITLE_THEME);
		}
		// TODO If the Data.victory is true and stage number is equal to Data.unlockedStages, increase
		// Data.stagesToUnlock by one
	}
}