package cancernet.census.map;
public class Projection
{
	public FloatPoint shift;
	public float zoom;

	public Projection()
	{
		shift = new FloatPoint(0.0F, 0.0F);
		zoom = 1.0F;
	}

	Projection(float z, float x, float y)
	{
		shift = new FloatPoint(x, y);
		zoom = z;
	}
	public static Projection getProjection(int nw, int nh, FloatRectangle extMax, FloatRectangle ext)
	{
		float w2 = Math.abs(ext.x2 - ext.x);
		float h2 = Math.abs(ext.y2 - ext.y);
		float z1 = 0;
		float z2 = 0;
		float pz;
		z1 = w2 <= Math.abs(extMax.x2 - extMax.x) ? w2 : Math.abs(extMax.x2 - extMax.x);
		z2 = h2 <= Math.abs(extMax.y2 - extMax.y) ? h2 : Math.abs(extMax.y2 - extMax.y);
		z1 = (float)nw / z1;
		z2 = (float)nh / z2;
		pz = z1 <= z2 ? z1 : z2;
		Projection projection = new Projection();
		projection.zoom = pz;
		projection.shift.x = ((float)nw / pz - (ext.x2 - ext.x)) / 2.0F - ext.x;
		projection.shift.y = ext.y2 + ((float)nh / pz - (ext.y2 - ext.y)) / 2.0F;
		return projection;
	}
}
