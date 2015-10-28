package indigo.Interactive;

import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class Destination extends Interactive
{
	private final int IDLE = 0;
	private final int DEATH = 1;

	public final static int WIDTH = 110;
	public final static int HEIGHT = 130;
	public final static double SPEED = 0;

	public Destination(Stage stage, double x, double y)
	{
		super(stage, x, y);
		width = WIDTH;
		height = HEIGHT;
		
		setAnimation(IDLE, ContentManager.getAnimation(ContentManager.HEALTH_PICKUP_IDLE), 6);
	}

	public void update()
	{
		super.update();
		
		if(currentAnimation == DEATH && animation.hasPlayedOnce())
		{
			dead = true;
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)getWidth(), (int)getHeight(), null);
	}
	
	public String getName()
	{
		return "the destination";
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void activate()
	{
		die();
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}

	public void die()
	{
		setAnimation(DEATH, ContentManager.getAnimation(ContentManager.HEALTH_PICKUP_DEATH), 3);
	}
}