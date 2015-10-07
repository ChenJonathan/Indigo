package indigo.Entity;

import indigo.Landscape.Land;
import indigo.Manager.ContentManager;
import indigo.Projectile.Mortar;
import indigo.Stage.Stage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Turret extends Entity
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;

	private double angle;

	public static final double TURRET_WIDTH = 100;
	public static final double TURRET_HEIGHT = 130;
	public static final int BASE_HEALTH = 250;
	
	public void setHealth(int health)
	{
		super.setHealth(health);
	}

	public Turret(Stage stage, double x, double y, int health)
	{
		super(stage, x, y, health);
		name = "a turret";

		width = TURRET_WIDTH;
		height = TURRET_HEIGHT;

		pushability = 5;
		flying = false;
		frictionless = false;

		friendly = false;

		angle = Math.PI / 2;

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.TURRET_IDLE), -1);
	}

	public void update()
	{
		if(currentAnimation == DEATH)
		{
			super.update();
			if(animation.hasPlayedOnce())
			{
				dead = true;
			}
			return;
		}

		super.update();

		if(canAttack() && inRange())
		{
			double optimalAngle = getOptimalAngle();
			double deltaAngle = optimalAngle - angle;

			if(Math.abs(deltaAngle) <= Math.PI / 18)
			{
				angle = optimalAngle;
				attack();
			}
			else
			{
				if(deltaAngle < 0)
				{
					deltaAngle += 2 * Math.PI;
				}
				if(deltaAngle < Math.PI)
				{
					angle += Math.PI / 90;
					if(angle >= 2 * Math.PI)
					{
						angle -= 2 * Math.PI;
					}
				}
				else
				{
					angle -= Math.PI / 90;
					if(angle < 0)
					{
						angle += 2 * Math.PI;
					}
				}
			}
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)(getWidth()), (int)(getHeight()), null);

		// Draws a simple line representing the turret arm // TODO Temporary
		g.setColor(Color.RED);
		g.drawLine((int)getX(), (int)getY(), (int)(getX() + 50 * Math.cos(angle)), (int)(getY() - 50 * Math.sin(angle)));
	}

	public void attack()
	{
		if(stage.getTime() % 50 == 0) // TODO Change to be more similar to
										// player firing
		{
			double velX = Mortar.SPEED * Math.cos(angle);
			double velY = Mortar.SPEED * -Math.sin(angle);
			stage.getProjectiles().add(new Mortar(this, getX(), getY(), velX, velY, Mortar.DAMAGE));
		}
	}

	// Checks if player is in range
	public boolean inRange()
	{
		double deltaX = stage.getPlayer().getX() - getX();
		double deltaY = -(stage.getPlayer().getY() - getY());

		if(deltaX == 0)
		{
			// True if maximum vertical height of projectile fired straight up is greater than or equal to deltaY
			return Math.pow(Mortar.SPEED, 2) / (2 * Stage.GRAVITY) >= deltaY;
		}
		else
		{
			return Math.pow(Mortar.SPEED, 4) - Stage.GRAVITY
					* (Stage.GRAVITY * deltaX * deltaX + 2 * deltaY * Math.pow(Mortar.SPEED, 2)) >= 0;
		}
	}

	// Calculates the firing angle necessary to hit the player
	public double getOptimalAngle()
	{
		double optimalAngle;
		double deltaX = stage.getPlayer().getX() - getX();
		double deltaY = -(stage.getPlayer().getY() - getY());

		if(deltaX == 0)
		{
			if(deltaY > 0)
			{
				optimalAngle = Math.PI / 2;
			}
			else
			{
				optimalAngle = 3 * Math.PI / 2;
			}
		}
		else
		{
			double a1 = Math.atan((Math.pow(Mortar.SPEED, 2) + Math.sqrt(Math.pow(Mortar.SPEED, 4) - Stage.GRAVITY
					* (Stage.GRAVITY * deltaX * deltaX + 2 * deltaY * Math.pow(Mortar.SPEED, 2))))
					/ (Stage.GRAVITY * deltaX));
			double a2 = Math.atan((Math.pow(Mortar.SPEED, 2) - Math.sqrt(Math.pow(Mortar.SPEED, 4) - Stage.GRAVITY
					* (Stage.GRAVITY * deltaX * deltaX + 2 * deltaY * Math.pow(Mortar.SPEED, 2))))
					/ (Stage.GRAVITY * deltaX));

			if(deltaX < 0)
			{
				a1 += Math.PI;
				a2 += Math.PI;
			}
			a1 %= 2 * Math.PI;
			a2 %= 2 * Math.PI;
			if(a1 < 0)
			{
				a1 += 2 * Math.PI;
			}
			if(a2 < 0)
			{
				a2 += 2 * Math.PI;
			}
			if(Math.abs(Math.sin(a1)) < Math.abs(Math.sin(a2)))
			{
				optimalAngle = a1;
			}
			else
			{
				optimalAngle = a2;
			}
		}
		return optimalAngle;
	}

	public double getAngle()
	{
		return angle;
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
		if(currentAnimation != DEATH)
		{
			setAnimation(DEATH, ContentManager.getAnimation(ContentManager.TURRET_DEATH), 2);
		}
	}
}