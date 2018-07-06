package kde.jobcontainer.util.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
	/**
	 * 为给定的日历字段添加或减去指定的时间量，并返回修改后的时间
	 * @param date
	 * @param field
	 * @param amount
	 * @return
	 */
	public static Date add(Date date, int field, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);
		return cal.getTime();
	}

	/**yyyy-MM-dd HH:mm:ss  */
	/**
	 * @param Date tm 要转换的时间
	 * @param Date level 默认为秒，1分钟，2为小时，3为天，4为月，5为年 
	 * @return 格式化的时间String
	 * @author LiDong
	 * @comment tm为null的话返回 “”
	 */
	public static String DateTimeToString(Date tm,int level)
	{
		if(tm==null) return "";
		String formatStr = getFormatStr( level );
		SimpleDateFormat dateFormt = new SimpleDateFormat( formatStr );
		return dateFormt.format( tm );
	}
	
	public static String DateTimeToString(Date tm,String formatStr)
	{
		if(tm==null) return "";
		SimpleDateFormat dateFormt = new SimpleDateFormat( formatStr );
		return dateFormt.format( tm );
	}
	
	/**
	 * @param dt 出生日期
	 * @return  根据出生日期和当前的日期计算年龄
	 */
	public static Integer getAge(Date dt)
	{
		if(dt==null)
		{
			return null;
		}
		try{
			Calendar c = Calendar.getInstance();
			Calendar birth = Calendar.getInstance();
			birth.setTime( dt );
			int year = (int)Math.floor( daysBetween(birth,c)/365.25 )  ;
			return new Integer(year);
		}catch(Exception ex)
		{
			System.out.println("出生年月计算年龄出错"+dt.toGMTString());
			return null;
		}
	}
	public static String getFormatStr(int level){
		String formatStr = null;
		
		switch( level )
		{
			case 1:
				formatStr = mm;
				break;
			case 2:
				formatStr = HH;
				break;
			case 3:
				formatStr = dd;
				break;
			case 4:
				formatStr = MM;
				break;
			case 5:
				formatStr = yyyy;
				break;
			case 6:
				formatStr = MM_DD;
				break;
			case 7:
				formatStr = MM_DD_HH;
				break;
			case 8:
				formatStr = all;
				break;
			default:
				formatStr = ss;
		}
		return formatStr;
	}
	
	public static Date StringToDateTime(String str,int level){
		String formatStr = getFormatStr( level );
		return StringToDateTime(str,formatStr);
	}
	public static Date StringToDateTime(String str, String formatStr){
		if(str==null||str.trim().equals("")){
			return null;
		}
		SimpleDateFormat dateFormt = new SimpleDateFormat( formatStr );
		try{
			return dateFormt.parse( str );
		}catch(ParseException pe){
			throw new RuntimeException( "将时间字符转换为日期错误:"+str+"\t"+formatStr );
		}
	}
	
	public static Date StringToDateTime(String str){
		if(str==null||str.length()==0)
			return null;
		int len =  str.length();
		switch(len){
		case 10:
			return StringToDateTime(str,3);
		case 13:
			return StringToDateTime(str,2);
		case 16:
			return StringToDateTime(str,1);
		case 19:
			return StringToDateTime(str,99); //使用 ss
		}
		
		return null;
	}
	
	public static Date setDate(int year, int month, int day) {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.YEAR, year);
		now.set(Calendar.MONTH, month);
		now.set(Calendar.DAY_OF_MONTH, day);
		return now.getTime();
	}
	//将日期转换为字符串格式
    public static String converDateToString(Date date)
    {
    	if(date.equals(null))return "";
    	SimpleDateFormat formater=new SimpleDateFormat("yyyy-MM-dd");
		String dateformat=formater.format(date);
		return dateformat;
    }
  //获取日期的前n位 
	public static String getString(Date date,int length)
	{
		if(date==null )return "";
		else if(date.toString().length()<length)
			return date.toString();
		else
			return date.toString().substring(0,length);
	}
	
	public static Date converStringToDate(String date, String format) throws ParseException {
		SimpleDateFormat _format = new SimpleDateFormat(format);
		Date _date = _format.parse(date);
		return _date;
	}
	
	public static List<Date> getMonthEveryDay(Date d){
		List<Date> s = new ArrayList<Date>();
		int year = d.getYear()+1900;
		int month = d.getMonth();
		Date first = DateUtil.setDate(year, month, 1);
		s.add( first );
		
		while(true){
			first = DateUtil.add(first, Calendar.DAY_OF_MONTH, 1);
			if(first.getMonth()==month)
				s.add( first );
			else
				break;
		}
		return s;
	}
	
	public static List<Date> getMonthDate(int year,int month){
		List<Date> l = new ArrayList<Date>();
		Date d = DateUtil.setDate(year, month, 1);
		l.add(d);
		while(true){
			d = DateUtil.add(d, Calendar.DATE, 5);
			if(d.getMonth()==month)
				l.add(d);
			else
				break;
		}
		return l;
	}

	public static double HourMillSecs = 3600*1000; 
	public static double getHoursBetween(Date d1,Date d2){
		if(d1==null||d2==null)
			throw new RuntimeException("比较的时间不能为空!"+d1+"\t"+d2);
		double z = (double)(d1.getTime()-d2.getTime());
		z = z/HourMillSecs;
		if(z<0) z=  0-z;
		return z;
	}
	
	public static double getSecongdsBetween(Date d1,Date d2 ){
		if(d1==null||d2==null)
			throw new RuntimeException("比较的时间不能为空!"+d1+"\t"+d2);
		
		return (d2.getTime()-d1.getTime())/1000;
	}
	public static double daysBetween(Date from,Date to){
		double hours = getHoursBetween(from,to);
		return hours/24;
		
	}
	/**
	 * @param pFormer
	 * @param pLatter
	 * @return 获取两个日期间的天数
	 */
	public static int daysBetween(Calendar pFormer,Calendar pLatter){
		
        Calendar vFormer = pFormer,vLatter = pLatter;
        boolean vPositive = true;
        if( pFormer.before(pLatter) ){
            vFormer = pFormer;
            vLatter = pLatter;
        }else{
            vFormer = pLatter;
            vLatter = pFormer;
            vPositive = false;
        }
 
        vFormer.set(Calendar.MILLISECOND,0);
        vFormer.set(Calendar.SECOND,0);
        vFormer.set(Calendar.MINUTE,0);
        vFormer.set(Calendar.HOUR_OF_DAY,0);
        vLatter.set(Calendar.MILLISECOND,0);
        vLatter.set(Calendar.SECOND,0);
        vLatter.set(Calendar.MINUTE,0);
        vLatter.set(Calendar.HOUR_OF_DAY,0);
 
        int vCounter = 0;
        while(vFormer.before(vLatter)){
            vFormer.add(Calendar.DATE, 1);
            vCounter++;
        }
        if( vPositive)
            return vCounter;
        else
            return -vCounter;
    }
	public static long year = 10*3600*24*36525;
	public static String all = "yyyyMMddHHmm";
	public static String ss = "yyyy-MM-dd HH:mm:ss";
	public static String mm = "yyyy-MM-dd HH:mm";
	public static String HH = "yyyy-MM-dd HH";
	public static String dd = "yyyy-MM-dd";
	public static String MM = "yyyy-MM";
	public static String yyyy = "yyyy";
	public static String MM_DD = "MM-dd";
	public static String MM_DD_HH = "MM-dd HH";
	/** 5 保留到年*/
	public static int Year = 5;
	/** 4 保留到月*/
	public static int Month = 4;
	/** 3 保留到天*/
	public static int Date = 3;
	/** 2 保留到小时*/
	public static int Hour = 2;
	/** 1 保留到分*/
	public static int Minute = 1;
	/** 6 只取月和日*/
	public static int Month_Date = 6;
	/** 7 只取月和日小时*/
	public static int Month_Date_HH = 7;
	
	
}
