package kde.jobcontainer.util.interfaces;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface FileParseHelper {
	/**
	 * <pre>
	 * 根据文件和配置，以及所传入的类，对文件进行到对象的转换，生成对应的对象数据
	 * </pre>
	 * @author Lidong 2014-4-14
	 * @param c 要实例化的类
	 * @param f 要解析的文件
	 * @param farseCfg 解析文件所需的配置
	 * @return
	 */
	public List<Object> getListObjects(Class c,File f,JSONObject farseCfg) throws Exception;
	
	
	/**
	 * <pre>
	 * 根据文件和配置，对文件内容进行到JSON的转换，生成整个文件中数据的json对象数组
	 * </pre>
	 * @author Lidong 2014-4-14
	 * @param f
	 * @param farseCfg
	 * @return
	 */
	public JSONArray getJsonArrayValues(File f,JSONObject farseCfg,FileRecordParseInterface rpHelper) throws Exception;
	
	
	/**
	 * <pre>
	 * 从文件片段中获取到单个的数据对象
	 * </pre>
	 * @author Lidong 2014-4-14
	 * @param c
	 * @param f
	 * @param farseCfg
	 * @return
	 */
	public Object getObject(Class c,Object f,JSONObject farseCfg) throws Exception;
	
	
	/**
	 * <pre>
	 * 从文件片段中获取到单个的json数据对象
	 * </pre>
	 * @author Lidong 2014-4-14
	 * @param f
	 * @param farseCfg
	 * @return
	 */
	public JSONObject getJsonObject(Object f,JSONObject farseCfg) throws Exception;
	
	
	
	
	/**
	 * <pre>
	 * 从文件中获取拆分成条的数据信息
	 * </pre>
	 * @author Lidong 2014-4-16
	 * @param f
	 * @param parseCfg
	 * @return
	 * @throws Exception
	 */
	public List<String> getOneDatas(File f,JSONObject parseCfg) throws Exception;
}
