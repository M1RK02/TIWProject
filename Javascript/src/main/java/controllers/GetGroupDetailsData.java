package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import utils.ConnectionHandler;
import beans.User;
import beans.Group;
import dao.GroupDAO;
import dao.UserDAO;

@WebServlet("/GetGroupDetailsData")
public class GetGroupDetailsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetGroupDetailsData() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer groupId = null;
		try {
			groupId = Integer.parseInt(request.getParameter("groupid"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Incorrect param values");
			return;
		}

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		UserDAO userDAO = new UserDAO(connection);
		GroupDAO groupDAO = new GroupDAO(connection);
		Group group = null;
		User creator = null;
		List<User> entrants = null;
		try {
			group = groupDAO.findGroupById(groupId);
			if (group == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Group not found");
				return;
			}
			creator = userDAO.findUserById(group.getCreatorId());
			entrants = userDAO.findEntrantsByGroupId(group.getId());
			if (!user.equals(creator) && !entrants.contains(user)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("User not allowed");
				return;
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover group");
			return;
		}

		Gson gson = new GsonBuilder().setDateFormat("dd MMM yyyy").create();
		
		String groupJson = gson.toJson(group);
		String creatorJson = gson.toJson(creator);
		String entrantsJson = gson.toJson(entrants);
		String json = '[' + groupJson + ',' + creatorJson + ',' + entrantsJson + ']';
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
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