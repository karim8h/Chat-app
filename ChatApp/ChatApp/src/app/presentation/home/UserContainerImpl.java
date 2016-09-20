package app.presentation.home;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import app.presentation.jsonObjects.JsonObject;

public class UserContainerImpl implements UserContainer {

	private Map<String, User> sessionTable;
	private Map<String, User> usersAndNames;
	private final int MAX_OFFLINE_INTERVAL = 60 * 35;
	
	
	public UserContainerImpl() {
		
		sessionTable = new HashMap<>();
		usersAndNames = new HashMap<>();
		refreshAllUsersStatusPeriodically();

	}
	
	@Override
	public void addSession(String session, User user) {
		synchronized (sessionTable) {
			user.setOnlineStatus(OnlineStatus.ONLINE);
			sessionTable.put(session, user);
			usersAndNames.put(user.getName(), user);
		}
	}
	
	@Override
	public String activeSession(String session) {
		if (!sessionTable.containsKey(session)) {
			return null;
		}
		return sessionTable.get(session).getName();
	}
		
	@Override
	public User getUserBySession(String session) {
		return sessionTable.get(session);
	}
	
	
	@Override
	public User getUserByName(String userName) {
		return usersAndNames.get(userName);
	}
	
	@Override
	public void deleteUser(User user) {
		synchronized (sessionTable) {
			usersAndNames.remove(user.getName());
			Iterator<String> iter = sessionTable.keySet().iterator();
			while (iter.hasNext()) {
				if (iter.next().equals(user)) {
					iter.remove();
					userRemoved(user);
					break;
				}
			}
		}
	}
	
	@Override
	public void deleteSession(String session) {
		if (!sessionTable.containsKey(session)) {
			return;
		}
		synchronized (sessionTable) {
			User user = sessionTable.get(session);
			usersAndNames.remove(user.getName());
			sessionTable.remove(session);
			userRemoved(user);
		}
	}
	
	
	@Override
	public void deleteList(List<User> users) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public void refreshUserStatus(String session) {
		
		User user = getUserBySession(session);
		if (user == null) {
			System.out.println("cannot refresh null user");
			return;
		}
		user.setOnlineStatus(OnlineStatus.ONLINE);
		user.setTimeSinceBeingOnline(0);
		//System.out.println(user.getName() + "->online");
	}

	@Override
	public List<User> allAvailableUsers() {
		List<User> onlineUsers = new LinkedList<User>(sessionTable.values());
		ListIterator<User> iter = onlineUsers.listIterator();
		while (iter.hasNext()) {
			if (iter.next().getChatStatus() == ChatStatus.CHATTING) {
				iter.remove();
			}
		}
		return onlineUsers;
	}

	@Override
	public JsonObject getUserMessage(String session) {
		User user = getUserBySession(session);
		if (user == null) {
			System.out.println("get message : null user : " + session);
			return null;
		}
		return user.getMessage();
	}

	@Override
	public void sendRedirect(String userName, String url) {
		User user = getUserByName(userName);
		if (user == null) {
			return;
		}
		user.sendRedirect(url);
	}

	@Override
	public void sendChatRequest(String senderName, String targetName) {
		User user = getUserByName(targetName);
		if (user == null) {
			System.out.println("null user (send chat request)");
			return;
		}
		user.sendChatRequest(senderName);
	}

	@Override
	public void sendFriendRequest(String senderName, String targetName) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public void sendChatResponse(String responder, String respondee, boolean response) {
		User user = getUserByName(respondee);
		if (user == null) {
			return;
		}
		user.respondToChatRequest(responder, response);
	}

	@Override
	public List<String> chatPartners(String userName) {
		return getUserByName(userName).getChatGroup();
	}
	
	@Override
	public void chatGroup(String... users) {
		for (String userName: users) {
			User user = getUserByName(userName);
			user.setChatStatus(ChatStatus.CHATTING);
			List<String> temp = new LinkedList<String>(Arrays.asList(users));
			user.setChatGroup(temp);
		}
		for (String userName: users) {
			User user = getUserByName(userName);
			userRemoved(user);
		}
	}
	
	@Override
	public void unChatGroup(String userName) {
		System.out.println("unchat : " + userName);
		List<String> users = getUserByName(userName).getChatGroup();
		if (users == null) {
			System.out.println("null chat group");
			return;
		}
		String url = "http://localhost:8080/ChatApp/home/";
		for (String temp: users) {
			System.out.print("user : " + temp);
			User user = getUserByName(temp);
			user.setChatGroup(null);
			if (user.getOnlineStatus() != OnlineStatus.OFFLINE 
					&& user.getChatStatus() == ChatStatus.CHATTING) {
				user.setChatStatus(ChatStatus.IDLE);
				System.out.println(" sent");
				user.sendRedirect(url + temp);
				//sendDirectedMessage(userName, userName, "left chat");				
			}
			if (user.getOnlineStatus() != OnlineStatus.OFFLINE) {
				userAdded(user);				
			}
		}
		System.out.println("unchat finished");
	}
	
	private void userRemoved(User user) {
		System.out.println("user removed : " + user.getName());
		JsonObject event = new JsonObject(user.getName(), "remove_user");
		event.setActionURL("none");
		notifyUnChattingUsers(event);
	}
	
	@Override
	public void userAdded(User user) {
		System.out.println("user added : " + user.getName());
		JsonObject event = new JsonObject(user.getName(), "add_user");
		event.setActionURL("none");
		notifyUnChattingUsers(event);
	}
	
	private void notifyAllUsers(JsonObject event) {
		for (User user : usersAndNames.values()) {
			if (user.getOnlineStatus() != OnlineStatus.OFFLINE) {
				user.userListChanged(event);				
			}
		}
	}
	
	private void notifyUnChattingUsers(JsonObject event) {
		for (User user : usersAndNames.values()) {
			if (user.getChatStatus() != ChatStatus.CHATTING
					&& user.getOnlineStatus() != OnlineStatus.OFFLINE) {
				user.userListChanged(event);
				System.out.println(user.getName() + " : notified");
			}
		}
	}
	
	private void refreshAllUsersStatusPeriodically() {
		new Thread(new PeriodicRefresh()).start();
	}
	
	@Override
	public void sendDirectedMessage(String sender, String target, 
			String directedMessage) {
		
		User user = getUserByName(target);
		if (user == null) {
			return;
		}
		
		JsonObject message = new JsonObject(sender, directedMessage);
		message.setActionURL("none");
		user.sendChatMessage(message);
	}
	
	@Override
	public boolean isChatting(String userName) {
		User user = getUserByName(userName);
		if (user == null) {
			return false;
		}
		return user.getChatStatus() == ChatStatus.CHATTING;
	}
	
	private class PeriodicRefresh implements Runnable {
		
		@Override
		public void run() {
			while (true) {
				refreshAll();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}
			}
		}
		
		private void refreshAll() {
			
			synchronized (sessionTable) {
				Iterator<Map.Entry<String, User>> iter = sessionTable.entrySet().iterator();
				
				while (iter.hasNext()) {
					User user = iter.next().getValue();
					
					if (user.getOnlineStatus() == OnlineStatus.ONLINE) {
						user.setOnlineStatus(OnlineStatus.TRANSIENT);
				//		System.out.println(user.getName() + " online->transient");
					} else if (user.getOnlineStatus() == OnlineStatus.OFFLINE) {
						int temp = user.getTimeSinceBeingOnline();
						if (temp >= MAX_OFFLINE_INTERVAL) {
							iter.remove();
							usersAndNames.remove(user.getName());
				//			System.out.println(user.getName() + " removed");
						}
						else {
							user.setTimeSinceBeingOnline(temp + 2);							
						}
					} else {
					//	System.out.println(user.getName() + " transient->offline");
						user.setOnlineStatus(OnlineStatus.OFFLINE);
						user.setTimeSinceBeingOnline(2);
						
						if (user.getChatStatus() == ChatStatus.CHATTING) {
							unChatGroup(user.getName());
						}
						userRemoved(user);
					}
				}
			}
			
		}
		
	}

}
