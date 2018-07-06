package kde.jobcontainer.dep;

import java.io.InputStream;
import java.util.Properties;

import kde.jobcontainer.dep.manager.ScheduleManager;

public class PlatformUtil {
	public static ScheduleManager getScheduleManager(){
		return Main.sm;
	}
	
	public static Properties getPropertites() throws Exception{
		//InputStream in = Main.class.getClassLoader().getResourceAsStream("config_gatherWeatherPic.xml");
		//InputStream in = Main.class.getClassLoader().getResourceAsStream("config_hyStationDataCheck.xml");
		InputStream in = Main.class.getClassLoader().getResourceAsStream("config.xml");
		Properties p = new Properties();
		p.loadFromXML( in );
		return p;
	}
	
}
