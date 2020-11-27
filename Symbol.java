package cancernet.census.map;
import java.awt.Color;
public class Symbol
{
	public int style=1;
	public boolean fill=true;
	public boolean outline=true;
	public Color fillColor;
	public Color outlineColor;
	public int size;
	public int step;
	public boolean legendVisible;
	public float value;
	public String label;

	public Symbol()
	{
		this(Color.white, Color.black, true, true, 1);
	}

	public Symbol(Color fillColor, Color outlineColor, boolean bFill, boolean bLine, int size)
	{
		legendVisible = true;
		this.fillColor = fillColor;
		this.outlineColor = outlineColor;
		fill = bFill;
		outline = bLine;
		this.size = size;
	}
}
