package indigo.GameState;

import indigo.Manager.GameStateManager;

import java.awt.Graphics2D;

// TODO Everything
public class OptionState extends GameState 
{
	public OptionState(GameStateManager gsm)
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
		/*
		 * Check checkboxes based on selected options
		 * Change state on "back"
		 */
	}
}