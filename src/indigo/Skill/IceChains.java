package indigo.Skill;

import indigo.Manager.InputManager;
import indigo.Phase.Phase;
import indigo.Projectile.IceChainParticle;

public class IceChains extends Skill
{
	private double dx,dy;
	private IceChainParticle hook;

	public IceChains(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.CHAINS;
		castOnSelect = false;
	}

	public void update()
	{
		super.update();

		if(player.getMana() >= 1  || castTime < 10)
		{
			if(castTime == 0)
			{
				player.setIceChains(true);
				dx = playState.getMouseX() - player.getX();
				dy = playState.getMouseY() - player.getY();
				
				hook = new IceChainParticle(player, player.getX(), player.getY(), 
						dx*IceChainParticle.SPEED, dy*IceChainParticle.SPEED, 0);
				playState.getProjectiles().add(hook);
				player.setMana(player.getMana() - 1);
			}
			castTime++;
		}
		else
		{
			playState.getProjectiles().remove(hook);
			hook = null;
			endCast();
		}
	}

	public boolean canCast()
	{
		return castOnSelect;
	}

	public void endCast()
	{
		super.endCast();
		player.setIceChains(false);
	}
}
