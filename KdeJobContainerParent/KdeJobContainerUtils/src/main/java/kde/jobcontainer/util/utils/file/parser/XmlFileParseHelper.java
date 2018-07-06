package kde.jobcontainer.util.utils.file.parser;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.interfaces.FileParseHelper;
import kde.jobcontainer.util.interfaces.FileRecordParseInterface;

/**
 * @author Lidong 2014-4-14
 * TODO 待实现
 */
public class XmlFileParseHelper implements FileParseHelper{

	public XmlFileParseHelper(){}
	
	public List<Object> getListObjects(Class c, File f, JSONObject farseCfg) {
		// TODO Auto-generated method stub
		return null;
	}

	public JSONArray getJsonArrayValues(File f, JSONObject farseCfg,FileRecordParseInterface rpHelper) {
		// TODO Auto-generated method stub
		return null;
	}
	

	public Object getObject(Class c,Object f,JSONObject farseCfg){
		return null;
	}

	public JSONObject getJsonObject(Object f,JSONObject farseCfg){
		return null;
	}
	
	public List<String> getOneDatas(File f,JSONObject parseCfg) throws Exception{
		return null;
	}
}
