package app.presentation.jsonObjects;

public class JsonObject {

	private String targetName;
	private String actionURL;
	private String message;
	
	public JsonObject() {}
	
	public JsonObject(String targetName, String message) {
		this.message = message;
		this.targetName = targetName;
	}
	
	public String getTargetName() { return targetName; }
	public void settargetName(String name) { targetName = name; }
	
	public String getActionURL() { return actionURL; }
	public void setActionURL(String actionURL) { this.actionURL = actionURL; }
	
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	
}
