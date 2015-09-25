package indigo.Skill;

import indigo.Phase.Phase;
import indigo.Projectile.PulseShot;
import indigo.Projectile.WaterProjectile;

public class Pulse extends Skill
{
	private int manaCost = 1; //TODO:Change for game balancing
	private boolean isGrounded;
	
	public Pulse(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.PULSE;
	}

	public void update()
	{
		super.update();
		
		isGrounded = player.isGrounded();
		player.canMove(false);
		player.canAttack(false);
		//create projectile, change pulseshot collide to affect projectile
		playState.getProjectiles().add(new PulseShot(player, player.getX(), player.getY(), 0, 0, 0)); //doesn't move, doesn't do damage
		endCast();
	}

	public boolean canCast()
	{
		return (player.getMana() >= manaCost && player.canAttack());
	}
	
	public void endCast()
	{
		super.endCast();
		player.canAttack(true);
		player.canMove(true);
	}
}
