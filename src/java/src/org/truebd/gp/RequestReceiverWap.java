package src.org.truebd.gp;

import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import src.org.truebd.gp.GPChargingConnectorImplNGW;

/**
 * Servlet implementation class RequestReceiverWap
 */
@WebServlet(name = "RequestReciever", urlPatterns = { "/charge.do" })
public class RequestReceiverWap extends HttpServlet {
    @EJB
    private GPChargingConnectorImplNGW gPChargingConnectorImplNGW;
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(RequestReceiverWap.class);

	// new GPChargingConnectorNGW();
	// GPChargingConnectorImplNGW gpChargNGW=new GPChargingConnectorImplNGW();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RequestReceiverWap() {
		super();
                Logmanager.intLogger(); 
		logger.debug("RequestRecieverWap starting");
                               
		// TODO Auto-generated constructor stub
	}

	//private static final String CONTENT_TYPE = "text/xml";

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
		//response.setContentType(CONTENT_TYPE);

		PrintWriter out = null;
		int HTTPResponse = 0;
		try {
			out = response.getWriter();
			String ServiceID = request.getParameter("ServiceID");
			String ProductID = request.getParameter("ProductID");
			String PurchaseCategoryCode = request.getParameter("PurchaseCategoryCode");
			String Amount = request.getParameter("Amount");
			String MSISDN = request.getParameter("MSISDN");
			logger.debug("MSISDN:" + MSISDN + "|ServiceID:" + ServiceID + "|ProductID:" + ProductID + "|PurchaseCategoryCode" + PurchaseCategoryCode
					+ "|Amount:" + Amount);

			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {

			}

			HTTPResponse = gPChargingConnectorImplNGW.doChargeNGW(MSISDN, Amount, ProductID, PurchaseCategoryCode, ServiceID);

			logger.debug(HTTPResponse);

			//out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
			//out.println("<HTTPRCODE>");
			out.println(HTTPResponse);
			//out.println("</HTTPRCODE>");
		} catch (Exception e) {
			logger.error("Excetion", e);
		} finally {
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request, response);
	}

	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
