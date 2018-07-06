package kde.jobcontainer.util.interfaces;

import java.io.File;

import com.alibaba.fastjson.JSONObject;

public interface FileRecordParseInterface {
	public static final String keyValueSplitCode="keyValueSplitCode";
	public static final String keyValueBeginIndex="keyValueBeginIndex";
	public static final String keyValueParse = "keyValueParse";
	
	/**
	 * <pre>
	 * 从解析出的单个对象中获取数据
	 * </pre>
	 * @author Lidong 2014-4-16
	 * @param o 可以为一串字符串，一个对象，一个Element元素，一个JSONObject
	 * @param year
	 * @return
	 */
	public JSONObject getRecordFromrecord(Object o);
	
	
	
	
	/**
	 * <pre>
	 * 有可能需要从对象中获取部分资源
	 * </pre>
	 * @author Lidong 2014-4-16
	 * @param o  可以为一串字符串，一个对象，一个Element元素，一个JSONObject
	 * @param f
	 * @return
	 */
	public  JSONObject getRecordFromrecord(Object o,File f);
	/**
	 * <pre>
	 * 供使用Class.forName实例化对象后调用
	 * </pre>
	 * @author Lidong 2014-4-16
	 * @param cfg
	 */
	public void setCfg(JSONObject cfg);
}
