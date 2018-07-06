package kde.jobcontainer.util.abstracts;

import java.util.Observable;
import java.util.Observer;

public abstract class JobConfigObserverAbstract implements Observer {

	public abstract void update(Observable o, Object arg);

}
