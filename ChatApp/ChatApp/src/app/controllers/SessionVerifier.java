package app.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.presentation.home.UserContainer;

@Aspect
@Component
public class SessionVerifier {
	
	private final int MAX_INACTIVE_TIME = 60 * 60;
	
	@Autowired
	private UserContainer onlineUsers;
	
	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void requestMappedMethods() {}
	
	@Pointcut("args(javax.servlet.http.HttpServletRequest, ..)")
	public void firstArgServletRequest() {}
	
	@Pointcut("requestMappedMethods() && firstArgServletRequest() && args(request, ..) && !theVerifyMethod()")
	public void unverifiedMethods(HttpServletRequest request) {}
	
	@Pointcut("@target(org.springframework.web.bind.annotation.RestController)")
	public void isRestController() {}
	
	@Pointcut("execution(public String app.controllers.PageController.verify(..))")
	public void theVerifyMethod() {}
	
	@Around("unverifiedMethods(request) && !isRestController()")
	public Object applySecurityNonRest(ProceedingJoinPoint pjp, HttpServletRequest request) throws Throwable {
		
		System.out.println("***************** verification *********************");
		
		HttpSession sessionObj = request.getSession(false);
		if (!getsecurityState(sessionObj)) {
			System.out.println("***************** insecure *********************");
			return "redirect:/login";
		}
		
		System.out.println("***************** secure *********************");
		sessionObj.setMaxInactiveInterval(MAX_INACTIVE_TIME);
		return pjp.proceed();
	}
	
	@Around("unverifiedMethods(request) && isRestController()")
	public Object applySecurityRest(ProceedingJoinPoint pjp, HttpServletRequest request) throws Throwable {
		
		System.out.println("***************** verification *********************");
		
		HttpSession sessionObj = request.getSession(false);
		if (!getsecurityState(sessionObj)) {
			System.out.println("***************** insecure *********************");
			return null;
		}
		
		System.out.println("***************** secure *********************");
		sessionObj.setMaxInactiveInterval(MAX_INACTIVE_TIME);
		return pjp.proceed();
	}
	
	private boolean getsecurityState(HttpSession sessionObj) {
		if (sessionObj == null) {
			return false;
		}
		String session = sessionObj.getId();
		String userName = onlineUsers.activeSession(session);
		if (userName == null) {
			return false;
		}
		
		if (sessionObj.getAttribute("name") == null) {
			return false;
		}
		
		if (!userName.equals((String) sessionObj.getAttribute("name"))) {
			return false;
		}
		return true;
	}
	
	
}
