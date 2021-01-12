package kde.jobcontainer.job.synchydro.service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.job.synchydro.dao.HYITSTriggerRecordSyncDao;
import kde.jobcontainer.job.synchydro.domain.HYITSTriggerRecordSyncConfig;
import kde.jobcontainer.job.synchydro.domain.PptnSumDrpSyncConfig;
import kde.jobcontainer.job.synchydro.domain.TriggerRecord;
import kde.jobcontainer.util.domain.DEPJobConfig;
import kde.jobcontainer.util.domain.DbConfig;
import kde.jobcontainer.util.utils.DateUtil;
import kde.jobcontainer.util.utils.db.DbUtil;
import kde.jobcontainer.util.utils.mq.MessageQueueUtils;

/**
 * @author lidong 2015年1月29日
 * <pre>
 * 
 * </pre>
 */
/**
 * <pre>
 *    
 * </pre>
 * @author lidong
 * @date 2016年11月7日 下午4:34:16
 */
public class HYITSTriggerRecordSyncService {
	private Logger logger = LoggerFactory
			.getLogger(HYITSTriggerRecordSyncService.class);
	private final String INSERT = "I";
	private final String DELETE	= "D";
	private final String UPDATE	= "U";
	private Map<String,Date> nextTmMap = new HashMap<String,Date>();//用来存储所有任务的下次执行时间
	/**
	 *  由于是单例模式,所以本身不能持有状态及其他配置项信息,不然无法同时运行两个
	 **/
	private static HYITSTriggerRecordSyncService _instance;
	
	private HYITSTriggerRecordSyncDao gwtpDao;
	
	public static HYITSTriggerRecordSyncService getInstance(){	//单例方法
		if(_instance==null)
			_instance = new HYITSTriggerRecordSyncService();
		return _instance; 
	}
	
	
	public static void main(String[] args) throws Exception{
		DbConfig dbCfg = new DbConfig();
		dbCfg.setUrl( "jdbc:jtds:sqlserver://10.22.2.151:1943;databaseName=JiLinRTDB" );
		dbCfg.setDriver( "net.sourceforge.jtds.jdbc.Driver" );
		dbCfg.setUsername( "sa" );
		dbCfg.setUserpwd( "jlfb!@3$" );
		String whereStr = " and tabid='ST_PPTN_R' and operation='D' and stcd in( select stcd from st_stbprp_b where addvcd like '2201%' ) and tm>'2017-07-19' and excinf like '%,DYP%'";
		StringBuilder sb = new StringBuilder();
		JSONArray arr = getInstance().getGwtpDao().getDatas( whereStr , dbCfg ,"ST_SENDDO_E",50000,sb,"测试");
		TriggerRecord triReg = null;
		JSONObject json = null;
		PptnSumDrpSyncConfig cfg = new PptnSumDrpSyncConfig();
		List<String> sqls = new ArrayList<String>();
		String tmpSql = null;
		for(int i=0;i<arr.size();i++){
			json = arr.getJSONObject(i);
			triReg = (TriggerRecord)JSONObject.toJavaObject( json,TriggerRecord.class );
			triReg.setOPERATION("I");
			tmpSql = triReg.getSql( "STCD,TM", "sqlserver" );
			sqls.add( tmpSql );
		}
			
		Connection connection = null;        
		Statement st = null;
		System.out.println("开始发送数据到目标数据库" + dbCfg.getUrl() );
		try {
			connection = DbUtil.getConnection( dbCfg );
			st = connection.createStatement();
			//测试或处理数据用的getInstance().dealEverySql( st , sqls,depConfig);
			
			
		}catch(Exception e){
			e.printStackTrace( );
		}finally{
			if(connection!=null)
				connection.close();
		}
		
		
		System.out.println( arr.size() );
	}
	
	/**
	 * @author lidong 2015年1月29日
	 * @param depConfig
	 * <pre>
	 * 抽取数据发送的服务方法
	 * </pre>
	 */
	public void doJob(DEPJobConfig depConfig){
		try{
			
			StringBuilder sb = new StringBuilder();
			sb.append( this.getLocalIP() ).append("\r\n");
			sb.append( depConfig.getJobClassName() ).append("\t\t");
			sb.append( depConfig.getName() ).append("\r\nSourceDb");
			
			//这个任务上次同步到的数据行
			String lastSlsh = this.getGwtpDao().getLastOnceSLSH(depConfig);
			//获取任务配置类
			HYITSTriggerRecordSyncConfig cfg = (HYITSTriggerRecordSyncConfig)
					JSONObject.toJavaObject( depConfig.getJobConfigJson(), HYITSTriggerRecordSyncConfig.class);
			
			sb.append( cfg.getSourceDbConfig().getString( "url" ) ).append("\r\nTargetDb");
			sb.append( cfg.getTargetDbConfig().getString("url") ).append("\r\n上次同步到记录slsh:")
					.append( lastSlsh );
			//同步数据的过滤条件
			String whereStr = cfg.getSendwaitQueryWhere()+" and slsh>"+lastSlsh;
			//和新加的主配置,合并一下
			if(cfg.getMainQuery()!=null&&!"".equals( cfg.getMainQuery().trim() )){
				whereStr+=cfg.getMainQuery();
			}
			//数据库链接
			DbConfig dbCfg = new DbConfig( cfg.getSourceDbConfig() );
			//查询数据库获取数据
			JSONArray arr = this.getGwtpDao().getDatas( whereStr , dbCfg ,cfg.getRecordTblName(),this.getRowsOnce( cfg.getRowsOnce() ),sb,depConfig.getName());
			//如果非空,则需要发送数据
			//记录最后一个slsh
			String newLashSlsh = null; 
			if(arr!=null&&arr.size()>0){

				//同步数据,不能保证所有数据真的都能正常入库,但由于有个流水的关系，又不能影响到入库操作的执行
				this.syncRecord( arr , depConfig );
				//记录当前发送到的slsh
				newLashSlsh = arr.getJSONObject( arr.size()-1 ).get("SLSH").toString();
			}
			sb.append("\r\n本次同步到的记录slsh:").append( newLashSlsh );
			//更新最后同步的slsh
			if(newLashSlsh!=null &&!newLashSlsh.equals(lastSlsh)){
				this.getGwtpDao().saveThisOnceSLSH(newLashSlsh, depConfig);
			}
			//20161107 lidong 增加配置,按配置中的内容判断是否将数据导成文件处理
			//参数越写越垃圾
			this.saveFileAndDelete(cfg,(newLashSlsh==null?lastSlsh:newLashSlsh)
						,dbCfg,depConfig,sb );
			
			
			//发送执行的消息到平台
			if(cfg.getMqServerAddr()!=null
					&&!"".equals(cfg.getMqServerAddr().trim())
					&&cfg.getMqServerAddr().trim().indexOf( "tcp:" )!=-1){
				MessageQueueUtils mqu = new MessageQueueUtils();
				mqu.sendMessage( sb.toString() , cfg.getMqServerAddr(), "syncinfo");
				logger.info("完成任务运行相关信息的发送:"+depConfig.getName()+"\t"+depConfig.getServerId());
			}else{
				logger.info("未配置处理信息发送的队列服务目标,跳过:"+depConfig.getName()+"\t"+depConfig.getServerId());
			}
		}catch(Exception e){
			//20150207 lidong 对于数据和异常处理的问题
			//获取数据时发生异常？不会进行记录
			//
			e.printStackTrace();
			logger.error( "异常任务:"+depConfig.getName()+"\t"+depConfig.getServerId()+"\r\n"+e.getMessage(),e );
		}finally{
			logger.info("执行完成:"+depConfig.getName()+"\t"+depConfig.getServerId());
		}
	}
	public String getLocalIP(){
		try{
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress().toString();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			return "无法获取本机ip";
		}
	}
	/**
	  * <pre>
	  *    获取一次同步的数据量
	  * </pre>
	  * @Title: getRowsOnce
	  * @param r
	  * @return
	  * @author lidong
	  * @date 2016年11月7日 下午4:34:20
	  */
	public int getRowsOnce(String r){
		int x = 0;
		try{
			x = Integer.parseInt( r );
		}catch(Exception e){
			logger.error( "将配置中的单次同步数量转换为整型时出错:"+r,e );
			x = 3000;	//异常了就用默认值
		}finally{
			return x;
		}
	}
	/**
	 * Description: 
	 *  20210107 淡出抽出一个方法，其他类继承的情况下容易调整sql生成的方法
	 * @author lidong
	 * @param triReg
	 * @param cfg
	 * @param dbCfg
	 * @return
	 * @throws Exception 
	 */ 
	public String getSqlByTriggerRec(TriggerRecord triReg,HYITSTriggerRecordSyncConfig cfg,DbConfig dbCfg) throws Exception {
		return triReg.getSql( cfg.getTblAndPks().getString( triReg.getTABID() ),this.getDbType(dbCfg));
	}
	
	
	/**
	 * @author lidong 2015年5月26日
	 * @param arr	待同步数据操作记录
	 * @param cfg	任务配置
	 * @throws Exception
	 * <pre>
	 * 对数据进行同步操作,从send的记录表中获取需要同步操作的数据
	 * 根据cfg中的配置sourceDbConfig读取记录表,项目表targetDbConfig中的数据进行操作
	 * </pre>
	 */
	public void syncRecord(JSONArray arr , DEPJobConfig depConfig) throws Exception {
		if(arr==null|| arr.size()==0){
			logger.error("syncRecord中传入的待同步数据为空");
			return;
		}
		HYITSTriggerRecordSyncConfig cfg = (HYITSTriggerRecordSyncConfig)
				JSONObject.toJavaObject( depConfig.getJobConfigJson(), HYITSTriggerRecordSyncConfig.class);
		//获取目标数据库链接信息
		DbConfig dbCfg = new DbConfig( cfg.getTargetDbConfig() );
		List<String> sqlList = null;
		Connection connection = null;        
		Statement st = null;
		logger.debug(depConfig.getJobSystemName()+"\t开始发送数据到目标数据库" + dbCfg.getUrl() );
		try {
			
			connection = DbUtil.getConnection( dbCfg );
			logger.debug( depConfig.getJobSystemName()+"\t获取数据库连接" );
			if(connection==null)
				throw new Exception(depConfig.getJobSystemName()+"\t数据库无法连接"+dbCfg.getUrl());
			logger.debug( depConfig.getJobSystemName()+"\t数据库链接不为null" );
			st = connection.createStatement();
			logger.debug( depConfig.getJobSystemName()+"\t创建statement" );
			JSONObject json = null;
			TriggerRecord triReg = null;
			sqlList = new ArrayList<String>();
			String tmpSql = null;
 			for(int i=0;i<arr.size();i++){
				json = arr.getJSONObject(i);
				triReg = (TriggerRecord)JSONObject.toJavaObject( json,TriggerRecord.class );
				//20210107 lidong 这里调整下，单独抽出来一个方法，这样在其他继承类中方便处理
				//tmpSql = triReg.getSql( cfg.getTblAndPks().getString( triReg.getTABID() ),this.getDbType(dbCfg));
				tmpSql = this.getSqlByTriggerRec(triReg, cfg, dbCfg);
				
				//将数据暂时存放到一个list中
				if(tmpSql!=null){	//20150608 lidong 松辽的update数据有问题，有可能会是update set 为空，然后加where 条件报错，后来处理这种情况返回null
					sqlList.add( tmpSql );
					st.addBatch( tmpSql );
				}
			}
 			logger.debug( depConfig.getJobSystemName()+"\t向st中添加了批量处理的sql" );
 			if(sqlList.size()>0){
 				logger.debug( depConfig.getJobSystemName()+"\tsqlList.size:"+sqlList.size() );
	 			int[] result = st.executeBatch();
	 			logger.debug( depConfig.getJobSystemName()+"\t执行完批量的sql执行,准备检查每一条的执行结果" );
	 			for(int i=0;i<result.length;i++){
	 				if(result[i]==0){
	 					logger.error(depConfig.getJobSystemName()+"\t操作影响为0.\t\t\t"+sqlList.get(i) );
	 				}
	 			}
	 			logger.debug(depConfig.getJobSystemName()+"\t执行和检查执行完毕" );
 			}else{
 				logger.debug( depConfig.getJobSystemName()+"\tsql语句数量为0,不需要执行" );
 				logger.info( depConfig.getJobSystemName()+"\t暂无需要同步的数据操作" );
 			}
 			st.close();
 			logger.debug( depConfig.getJobSystemName()+"\t已关闭了statement" );
		}catch(Exception e){
			e.printStackTrace();
			/*
			if(sqlList!=null){
				for(String s : sqlList){
					logger.debug( s );
				}
			}
			*/
			logger.error( depConfig.getJobSystemName()+"\t异常任务:"+depConfig.getName()+"\t"+depConfig.getServerId()+"\r\n"+e.getMessage(),e );
			
			//20150609 lidong 不抛出异常了,逐条处理sql
			//throw e;
			if(st!=null){
				logger.error( depConfig.getJobSystemName()+"\t批量执行sql出错，逐条执行语句！");
				this.dealEverySql( st , sqlList,depConfig);
			}else
				logger.error( depConfig.getJobSystemName()+"\tstatement为null,无法逐条执行语句" );
		}finally {
			logger.debug( depConfig.getJobSystemName()+"\t准备关闭connection"+connection );
			if (null != connection)                    
				connection.close();          
			logger.debug( depConfig.getJobSystemName()+"\t已关闭" );
		}
	}
	/**
	 * @author lidong 2015年6月9日
	 * @param st
	 * @param list
	 * <pre>
	 * 对于批处理的数据,逐条处理执行操作
	 * </pre>
	 */
	private void dealEverySql(Statement st,List<String> list,DEPJobConfig depConfig){
		if(list!=null&&list.size()>0){
			try{
				st.clearBatch();
			}catch(Exception e){
				logger.error( depConfig.getJobSystemName()+"\t批量执行语句出现异常后,clearBatch出错" );
				e.printStackTrace();
			}
			for(String s:list){
				try{
					logger.debug( s );
					boolean x = st.execute( s );
					logger.debug( depConfig.getJobSystemName()+"\t"+x+"\t"+s );
				}catch(Exception e){
					e.printStackTrace();
					logger.error( depConfig.getJobSystemName()+"\t逐条语句执行错误,忽略后继续向下执行:" + s  );
				}
			}
		}
	}
	
	/**
	  * <pre>
	  *    新增功能,将历史的数据记录导出为文件,之后删除
	  * </pre>
	  * @Title: saveFileAndDelete	
	  * @param config	本任务的自定义配置
	  * @param newSlsh	需要进行判断的st_sendwait_e表的主键数据,在此之前的数据才会被删掉
	  * @param dbcfg	数据库连接配置
	  * @param depCfg	任务整体配置
	  * @author lidong
	  * @date 2016年11月7日 上午11:50:06
	  */
	private void saveFileAndDelete(HYITSTriggerRecordSyncConfig config,String newSlsh,
			DbConfig dbcfg,DEPJobConfig depCfg ,StringBuilder sb){
		//处理保存\删除数据的执行时间
		logger.debug("{}:开始准备删除数据并保存文件记录",depCfg.getName());
		String key=depCfg.getId()+"_"+depCfg.getServerId()+"_"+depCfg.getName();
		Date nextRunDelTm = nextTmMap.get( key );
		if(nextRunDelTm==null){
			nextRunDelTm = new Date();
			nextTmMap.put( key , nextRunDelTm);
		}
		
		
		//先判断是否在配置中处理删除保存文件的参数,如果没有的话则直接离开
		String saveAndDeleteQry = config.getSaveAndDeleteQry();
		if( saveAndDeleteQry!=null
				&&!saveAndDeleteQry.trim().equals("")){
			//配置不为空,应该可以执行,为了限制执行的次数,检查当前的时间,是不是比类中持有的事件晚
			Date now = new Date();
			
			if(now.after( nextRunDelTm )){
				//配置了语句,并且当前要执行
				try{
					//查询相关的数据
					//1、处理查询语句
					if( saveAndDeleteQry.indexOf( ":slsh" )!=-1 ){
						//暂时不处理slsh配置有问题的情况,如下面这句
						//if(saveAndDeleteQry.indexOf( "slsh>" )!=-1|| saveAndDeleteQry.indexOf( "slsh=" ) )
						if(newSlsh==null||"".equals( newSlsh.trim() )||"0".equals( newSlsh.trim() )){
							//如果运行时获取到的最新的slsh有问题,不能设置默认值,要把这个条件去掉,并记录
							logger.warn( "当前接收到的newSlsh为空,无法使用,去掉这一条件,换为1=1" );
							saveAndDeleteQry = saveAndDeleteQry.replaceAll( "slsh<:slsh" , "1=1");
							//此时由于语句删除是<{slsh},很有可能一点都不能删除
						}else
							saveAndDeleteQry = saveAndDeleteQry.replaceAll(":slsh", newSlsh);
					}
					//处理日期参数
					if( saveAndDeleteQry.indexOf( ":moditime" )!=-1 ){
						int daysAgo = config.getDelDaysAgo();//有默认的50天
						Date d = DateUtil.add(new Date(),Calendar.DATE, -daysAgo);//当前时间前推
						saveAndDeleteQry = saveAndDeleteQry.replaceAll( ":moditime" , "'"+DateUtil.DateTimeToString(d, DateUtil.mm)+"'");
					}
					if(config.getMainQuery()!=null&&!"".equals( config.getMainQuery().trim() )){
						saveAndDeleteQry+=config.getMainQuery();
					}
					//查询数据库获取数据,rowsOnce传0,取出符合条件的所有数据:where{}",saveAndDeleteQry);
					JSONArray arr = this.getGwtpDao().getDatas(saveAndDeleteQry, dbcfg,
							config.getRecordTblName(), config.getDelRowCount() ,sb,depCfg.getName());
					if(arr!=null&&arr.size()>0){
						//有数据,保存为zip文件
						this.saveSendDataToFile(arr, depCfg);
						//保存后,删除相关的数据
						//执行
						this.getGwtpDao().deleteData(saveAndDeleteQry, dbcfg, config.getRecordTblName());
						sb.append("\r\n完成").append(arr.size()).append("条记录的存储和删除,到SLSH")
								.append( arr.getJSONObject(arr.size()-1).getString("SLSH") );
					}else{
						String msg = "未能查询到要保存为文件并且删除的数据。";
						logger.debug( msg );
						sb.append("\r\n").append( msg );
					}
					//不管有没有数据,都是下次再执行,除非是异常了
					//设置netRunDelTm的值,到第二天,每次数据应该都不会特别多,设成一天的数据就好
					nextRunDelTm = DateUtil.add( now , Calendar.MINUTE, config.getDelIntervalMins() );
					nextTmMap.put( key , nextRunDelTm);
					
					
					//如果前面的异常了,设置时间的会不成过,下次还要再执行
					String  msg="{}:设置下次删除数据的时间不早于"+DateUtil.DateTimeToString( nextRunDelTm, "MM-dd HH:mm");
					sb.append("\r\n").append( msg );
					logger.debug( msg,depCfg.getName() );
				}catch(Exception e){
					logger.error(depCfg.getName()+e.getMessage(), e);
					sb.append( "\r\n" ).append("保存删除出错").append( e.getMessage() );
				}finally{
					//做些剩余的工作
					
				}
			}else{
				String msg = "{}:配置了delete参数,目前未到执行时间"+DateUtil.DateTimeToString(nextRunDelTm, "MM-dd HH:mm");
				logger.info( msg,depCfg.getName() );
				sb.append( "\r\n" ).append( msg );
			}
		}else{
			String msg = "{}:未配置saveAndDeleteQry或配置为空,程序不进行历史数据的导出保存和删除";
			logger.debug( msg,depCfg.getName() );
			sb.append( "\r\n" ).append( msg );
		}
	}
	
	/**
	  * <pre>
	  *    压缩文本
	  * </pre>
	  * @Title: gzip
	  * @param primStr
	  * @return
	  * @author lidong
	  * @date 2016年11月7日 下午10:04:45
	  */
	public  String gzip(String primStr) {
		if (primStr == null || primStr.length() == 0) {
			return primStr;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip=null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(primStr.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(gzip!=null){
				try {
					gzip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//20180707 lidong 替换掉了jdk中的方法，不确定执行情况
		return com.alibaba.druid.util.Base64.byteArrayToBase64(out.toByteArray());
	}
	
	/**
	  * <pre>
	  *    将符合saveAndDeleteQry查询条件的数据表的内容保存下来,后面删除
	  * </pre>
	  * @Title: saveSendDataToFile
	  * @param arr
	  * @author lidong
	  * @date 2016年11月7日 下午4:51:43
	  */
	private void saveSendDataToFile(JSONArray arr,DEPJobConfig depCfg){
		String key = depCfg.getId().toString()+"_"+depCfg.getServerId();
		String path = depCfg.getJarPath();
		if(arr==null||arr.size()==0){
			logger.warn( "{}:需要保存的jsonArray为空",depCfg.getName() );
			return;
		}
		//获取文件名
		JSONObject last = arr.getJSONObject( arr.size()-1 );
		if(last==null||last.get("SLSH")==null){
			logger.warn( "{}:arr中最后数据为空或SLSH为空",depCfg.getName() );
			return;
		}
		String name = last.get("SLSH").toString();
		//转为文本
		String str = arr.toString();
		//获取压缩后的文本
		//str = this.gzip( str );
		//将文本保存为文件,先找文件名
		File jarFile = new File(path);
		if(jarFile.exists()){
			//一般肯定有,不然无法执行
			try{
				File dataDic = new File(jarFile.getParentFile().getCanonicalPath()+"/data");
				if(!dataDic.exists())
					dataDic.mkdir();
				File f = new File( dataDic.getCanonicalPath()+"/"+jarFile.getName().replaceAll(".jar", "_"+key+"_"+name+".data") );
				f = this.writeStringToFile( f , str);
				//压缩文件
				if(f!=null){
					this.toZip( f );
				}
			}catch(Exception e){
				logger.error( e.getMessage(),e );
			}
		}else{
			logger.error( "{}:找不到对应的jar文件路径,来生成数据文件",depCfg.getName() );
		}

	}
	
	protected String getDbType(DbConfig cfg ) throws Exception {
		//直接限制单次10000调
		if(cfg.getDriver().indexOf("oracle")!=-1){
			return "oracle";
		}else if(cfg.getDriver().indexOf("jtds")!=-1){
			return "sqlserver";
		}else if(cfg.getDriver().indexOf("mysql")!=-1){
            return "mysql";
        }else{
			throw new Exception( "驱动无法识别" );
		}
	}
	
	public HYITSTriggerRecordSyncDao getGwtpDao() {
		if(this.gwtpDao==null)
			this.gwtpDao = HYITSTriggerRecordSyncDao.getInstance();
		
		return this.gwtpDao;
	}

	public void setGwtpDao(HYITSTriggerRecordSyncDao gwtpDao) {
		this.gwtpDao = gwtpDao;
	}
	/**
	  * <pre>
	  *    压缩成文件
	  * </pre>
	  * @Title: toZip
	  * @param f
	  * @author lidong
	  * @date 2016年11月8日 上午12:19:21
	  */
	public void toZip(File f){
		if(f==null||!f.exists()){
			logger.error("要打包压缩的文件传入为空或不存在");
			return;
		}
		byte[] buf = new byte[1024];
		File zipFile = null;
		ZipOutputStream out = null;
		FileInputStream in = null;
		try{
			zipFile = new File(f.getCanonicalPath()+".zip");
			out = new ZipOutputStream(new FileOutputStream(
					zipFile));
			in = new FileInputStream( f );
            out.putNextEntry(new ZipEntry(f.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            logger.debug("文件压缩完成{}",zipFile.getCanonicalPath());
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(out!=null)
					out.close();
				if(in!=null)
					in.close();
				f.delete();
			}catch(Exception ee){
				logger.error("关闭压缩zip文件io时异常",ee);
			}
		}
	}
	/**
	  * <pre>
	  *    新加进来写文件的东西
	  * </pre>
	  * @Title: writeStringToFile
	  * @param file
	  * @param str
	  * @author lidong
	  * @date 2016年11月7日 下午5:13:12
	  */
	public File writeStringToFile(File file, String str) {
		BufferedWriter writer = null;
		try{
			if(!file.exists()){
				file.createNewFile();
			}else{
				//构造一个新文件,继续往下
				String oldFilePath = file.getCanonicalPath();
				File newFile = new File( oldFilePath+"_1" );
				logger.error( "发现文件重复了,重命名再执行一遍,{}" ,newFile.getCanonicalPath());
				this.writeStringToFile( newFile , str);
				file = newFile;
			}
			writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(file)));
			writer.write( str );
			return file;
		}catch(Exception e){
			logger.error( "将数据保存到文件出错",e );
			return null;
		}finally{
			try{
				if(writer!=null)
					writer.close();
			}catch(Exception ee){
				logger.error( "将数据保存到文件,关闭文件读取时出错",ee );
			}
		}
	}
}
