package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import utils.ConnectionHandler;

@WebServlet("/CreateGroup")
public class CreateGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	public CreateGroup() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		// Get and parse all parameters from request
		boolean isBadRequest = false;
		String title = null;
		Integer duration = null;
		Integer minEntrants = null;
		Integer maxEntrants = null;
		try {
			title = StringEscapeUtils.escapeJava(request.getParameter("title"));
			duration = Integer.parseInt(request.getParameter("duration"));
			minEntrants = Integer.parseInt(request.getParameter("minEntrants"));
			maxEntrants = Integer.parseInt(request.getParameter("maxEntrants"));
			isBadRequest = title.isEmpty() || duration < 1 || minEntrants < 0 || maxEntrants < minEntrants;
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		// TODO Send user to anagrafica
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
