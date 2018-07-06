package kde.jobcontainer.util.domain;

import java.io.Serializable;

public class DataOpLogD implements Serializable{
	private String tname;
	private Integer optype;
	private String tkey;
	private Integer keyInc;
	
	public DataOpLogD(){
		
	}
	
	public DataOpLogD(String tname, Integer optype, String tkey, Integer keyInc){
		this.tname = tname;
		this.keyInc = keyInc;
		this.optype = optype;
		this.tkey = tkey;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public Integer getOptype() {
		return optype;
	}

	public void setOptype(Integer optype) {
		this.optype = optype;
	}

	public String getTkey() {
		return tkey;
	}

	public void setTkey(String tkey) {
		this.tkey = tkey;
	}

	public Integer getKeyInc() {
		return keyInc;
	}

	public void setKeyInc(Integer keyInc) {
		this.keyInc = keyInc;
	}
	
	
}
