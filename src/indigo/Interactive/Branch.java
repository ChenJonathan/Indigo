package indigo.Interactive;

import indigo.Entity.Entity;
import indigo.Entity.Player;
import indigo.Landscape.Platform;
import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Branch extends Interactive
{

	public final static int HEALTH = 100;
	public final static int WIDTH = 200;
	public final static int HEIGHT = 50;
	public final static double SPEED = 0;

	private int timer = 0;
	private boolean breaking = false;
	private Platform platform;

	public Branch(Stage stage, double x, double y)
	{
		super(stage, x, y);

		width = WIDTH;
		height = HEIGHT;

		platform = new Platform(this.stage, x - getWidth() / 2, y - getHeight() / 2, x + getWidth() / 2, y
				- getHeight() / 2);
		stage.getLandscape().add(platform);

		setAnimation(0, ContentManager.getAnimation(ContentManager.BRANCH_DEFAULT), -1);
	}

	public void update()
	{
		if(stage.getPlayer().isGrounded() && stage.getPlayer().getGround().equals(platform))
		{
			breaking = true;
		}

		if(breaking)
		{
			timer++;

			if(timer == 30)
			{
				die();
			}
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)getWidth(), (int)getHeight(), null);

	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	// Not used
	public void activate(Player player)
	{

	}

	public boolean isActive()
	{
		return true;
	}

	public Platform getPlatform()
	{
		return platform;
	}

	public void die()
	{
		stage.getLandscape().remove(platform);
		for(Entity i : stage.getEntities())
		{
			if(i.isGrounded() && i.getGround().equals(platform))
			{
				i.removeGround();
			}
		}
		dead = true;
		// TODO add death animation
	}
}