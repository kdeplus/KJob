package kde.jobcontainer.util.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kde.jobcontainer.util.domain.DEPJobConfig;

public class JobConfigUtil {
	public static Map<String,DEPJobConfig> getConfigMap(List<DEPJobConfig> cfgs){
		Map<String,DEPJobConfig> m = new HashMap<String,DEPJobConfig>();
		if(cfgs==null||cfgs.size()==0)
			return m;
		DEPJobConfig c = null;
		for(Iterator<DEPJobConfig> it=cfgs.iterator();it.hasNext();){
			c = it.next();
			m.put(c.getServerId(), c);
		}
		return m;
	}
}
