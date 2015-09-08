package indigo.Skill;

import indigo.Entity.Player;
import indigo.GameState.PlayState;
import indigo.Manager.InputManager;
import indigo.Phase.Phase;

public abstract class Skill
{
	protected PlayState playState;
	protected InputManager input;
	protected Phase phase;
	
	protected Player player;
	protected int id; // Initialize in each skill constructor
	
	protected int castTime;
	
	public Skill(Phase phase)
	{
		playState = phase.getPlayState();
		input = phase.getInput();
		this.phase = phase;
		player = phase.getPlayer();
		
		castTime = -1;
	}
	
	public void update()
	{
		castTime++;
	}
	
	public abstract boolean canCast();
	
	public void endCast()
	{
		castTime = -1;
		phase.endCast(id); // Resets skill icon
	}
}