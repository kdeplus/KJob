package kde.jobcontainer.job.synchydro.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.job.synchydro.domain.PptnSumDrpSyncConfig;
import kde.jobcontainer.util.domain.DEPJobConfig;
import kde.jobcontainer.util.domain.DbConfig;
import kde.jobcontainer.util.utils.StringUtil;
import kde.jobcontainer.util.utils.db.DbUtil;

/**
 * @author lidong 2015年1月29日
 * <pre>
 * 
 * </pre>
 */
public class HYITSTriggerRecordSyncDao {
	private static Logger logger = LoggerFactory
			.getLogger(HYITSTriggerRecordSyncDao.class);
	
	/**  自身对象
	 *  由于是单例模式,所以本身不能持有状态及其他配置项信息,不然无法同时运行两个
	 *   */
	private static HYITSTriggerRecordSyncDao _instance;
	
	public static HYITSTriggerRecordSyncDao getInstance(){	//单例方法
		if(_instance==null)
			_instance = new HYITSTriggerRecordSyncDao();
		
		return _instance; 
	}
	
	/**
	 * @author lidong 2015年1月29日
	 * @param whereSql	需要补充的数据
	 * @param cfg		数据库连接信息
	 * @param recordTable	存储数据的表
	 * @param rowsOnce	一次同步的行数
	 * @return
	 * @throws Exception
	 * <pre>
	 * 通过过滤条件,获取数据
	 * </pre>
	 */
	public JSONArray getDatas(String whereSql,DbConfig cfg,String recordTable,int rowsOnce,StringBuilder sb)  throws Exception{
		Connection conn = null ;
		JSONArray arr = new JSONArray();
		String sql = null;
		try{
			conn = DbUtil.getConnection( cfg );
			if(conn==null){
				throw new Exception( "无法获取到数据库链接"+JSONObject.toJSONString( cfg ).toString() );
			}
			//直接限制单次10000调
			if(cfg.getDriver().indexOf("oracle")!=-1){
				//sql = "select * from st_sendwait_e where rownum<10000 ";
				sql = "select * from "+recordTable+" where 1=1 " ;
				if(rowsOnce!=0)
					sql+= " and rownum<"+ String.valueOf( rowsOnce );
			}else if(cfg.getDriver().indexOf("jtds")!=-1){
				sql = " select "+(rowsOnce==0?"":("top "+rowsOnce))
						+" * from "+recordTable+" where 1=1 ";
			}else{
				throw new Exception( "驱动无法识别" );
			}
			sql+= " "+whereSql;
			sql+=" order by slsh ";
			ResultSet rs = conn
					.createStatement().executeQuery( sql );
			while(rs.next()){
				arr.add( this.getStSendWaitEJson(rs) );
			}
			rs.close();
			
		}catch(Exception e){
			String msg = "执行sql出错:"+e.getMessage()+"\r\n"+sql;
			sb.append("\r\n").append( msg );
			logger.error( msg,e );
			
		}finally{
			if(conn!=null)
				conn.close();
			String msg =  "查询库中待同步雨水情数据,SQL:\t"+sql+",\t得到"+arr.size()+"条数据.";
			logger.debug( msg );
			sb.append("\r\n").append( msg );
			return arr;
		}
	}
	
	
	
	public static JSONObject getStSendWaitEJson( ResultSet rs ) throws SQLException{
		JSONObject json = new JSONObject();
		int x = 0;
		/*
		class java.math.BigDecimal
		class java.lang.String
		class java.lang.String
		class java.sql.Timestamp
		class java.lang.String
		class java.lang.String
		class java.lang.String
		class java.lang.String
		class java.sql.Timestamp
		*/
		json.put( "SLSH" , rs.getObject("SLSH"));
		json.put( "STCD" , rs.getObject("STCD"));
		json.put( "TABID" , rs.getObject("TABID"));
		json.put( "TM" , rs.getObject("TM"));
		json.put( "EXCKEY" , rs.getObject("EXCKEY"));
		json.put( "OPERATION" , rs.getObject("OPERATION"));
		json.put( "EXCINF" , rs.getObject("EXCINF"));
		//json.put( "POLLSTATUS" , rs.getObject("POLLSTATUS")); 20150510 没有啥用的字段
		json.put( "MODITIME" , rs.getObject("MODITIME"));
		return json;
	}
	
	
	public static void main(String[] args) throws Exception{
		String str = "STCD:\"62913900\",TM:\"2015-01-16 08:00:00\",UPZ:\"8.720\",";			;
		if(str.endsWith(",")){
			str = str.substring( 0 , str.length()-1);
		}
		System.out.println( str );
		/*
		DbConfig cfg = new DbConfig();
		cfg.setDriver("net.sourceforge.jtds.jdbc.Driver");
		cfg.setUrl("jdbc:jtds:sqlserver://10.34.0.6:1433;databaseName=ahsl_yc");;
		cfg.setUsername("ahsl");
		cfg.setUserpwd("ahsl2014");
		
		Connection conn = null ;
		JSONArray arr = new JSONArray();
		String sql = null;
		try{
			conn = DbUtil.getConnection( cfg );
			if(conn==null){
				throw new Exception( "无法获取到数据库链接"+JSONObject.fromObject( cfg ).toString() );
			}
			if(cfg.getDriver().indexOf("oracle")!=-1){
				sql = "select * from st_sendwait_e where 1=1 ";
			}else if(cfg.getDriver().indexOf("jtds")!=-1){
				sql = " select * from st_sendwait_e where 1=1 ";
			}else{
				throw new Exception( "驱动无法识别" );
			}
			
			sql+=" and stcd in( select stcd from st_stbprp_b where addvcd like '3415%' ) and slsh>156785302";
			ResultSet rs = conn
					.createStatement().executeQuery( sql );
			while(rs.next()){
				arr.add( getStSendWaitEJson(rs) );
				break;
			}
			rs.close();
			System.out.println(HYITSTriggerRecordSendDao.class.getResource("/").getPath() );
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			if(conn!=null)
				conn.close();
			System.out.println( "查询库中待同步雨水情数据,SQL:\t"+sql+",\t得到"+arr.size()+"条数据." );
		}
		*/
	}
	
	/**
	 * @author lidong 2015年1月29日
	 * @param key 同步任务的标识
	 * @param DEPJobConfig cfg 整个任务的config，前期用jarPath获取文件路径，后续用数据库处理
	 * <pre>
	 * 获取上次这个任务同步到了哪个slsh记录
	 * </pre>
	 */
	public String getLastOnceSLSH(DEPJobConfig cfg) throws Exception{
		//TODO 简单用文件处理，后面要改成数据库方式
		//使用jar包的路径作为存储文件的路径
		String key = cfg.getId().toString()+"_"+cfg.getServerId();
		String path = cfg.getJarPath();
		logger.debug("*********************************"+(path.substring(0,path.lastIndexOf("/")+1)));
		logger.debug("*********************************"+(cfg.getJarPath()));
		path = path.replace(".jar", "_"+key+".rec");
		logger.debug( "获取slsh的路径"+path );
		File f = new File(path);
		if(f==null||!f.exists()){
			//没有文件，创建，返回0
			f.createNewFile();
			return "0";
		} else {
			FileReader fr = new FileReader(f);
			BufferedReader br = null;
			String result = "0";
			try{
				br = new BufferedReader(fr);
				result = br.readLine();
				if(result==null||result.trim().equals("")){
					result = "0";
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
				e.printStackTrace();
			}finally{
				if(br!=null)
					br.close();
				return result;
			}
		}
	}
	
	/**
	 * @author lidong 2015年1月29日
	 * @param key	同步任务的标识
	 * @param slsh	当前需要保存的slsh号
	 * @param DEPJobConfig cfg 整个任务的config，前期用jarPath获取文件路径，后续用数据库处理
	 * <pre>
	 * 保存本地同步到的slsh
	 * </pre>
	 */
	public void saveThisOnceSLSH(String slsh, DEPJobConfig cfg) throws Exception {
		// TODO 简单用文件处理，后面要改成数据库方式
		// 使用jar包的路径作为存储文件的路径
		String key = cfg.getId().toString()+"_"+cfg.getServerId();
		String path = cfg.getJarPath();
		path = path.replace(".jar", "_"+key+".rec");
		File f = new File(path);
		logger.debug( "保存slsh的路径"+path );
		if (f == null || !f.exists()) {
			f.createNewFile();
		}
		FileWriter fr = new FileWriter(f);
		BufferedWriter br = null;
		String result = "0";
		try {
			br = new BufferedWriter(fr);
			br.write( slsh );
			br.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} finally {
			if (br != null)
				br.close();
		}
	}
	public void deleteData(String whereSql,DbConfig cfg,String recordTable)  throws Exception{
		Connection conn = null ;
		String sql = null;
		try{
			conn = DbUtil.getConnection( cfg );
			if(conn==null){
				throw new Exception( "无法获取到数据库链接"+JSONObject.toJSONString( cfg ).toString() );
			}
			sql = "delete from "+recordTable+" where 1=1 " ;
			sql+= " "+whereSql;
			//执行sql
			boolean result = DbUtil.execute(sql, conn);
			logger.debug( "删除数据完成,所执行的sql{}",sql );
		}catch(Exception e){
			logger.error( "执行sql出错{}",sql );
			logger.error( e.getMessage(),e );
			
		}finally{
			if(conn!=null)
				conn.close();
		}
	}
	public void deleteData(JSONArray arr,DbConfig cfg,String recordTable)  throws Exception{
		Connection conn = null ;
		String sql = null;
		try{
			conn = DbUtil.getConnection( cfg );
			if(conn==null){
				throw new Exception( "无法获取到数据库链接"+JSONObject.toJSONString( cfg ).toString() );
			}
			sql = "delete from "+recordTable+" where slsh=?" ;
			PreparedStatement pp = conn.prepareStatement( sql );
			for(int i=0;i<arr.size();i++){
				pp.setInt(1, arr.getJSONObject(i).getInteger( "SLSH" ));
				pp.addBatch();
				if(i%50==0){
					pp.executeBatch();
				}
			}
			if(arr.size()%50!=0){
				pp.executeBatch();
			}
			pp.close();
			logger.debug( "删除数据完成" );
		}catch(Exception e){
			logger.error( "执行sql出错{}",sql );
			logger.error( e.getMessage(),e );
			
		}finally{
			if(conn!=null)
				conn.close();
		}
	}
	/**
	  * <pre>
	  *    为了从源数据库中读取数据时,判断异常值,单独提取一个方法
	  * </pre>
	  * @Title: getSumPptnSource
	  * @param rs	从源数据库查询出的结果集
	  * @param obj	存储数据结果的对象
	  * @param hourMaxRain	小时的最大雨量,如果不为空的话就需要进行对别,超出的数据忽略
	  * @author lidong
	  * @date 2017年5月16日 下午1:16:42
	  */
	public static void getSumPptnSource(ResultSet rs,JSONObject obj,Double hourMaxRain) throws SQLException{
		Object temp = rs.getObject( "drp" );
		Double drp = (temp==null||"null".equals( temp.toString() ))
				?0:StringUtil.ConversionFromString( temp.toString() );
		if(hourMaxRain!=null){
			if(hourMaxRain.doubleValue()<drp.doubleValue()){
				//设置的小时最大雨量小于了当前的计算雨量,不上报
				logger.error( rs.getString( "stcd" )+rs.getString("tm")+" 时间上报的数据"+
						rs.getObject( "drp" )+"超过设置的小时最大雨量值"+String.valueOf( hourMaxRain )+",不进行上报同步");
				return;
			}
		}
		String stcd = rs.getString( "stcd" );
		String tm = rs.getString( "tm" );
		obj.put( stcd+","+tm , drp);
	}
	
	public static void getSumPptn( ResultSet rs,JSONObject obj) throws SQLException{
		String stcd = rs.getString( "stcd" );
		String tm = rs.getString( "tm" );
		Object temp = rs.getObject( "drp" );
		Double drp = (temp==null||"null".equals( temp.toString() ))
				?0:StringUtil.ConversionFromString( temp.toString() );
		obj.put( stcd+","+tm , drp);
	}
	/**
	  * <pre>
	  *    获取短历时的,来源数据,已经计算成小时的累计数了
	  * </pre>
	  * @Title: getSourceData
	  * @param d1
	  * @param d2
	  * @param cfg
	  * @return
	  * @author lidong
	  * @date 2016年11月8日 上午11:46:52
	  */
	public JSONObject getSourceData(String bg,String end,PptnSumDrpSyncConfig cfg,StringBuilder sb2){
		/*select stcd,tm2 tm,SUM(drp) drp from  (
		select STCD,TM,convert(varchar(13),DATEADD(ss,3599,TM),21)+':00' tm2,drp from ST_PPTN_R where TM>'2016-06-08 08:00' 
			and TM<='2016-06-10 08:00' 
			) tt group by stcd,tm2 order by stcd,tm2*/
		JSONObject obj = new JSONObject();
		try{
			StringBuilder sql = new StringBuilder();
			sql.append( "select stcd,tm2 tm,SUM(drp) drp from  (")
				.append("select STCD,TM,convert(varchar(13),DATEADD(ss,3599,TM),21)+':00' tm2,drp from ST_PPTN_R where TM>'")
				.append(bg).append("' and tm<='").append( end )
				.append("' ");
			String filter = cfg.getSourceFilter();
			if( filter!=null&&!"".equals( filter.trim())){
				sql.append( filter ); 
			}
			sql.append(" ) tt group by stcd,tm2 order by stcd,tm2");
			DbConfig dbCfg = new DbConfig( cfg.getSourceDbConfig() );
			obj = this.getPpptnSumDrpDatas(sql.toString(),dbCfg, sb2 ,true,cfg.getHourMaxRain());
			logger.debug( obj.toString() );
		}catch(Exception e){
			logger.error( e.getMessage(),e );
		}finally{
			return obj;
		}
	}
	/**
	  * <pre>
	  *    获取目标库的数据,原本就应该是小时的累计数
	  * </pre>
	  * @Title: getTargetData
	  * @param d1
	  * @param d2
	  * @param cfg
	  * @return
	  * @author lidong
	  * @date 2016年11月8日 上午11:46:54
	  */
	public JSONObject getTargetData(String bg,String end,PptnSumDrpSyncConfig cfg,StringBuilder sb2){
		/*select *,DATEPART(mi,tm) from ST_PPTN_R where TM>'2016-06-08 08:00'  and TM<='2016-06-10 08:00' 
					and (  DATEPART(mi,tm)=0 and DATEPART(ss,tm)=0 and DATEPART(ms,tm)=0)*/

		JSONObject obj = new JSONObject();
		try{
			StringBuilder sql = new StringBuilder();
			sql.append( "select stcd,CONVERT(varchar(16), tm, 21) tm,drp  from ST_PPTN_R where tm>'")
				.append(bg).append("' and tm<='").append( end )
				.append("' and (  DATEPART(mi,tm)=0 and DATEPART(ss,tm)=0 and DATEPART(ms,tm)=0  ) ");
			String filter = cfg.getTargetFilter();
			if( filter!=null&&!"".equals( filter.trim())){
				sql.append( filter ); 
			}
			
			DbConfig dbCfg = new DbConfig( cfg.getTargetDbConfig() );
			obj = this.getPpptnSumDrpDatas(sql.toString(),dbCfg, sb2,false ,null);
			logger.debug( obj.toString() );
		}catch(Exception e){
			logger.error( e.getMessage(),e );
		}finally{
			return obj;
		}
	}
	
	/**
	  * <pre>
	  *    查询降雨量的数据,暂时只针对sqlserver
	  * </pre>
	  * @Title: getDatas
	  * @param whereSql
	  * @param cfg
	  * @param recordTable
	  * @param rowsOnce
	  * @param sb
	  * @return
	  * @throws Exception
	  * @author lidong
	  * @date 2016年11月8日 下午12:25:27
	  */
	public JSONObject getPpptnSumDrpDatas(String sql,DbConfig cfg,StringBuilder sb,boolean isSource,Double hourMaxRain) throws Exception{
		Connection conn = null ;
		JSONObject obj = new JSONObject();
		try{
			conn = DbUtil.getConnection( cfg );
			//暂时值
			if(conn==null){
				throw new Exception( "无法获取到数据库链接"+JSONObject.toJSONString( cfg ).toString() );
			}
			
			ResultSet rs = conn
					.createStatement().executeQuery( sql );
			//如果设定了是源库,并且在hourMaxRain起作用的情况下,判断hourMaxRain和drp的值,进行处理
			if(isSource&&hourMaxRain!=null&&hourMaxRain.doubleValue()>0){
				logger.debug( "将进入对源数据的小时雨量过滤处理,小时雨量<="+hourMaxRain );
				while(rs.next()){
					 this.getSumPptnSource(rs,obj ,hourMaxRain) ;
				}
			}else{
				while(rs.next()){
					 this.getSumPptn(rs,obj) ;
				}
			}
			rs.close();
		}catch(Exception e){
			String msg = "执行sql出错:"+e.getMessage()+"\r\n"+sql;
			sb.append("\r\n").append( msg );
			logger.error( msg,e );
		}finally{
			if(conn!=null)
				conn.close();
			String msg =  "查询库中待同步雨水情数据,SQL:\t"+sql+",\t得到"+obj.keySet().size()+"条数据.";
			logger.debug( msg );
			sb.append("\r\n").append( msg );
			return obj;
		}
	}
	
	private void doExeSql(JSONObject obj,String sql,Connection conn,StringBuilder sb) throws Exception{
		Statement st = conn.createStatement();
		int i = 0;
		String tmp = null;
		String newsql = null;
		for(Iterator<String> it = obj.keySet().iterator();it.hasNext();){
			tmp = it.next();
			String[] s = tmp.split(",");
			try{
				newsql = sql;
				newsql = newsql.replaceAll(":drp", obj.getString( tmp ));
				newsql = newsql.replaceAll(":stcd", s[0]);
				newsql = newsql.replaceAll(":tm", s[1]);
				st.executeUpdate( newsql );
			}catch(Exception e){
				String msg = e.getMessage()+"Data :"+s[0]+"\t"+s[1]+"\t"+obj.getDouble( tmp );
				logger.error( msg,e );
				sb.append( "\r\n" ).append( msg).append("\t\t").append( newsql );
			}
		}
		st.close();
	}
	
	public void doUpdateTarget(JSONObject obj,PptnSumDrpSyncConfig cfg,StringBuilder sb) throws Exception{
		Connection conn = null ;
		try{
			DbConfig dbCfg = new DbConfig( cfg.getTargetDbConfig() );
			conn = DbUtil.getConnection( dbCfg );
			//暂时值
			if(conn==null){
				throw new Exception( "无法获取到数据库链接"+JSONObject.toJSONString( cfg ).toString() );
			}
			String tmp = null;
			if( obj.getJSONObject("update").keySet().size()>0){
				this.doExeSql( obj.getJSONObject( "update" ) , "update st_pptn_r set drp=:drp where stcd=':stcd' and tm=':tm'", conn, sb);
			}else{
				logger.debug( "需要更新的数据数量为0" );
			}
			if( obj.getJSONObject("insert").keySet().size()>0){
				//		insert into ST_PPTN_R(STCD,TM,DRP,INTV) values('123','2016-10-11 11:00',10,1)
				this.doExeSql( obj.getJSONObject( "insert" ) , "insert into ST_PPTN_R(DRP,STCD,TM,INTV) values(:drp,':stcd',':tm',1)", conn, sb);
			}else{
				logger.debug( "需要新增的数据数量为0" );
			}
			if( obj.getJSONObject("delete").keySet().size()>0){
				this.doExeSql( obj.getJSONObject( "delete" ) , "delete from st_pptn_r where  drp=:drp and stcd=':stcd' and tm=':tm'  ", conn, sb);
			}else{
				logger.debug( "需要删除的数据数量为0" );
			}
			
		}catch(Exception e){
			String msg = "执行sql出错:"+e.getMessage()+"\r\n";
			sb.append("\r\n").append( msg );
			logger.error( msg,e );
		}finally{
			if(conn!=null)
				conn.close();
			String msg =  "完成数据更新操作update:"+obj.getJSONObject("update").keySet().size()
							+",insert:"+obj.getJSONObject("insert").keySet().size()
							+",delete:"+obj.getJSONObject("delete").keySet().size();
			logger.debug( msg );
			sb.append("\r\n").append( msg );
		}
	}
	
	
}
