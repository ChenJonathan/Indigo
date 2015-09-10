package indigo.GameState;

import indigo.Manager.Content;
import indigo.Manager.GameStateManager;

import java.awt.Graphics2D;

public class StageSelectState extends GameState 
{
	public StageSelectState(GameStateManager gsm)
	{
		super(gsm);
		
		// Display unlocked stages based on Data.getUnlockedStages
		// If Data.getStagesToUnlock > 0, run the unlock animation (???)
		// May have to track tick count for this
	}
	
	public void update()
	{
		handleInput();
	}
	
	public void render(Graphics2D g)
	{
		g.drawImage(Content.STAGE_SELECT_BACKGROUND, 0, 0, 1920, 1080, null);
	}
	
	public void handleInput()
	{
		if(input.mouseRelease() && input.mouseX() >= 113 && input.mouseX() <= 365 && input.mouseY() >= 116 && input.mouseY() <= 293)
		{
			data.setStage(PlayState.BEACH);
			gsm.setState(GameStateManager.PLAY);
		}
	}
}