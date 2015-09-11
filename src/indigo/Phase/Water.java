package indigo.Phase;

import indigo.GameState.PlayState;
import indigo.Manager.InputManager;
import indigo.Skill.EmptySkill;
import indigo.Skill.Geyser;

public class Water extends Phase
{
	public int attackDelay = 6; // Delay between attacks when not crouched // TODO Revert after demonstration
	private final int attackDelayFocused = 3; // Delay between attacks when crouched
	
	public Water(PlayState playState)
	{
		super(playState);
		id = Phase.WATER;
		
		maxCooldowns = new int[] {0, 0, 0, 0};
		
		skills[0] = new Geyser(this, 0);
		skills[1] = new EmptySkill(this, 1);
		skills[2] = new EmptySkill(this, 2);
		skills[3] = new EmptySkill(this, 3);
		// TODO Implement locked skills
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