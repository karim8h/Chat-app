package app.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.presentation.home.UserContainer;
import app.presentation.jsonObjects.JsonObject;

@RestController
public class HomeController {

	@Autowired
	private UserContainer onlineUsers;

	@RequestMapping(value = "/refresh_status", method = RequestMethod.POST)
	public void refreshStatus(HttpServletRequest request) {

		HttpSession sessionObj = request.getSession();
		onlineUsers.refreshUserStatus(sessionObj.getId());
		
	}

	@CrossOrigin
	@RequestMapping(value="/hold_me", method=RequestMethod.PUT)
	public JsonObject respond(HttpServletRequest request, HttpServletResponse response) {
		HttpSession sessionObj = request.getSession();
		return onlineUsers.getUserMessage(sessionObj.getId());
	}

	@RequestMapping(value = "/chat/req", method = RequestMethod.POST)
	public void requestChatting(HttpServletRequest request,
			@RequestParam(value="name", required=true) String target) {
		
		HttpSession sessionObj = request.getSession();
		
		onlineUsers.sendChatRequest((String) sessionObj.getAttribute("name"),
				target);
		System.out.println("finished request");
	}

	@RequestMapping(value = "/chat/res", method = RequestMethod.POST)
	public void responseChatting(HttpServletRequest request,
			@RequestParam(value="name", required=true) String target,
			@RequestParam(value="response", required=true) boolean response) {

		HttpSession sessionObj = request.getSession();
		String responder = (String) sessionObj.getAttribute("name");
		onlineUsers.sendChatResponse(responder,target, response);
		
		if (response) {
			onlineUsers.chatGroup(responder, target);
			String url = "/ChatApp/chat";
			onlineUsers.sendRedirect(responder, url);
			onlineUsers.sendRedirect(target, url);
		}
	}

}
