package cancernet.census.map;

import java.io.*;
import java.util.Vector;
import java.util.Enumeration;
import javax.servlet.*;
import javax.servlet.http.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;
import java.sql.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

public class wms extends HttpServlet {
	Connection con = null;

	Dataset dataset = new Dataset();

	Project project = new Project();

	public void init() throws ServletException {
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/postgres");
			con = ds.getConnection();
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			saxParser.parse(new File(getServletContext().getRealPath("/")+ "china.xml"), project);
			dataset.data_init(con);
		} catch (SQLException e) {
		} catch (NamingException e) {
		} catch (IOException e) {
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=GB2312");

		if (request.getParameter("REQUEST").equals("GetPage"))
			getHtml(request, response);
		if (request.getParameter("REQUEST").equals("GetMap"))
			getMap(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void getMap(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		FloatRectangle bBox;
		int request_disease = Integer
				.parseInt(request.getParameter("d") == null ? "-1" : request
						.getParameter("d"));
		int request_sex = Integer
				.parseInt(request.getParameter("s") == null ? "-1" : request
						.getParameter("s"));
		Renderer renderer = project.getRenderer(request_sex, request_disease);
		String parBbox = request.getParameter("BBOX");
		try {
			bBox = FloatRectangle.makeExtent(parBbox);
		} catch (Exception e) {
			bBox = project.border;
		}
		Projection projection = Projection
				.getProjection(Integer.parseInt(request.getParameter("WIDTH")),
						Integer.parseInt(request.getParameter("HEIGHT")),
						project.border, bBox);
		dataset.update_record(request, con);
		dataset.drawMap(renderer, bBox, request, response, projection);
		// PrintWriter out = response.getWriter();
		/*
		 * response.setContentType("text/html;charset=GB2312");
		 * out.println(dataset.records.length); for(int i=0;i<dataset.records.length;i++){
		 * for(int ii=renderer.symbols.size()-1;ii>-1;ii--){ Symbol
		 * symbol_temp=(Symbol)renderer.symbols.get(ii);
		 * if(dataset.records[i].rate>=symbol_temp.value){
		 * out.println(dataset.records[i].rate);
		 * out.println("fill="+symbol_temp.fillColor);
		 * out.println("outline="+symbol_temp.outlineColor); break; } } }
		 */
	}

	public void destroy() {
		if (con != null)
			try {
				con.close();
			} catch (SQLException e) {
			}
	}

	public void getHtml(HttpServletRequest request, HttpServletResponse response) {
		String parBbox = request.getParameter("BBOX");
		int width = Integer.parseInt(request.getParameter("WIDTH"));
		int height = Integer.parseInt(request.getParameter("HEIGHT"));
		int currentTool = Integer
				.parseInt(request.getParameter("tool") == null ? "-1" : request
						.getParameter("tool"));
		int request_epoch = Integer
				.parseInt(request.getParameter("t") == null ? "-1" : request
						.getParameter("t"));
		int request_disease = Integer
				.parseInt(request.getParameter("d") == null ? "-1" : request
						.getParameter("d"));
		int request_sex = Integer
				.parseInt(request.getParameter("s") == null ? "-1" : request
						.getParameter("s"));
		String request_ratio = request.getParameter("ratio") == null ? "crude"
				: request.getParameter("ratio");
		Renderer renderer = project.getRenderer(request_sex, request_disease);
		FloatRectangle projectExtent = project.border;
		FloatRectangle newBox = null;
		FloatPoint shift = null;
		FloatPoint center = null;
		boolean isLinkTool = false;
		FloatRectangle bBox;
		try {
			bBox = FloatRectangle.makeExtent(parBbox);
		} catch (Exception e) {
			bBox = projectExtent;
		}
		bBox = FloatRectangle.setExtent(width, height, projectExtent, bBox,
				null);
		Projection projection = Projection.getProjection(width, height,
				projectExtent, bBox);
		try {
			int center_x = Integer.parseInt(request.getParameter("CENTER.x"));
			int center_y = Integer.parseInt(request.getParameter("CENTER.y"));
			center = FloatPoint.screenToMap(center_x, center_y, projection);
			dataset.PrintRecord(center, con, request, response);
			return;
		} catch (Exception exception2) {
		}
		float dx = Math.abs(bBox.x2 - bBox.x) / 3F;
		float dy = Math.abs(bBox.y2 - bBox.y) / 3F;
		try {
			Statement stmt = con.createStatement();
			ResultSet rs;
			InputStreamReader in = new InputStreamReader(
					new FileInputStream(getServletContext().getRealPath("/")
							+ "wms_template.html"), "GBK");
			boolean b = false;
			StringBuffer buf = new StringBuffer();
			StringBuffer bufElement = new StringBuffer();
			bBox = FloatRectangle.getExtent(width, height, projection);
			center = new FloatPoint((bBox.x + bBox.x2) / 2.0F,
					(bBox.y2 + bBox.y) / 2.0F);
			StringBuffer bufLink = new StringBuffer();
			StringBuffer bufLink_3 = new StringBuffer();
			StringBuffer bufForm = new StringBuffer();
			StringBuffer bufForm2 = new StringBuffer();
			for (Enumeration e = request.getParameterNames(); e
					.hasMoreElements();) {
				String s = (String) e.nextElement();
				if (!s.equalsIgnoreCase("CENTER.x")
						&& !s.equalsIgnoreCase("CENTER.y")
						&& !s.equalsIgnoreCase("BBOX")) {
					String sVal = new String(request.getParameter(s));
					if (!s.equalsIgnoreCase("REQUEST")
							&& !s.equalsIgnoreCase("TOOL")) {
						bufLink.append("&amp;").append(s).append('=').append(
								sVal);
						bufLink_3.append("&").append(s).append('=')
								.append(sVal);
					}
					bufForm.append("<input type=hidden name=\"").append(s)
							.append("\" value=\"").append(sVal).append("\">");
					if (!s.equalsIgnoreCase("THEME")
							&& !s.equalsIgnoreCase("ACTIVE")) {
						bufForm2.append("<input type=hidden name=\"").append(s)
								.append("\" value=\"").append(sVal).append(
										"\">");
					}
				}
			}

			bufForm.append("<input type=hidden name=\"").append("BBOX").append(
					"\" value=\"").append(bBox.x).append(',').append(bBox.y)
					.append(',').append(bBox.x2).append(',').append(bBox.y2)
					.append("\">");
			bufForm2.append("<input type=hidden name=\"").append("BBOX")
					.append("\" value=\"").append(bBox.x).append(',').append(
							bBox.y).append(',').append(bBox.x2).append(',')
					.append(bBox.y2).append("\">");
			int j;
			while ((j = in.read()) >= 0) {
				char ch = (char) j;
				if (b || ch == '$') {
					if (ch == '$' && bufElement.length() > 0) {
						String s4 = bufElement.toString();
						if (s4.equals("$WIDTH")) {
							buf.append(width);
							b = false;
						} else if (s4.equals("$HEIGHT")) {
							buf.append(height);
							b = false;
						} else if (s4.equals("$NW")) {
							shift = new FloatPoint(-dx, dy);
						} else if (s4.equals("$N")) {
							shift = new FloatPoint(0.0F, dy);
						} else if (s4.equals("$NE")) {
							shift = new FloatPoint(dx, dy);
						} else if (s4.equals("$E")) {
							shift = new FloatPoint(dx, 0.0F);
						} else if (s4.equals("$SE")) {
							shift = new FloatPoint(dx, -dy);
						} else if (s4.equals("$S")) {
							shift = new FloatPoint(0.0F, -dy);
						} else if (s4.equals("$SW")) {
							shift = new FloatPoint(-dx, -dy);
						} else if (s4.equals("$W")) {
							shift = new FloatPoint(-dx, 0.0F);
						} else if (s4.equals("$TARGET")) {
							if (currentTool == 1) {
								buf.append("target=\"_blank\"");
							}
							b = false;
						} else if (s4.equals("$MAPIMG") /*
														 * ||
														 * s4.equals("$LEGEND")
														 */) {
							buf.append('"').append("wms?BBOX=").append(bBox.x)
									.append(',').append(bBox.y).append(',')
									.append(bBox.x2).append(',')
									.append(bBox.y2).append("&REQUEST=GetMap")
									.append(bufLink_3).append('"');
							b = false;
						} else if (s4.equals("$MAPHTM")) {
							buf.append(bufForm);
							b = false;
						} else if (s4.equals("$EPOCH")) {
							if (stmt != null)
								try {
									rs = stmt.executeQuery("select * from CENSUS.query_info");
									while (rs.next())
										buf.append("<option value="
														+ rs.getInt(1)
														+ (rs.getInt(1) == request_epoch ? " selected"
																: "")
														+ ">"
														+ rs.getString(3)
																.trim()
														+ "</option>");
									rs.close();
								} catch (SQLException e) {
								}
							b = false;
						} else if (s4.equals("$DISEASE")) {
							if (stmt != null)
								try {
									rs = stmt
											.executeQuery("select * from CENSUS.mapccd where epoch="
													+ request_epoch
													+ " order by ccd_id");
									while (rs.next())
										buf.append("<option value="
														+ rs.getInt(1)
														+ (rs.getInt(1) == request_disease ? " selected"
																: "") + ">"
														+ rs.getString(2)
														+ "</option>");
									rs.close();
								} catch (SQLException e) {
								}
							b = false;
						} else if (s4.equals("$LEGEND")) {
							for (int jjj = 0; jjj < renderer.symbols.size(); jjj++) {
								Symbol symbol_t = (Symbol) renderer.symbols
										.get(jjj);
								buf.append("<font style=\"background-Color: rgb("
										+ symbol_t.fillColor.getRed() + ","
										+ symbol_t.fillColor.getGreen() + ","
										+ symbol_t.fillColor.getBlue()
										+ ")\"> &#160; </font><font size=2> "
										+ symbol_t.label + " </font><br>");
							}
							b = false;
						} else if (s4.equals("$SECTION")) {
							buf.append("<input type=\"radio\" name=\"s\" value=\"2\""
											+ (request_sex == 2 ? " checked"
													: "")
											+ ">女<br><input type=\"radio\" name=\"s\" value=\"1\""
											+ (request_sex == 1 ? " checked"
													: "") + ">男");
							b = false;
						} else if (s4.equals("$RATIO")) {
							buf.append("<select name=ratio size=3>\n");
							buf.append("<option value='crude'"
											+ (request_ratio.equals("crude") ? " selected"
													: "")
											+ ">粗&nbsp;&nbsp;率</option>\n");
							buf.append("<option value='mrete64'"
											+ (request_ratio.equals("mrete64") ? " selected"
													: "") + ">中调率</option>\n");
							buf.append("<option value='mretewld'"
											+ (request_ratio.equals("mretewld") ? "selected"
													: "") + ">世调率</option>\n");
							buf.append("<option value='mrete64bys'"
											+ (request_ratio
													.equals("mrete64bys") ? "selected"
													: "")
											+ ">bayes(china64)</option>\n");
							buf.append("<option value='mretewldbys'"
											+ (request_ratio
													.equals("mretewldbys") ? "selected"
													: "")
											+ ">bayes(world)</option>\n");
							buf.append("</select>\n");
							b = false;
						} else if (s4.equals("$Z1")) {
							newBox = projectExtent.scale(1.0F);
						} else if (s4.equals("$Z2")) {
							newBox = projectExtent.scale(0.25F);
						} else if (s4.equals("$Z3")) {
							newBox = projectExtent.scale(0.125F);
						} else if (s4.equals("$Z4")) {
							newBox = projectExtent.scale(0.0625F);
						} else if (s4.equals("$Z5")) {
							newBox = projectExtent.scale(0.015625F);
						} else {
							buf.append(s4);
							b = false;
						}
						if (b) {
							if (shift != null) {
								newBox = new FloatRectangle(bBox.x + shift.x,
										bBox.y + shift.y, bBox.x2 + shift.x,
										bBox.y2 + shift.y);
								newBox = FloatRectangle.setExtent(width,
										height, projectExtent, newBox, null);
							} else {
								newBox = FloatRectangle.setExtent(width,
										height, projectExtent, newBox, center);
							}
							buf.append("wms?BBOX=").append(newBox.x)
									.append(',').append(newBox.y).append(',')
									.append(newBox.x2).append(',').append(
											newBox.y2).append(
											"&amp;tool=" + currentTool).append(
											"&amp;REQUEST=GetPage").append(
											bufLink);
							newBox = null;
							shift = null;
						}
						b = false;
						bufElement.setLength(0);
					} else {
						b = true;
						bufElement.append(ch);
					}
				} else {
					buf.append(ch);
				}
			}
			in.close();
			response.setContentType("text/html;charset=GB2312");
			PrintWriter out = response.getWriter();
			out.print(buf.toString());
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		} catch (SQLException e) {
			System.err.println(e);
		}
	}
}
