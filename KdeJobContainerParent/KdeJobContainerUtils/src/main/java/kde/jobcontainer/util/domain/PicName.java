package kde.jobcontainer.util.domain;

import java.io.Serializable;

public class PicName implements Serializable{
	private long id;
	private String name;
	public PicName() {
		super();
	}
	public PicName(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
