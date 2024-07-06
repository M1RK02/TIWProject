package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import dao.UserDAO;
import utils.ConnectionHandler;

@WebServlet("/RegisterUser")
public class RegisterUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	
	private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

	public RegisterUser() {
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
		// Obtain and clean parameters inserted by the user
		String username = null;
		String password = null;
		String repeatPassword = null;
		String email = null;
		String name = null;
		String surname = null;
		try {
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			password = StringEscapeUtils.escapeJava(request.getParameter("password"));
			repeatPassword = StringEscapeUtils.escapeJava(request.getParameter("repeatPassword"));
			email = StringEscapeUtils.escapeJava(request.getParameter("email"));
			name = StringEscapeUtils.escapeJava(request.getParameter("name"));
			surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
			if (Stream.of(username, password, repeatPassword, email, name, surname).anyMatch(str -> str == null || str.isEmpty())) {
				throw new Exception("Missing or empty credential value");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String path = "/index.html";

		// Check if username is unique
		UserDAO userDao = new UserDAO(connection);
		try {
			if(!userDao.isUsernameUnique(username)){
				ctx.setVariable("errorMsg", "Username not unique");
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to check username uniqueness");
			return;
		}
		
		// Check if repeatPassword matches password
		if (!repeatPassword.equals(password)) {
			ctx.setVariable("errorMsg", "Field Repeat Password does not match Password");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		// Check if email is valid
		if (!Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
			ctx.setVariable("errorMsg", "Email is not valid");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		// Insert new user in database and show confirm message
		try {
			userDao.addUser(username, password, email, name, surname);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to register user");
			return;
		}

		ctx.setVariable("genericMsg", "User registered successfully");
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