package indigo.Entity;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import indigo.Landscape.Land;
import indigo.Projectile.HarvesterProjectile;
import indigo.Projectile.WaterBolt;
import indigo.Stage.Stage;

public class Harvester extends Entity
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;

	private Tree TREE = null; // find tree later

	public static final double HARVESTER_WIDTH = 100;
	public static final double HARVESTER_HEIGHT = 130;
	public static final int BASE_HEALTH = 250;// TODO: Change to harvester's stats
	public static final int RANGE = 50;// TODO: Balancing

	public Harvester(Stage stage, double x, double y, int health)
	{
		super(stage, x, y, health);
		name = "a harvester";

		width = HARVESTER_WIDTH;
		height = HARVESTER_HEIGHT;

		pushability = 5;
		flying = false;
		frictionless = false;

		friendly = false;

		// TODO: setAnimation(DEFAULT, ContentManger.getAnimation)
	}

	public void update()
	{
		super.update();

		// Look for nearest tree
		if(TREE == null || TREE.isDead())
		{
			for(int count = 0; count < stage.getEntities().size(); count++)
			{
				if(stage.getEntities().get(count) instanceof Tree)
				{
					Tree temp = stage.getEntities().get(count);
					// check if closer than current tree
					if(TREE == null)
					{
						TREE = stage.getEntities().get(count);
					}
					else
					{
						double currentDist = Math
								.sqrt(Math.pow(TREE.getX() - this.getX(), 2) + Math.pow(TREE.getY() - this.getY(), 2));
						double newDist = Math
								.sqrt(Math.pow(temp.getX() - this.getX(), 2) + Math.pow(temp.getY() - this.getY(), 2));
						if(newDist < currentDist)
						{
							TREE = temp;
						}
					}
				}
			}
		} // finished looking for nearest tree

		if(!inRange())
		{
			double scale = Math.sqrt(Math.pow(TREE.getY() - this.getY(), 2) + Math.pow(TREE.getX() - this.getY(), 2));
			double velX = WaterBolt.SPEED * (TREE.getX() - this.getX()) / scale;
			double velY = WaterBolt.SPEED * (TREE.getY() - this.getY()) / scale;

			this.setVelX(velX);
			this.setVelY(velY);
		}
		else // in range
		{
			this.setVelX(0);
			this.setVelY(0);
		}
		// If tree found, initiate attacking procedure
		if(inRange() && canAttack())
		{
			if(TREE.getY() > this.getY()) // if tree is on the right
			{
				if(this.isFacingRight()) // harvester is facing right
				{
					attack();
				}
				else // harvester is facing left
				{
					this.setDirection(true);
				}
			}
			else // tree is on the left
			{
				if(this.isFacingRight()) // harvester is facing right
				{
					this.setDirection(false);
				}
				else // harvester is facing left
				{
					attack();
				}
			}
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)(getWidth()), (int)(getHeight()), null);
	}

	public boolean inRange()
	{ // in range is if harvester is within attacking range (distance) of tree on both x and y.
		boolean xInRange = Math.abs(TREE.getX() - getX()) <= RANGE;
		boolean yInRange = (getY() - HARVESTER_HEIGHT >= TREE.getY() - TREE.HEIGHT)
				&& (getY() + HARVESTER_HEIGHT <= TREE.getY() + TREE.HEIGHT);
		return xInRange && yInRange;
	}

	public void attack()
	{
		// attack tree
		if(stage.getTime() % 5 == 0)
		{
			// Summon projectile, it should never move, disappear after time
			stage.getProjectiles().add(new HarvesterProjectile(this, this.getX() + this.getWidth() / 2, this.getY(), 0,
					0, HarvesterProjectile.DAMAGE));
		}
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}

	public void setGround(Land ground)
	{
		super.setGround(ground);
		canAttack(true);
	}

	public void removeGround()
	{
		super.removeGround();
		canAttack(false);
	}

	public void die()
	{
		// TODO
	}
}
