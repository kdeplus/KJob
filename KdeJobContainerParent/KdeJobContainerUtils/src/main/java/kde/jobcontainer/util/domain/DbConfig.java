package kde.jobcontainer.util.domain;

import com.alibaba.fastjson.JSONObject;

public class DbConfig {
	
	private String url;
	private String driver;
	private String username;
	private String userpwd;
	
	public DbConfig(){};
	public DbConfig(JSONObject j){
		if(j.containsKey( "url" )){
			this.url = j.getString( "url" );
		}
		if(j.containsKey("driver"))
			this.driver = j.getString("driver");
		if(j.containsKey( "username" ))
			this.username = j.getString("username");
		if(j.containsKey( "userpwd" ))
			this.userpwd = j.getString("userpwd");
	}
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserpwd() {
		return userpwd;
	}
	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}
}
