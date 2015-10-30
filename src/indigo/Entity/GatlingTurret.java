package indigo.Entity;

import indigo.Landscape.Land;
import indigo.Landscape.Platform;
import indigo.Landscape.Wall;
import indigo.Manager.Animation;
import indigo.Manager.ContentManager;
import indigo.Projectile.Bullet;
import indigo.Projectile.Mortar;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class GatlingTurret extends Entity
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;

	private int timer;

	private double angle;
	private double groundAngle;

	private Animation animationCannon;

	public final double TURRET_ANGLE = Math.PI / 4;

	public static final double TURRET_WIDTH = 130;
	public static final double TURRET_HEIGHT = 110;
	public static final int BASE_HEALTH = 250;
	public static final int FIRE_RATE = 2;

	public GatlingTurret(Stage stage, double x, double y)
	{
		this(stage, x, y, BASE_HEALTH);
	}

	public GatlingTurret(Stage stage, double x, double y, int health)
	{
		super(stage, x, y, health);

		width = TURRET_WIDTH;
		height = TURRET_HEIGHT;

		pushability = 0;
		solid = true;
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
			dead = true;
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

			angle = Math.PI - groundAngle;

			// Check if turret is on land
			if(closestLand.getLine().ptSegDist(intersection) > 1)
			{
				dead = true;
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

		timer = (timer == 0)? 0 : timer - 1;

		super.update();
		animationCannon.update();

		if(canAttack() && inRange())
		{
			double optimalAngle = getOptimalAngle();
			double deltaAngle = optimalAngle - angle;

			if(isLegal(optimalAngle) && Math.abs(deltaAngle) <= Math.PI / 36)
			{
				angle = optimalAngle;
				if(timer == 0)
				{
					attack();
					timer = FIRE_RATE;
				}
			}
			else
			{
				deltaAngle = (deltaAngle + 2 * Math.PI) % (2 * Math.PI);
				angle = (angle + 2 * Math.PI) % (2 * Math.PI);
				if(checkArc(angle, optimalAngle))
				{
					if(isLegal(angle + Math.PI / 90))
					{
						angle += Math.PI / 90;
					}
				}
				else
				{
					if(isLegal(angle - Math.PI / 90))
					{
						angle -= Math.PI / 90;
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
		double velX = Bullet.SPEED * Math.cos(angle);
		double velY = Bullet.SPEED * -Math.sin(angle);
		stage.getProjectiles().add(new Bullet(this, getX(), getY(), velX, velY, Mortar.DAMAGE));
	}

	// Checks if player is in range
	public boolean inRange()
	{
		double deltaX = stage.getPlayer().getX() - getX();
		double deltaY = -(stage.getPlayer().getY() - getY());

		return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) < Bullet.SPEED * Bullet.DURATION;
	}

	// Calculates the firing angle necessary to hit the player
	public double getOptimalAngle()
	{
		double optimalAngle = Math.atan2(-stage.getPlayer().getY() + getY(), stage.getPlayer().getX() - getX());
		return (optimalAngle + 2 * Math.PI) % (2 * Math.PI);
	}

	public boolean isLegal(double testAngle)
	{
		boolean legal = false;

		double leftBound = (Math.PI * 2 - groundAngle) - TURRET_ANGLE;
		if(leftBound < 0)
		{
			leftBound += Math.PI * 2;
		}
		leftBound %= Math.PI * 2;
		double rightBound = ((Math.PI * 2 - groundAngle) + TURRET_ANGLE) % (Math.PI * 2);

		// System.out.println(leftBound + ", " + testAngle + "," + rightBound);
		if(rightBound > leftBound && (testAngle > rightBound || testAngle < leftBound))
		{
			legal = true;
		}
		else if(rightBound < leftBound && (testAngle > rightBound && testAngle < leftBound))
		{
			legal = true;
		}
		return legal;
	}

	public boolean checkArc(double startAngle, double endAngle)
	{
		boolean canReachCCW = true;
		startAngle = (startAngle > Math.PI)? -1 * (Math.PI * 2 - startAngle) : startAngle;
		endAngle = (endAngle > Math.PI)? -1 * (Math.PI * 2 - endAngle) : endAngle;

		double leftBound = (Math.PI * 2 - groundAngle) - TURRET_ANGLE;
		double rightBound = ((Math.PI * 2 - groundAngle) + TURRET_ANGLE) % (Math.PI * 2);
		leftBound = (leftBound > Math.PI)? -1 * (Math.PI * 2 - leftBound) : leftBound;
		rightBound = (rightBound > Math.PI)? -1 * (Math.PI * 2 - rightBound) : rightBound;
		// System.out.println(leftBound + ", " + rightBound);

		// System.out.println("startAngle: " + startAngle + " endAngle: " + endAngle);
		if(startAngle > endAngle)
		{
			canReachCCW = (endAngle < leftBound && startAngle > leftBound)? true : false;
			// System.out.println(canReachCCW);
		}
		else
		{
			canReachCCW = (startAngle < leftBound && endAngle > leftBound)? false : true;
		}
		if(endAngle > leftBound && endAngle < rightBound)
		{
			canReachCCW = (endAngle < (leftBound + rightBound) / 2)? true : false;
		}
		return canReachCCW;
	}

	public String getName()
	{
		return "a turret";
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