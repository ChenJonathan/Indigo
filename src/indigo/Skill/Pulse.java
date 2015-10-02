package indigo.Skill;

import indigo.Phase.Phase;
import indigo.Projectile.PulseWave;

public class Pulse extends Skill
{
	private int manaCost = 1; //TODO:Change for game balancing
	private boolean grounded;
	
	public Pulse(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.PULSE;
	}

	public void update()
	{
		super.update();
		player.canMove(false);
		player.canAttack(false);
		player.setMana(player.getMana() - manaCost);
		// Create projectile, change pulseshot collide to affect projectile
		playState.getProjectiles().add(new PulseWave(player, player.getX(), player.getY(), 0, 0, 0));
		endCast();
	}

	public boolean canCast()
	{
		return player.getMana() >= manaCost && player.canAttack();
	}

	public void endCast()
	{
		super.endCast();
		player.canAttack(true);
		player.canMove(true);
	}
}
