package indigo.Skill;

import indigo.Phase.Phase;
import indigo.Projectile.PulseShot;

public class Pulse extends Skill
{
	private int manaCost = 1; // TODO:Change for game balancing

	public Pulse(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.PULSE;
	}

	public void update()
	{
		super.update();

		if(input.mouseLeftDown())
		{
			if(castTime == 0)
			{
				double scale = Math.sqrt(Math.pow(playState.getMouseY() - player.getY(), 2)
						+ Math.pow(playState.getMouseX() - player.getX(), 2));
				double velX = PulseShot.SPEED * (playState.getMouseX() - player.getX()) / scale;
				double velY = PulseShot.SPEED * (playState.getMouseY() - player.getY()) / scale;
				playState.getProjectiles().add(
						new PulseShot(player, player.getX(), player.getY(), velX, velY, PulseShot.DAMAGE));
				player.setMana(player.getMana() - manaCost);
				endCast();
			}
		}
	}

	public boolean canCast()
	{
		return(player.getMana() >= manaCost && player.canAttack());
	}

	public void endCast()
	{
		super.endCast();
		player.canAttack(true);
	}
}
