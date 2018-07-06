package kde.jobcontainer.util.utils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kde.jobcontainer.util.utils.mq.MessageQueueUtils;



public class BeanUtil {
	public static String PACKAGE_MODEL = "com.richway.";
	public static String EmptyStr = "";
	private static final Logger logger = LoggerFactory.getLogger( BeanUtil.class );
	//主要供webservice的简单对象进行转换
	public static void copyPropertiesBewteenDiffClass(Object source,Object target){
		try{
			if(source==null||target==null){
				System.out.println("源或目标对象为空!");
				return;
			}
			Class sourceC = source.getClass();
			Class sourceT = target.getClass();
			if( sourceC == sourceT ){
				copyNotNullAndNotEmptyStrAndObjectProperties(source,target);
				return ;
			}
			Field[] sFs = sourceC.getDeclaredFields();
			Field[] tFs = sourceT.getDeclaredFields();
			Field tmpField = null;
			String fieldName = null;
			StringBuffer sb = new StringBuffer(",");
			for(int i=0;i<tFs.length;i++){
				tmpField = tFs[i];
				fieldName = tmpField.getName();
				if(fieldName.equals( "serialVersionUID" ))
					continue;
				else if(fieldName.indexOf("$")!=-1)
					continue;
				sb.append( fieldName ).append(",");
			}
			Method getMethod = null;
			Object getResult = null;
			
			for(int i=0;i<sFs.length;i++){
				tmpField = sFs[i];
				fieldName = tmpField.getName();
				if(fieldName.equals( "serialVersionUID" ))
					continue;
				else if(fieldName.indexOf("$")!=-1)
					continue;
				//检查目标对象中是否有该名称的属性
				if(sb.indexOf( ","+fieldName+"," )!=-1){
					//获取该字段的get方法
					getMethod = sourceC.getDeclaredMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), null);
					//获取该字段的值 
					//TODO 目前仅为时间类型字段
					getResult = getMethod.invoke(source, null);
					if ( getResult != null ) { //如果源对象中值不为空,则对目标对象操作
						Object[] obj = {getResult};
						SetValueByPropertyName( fieldName,obj,target );
					}
				}
			}
			
		}catch(Exception e){
			logger.error( e.getMessage() , e);
			return;
		}
	}
	
	
	public static void copyNotNullProperties(Object source, Object target) {
		try{
			if(source==null) {
				System.out.println( "源对象为空!" );
				return;
			}

			if (source.getClass() != target.getClass()){
				//处理 load出的类带有 __$$_javassist
				if( target.getClass().getName().indexOf( source.getClass().getName()+"_" )==-1 )
					throw new Exception("对象类型不相同!");
			}
			Class clazz = source.getClass();
			Field[] fields = clazz.getDeclaredFields();
			
			Field tmpField;
			for(int i = 0; i < fields.length; i++) {
				/*
				String fieldStr = fields[i].getName();
				Method setMethod = clazz.getDeclaredMethod("set" + fieldStr.substring(0, 1).toUpperCase() + fieldStr.substring(1), new Class[] {fields[i].getType()});
				Method getMethod = clazz.getDeclaredMethod("get" + fieldStr.substring(0, 1).toUpperCase() + fieldStr.substring(1), null);
				if (getMethod.invoke(source, null) != null) {
					setMethod.invoke(target, new Object[] {getMethod.invoke(source, null)});
				}
				*/
				tmpField = fields[i];
				String fieldStr = tmpField.getName();
				
				if(fieldStr.equals( "serialVersionUID" ))
					continue;
				else if(fieldStr.indexOf("$")!=-1)
					continue;
				Method getMethod = clazz.getDeclaredMethod("get" + fieldStr.substring(0, 1).toUpperCase() + fieldStr.substring(1), null);
				if (getMethod.invoke(source, null) != null) {
					tmpField.setAccessible(true);
					tmpField.set(target, tmpField.get(source));
				}
			}
		}catch(Exception e)
		{
			logger.error( e.getMessage() , e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public static void copyNotNullAndNotEmptyStrAndObjectProperties(Object source, Object target) {
		try{
			if(source==null) {
				System.out.println( "源对象为空!" );
				return;
			}
			Class sc = source.getClass();
			Class tc = target.getClass();
			String sn = sc.getName();
			String tn = tc.getName();
			if ( sc != tc ){
				//处理 load出的类带有 __$$_javassist
				if( tn.indexOf( sn+"_" )==-1&&tn.indexOf( sn+'$' )==-1 ){
					//System.out.println( target.getClass().getName()+"\t"+source.getClass().getName() );
					//com.richway.project.domain.Tb0002Dscdnm$$EnhancerByCGLIB$$de3b67ac	com.richway.project.domain.Tb0002Dscdnm
					throw new Exception("对象类型不相同!");
				}
			}
			//Class clazz = source.getClass();
			Field[] fields = sc.getDeclaredFields();
			
			Field tmpField;
			Object tmpObj = null;
			Object tmpObj2 = null;
			Method getMethod = null;
			Method setMethod = null;
			for(int i = 0; i < fields.length; i++) {
				tmpField = fields[i];
				String fieldStr = tmpField.getName();
				if(fieldStr.equals( "serialVersionUID" ))
					continue;
				else if(fieldStr.indexOf("$")!=-1)
					continue;
				getMethod = sc.getDeclaredMethod("get" + fieldStr.substring(0, 1).toUpperCase() + fieldStr.substring(1), null);

				tmpObj = getMethod.invoke(source, null);
				
				if (tmpObj != null) {
					if( tmpObj.getClass().getName().indexOf( PACKAGE_MODEL )!=-1 ){
						Object newPropertyObj = null;
						tmpObj2 = getMethod.invoke(target, null); 
						if( tmpObj2!=null ){
							newPropertyObj = tmpObj2;
						}else{
							tmpObj2 = tmpObj.getClass().newInstance();
							newPropertyObj = tmpObj2;
						}
						//tmpObj.getClass().newInstance();
						BeanUtil.copyNotNullAndNotEmptyStrAndObjectProperties(tmpObj, newPropertyObj);
						Class[] modelClassArr = { tmpObj.getClass() };
						setMethod= sc.getDeclaredMethod(("set"+fieldStr.substring(0, 1).toUpperCase() + fieldStr.substring(1)), modelClassArr  );
						Object[] tmpModelParam = {newPropertyObj };
						setMethod.invoke(target, tmpModelParam);
					}else if(!(tmpObj instanceof java.lang.String)||!EmptyStr.equals( tmpObj )){
						tmpField.setAccessible(true);
						tmpField.set(target, tmpField.get(source));
					}
				}
			}
		}catch(Exception e)
		{
			logger.error( e.getMessage() , e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	
	public static Method getMethodByName(String methodName,Class c)
	{
		Method[] ms = c.getDeclaredMethods();
		
		for(int i=0;i<ms.length;i++)
		{
			if(ms[i].getName().equals( methodName )){
				return ms[i];
			}
		}
		return null;
	}
	
	/**
	 * <pre>
	 * 获取集合类中对象的某系字段，组合成一个string
	 * </pre>
	 * @author Lidong 2014-4-15
	 * @param l
	 * @param methodName  如果为空的话则把整个对象进行toString
	 * @return
	 */
	public static String getAttributeStrs(Collection l,String methodName ){
		StringBuffer sb = new StringBuffer();
		if(l==null||l.size()==0)
			return BaseConstants.EMPTY_STR;
		try{
			Object obj;
			Method m;
			
			String tmpStr;
			if(methodName==null||BaseConstants.EMPTY_STR.equals( methodName )){
				//20140415 lidong 当没有参数的时候，直接使用集合中对象的toString
				for(Iterator it = l.iterator();it.hasNext();){
					obj = it.next();
					sb.append( obj.toString() ).append( "," );
				}
			}else{
				for(Iterator it = l.iterator();it.hasNext();){
					obj = it.next();
					m = obj.getClass().getDeclaredMethod(methodName, null);
					obj = m.invoke(obj, null);
					if(obj==null){
						tmpStr = BaseConstants.EMPTY_STR;
					}else
						tmpStr = obj.toString();
					sb.append(tmpStr ).append( (tmpStr==null||tmpStr.equals(EmptyStr)) ? EmptyStr:",");
				}
			}
			if(sb.length()>0&&sb.charAt(sb.length()-1)==',')
				sb.deleteCharAt(sb.length()-1);
		}catch(Exception e){
			logger.error( e.getMessage() , e);
			throw new RuntimeException(e.getMessage());
		}
		return sb.toString();
	}
	/**
	 * 通过对象和它的对象属性名名称获取对象实例,如果有值的话则返回对应的值,<br>
	 * 如果没有值的话,并且createNew为True的时候返回一个新的实例
	 * */
	public static Object getObjInstanceFromModelByPropertyName (Object model,String propertyName,boolean createNew) throws Exception{
		//通过方法获取目标 propertyName指定的属性的值
		Object oldObj = GetValueByPropertyName( propertyName,model );
		if(oldObj!=null)
			return oldObj;
		if(!createNew)	//如果 createNew参数为false的话,则直接返回null
			return null;
		//如果没有值的话,获取该字段的类型,生成一个新的
		String mName = "set"+Character.toUpperCase( propertyName.charAt(0))+propertyName.substring(1);
		Method m = getMethodByName( mName ,model.getClass() );
		if(m!=null){
			Class[] pc = m.getParameterTypes();
			if(pc!=null&&pc.length>0){
				//只根据set方法的第一个参数获取对象
				Object obj = null ;
				try{
					obj = pc[0].newInstance();
				}catch(Exception e){
					System.out.println( "*********getObjInstanceFromModelByPropertyName: "+model.getClass().getSimpleName()+'.'+propertyName+ "---" + pc[0].getName()+" 无法实例化!" );
				}
				return obj;
			}else{
				throw new Exception( "getObjInstanceFromModelByPropertyName set方法无参数:"+mName );
			}
		}else{
			
			throw new Exception( "getObjInstanceFromModelByPropertyName emptyMehod:"+mName );
		}
	}
	/**
	 * @param propertyName
	 * @param model
	 * @return 根据属性字段的名称设置对应的值<br>允许对 对象属性的属性值进行设定,如类TProject.id.ennmcd<br>
	 * 所传propertyName为id.ennmcd
	 */
	public static void SetValueByPropertyName(String propertyName,Object[] value,Object model) throws Exception{
		int idx = propertyName.indexOf(".");
		if( idx!=-1 ){
			//属性名称 整个propertyBName 用.隔开后的第一个属性
			String tmpStr = propertyName.substring(0,idx); 
			//得到对象
			Object obj = getObjInstanceFromModelByPropertyName(model,tmpStr,true);
			//放到原model中
			Object[] values = { obj }; 
			SetValueByPropertyName(tmpStr,values ,model );
			//继续对这个对象进行赋值
			SetValueByPropertyName(propertyName.substring(idx+1),value,obj );
		}else{
			Method m = getMethodByName("set"+Character.toUpperCase( propertyName.charAt(0))+propertyName.substring(1),model.getClass());
			if(m!=null)
			{
				Class cs = m.getParameterTypes()[0];
				//20120426 obj为 需要set到目标对象中的值
				Object obj = value.length>0?value[0]:null;
				if(obj!=null&&!obj.toString().trim().equals(EmptyStr)){
					if(!cs.equals( java.lang.String.class )){
						
						if(cs.equals( java.lang.Double.class )){
							Object[] values = {StringUtil.ConversionFromString(obj.toString())}; 
							value = values;
						}else if(cs.equals( java.lang.Integer.class )){
							Object[] values = { new Integer(StringUtil.ConversionFromString(obj.toString()).intValue())}; 
							value = values;
						}else if(cs.equals( java.util.Date.class )){
							//Object[] values = {DateUtil.StringToDateTime(obj.toString(), DateUtil.Date)}; //默认是天
							if(obj instanceof java.lang.String){
								Object[] values = {DateUtil.StringToDateTime(obj.toString())}; //自动匹配 ,不完整方法
								value = values;
							}else{
								Object[] values = { (Date)obj }; //自动匹配 ,不完整方法
								value = values;
							}
						}else if(cs.equals( java.sql.Timestamp.class )){
							//Object[] values = {new Timestamp(DateUtil.StringToDateTime(obj.toString(), DateUtil.Date).getTime())}; //默认是天
							Date d = null;
							if( obj instanceof java.lang.String ){
								d = DateUtil.StringToDateTime( (String)obj );
							}else if( obj instanceof java.util.Date ){
								d = (Date)obj;
							}
							if( d==null ){
								System.out.println( "*****************"+obj.toString()+"转化日期失败!" );
								return;
							}
							Object[] values = {new Timestamp(d.getTime())}; //自动匹配 ,不完整方法
							value = values;
						}else if(cs.equals( java.io.Serializable.class )){
							Object[] values = {(Serializable)obj}; 
							value = values;
						}else if( cs.equals( java.lang.Float.class )){
							Object[] values = { Float.valueOf( (String)obj )  }; 
							value = values;
						}else if( cs.equals( java.lang.Long.class )){
							Object[] values = { Long.valueOf( (String)obj )  }; 
							value = values;
						}else if(cs.equals( java.lang.Boolean.class )){
							Boolean bool=null;
							if("false".equals((String)obj)||"0".equals((String)obj)){
								bool=new Boolean(false);
							}
							if("true".equals((String)obj)||"1".equals((String)obj)){
								bool=new Boolean(true);
							}
							Object[] values =new Boolean[]{bool}; 
							value = values;
						}
					}
				}else{
					Object[] values={null};
					value = values;
				}

				m.invoke(model, value);
			}
		}
	}
	/**
	 * @param propertyName
	 * @param model
	 * @return 根据属性字段的名称获取对应的值<br>允许获取对象属性的属性值,如类TProject.id.ennmcd<br>
	 * 所传propertyName为id.ennmcd
	 */
	public static Object GetValueByPropertyName(String propertyName,Object model){
		if(model==null){
			return null;
		}
		int idx = propertyName.indexOf(".");
		if( idx!=-1){
			String tmpStr = propertyName.substring(0,idx); //获取 点之前的属性名
			Object o = GetValueByPropertyName( tmpStr,model ); //获取第一个属性名的对象
			tmpStr = propertyName.substring( idx+1 );	//获取剩余的属性
			return GetValueByPropertyName( tmpStr,o );	//继续对剩余的属性获取对象和值
		}else{
			Method m = getMethodByName("get"+Character.toUpperCase( propertyName.charAt(0))+propertyName.substring(1),model.getClass());
			try{
				if(m!=null)
				{
					Object[] values = {};
					return m.invoke(model, values);
				}else{
					return null;
				}
			}catch(Exception e){
				logger.error( e.getMessage() , e);
				return null;
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static void setModelValueByParameters(Object model,Map parametersMap,String modelName) throws Exception{
		String tmpStr;
		String[] value;
		int idx = -1;
		if(modelName!=null){
			for(Iterator<String> it = parametersMap.keySet().iterator();it.hasNext();){
				tmpStr = it.next();
				idx = tmpStr.indexOf('.');
				if(tmpStr.indexOf(modelName)==-1)
					continue;
				if(idx!=-1){
					value = (String[])parametersMap.get( tmpStr );
					//传过去的 tmpStr 是去掉对象名的属性名成了,如果属性仍为对象,需要处理
					SetValueByPropertyName( tmpStr.substring( idx+1 ),value,model );
				}
			}
		}else{
			for(Iterator<String> it = parametersMap.keySet().iterator();it.hasNext();){
				tmpStr = it.next();
				idx = tmpStr.indexOf('.');
				if(idx!=-1){
					value = (String[])parametersMap.get( tmpStr );
					SetValueByPropertyName( tmpStr.substring( idx+1 ),value,model );
				}
			}
		}
	}
	
	/**
	 * 20120606 lidong<br>
	 * 根据指定的字段生产比较器,对序列进行排序
	 * @param l
	 * @param field
	 * @param asc
	 * @return
	 */
	public static void sortList(List l,String field,boolean asc ){
		if(l==null||l.size()==0)
			return ;
		CompareComparatorImpl comparator = new CompareComparatorImpl( field ,asc);
		Collections.sort( l , comparator);
	}
	
	
	
}
