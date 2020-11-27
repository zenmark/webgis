package cancernet.census.map;
import java.util.Vector;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;
public class Dataset{
	public Record[] records;
	//int sex=-1;
	//int disease=-1;
	//int epoch=-1;
	String sex=new String("-1");
	String disease=new String("-1");
	String epoch=new String("-1");
	String ratio=new String("-1");
	public Dataset(){
	}
	void update_record(HttpServletRequest request,Connection con) throws IOException{
		String request_epoch=new String(request.getParameter("t")==null?"-1":request.getParameter("t"));
		String request_disease=new String(request.getParameter("d")==null?"-1":request.getParameter("d"));
		String request_sex=new String(request.getParameter("s")==null?"-1":request.getParameter("s"));
		String request_ratio=new String(request.getParameter("ratio")==null?"crude":request.getParameter("ratio"));

		if(true){
			//out.println("select gbcode,"+request_ratio+" from census.cal4 where query_id="+request_epoch+" and sex="+request_sex+" and ccd_id="+request_disease+" order by gbcode");
			try{
				Statement stmt=con.createStatement();
				ResultSet rateresult=stmt.executeQuery("select gbcode,"+request_ratio+" from census.cal4 where query_id="+request_epoch+" and sex="+request_sex+" and ccd_id="+request_disease+" order by gbcode");
				int i=0;
				while(rateresult.next()){
					int gbcode=rateresult.getInt("gbcode");
					//out.println("gbcode="+gbcode);
					while(i<records.length){
						//out.println("code_90="+records[i].code_90);
						if(records[i].code_90==gbcode){
							records[i].rate=rateresult.getFloat(2);
							if(records[i].rate==0)
								if(rateresult.getString(2)==null)
									records[i].rate=-0.2F;
						}
						if(records[i].code_90>gbcode){
							if(rateresult.isLast())
								records[i].rate=-0.2F;
							else
								break;
						}
						if(records[i].code_90<gbcode){
							records[i].rate=-0.2F;
						}
						i++;
					}
				}
				rateresult.close();
				if(i==0)
					for(i=0;i<records.length;i++)
						records[i].rate=-0.2F;
			}
			catch(SQLException e){}
			epoch=null;epoch=new String(request_epoch);
			disease=null;disease=new String(request_disease);
			sex=null;sex=new String(request_sex);
			ratio=null;
			ratio=new String(request_ratio);
		}
	}
	public void drawMap(Renderer renderer,FloatRectangle bBox,HttpServletRequest request, HttpServletResponse response,Projection projection){
		try{
			int width=Integer.parseInt(request.getParameter("WIDTH"));
			int height=Integer.parseInt(request.getParameter("HEIGHT"));
			BufferedImage bufferedImage = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D)bufferedImage.getGraphics();
    			g2d.setColor(Color.white);
    			g2d.fillRect(0, 0, width, height);
			for(int i=0;i<records.length;i++){
				if(records[i].shape.extent.intersects(bBox)){
					records[i].draw_record(renderer,g2d,projection);
				}
			}
			g2d.dispose();
			response.setContentType("image/jpeg");
			OutputStream out=response.getOutputStream();
			ImageIO.write((RenderedImage)bufferedImage,"jpeg",out);
		}
		catch(IOException e){}
	}
	public void PrintRecord(FloatPoint center,Connection con,HttpServletRequest request,HttpServletResponse response)
		throws IOException,SQLException{
		int request_epoch=Integer.parseInt(request.getParameter("t")==null?"-1":request.getParameter("t"));
		int request_disease=Integer.parseInt(request.getParameter("d")==null?"-1":request.getParameter("d"));
		int request_sex=Integer.parseInt(request.getParameter("s")==null?"-1":request.getParameter("s"));
		int i;
		StringBuffer outputbuffer=new StringBuffer();
		for(i=0;i<records.length;i++)
			if(records[i].shape.extent.contain(center))
				if(records[i].shape.containsPoint(center.x,center.y))
					break;
		outputbuffer.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>\n<?xml-stylesheet type=\"text/xsl\" href=\"../../../data/epi/census/wmsrecord.xsl\"?>\n<results>");
		if(i!=0 && i!=records.length){
			//outputbuffer.append("select * from census.cal4 where epoch="+request_epoch+" and ccd_id="+request_disease+" and sex="+request_sex+" and gbcode="+records[i].code_90);
			Statement stmt=con.createStatement();
			ResultSet recordresult=stmt.executeQuery("select * from census.cal4 where query_id="+request_epoch+" and ccd_id="+request_disease+" and sex="+request_sex+" and gbcode="+records[i].code_90);
			//outputbuffer.append(records[i].code_90+"-"+records[i].rate);
			while(recordresult.next()){
				outputbuffer.append("<record>\n").append("<cnareaname>"+records[i].cn_area_name+"</cnareaname>");
				outputbuffer.append("<areaname>"+records[i].area_name+"</areaname>").append("<crude>"+recordresult.getString("crude")+"</crude>\n");
				outputbuffer.append("<mrete64>"+recordresult.getString("mrete64")+"</mrete64>\n");
				outputbuffer.append("<mretewld>"+recordresult.getString("mretewld")+"</mretewld>\n");
				outputbuffer.append("<mrete64bys>"+recordresult.getString("mrete64bys")+"</mrete64bys>\n");
				outputbuffer.append("<mretewldbys>"+recordresult.getString("mretewldbys")+"</mretewldbys>\n");
				outputbuffer.append("</record>");
			}
			recordresult.close();
		}
		outputbuffer.append("</results>");
		PrintWriter out = response.getWriter();
		response.setContentType("text/xml; charset=gb2312");
		out.println(outputbuffer.toString());
	}
	void data_init(Connection con){
		Vector  records_temp=new Vector(2960,10);
		try{
			Statement stmt=con.createStatement();
			ResultSet mapresult=stmt.executeQuery("select code_90,shape,border,area_name,cn_area_name from census.china_country_map order by code_90");
			while(mapresult.next()){
				Record temp_record=new Record();
				temp_record.data_init(mapresult.getInt("code_90"),mapresult.getString("shape"),mapresult.getString("border"),mapresult.getString("area_name"),mapresult.getString("cn_area_name"));
				records_temp.addElement(temp_record);
			}
			mapresult.close();
		}
		catch(SQLException e){}
		records=new Record[records_temp.size()];
		records=(Record[])records_temp.toArray(records);
	}
}
