package indigo.Skill;

import indigo.Phase.Phase;
import indigo.Weapon.Staff;

public class ManaChannelling extends Skill
{
	public ManaChannelling(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.CHANNEL;
		castOnSelect = false;
	}

	public void update()
	{
		super.update();
		
		if(input.mouseDown() || castTime < 10)
		{
			if(castTime == 0)
			{
				player.canAttack(false);
				player.canMove(false);
				player.canTurn(false);
				
				((Staff)player.getWeapon()).cast();
			}
			
			player.setMana(player.getMana() + 1);
			
			((Staff)player.getWeapon()).holdCast();
		}
		else
		{
			endCast();
		}
	}

	public boolean canCast()
	{
		return player.canAttack() && player.canMove();
	}

	public void endCast()
	{
		super.endCast();
		player.canAttack(true);
		player.canMove(true);
		player.canTurn(true);
	}
}