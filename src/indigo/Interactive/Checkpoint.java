package indigo.Interactive;

import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class Checkpoint extends Interactive
{
	private final int DEFAULT = 0;

	public final static int WIDTH = 130;
	public final static int HEIGHT = 110;
	public final static double SPEED = 0;

	public Checkpoint(Stage stage, double x, double y)
	{
		super(stage, x, y);
		width = WIDTH;
		height = HEIGHT;

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.TURRET_BASE_DEFAULT), -1);
	}

	public void update()
	{
		super.update();
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), null);
	}

	public String getName()
	{
		return "a checkpoint";
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void collide()
	{
		stage.setStartingPosition((int)getX(), (int)getY());
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