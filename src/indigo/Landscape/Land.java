package indigo.Landscape;

import indigo.Stage.Stage;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class Land
{
	protected Stage stage;

	protected Line2D.Double line;
	protected Polygon hitbox;

	protected boolean horizontal;
	protected double slope;
	protected double angle;

	protected double minX;
	protected double maxX;

	public static final int THICKNESS = 30;

	public Land(Stage stage, double x1, double y1, double x2, double y2)
	{
		this.stage = stage;

		line = new Line2D.Double(x1, y1, x2, y2);

		horizontal = (Math.abs(x2 - x1) >= Math.abs(y2 - y1))? true : false;
		slope = (y2 - y1) / ((x2 - x1) == 0? 0.0000001 : (x2 - x1));
		angle = Math.atan(slope);
		minX = Math.min(x1, x2);
		maxX = Math.max(x1, x2);

		int[] xPoints = {(int)(x1 + Math.sin(angle) * THICKNESS / 2), (int)(x1 - Math.sin(angle) * THICKNESS / 2),
				(int)(x2 - Math.sin(angle) * THICKNESS / 2), (int)(x2 + Math.sin(angle) * THICKNESS / 2)};
		int[] yPoints = {(int)(y1 - Math.cos(angle) * THICKNESS / 2), (int)(y1 + Math.cos(angle) * THICKNESS / 2),
				(int)(y2 + Math.cos(angle) * THICKNESS / 2), (int)(y2 - Math.cos(angle) * THICKNESS / 2)};
		hitbox = new Polygon(xPoints, yPoints, 4);
	}

	public double getMinX()
	{
		return minX;
	}

	public double getMaxX()
	{
		return maxX;
	}

	public double getDeltaX()
	{
		return maxX - minX;
	}

	public double getDeltaY()
	{
		return Math.abs(line.getY1() - line.getY2());
	}

	public Line2D.Double getLine()
	{
		return line;
	}

	public double getSlope()
	{
		return slope;
	}

	public boolean isHorizontal()
	{
		return horizontal;
	}

	public double getLength()
	{
		return Math.sqrt(Math.pow(getDeltaX(), 2) + Math.pow(getDeltaY(), 2));
	}

	public Polygon getHitbox()
	{
		return hitbox;
	}

	public double getSurface(double x, boolean top)
	{
		if(top)
		{
			return slope * (x - line.getX1()) + line.getY1() - (double)(THICKNESS / 2) / Math.cos(angle) - 1;
		}
		else
		{
			return slope * (x - line.getX1()) + line.getY1() + (double)(THICKNESS / 2) / Math.cos(angle) + 1;
		}
	}

	public double getDistance(Point2D.Double point)
	{
		double minDistance = -1;
		PathIterator polyIt = hitbox.getPathIterator(null); // Getting an iterator along the polygon path
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
					minDistance = minDistance == -1? currentLine.ptSegDist(point) : Math.min(minDistance,
							currentLine.ptSegDist(point));
					lastCoords[0] = coords[0];
					lastCoords[1] = coords[1];
					break;
				}
				case PathIterator.SEG_CLOSE:
				{
					Line2D.Double currentLine = new Line2D.Double(coords[0], coords[1], firstCoords[0], firstCoords[1]);
					minDistance = minDistance == -1? currentLine.ptSegDist(point) : Math.min(minDistance,
							currentLine.ptSegDist(point));
					break;
				}
			}
			polyIt.next();
		}
		return minDistance;
	}

	public Point2D.Double getLineIntersection(Line2D.Double line)
	{
		return getIntersection(this.line, line);
	}

	public Point2D getHitboxIntersection(Line2D.Double travelLine)
	{
		Point2D.Double prevPos = new Point2D.Double(travelLine.getX1(), travelLine.getY1());
		ArrayList<Point2D> intersections = getHitboxIntersections(travelLine);
		if(intersections.size() == 0)
		{
			return new Point2D.Double(travelLine.getX1(), travelLine.getY1());
		}

		Point2D minDistancePoint = intersections.get(0);
		double minDistance = Math.pow(prevPos.getX() - minDistancePoint.getX(), 2)
				+ Math.pow(prevPos.getY() - minDistancePoint.getY(), 2);
		for(Point2D point : intersections)
		{
			double distance = Math.pow(prevPos.getX() - point.getX(), 2) + Math.pow(prevPos.getY() - point.getY(), 2);
			if(distance < minDistance)
			{
				minDistance = distance;
				minDistancePoint = point;
			}
		}
		return minDistancePoint;
	}

	private ArrayList<Point2D> getHitboxIntersections(Line2D.Double line)
	{
		PathIterator polyIt = hitbox.getPathIterator(null); // Getting an iterator along the polygon path
		double[] coords = new double[6]; // Double array with length 6 needed by iterator
		double[] firstCoords = new double[2]; // First point (needed for closing polygon path)
		double[] lastCoords = new double[2]; // Previously visited point
		ArrayList<Point2D> intersections = new ArrayList<Point2D>(); // List to hold found intersections
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
					if(currentLine.intersectsLine(line))
					{
						intersections.add(getIntersection(currentLine, line));
					}
					lastCoords[0] = coords[0];
					lastCoords[1] = coords[1];
					break;
				}
				case PathIterator.SEG_CLOSE:
				{
					Line2D.Double currentLine = new Line2D.Double(coords[0], coords[1], firstCoords[0], firstCoords[1]);
					if(currentLine.intersectsLine(line))
					{
						intersections.add(getIntersection(currentLine, line));
					}
					break;
				}
			}
			polyIt.next();
		}
		return intersections;
	}

	private static Point2D.Double getIntersection(Line2D.Double line1, Line2D.Double line2)
	{
		double px = line1.getX1(), py = line1.getY1(), rx = line1.getX2() - px, ry = line1.getY2() - py;
		double qx = line2.getX1(), qy = line2.getY1(), sx = line2.getX2() - qx, sy = line2.getY2() - qy;

		// Parallel lines
		if(rx == sx)
		{
			if(line1.ptSegDist(new Point2D.Double(px, py)) == 0)
			{
				return new Point2D.Double(line1.getX1(), line1.getY1());
			}
			else
			{
				return new Point2D.Double(line1.getX2(), line1.getY2());
			}
		}
		
		double det = sx * ry - sy * rx;
		double z = (sx * (qy - py) + sy * (px - qx)) / det;
		if(z == 0 || z == 1)
		{
			// Intersection at  end point
		}
		return new Point2D.Double((px + z * rx), (py + z * ry));
	}
}