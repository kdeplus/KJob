package kde.jobcontainer.util.domain;

import java.io.Serializable;

/**
 * @author wsj
 * @date 2012-11-4, 下午08:12:01
 * 自定义消息对象，包含数据对象、对象id、和消息相关属性
 */
public class MsgObj implements Serializable{
	private Serializable dataObj;//数据对象
	private Serializable dataType;//记录传递的对象类型
	private DataOpLogD dataOpLog;//日志对象，包含表名、库名、操作类型、主键信息等
	
	public MsgObj() {
	}
	
	public MsgObj(Serializable dataObj,Serializable dataType, DataOpLogD dataOpLog) {
		this.dataObj = dataObj;
		this.dataType=dataType;
		this.dataOpLog=dataOpLog;
	}
	
	public DataOpLogD getDataOpLog() {
		return dataOpLog;
	}
	
	public void setDataOpLog(DataOpLogD dataOpLog) {
		this.dataOpLog = dataOpLog;
	}

	public Object getDataObj() {
		return dataObj;
	}
	public void setDataObj(Serializable dataObj) {
		this.dataObj = dataObj;
	}
	
	public Serializable getDataType() {
		return dataType;
	}

	public void setDataType(Serializable dataType) {
		this.dataType = dataType;
	}

	
	
}

