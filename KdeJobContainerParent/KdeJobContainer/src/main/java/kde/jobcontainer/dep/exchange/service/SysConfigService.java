package kde.jobcontainer.dep.exchange.service;

import java.util.Date;

import kde.jobcontainer.dataSync.util.DataConstants;
import kde.jobcontainer.dep.domain.SysStatusConfig;
import kde.jobcontainer.dep.exchange.dao.base.BaseAccessDao;
import kde.jobcontainer.dep.exchange.util.ConfigUtil;

/**
 * @author xxxxxxxx
 * @date 2014-9-26, 下午5:26:35
 * 系统配置服务功能
 */
public class SysConfigService {
	private BaseAccessDao dao;
	
	private String tableName = "DEP_SYNCCONFIG";
	
	private String createSql = "CREATE TABLE DEP_SYNCCONFIG(ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)primary key,NODENAME varchar(50) UNIQUE,START INTEGER,LASTUPDATETIME timestamp)";
	
	public SysConfigService(BaseAccessDao dao) {
		this.dao = dao;
	}
	
	/**
	 * 检查表是否存在并创建、插入系统配置数据
	 * @throws Exception
	 */
	public void checkAndInsert(String sysName) throws Exception {
		boolean hasTable = dao.isHasTable(tableName);
		if(!hasTable) {   //如果没有就进行创建，并初始化数据
			dao.execute(createSql); 
		}
		boolean hasConfig = dao.hasSysInfo(sysName);
		if(!hasConfig) {  //如果没有则手动插入
			SysStatusConfig sysConfig = new SysStatusConfig();
			sysConfig.setNodeName(sysName);
			sysConfig.setLastUpdateTime(new Date());
			sysConfig.setStart(DataConstants.DEFAULT_SYS_STATUS);   //根据默认配置
			dao.insertSysInfo(sysConfig);
		}
	}
	
	public SysStatusConfig getSysConfig() throws Exception {    //得到本系统的配置项（是否启动等配置）
		ConfigUtil configUtil = ConfigUtil.getInstance();
		String sysName = configUtil.getDataSyncName();   //得到本实例名
		checkAndInsert(sysName);
		SysStatusConfig statuConfig = dao.getSysConfig(sysName);
		return statuConfig;
	}
	
	/*  Gatters and Setters  */
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getCreateSql() {
		return createSql;
	}
	public void setCreateSql(String createSql) {
		this.createSql = createSql;
	}

}

