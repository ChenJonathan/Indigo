package indigo.GameState;

import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class PauseState extends GameState 
{
	public PauseState(GameStateManager gsm)
	{
		super(gsm);
	}
	
	public void update()
	{
		handleInput();
	}
	
	public void render(Graphics2D g)
	{
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
		g.drawString("GAME PAUSED", 150, 300);
	}
	
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