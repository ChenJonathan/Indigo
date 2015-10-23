package indigo.Entity;

import indigo.Landscape.Land;
import indigo.Landscape.Platform;
import indigo.Landscape.Wall;
import indigo.Manager.Animation;
import indigo.Manager.ContentManager;
import indigo.Projectile.Mortar;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Turret extends Entity
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;

	private int timer;

	private double angle;
	private double groundAngle;

	private boolean hide;

	private Animation animationCannon;

	public final double TURRET_ANGLE = Math.PI / 4;

	public static final double TURRET_WIDTH = 100;
	public static final double TURRET_HEIGHT = 130;
	public static final int BASE_HEALTH = 250;
	public static final int FIRE_RATE = 60;

	public Turret(Stage stage, double x, double y)
	{
		this(stage, x, y, BASE_HEALTH);
	}

	public Turret(Stage stage, double x, double y, int health)
	{
		super(stage, x, y, health);
		name = "a turret";

		width = TURRET_WIDTH;
		height = TURRET_HEIGHT;

		pushability = 0;
		flying = true;
		frictionless = false;

		friendly = false;

		timer = 0;

		animationCannon = new Animation();
		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.TURRET_BASE_DEFAULT), -1);
		setAnimationCannon(DEFAULT, ContentManager.getAnimation(ContentManager.TURRET_CANNON_DEFAULT), -1);

		// Finding closest wall
		double minDistance = 500;
		Land closestLand = null;
		for(Wall wall : stage.getWalls())
		{
			double distance = wall.getLine().ptSegDist(x, y);
			if(distance < minDistance)
			{
				minDistance = distance;
				closestLand = wall;
			}
		}
		for(Platform plat : stage.getPlatforms())
		{
			double distance = plat.getLine().ptSegDist(x, y);
			if(distance < minDistance)
			{
				minDistance = distance;
				closestLand = plat;
			}
		}

		if(closestLand == null)
		{
			hide = true;
		}
		else
		{
			groundAngle = Math.atan(-1 / closestLand.getSlope());
			if(closestLand.getLine().ptSegDist(x + Math.cos(groundAngle), y + Math.sin(groundAngle)) > minDistance)
			{
				groundAngle += Math.PI;
			}

			Point2D.Double intersection = closestLand.getIntersection(new Line2D.Double(getX(), getY(), getX()
					+ (minDistance + getHeight()) * Math.cos(groundAngle), getY() + (minDistance + getHeight())
					* Math.sin(groundAngle)));

			setX(intersection.getX() - Math.cos(groundAngle) * getHeight() / 2);
			setY(intersection.getY() - Math.sin(groundAngle) * getHeight() / 2);

			angle = groundAngle;

			// Check if turret is on wall
			if(closestLand.getLine().ptSegDist(intersection) > 1)
			{
				hide = true;
			}
		}
	}

	public void update()
	{
		if(currentAnimation == DEATH)
		{
			super.update();
			animationCannon.update();
			if(animation.hasPlayedOnce())
			{
				dead = true;
			}
			return;
		}

		if(hide)
		{
			stage.getEntities().remove(this);
		}

		timer = (timer == 0)? 0 : timer - 1;

		super.update();
		animationCannon.update();

		if(canAttack() && inRange())
		{
			double optimalAngle = getOptimalAngle();
			double deltaAngle = optimalAngle - angle;

			if(Math.abs(deltaAngle) <= Math.PI / 36 && timer == 0)
			{
				angle = optimalAngle;
				if(Math.random() < 0.4)
				{
					attack();
				}
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
		// Rotation breaks if x is negative
		g.rotate(groundAngle - Math.PI / 2, getX(), getY());
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)getWidth(), (int)getHeight(), null);
		g.rotate(-(groundAngle - Math.PI / 2), getX(), getY());

		// Drawing cannon
		if(getX() > 0 && getX() < stage.getMapX())
		{
			g.rotate(-angle + Math.PI / 2, getX(), getY());
			if(currentAnimation == DEATH)
			{
				g.drawImage(animationCannon.getImage(), (int)(getX() - 65), (int)(getY() - 65), 130, 130, null);
			}
			else
			{
				g.drawImage(animationCannon.getImage(), (int)(getX() - 65), (int)(getY() - 65), 130, 130, null);
			}
			g.rotate(angle - Math.PI / 2, getX(), getY());
		}
	}

	public void attack()
	{
		if(stage.getTime() % 50 == 0) // TODO Change to be more similar to player firing
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

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - 32, getY() - 32, 64, 64);
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}

	public void die()
	{
		if(currentAnimation != DEATH)
		{
			setAnimation(DEATH, ContentManager.getAnimation(ContentManager.TURRET_BASE_DEATH), 1);
			setAnimationCannon(DEATH, ContentManager.getAnimation(ContentManager.TURRET_CANNON_DEATH), 1);
		}
	}

	// Method used to change animation
	private void setAnimationCannon(int count, BufferedImage[] images, int delay)
	{
		animationCannon.setFrames(images);
		animationCannon.setDelay(delay);
	}
}