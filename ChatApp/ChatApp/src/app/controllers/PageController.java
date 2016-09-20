package app.controllers;

import java.time.chrono.IsoChronology;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import app.model.DbOfUsers;
import app.model.UserProfile;
import app.presentation.home.ChatStatus;
import app.presentation.home.OnlineStatus;
import app.presentation.home.User;
import app.presentation.home.UserContainer;

@Controller
public class PageController {
	
	private final int MAX_INACTIVE_TIME = 60 * 30;
	
	@Autowired
	private UserContainer onlineUsers;
	@Autowired
	private DbOfUsers registeredUsers;
	
	
	@RequestMapping(value="/chat", method=RequestMethod.GET)
	public String beginChat(HttpServletRequest request, Model model) {

		HttpSession sessionObj = request.getSession();
		String userName = (String) sessionObj.getAttribute("name");
		if (!onlineUsers.isChatting(userName)) {
			return "redirect:/home/" + userName;
		}
		
		model.addAttribute("userName", userName);
		model.addAttribute("partners", onlineUsers.chatPartners(userName));
		
		return "chat";
	}
	
	@RequestMapping(value = "/home/{userName}", method = RequestMethod.GET)
	public String welcome(HttpServletRequest request,
			@PathVariable(value="userName") String userName,
			Model model) {

		User user = onlineUsers.getUserByName(userName);
		if (user == null) {
			return "redirect:/login";
		}
		if (onlineUsers.isChatting(userName)) {
			return dropChat(request);
		}
		
		user.setChatStatus(ChatStatus.IDLE);
		user.setOnlineStatus(OnlineStatus.ONLINE);
		onlineUsers.userAdded(user);
		List<User> allUsers = onlineUsers.allAvailableUsers();
		allUsers.remove(user);
		model.addAttribute("allUsers", allUsers);
		model.addAttribute("userName", user.getName());
		
		return "home-test";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(@RequestParam(name = "incorrect", required = false, defaultValue="false") boolean incorrect,
			Model model) {
		/*
		HttpSession sessionObj = request.getSession(false);
		if (sessionObj != null) {
			String userName = (String) sessionObj.getAttribute("name");
			if (userName != null && onlineUsers.isChatting(userName)) {
				return "redirect:/chat";				
			}
		}
		*/
		String message = null;
		if (incorrect) {
			message = "incorrect username or password";
		}
		else {
			message = "";
		}
		model.addAttribute("message", message);
		return "login";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String verify(HttpServletRequest request,
			@RequestParam(name = "name", required = true) String userName,
			@RequestParam(name = "password", required = true) String password) {
		
		if (!registeredUsers.matchingData(userName, password)) {
			return "redirect:/login?incorrect=true";
		}
		HttpSession sessionObj = request.getSession();
		sessionObj.setMaxInactiveInterval(MAX_INACTIVE_TIME);
		sessionObj.setAttribute("name", userName);
		
		UserProfile userProfile = registeredUsers.getUserByName(userName);
		User user = new User(userProfile.getId(), userProfile.getUserName(), OnlineStatus.ONLINE);
		onlineUsers.addSession(sessionObj.getId(), user);
		
		return "redirect:/home/" + userName;
		
	}
	
	@RequestMapping(value="/chat/drop", method = RequestMethod.GET)
	public String dropChat(HttpServletRequest request) {
		HttpSession sessionObj = request.getSession();
		String userName = (String) sessionObj.getAttribute("name");
		User user = onlineUsers.getUserByName(userName);
		if (user == null || !onlineUsers.isChatting(userName)) {
			return "redirect:/home/" + userName;
		}
		user.setChatStatus(ChatStatus.IDLE);
		onlineUsers.unChatGroup(userName);
		
		return "redirect:/home/" + userName;
	}
	
	
	@RequestMapping(value="/x", method = RequestMethod.GET)
	public String deactivate(HttpServletResponse response, HttpServletRequest request) {
		
		HttpSession sessionObj = request.getSession(false);
		if (sessionObj != null) {
			sessionObj.setMaxInactiveInterval(1);
			sessionObj.invalidate();
			System.out.println("invalidated");
		}
		
		Cookie c = new Cookie("JSESSIONID", "sds");
		c.setMaxAge(0);
		response.addCookie(c);
		
		return "redirect:/login";
	}
	
	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public String signUp(@RequestParam(name = "incorrect", required = false, defaultValue="false") boolean nameExists,
			Model model) {
		String message = null;
		if (nameExists) {
			message = "user name already exists";
		}
		else {
			message = "";
		}
		model.addAttribute("message", message);
		return "signup";
	}
	
	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String saveNewUser(@ModelAttribute UserProfile userProfile, HttpServletRequest request) {
		
		if (registeredUsers.getUserByName(userProfile.getUserName()) != null) {
			return "redirect:/signup?nameExists=true";
		}
		registeredUsers.newProfile(userProfile);
		
		return verify(request, userProfile.getUserName(), userProfile.getPassword());
	}
	
	
}
