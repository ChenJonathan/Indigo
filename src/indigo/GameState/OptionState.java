package indigo.GameState;

import indigo.Manager.GameStateManager;

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
	 * INCOMPLETE
	 * 
	 * @param g The graphics to be rendered.
	 */
	@Override
	public void render(Graphics2D g)
	{
		// Draw things
	}

	/**
	 * INCOMPLETE
	 */
	@Override
	public void handleInput()
	{
		/*
		 * Check checkboxes based on selected options Change state on "back"
		 */
	}
}