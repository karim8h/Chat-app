package app.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import app.controllers.AppInitializer;
import app.controllers.RootConfig;
import app.controllers.WebConfig;
import app.presentation.home.UserContainer;

/*
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RootConfig.class)
*/
public class Test0 {
	
	/*
	@Autowired
	private TempDb inst0;
	@Autowired
	private ChattingUsers inst1;
	@Autowired
	private ChattingUsersImpl inst2;
	*/
	
	
	@Test
	public void test1() {
		AnnotationConfigWebApplicationContext a = new AnnotationConfigWebApplicationContext();
		a.register(new Class<?>[]{AppInitializer.class, RootConfig.class, WebConfig.class});
		a.refresh();
		//a.getBeanFactory().getBean(UserContainer.class);
		assertNotNull("impl is null", a.getBean(UserContainer.class));
		
		
		
		/*
		assertNotNull("impl is null", inst2);
		assertNotNull("cu is null", inst1);
		assertNotNull("tempdb is null", inst0);
		*/
	}
	
}
