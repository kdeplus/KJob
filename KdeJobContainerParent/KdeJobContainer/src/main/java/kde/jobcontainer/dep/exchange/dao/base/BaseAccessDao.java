package kde.jobcontainer.dep.exchange.dao.base;

import java.util.Date;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.dataSync.domain.DEPNode;
import kde.jobcontainer.dataSync.domain.DepJobConfig;
import kde.jobcontainer.dep.domain.SysStatusConfig;

/**
 * @author xxxxxxxx
 * @date 2014-9-21, 下午9:15:38
 * 基本访问dao
 */
public abstract class BaseAccessDao {
	public Logger logger = LoggerFactory.getLogger(BaseAccessDao.class);
	
	public DataSource dataSource;
	
	public void init(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * 新增一条job记录
	 * @param jobConfig   job对象
	 * @return
	 * @throws Exception
	 */
	public abstract int addConfigJob(DepJobConfig jobConfig) throws Exception;
	
	/**
	 * 修改一条job记录
	 * @param jobConfig
	 * @return
	 * @throws Exception
	 */
	public abstract int updateConfigJob(DepJobConfig jobConfig) throws Exception;
	
	/**
	 * 删除一条记录
	 * @param jobConfig
	 * @return
	 * @throws Exception
	 */
	public abstract int deleteConfigJob(DepJobConfig jobConfig) throws Exception;
	
	/**
	 * 查看表是否存在
	 * @param tableName   表名
	 * @return
	 * @throws Exception
	 */
	public abstract boolean isHasTable(String tableName) throws Exception;
	
	/**
	 * 执行指定的sql
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public abstract boolean execute(String sql) throws Exception;
	
	/**
	 * 查看数据库中是否含有本系统信息
	 * @param sysName
	 * @return
	 * @throws Exception
	 */
	public abstract boolean hasSysInfo(String sysName) throws Exception;
	
	/**
	 * 插入一条系统配置信息
	 * @param sysConfig
	 * @return
	 * @throws Exception
	 */
	public abstract int insertSysInfo(SysStatusConfig sysConfig) throws Exception;
	
	/**
	 * 得到本系统的默认配置
	 * @param sysName
	 * @return
	 * @throws Exception
	 */
	public abstract SysStatusConfig getSysConfig(String sysName) throws Exception;
	
	/**
	 * 修改节点同步配置
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public abstract int updateNodeInfo(DEPNode node,Date updateTime) throws Exception;
}

