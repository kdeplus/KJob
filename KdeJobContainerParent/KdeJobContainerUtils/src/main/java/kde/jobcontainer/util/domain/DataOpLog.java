package kde.jobcontainer.util.domain;

import java.io.Serializable;
import java.util.Date;


public class DataOpLog implements Serializable{
	private Long id;
	private String databasename;
	private String tname;
	private Date ltime;
	private Integer optype;
	private String tkey;
	private String userid;
	private String isread;
	private Integer keyInc;
	
	public DataOpLog(){
		
	}

	public DataOpLog(Long id, String databasename, String tname, Date ltime,
			Integer optype, String tkey, String userid, String isread,
			Integer keyInc) {
		super();
		this.id = id;
		this.databasename = databasename;
		this.tname = tname;
		this.ltime = ltime;
		this.optype = optype;
		this.tkey = tkey;
		this.userid = userid;
		this.isread = isread;
		this.keyInc = keyInc;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDatabasename() {
		return databasename;
	}

	public void setDatabasename(String databasename) {
		this.databasename = databasename;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public Date getLtime() {
		return ltime;
	}

	public void setLtime(Date ltime) {
		this.ltime = ltime;
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

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getIsread() {
		return isread;
	}

	public void setIsread(String isread) {
		this.isread = isread;
	}

	public Integer getKeyInc() {
		return keyInc;
	}

	public void setKeyInc(Integer keyInc) {
		this.keyInc = keyInc;
	}
	
	
}
