package indigo.Manager;

import indigo.GameState.ClearStageState;
import indigo.GameState.GameState;
import indigo.GameState.MenuState;
import indigo.GameState.OptionState;
import indigo.GameState.PauseState;
import indigo.GameState.PlayState;
import indigo.GameState.StageSelectState;
import indigo.GameState.TalentState;
import indigo.Main.Game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Manages the state of the game.  Game state determines what is currently being
 * updated and rendered.
 */
public class GameStateManager
{
	private Game game;
	private Data data;
	private InputManager input;
	private SoundManager sound;
	
	private boolean paused;
	private PauseState pauseState;
	private boolean options;
	private OptionState optionState;
	private boolean talents;
	private TalentState talentState;
	
	private GameState[] gameStates;
	private int currentState;
	
	public static final int NUM_STATES = 4;
	public static final int MENU = 0;
	public static final int SELECT = 1;
	public static final int PLAY = 2;
	public static final int CLEAR = 3;

    /**
     * Sets up the GameManager with the current game instance.
     * @param game The current game session.
     */
	public GameStateManager(Game game)
	{
		this.game = game;
		data = new Data();
		input = game.getInput();
		sound = new SoundManager();
		
		paused = false;
		pauseState = new PauseState(this);
		
		options = false;
		optionState = new OptionState(this);
		
		talents = false;
		talentState = new TalentState(this);
		
		gameStates = new GameState[NUM_STATES];
		setState(MENU);
	}

    /**
     * Changes the game state and removes the previous state.
     * @param state The new game state.
     */
	public void setState(int state)
	{
		gameStates[currentState] = null;
		currentState = state;
		if(state == MENU)
		{
			gameStates[state] = new MenuState(this);
		}
		else if(state == SELECT)
		{
			gameStates[state] = new StageSelectState(this);
		}
		else if(state == PLAY)
		{
			gameStates[state] = new PlayState(this);
		}
		else if(state == CLEAR)
		{
			gameStates[state] = new ClearStageState(this);
		}
	}

    /**
     * Pauses and unpauses the game.
     * @param paused Whether the game is paused.
     */
	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}

    /**
     * Shows options and stops updates.
     * @param options Whether the options screen is to be shown.
     */
	public void setOptions(boolean options)
	{
		this.options = options;
	}

    /**
     * Shows talents and stops updates.
     * @param talents Whether the talents screen is to be shown.
     */
	public void setTalents(boolean talents)
	{
		this.talents = talents;
	}

    /**
     * Delegates game updates to current game state.  Priority is given to
     * "temporary" game states.
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
		else if(gameStates[currentState] != null)
		{
			gameStates[currentState].update();
		}
	}

    /**
     * Renders the current game state.  Priority is given to "temporary" game
     * states.
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
		else if(gameStates[currentState] != null)
		{
			gameStates[currentState].render(g);
		}
	}

    /**
     * Relays Data object to GameState objects.
     * @return The data to be relayed.
     */
	public Data getData()
	{
		return data;
	}

    /**
     * Relays InputManager object to GameState objects.
     * @return The input to be relayed.
     */
	public InputManager getInputManager()
	{
		return input;
	}

    /**
     * Relays SoundManager object to GameState objects.
     * @return The sound to be relayed.
     */
	public SoundManager getSoundManager()
	{
		return sound;
	}

    /**
     * Sets the cursor to a new image.
     * @param image The new cursor image.
     */
	public void setCursor(BufferedImage image)
	{
		game.setCursor(image);
	}
}