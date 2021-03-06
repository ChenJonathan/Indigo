package indigo.Skill;

import indigo.Entity.Entity;
import indigo.Manager.ContentManager;
import indigo.Manager.SoundManager;
import indigo.Phase.Phase;
import indigo.Projectile.PulseWave;
import indigo.Weapon.Staff;

public class Pulse extends Skill
{
	private PulseWave pulse;

	public final static double PUSHBACK = 40;
	public final static double RADIUS = 1000; // Radius of effect
	public static final int DAMAGE = 20;

	public Pulse(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.PULSE;
		icon = ContentManager.getImage(ContentManager.SKILL_PULSE);
		castOnSelect = true;
	}

	public void update()
	{
		super.update();

		if(castTime == 0)
		{
			player.canMove(false);
			player.canAttack(false);
			player.setMana(player.getMana() - 80);

			// Create projectile, change pulseshot collide to affect projectile
			pulse = new PulseWave(player, player.getX(), player.getY(), 0, 0, 0);
			playState.getProjectiles().add(pulse);

			((Staff)player.getWeapon()).cast();

			SoundManager.play(ContentManager.PULSE_EFFECT);
		}
		else if(castTime == 3)
		{
			endCast();
		}
		else
		{
			for(int count = 0; count < playState.getProjectiles().size(); count++)
			{
				double xDist = player.getX() - playState.getProjectiles().get(count).getX();
				double yDist = player.getY() - playState.getProjectiles().get(count).getY();
				double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));

				if(dist <= PulseWave.WIDTH && !playState.getProjectiles().get(count).equals(pulse))
				{
					playState.getProjectiles().get(count).die();
				}
			}

			for(int count = 0; count < playState.getEntities().size(); count++)
			{
				Entity ent = playState.getEntities().get(count); // Ease of reference

				double xDist = player.getX() - ent.getX();
				double yDist = player.getY() - ent.getY();
				double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
				
				// Following if statement deals knockback
				if(!ent.isFriendly() && dist <= RADIUS)
				{
					// Push enemy away, further when closer
					double scale = Math.sqrt(Math.pow(ent.getY() - player.getY(), 2)
							+ Math.pow(ent.getX() - player.getX(), 2));
					double iDP = 1 - (scale / RADIUS); // Inverse distance percentage; TODO: Change WIDTH to Radius here
														// when animation is drawn
					double velX = PUSHBACK * iDP * (ent.getX() - player.getX()) / scale;
					double velY = PUSHBACK * iDP * (ent.getY() - player.getY()) / scale;

					if(scale < RADIUS * 0.02) // TODO: Change for when get circular hitbox.
					{
						// Directly apply knockback to avoid divide by zero error
						velX = PUSHBACK * (ent.getX() - player.getX()) / scale;
						velY = PUSHBACK * (ent.getY() - player.getY()) / scale;
					}

					if(ent.isPushable())
					{
						ent.setVelX(velX + ent.getVelX()); // Velocity is added on rather than set
						ent.setVelY(velY + ent.getVelY());
					}

					if(!ent.isDodging()) // Damaged, won't hurt if is dodging, scale by distance
					{
						ent.setHealth((int)(ent.getHealth() - (DAMAGE * iDP)));
						ent.mark();
					}
				}
			}
		}
	}

	public boolean canCast()
	{
		return player.getMana() >= 80 && player.canAttack() && player.canMove();
	}

	public void endCast()
	{
		player.canAttack(true);
		player.canMove(true);
		phase.setAttackTimer(10);
		super.endCast();
	}
}