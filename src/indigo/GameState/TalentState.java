package indigo.GameState;

import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;

import java.awt.Graphics2D;

/**
 * The state that displays player level, experience, weapon choice, and talent choice. Only one instance is created.
 */
public class TalentState extends GameState
{
	/**
	 * Sets up the talents display.
	 * 
	 * @param gsm The game state manager.
	 */
	public TalentState(GameStateManager gsm)
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

	@Override
	public void handleInput()
	{
		if(input.keyPress(InputManager.ESCAPE))
		{
			gsm.setTalents(false);
		}
		/*
		 * Detect clicking resume gsm.setState(GameStateManager.SELECT);
		 */
	}
}