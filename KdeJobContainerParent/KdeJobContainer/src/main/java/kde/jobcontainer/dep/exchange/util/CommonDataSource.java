package kde.jobcontainer.dep.exchange.util;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.util.domain.DbConfig;

/**
 * @author xxxxxxxx
 * @date 2014-9-29, 上午9:16:41
 * 默认、系统使用的datasource获取类
 */
public class CommonDataSource {
	private static final Logger logger = LoggerFactory.getLogger( CommonDataSource.class );
	private CommonDataSource() {
		defaultInit();
	}
	
	private static CommonDataSource instance;
	
	private DataSource dataSource;
	
	public static CommonDataSource getInstance() {
		if(instance == null) {
			instance = new CommonDataSource();
		} 
		return instance;
	}
	
	public void defaultInit() {
		try {
			ConfigUtil configUtil = ConfigUtil.getInstance();
			DbConfig dbConfig = configUtil.getDbConfig();
			dataSource = ClassFactory.getCommonDataSource(dbConfig);
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public static void setInstance(CommonDataSource instance) {
		CommonDataSource.instance = instance;
	}
	
}

