package cancernet.census.map;
import java.awt.*;
public class Record{
	int code_90;
	Shape shape=new Shape();
	float rate=-1;
	String area_name,cn_area_name;
	public void data_init(int area_code,String area_shape,String area_border,String map_area_name,String map_cn_area_name){
		code_90=area_code;
		int comma_position;
		area_name=new String(map_area_name);
		cn_area_name=new String(map_cn_area_name);
		String area_shape_split[]=area_shape.split("\\(\\(|\\),\\(|\\)\\)");
		shape.xCoords=new float[area_shape_split.length-1];
		shape.yCoords=new float[area_shape_split.length-1];
		for(int i=1;i<area_shape_split.length;i++){
			comma_position=area_shape_split[i].indexOf(",");
			shape.xCoords[i-1]=Float.parseFloat(area_shape_split[i].substring(0,comma_position));
			shape.yCoords[i-1]=Float.parseFloat(area_shape_split[i].substring(comma_position+1,area_shape_split[i].length()));
		}
		String area_border_split[]=area_border.split("\\(|\\),\\(|\\)");
		comma_position=area_border_split[1].indexOf(",");
		shape.extent.x=Float.parseFloat(area_border_split[1].substring(0,comma_position));
		shape.extent.y=Float.parseFloat(area_border_split[1].substring(comma_position+1,area_border_split[1].length()));
		comma_position=area_border_split[2].indexOf(",");
		shape.extent.x2=Float.parseFloat(area_border_split[2].substring(0,comma_position));
		shape.extent.y2=Float.parseFloat(area_border_split[2].substring(comma_position+1,area_border_split[2].length()));
	}
	public void draw_record(Renderer renderer,Graphics2D g2d,Projection prj){
		for(int i=renderer.symbols.size()-1;i>-1;i--){
			Symbol symbol_temp=(Symbol)renderer.symbols.get(i);
			if(rate>=symbol_temp.value){
				shape.paintShape(symbol_temp,g2d,prj);
				break;
			}
		}
	}
}
