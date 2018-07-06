package kde.jobcontainer.dep.domain;

import java.util.Date;

/**
 * @author xxxxxxxx
 * @date 2014-9-26, 下午5:14:49
 * 系统配置参数类实例
 */
public class SysStatusConfig {
	private int id;     //id
	
	private String nodeName;    //节点英文名
	
	private int start;    //是否启动
	
	private Date lastUpdateTime;   //最后修改时间

	/*  Gatters and Setters  */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
}

