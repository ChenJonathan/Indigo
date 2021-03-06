package indigo.Manager;

import indigo.GameState.ClearState;
import indigo.GameState.DesignState;
import indigo.GameState.GameState;
import indigo.GameState.MenuState;
import indigo.GameState.OptionState;
import indigo.GameState.PauseState;
import indigo.GameState.PlayState;
import indigo.GameState.SelectState;
import indigo.GameState.TalentState;

import java.awt.Graphics2D;

/**
 * Manages the state of the game. Game state determines what is currently being updated and rendered.
 */
public class GameStateManager
{
	private Data data;

	private boolean paused;
	private PauseState pauseState;
	private boolean options;
	private OptionState optionState;
	private boolean talents;
	private TalentState talentState;

	private GameState currentState;

	public static final int MENU = 0;
	public static final int SELECT = 1;
	public static final int PLAY = 2;
	public static final int CLEAR = 3;
	public static final int DESIGN = 4;

	/**
	 * Sets up the GameManager with the current game instance.
	 * 
	 * @param game The current game session.
	 */
	public GameStateManager()
	{
		data = new Data();

		paused = false;
		pauseState = new PauseState(this);

		options = false;
		optionState = new OptionState(this);

		talents = false;
		talentState = new TalentState(this);

		setState(MENU);

		SoundManager.stopAll();
		SoundManager.play(ContentManager.TITLE_THEME);
	}

	/**
	 * Changes the game state and removes the previous state.
	 * 
	 * @param state The new game state.
	 */
	public void setState(int state)
	{
		currentState = null;
		ContentManager.dispose();
		if(state == MENU)
		{
			currentState = new MenuState(this);
		}
		else if(state == SELECT)
		{
			currentState = new SelectState(this);
		}
		else if(state == PLAY)
		{
			currentState = new PlayState(this);
		}
		else if(state == CLEAR)
		{
			currentState = new ClearState(this);
		}
		else if(state == DESIGN)
		{
			currentState = new DesignState(this);
		}
	}

	/**
	 * Pauses and unpauses the game.
	 * 
	 * @param paused Whether the game is paused.
	 */
	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}

	/**
	 * Shows options and stops updates.
	 * 
	 * @param options Whether the options screen is to be shown.
	 */
	public void setOptions(boolean options)
	{
		this.options = options;
	}

	/**
	 * Shows talents and stops updates.
	 * 
	 * @param talents Whether the talents screen is to be shown.
	 */
	public void setTalents(boolean talents)
	{
		this.talents = talents;
	}

	/**
	 * Delegates game updates to current game state. Priority is given to "temporary" game states.
	 */
	public void update()
	{
		if(talents)
		{
			talentState.update();
		}
		else if(options)
		{
			optionState.update();
		}
		else if(paused)
		{
			pauseState.update();
		}
		else if(currentState != null)
		{
			currentState.update();
		}
	}

	/**
	 * Renders the current game state. Priority is given to "temporary" game states.
	 * 
	 * @param g The graphics to be rendered.
	 */
	public void render(Graphics2D g)
	{
		if(talents)
		{
			talentState.render(g);
		}
		else if(options)
		{
			optionState.render(g);
		}
		else if(paused)
		{
			pauseState.render(g);
		}
		else if(currentState != null)
		{
			currentState.render(g);
		}
	}

	/**
	 * Relays Data object to GameState objects.
	 * 
	 * @return The data to be relayed.
	 */
	public Data getData()
	{
		return data;
	}
}