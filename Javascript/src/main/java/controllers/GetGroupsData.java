package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

@WebServlet("/GetGroupsData")
public class GetGroupsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetGroupsData() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		GroupDAO groupDao = new GroupDAO(connection);
		List<Group> createdGroups = new ArrayList<>();
		List<Group> invitedGroups = new ArrayList<>();
		try {
			createdGroups = groupDao.findActiveGroupsByCreatorId(user.getId());
			invitedGroups = groupDao.findActiveGroupsByEntrantId(user.getId());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover groups");
			return;
		}

		Gson gson = new GsonBuilder().create();
		String created = gson.toJson(createdGroups);
		String invited = gson.toJson(invitedGroups);
		String json = '[' + created + ',' + invited + ']';
		
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