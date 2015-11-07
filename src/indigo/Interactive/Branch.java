package indigo.Interactive;

import indigo.Entity.Entity;
import indigo.Landscape.Platform;
import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Branch extends Interactive
{
	public final static int WIDTH = 240;
	public final static int HEIGHT = 75;
	public final static int DURATION = 30;

	private int timer = 0;
	private boolean breaking = false;
	private Platform platform;

	public Branch(Stage stage, double x, double y, boolean direction)
	{
		super(stage, x, y);

		width = WIDTH;
		height = HEIGHT;

		if(direction)
		{
			setX(getX() + WIDTH / 2 + 25);
			setAnimation(0, ContentManager.getAnimation(ContentManager.BRANCH_RIGHT), -1);
		}
		else
		{
			setX(getX() - WIDTH / 2 - 5);
			setAnimation(0, ContentManager.getAnimation(ContentManager.BRANCH_LEFT), -1);
		}

		platform = new Platform(this.stage, getX() - getWidth() / 2, getY() - getHeight() / 2 + 20, getX() + getWidth()
				/ 2, getY() - getHeight() / 2 + 20);
		stage.getPlatforms().add(platform);
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

			if(timer == DURATION)
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

	public String getName()
	{
		return "a branch";
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	// Not used
	public void collide()
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
		stage.getPlatforms().remove(platform);
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