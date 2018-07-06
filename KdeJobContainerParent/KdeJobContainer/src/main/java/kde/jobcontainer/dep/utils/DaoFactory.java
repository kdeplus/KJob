package  kde.jobcontainer.dep.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DaoFactory {
	
	public static Logger logger = LoggerFactory.getLogger(  DaoFactory.class );
	public static <T> T getInstance(String c) {
		
		try {
			return (T) Class.forName(c).newInstance();
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
	}
	
}


