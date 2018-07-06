package kde.jobcontainer.util.domain;

public class KeyValueBean {

	private Object key;
	private Object value;
	
	public KeyValueBean(){}
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public KeyValueBean( Object key,Object value )
	{
		this.key = key;
		this.value = value;
	}
}
