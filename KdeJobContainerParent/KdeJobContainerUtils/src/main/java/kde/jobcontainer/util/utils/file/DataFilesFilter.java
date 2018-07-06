package kde.jobcontainer.util.utils.file;

import java.io.File;
import java.io.FileFilter;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.util.utils.BaseConstants;
import kde.jobcontainer.util.utils.DateUtil;


/**
 * @author Lidong 2014-4-10
 * 
 */
public class DataFilesFilter implements FileFilter {
	
	private static Logger logger = LoggerFactory.getLogger( DataFilesFilter.class );
	
	private String nameFilter ;	//名字的正则表达式
	private Integer dayBefore ;	//改天数之内的文件
	private String fileExt ;	//扩展名
	private Date date;			//该日期之后的文件
	
	public DataFilesFilter(){}
	public DataFilesFilter(JSONObject c){
		if( !c.containsKey( "nameReg" ) ){
			logger.info( "文件过滤器没有文件名的过滤配置" );
		}else{
			//YYYYMMDDHH ^\d{4}\d{1,2}\d{1,2}\d{1,2}$
			this.nameFilter = c.getString( "nameReg" );
		}
		if(!c.containsKey( "dayBefore"  )){
			logger.info( "文件过滤器没有修改日期天数的过滤配置" );
		}else
			this.dayBefore = c.getInteger( "dayBefore" );
		if(!c.containsKey( "fileExt" )){
			logger.info( "文件过滤器没有文件后缀的过滤配置" );
		}else
			this.fileExt = c.getString( "fileExt" );
		if(!c.containsKey( "date" )){
			logger.info( "文件过滤器没有文件日期的总体过滤配置" );
		}else if(c.getString( "date" )!=null && !BaseConstants.EMPTY_STR.equals( c.getString("date")) ){
			try{
				this.date = DateUtil.StringToDateTime( c.getString( "date" ) );
			}catch(Exception e){
				logger.error( e.getMessage() , e);
			}
		}
		
	}
	public DataFilesFilter(String nameFilter , Integer dayBefore,String fileExt,Date date){
		//YYYYMMDDHH ^\d{4}\d{1,2}\d{1,2}\d{1,2}$
		this.nameFilter = nameFilter ;
		this.dayBefore =  dayBefore ;
		this.fileExt = fileExt;
		this.date = date;
	}
	public boolean accept(File f) {
		boolean result = false;
		try{
			if( this.nameFilter!=null&&!BaseConstants.EMPTY_STR.equals(this.nameFilter)&& !this.acceptName( f ))
				result = false;
			else if(this.dayBefore!=null&&this.dayBefore.intValue()!=0&&!this.acceptModifyDate(f))
				result =  false;
			else if(this.fileExt!=null&&!BaseConstants.EMPTY_STR.equals( this.fileExt )&&!this.acceptFileExt( f ))
				result =  false;
			else if(this.date!=null&&!this.acceptFileDate( f ))
				result =  false;
			else
				result =  true;
			
			return result;
		}finally{
			//System.out.println( "file filter :"+f.getName()+"\t"+result );
		}
	}

	public String getDescription() {
		return "根据所配置的参数对文件进行过滤";
	}

	/**
	 * @author aaa
	 * @param f
	 * @return
	 */
	private boolean acceptName(File f){
		String name = f.getName().substring(0, f.getName().lastIndexOf('.'));
		//System.out.println( nameFilter+"\t"+name+"\t"+Pattern.matches(nameFilter, name) );
		return Pattern.matches(nameFilter, name);
		
	}
	/**
	 * @author Lidong
	 * @param f
	 * @return 文件是否符合最后修改日期与配置的匹配
	 */
	private boolean acceptModifyDate(File f){
		Date d =  DateUtil.add(new Date(), Calendar.DATE, 0-dayBefore.intValue());
		long l = f.lastModified();
		long l2 = d.getTime();
		if(l>l2)
			return true;
		else 
			return false;
	}
	/**
	 * @author Lidong 2014-4-10
	 * @param f	
	 * @return
	 * <pre>
	 *	根据后缀名设置过滤文件,如果没有后缀名的设置则返回true
	 * </pre>
	 */
	private boolean acceptFileExt(File f){
		if(f.getName().endsWith( '.'+this.fileExt ))
			return true;
		else
			return false;
		
	}
	
	/**
	 * <pre>
	 * 根据传入的时间判断的,文件的修改时间要在该时间之后
	 * </pre>
	 * @author Lidong 2014-4-15
	 * @param f
	 * @return
	 */
	private boolean acceptFileDate(File f){
		long l = f.lastModified();
		long l2 = date.getTime();
		if(l>l2)
			return true;
		else 
			return false;

	}
	
	public static void main(String[] args) throws Exception  {
		
		//DataFilesFilter dff = new DataFilesFilter(null,null,"txt",null);
		
	}
	
}
