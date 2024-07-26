package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utils.ConnectionHandler;

@WebServlet("/IncreaseAttempts")
@MultipartConfig
public class IncreaseAttempts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public IncreaseAttempts() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		int tent = 0;
		
		if (session.getAttribute("attempts") == null)
			session.setAttribute("attempts", tent);
		else {
			tent = (int) session.getAttribute("attempts");
			tent = tent + 1;
		}
		
		if (tent >= 2) {
			session.removeAttribute("attempts");
			session.removeAttribute("tempGroup");
			
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Error: too many attempts.");
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}