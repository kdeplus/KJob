package kde.jobcontainer.job.synchydro.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.utils.BaseConstants;

/**
 * @author lidong 2015年1月29日
 * <pre>
 * 本任务的配置对象
 * </pre>
 */
public class HYITSTriggerRecordSyncConfig {
	private Logger logger = LoggerFactory.getLogger( HYITSTriggerRecordSyncConfig.class );
	private String sendwaitQueryWhere;		//20161107 LIDONG 查询sendwait的过滤条件
	private String rowsOnce = "3000";		//20161107 LIDONG 单次同步的数据量,默认3000
	private int delDaysAgo = 50;
	private int delRowCount = 100000;			//默认一次删除10万条数据
	private int delIntervalMins = 5;			//删除操作的间隔分钟,通过每次执行后设定一个时间来判断下次执行时必须在间隔时间之后
	private JSONObject tblAndPks;				//表与主键的对应关系
	private JSONObject tblAndPKsJSON;		//表与主键的对应关系转换为JSON对象
	private JSONObject sourceDbConfig;
	private JSONObject targetDbConfig;	//目标数据库连接信息
	private String mqServerAddr;			//消息队列,用于保存任务执行的情况
	private String mainQuery;		//主要的查询语句,通过这个和saveAndDeleteQry以及sendwaitQueryWhere拼接起来进行操作
	/**
	 * 20161107 lidong 增加字段,用于判断是否将数据删掉一部分,并且直接是sql的查询条件
	 */
	private String saveAndDeleteQry;


	private String recordTblName = "st_sendwait_e";		//20150510 lidong 记录表的名称，st_sendwait_E是触发器生成的待发送表，但一旦发送了，就有可能丢掉这条数据 st_senddo_E是已经同步发送了的数据,默认sendwait
	
	public String getSendwaitQueryWhere() {
		return sendwaitQueryWhere;
	}

	public void setSendwaitQueryWhere(String sendwaitQueryWhere) {
		this.sendwaitQueryWhere = sendwaitQueryWhere;
	}

	public JSONObject getTblAndPks() {
		return tblAndPks;
	}

	public void setTblAndPks(JSONObject tblAndPks) {
		this.tblAndPks = tblAndPks;
	}
	
	/**
	 * @author lidong 2015年1月29日
	 * @return
	 * <pre>
	 * 将tblAndPKs中的主键转为为list,放置到tblAndPKsJSON中
	 * </pre>
	 */
	public JSONObject getTblAndPKsJSON(){
		if(this.tblAndPKsJSON==null&&this.tblAndPks!=null){
			this.tblAndPKsJSON = new JSONObject();
			String tmpKey = null;
			//直接将主键处理成列名List
			for(Iterator<String> it = this.tblAndPks.keySet().iterator();it.hasNext(); ){
				tmpKey = it.next();
				List li = new ArrayList();
				String x = this.tblAndPks.getString(tmpKey);
				if(x!=null&&!x.trim().equals(BaseConstants.EMPTY_STR)){
					Collections.addAll( li , x.split(",") );
				}
				tblAndPKsJSON.put( tmpKey , li );
			}
		}
		return this.tblAndPKsJSON;
	}

	public void setTblAndPKsJSON(JSONObject tblAndPKsJSON) {
		this.tblAndPKsJSON = tblAndPKsJSON;
	}

	public String getRecordTblName() {
		return recordTblName;
	}

	public void setRecordTblName(String recordTable) {
		this.recordTblName = recordTable;
	}

	public JSONObject getTargetDbConfig() {
		return targetDbConfig;
	}

	public void setTargetDbConfig(JSONObject targetDbConfig) {
		this.targetDbConfig = targetDbConfig;
	}

	public JSONObject getSourceDbConfig() {
		return sourceDbConfig;
	}

	public void setSourceDbConfig(JSONObject sourceDbConfig) {
		this.sourceDbConfig = sourceDbConfig;
	}

	public String getSaveAndDeleteQry() {
		return saveAndDeleteQry;
	}

	public void setSaveAndDeleteQry(String saveAndDeleteQry) {
		this.saveAndDeleteQry = saveAndDeleteQry;
	}

	public String getRowsOnce() {
		return rowsOnce;
	}

	public void setRowsOnce(String rowsOnce) {
		this.rowsOnce = rowsOnce;
	}

	public int getDelDaysAgo() {
		return delDaysAgo;
	}

	public void setDelDaysAgo(int delDaysAgo) {
		this.delDaysAgo = delDaysAgo;
	}

	public int getDelRowCount() {
		return delRowCount;
	}

	public void setDelRowCount(int delRowCount) {
		this.delRowCount = delRowCount;
	}

	public int getDelIntervalMins() {
		return delIntervalMins;
	}

	public void setDelIntervalMins(int delIntervalMins) {
		this.delIntervalMins = delIntervalMins;
	}

	public String getMqServerAddr() {
		return mqServerAddr;
	}

	public void setMqServerAddr(String mqServerAddr) {
		this.mqServerAddr = mqServerAddr;
	}

	public String getMainQuery() {
		return mainQuery;
	}

	public void setMainQuery(String mainQuery) {
		this.mainQuery = mainQuery;
	}
	
	
}
