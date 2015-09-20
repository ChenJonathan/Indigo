package indigo.GameState;

import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * The state where the game is paused.  Accessible during play.
 */
public class PauseState extends GameState 
{
    /**
     * Sets up the paused game state.
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
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
		g.drawString("GAME PAUSED", 150, 300);
	}

    @Override
	public void handleInput()
	{
		if(input.keyPress(InputManager.ESCAPE))
		{
			gsm.setPaused(false);
		}
		/*
		 * Detect clicking resume, options, and quit
		 * gsm.setState(GameStateManager.MENU);
		 */
	}
}