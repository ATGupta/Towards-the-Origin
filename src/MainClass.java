import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MainClass
 */
@WebServlet("/tto")
public class MainClass extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String parentDir;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainClass() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter sw = response.getWriter();
		// pw.append("Served at: ").append(request.getContextPath());

		ServletContext context = getServletContext();
		parentDir = context.getRealPath("Resources_TTO_ATG");
		
		String s = request.getParameter("s");
		String un = request.getParameter("un");
		String ch = request.getParameter("ch");

		if(s==null && un==null)
			createWebpage("data", sw);
		else if (s == null && un != null)
			createWebpage(un + "/" + ch + "/data", sw);
		else
			createWebpage(s + "/" + un + "/" + ch + "/data", sw);
			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private void createWebpage(String path, PrintWriter sw) throws IOException {
		printData(readFun("page_top"), sw, path);
		printData(readFun(path), sw, path);
		printData(readFun("page_bottom"), sw, path);
	}

	private BufferedReader readFun(String path) throws IOException {
		path = parentDir + "/" + path;
		BufferedReader br = new BufferedReader(new FileReader(path));
		return br;
	}

	private void printData(BufferedReader br, PrintWriter sw, String path) throws IOException {
		path = path.substring(0, path.length() - 4);// remove "data" file
		for (;;) {
			String st = br.readLine();
			if (st == null)
				break;

			if (st.startsWith("\\takefrom")) {
				takeFrom(st, sw);
				continue;
			} else if (st.startsWith("\\image")) {
				st = st.substring(7);
				st = "Resources_TTO_ATG/" + path + st;// path already contains "/" at the end
				sw.println("<p style=\"text-align: center\"><img style=\"max-height: 300px; max-width: 80%;\" src=\"" + st + "\">");
				continue;
			}
			sw.println(st);
		}
	}

	private void takeFrom(String st, PrintWriter sw) throws IOException {
		st = st.substring(9);
		if (st.equals("chapterMenu")) {
			takeFromChapterMenu(st, sw);
		}
	}

	int row = 1;
	private void takeFromChapterMenu(String st, PrintWriter sw) throws IOException {
		BufferedReader br = readFun(st);
		int tab = -1;
		String unit = "",subject="",prev_s=null;

		Vector<String> chapters = new Vector<String>();

		for (;;) {
			String a = br.readLine();

			if (a == null) {
				if (tab == 2)
					writeChapters(subject, sw, chapters);

				sw.println("</tbody>");
				sw.println("</table>");
				sw.println("<p><marquee><i><b>Click on the name of a chaper to continue.</i></b></marquee>");
				sw.println("<p>");
				break;
			}

			if (a.startsWith("\t\t")) {
				a = a.substring(2);
				chapters.add(a);
				tab = 2;
			} else if (a.startsWith("\t")) {
				a = a.substring(1);

				if (tab == 2)
					writeChapters(subject, sw, chapters);
				
				tab = 1;
				sw.println("<tr>");
				sw.println("<td>"+row++);
				sw.println("<td rowspan=");
				chapters.add(a);
			} else {
				prev_s=subject;
				subject=a;
				a=a.toUpperCase();
				if (tab == -1) {
					sw.println("<p><marquee><i><b>Click on the name of a chaper to continue.</i></b></marquee>");
					sw.println("<p>");
					sw.println("<h2 style=\"text-align: center\">" + a + "</h2>");
					sw.println(
									  "<p>\n"
									+ "<table border=1 style=\"border-collapse:collapse\">\n"
									+ "	<tbody>\n"
									+ "		<tr>\n"
									+ "			<th>S. NO.\n"
									+ "			<th>UNIT\n"
									+ "			<th>CHAPTER");
				} else {
					if (tab == 2) {
						writeChapters(prev_s, sw, chapters);
					}
					
					sw.println("\t</tbody>" + "</table>\n");
					sw.println("<p><marquee><i><b>Click on the name of a chaper to continue.</i></b></marquee>");
					sw.println("<p>");
					sw.println("<h2 style=\"text-align: center\">" + a + "</h2>");
					sw.println(
							  		  "<p>\n"
									+ "<table border=1 style=\"border-collapse:collapse\">\n"
									+ "	<tbody>\n" 
									+ "		<tr>\n"
									+ "			<th>S. NO.\n"
									+ "			<th>UNIT\n"
									+ "			<th>CHAPTER");
				}
				tab = 0;
				row=1;
			}
		}
	}

	private void writeChapters(String subject, PrintWriter sw, Vector<String>chapters) {
		sw.print(chapters.size() - 1 + ">");
		sw.println(chapters.get(0));
		String unit = chapters.get(0).replaceAll(" ", "");
		sw.print("<td>");
		sw.println("<a href=\"?s=" + subject + "&un=" + unit + "&ch="
				+ chapters.get(1).replaceAll(" ", "") + "\">"
				+ chapters.get(1)
				+ "</a>");

		for (int i = 2; i < chapters.size(); i++) {
			sw.println("<tr>");
			sw.println("<td>" + (row++));
			sw.println("<td>" + "<a href=\"?s=" + subject + "&un=" + unit + "&ch="
					+ chapters.get(i).replaceAll(" ", "") + "\">"
					+ chapters.get(i)
					+ "</a>");
		}
		chapters.removeAllElements();
	}
}