package kde.jobcontainer.dep.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.domain.DEPJobConfig;
import kde.jobcontainer.util.domain.DbConfig;
import kde.jobcontainer.util.utils.db.DbUtil;
import kde.jobcontainer.utils.ConnectionUtils;

public class JobDbUtil {
	private DbConfig config;
	public JobDbUtil( DbConfig cfg ){
		this.config = cfg;
	}
	private static Logger logger = LoggerFactory.getLogger( JobDbUtil.class );
	
	/**
	 * id 主键标识
	 * jobName	标识出这个job的任务,避免多任务中文件名重复
	 * fileName	操作过的文件名称
	 * fileModifyDate TIMESTAMP 文件处理时它的修改时间
	 * processDate 处理的时间
	 * status 处理状态标识
	 * info 相关信息
	 */
	private static String createTableSql = "create table DEP_JOBCONFIG(ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)primary key,serverId varchar(50),tag varchar(50),type varchar(50),name varchar(50),jobConfig varchar(5000),schedule varchar(50),jarPath varchar(100),jobClassName varchar(100),start INTEGER) ";
	private static String dropTableSql = "drop table DEP_JOBCONFIG";
	public void checkAndCreateTable() throws Exception{
		Connection conn = null;
		try{
			//conn = DbUtil.getConnection(this.config);
			conn = ConnectionUtils.getConnection(this.config);
			//conn.prepareStatement("update rel_url_id set logid=0").executeUpdate();
			DbUtil.execute(dropTableSql, conn);
			if(! DbUtil.checkTable( "DEP_JOBCONFIG" , conn) ){
				//create table
				DbUtil.execute( createTableSql , conn);
				logger.info( "构建SendJob日志表完成" );
				//insertRsvr();
				//insertDataSend();
				//insertPicSend();
				//insertReceive();
//				insertWarnInfo();
//				insertRainDay();
//				insertRain();
				//insertWeather();
				insertDBMsgSenderJob();
				insertDBReceiveJob();
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			ConnectionUtils.close(conn);
		}
	}
	
	private void insertRain() {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "insert into DEP_JOBCONFIG(serverId,tag,type,name,jobConfig,schedule,jarPath,jobClassName,start) values(?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "rain");
			stmt.setString(2, "rain");
			stmt.setString(3, "rain");
			stmt.setString(4, "雨量信息");
			stmt.setString(5, "{failCountLimit:3,hourBefore:240,logFileName:'e:/SHZH4Services/log/message.log',smsReceiptHours:3,userName:'沿河县',city:'铜仁地区',county:'沿河县',hydrological:{databasename:'FX_SHZHINFO',url:'jdbc:jtds:sqlserver://192.168.1.120;instance=SQL2008',username:'sa',password:'Richway1',driverclass:'net.sourceforge.jtds.jdbc.Driver'},system:{databasename:'RICHWAY_SSSHZH_SYSTEM',url:'jdbc:jtds:sqlserver://192.168.1.120;instance=SQL2008',username:'sa',password:'Richway1',driverclass:'net.sourceforge.jtds.jdbc.Driver'}}");
			stmt.setString(6, "0 0/2 * * * * ?");
			stmt.setString(7, "e:/jobs/rainDay.jar");
			stmt.setString(8, "kde.jobcontainer..job.RainUtil");
			stmt.setInt(9, 1);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
		
	}

	private void insertRainDay() {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "insert into DEP_JOBCONFIG(serverId,tag,type,name,jobConfig,schedule,jarPath,jobClassName,start) values(?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "rainDay");
			stmt.setString(2, "rainDay");
			stmt.setString(3, "rainDay");
			stmt.setString(4, "雨量信息");
			stmt.setString(5, "{failCountLimit:3,dayBefore:10,logFileName:'e:/richway/Services/log/rainday.log',hydrological:{databasename:'FX_SHZHINFO',url:'jdbc:jtds:sqlserver://192.168.1.120;instance=SQL2008',username:'sa',password:'Richway1',driverclass:'net.sourceforge.jtds.jdbc.Driver'},system:{databasename:'RICHWAY_SSSHZH_SYSTEM',url:'jdbc:jtds:sqlserver://192.168.1.120;instance=SQL2008',username:'sa',password:'Richway1',driverclass:'net.sourceforge.jtds.jdbc.Driver'}}");
			stmt.setString(6, "0 0/2 * * * ?");
			stmt.setString(7, "e:/jobs/rainDay.jar");
			stmt.setString(8, "kde.jobcontainer..job.RainDayUtil");
			stmt.setInt(9, 1);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
	}

	private void insertWarnInfo() {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "insert into DEP_JOBCONFIG(serverId,tag,type,name,jobConfig,schedule,jarPath,jobClassName,start) values(?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "warninfo");
			stmt.setString(2, "warninfo");
			stmt.setString(3, "warninfo");
			stmt.setString(4, "预警信息生成");
			stmt.setString(5, "{warnPeriodHours:3,autoCloseWarnHours:24,hourBefore:24,logFileName:'e:/SHZH4Services/log/warn.log',warnMsgConfig:{signName:'防办',templateList:[{gradeId:'default',content:'[stnm]，[tm]，[intv][item][value]，超预警阀值[exValue]，引发[warnGradeNm]预警。'},{gradeId:'6',content:'[stnm]，[tm]，[intv][item][value]，超预警阀值[exValue]，引发[warnGradeNm]预警。'},{gradeId:'7',content:'[stnm]，[tm]，[intv][item][value]，超预警阀值[exValue]，引发[warnGradeNm]预警。'}]},hydrological:{databasename:'FX_SHZHINFO',url:'jdbc:jtds:sqlserver://192.168.1.120;instance=SQL2008',username:'sa',password:'Richway1',driverclass:'net.sourceforge.jtds.jdbc.Driver'},system:{databasename:'RICHWAY_SSSHZH_SYSTEM',url:'jdbc:jtds:sqlserver://192.168.1.120;instance=SQL2008',username:'sa',password:'Richway1',driverclass:'net.sourceforge.jtds.jdbc.Driver'}}");
			stmt.setString(6, "0/30 * * * * ?");
			stmt.setString(7, "e:/jobs/warnInfo.jar");
			stmt.setString(8, "kde.jobcontainer..job.WarnUtil");
			stmt.setInt(9, 1);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
		
	}

	private void insertRsvr() {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "insert into DEP_JOBCONFIG(serverId,tag,type,name,jobConfig,schedule,jarPath,jobClassName,start) values(?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "parse");
			stmt.setString(2, "nsj_rsvr");
			stmt.setString(3, "file_parse");
			stmt.setString(4, "\u519C\u6C34\u5C40\u6C34\u5E93\u62A5\u6C5B\u6570\u636E");
			stmt.setString(5, "{'dataFilePath': 'e:/dataFiles/',modifyReProcess:'false',"+
		                "fileFilterCfg: {nameReg: '^\\d{4}\\d{1,2}\\d{1,2}\\d{1,2}$',dayBefore: 1,fileExt: 'txt',date: '2014-04-13'},"+
		                "dbConfig: {url: 'jdbc:derby:RWDEPSys;create=true',driver: 'org.apache.derby.jdbc.EmbeddedDriver'},"+
		                "fileParseCfg:{"+
		                	"fileParseHelper:'kde.jobcontainer.utilutils.file.parser.KeyValueTxtParseHelper',"+
		                	"recParser:'kde.jobcontainer..dep.nsjrsvr.file.NsjFileRecordParse',"+
		                	"keyValueSplitCode:' ';	keyValueBeginIndex:0,"+
		                	"splitRegExp:'K+([^KNN]*)+NN',"+
		                	"keyValueParse:{PD:'pptn.dyp',WS:'pptn.wth,rsvr.wth',W:'rsvr.r',QI:'rsvr.inq',ZU:'rsvr.rz',QA:'rsvr.otq',ZS:'rsvr.rwptn'}"+
		                "},"+
		                "sendQueName:'nsjrsvr',"+
		                "targetDb:[{"+
		                		"url: 'jdbc:jtds:sqlserver://192.168.1.229:1433;databaseName=SQRT',"+
		                    	"driver: 'net.sourceforge.jtds.jdbc.Driver',"+
		                    	"username:'sa',"+
		                    	"dbTypeHelper:'derby'"+
		                	"},{"+
		                		"url: 'jdbc:jtds:sqlserver://192.168.1.229:1433;databaseName=test',"+
		                    	"driver: 'net.sourceforge.jtds.jdbc.Driver',"+
		                    	"username:'sa',"+
		                    	"dbTypeHelper:'derby'"+
		                	"}],"+
		                "objSqlConfig:["+
		                	"{name:'rsvr',table:'ST_RSVR_R',fields:["+
		                			"{'keyName':'stcd','fieldName':'stcd',pk:'true',type:'String'},"+
		                			"{'keyName':'tm','fieldName':'tm',pk:'true',type:'Date'},"+
		                			"{'keyName':'rz','fieldName':'rz',pk:'false',type:'Double'},"+
		                			"{'keyName':'inq','fieldName':'inq',pk:'false',type:'Double'},"+
									"{'keyName':'w','fieldName':'w',pk:'false',type:'Double'},"+
									"{'keyName':'blrz','fieldName':'blrz',pk:'false',type:'Double'},"+
									"{'keyName':'otq','fieldName':'otq',pk:'false',type:'Double'},"+
									"{'keyName':'inqdr','fieldName':'inqdr',pk:'false',type:'Double'},"+
									"{'keyName':'rwchrcd','fieldName':'rwchrcd',pk:'false',type:'String'},"+
									"{'keyName':'rwptn','fieldName':'rwptn',pk:'false',type:'String'},"+
									"{'keyName':'msqmt','fieldName':'msqmt',pk:'false',type:'String'}"+
		                		"]},"+
		                	"{name:'pptn',table:'ST_PPTN_R',fields:["+
		                			"{'keyName':'stcd','fieldName':'stcd',pk:'true',type:'String'},"+
		                			"{'keyName':'tm','fieldName':'tm',pk:'true',type:'Date'},"+
		                			"{'keyName':'drp','fieldName':'drp',pk:'false',type:'Double'},"+
		                			"{'keyName':'dyp','fieldName':'dyp',pk:'false',type:'Double'},"+
									"{'keyName':'intv','fieldName':'intv',pk:'false',type:'Double'},"+
									"{'keyName':'pdr','fieldName':'pdr',pk:'false',type:'Double'},"+
									"{'keyName':'wth','fieldName':'wth',pk:'false',type:'String'}"+
		                		"]}]}");
			stmt.setString(6, "0/10 * * * * ?");
			stmt.setString(7, "f:/file/RWDEJ_File_20140408.jar");
			stmt.setString(8, "kde.jobcontainer..dep.nsjrsvr.NsjRsvrFileJob");
			stmt.setInt(9, 1);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
	}

	private void insertReceive() {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "insert into DEP_JOBCONFIG(serverId,tag,type,name,jobConfig,schedule,jarPath,jobClassName,start) values(?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "receive");
			stmt.setString(2, "msgreceive");
			stmt.setString(3, "msgreceive");
			stmt.setString(4, "消息接收");
			stmt.setString(5, "{fileurl:'e:/upload',nodeid:'123123123123',receiveadd:{'test':'192.168.1.120'},depjobconfig:[{databasename:'FX_SYSTEM',url:'jdbc:jtds:sqlserver://192.168.1.120;instance=SQL2008',username:'sa',password:'Richway1',driverclass:'net.sourceforge.jtds.jdbc.Driver'}],dataconfig:[{databasename:'dataconfig',url:'jdbc:derby://localhost:1527/dataconfig;create=true',driverclass:'org.apache.derby.jdbc.ClientDriver'}], hydrological:[{databasename:'RICHWAY_SSSHZH_TEST',url:'jdbc:jtds:sqlserver://192.168.1.120;instance=SQL2008',username:'sa',password:'Richway1',driverclass:'net.sourceforge.jtds.jdbc.Driver'}],weather:[{databasename:'FX_WEATHER',url:'jdbc:jtds:sqlserver://192.168.1.120;instance=SQL2008',username:'sa',password:'Richway1',driverclass:'net.sourceforge.jtds.jdbc.Driver'}]}");
			stmt.setString(6, "0 1/2 * * * ?");
			stmt.setString(7, "e:/jobs/MsgReceiveJob.jar");
			stmt.setString(8, "kde.jobcontainer..receiveMsg.recjob.ReceiveMsgJob");
			stmt.setInt(9, 1);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
	}

	private void insertPicSend() {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "insert into DEP_JOBCONFIG(serverId,tag,type,name,jobConfig,schedule,jarPath,jobClassName,start) values(?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "picsend");
			stmt.setString(2, "picsend");
			stmt.setString(3, "picsend");
			stmt.setString(4, "图片信息发送");
			stmt.setString(5, "{sendconfig:{"+
							"'test':'tcp://192.168.1.120:61616?jms.blobTransferPolicy.defaultUploadUrl=http://192.168.1.120:8161/fileserver/'"+
		            	"}, "+
		                "type:{'气象云图':"+
		            		"{baseUrl:'http://124.193.192.194:8080/Clouds/Cloud/Weather/',"+
		            		"startMinute:0,"+
		            		"intervalMinute:30,"+
		            		"canAccessOffset:10,"+
		            		"failCountLimit:3,"+
		            		"failTimeout:90,"+
		            		"hourBefore:2,"+
		            		"hourDifference:0,"+
		            		"nameExt:'000.jpg',"+
		            		"dateFormat:'yyyyMMddHHmmss',"+
		            		"pdaImgZoomRatio:'0.8',"+
		            		"savingDir:'Weather'},"+
		            		"'水汽云图':"+
		            		"{baseUrl:'http://124.193.192.194:8080/Clouds/Cloud/WaterVapor/',"+
		            		"startMinute:15,"+
		            		"intervalMinute:60,"+
		            		"canAccessOffset:20,"+
		            		"failCountLimit:3,"+
		            		"failTimeout:120,"+
		            		"hourBefore:2,"+
		            		"hourDifference:0,"+
		            		"nameExt:'000.jpg',"+
		            		"dateFormat:'yyyyMMddHHmmss',"+
		            		"pdaImgZoomRatio:'0.6',"+
		            		"savingDir:'WaterVapor'},"+
		            		"'红外云图':"+
		            		"{baseUrl:'http://124.193.192.194:8080/Clouds/Cloud/Infrared/',"+
		            		"startMinute:0,"+
		            		"intervalMinute:30,"+
		            		"canAccessOffset:15,"+
		            		"failCountLimit:3,"+
		            		"failTimeout:90,"+
		            		"hourBefore:2,"+
		            		"hourDifference:0,"+
		            		"nameExt:'000.jpg',"+
		            		"dateFormat:'yyyyMMddHHmmss',"+
		            		"pdaImgZoomRatio:'0.8',"+
		            		"savingDir:'Infrared'},"+
		            		"'全国雷达拼图':"+
		            		"{baseUrl:'http://124.193.192.194:8080/Clouds/Cloud/Radar/qg/',"+
		            		"startMinute:0,"+
		            		"intervalMinute:10,"+
		            		"canAccessOffset:10,"+
		            		"failCountLimit:3,"+
		            		"failTimeout:60,"+
		            		"hourBefore:2,"+
		            		"hourDifference:0,"+
		            		"nameExt:'001.gif',"+
		            		"dateFormat:'yyyyMMddHHmmss',"+
		            		"pdaImgZoomRatio:'',"+
		            		"savingDir:'Radar'}},"+
		            	"baseSavingDir:'e:/SHZH4/Clouds/Cloud/',"+
		            	"dbConfig: {"+
		                    "url: 'jdbc:derby:demo;create=true',"+
		                    "driver: 'org.apache.derby.jdbc.EmbeddedDriver'"+
		               " },"+
		               " dataconfig:{"+
		                	"databasename:'FX_SYSTEM',"+
		            		"url:'jdbc:jtds:sqlserver://192.168.1.120;instance=SQL2008',"+
		            		"username:'sa',"+
		            		"password:'Richway1',"+
		            		"driverclass:'net.sourceforge.jtds.jdbc.Driver'"+
		                "},"+
		                "sendtype:1}");
			stmt.setString(6, "0 0/2 * * * ?");
			stmt.setString(7, "e:/jobs/PicSendJob.jar");
			stmt.setString(8, "kde.jobcontainer..job.PicSendJob");
			stmt.setInt(9, 1);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
	}

	private void insertDataSend() {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "insert into DEP_JOBCONFIG(serverId,tag,type,name,jobConfig,schedule,jarPath,jobClassName,start) values(?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "msgsend");
			stmt.setString(2, "msgsend");
			stmt.setString(3, "msgsend");
			stmt.setString(4, "消息发送");
			stmt.setString(5, "{datatype:'hydrological',"+
						"derbyconfig:'dataconfig',"+
						"depjobconfig:'depjobconfig',"+
		                "dbConfig: {"+
		                    "url: 'jdbc:derby://localhost:1527/dataconfig;create=true',"+
		                    "driver: 'org.apache.derby.jdbc.ClientDriver'"+
		                "},"+
		            	"logdbconfig:{"+
		            		"databasename:'FX_SYSTEM',"+
		            		"url:'jdbc:jtds:sqlserver://192.168.1.120;instance=SQL2008',"+
		            		"username:'sa',"+
		            		"password:'Richway1',"+
		            		"driverclass:'net.sourceforge.jtds.jdbc.Driver'"+
		            	"},"+
		            	"datadbconfig:{"+
		            		"url:'jdbc:jtds:sqlserver://192.168.1.120;instance=SQL2008',"+
		            		"username:'sa',"+
		            		"password:'Richway1',"+
		            		"driverclass:'net.sourceforge.jtds.jdbc.Driver'"+
		            	"},"+		  
		            	"sendconfig:{"+
							"'test':'tcp://192.168.1.120:61616?jms.blobTransferPolicy.defaultUploadUrl=http://192.168.1.120:8161/fileserver/'"+
		            	"},"
							+"keymap:{ST_STORM_R:{'STCD@60608100%TM@2008-07-11 07:00:00.000':{'STCD':'60610400','TM':'2009-09-09 09:00:00.000'}}},"+
		            	"rule:[{tname:'ST_RIVER_R',col:'STCD',opt:'in',val:'60608100',dataType:'string',url:'tcp://192.168.1.120:61616?jms.blobTransferPolicy.defaultUploadUrl=http://192.168.1.120:8161/fileserver/'}]}");
			stmt.setString(6, "0 0/2 * * * ?");
			stmt.setString(7, "e:/jobs/MsgOptJob.jar");
			stmt.setString(8, "kde.jobcontainer..sendMsg.sendJob.SendMsgJob");
			stmt.setInt(9, 1);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
	}

	private void insertWeather() {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "insert into DEP_JOBCONFIG(serverId,tag,type,name,jobConfig,schedule,jarPath,jobClassName,start) values(?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "wtsend");
			stmt.setString(2, "wtsend");
			stmt.setString(3, "wtsend");
			stmt.setString(4, "天气消息发送");
			stmt.setString(5, "{"+
		             	"sendconfig:{"+
							"'test':'tcp://192.168.1.120:61616?jms.blobTransferPolicy.defaultUploadUrl=http://192.168.1.120:8161/fileserver/'"+
		            	"},"+
		            	"theWeatherXmlUrl:'http://flash.weather.com.cn/wmaps/xml/',"+
		            	"datatype:'weather',"+
		            	"tablename:'WT_WEATHER_R',"+
		                "file:'china.xml,sichuan.xml'"+ 
		            "}");
			stmt.setString(6, "0/50 * * * * ?");
			stmt.setString(7, "e:/jobs/WTSendJob.jar");
			stmt.setString(8, "kde.jobcontainer..sendJob.WTSendJob");
			stmt.setInt(9, 1);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
	}

	public void updateRel(Long id, Long logid,JSONObject logdbconfig) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "update  REL_URL_ID set logid="+logid+" where id="+id+"";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
	}
	public boolean checkUrl(String url){
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean hasurl=false;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "select id from  REL_URL_ID where url=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, url);
			rs = stmt.executeQuery();
			if(rs.next()){
				hasurl=true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(rs);
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
		return hasurl;
	}
	
	public void insertData(String url){
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "insert into  REL_URL_ID(url,logid) values(?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, url);
			stmt.setLong(2, 0l);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(rs);
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
	}
	public void checkAndInsert(String url) {
		Connection conn = null;
		try{
			conn = ConnectionUtils.getConnection(this.config);
			if(!checkUrl(url) ){
				//insert data
				insertData(url);
				logger.info( "插入数据完成" );
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally{
			try {
				ConnectionUtils.close(conn);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 无触发器同步服务job初始化数据
	 */
	private void insertDBMsgSenderJob() {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "insert into DEP_JOBCONFIG(serverId,tag,type,name,jobConfig,schedule,jarPath,jobClassName,start) values(?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "receive");
			stmt.setString(2, "msgreceive");
			stmt.setString(3, "msgreceive");
			stmt.setString(4, "消息接收");
			stmt.setString(5, "{" +
					          "mqAccessConfig:{" +    //Mq发送服务配置
					                          "brokeUrl:'failover:(tcp://localhost:61616?wireFormat.maxInactivityDuration=0)'," +
					                          "queueName:'xxxxxxxx'}," +
							  "dataFromDao:{" +    //数据来源库配置
					                        "databaseType:'SQLSERVER'," +
					                        "url:'jdbc:jtds:sqlserver://192.168.1.120:1433;DatabaseName=RICHWAY_SHZH_WATER',"+
						            	    "username:'sa',"+
						            		"password:'sa',"+
						            		"driverclass:'net.sourceforge.jtds.jdbc.Driver'}," +
						      "cacheDao:{" +     //数据比对库配置
						      				"databaseType:'DERBY'," + 
						                    "url:'jdbc:derby://localhost:1527/dataconfig;create=true',"+
		                                    "driverclass:'org.apache.derby.jdbc.ClientDriver'}," +
						      "cacheDataInit:[{" +
						                     "checkSql:\"select * from sys.systables t where t.tablename='ST_PPTN_R'\"," +
						                     "disposeSql:['CREATE TABLE ST_PPTN_R(STCD VARCHAR(8),TM timestamp,drp float,intv float,pdr float,dyp float,wth varchar(1),primary key(STCD,TM))']}]," +
		                      "operateSqls:[{" +   
		                                    "checkSql:'select count(tm) as newDataTag FROM  ST_PPTN_R  where tm > @TM'," +
		                                    "fetchSql:'select TOP 20  * FROM  ST_PPTN_R  where tm > @TM'," +
		                                    "curBoundKey:'TM'," +
		                                    "tableName:'ST_PPTN_R'," +
		                                    "sourceFetchSql:'select * FROM ST_PPTN_R WHERE TM BETWEEN @FORETM AND @TM'," +
		                                    "compareFetchSql:'select * FROM ST_PPTN_R WHERE TM BETWEEN @FORETM AND @TM'," +
		                                    "primaryKey:['STCD','TM']," +
		                                    "paramsSql:[{paramsSql:'SELECT coalesce(max(TM),'1990-01-01 00:00:00.000') AS TM FROM ST_PPTN_R',keysBack:['TM']}]," +
		                                    "paramsSelfDefTag:'#dateDiff(@TM,DATE,-3,FORETM)'," +    //自定义参数获取,可以有多个
		                                    "comparator:'greater'," +
		                                    "filterSql:\" STCD = '10000001' and TM > #dateTrans(1990-01-02 12:00:00.000,filterDate)\" +           //过滤条件,可以嵌入自定义标签，前提是已经有自定义标签类且有spring配置" +
		                                    "}]" +
		                                    "}");
			stmt.setString(6, "0 0/2 * * * ?");
			stmt.setString(7, "e:/jobs/dbMsgSenderJob.jar");
			stmt.setString(8, "kde.jobcontainer..receiveMsg.recjob.ReceiveMsgJob");
			stmt.setInt(9, 1);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
	}
	
	/**
	 * 无触发器同步服务job初始化数据
	 */
	private void insertDBReceiveJob() {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "insert into DEP_JOBCONFIG(serverId,tag,type,name,jobConfig,schedule,jarPath,jobClassName,start) values(?,?,?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "receive");
			stmt.setString(2, "msgreceive");
			stmt.setString(3, "msgreceive");
			stmt.setString(4, "消息接收");
			stmt.setString(5, "{" +
					 			"mqAccessConfig:{" +    //Mq发送服务配置
					 					"brokeUrl:'failover:(tcp://localhost:61616?wireFormat.maxInactivityDuration=0)'," +
					 					"queueName:'xxxxxxxx'}," +
					 			"dataExchangeDao:{" +    //数据来源库配置
				                        "databaseType:'SQLSERVER'," +
				                        "url:'jdbc:jtds:sqlserver://localhost:1433;DatabaseName=xxxxxxxx_TEST',"+
					            	    "username:'sa',"+
					            		"password:'Richway1',"+
					            		"driverclass:'net.sourceforge.jtds.jdbc.Driver'}" +
		                      "}");
			stmt.setString(6, "0 1/2 * * * ?");
			stmt.setString(7, "e:/jobs/dbMsgSenderJob.jar");
			stmt.setString(8, "kde.jobcontainer..receiveMsg.recjob.ReceiveMsgJob");
			stmt.setInt(9, 1);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
	}
	
	public List<DEPJobConfig> getConfigs() {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<DEPJobConfig> jobList = new ArrayList<DEPJobConfig>();
		try {
			conn = ConnectionUtils.getConnection(this.config);
			String sql = "select * from DEP_JOBCONFIG where start=1";
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			DEPJobConfig job = null;
			while(rs.next()){
				job = new DEPJobConfig();
				job.setId(rs.getInt("ID"));
				job.setJarPath(rs.getString("jarPath"));
				job.setJobClassName(rs.getString("jobClassName"));
				job.setJobConfig(rs.getString("jobConfig"));
				job.setName(rs.getString("name"));
				job.setSchedule(rs.getString("schedule"));
				job.setServerId(rs.getString("serverId"));
				job.setStart(rs.getInt("start"));
				job.setTag(rs.getString("tag"));
				job.setType(rs.getString("type"));
				jobList.add(job);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			ConnectionUtils.close(rs);
			ConnectionUtils.close(stmt);
			ConnectionUtils.close(conn);
		}
		return jobList;
	}
	
	public static void main(String[] args) {
		
	}
}
