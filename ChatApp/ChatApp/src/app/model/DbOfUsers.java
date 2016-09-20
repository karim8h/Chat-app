package app.model;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DbOfUsers {
	
	@Autowired
	private UserProfileRepo userRepo;
	
	public boolean matchingData(String userName, String password) {
		
		UserProfile userProfile = getUserByName(userName);
		if (userProfile == null) {
			return false;
		}
		return userProfile.getPassword().equals(password);
	}
	
	public void newProfile(UserProfile userProfile) {
		userRepo.save(userProfile);
	}
	
	public UserProfile getUserByName(String userName) {
		Iterator<UserProfile> iter = userRepo.findAll().iterator();
		while (iter.hasNext()) {
			UserProfile userProfile = iter.next();
			if (userProfile.getUserName().equals(userName)) {
				return userProfile;
			}
		}
		return null;
	}
	
}
