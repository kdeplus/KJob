package kde.jobcontainer.util.utils.file.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.util.interfaces.FileParseHelper;
import kde.jobcontainer.util.utils.BaseConstants;

public class FileParseHelperUtil {
	
	private static Logger logger = LoggerFactory.getLogger( FileParseHelperUtil.class );
	
	private static Map<String,FileParseHelper> helpers = new HashMap<String,FileParseHelper>();
	
	
	public static FileParseHelper getParseHelper( String name ) throws Exception{
		if( helpers.containsKey( name ) )
			return (FileParseHelper)helpers.get( name );
		else{
			FileParseHelper f = (FileParseHelper)Class.forName( name ).newInstance();
			helpers.put( name , f);
			return f;
		}
	}
	
	/*
	 * <pre>
	 * 根据文件和配置，以及所传入的类，对文件进行到对象的转换，生成对应的对象数据
	 * </pre>
	 * @author Lidong 2014-4-14
	 * @param c 要实例化的类
	 * @param f 要解析的文件
	 * @param farseCfg 解析文件所需的配置
	 * @param fileType 文件类型 1 txt，2 xml，3 json
	 * @return
	public List<Object> getListObjects(String fileType,Class c,File f,JSONObject parseCfg) throws Exception{
		
		return this.getHelper(fileType).getListObjects(c, f, parseCfg);
	}
	*/
	
	/*
	 * <pre>
	 * 根据文件和配置，对文件内容进行到JSON的转换，生成整个文件中数据的json对象
	 * </pre>
	 * @author Lidong 2014-4-14
	 * @param f
	 * @param farseCfg
	 * @param fileType 文件类型 1 txt，2 xml，3 json
	 * @return
	 
	public JSONArray getJsonArrayValues(String fileType,File f,JSONObject parseCfg) throws Exception{
		return this.getHelper(fileType).getJsonArrayValues(f, parseCfg,);
	}
	*/
	/*
	 * <pre>
	 * 从文件片段中获取到单个的数据对象
	 * </pre>
	 * @author Lidong 2014-4-14
	 * @param c
	 * @param f
	 * @param farseCfg
	 * @return
	 
	public Object getObject(String fileType,Class c,Object f,JSONObject parseCfg) throws Exception{
		return this.getHelper(fileType).getObject(c, f, parseCfg);
	}
	
	*/
	/*
	 * <pre>
	 * 从文件片段中获取到单个的json数据对象
	 * </pre>
	 * @author Lidong 2014-4-14
	 * @param f
	 * @param farseCfg
	 * @return
	 
	public JSONObject getJsonObject(String fileType,Object f,JSONObject parseCfg) throws Exception{
		return this.getHelper(fileType).getJsonObject(f, parseCfg);
	}
	*/
	
	
	
	public static String getContentOfFile(File f) throws Exception{
		if(!f.exists()){
			logger.warn( "需要解析的文件不存在" );
			return BaseConstants.EMPTY_STR;
		}
		BufferedReader in = new BufferedReader(new FileReader(f));
        String str;
        StringBuilder sb = new StringBuilder();
        while ((str = in.readLine()) != null) 
        {
              sb.append( str );
        }
        in.close();
        return sb.toString();
	}
}
