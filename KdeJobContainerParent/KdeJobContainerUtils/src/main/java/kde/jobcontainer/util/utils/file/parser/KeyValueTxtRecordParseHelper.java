package kde.jobcontainer.util.utils.file.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.interfaces.FileRecordParseInterface;

/**
 * @author Lidong 2014-4-16
 * 一般的转换键值性数据的转换方法
 */
public class KeyValueTxtRecordParseHelper implements FileRecordParseInterface {
	
	private JSONObject fileParseCfg;
	
	public KeyValueTxtRecordParseHelper(){}
	public KeyValueTxtRecordParseHelper(JSONObject fileParseCfg){
		this.fileParseCfg = fileParseCfg;
	}
	
	/* (non-Javadoc)
	 * @see kde.jobcontainer.utilinterfaces.FileRecordParseInterface#setCfg(net.sf.json.JSONObject)
	 */
	public void setCfg(JSONObject cfg){
		this.fileParseCfg = cfg;
	}
	
	/* (non-Javadoc)
	 * @see kde.jobcontainer.utilinterfaces.FileRecordParseInterface#getRecordFromrecord(java.lang.Object)
	 */
	public JSONObject getRecordFromrecord(Object o ,File f){
		return this.getRecordFromrecord(o);
	}
	
	public JSONObject getRecordFromrecord(Object o ) {
		String rec = (String)o;
		JSONObject json = new JSONObject();
		String[] vs = rec.split( fileParseCfg.getString( keyValueSplitCode ) );
		
		Map<String,String> valueMap = new HashMap<String,String>();

		for(int i= this.fileParseCfg.getInteger( this.keyValueBeginIndex ) ;i<vs.length;){
			valueMap.put( vs[i] , vs[i+1]);
			i+=2;
		}
		String tmpStr = null;
		String objAttr = null;
		JSONObject keyValueParse = this.fileParseCfg.getJSONObject( this.keyValueParse );
		for(Iterator<String> it = keyValueParse.keySet().iterator();it.hasNext();){
			tmpStr = it.next();
			if(valueMap.containsKey(tmpStr)){
				objAttr = keyValueParse.getString( tmpStr );
				if(objAttr.indexOf(',')!=-1){
					String[] attrs = objAttr.split(",");
					for(String a:attrs){
						json.put( a , valueMap.get( tmpStr ));
					}
				}else{
					json.put( objAttr , valueMap.get( tmpStr ));
				}
			}
		}
		
		return json;
	}

}
