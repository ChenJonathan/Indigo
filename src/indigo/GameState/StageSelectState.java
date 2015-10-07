package indigo.GameState;

import indigo.Manager.ContentManager;
import indigo.Manager.GameStateManager;
import indigo.Manager.Manager;

import java.awt.Graphics2D;

/**
 * The state that allows the player to select a stage to play. Accessed when they player selects "play" from the main
 * menu.
 */
public class StageSelectState extends GameState
{
	/**
	 * Sets up the stage selection state.
	 * 
	 * @param gsm The game state manager.
	 */
	public StageSelectState(GameStateManager gsm)
	{
		super(gsm);

		// Display unlocked stages based on Data.getUnlockedStages
		// If Data.getStagesToUnlock > 0, run the unlock animation (???)
		// May have to track tick count for this
	}

	@Override
	public void update()
	{
		handleInput();
	}

	@Override
	public void render(Graphics2D g)
	{
		g.drawImage(ContentManager.getImage(ContentManager.STAGE_SELECT_BACKGROUND), 0, 0, 1920, 1080, null);
	}

	/**
	 * Handles the mouse interactions with the stage selections.
	 */
	public void handleInput()
	{
		if(Manager.input.mouseLeftRelease() && Manager.input.mouseX() >= 113 && Manager.input.mouseX() <= 365
				&& Manager.input.mouseY() >= 116 && Manager.input.mouseY() <= 293)
		{
			data.setStage(PlayState.BEACH);
			gsm.setState(GameStateManager.PLAY);
		}
	}
}