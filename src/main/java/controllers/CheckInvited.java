package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import utils.ConnectionHandler;
import beans.Group;
import beans.User;
import dao.GroupDAO;
import dao.UserDAO;

@WebServlet("/CheckInvited")
public class CheckInvited extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public CheckInvited() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
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
		List<Integer> invitedUserIds = null;
		try {
			String[] checkedUserIds = request.getParameterValues("checkedUserIds");
			invitedUserIds = Arrays.stream(checkedUserIds).map(id -> Integer.parseInt(id)).toList();
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		Group tempGroup = (Group) session.getAttribute("tempGroup");
		String error = "";
		
		if (invitedUserIds.size() < tempGroup.getMinEntrants()) {
			error = "Select " + (tempGroup.getMinEntrants() - invitedUserIds.size()) + " more";
		}
		
		if (invitedUserIds.size() > tempGroup.getMaxEntrants()) {
			error = "Select " + (invitedUserIds.size() - tempGroup.getMaxEntrants()) + " less";
		}
		
		int attempts = (int) session.getAttribute("attempts");
		if (!error.isEmpty() && attempts >= 2) {
			String path = "/WEB-INF/error.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		session.setAttribute("attempts", attempts+1);
		
		if (!error.isEmpty()) {
			// Recall all users
			UserDAO userDAO = new UserDAO(connection);
			List<User> users = null;
			try {
				users = userDAO.findAllUsersOrderedBySurname();
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover users");
				return;
			}
			
			String path = "/WEB-INF/anagrafica.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("users", users);
			ctx.setVariable("checkedUsers", invitedUserIds);
			ctx.setVariable("errorMsg", error);
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		// Add group
		GroupDAO groupDAO = new GroupDAO(connection);
		try {
			groupDAO.addGroup(tempGroup, invitedUserIds);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to add group");
			return;
		}
		String path = getServletContext().getContextPath() + "/Home";
		response.sendRedirect(path);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}