package indigo.Entity;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import indigo.Landscape.Land;
import indigo.Manager.ContentManager;
import indigo.Stage.Stage;
import indigo.Weapon.Saw;

public class Harvester extends Entity
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;

	private Tree tree;

	public static final double HARVESTER_WIDTH = 100;
	public static final double HARVESTER_HEIGHT = 130;
	public static final int BASE_HEALTH = 250;
	public static final int BASE_EXPERIENCE = 30;
	public static final int RANGE = 100;
	public static final int SPEED = 5;

	public Harvester(Stage stage, double x, double y)
	{
		this(stage, x, y, BASE_HEALTH, BASE_EXPERIENCE);
	}

	public Harvester(Stage stage, double x, double y, int health, int experience)
	{
		super(stage, x, y, health, experience);

		width = HARVESTER_WIDTH;
		height = HARVESTER_HEIGHT;

		pushability = 5;
		solid = true;
		flying = false;
		frictionless = false;

		friendly = false;

		weapon = new Saw(this, Saw.DAMAGE);

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.FLYING_BOT_IDLE), 3);
	}

	public void update()
	{
		super.update();

		// Update weapon
		if(hasWeapon())
		{
			weapon.update();
		}

		// Look for nearest tree
		if(tree == null || tree.isDead())
		{
			tree = null;
			for(int count = 0; count < stage.getEntities().size(); count++)
			{
				if(stage.getEntities().get(count) instanceof Tree)
				{
					Tree temp = (Tree)stage.getEntities().get(count);
					// check if closer than current tree
					if(tree == null)
					{
						tree = temp;
					}
					else
					{
						double currentDist = Math.sqrt(Math.pow(tree.getX() - this.getX(), 2)
								+ Math.pow(tree.getY() - this.getY(), 2));
						double newDist = Math.sqrt(Math.pow(temp.getX() - this.getX(), 2)
								+ Math.pow(temp.getY() - this.getY(), 2));
						if(newDist < currentDist)
						{
							tree = temp;
						}
					}
				}
			}
		}
		// Finished looking for nearest tree

		if(tree != null)
		{
			if(!inRange() && Math.abs(getVelX()) < SPEED)
			{
				this.setVelX(tree.getX() > this.getX()? SPEED : -SPEED);
				setDirection(getVelX() > 0? true : false);
			}

			// If tree found, initiate attacking procedure
			if(inRange() && canAttack())
			{
				if(tree.getX() > this.getX()) // if tree is on the right
				{
					if(this.isFacingRight()) // harvester is facing right
					{
						((Saw)weapon).attack();
					}
					else
					// harvester is facing left
					{
						this.setDirection(true);
					}
				}
				else
				// tree is on the left
				{
					if(this.isFacingRight()) // harvester is facing right
					{
						this.setDirection(false);
					}
					else
					// harvester is facing left
					{
						((Saw)weapon).attack();
					}
				}
			}
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)(getWidth()), (int)(getHeight()), null);
		
		if(hasWeaponHitbox())
		{
			weapon.render(g);
		}
	}

	public boolean inRange()
	{
		// in range is if harvester is within attacking range (distance) of tree on both x and y.
		boolean xInRange = Math.abs(tree.getX() - getX()) <= RANGE + tree.getWidth() / 2;
		boolean yInRange = (getY() - HARVESTER_HEIGHT >= tree.getY() - tree.getHeight())
				&& (getY() + HARVESTER_HEIGHT <= tree.getY() + tree.getHeight());
		return xInRange && yInRange;
	}

	public String getName()
	{
		return "a harvester";
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH && dead == false;
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
		dead = true;
	}
}
