package app.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.presentation.home.UserContainer;
import app.presentation.jsonObjects.JsonObject;

@RestController
public class ChatController {
	
	@Autowired
	private UserContainer onlineUsers;
	
	@RequestMapping(value = "/chat", method = RequestMethod.POST)
	public void chat(HttpServletRequest request, @RequestBody JsonObject message) {
		HttpSession sessionObj = request.getSession();
		onlineUsers.sendDirectedMessage((String) sessionObj.getAttribute("name"),
										message.getTargetName(), 
										message.getMessage());
	}
	
	@CrossOrigin
	@RequestMapping(value="/chat", method = RequestMethod.PUT)
	public JsonObject respond(HttpServletRequest request, HttpServletResponse response) {
		HttpSession sessionObj = request.getSession();
		response.setContentType("application/json");
		return onlineUsers.getUserMessage(sessionObj.getId());
	}
	
}
