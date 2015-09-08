package indigo.GameState;

import indigo.Manager.Data;
import indigo.Manager.GameStateManager;
import indigo.Manager.InputManager;
import indigo.Manager.SoundManager;

import java.awt.Graphics2D;

public abstract class GameState
{
	protected Data data;
	protected GameStateManager gsm;
	protected InputManager input;
	protected SoundManager sound;
	
	public GameState(GameStateManager gsm)
	{
		this.gsm = gsm;
		data = gsm.getData();
		input = gsm.getInputManager();
		sound = gsm.getSoundManager();
	}
	
	public abstract void update();
	public abstract void render(Graphics2D g);
	public abstract void handleInput();
}