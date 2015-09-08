package indigo.Phase;

import indigo.GameState.PlayState;
import indigo.Manager.InputManager;
import indigo.Skill.Geyser;

public class Water extends Phase
{
	public int attackDelay = 6; // Delay between attacks when not crouched // TODO Revert after demonstration
	private final int attackDelayFocused = 3; // Delay between attacks when crouched
	
	public static final int GEYSER = 0;
	public static final int MIST = 1;
	public static final int TORRENT = 2;
	public static final int VORTEX = 3;
	
	public Water(PlayState playState)
	{
		super(playState);
		id = Phase.WATER;
		
		maxCooldowns = new int[] {0, 0, 0, 0};
		
		skills[GEYSER] = new Geyser(this);
		// TODO Add other skills to skills array
	}
	
	public boolean canNormalAttack()
	{
		if(player.canAttack())
		{
			if(player.isCrouching() && playState.getTime() - attackStartTime >= attackDelayFocused)
			{
				return true;
			}
			else if(playState.getTime() - attackStartTime >= attackDelay)
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean canShift()
	{
		// Makes sure a direction is selected // TODO Don't hard code
		boolean directionSelected = input.keyDown(InputManager.W) || input.keyDown(InputManager.S) || input.keyDown(InputManager.A) || input.keyDown(InputManager.D);
		return player.canAttack() && player.canMove() && player.getStamina() >= 0.1 && directionSelected;
	}
}