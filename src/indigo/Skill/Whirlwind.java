package indigo.Skill;

import indigo.Phase.Phase;

public class Whirlwind extends Skill
{
	public Whirlwind(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.WHIRLWIND;
		castOnSelect = true;
	}
	
	public void update()
	{
		super.update();

		if(castTime == 0)
		{
			player.setMana(player.getMana() - 20);
			player.shift();
		}
		else if(player.shifted())
		{
			endCast();
		}
		else
		{
			player.jumpMore();
		}
	}

	public boolean canCast()
	{
		return player.getMana() >= 20 && player.canAttack() && player.canMove() && player.canTurn();
	}

	public void endCast()
	{
		super.endCast();
	}
}