package kde.jobcontainer.dep.exchange.util;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;

import kde.jobcontainer.dep.exchange.dao.DataAccessDao;
import kde.jobcontainer.dep.exchange.dao.base.BaseAccessDao;
import kde.jobcontainer.dep.exchange.service.DataDealService;
import kde.jobcontainer.dep.exchange.service.SysConfigService;
import kde.jobcontainer.util.domain.DbConfig;

/**
 * @author xxxxxxxx
 * @date 2014-9-21, 下午9:17:58
 * 类生成器
 */
public class ClassFactory {
	
	public static BaseAccessDao getDefaultAccessDao(DataSource dataSource) {
		BaseAccessDao dao = new DataAccessDao();
		dao.init(dataSource);
		return dao;
	}
	
	/**
	 * 得到mq数据处理service
	 * @return
	 * @throws Exception
	 */
	public static DataDealService getDefaultDealService() throws Exception {
		DataDealService service = null;
		DataSource dataSource = CommonDataSource.getInstance().getDataSource();
		BaseAccessDao dao = new DataAccessDao();
		dao.init(dataSource);
		service = new DataDealService(dao);
		return service;
	}
	
	/**
	 * 得到系统配置读取类
	 * @return
	 * @throws Exception
	 */
	public static SysConfigService getConfigService() throws Exception {
		SysConfigService service = null;
		DataSource dataSource = CommonDataSource.getInstance().getDataSource();
		BaseAccessDao dao = new DataAccessDao();
		dao.init(dataSource);
		service = new SysConfigService(dao);
		return service;
	}
	
	/**
	 * 获取一个配置的dataSource
	 * @return
	 * @throws Exception
	 */
	public static DataSource getCommonDataSource(DbConfig dbConfig) throws Exception {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(dbConfig.getUrl());
		dataSource.setDriverClassName(dbConfig.getDriver());
		if(!CommonMethod.isStrNullOrBlank(dbConfig.getUsername())) {
			dataSource.setUsername(dbConfig.getUsername());
		}
		if(!CommonMethod.isStrNullOrBlank(dbConfig.getUserpwd())) {
			dataSource.setPassword(dbConfig.getUserpwd());
		}
		return dataSource;
	}
}

