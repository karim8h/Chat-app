package app.tests;

import org.hamcrest.Matchers.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.media.jfxmediaimpl.MarkerStateListener;

import java.util.LinkedList;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;

import app.controllers.ChatController;
import app.controllers.HomeController;
import app.controllers.PageController;
import app.controllers.RootConfig;
import app.presentation.home.OnlineStatus;
import app.presentation.home.User;
import app.presentation.home.UserContainer;
import app.presentation.home.UserContainerImpl;
import app.presentation.jsonObjects.JsonObject;
import sun.rmi.runtime.NewThreadAction;

/*
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RootConfig.class)
public class MainTests {
	
	@Autowired
	private ChattingUsers cU;
	@Autowired
	private UserContainerImpl uC;
	@Autowired
	private ChatController chatController;
	@Autowired
	private HomeController homeController;
	
	@Test
	public void test1() throws Exception {
		org.junit.Assert.assertNotNull("controller is null", chatController);
		init1();

		MockMvc chatMoc = MockMvcBuilders.standaloneSetup(chatController).build();
		MockMvc homeMoc = MockMvcBuilders.standaloneSetup(homeController).build();
		
		
		JsonObject fToS = new JsonObject(1, 2, "user1");fToS.setMessage("from 1 to 2");
		JsonObject sToF = new JsonObject(2, 1, "sToF");sToF.setMessage("from 2 to 1");
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		//String s = ow.writeValueAsString(temp);
		

		sendChatMessage(chatMoc, fToS, ow);
		sendChatMessage(chatMoc, sToF, ow);
		Thread.currentThread().sleep(3000);
		
		makeRequest(homeMoc, fToS, "1");
		makeRequest(homeMoc, fToS, "2");
		
		System.out.println("place 2");
			
		System.out.println("place 3");
		Thread.currentThread().sleep(5000);
		
		makeRequest(homeMoc, sToF, "1");
		makeRequest(homeMoc, sToF, "2");

		Thread.currentThread().join(20000);
	}
	
	private void sendChatMessage(MockMvc chatMoc, JsonObject fToS, ObjectWriter ow) throws JsonProcessingException, Exception {
		//System.out.println("sendChatMessage");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					//System.out.println("run");
					chatMoc.perform(MockMvcRequestBuilders.post("/chat").param("session", "0")
							.contentType(MediaType.APPLICATION_JSON)
							.content(ow.writeValueAsString(fToS)));
					System.out.println("message: " + fToS.getMessage() + " sent to user : " + fToS.geTargetId());
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}, "one").start();
		
	}
	
	private void makeRequest(MockMvc mock, JsonObject o, String id) throws Exception {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					//System.out.println("make request");
					mock.perform(MockMvcRequestBuilders.get("/hold_me").param("id", id))
					.andExpect(MockMvcResultMatchers.status().isOk())
		        	.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
					.andExpect(MockMvcResultMatchers.jsonPath("senderId").value(o.getSenderId()))
			//		.andExpect(MockMvcResultMatchers.jsonPath("targetId").value(o.geTargetId()))
					.andExpect(MockMvcResultMatchers.jsonPath("message").value(o.getMessage()));

					System.out.println("message: " + o.getMessage() + " verifired");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "one"+id).start();
		
	}
	
	private void init1() {
		org.junit.Assert.assertNotNull("it is null indeed", cU);
		org.junit.Assert.assertNotNull("it is null indeed", uC);
		LinkedList<User> userList = new LinkedList<>();
		User user1 = new User(1, "user1", OnlineStatus.ONLINE);
		User user2 = new User(2, "user2", OnlineStatus.ONLINE);
		userList.add(user1);
		userList.add(user2);
		cU.addGroup(userList);
		uC.addUser(user1);;
		uC.addUser(user2);
	}
	
}
*/

//mocMvc.perform(MockMvcRequestBuilders.get("/chat")).andExpect(MockMvcResultMatchers.view().name("chat"));
/*
			mocMvc.perform(MockMvcRequestBuilders.get("/test").content(s).accept(MediaType.APPLICATION_JSON))
	        	.andExpect(MockMvcResultMatchers.status().isOk())
	        	.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
	        	.andExpect(MockMvcResultMatchers.jsonPath("senderId").value(Matchers.equalTo(1)));
 */