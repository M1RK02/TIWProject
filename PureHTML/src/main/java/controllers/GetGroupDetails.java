package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import beans.User;
import beans.Group;
import dao.GroupDAO;
import dao.UserDAO;

@WebServlet("/GetGroupDetails")
public class GetGroupDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public GetGroupDetails() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If the session is new or empty redirect to the login
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		// get and check params
		Integer groupId = null;
		try {
			groupId = Integer.parseInt(request.getParameter("groupId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		User user = (User) session.getAttribute("user");
		UserDAO userDAO = new UserDAO(connection);
		GroupDAO groupDAO = new GroupDAO(connection);
		Group group = null;
		User creator = null;
		List<User> entrants = null;
		try {
			group = groupDAO.findGroupById(groupId);
			if (group == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Group not found");
				return;
			}
			creator = userDAO.findUserById(group.getCreatorId());
			entrants = userDAO.findEntrantsByGroupId(group.getId());
			if (!user.equals(creator) && !entrants.contains(user)) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover group");
			return;
		}

		// Redirect to the group details
		String path = "/WEB-INF/details.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("group", group);
		ctx.setVariable("creator", creator);
		ctx.setVariable("entrants", entrants);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}