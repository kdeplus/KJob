package kde.jobcontainer.util.domain;

import java.sql.ResultSet;
import java.util.Observable;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.utils.BaseConstants;

public class DEPJobConfig extends Observable {
	private Logger logger = LoggerFactory.getLogger( DEPJobConfig.class );
	/**
	 * 默认构造函数
	 */
	public DEPJobConfig(){}
	
	/**
	 * 使用sql查询的Result进行构造
	 * @param rs
	 */
	public DEPJobConfig(ResultSet rs) throws Exception{
		this.id = rs.getInt("id");
		this.serverId = rs.getString("serverId");
		this.tag = rs.getString("tag");
		this.type = rs.getString("type");
		this.name = rs.getString("name");
		this.jobConfig = rs.getString("jobConfig");
		this.schedule = rs.getString("schedule");
		this.jarPath = rs.getString("jarPath");
		this.jobClassName = rs.getString("jobClassName");
		this.jobConfigJson = JSONObject.parseObject( this.jobConfig );
	}
	
	public DEPJobConfig(JSONObject json) throws Exception {
		this.id = json.getInteger( "id" );
		this.serverId = json.getString( "serverId" );
		this.tag = json.getString( "tag" );
		this.type = json.getString( "type" );
		this.name = json.getString( "name" );
		this.schedule = json.getString( "schedule" );
		this.jarPath = json.getString( "jarPath" );
		this.jobClassName = json.getString( "jobClassName" );
		this.jobConfigJson = json.getJSONObject( "jobConfig" );
		this.jobConfig = this.jobConfigJson.toString();
		this.start = json.getInteger( "start" );
	}
	public DEPJobConfig(Element e){
		//TODO
	}
	//TODO 子节点直接配置情况下与中心的同步?
	private Integer id;			//任务主键字段	--本地id字段
	private String serverId;	//服务器中job的tblid字段
	private String tag;			//本服务的标识,与中心节点同步,java.util.UUID
	private String type;		
	private String name;		//任务名称
	private String jobConfig;		//任务配置元素
	private JSONObject jobConfigJson;	//任务配置的json对象
	private String schedule;		//quartz 任务执行计划 表达式 '0 0/20 * * * ?'
	private String jarPath;			//任务类加载路径
	private String jobClassName;	//执行任务的类名称,类需集成KJob
	private int start;			//启动方式 0 不启动 ,1 自动启动,2 手工启动
	private String jobSystemName;	//拼写完成,该任务的唯一标识
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((jarPath == null) ? 0 : jarPath.hashCode());
		result = prime * result
				+ ((jobClassName == null) ? 0 : jobClassName.hashCode());
		result = prime * result
				+ ((jobConfig == null) ? 0 : jobConfig.hashCode());
		result = prime * result
				+ ((jobConfigJson == null) ? 0 : jobConfigJson.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((schedule == null) ? 0 : schedule.hashCode());
		result = prime * result
				+ ((serverId == null) ? 0 : serverId.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	
	
	/**
	 * 检查配置类是否不同,如系统运行过程中配置类发生了变化
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DEPJobConfig other = (DEPJobConfig) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (jarPath == null) {
			if (other.jarPath != null)
				return false;
		} else if (!jarPath.equals(other.jarPath))
			return false;
		if (jobClassName == null) {
			if (other.jobClassName != null)
				return false;
		} else if (!jobClassName.equals(other.jobClassName))
			return false;
		if (jobConfig == null) {
			if (other.jobConfig != null)
				return false;
		} else if (!jobConfig.equals(other.jobConfig))
			return false;
		if (jobConfigJson == null) {
			if (other.jobConfigJson != null)
				return false;
		} else if (!jobConfigJson.equals(other.jobConfigJson))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (schedule == null) {
			if (other.schedule != null)
				return false;
		} else if (!schedule.equals(other.schedule))
			return false;
		if (serverId == null) {
			if (other.serverId != null)
				return false;
		} else if (!serverId.equals(other.serverId))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
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

	public JSONObject getJobConfigJson() {
		return jobConfigJson;
	}

	public void setJobConfigJson(JSONObject jobConfigJson) {
		this.jobConfigJson = jobConfigJson;
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
	public String getJobSystemName(){
		if(this.jobSystemName==null||this.jobSystemName.equals(BaseConstants.EMPTY_STR))
			this.jobSystemName = this.id+'_'+this.type+"_"+this.tag+'_'+this.serverId ;
		return this.jobSystemName;
	}
	public void changed(){
		this.setChanged();
		this.notifyObservers();
	}
}