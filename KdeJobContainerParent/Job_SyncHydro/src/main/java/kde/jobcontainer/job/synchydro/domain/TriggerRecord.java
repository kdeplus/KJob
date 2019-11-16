package kde.jobcontainer.job.synchydro.domain;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
/*
{
    "SLSH": 55691782,
    "STCD": "11427455",
    "TABID": "ST_PPTN_R",
    "TM": {
        "date": 25,
        "day": 1,
        "hours": 15,
        "minutes": 0,
        "month": 4,
        "nanos": 0,
        "seconds": 0,
        "time": 1432537200000,
        "timezoneOffset": -480,
        "year": 115
    },
    "EXCKEY": "0",
    "OPERATION": "I",
    "EXCINF": "STCD:\"11427455\",TM:\"2015-05-25 15:00:00\",DRP:\"0.0\",INTV:\"1.00\",",
    "MODITIME": {
        "date": 25,
        "day": 1,
        "hours": 14,
        "minutes": 18,
        "month": 4,
        "nanos": 480000000,
        "seconds": 25,
        "time": 1432534705480,
        "timezoneOffset": -480,
        "year": 115
    }
}
 */
public class TriggerRecord {
	public static Logger logger = LoggerFactory.getLogger( TriggerRecord.class );
	
	
	public int SLSH;
	public String STCD;
	public String TABID;
	public String TM;
	public String EXCKEY;
	public String OPERATION;
	public String MODITIME;
	public String EXCINF;
	
	public TriggerRecord() {
		
	}
	public TriggerRecord(JSONObject obj) {
		this.SLSH = obj.getIntValue("SLSH");
		this.STCD = obj.getString( "STCD" );
		this.TM = obj.getString( "TM" );
		this.EXCINF = obj.getString("EXCINF");
		this.OPERATION = obj.getString("OPERATION");
	}
	
	public int getSLSH() {
		return SLSH;
	}
	public void setSLSH(int sLSH) {
		SLSH = sLSH;
	}
	public String getSTCD() {
		return STCD;
	}
	public void setSTCD(String sTCD) {
		STCD = sTCD;
	}
	public String getTABID() {
		return TABID;
	}
	public void setTABID(String tABID) {
		TABID = tABID;
	}
	public String getEXCKEY() {
		return EXCKEY;
	}
	public void setEXCKEY(String eXCKEY) {
		EXCKEY = eXCKEY;
	}
	public String getOPERATION() {
		return OPERATION;
	}
	public void setOPERATION(String oPERATION) {
		OPERATION = oPERATION;
	}
	
	public String getEXCINF() {
		return EXCINF;
	}
	public void setEXCINF(String eXCINF) {
		EXCINF = eXCINF;
	}
	
	/**
	 * @author lidong 2015年5月26日
	 * @param pks	主键字段，以逗号隔开
	 * @param dbtype	数据库类型,用来处理不同数据库的特殊语句
	 * @return
	 * <pre>
	 * 自己解析数据，获取数据操作的sql 语句,根据不通
	 * </pre>
	 */
	public String getSql(String pks,String dbtype){
		//String[] pkss = pks.split( "," );//以逗号隔开的字符串
		JSONObject j = null ;
		char c = this.getOPERATION().charAt(0);
		switch(c){
		case 'D':
			return this.getDeleteSql(pks, dbtype);
		case 'U':
			return this.getUpdateSql( pks , dbtype);
		case 'I':
			return this.getInsertSql( dbtype );
			default:
				return null;
		}
	}
	
	public String getDeleteSql(String pks,String dbtype){
		String[] pkss = pks.split(",");
		StringBuilder sb = new StringBuilder();
		sb.append( " delete from " ).append( this.getTABID() )
			.append( " where 1=1 " );
		JSONObject j = this.getExcinfJSON();
		for(int i=0;i<pkss.length;i++){
			sb.append(" and ").append( pkss[i] ).append("=")
				.append( this.getColumnValue(pkss[i], j, dbtype) ).append(" ");
		}
		return sb.toString();
	}
	
	/**
	 * @author lidong 2015年5月26日
	 * @param pk	主键字符串
	 * @param dbtype	数据库类型
	 * @return
	 * <pre>
	 * 通过excinf的数据，获取更新sql语句
	 * </pre>
	 */
	public String getUpdateSql(String pk,String dbtype){
		//20150606 lidong 提到前面，如果excinfJSON为空或无法转成json，没有key，则直接跳过
		JSONObject j = this.getExcinfJSON();

		String[] pks = pk.split(",");
		StringBuilder sb = new StringBuilder();
		sb.append( " update " ).append( this.getTABID() )
			.append( " set " );
		boolean updateColumn = false ;	//20150608 lidong 发现松辽委数据有更新但字段为空的，要跳过这种语句
		String tmpStr = null;
		//循环 json中的key,设定更新数据
		for(Iterator<String> it=j.keySet().iterator();it.hasNext();){
			tmpStr = it.next();
			if(pk.contains( tmpStr ))
				continue;
			updateColumn = true;
			sb.append( tmpStr ).append( "=" )
				.append( this.getColumnValue(tmpStr, j, dbtype) )
				.append( ' ' );
			sb.append( "," );
		}
		if(  !updateColumn ){
			logger.debug( "该语句无法转换成update语句，无可转换为json的excinf信息 SLSH:" + this.getSLSH()+"\texcinf:"+this.getEXCINF());
			return null;
		}
		//删掉最后一个逗号
		int idx = sb.length();
		if( sb.charAt( idx-1 )==',' )
			sb.deleteCharAt( idx-1 );
		sb.append( " where 1=1 " );
		//设置where中的主键信息
		for(int i=0;i<pks.length;i++){
			sb.append(" and ").append( pks[i] ).append("=")
				.append( this.getColumnValue(pks[i], j, dbtype) ).append("");
		}
		return sb.toString();
	}
	
	/**
	 * @author lidong 2015年5月26日
	 * @param dbtype
	 * @return
	 * <pre>
	 * 获取数据插入的sql语句
	 * </pre>
	 */
	public String getInsertSql(String dbtype){
		
		StringBuilder sb = new StringBuilder();		//存整个sql
		StringBuilder sb2 = new StringBuilder();	//存values	
		sb.append( " insert into " ).append( this.getTABID() )
			.append( "( " );
		
		sb2.append( " values (" );
		JSONObject j = this.getExcinfJSON();
		String tmpStr = null;
		//循环 json中的key,获取字段名和值,分别放到sb和sb2中
		for(Iterator<String> it=j.keySet().iterator();it.hasNext();){
			tmpStr = it.next();
			sb.append( tmpStr ).append( ',' );
			sb2.append( this.getColumnValue(tmpStr, j, dbtype) ).append(',');
		}
		//删掉最后一个逗号
		int idx = sb.length();
		if( sb.charAt( idx-1 )==',' )
			sb.deleteCharAt( idx-1 );
		idx = sb2.length();
		if( sb2.charAt( idx-1 )==',' )
			sb2.deleteCharAt( idx-1 );
		//insert +values
		sb.append( ')' ).append( sb2 ).append( ')' );
		return sb.toString();
	}
	
	
	/**
	 * @author lidong 2015年5月26日
	 * @param key		字段名称
	 * @param excinf	存放数据操作记录的字段内容
	 * @param dbtype	数据库类型,sqlserver\oracle
	 * @return
	 * <pre>
	 * 根据字段名称获取对应数据库类型的值
	 * 返回的值里面会有
	 * </pre>
	 */
	public String getColumnValue(String key,JSONObject excinf,String dbtype){
		//System.out.println( excinf );
		Object obj = excinf.get( key );
		if(obj==null)
			return "null";
		if( obj instanceof java.lang.String ){
			//根据时间进行处理
			if(key.equals( "TM" )){
				if("oracle".equals( dbtype )){
					return "to_date('"+(String)obj+"','yyyy-MM-dd HH24:mi:ss')";
				}else if("sqlserver".equals( dbtype )){
					return "'"+(String)obj+"'";
				}else if(1==1){	//其他类型
					return excinf.getString( key );
				}
			}else{
				return "'"+excinf.getString( key )+"'";
			}
		}else if( obj instanceof java.lang.Double){
			return String.valueOf( (Double)obj );
		}else if( obj instanceof java.lang.Integer ){
			return String.valueOf( (Integer)obj );
		}else if( obj instanceof JSONObject ){
			
		}else 
			return String.valueOf( obj );
		return "null";
	}
	
	public JSONObject getExcinfJSON(){
		return JSONObject.parseObject( "{"+this.getEXCINF()+"}" );
	}
//	public static void main(String[] args){
//		String s = "{STCD:\"11427455\",TM:\"2015-05-25 15:00:00\",DRP:0.0,INTV:\"1.00\",\"MODITIME\": {        \"date\": 25,        \"day\": 1,        \"hours\": 14,        \"minutes\": 18,        \"month\": 4,        \"nanos\": 480000000,        \"seconds\": 25,        \"time\": 1432534705480,        \"timezoneOffset\": -480,        \"year\": 115    }}";
//		JSONObject obj = JSONObject.fromObject( s );
//		System.out.println( obj.get( "STCD" ).getClass() );
//		System.out.println( obj.get( "TM" ).getClass() );
//		System.out.println( obj.get( "DRP" ).getClass() );
//		System.out.println( obj.get( "INTV" ).getClass() );
//		Object j = new Integer(23);
//		Object j1 = new Double(23.2);
//		System.out.println( String.valueOf( j ) );
//		System.out.println( String.valueOf( j1 ) );
//		
//	}
	public String getTM() {
		return TM;
	}
	public void setTM(String tM) {
		TM = tM;
	}
	public String getMODITIME() {
		return MODITIME;
	}
	public void setMODITIME(String mODITIME) {
		MODITIME = mODITIME;
	}
	
}
