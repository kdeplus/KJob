package kde.jobcontainer.dep;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import kde.jobcontainer.dep.manager.ScheduleManager;

public class PlatformUtil {
	public static ScheduleManager getScheduleManager(){
		return Main.sm;
	}
	
	public static Properties getPropertites() throws Exception{
		//本地调试用
		//InputStream in = Main.class.getClassLoader().getResourceAsStream("config_gatherWeatherPic.xml");
		//InputStream in = Main.class.getClassLoader().getResourceAsStream("config_hyStationDataCheck.xml");
		//InputStream in = Main.class.getClassLoader().getResourceAsStream("config.xml");
		//打包发布用
		PropertyConfigurator.configure(System.getProperty("user.dir") + "\\config\\log4j.properties");
	    File file = new File(System.getProperty("user.dir") + "\\config\\config.xml");
	    InputStream in = new FileInputStream(file);
	  
		Properties p = new Properties();
		p.loadFromXML( in );
		return p;
	}
	
}
