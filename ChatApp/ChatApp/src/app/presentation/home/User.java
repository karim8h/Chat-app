package app.presentation.home;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import app.presentation.jsonObjects.JsonObject;

public class User {
	
	private int id;
	private String name;
	private OnlineStatus onlineStatus;
	private ChatStatus chatStatus; 
	private Queue<JsonObject> response;
	private int threadsAvailable;
	private List<String> chatGroup;
	private int timeSinceBeingOnline;
	
	public User(int id, String name, OnlineStatus status) {
		this.id = id;
		this.name = name;
		this.onlineStatus = status;
		chatStatus = ChatStatus.IDLE;
		response = new LinkedList<>();
		threadsAvailable = 0;
		chatGroup = null;
		timeSinceBeingOnline = 0;
	}
	
	public int getTimeSinceBeingOnline() {
		return timeSinceBeingOnline;
	}
	public void setTimeSinceBeingOnline(int timeSinceBeingOnline) {
		this.timeSinceBeingOnline = timeSinceBeingOnline;
	}
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public OnlineStatus getOnlineStatus() { return onlineStatus; }
	public void setOnlineStatus(OnlineStatus status) { 
		this.onlineStatus = status;
		if (status == OnlineStatus.OFFLINE && threadsAvailable > 0) {
			synchronized (this) {
				notifyAll();
				threadsAvailable = 0;
			}
		}
	}
	
	public ChatStatus getChatStatus() { return chatStatus; }
	public void setChatStatus(ChatStatus status) { this.chatStatus = status; }
	
	public List<String> getChatGroup() { return chatGroup; }
	public void setChatGroup(List<String> chatGroup) { this.chatGroup = chatGroup; }
	
	
	public void sendChatRequest(String sender) {
				
		JsonObject tempResponse = new JsonObject(sender, "chat:request");
		tempResponse.setActionURL("http://localhost:8080/ChatApp/chat/res");
		
		response.offer(tempResponse);
		freeTheMessage();
	}
	
	public void respondToChatRequest(String responder, boolean decision) {
		
		String reply;
		if (decision) {
			reply = "chat:approval";
		}
		else {
			reply = "chat:refusal";
		}
		
		JsonObject tempResponse = new JsonObject(responder, reply);
		
		tempResponse.setActionURL("none");
		
		response.offer(tempResponse);
		freeTheMessage();
	}
	
	public void sendRedirect(String url) {
		JsonObject redirect = new JsonObject("none", "redirect");
		redirect.setActionURL(url);
		
		response.offer(redirect);
		freeTheMessage();
	}
	
	public void sendChatMessage(JsonObject message) {
		response.offer(message);
		freeTheMessage();
	}

	
	public void sendFriendRequest(User sender) {
		throw new RuntimeException("unimplemented");
	}
	
	public void userListChanged(JsonObject event) {
		response.offer(event);
		freeTheMessage();
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((User) obj).id == this.id;
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
	
	private void freeTheMessage() {
		new delegate();
	}
	
	
	public synchronized JsonObject getMessage() {
		try {
			threadsAvailable++;
			System.out.println("+threadsAvailable=" + threadsAvailable + ", id=" + id);
			if (threadsAvailable > 1) {
				threadsAvailable = 1;
				notifyAll();
			}
			wait();
			System.out.println("-threadsAvailable=" + threadsAvailable + ", id=" + id);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return response.poll();
	}

	
	private class delegate implements Runnable{
		public delegate() {
			new Thread(this).start();
		}

		@Override
		public void run() {
			int counter = 20;
			while (true) {
				synchronized (User.this) {
					if (threadsAvailable > 0) {
						User.this.notify();						
						threadsAvailable--;
						return;
					}
					
				}
				if (--counter <= 0) {
					System.out.println("counter exceed");
					return;
				}
				try {
					System.out.println("sleeping in " + getName() + ", for : " + response.peek().getMessage());
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		
	}
	
	
}

