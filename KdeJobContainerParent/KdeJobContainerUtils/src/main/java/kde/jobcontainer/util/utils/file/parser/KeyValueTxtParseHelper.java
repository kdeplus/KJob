package kde.jobcontainer.util.utils.file.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.interfaces.FileParseHelper;
import kde.jobcontainer.util.interfaces.FileRecordParseInterface;

public class KeyValueTxtParseHelper implements FileParseHelper {
	
	private static Logger logger = LoggerFactory.getLogger( KeyValueTxtParseHelper.class );
	
	public KeyValueTxtParseHelper(){}
	
	/*
	K┗┛30559804┗┛08010800┗┛P6┗┛10┗┛PD┗┛24.5┗┛WS┗┛8┗┛PX┗┛68.5┗┛PM┗┛98.5┗┛QI┗┛308┗┛DT┗┛6┗┛ZU┗┛318.05┗┛ZS┗┛4┗┛W┗┛1500┗┛QA┗┛401┗┛QID┗┛300┗┛ZUD┗┛318.01┗┛WD┗┛1500┗┛QAD┗┛390┗┛NN
	*/
	public List<Object> getListObjects(Class c, File f, JSONObject parseCfg)  throws Exception{
		// TODO Auto-generated method stub
		List<String> datas = this.getOneDatas(f, parseCfg);
		
		
		return null;
	}

	public JSONArray getJsonArrayValues(File f, JSONObject parseCfg,FileRecordParseInterface rpHelper)  throws Exception{
		List<String> datas = this.getOneDatas(f, parseCfg);
		if(datas==null||datas.size()==0){
			logger.warn( f.getName()+"中没有获取到可供分析的数据" );
			return null;
		}
		JSONArray arr = new JSONArray();
		for(String s:datas){
			arr.add( rpHelper.getRecordFromrecord(s, f) );
		}
		return arr;
	}

	public Object getObject(Class c, Object f, JSONObject parseCfg) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public JSONObject getJsonObject(Object f, JSONObject parseCfg)  throws Exception{
		// TODO Auto-generated method stub
		
		return null;
	}
	
	public List<String> getOneDatas(File f,JSONObject parseCfg) throws Exception{
		List<String> records = new ArrayList<String>();
		//TODO 从文件中读取出每一个数据记录
		String content = FileParseHelperUtil.getContentOfFile(f);
		String splitRegExp = parseCfg.getString( "splitRegExp" );
		Pattern p = Pattern.compile(splitRegExp);
		Matcher m = p.matcher( content );
		while(m.find()){
			records.add( m.group().replaceAll("\n", "") );
		}
		return records;
	}	
}
