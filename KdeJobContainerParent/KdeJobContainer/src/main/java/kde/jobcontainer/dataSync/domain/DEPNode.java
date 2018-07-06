package kde.jobcontainer.dataSync.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Lidong 2014-4-26
 * 数据交换平台的节点信息
 */
public class DEPNode implements Serializable {

	private static final long serialVersionUID = 7019177245005045628L;
	
	private String tblid;
	private String name;
	private String type;
	private String descText;
	private String ip;
	private Integer  port;
	private String deptId;
	private Integer statusTag;
	private Date createTime;
	private String remark;
	private List<DepJobConfig> configs;
	
	
	private DepJobConfig jobConfig;	//查询用
	 
	public String getTblid() {
		return this.tblid;
	}

	public void setTblid(String value) {
		this.tblid = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescText() {
		return descText;
	}

	public void setDescText(String descText) {
		this.descText = descText;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public Integer getStatusTag() {
		return statusTag;
	}

	public void setStatusTag(Integer statusTag) {
		this.statusTag = statusTag;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<DepJobConfig> getConfigs() {
		return configs;
	}

	public void setConfigs(List<DepJobConfig> configs) {
		this.configs = configs;
	}

	public DepJobConfig getJobConfig() {
		return jobConfig;
	}

	public void setJobConfig(DepJobConfig jobConfig) {
		this.jobConfig = jobConfig;
	}

	/*
	create table DEP_NODE(
		TBLID		varchar(32) not null primary key,
		NAME		varchar(64),
		TYPE		varchar(16),
		DESCTEXT	varchar(512),
		IP			varchar(16),
		PORT		int,			--为子节点消息中间件预留
		DEPTID		varchar(64)	,	--标识节点单位
		STATUSTAG		int,				--当前状态，
		CREATETIME smalldatetime default getDate(), --默认的记录添加时间
		REMARK	varchar(256)
	)
	*/
}
