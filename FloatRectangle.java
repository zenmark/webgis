package cancernet.census.map;
import java.util.StringTokenizer;
public class FloatRectangle
{

	public float x;
	public float y;
	public float x2;
	public float y2;

	public FloatRectangle()
	{
	}

	public FloatRectangle(FloatRectangle rec)
	{
		setBounds(rec.x, rec.y, rec.x2, rec.y2);
	}

	public FloatRectangle(float x1, float y1, float x2, float y2)
	{
		setBounds(x1, y1, x2, y2);
	}

	public void setBounds(float x1, float y1, float x2, float y2)
	{
		x = x1;
		y = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public boolean isZero()
	{
		return x == 0.0F && y == 0.0F && x2 == 0.0F && y2 == 0.0F;
	}

	public boolean intersects(FloatRectangle r)
	{
		float x1 = x>r.x?x:r.x;
		float y1 = y>r.y?y:r.y;
		float x3 = x2<r.x2?x2:r.x2;
		float y3 = y2<r.y2?y2:r.y2;
		return !((x3 >= x1) && (y3 >= y1));
	}
	
	public boolean in(FloatRectangle r){
		return r.x2 > x && r.y2 > y && r.x < x2 && r.y < y2;
	}
	
	public boolean contain(FloatPoint pt)
	{
		return pt.x >= x2 && pt.y >= y2 && pt.x <= x && pt.y <= y;
	}

	public FloatRectangle intersection(FloatRectangle r)
	{
		float x1 = Math.max(x, r.x);
		float x2 = Math.min(this.x2, r.x2);
		float y1 = Math.max(y, r.y);
		float y2 = Math.min(this.y2, r.y2);
		if(x2 - x1 < 0.0F || y2 - y1 < 0.0F)
		{
			return null;
		} else
		{
			return new FloatRectangle(x1, y1, x2, y2);
		}
	}

	public FloatRectangle union(FloatRectangle r)
	{
		if(r == null)
		{
			return this;
		} else
		{
			float x1 = Math.min(x, r.x);
			float x2 = Math.max(this.x2, r.x2);
			float y1 = Math.min(y, r.y);
			float y2 = Math.max(this.y2, r.y2);
			return new FloatRectangle(x1, y1, x2, y2);
		}
	}

	public void add(float newx, float newy)
	{
		x = Math.min(x, newx);
		x2 = Math.max(x2, newx);
		y = Math.min(y, newy);
		y2 = Math.max(y2, newy);
	}

	public FloatRectangle scale(float s)
	{
		float w = ((x2 - x) * s) / 2.0F;
		float h = ((y2 - y) * s) / 2.0F;
		float x0 = (x2 + x) / 2.0F;
		float y0 = (y2 + y) / 2.0F;
		return new FloatRectangle(x0 - w, y0 - h, x0 + w, y0 + h);
	}

	public String toString()
	{
		return "(" + x + ", " + y + ") - (" + x2 + ", " + y2 + ")";
	}
	public static FloatRectangle setExtent(int nw, int nh, FloatRectangle extMax, FloatRectangle ext, FloatPoint center)
	{
		float w2 = Math.abs(ext.x2 - ext.x);
		float h2 = Math.abs(ext.y2 - ext.y);
		if(center != null)
		{
			w2 /= 2.0F;
			h2 /= 2.0F;
			ext.x = center.x - w2;
			ext.y = center.y - h2;
			ext.x2 = center.x + w2;
			ext.y2 = center.y + h2;
		}
		if(extMax != null)
		{
			float x1 = extMax.x;
			float y1 = extMax.y;
			float x2 = extMax.x2;
			float y2 = extMax.y2;
			float w = x2 - x1;
			float h = y2 - y1;
			w2 = ext.x2 - ext.x;
			h2 = ext.y2 - ext.y;
			if(w < w2)
			{
				ext.x = ((x1 + x2) - w2) / 2.0F;
				ext.x2 = ext.x + w2;
			} else
			if(ext.x < x1)
			{
				ext.x = x1;
				ext.x2 = x1 + w2;
			} else
			if(ext.x2 > x2)
			{
				ext.x2 = x2;
				ext.x = x2 - w2;
			}
			if(h < h2)
			{
				ext.y = ((y1 + y2) - y2) / 2.0F;
				ext.y2 = ext.y + y2;
			} else
			if(ext.y < y1)
			{
				ext.y = y1;
				ext.y2 = y1 + h2;
			} else
			if(ext.y2 > y2)
			{
				ext.y2 = y2;
				ext.y = y2 - h2;
			}
		}
		return ext;
	}
	
	public static FloatRectangle getExtent(int width, int height, Projection projection){
		FloatRectangle ext = new FloatRectangle();
		ext.x = -projection.shift.x;
		ext.y2 = projection.shift.y;
		ext.x2 = (float)(width - 1) / projection.zoom - projection.shift.x;
		ext.y = -((float)(height - 1) / projection.zoom - projection.shift.y);
		return ext;
	}
	
	public static FloatRectangle makeExtent(String bbox) throws Exception
	{
		if(bbox == null)
		{
			throw new Exception("Bounding box not specified");
		}
 		try
		{
			StringTokenizer tok = new StringTokenizer(bbox, ",");
			float xmin = Float.parseFloat(tok.nextToken());
			float ymin = Float.parseFloat(tok.nextToken());
			float xmax = Float.parseFloat(tok.nextToken());
			float ymax = Float.parseFloat(tok.nextToken());
			if(xmax <= xmin || ymax <= ymin){
				throw new Exception("Invalid bounding box values specified.");
			} 
			else{
				return new FloatRectangle(xmin, ymin, xmax, ymax);
			}
		}
		catch(Exception exception){
			throw new Exception("Invalid BBOX parameter value.");
		}
	}
}
