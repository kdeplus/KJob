package kde.jobcontainer.dep.domain;

import java.util.Date;

public class CountyPlat {
	private String id;
	private String ip;
	private String adcd;
	private Date queryTime;
	private Integer connInfo;
	private String remark;
	public CountyPlat() {
		super();
	}
	
	public CountyPlat(String id, String ip, String adcd, Date queryTime,
			Integer connInfo, String remark) {
		super();
		this.id = id;
		this.ip = ip;
		this.adcd = adcd;
		this.queryTime = queryTime;
		this.connInfo = connInfo;
		this.remark = remark;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getAdcd() {
		return adcd;
	}
	public void setAdcd(String adcd) {
		this.adcd = adcd;
	}
	public Date getQueryTime() {
		return queryTime;
	}
	public void setQueryTime(Date queryTime) {
		this.queryTime = queryTime;
	}
	public Integer getConnInfo() {
		return connInfo;
	}
	public void setConnInfo(Integer connInfo) {
		this.connInfo = connInfo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
