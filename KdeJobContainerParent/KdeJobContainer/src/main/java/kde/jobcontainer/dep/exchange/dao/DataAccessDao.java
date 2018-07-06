package kde.jobcontainer.dep.exchange.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import kde.jobcontainer.dataSync.domain.DEPNode;
import kde.jobcontainer.dataSync.domain.DepJobConfig;
import kde.jobcontainer.dep.domain.SysStatusConfig;
import kde.jobcontainer.dep.exchange.dao.base.BaseAccessDao;
import kde.jobcontainer.dep.exchange.util.CommonMethod;

/**
 * @author xxxxxxxx
 * @date 2014-9-21, 下午9:15:04
 */
public class DataAccessDao extends BaseAccessDao {

	@Override
	public int addConfigJob(DepJobConfig jobConfig) throws Exception {
		int changeCount = 0;
		Connection connection = null;
		PreparedStatement psMent = null;
		try {
			connection = getConnection();
			List<Object> paramList = new Vector<Object>();
			String sql = getInsertSql(paramList, jobConfig);
			boolean autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			psMent = connection.prepareStatement(sql);
			if(logger.isInfoEnabled()) {
				logger.info("operationSql = "+sql);
			}
			for (int i = 0; i < paramList.size(); i++) {
				psMent.setObject(i + 1, paramList.get(i));
			}
			changeCount = psMent.executeUpdate();
			connection.commit();
			connection.setAutoCommit(autoCommit);
		} catch (Exception e) {
			connection.rollback();
			throw new RuntimeException(e);
		} finally {
			if(psMent != null) {
				psMent.close();
			}
			if(connection != null) {
				connection.close();
			}
		}
		return changeCount;
	}
	
	@Override
	public int updateConfigJob(DepJobConfig jobConfig) throws Exception {
		int changeCount = 0;
		Connection connection = null;
		PreparedStatement psMent = null;
		try {
			connection = getConnection();
			List<Object> paramList = new Vector<Object>();
			String sql = getUpdateSql(paramList, jobConfig);
			boolean autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			psMent = connection.prepareStatement(sql);
			if(logger.isInfoEnabled()) {
				logger.info("operationSql = "+sql);
			}
			for (int i = 0; i < paramList.size(); i++) {
				psMent.setObject(i + 1, paramList.get(i));
			}
			changeCount = psMent.executeUpdate();
			connection.commit();
			connection.setAutoCommit(autoCommit);
		} catch (Exception e) {
			connection.rollback();
			throw new RuntimeException(e);
		} finally {
			if(psMent != null) {
				psMent.close();
			}
			if(connection != null) {
				connection.close();
			}
		}
		return changeCount;
	}
	
	@Override
	public int deleteConfigJob(DepJobConfig jobConfig) throws Exception {
		int changeCount = 0;
		Connection connection = null;
		PreparedStatement psMent = null;
		try {
			connection = getConnection();
			String sql = "delete from DEP_JOBCONFIG where name=?";
			boolean autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			psMent = connection.prepareStatement(sql);
			if(logger.isInfoEnabled()) {
				logger.info("operationSql = "+sql);
			}
			psMent.setString(1, jobConfig.getName());
			changeCount = psMent.executeUpdate();
			connection.commit();
			connection.setAutoCommit(autoCommit);
		} catch (Exception e) {
			connection.rollback();
			throw new RuntimeException(e);
		} finally {
			if(psMent != null) {
				psMent.close();
			}
			if(connection != null) {
				connection.close();
			}
		}
		return changeCount;
	}
	
	@Override
	public boolean isHasTable(String tableName) throws Exception {
		boolean hasTable = false;
		Connection connection = null;
		PreparedStatement psMent = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			String sql = "select * from SYS.SYSTABLES t where t.tablename = ?";
			psMent = connection.prepareStatement(sql);
			if(logger.isInfoEnabled()) {
				logger.info("operationSql = "+sql);
			}
			psMent.setString(1, tableName);
			rs = psMent.executeQuery();
			if(rs.next()) {
				hasTable = true;
			}
		} catch (Exception e) {
			connection.rollback();
			throw new RuntimeException(e);
		} finally {
			if(psMent != null) {
				psMent.close();
			}
			if(connection != null) {
				connection.close();
			}
		}
		return hasTable;
	}
	
	@Override
	public boolean execute(String sql) throws Exception {
		boolean success = false;
		Connection connection = null;
		PreparedStatement psMent = null;
		try {
			connection = getConnection();
			psMent = connection.prepareStatement(sql);
			if(logger.isInfoEnabled()) {
				logger.info("operationSql = "+sql);
			}
			success = psMent.execute();
		} catch (Exception e) {
			connection.rollback();
			throw new RuntimeException(e);
		} finally {
			if(psMent != null) {
				psMent.close();
			}
			if(connection != null) {
				connection.close();
			}
		}
		return success;
	}
	
	@Override
	public boolean hasSysInfo(String sysName) throws Exception {
		String sql = "select ID,NODENAME,START,LASTUPDATETIME from DEP_SYNCCONFIG where NODENAME=? ";
		Connection connection = null;
		PreparedStatement psMent = null;
		boolean hasData = false;
		ResultSet rs = null;
		try {
			connection = getConnection();
			psMent = connection.prepareStatement(sql);
			if(logger.isInfoEnabled()) {
				logger.info("operationSql = "+sql);
			}
			psMent.setString(1, sysName);
			rs = psMent.executeQuery();
			if(rs.next()) {
				hasData = true;
			}
		} catch (Exception e) {
			connection.rollback();
			throw new RuntimeException(e);
		} finally {
			if(psMent != null) {
				psMent.close();
			}
			if(connection != null) {
				connection.close();
			}
		}
		return hasData;
	}
	
	@Override
	public int insertSysInfo(SysStatusConfig sysConfig) throws Exception {
		String sql = "insert into DEP_SYNCCONFIG(NODENAME,START,LASTUPDATETIME) values(?,?,?)";
		int changeCount = 0;
		Connection connection = null;
		PreparedStatement psMent = null;
		try {
			connection = getConnection();
			boolean autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			psMent = connection.prepareStatement(sql);
			if(logger.isInfoEnabled()) {
				logger.info("operationSql = "+sql);
			}
			psMent.setString(1, sysConfig.getNodeName());
			psMent.setInt(2, sysConfig.getStart());
			if(sysConfig.getLastUpdateTime() != null) {
				psMent.setObject(3, sysConfig.getLastUpdateTime());
			} else {
				psMent.setObject(3, new Date());
			}
			changeCount = psMent.executeUpdate();
			connection.commit();
			connection.setAutoCommit(autoCommit);
		} catch (Exception e) {
			connection.rollback();
			throw new RuntimeException(e);
		} finally {
			if(psMent != null) {
				psMent.close();
			}
			if(connection != null) {
				connection.close();
			}
		}
		return changeCount;
	}
	
	@Override
	public SysStatusConfig getSysConfig(String sysName) throws Exception {
		SysStatusConfig sysConfig = null;
		Connection connection = null;
		PreparedStatement psMent = null;
		try {
			connection = getConnection();
			String sql = "select ID,NODENAME,START,LASTUPDATETIME from DEP_SYNCCONFIG where NODENAME=?";
			boolean autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			psMent = connection.prepareStatement(sql);
			if(logger.isInfoEnabled()) {
				logger.info("operationSql = "+sql);
			}
			psMent.setString(1, sysName);
			ResultSet rs = psMent.executeQuery();
			while(rs.next()) {
				sysConfig = new SysStatusConfig();
				sysConfig.setId(rs.getInt("ID"));
				sysConfig.setNodeName("NODENAME");
				sysConfig.setStart(rs.getInt("START"));
				sysConfig.setLastUpdateTime(rs.getDate("LASTUPDATETIME"));
			}
			connection.commit();
			connection.setAutoCommit(autoCommit);
		} catch (Exception e) {
			connection.rollback();
			throw new RuntimeException(e);
		} finally {
			if(psMent != null) {
				psMent.close();
			}
			if(connection != null ) {
				connection.close();
			}
		}
		return sysConfig;
	}
	
	@Override
	public int updateNodeInfo(DEPNode node,Date updateTime) throws Exception {
		String sql = "update DEP_SYNCCONFIG set START=?,LASTUPDATETIME=? where NODENAME=?";
		int changeCount = 0;
		Connection connection = null;
		PreparedStatement psMent = null;
		try {
			connection = getConnection();
			boolean autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			psMent = connection.prepareStatement(sql);
			if(logger.isInfoEnabled()) {
				logger.info("operationSql = "+sql);
			}
			psMent.setInt(1, node.getStatusTag());
			psMent.setObject(2, updateTime);
			psMent.setString(3, node.getName());
			changeCount = psMent.executeUpdate();
			connection.commit();
			connection.setAutoCommit(autoCommit);
		} catch (Exception e) {
			connection.rollback();
			throw new RuntimeException(e);
		} finally {
			if(psMent != null) {
				psMent.close();
			}
			if(connection != null) {
				connection.close();
			}
		}
		return changeCount;
	}
	
	public Connection getConnection() throws Exception {
		return dataSource.getConnection();
	}
	
	public String getInsertSql(List<Object> paramList,DepJobConfig jobConfig) {  //获取到插入语句
		StringBuffer sqlBuffer = new StringBuffer();
		StringBuffer columnBuffer = new StringBuffer();
		StringBuffer valueBuffer = new StringBuffer();
		columnBuffer.append("serverId,name,jobConfig,schedule,jarPath,jobClassName,start");  //必须要插入的数据
		paramList.add(jobConfig.getName());
		paramList.add(jobConfig.getName());
		paramList.add(jobConfig.getJobConfig());
		paramList.add(jobConfig.getSchedule());
		paramList.add(jobConfig.getJarPath());
		paramList.add(jobConfig.getJobClassName());
		paramList.add(jobConfig.getStart());
		valueBuffer.append("?,?,?,?,?,?,?");
		if(!CommonMethod.isStrNullOrBlank(jobConfig.getTag())) {
			columnBuffer.append(",tag");
			valueBuffer.append(",?");
			paramList.add(jobConfig.getTag());
		}
		if(!CommonMethod.isStrNullOrBlank(jobConfig.getType())) {
			columnBuffer.append(",type");
			valueBuffer.append(",?");
			paramList.add(jobConfig.getType());
		}
		sqlBuffer.append("insert into DEP_JOBCONFIG(")
		         .append(columnBuffer.toString())
		         .append(") values(")
		         .append(valueBuffer.toString())
		         .append(")");
		return sqlBuffer.toString();
	}
	
	public String getUpdateSql(List<Object> paramList,DepJobConfig jobConfig) {  //获取到修改语句
		StringBuffer sqlBuf = new StringBuffer();
		StringBuffer setBuf = new StringBuffer();
		setBuf.append(" jobConfig=?,schedule=?,jobClassName=?,start=?");
		paramList.add(jobConfig.getJobConfig());
		paramList.add(jobConfig.getSchedule());
		paramList.add(jobConfig.getJobClassName());
		paramList.add(jobConfig.getStart());
		if(!CommonMethod.isStrNullOrBlank(jobConfig.getJarPath())) {
			setBuf.append(",jarPath=? ");
			paramList.add(jobConfig.getJarPath());
		}
		if(jobConfig.getTag() != null) {
			setBuf.append(",tag=?");
			paramList.add(jobConfig.getTag());
		} else {
			setBuf.append(",tag=null");
		}
		if(jobConfig.getType() != null) {
			setBuf.append(",type=?");
			paramList.add(jobConfig.getType());
		} else {
			setBuf.append(",type=null");
		}
		sqlBuf.append(" update DEP_JOBCONFIG set ")
			  .append(setBuf.toString())
			  .append(" where name = ? ");
		paramList.add(jobConfig.getName());
		return sqlBuf.toString();
	}

}

