package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import dao.UserDAO;
import utils.ConnectionHandler;

@WebServlet("/RegisterUser")
@MultipartConfig
public class RegisterUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

	public RegisterUser() {
		super();
	}

	public void init() throws ServletException {
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
		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		repeatPassword = StringEscapeUtils.escapeJava(request.getParameter("repeatPassword"));
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
		if (Stream.of(username, password, repeatPassword, email, name, surname).anyMatch(str -> str == null || str.isEmpty())) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing or empty credential value");
			return;
		}

		// Check if username is unique
		UserDAO userDao = new UserDAO(connection);
		try {
			if(!userDao.isUsernameUnique(username)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Username not unique");
				return;
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not Possible to check credentials");
			return;
		}
		
		// Check if repeatPassword matches password
		if (!repeatPassword.equals(password)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Field Repeat Password does not match Password");
			return;
		}
		
		// Check if email is valid
		if (!Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Email is not valid");
			return;
		}
		
		// Insert new user in database and show confirm message
		try {
			userDao.addUser(username, password, email, name, surname);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not Possible to register user");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("User registered successfully");
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}