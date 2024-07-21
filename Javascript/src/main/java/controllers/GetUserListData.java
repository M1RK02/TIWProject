package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang.StringEscapeUtils;

import beans.Group;
import beans.User;
import dao.UserDAO;
import utils.ConnectionHandler;

@WebServlet("/GetUserListData")
@MultipartConfig
public class GetUserListData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetUserListData() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
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
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect parameters");
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

		
		UserDAO userDAO = new UserDAO(connection);
		List<User> users = null;
		try {
			users = userDAO.findAllUsersOrderedBySurname();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover users");
			return;
		}
		users.remove(user);
		
		Gson gson = new GsonBuilder().create();
		
		String json = gson.toJson(users);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}