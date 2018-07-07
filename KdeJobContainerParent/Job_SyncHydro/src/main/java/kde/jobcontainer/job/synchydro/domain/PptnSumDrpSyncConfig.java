package kde.jobcontainer.job.synchydro.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * <pre>
 *    小时累计雨量同步的计算
 * </pre>
 * @author lidong
 * @date 2016年11月8日 上午11:19:19
 */
public class PptnSumDrpSyncConfig {
	private Logger logger = LoggerFactory.getLogger( PptnSumDrpSyncConfig.class );
	
	private JSONObject sourceDbConfig;
	private JSONObject targetDbConfig;	//目标数据库连接信息
	
	private String sourceFilter ;
	private String targetFilter ;
	
	private int hourAgo = 48;
	
	private Double hourMaxRain = 65d;	//需在配置项中补充对应内容,配置项中为空则不判断,配置项中如果设置了空字符？
	
	private String mqServerAddr;			//消息队列,用于保存任务执行的情况

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public JSONObject getSourceDbConfig() {
		return sourceDbConfig;
	}

	public void setSourceDbConfig(JSONObject sourceDbConfig) {
		this.sourceDbConfig = sourceDbConfig;
	}

	public JSONObject getTargetDbConfig() {
		return targetDbConfig;
	}

	public void setTargetDbConfig(JSONObject targetDbConfig) {
		this.targetDbConfig = targetDbConfig;
	}


	public int getHourAgo() {
		return hourAgo;
	}

	public void setHourAgo(int hourAgo) {
		this.hourAgo = hourAgo;
	}

	public String getMqServerAddr() {
		return mqServerAddr;
	}

	public void setMqServerAddr(String mqServerAddr) {
		this.mqServerAddr = mqServerAddr;
	}

	public String getSourceFilter() {
		return sourceFilter;
	}

	public void setSourceFilter(String sourceFilter) {
		this.sourceFilter = sourceFilter;
	}

	public String getTargetFilter() {
		return targetFilter;
	}

	public void setTargetFilter(String targetFilter) {
		this.targetFilter = targetFilter;
	}

	public Double getHourMaxRain() {
		return hourMaxRain;
	}

	public void setHourMaxRain(Double hourMaxRain) {
		this.hourMaxRain = hourMaxRain;
	}

	
}
