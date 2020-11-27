package cancernet.census.map;
import java.util.Vector;

// Referenced classes of package org.alov.map:
//            Symbol, FieldValueFilter, LayerVector, Shape, 
//            Layer, DisplayContext, Record

public class Renderer
{
    public boolean equal;
    public int id;
    public String label;
    public boolean legendVisible;
    public Vector sex;
    public Vector disease;
    public Vector symbols;
    public Renderer(){
    	disease= new Vector(1,1);
    	symbols=new Vector<Symbol>(8,2);
    	sex=new Vector(2,1);
    }
}
