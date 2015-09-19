package indigo.GameState;

import indigo.Manager.Data;
import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;
import indigo.Manager.SoundManager;

import java.awt.Graphics2D;

/**
 * Template for game states.
 */
public abstract class GameState
{
	protected Data data;
	protected GameStateManager gsm;
	protected InputManager input;
	protected SoundManager sound;

    /**
     * Sets up the game state and passes in the game state manager, allowing
     * for control of game data, input, and sound.
     * @param gsm The game state manager.
     */
	public GameState(GameStateManager gsm)
	{
		this.gsm = gsm;
		data = gsm.getData();
		input = gsm.getInputManager();
		sound = gsm.getSoundManager();
	}

    /**
     * The running game loop, called continuously to manages changes during the
     * state.
     */
	public abstract void update();

    /**
     * Renders the graphics with each update to the game state.
     * @param g The graphics to be rendered.
     */
	public abstract void render(Graphics2D g);

    /**
     * Manages the input with the help of the input manager.
     */
	public abstract void handleInput();
}