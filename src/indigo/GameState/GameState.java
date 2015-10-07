package indigo.GameState;

import indigo.Manager.Data;
import indigo.Manager.GameStateManager;

import java.awt.Graphics2D;

/**
 * Template for game states.
 */
public abstract class GameState
{
	protected Data data;
	protected GameStateManager gsm;

	/**
	 * Sets up the game state and passes in the game state manager, allowing for control of game data, input, and sound.
	 * 
	 * @param gsm The game state manager.
	 */
	public GameState(GameStateManager gsm)
	{
		this.gsm = gsm;
		data = gsm.getData();
	}

	/**
	 * The running game loop, called continuously to manages changes during the state.
	 */
	public abstract void update();

	/**
	 * Renders the graphics with each update to the game state.
	 * 
	 * @param g The graphics to be rendered.
	 */
	public abstract void render(Graphics2D g);

	/**
	 * Manages the input with the help of the input manager.
	 */
	public abstract void handleInput();
}