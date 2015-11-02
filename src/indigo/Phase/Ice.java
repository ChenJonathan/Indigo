package indigo.Phase;

import indigo.GameState.PlayState;
import indigo.Manager.Data;
import indigo.Skill.LockedSkill;
import indigo.Skill.IceArmor;
import indigo.Skill.IceChains;
import indigo.Skill.Skill;

public class Ice extends Phase
{
	public Ice(PlayState playState)
	{
		super(playState);
		id = Phase.ICE;

		maxCooldowns = new int[] {150, 150, 150};

		int level = playState.getData().getLevel();
		skills[0] = level >= 5? new IceChains(this, 0) : new LockedSkill(this, 0);
		skills[1] = level >= 10? new IceChains(this, 1) : new LockedSkill(this, 1);
		skills[2] = level >= 15? new IceArmor(this, 2) : new LockedSkill(this, 2);
	}

	@Override
	public boolean canNormalAttack()
	{
		return player.canAttack() && !player.hasWeaponHitbox() && playState.getTime() >= nextAttackTime;
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
		if(skills[0].id() == Skill.EMPTY)
		{
			skills[0] = new IceChains(this, 0);
		}
		else if(skills[1].id() == Skill.EMPTY)
		{
			skills[1] = new IceChains(this, 1);
		}
		else if(skills[2].id() == Skill.EMPTY)
		{
			skills[2] = new IceArmor(this, 2);
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