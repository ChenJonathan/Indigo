package indigo.Skill;

import java.awt.geom.Area;

import indigo.Entity.Entity;
import indigo.Phase.Phase;
import indigo.Projectile.Projectile;
import indigo.Projectile.PulseWave;
import indigo.Weapon.Staff;

public class Pulse extends Skill
{
	private int manaCost = 1; //TODO:Change for game balancing
	public final static double PUSHBACK = 100; // TODO: Change getWidth(), getHeight(), and PUSHBACK to suit the Pulse
	// Shot, keeping hitbox size in mind
	public final static double RADIUS = 1000; //radius of effect
	public static final int DAMAGE = 20;
	
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
		
		((Staff)player.getWeapon()).cast();
		for(int count = 0; count < playState.getProjectiles().size(); count++)
		{
			double xDist = player.getX() - playState.getProjectiles().get(count).getX();
			double yDist = player.getY() - playState.getProjectiles().get(count).getY();
			double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
			
			if(dist<=PulseWave.WIDTH 
					&& !(playState.getProjectiles().get(count).getX() == player.getX()
							&& playState.getProjectiles().get(count).getY() == player.getY()))
			{
				playState.getProjectiles().get(count).die();
			}
		}
		
		for(int count = 0; count < playState.getEntities().size(); count++)
		{
			Entity ent = playState.getEntities().get(count); //Ease of references
			
			double xDist = player.getX() - ent.getX();
			double yDist = player.getY() - ent.getY();
			double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
			// Following if statement deals damage
			if(dist<=PulseWave.WIDTH 
					&& !(ent.getX() == player.getX() && ent.getY() == player.getY()))
			{
				ent.setHealth(ent.getHealth()-DAMAGE);
			}
			 //Following if statement deals knockback
			//Deleted conditions: !player.isGrounded() || (ent.getY() < player.getY() + player.getHeight() / 2) && 
			if(!(ent.isFriendly())
					&& dist<=RADIUS)
			{
				// Push enemy away, further when closer
				double scale = Math.sqrt(Math.pow(ent.getY() - player.getY(), 2) + Math.pow(ent.getX() - player.getX(), 2));
				double iDP = 1 - (scale / RADIUS); // Inverse distance percentage; TODO: Change WIDTH to Radius here when
													// animation is drawn
				double velX = PUSHBACK * iDP * (ent.getX() - player.getX()) / scale;
				double velY = PUSHBACK * iDP * (ent.getY() - player.getY()) / scale;

				if(scale < RADIUS * 0.02) // TODO: Change for when get circular hitbox.
				{
					// Directly apply knockback to avoid divide by zero error
					velX = PUSHBACK * (ent.getX() - player.getX()) / scale;
					velY = PUSHBACK * (ent.getY() - player.getY()) / scale;
				}

				ent.setVelX(velX + ent.getVelX()); // velocity is added on rather than set
				ent.setVelY(velY + ent.getVelY());

				if(!ent.isDodging()) // damaged, won't hurt if is dodging, scale by distance
				{
					ent.setHealth((int)(ent.getHealth() - (DAMAGE * iDP)));
					ent.mark();
				}
			}
		}
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
