/**
 * 
 */
package kde.jobcontainer.util.utils;

import java.util.Comparator;

/**
 * @author Lidong
 * 2012-6-6下午03:39:28
 * <br>
 * 排序比较器对�?
 */
public class CompareComparatorImpl implements Comparator {
	private String filedName;
	private boolean asc;
	public CompareComparatorImpl(String filedName ){
		this.filedName = filedName;
		this.asc = true;
	} 
	public CompareComparatorImpl(String filedName ,boolean asc){
		this.filedName = filedName;
		this.asc = false;
	}
	
	public int innerCompare(Object o1,Object o2  ){
		//compareTo(T o)比较此对象与指定对象的顺序�?如果该对象小于�?等于或大于指定对象，则分别返回负整数、零或正整数�?
		int x = 0;
		if( o1==null&&o2!=null )
			x = -1;
		else if(o1==null&&o2==null)
			x = 0;
		else if(o1!=null&&o2==null)
			x = 1;
		else{
			Comparable v1 = (Comparable)BeanUtil.GetValueByPropertyName(this.filedName, o1);
			Comparable v2 = (Comparable)BeanUtil.GetValueByPropertyName(this.filedName, o2);
			if(v1==null&&v2==null)	x = 0;
			else if(v1!=null&&v2==null)	x = 1;
			else if(v1==null&&v2!=null)	x = -1;
			else x = v1.compareTo( v2 );
		}
		
		return x;
	}
	
	public int compare(Object o1, Object o2) {
		int x = this.innerCompare(o1, o2);
		if(asc)return x;
		else{
			return 0-x;
		}
	}
	public String getFiledName() {
		return filedName;
	}
	public void setFiledName(String filedName) {
		this.filedName = filedName;
	}
}
