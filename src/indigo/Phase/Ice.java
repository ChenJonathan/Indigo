package indigo.Phase;

import indigo.GameState.PlayState;
import indigo.Skill.EmptySkill;
import indigo.Skill.Geyser;
import indigo.Skill.IceArmor;
import indigo.Skill.IceChains;
import indigo.Skill.Skill;
import indigo.Weapon.IceSword;

public class Ice extends Phase
{
	public int attackDelay = 15;

	public Ice(PlayState playState)
	{
		super(playState);
		id = Phase.ICE;

		maxCooldowns = new int[] { 0, 0, 0, 1800 };

		skills[0] = new EmptySkill(this, 0);
		skills[1] = new IceChains(this, 1);
		skills[2] = new IceArmor(this, 2);
		skills[3] = new EmptySkill(this, 3);
		// TODO Implement locked skills
	}

	public boolean canNormalAttack()
	{
		if(player.canAttack() && !((IceSword)player.getWeapon()).isAttacking() && (playState.getTime() - attackStartTime >= attackDelay))
		{
			return true;
		}
		return false;
	}

	public boolean canShift()
	{
		return true; // TODO Finish
	}

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
}