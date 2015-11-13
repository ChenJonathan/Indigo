package indigo.Projectile;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import indigo.Entity.Entity;
import indigo.Interactive.Interactive;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

public class SteamCloud extends Projectile
{
	private final int DEFAULT = 0;

	public static final int DAMAGE = 0;
	public static final int WIDTH = 100;
	public static final int HEIGHT = 50;
	public static final double SPEED = 10;

	public SteamCloud(Stage stage, double x, double y)
	{
		super(stage, x, y, 0, 0, DAMAGE);

		width = WIDTH;
		height = HEIGHT;
		solid = true;
		flying = true;
		
		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.STEAM_CLOUD), -1);
	}
	
	public SteamCloud(Interactive interactive, double x, double y, double velX, double velY, int dmg)
	{
		super(interactive, x, y, velX, velY, dmg);

		width = WIDTH;
		height = HEIGHT;
		solid = true;
		flying = true;
		
		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.STEAM_CLOUD), -1);
	}
	
	public void update()
	{
		super.update();

		setVelY(Math.max(getVelY() - Stage.GRAVITY, -75));
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), null);
	}
	
	public String getName()
	{
		return creator.getName();
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void collide(Entity ent)
	{
		ent.setVelY(Math.max(ent.getVelY() - ent.getPushability() * 3, -40));
	}

	public void collide(Wall wall)
	{
		die();
	}

	public boolean isActive()
	{
		return true;
	}
}
