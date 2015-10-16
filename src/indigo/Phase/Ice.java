package indigo.Phase;

import indigo.GameState.PlayState;
import indigo.Manager.Data;
import indigo.Skill.LockedSkill;
import indigo.Skill.Geyser;
import indigo.Skill.IceArmor;
import indigo.Skill.IceChains;
import indigo.Skill.Skill;
import indigo.Weapon.IceSword;

public class Ice extends Phase
{
	public int attackDelay = 10;

	public Ice(PlayState playState)
	{
		super(playState);
		id = Phase.ICE;

		maxCooldowns = new int[] { 150, 150, 150, 150 };

		skills[0] = new LockedSkill(this, 0);
		skills[1] = new IceChains(this, 1);
		skills[2] = new IceArmor(this, 2);
		skills[3] = new LockedSkill(this, 3);
		// TODO Implement locked skills
	}

	@Override
	public boolean canNormalAttack()
	{
		if(player.canAttack() && !((IceSword)player.getWeapon()).isAttacking() && (playState.getTime() - attackStartTime >= attackDelay))
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean canShift()
	{
		return true; // TODO Finish
	}
	
	@Override
	public boolean canSwap()
	{
		// Makes sure no skills are casting
		boolean casting = false;
		for(int count = 0; count < Data.NUM_SKILLS; count++)
		{
			// Ice Armor is given an exception
			if(skills[count].id() != Skill.ARMOR && skillStates[count] == CAST)
			{
				casting = true;
			}
		}
		return player.canAttack() && player.canMove() && player.canTurn() && !casting;
	}

	@Override
	public void unlockSkill()
	{
		if(skills[0].id() == Skill.EMPTY) // TODO Add in other skills
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

	@Override
	public void resetSkillStates()
	{
		for(int count = 0; count < Data.NUM_SKILLS; count++)
		{
			// Ends Ice Armor if active
			if(skills[count].id() == Skill.ARMOR && skillStates[count] == CAST)
			{
				skills[count].endCast();
			}
			skillStates[count] = 0;
		}
		selectedSkill = NO_SKILL_SELECTED;
	}
}