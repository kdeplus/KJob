package kde.jobcontainer.dataSync.policy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xxxxxxxx
 * 请求协议，包括请求码，请求参数数据
 * @date 2014-9-20, 下午1:45:50
 */
public class PolicyRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int TEST_CONNECTION_SIGN = 108;   //测试连接
	
	public static final int ADD_JOBCONFIG_SIGN = 109;   //创建job
	
	public static final int UPDATE_JOBCONFIG_SIGN = 110;   //修改job
	
	public static final int DELETE_JOBCONFIG_SIGN = 111;   //删除job
	
	public static final int UPDATE_NODE_STATUS_SIGN = 112;    //修改节点运行情况
	
	public int requestType;  //请求类型
	
	private Map<String, Serializable> parameter = new HashMap<String, Serializable>();
	
	public PolicyRequest() {}
	
	public PolicyRequest(int requestType) {
		this.requestType = requestType;
	}
	
	public void addParameter(String key,Serializable value) {   //添加参数数据
		this.parameter.put(key, value);
	}
	
	public Serializable getParamValue(String key) {   //获取参数数据
		return this.parameter.get(key);
	}

	/*Gatters and Setters*/
	public Map<String, Serializable> getParameter() {
		return parameter;
	}
	public void setParameter(Map<String, Serializable> parameter) {
		this.parameter = parameter;
	}
	public int getRequestType() {
		return requestType;
	}
	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}
}

