package kde.jobcontainer.job.synchydro.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.job.synchydro.domain.HYITSTriggerRecordSyncConfig;
import kde.jobcontainer.job.synchydro.domain.TriggerRecord;
import kde.jobcontainer.util.domain.DbConfig;

/** 
 * <Description> 
 *  为从老管家库到融合版本，单独设立一个服务，主要考虑：
 *  1、增加对渗压的处理;
 *  2、增加部分数据同步时，对id以及水库编码的处理
 * @author lidong
 * @CreateDate 2021年1月7日 下午5:45:11
 * @since V1.0
 * @see kde.jobcontainer.job.synchydro.service 
 */
public class HYITSTriggerRecordSyncToV2Service extends HYITSTriggerRecordSyncService{
	public static final Logger logger = LoggerFactory.getLogger( HYITSTriggerRecordSyncToV2Service.class );
	private static HYITSTriggerRecordSyncToV2Service _instance;
	
	public static HYITSTriggerRecordSyncToV2Service getInstance(){	//单例方法
		if(_instance==null)
			_instance = new HYITSTriggerRecordSyncToV2Service();
		return _instance; 
	}
	private static final IdWorker idWorker= new IdWorker(13,7,1);
	private static final String DAM_RT_OSMOMETER = "DAM_RT_OSMOMETER";
	private static final String ST_SEEPAGEFLOW_R = "ST_SEEPAGEFLOW_R";
	
	/**
	 * Description: 
	 *  管家到融合版本平台的对接，代码主要考虑
	 * @author lidong 
	 * @param triReg
	 * @param cfg
	 * @param dbCfg
	 * @return
	 * @throws Exception 
	 */ 
	@Override
	public String getSqlByTriggerRec(TriggerRecord triReg, HYITSTriggerRecordSyncConfig cfg, DbConfig dbCfg)
			throws Exception {
		if(DAM_RT_OSMOMETER.equals(triReg.getTABID())) {
			//特殊处理渗压
			return this.getShenyaSql(triReg, cfg, dbCfg);
		}else if(ST_SEEPAGEFLOW_R.equals( triReg.getTABID() )) {
			//特殊处理渗流
			return this.getSeepageSql(triReg, cfg, dbCfg);
		}else {
			//默认情况处理水情、雨量、图像
			return super.getSqlByTriggerRec(triReg, cfg, dbCfg);
		}
		
	}
	public static void main(String[] args) {
		/*
		StringBuilder excinf = new StringBuilder("stcd:\"0000181005\",tm:\"2020-01-01 09:00:00\",rz:\"0\",");
		
		int idx = excinf.indexOf("stcd");
		excinf.replace( idx , idx+4, "measuring_point");
		
		idx = excinf.indexOf("msqmt");
		if(idx>0)
			excinf.replace( idx , idx+5, "monitor_way");
		
		idx = excinf.indexOf("rz");
		if(idx>0)
			excinf.replace( idx , idx+2, "water_level");
		
		idx = excinf.indexOf("uploadinguser");
		if(idx>0)
			excinf.replace( idx , idx+13, "remark");
		
		if(excinf.indexOf("monitor_way")==-1) {
			excinf.append("monitor_way:1,");//如果没有的话，默认为1,自动站监测
		}
		excinf.append( "id:" ).append( String.valueOf( idWorker.nextId() ) ).append(",");
		//用json嵌入的方式增加一种特殊的类型，在TriggerRecord里方便处理
		excinf.append( "reservoir_code:" ).append( "{sql:\"(select reservoir_id from st_stbprp_b where stcd='").append("999999").append("')\"}," );
		
		TriggerRecord triReg = new TriggerRecord();
		triReg.setEXCINF( excinf.toString() );
		triReg.setTABID("dam_rt_osmometer");
		triReg.setOPERATION("D");
		System.out.println( triReg.getSql("measuring_point,TM", "mysql") );
		*/
	}
	
	
	//id	reservoir_code	measuring_point	tm	reading_frequency	reading_temperature	water_level	pore_pressure	remark	create_time	monitor_way
	public String getShenyaSql(TriggerRecord triReg, HYITSTriggerRecordSyncConfig cfg, DbConfig dbCfg) throws Exception{
		//triReg中无用字段需要去掉，管家中渗压用的是水库水情数据，有用的就是stcd、tm、rz、msqmt、uploadinguser
		StringBuilder excinf = new StringBuilder(triReg.getEXCINF());
		//stcd:"0000181005",tm:"2020-01-01 09:00:00",rz:"0", 其他字段为空的情况下也不会有，不需要替换
		int idx = excinf.indexOf("stcd");
		excinf.replace( idx , idx+4, "measuring_point");
		
		idx = excinf.indexOf("msqmt");
		if(idx>0)
			excinf.replace( idx , idx+5, "monitor_way");
		
		idx = excinf.indexOf("rz");
		if(idx>0)
			excinf.replace( idx , idx+2, "water_level");
		
		idx = excinf.indexOf("uploadinguser");
		if(idx>0)
			excinf.replace( idx , idx+13, "remark");
		
		if(excinf.indexOf("monitor_way")==-1) {
			excinf.append("monitor_way:1,");//如果没有的话，默认为1,自动站监测
		}
		excinf.append( "id:" ).append( String.valueOf( idWorker.nextId() ) ).append(",");
		//用json嵌入的方式增加一种特殊的类型，在TriggerRecord里方便处理
		excinf.append( "reservoir_code:" ).append( "{sql:\"(select reservoir_id from st_stbprp_b where stcd='").append(triReg.getSTCD()).append("'  limit 0,1 )\"}," );
		
		triReg.setEXCINF( excinf.toString() );
		
		return triReg.getSql( cfg.getTblAndPks().getString( triReg.getTABID() ),this.getDbType(dbCfg));
	}
	//id	reservoir_code	measuring_point	tm	weir_head	standard_flow	remark	create_time
	public String getSeepageSql(TriggerRecord triReg, HYITSTriggerRecordSyncConfig cfg, DbConfig dbCfg) throws Exception{
		//triReg中,stcd,tm,sq,mt(测量方法),wh,operator
		StringBuilder excinf = new StringBuilder(triReg.getEXCINF());
		excinf.append( "id:" ).append( String.valueOf( idWorker.nextId() ) ).append(",");//添加id
		//用json嵌入的方式增加一种特殊的类型，在TriggerRecord里方便处理
		excinf.append( "reservoir_code:" ).append( "{sql:\"(select reservoir_id from st_stbprp_b where stcd='").append(triReg.getSTCD()).append("'  limit 0,1 )\"}," );
		int idx = excinf.indexOf("stcd");
		excinf.replace( idx , idx+4, "measuring_point");
		idx = excinf.indexOf("wh");
		if(idx>0)
			excinf.replace( idx , idx+2, "weir_head");
		
		idx = excinf.indexOf("sq");
		if(idx>0)
			excinf.replace( idx , idx+2, "standard_flow");
		
		idx = excinf.indexOf("operator");
		if(idx>0)
			excinf.replace( idx , idx+8, "remark");
		
		//TODO 测流方法目前在融合版本库中没有对应的字段
		
		
		//用json嵌入的方式增加一种特殊的类型，在TriggerRecord里方便处理
		excinf.append( "reservoir_code:" ).append( "{sql:\"(select reservoir_id from st_stbprp_b where stcd='").append(triReg.getSTCD()).append("'  limit 0,1)\"}," );
		
		triReg.setTABID("DAM_RT_WEIR");
		triReg.setEXCINF( excinf.toString() );
		return triReg.getSql( cfg.getTblAndPks().getString( triReg.getTABID() ),this.getDbType(dbCfg));
	}

	
}
