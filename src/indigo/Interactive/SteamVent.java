package indigo.Interactive;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import indigo.Landscape.Land;
import indigo.Landscape.Platform;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;
import indigo.Projectile.SteamCloud;
import indigo.Stage.Stage;

public class SteamVent extends Interactive
{
	private double groundAngle;

	private int timer;

	private final int DEFAULT = 0;
	private final int DEATH = 1;

	public static final double WIDTH = 130;
	public static final double HEIGHT = 110;
	public static final int DURATION = 60;

	public SteamVent(Stage stage, double x, double y)
	{
		super(stage, x, y);
		width = WIDTH;
		height = HEIGHT;

		timer = 0;

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.TURRET_BASE_DEFAULT), -1);

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
			if(distance < minDistance && stage.aboveLand(this, plat))
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
			double testX = x + Math.cos(groundAngle);
			double testY = y + Math.sin(groundAngle);
			if(closestLand.getLine().ptSegDist(testX, testY) > minDistance
					&& (closestLand instanceof Wall || groundAngle < 0))
			{
				groundAngle += Math.PI;
			}

			Point2D intersection = closestLand.getHitboxIntersection(new Line2D.Double(getX(), getY(), getX()
					+ (minDistance + getHeight()) * Math.cos(groundAngle), getY() + (minDistance + getHeight())
					* Math.sin(groundAngle)));

			setX(intersection.getX() - Math.cos(groundAngle) * getHeight() / 2);
			setY(intersection.getY() - Math.sin(groundAngle) * getHeight() / 2);

			// Check if turret is on land
			if(closestLand.getLine().ptSegDist(intersection) > Land.THICKNESS / 2 + 1)
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
			if(animation.hasPlayedOnce())
			{
				dead = true;
			}
			return;
		}

		super.update();

		if(timer == DURATION)
		{
			stage.getProjectiles().add(
					new SteamCloud(this, getX(), getY(), -Math.cos(groundAngle) * SteamCloud.SPEED, -Math
							.sin(groundAngle) * SteamCloud.SPEED, SteamCloud.DAMAGE));
			timer = 0;
		}
		timer++;
	}

	public void render(Graphics2D g)
	{
		// Rotation breaks if x is negative
		g.rotate(groundAngle - Math.PI / 2, getX(), getY());
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)getWidth(), (int)getHeight(), null);
		g.rotate(-(groundAngle - Math.PI / 2), getX(), getY());
	}

	public String getName()
	{
		return "a steam vent";
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	// Not used
	public void collide()
	{

	}

	public boolean isActive()
	{
		return true;
	}

	public void die()
	{
		dead = true;
	}
}
