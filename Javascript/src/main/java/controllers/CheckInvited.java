package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utils.ConnectionHandler;
import beans.Group;
import dao.GroupDAO;

@WebServlet("/CheckInvited")
@MultipartConfig
public class CheckInvited extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CheckInvited() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get and parse all parameters from request
		HttpSession session = request.getSession();

		List<Integer> invitedUserIds = new ArrayList<>();

	   boolean isBadRequest = false;

	    try {
	        String[] checkedUserIds = request.getParameterValues("checkedUserIds");
	        if (checkedUserIds != null) {
	            for (String id : checkedUserIds) {
	                invitedUserIds.add(Integer.parseInt(id));
	            }
	            System.out.println("invitedUserIds: " + invitedUserIds);

	        } else {
	            System.out.println("checkedUserIds is null");
	            isBadRequest = true;
	        }
	    } catch (NumberFormatException e) {
	    	System.out.println("number format exception e");
	        System.out.println("NumberFormatException: " + e.getMessage()); 
	       isBadRequest = true;
	    }
	    


	   if (isBadRequest) {
	       response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	       response.getWriter().println("Incorrect parameters");
	        return;
	    }

		
		Group tempGroup = (Group) session.getAttribute("tempGroup");
		if (tempGroup == null) {
		    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    response.getWriter().println("tempGroup is null");
		    return;
		}
		String error = "";
		
		if (invitedUserIds.size() < tempGroup.getMinEntrants()) {
			error = "Select " + (tempGroup.getMinEntrants() - invitedUserIds.size()) + " more";
		}
		
		if (invitedUserIds.size() > tempGroup.getMaxEntrants()) {
			error = "Select " + (invitedUserIds.size() - tempGroup.getMaxEntrants()) + " less";
		}
		
	
		int attempts=0;
		
		if (session.getAttribute("attempts") == null) {
			session.setAttribute("attempts", attempts);
		}else {
			attempts = (int) session.getAttribute("attempts");
		}
		
		if (!error.isEmpty() && attempts >= 2) {
	
			session.removeAttribute("tempGroup");
			session.removeAttribute("attempts");
		
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Error: too many attempts.");
			return;
		}
		
		session.setAttribute("attempts", attempts+1);
		
		if (!error.isEmpty()) {	
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write(error);	
			return;
		}
		
		// Add group
		GroupDAO groupDAO = new GroupDAO(connection);
		int idGroup;
		try {
			idGroup = groupDAO.addGroup(tempGroup, invitedUserIds);
		} catch (SQLException e) {
			 System.out.println("non riesco ad aggiungere il gruppo");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(" "+ e.getMessage());
			return;
		}
		session.removeAttribute("attempts");
		session.removeAttribute("tempGroup");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(idGroup);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}