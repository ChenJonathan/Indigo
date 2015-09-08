package indigo.GameState;

import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;

import java.awt.Graphics2D;

// Tracks player level, experience, weapon choice, and talent choice
// Only one instance is created
public class TalentState extends GameState 
{	
	public TalentState(GameStateManager gsm)
	{
		super(gsm);
	}
	
	public void update()
	{
		handleInput();
	}
	
	public void render(Graphics2D g)
	{
		//Draw things
	}
	
	public void handleInput()
	{
		if(input.keyPress(InputManager.ESCAPE))
		{
			gsm.setTalents(false);
		}
		/*
		 * Detect clicking resume
		 * gsm.setState(GameStateManager.SELECT);
		 */
	}
}