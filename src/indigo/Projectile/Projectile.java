package indigo.Projectile;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.Animation;
import indigo.Stage.Named;
import indigo.Stage.Respawnable;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;

public abstract class Projectile implements Respawnable, Named
{
	protected Stage stage;
	protected Named creator;

	private double x, y;
	private double velX, velY;

	private double prevX, prevY;
	private Line2D.Double travel;

	protected double width, height;
	protected boolean solid;

	protected int damage;

	protected Animation animation;
	protected int currentAnimation;

	protected boolean facingRight;
	protected boolean friendly;
	protected boolean flying;
	protected boolean dead;

	// Subclasses - Initialize solid and flying
	public Projectile(Named creator, double x, double y, double velX, double velY, int dmg)
	{
		this(creator.getStage(), x, y, velX, velY, dmg);
		this.creator = creator;

		if(creator instanceof Entity)
		{
			friendly = ((Entity)creator).isFriendly();
		}
		else if(creator instanceof Projectile)
		{
			friendly = ((Projectile)creator).isFriendly();
		}
	}

	public Projectile(Stage stage, double x, double y, double velX, double velY, int dmg)
	{
		this.stage = stage;

		this.x = x;
		this.y = y;
		this.velX = velX;
		this.velY = velY;

		prevX = x - velX;
		prevY = y - velY;
		travel = new Line2D.Double(prevX, prevY, x, y);

		facingRight = (velX > 0);

		if(velX < -Stage.TERMINAL_VELOCITY)
		{
			velX = -Stage.TERMINAL_VELOCITY;
		}
		else if(velX > Stage.TERMINAL_VELOCITY)
		{
			velX = Stage.TERMINAL_VELOCITY;
		}

		damage = dmg; // TODO Modify based on player level

		friendly = false;

		animation = new Animation();
	}

	protected void setAnimation(int count, BufferedImage[] images, int delay)
	{
		currentAnimation = count;
		animation.setFrames(images);
		animation.setDelay(delay);
	}

	public void update()
	{
		prevX = x;
		prevY = y;

		x += velX;
		y += velY;
		travel = new Line2D.Double(prevX, prevY, x, y);

		// Applying gravity
		if(!flying)
		{
			velY += Stage.GRAVITY;
		}
		if(velY < -Stage.TERMINAL_VELOCITY)
		{
			velY = -Stage.TERMINAL_VELOCITY;
		}
		else if(velY > Stage.TERMINAL_VELOCITY)
		{
			velY = Stage.TERMINAL_VELOCITY;
		}

		facingRight = (velX > 0);

		animation.update();
	}

	public abstract void render(Graphics2D g);

	public abstract Shape getHitbox();

	public abstract void collide(Entity ent);

	public abstract void collide(Wall wall);

	public abstract boolean isActive(); // Able to collide with entities

	// Override if death animation exists
	public void die()
	{
		velX = 0;
		velY = 0;
		dead = true;
	}

	// Used for Pulse skill
	public boolean intersects(Projectile proj)
	{
		Area projArea = new Area(getHitbox());
		projArea.intersect(new Area(proj.getHitbox()));
		return !projArea.isEmpty();
	}

	// Used for projectile-wall collision - Checks if the projectile passed through the wall
	public boolean intersects(Wall wall)
	{
		PathIterator polyIt = wall.getHitbox().getPathIterator(null); // Getting an iterator along the polygon path
		double[] coords = new double[6]; // Double array with length 6 needed by iterator
		double[] firstCoords = new double[2]; // First point (needed for closing polygon path)
		double[] lastCoords = new double[2]; // Previously visited point
		polyIt.currentSegment(firstCoords); // Getting the first coordinate pair
		lastCoords[0] = firstCoords[0]; // Priming the previous coordinate pair
		lastCoords[1] = firstCoords[1];
		polyIt.next();
		while(!polyIt.isDone())
		{
			int type = polyIt.currentSegment(coords);
			switch(type)
			{
				case PathIterator.SEG_LINETO:
				{
					Line2D.Double currentLine = new Line2D.Double(lastCoords[0], lastCoords[1], coords[0], coords[1]);
					if(currentLine.intersectsLine(travel))
					{
						return true;
					}
					lastCoords[0] = coords[0];
					lastCoords[1] = coords[1];
					break;
				}
				case PathIterator.SEG_CLOSE:
				{
					Line2D.Double currentLine = new Line2D.Double(coords[0], coords[1], firstCoords[0], firstCoords[1]);
					if(currentLine.intersectsLine(travel))
					{
						return true;
					}
					break;
				}
			}
			polyIt.next();
		}
		return false;
	}

	// Used for projectile-wall collision - Utilizes previous projectile position
	public boolean isRightOfLine(Line2D.Double line)
	{
		double deltaY = line.getP2().getY() - line.getP1().getY();
		// Formula to calculate if a point is located on the right or left side of a line
		double value = (line.getP2().getX() - line.getP1().getX()) * (getPrevY() - line.getP1().getY())
				- (getPrevX() - line.getP1().getX()) * (line.getP2().getY() - line.getP1().getY());
		return value * deltaY < 0;
	}

	// Used for projectile-wall collision - Utilizes previous projectile position
	public boolean isAboveLine(Line2D.Double line)
	{
		double deltaX = line.getP2().getX() - line.getP1().getX();
		// Formula to calculate if a point is located above the line
		double value = (line.getP2().getY() - line.getP1().getY()) * (getPrevX() - line.getP1().getX())
				- (getPrevY() - line.getP1().getY()) * (line.getP2().getX() - line.getP1().getX());
		return value * deltaX > 0;
	}

	public Named getCreator()
	{
		return creator;
	}

	public abstract String getName();

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
		travel = new Line2D.Double(prevX, prevY, x, y);
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
		travel = new Line2D.Double(prevX, prevY, x, y);
	}

	public double getPrevX()
	{
		return prevX;
	}

	public double getPrevY()
	{
		return prevY;
	}

	public double getWidth()
	{
		return width;
	}

	public double getHeight()
	{
		return height;
	}

	public boolean isSolid()
	{
		return solid;
	}

	public boolean isFriendly()
	{
		return friendly;
	}

	public double getVelX()
	{
		return velX;
	}

	public void setVelX(double velX)
	{
		this.velX = velX;
		if(velX < -Stage.TERMINAL_VELOCITY)
		{
			this.velX = -Stage.TERMINAL_VELOCITY;
		}
		else if(velX > Stage.TERMINAL_VELOCITY)
		{
			this.velX = Stage.TERMINAL_VELOCITY;
		}
	}

	public double getVelY()
	{
		return velY;
	}

	public void setVelY(double velY)
	{
		this.velY = velY;
		if(velY < -Stage.TERMINAL_VELOCITY)
		{
			this.velY = -Stage.TERMINAL_VELOCITY;
		}
		else if(velY > Stage.TERMINAL_VELOCITY)
		{
			this.velY = Stage.TERMINAL_VELOCITY;
		}
	}

	public boolean isFacingRight()
	{
		return facingRight;
	}

	public boolean isDead()
	{
		return dead;
	}

	// Do not use
	public void setDead()
	{
		dead = true;
	}

	public Stage getStage()
	{
		return stage;
	}
}