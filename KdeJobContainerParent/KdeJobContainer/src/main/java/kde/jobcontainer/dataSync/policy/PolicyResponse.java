package kde.jobcontainer.dataSync.policy;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import kde.jobcontainer.dataSync.util.DataConstants;

/**
 * @author xxxxxxxx
 * 
 * @date 2014-9-20, 下午1:46:10
 */
public class PolicyResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int SUCCESS_SIGN = 100;
	
	public static final int FAILURE_SIGN = 500;   //错误号
	
	private int state;   //状态
	
	private Map<String, Serializable> parameter = Collections.synchronizedMap(new HashMap<String, Serializable>());
	
	public PolicyResponse() {}
	
	public PolicyResponse(int state) {
		this.state = state;
	}
	
	public PolicyResponse(Exception e) {
		this.state = FAILURE_SIGN;
		this.parameter.put(DataConstants.ERROR_MESSAGE_KEY, "节点处理出错，出错信息="+e.getMessage());
	}
	
	public void addParameter(String key,Serializable value) {   //添加参数数据
		this.parameter.put(key, value);
	}
	
	public Serializable getParamValue(String key) {   //获取参数数据
		return this.parameter.get(key);
	}
	
	public void createASimpleSuccess() {   //创建一个最基本的成功消息
		this.state = SUCCESS_SIGN;
		this.parameter.put(DataConstants.BASE_SUCCESS_KEY, "处理成功!");
	}

	/* Gatters and Setters */
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public Map<String, Serializable> getParameter() {
		return parameter;
	}
	public void setParameter(Map<String, Serializable> parameter) {
		this.parameter = parameter;
	}
}

