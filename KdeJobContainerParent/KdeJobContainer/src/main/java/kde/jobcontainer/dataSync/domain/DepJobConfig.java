package kde.jobcontainer.dataSync.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xxxxxxxx
 * @date 2014-9-6, 下午5:22:47
 * 数据同步配置映射类
 */
public class DepJobConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String tbLid;
	private String nodeId;
	private String tag;
	private String type;
	private String name;
	private String jobConfig;
	
	private String schedule;
	private String jarPath;
	private String jobClassName;
	private int start;
	private Date createTime;
	
	/*Gatters and Setters*/
	public String getTbLid() {
		return tbLid;
	}
	public void setTbLid(String tbLid) {
		this.tbLid = tbLid;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJobConfig() {
		return jobConfig;
	}
	public void setJobConfig(String jobConfig) {
		this.jobConfig = jobConfig;
	}
	public String getSchedule() {
		return schedule;
	}
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	public String getJarPath() {
		return jarPath;
	}
	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}
	public String getJobClassName() {
		return jobClassName;
	}
	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}

