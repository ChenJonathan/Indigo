package indigo.Interactive;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Gate extends Interactive
{
	private int id;
	private boolean open;

	private Wall wall;

	private final int CLOSED = 0;
	private final int CLOSING = 1;
	private final int OPEN = 2;
	private final int OPENING = 3;

	public final static int WIDTH = 30;
	public final static int HEIGHT = 200;

	public Gate(Stage stage, double x, double y, int id, boolean open)
	{
		super(stage, x, y);
		width = WIDTH;
		height = HEIGHT;

		this.id = id;
		this.open = open;

		wall = new Wall(stage, x, y - getHeight() / 2, x, y + getHeight() / 2);
		stage.getWalls().add(wall);
	}

	public void update()
	{
		super.update();
	}

	public void render(Graphics2D g)
	{
		g.drawRect((int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), (int)getWidth(), (int)getHeight());
	}

	public int id()
	{
		return id;
	}
	
	public void toggle()
	{
		open = !open;
		if(!open)
		{
			stage.getWalls().remove(wall);
			for(Entity ent : stage.getEntities())
			{
				if(ent.isGrounded() && ent.getGround().equals(wall))
				{
					ent.removeGround();
				}
			}
		}
		else
		{
			stage.getWalls().add(wall);
		}
	}
	
	public String getName()
	{
		return "a gate";
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	// Not used
	public void activate()
	{

	}

	public boolean isActive()
	{
		return true;
	}

	// Not used
	public void die()
	{

	}
}