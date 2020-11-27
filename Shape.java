package cancernet.census.map;
import java.awt.*;
public class Shape
{
	private static final int MARKER_CIRCLE = 0;
	private static final int MARKER_SQUARE = 1;
	private static final int MARKER_TRIANGLE = 2;
	private static final int MARKER_CROSS = 3;
	private static final int LINE_SOLID = 0;
	private static final int LINE_DASH = 1;
	private static final int LINE_DOT = 2;
	private static final int LINE_DASHDOT = 3;
	private static final int LINE_DASHDOTDOT = 4;
	private static final int LINE_STRIPE = 5;
	public float xCoords[];
	public float yCoords[];
	public FloatRectangle extent;
	public Shape()
	{
		xCoords = null;
		yCoords = null;
		extent = new FloatRectangle();
	}
	boolean containsPoint(float x, float y){
		int sc = 0;
		int len = xCoords.length;
		float p1x = xCoords[len - 1];
		float p1y = yCoords[len - 1];
		for(int i = 0; i < len; i++){
	    float p2x = xCoords[i];
	    float p2y = yCoords[i];
	    if((x < p1x || x < p2x) && (y < p1y && y >= p2y || y < p2y && y >= p1y)){
				if(x < p1x && x < p2x){
			    sc++;
				} 
				else if(((y - p1y) * (p2x - p1x)) / (p2y - p1y) + p1x > x){
			    sc++;
				}
	    }
	    p1x = p2x;
	    p1y = p2y;
		}
		return (sc & 1) != 0;
	}
	void paintShape(Symbol sym,Graphics2D g, Projection prj)
	{
		try
		{
			int pointCount = xCoords.length;
			int xCoords2[] = new int[xCoords.length];
			int yCoords2[] = new int[yCoords.length];
			for(int i = 0; i < pointCount; i++)
			{
				xCoords2[i] = (int)((xCoords[i] + prj.shift.x) * prj.zoom);
				yCoords2[i] = (int)((-yCoords[i] + prj.shift.y) * prj.zoom);
			}

			switch(sym.style)
			{
				default:
					break;
				case 1: // '\003'
					if(sym.fill)
					{
						g.setColor(sym.fillColor);
						g.fillPolygon(xCoords2, yCoords2, pointCount);
					}
					if(sym.outline)
 					{
						g.setColor(sym.outlineColor);
  					g.drawPolygon(xCoords2, yCoords2, pointCount);
					}
					break;
			}
		}
		catch(Exception exception) { }
	}
}
