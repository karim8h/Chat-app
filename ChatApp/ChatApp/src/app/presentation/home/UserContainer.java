package app.presentation.home;

import java.util.List;

import app.presentation.jsonObjects.JsonObject;

public interface UserContainer {
	
	public void addSession(String session, User user);
	
	public String activeSession(String session);
	
	public User getUserBySession(String session);
	
	public User getUserByName(String userName);
	
	public void deleteUser(User user);
	
	public void userAdded(User user);
	
	public void deleteSession(String session);
	
	public void deleteList(List<User> users);
	
	public void refreshUserStatus(String session);
	
	public java.util.List<User> allAvailableUsers();
	
	public JsonObject getUserMessage(String userName);
	
	public void sendDirectedMessage(String sender, String target, 
			String directedMessage);
	
	public void sendRedirect(String userName, String url);
	
	public void sendChatRequest(String senderName, String targetName);
	
	public void sendFriendRequest(String senderName, String targetName);
	
	public void sendChatResponse(String responder, 
								String respondee, 
								boolean response);
	
	public java.util.List<String> chatPartners(String userName);
	
	public void chatGroup(String... x);
	
	public void unChatGroup(String userName);
	
	public boolean isChatting(String userName);
	
}
