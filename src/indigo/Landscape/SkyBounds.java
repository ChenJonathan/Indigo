package indigo.Landscape;

import indigo.Stage.Stage;

public class SkyBounds extends Wall
{
	public SkyBounds(Stage stage, double x1, double y1, double x2, double y2)
	{
		super(stage, x1, y1, x2, y2);

		name = "the sky";

		blocksEntities = true;
		blocksNonsolidProjectiles = false;
		blocksSolidProjectiles = false;

		killsEntities = false;
		killsNonsolidProjectiles = true;
		killsSolidProjectiles = false;
	}

}