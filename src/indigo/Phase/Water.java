package indigo.Phase;

import indigo.Entity.Player;
import indigo.GameState.PlayState;
import indigo.Manager.InputManager;
import indigo.Skill.EmptySkill;
import indigo.Skill.Geyser;
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

		maxCooldowns = new int[] { 0, 0, 0, 0 };

		skills[0] = new Geyser(this, 0);
		skills[1] = new Pulse(this, 1);
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
		// Makes sure a direction is selected
		boolean directionSelected = input.keyDown(InputManager.W) || input.keyDown(InputManager.S)
				|| input.keyDown(InputManager.A) || input.keyDown(InputManager.D);
		return player.canAttack() && player.canMove() && player.getStamina() >= Player.SHIFT_STAMINA_COST
				&& directionSelected;
	}

	public void unlockSkill()
	{
		if(skills[0].id() == Skill.EMPTY)
		{
			skills[0] = new Geyser(this, 0);
		}
		else if(skills[1].id() == Skill.EMPTY) // TODO Add in other skills
		{
			skills[1] = new Geyser(this, 1);
		}
		else if(skills[2].id() == Skill.EMPTY) // TODO Add in other skills
		{
			skills[2] = new Geyser(this, 2);
		}
		else if(skills[3].id() == Skill.EMPTY) // TODO Add in other skills
		{
			skills[3] = new Geyser(this, 3);
		}
	}
}