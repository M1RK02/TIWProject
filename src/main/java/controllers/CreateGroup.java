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

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Group;
import beans.User;
import dao.UserDAO;
import utils.ConnectionHandler;

@WebServlet("/CreateGroup")
public class CreateGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public CreateGroup() {
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
		
		User user = (User) session.getAttribute("user");
		Group tempGroup = new Group();
		tempGroup.setCreatorId(user.getId());
		tempGroup.setTitle(title);
		tempGroup.setDuration(duration);
		tempGroup.setMinEntrants(minEntrants);
		tempGroup.setMaxEntrants(maxEntrants);
		session.setAttribute("tempGroup", tempGroup);
		session.setAttribute("attempts", 0);
		
		UserDAO userDAO = new UserDAO(connection);
		List<User> users = null;
		try {
			users = userDAO.findAllUsersOrderedBySurname();
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover users");
			return;
		}
		
		// Send user to anagrafica
		String path = "/WEB-INF/anagrafica.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("users", users);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}