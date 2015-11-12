package indigo.Display;

import indigo.Main.Game;
import indigo.Manager.ContentManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Displays a book used for level selection.
 * 
 * @author Jonathan Chen
 */
public class Book
{
	private double x;
	private double y;
	private double velX;
	private double accelX;

	private int timer;

	private String[] levels;

	public static final int WIDTH = 900;
	public static final int HEIGHT = 600;

	public static final int ACCEL = 4;
	public static final int DURATION = 19;

	public static final int LEVELS_PER_BOOK = 2;

	public Book(double x, double y, double accelX, int book, String[] levels)
	{
		this.x = x;
		this.y = y;
		this.setAccelX(accelX);

		timer = accelX == 0? -1 : DURATION;

		this.levels = new String[LEVELS_PER_BOOK];
		for(int count = 0; count < LEVELS_PER_BOOK; count++)
		{
			if(book * LEVELS_PER_BOOK + count < levels.length)
			{
				this.levels[count] = levels[book * LEVELS_PER_BOOK + count];
			}
		}
	}

	public void update()
	{
		if(timer != -1)
		{
			if(timer == 0)
			{
				accelX = -accelX;
			}
			timer--;
		}

		x += velX;
		velX += accelX;
	}

	public void render(Graphics2D g)
	{
		g.drawImage(ContentManager.getImage(ContentManager.BOOK), (int)x - WIDTH / 2, (int)y - HEIGHT / 2, null);

		float transparency = (float)Math.pow(1 - Math.abs(x - Game.WIDTH / 2) / (Game.WIDTH / 2 + WIDTH / 2), 10);
		g.setColor(new Color(0, 0, 0, transparency));
		g.setStroke(new BasicStroke(6));
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
		FontMetrics fontMetrics = g.getFontMetrics();
		for(int count = 0; count < LEVELS_PER_BOOK; count++)
		{
			if(levels[count] != null)
			{
				double stringX = x + (count == 0? (-WIDTH / 4) : (WIDTH / 4)) - fontMetrics.stringWidth(levels[count])
						/ 2;
				double stringY = y - 150;
				g.drawString(levels[count], (int)stringX, (int)stringY);
			}
		}
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public double getVelX()
	{
		return velX;
	}

	public void setVelX(double velX)
	{
		this.velX = velX;
	}

	public double getAccelX()
	{
		return accelX;
	}

	public void setAccelX(double accelX)
	{
		this.accelX = accelX;
		if(accelX != 0)
		{
			timer = DURATION;
		}
	}
}