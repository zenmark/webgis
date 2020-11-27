package cancernet.census.map;
import java.io.*;
import java.util.Vector;
import org.xml.sax.*;
import java.awt.Color;
import org.xml.sax.helpers.DefaultHandler;
public class Project extends DefaultHandler
{
	public Vector layers;
	FloatRectangle border;
	int zoomunits;
	float zmin=9;
	
	Layer layertemp;
	Renderer renderertemp;
	Symbol symboltemp;
	
	public Project(){
		border=new FloatRectangle();
		layers=new Vector(1,1);
  }
  public void startDocument(){
  }
  public void startElement(String namespaceURI,String localName,String fullName,Attributes attributes){
  	if(fullName=="project"){
  		if(attributes!=null)
  			for(int i=0;i<attributes.getLength();i++){
  				if(attributes.getQName(i)=="zoomunits")
  					this.zoomunits=Integer.parseInt(attributes.getValue(i));
  				if(attributes.getQName(i)=="zmin")
  					zmin=Float.parseFloat(attributes.getValue(i));
  			}
  	}
  	if(fullName=="domain")
  		if(attributes!=null)
  			for(int i=0;i<attributes.getLength();i++){
  				if(attributes.getQName(i)=="xmin")
  					this.border.x=Float.parseFloat(attributes.getValue(i));
  				if(attributes.getQName(i)=="ymin")
  					this.border.y=Float.parseFloat(attributes.getValue(i));
  				if(attributes.getQName(i)=="xmax")
  					this.border.x2=Float.parseFloat(attributes.getValue(i));
  				if(attributes.getQName(i)=="ymax")
  					this.border.y2=Float.parseFloat(attributes.getValue(i));
  			}
  	if(fullName=="layer")
  		layertemp=new Layer();
  	if(fullName=="renderer"){
  		renderertemp=new Renderer();
  		for(int i=0;i<attributes.getLength();i++){
  			if(attributes.getQName(i)=="sex"){
  				String sex_split[]=attributes.getValue(i).split(",");
  				for(int ii=0;ii<sex_split.length;ii++)
  					renderertemp.sex.addElement(new Integer(sex_split[ii]));
  			}
  			if(attributes.getQName(i)=="disease"){
  				String disease_split[]=attributes.getValue(i).split(",");
  				for(int ii=0;ii<disease_split.length;ii++)
  					renderertemp.disease.addElement(new Integer(disease_split[ii]));
  			}
  		}
  	}
  	if(fullName=="symbol"){
  		symboltemp=new Symbol();
  		for(int i=0;i<attributes.getLength();i++){
  			if(attributes.getQName(i)=="val")
  				symboltemp.value=Float.parseFloat(attributes.getValue(i));
  			if(attributes.getQName(i)=="label")
  				symboltemp.label=new String(attributes.getValue(i));
  			if(attributes.getQName(i)=="fill" || attributes.getQName(i)=="outline"){
  				String color_split[]=attributes.getValue(i).split(":");
  				if(color_split.length==3){
  					if(attributes.getQName(i)=="fill")
  						symboltemp.fillColor=new Color(Integer.parseInt(color_split[0]),Integer.parseInt(color_split[1]),Integer.parseInt(color_split[2]));
  					if(attributes.getQName(i)=="outline")
  						symboltemp.outlineColor=new Color(Integer.parseInt(color_split[0]),Integer.parseInt(color_split[1]),Integer.parseInt(color_split[2]));
  				}
  			}		
  		}
  	}
  }
  public void endElement(String namespaceURI,String localName,String fullName){
  	if(fullName=="layer")
  		if(layertemp!=null){
  			layers.addElement(layertemp);
  			layertemp=null;
  		}
  	if(fullName=="renderer")
  		if(renderertemp!=null && layertemp!=null){
  			layertemp.renderers.addElement(renderertemp);
  			renderertemp=null;
  		}
  	if(fullName=="symbol")
  		if(renderertemp!=null && symboltemp!=null){
  			renderertemp.symbols.addElement(symboltemp);
  			symboltemp=null;
  		}	
  }
  public void characters(char[] buffer,int start,int length) throws SAXException{
  }
  public Renderer getRenderer(int request_sex,int request_disease){
  	for(int i=0;i<layers.size();i++){
  		layertemp=(Layer)layers.get(i);
  		for(int ii=0;ii<layertemp.renderers.size();ii++){
  			renderertemp=(Renderer)layertemp.renderers.get(ii);
  			if(renderertemp.disease.contains(new Integer(request_disease)) && renderertemp.sex.contains(new Integer(request_sex)))
  				return renderertemp;
  			if(renderertemp.disease.contains(new Integer(-1)) && ii==layertemp.renderers.size()-1)
  				return renderertemp;
  		}
  	}
  	return null;
  }
}
