package kde.jobcontainer.dep.communication.action;

import kde.jobcontainer.dataSync.policy.PolicyRequest;
import kde.jobcontainer.dataSync.policy.PolicyResponse;
import kde.jobcontainer.dep.communication.action.base.BaseAction;
import kde.jobcontainer.dep.exchange.util.ConfigUtil;

/**
 * @author xxxxxxxx
 * @date 2014-9-21, 下午6:55:25
 * 初始化本节点对象，注册工作action
 */
public class NodeInitialAction extends BaseAction {

	@Override
	public PolicyResponse executeSome(PolicyRequest request) throws Exception {
		ConfigUtil configs = ConfigUtil.getInstance();
		String syncName = configs.getDataSyncName();    //得到本同步配置名
		
		return null;
	}

}

