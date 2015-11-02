package indigo.Phase;

import indigo.GameState.PlayState;
import indigo.Skill.Geyser;
import indigo.Skill.LockedSkill;
import indigo.Skill.Mist;
import indigo.Skill.Pulse;
import indigo.Skill.Skill;

public class Water extends Phase
{
	public Water(PlayState playState)
	{
		super(playState);
		id = Phase.WATER;

		maxCooldowns = new int[] {50, 150, 150};

		int level = playState.getData().getLevel();
		skills[0] = level >= 5? new Mist(this, 0) : new LockedSkill(this, 0);
		skills[1] = level >= 10? new Pulse(this, 1) : new LockedSkill(this, 1);
		skills[2] = level >= 15? new Geyser(this, 2) : new LockedSkill(this, 2);
	}

	@Override
	public boolean canNormalAttack()
	{
		return player.canAttack() && playState.getTime() >= nextAttackTime;
	}

	@Override
	public void unlockSkill()
	{
		if(skills[0].id() == Skill.EMPTY)
		{
			skills[0] = new Mist(this, 0);
		}
		else if(skills[1].id() == Skill.EMPTY)
		{
			skills[1] = new Pulse(this, 1);
		}
		else if(skills[2].id() == Skill.EMPTY)
		{
			skills[2] = new Geyser(this, 2);
		}
	}
}