package indigo.Phase;

import indigo.Entity.Player;
import indigo.GameState.PlayState;
import indigo.Manager.InputManager;
import indigo.Manager.Manager;
import indigo.Skill.Geyser;
import indigo.Skill.LockedSkill;
import indigo.Skill.ManaChannelling;
import indigo.Skill.Pulse;
import indigo.Skill.Skill;

public class Water extends Phase
{
	public int attackDelay = 6; // Delay between attacks when not crouched // TODO Revert cheat mode
	private int attackDelayFocused = 3; // Delay between attacks when crouched

	public Water(PlayState playState)
	{
		super(playState);
		id = Phase.WATER;

		maxCooldowns = new int[] { 150, 150, 150, 150 };

		skills[0] = new LockedSkill(this, 0);
		skills[1] = new LockedSkill(this, 1);
		skills[2] = new LockedSkill(this, 2);
	}

	@Override
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

	@Override
	public boolean canShift()
	{
		// Makes sure a direction is selected
		boolean directionSelected = Manager.input.keyDown(InputManager.W) || Manager.input.keyDown(InputManager.S)
				|| Manager.input.keyDown(InputManager.A) || Manager.input.keyDown(InputManager.D);
		return player.canAttack() && player.canMove() && player.getStamina() >= Player.SHIFT_STAMINA_COST
				&& directionSelected;
	}

	@Override
	public void unlockSkill()
	{
		if(skills[0].id() == Skill.EMPTY)
		{
			skills[0] = new Geyser(this, 0);
		}
		else if(skills[1].id() == Skill.EMPTY)
		{
			skills[1] = new Pulse(this, 1);
		}
		else if(skills[2].id() == Skill.EMPTY)
		{
			skills[2] = new ManaChannelling(this, 2);
		}
	}
}