package kde.jobcontainer.util.utils.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kde.jobcontainer.util.domain.data.FieldAnnotation;
import kde.jobcontainer.util.domain.data.TableAnnotation;

/**
 * <pre>
 * 	http://blog.csdn.net/leon_ice/article/details/7537517
 * </pre>
 * @ClassName: CreateSqlTools
 * @Description: TODO(根据实体类对象生成SQL语句)
 * @author LiYang
 * @date 2012-5-4 下午10:07:03
 * 
 */
public class CreateSqlTools{

	/**
	 * 
	 * @Title: getTableName
	 * @Description: TODO(获取表名)
	 * @param @param obj
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	private static String getTableName(Object obj) {
		String tableName = null;
		if (obj.getClass().isAnnotationPresent(TableAnnotation.class)) {
			tableName = obj.getClass().getAnnotation(TableAnnotation.class)
					.tableName();
		}
		return tableName;
	}

	/**
	 * 
	 * @Title: getAnnoFieldList
	 * @Description: TODO(获取所有有注释的字段,支持多重继承)
	 * @param @param obj
	 * @param @return 设定文件
	 * @return List<Field> 返回类型
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	private static List<Field> getAnnoFieldList(Object obj) {
		List<Field> list = new ArrayList<Field>();
		Class superClass = obj.getClass().getSuperclass();
		while (true) {
			if (superClass != null) {
				Field[] superFields = superClass.getDeclaredFields();
				if (superFields != null && superFields.length > 0) {
					for (Field field : superFields) {
						if (field.isAnnotationPresent(FieldAnnotation.class)) {
							list.add(field);
						}
					}
				}
				superClass = superClass.getSuperclass();
			} else {
				break;
			}
		}
		Field[] objFields = obj.getClass().getDeclaredFields();
		if (objFields != null && objFields.length > 0) {
			for (Field field : objFields) {
				if (field.isAnnotationPresent(FieldAnnotation.class)) {
					list.add(field);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @Title: getFieldValue
	 * @Description: TODO(获取字段的值,支持多重继承)
	 * @param @param obj
	 * @param @param field
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes" })
	private static String getFieldValue(Object obj, Field field) {
		String value = null;
		String name = field.getName();
		String methodName = "get" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		Method method = null;
		Object methodValue = null;
		try {
			method = obj.getClass().getMethod(methodName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// 异常，抛出？
			throw new RuntimeException(e);
		}
		if (method != null) {
			try {
				methodValue = method.invoke(obj);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// 异常，抛出？
				throw new RuntimeException(e);
			}
			if (methodValue != null) {
				value = methodValue.toString();
			} else {
				Class objSuperClass = obj.getClass().getSuperclass();
				while (true) {
					if (objSuperClass != null) {
						try {
							methodValue = method.invoke(objSuperClass);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							// 异常，抛出？
							throw new RuntimeException(e);
						}
						if (methodValue != null) {
							value = methodValue.toString();
							break;
						} else {
							objSuperClass = objSuperClass.getSuperclass();
						}
					} else {
						break;
					}
				}
			}
		}
		return value;
	}

	/**
	 * 
	 * @Title: getInsertSql
	 * @Description: TODO(根据实体类对象字段的值生成INSERT SQL语句,可选固定参数)
	 * @param @param obj
	 * @param @param fixedParams
	 *        固定参数(如该参数与实体类中有相同的字段,则忽略实体类中的对应字段,HashMap<String
	 *        ,String>,key=指定字段名,value=对应字段的值)
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String getInsertSql(Object obj,
			HashMap<String, String> fixedParams) {
		String insertSql = null;
		String tableName = getTableName(obj);
		if (tableName != null) {
			StringBuffer sqlStr = new StringBuffer("INSERT INTO ");
			StringBuffer valueStr = new StringBuffer(" VALUES (");
			List<Field> annoFieldList = getAnnoFieldList(obj);
			if (annoFieldList != null && annoFieldList.size() > 0) {
				sqlStr.append(tableName + " (");
				if (fixedParams != null && fixedParams.size() > 0) {
					Iterator<String> keyNames = fixedParams.keySet().iterator();
					while (keyNames.hasNext()) {
						String keyName = (String) keyNames.next();
						sqlStr.append(keyName + ",");
						valueStr.append(fixedParams.get(keyName) + ",");
					}
				}
				for (Field field : annoFieldList) {
					FieldAnnotation anno = field
							.getAnnotation(FieldAnnotation.class);
					if (!anno.pk()) {
						Object fieldValue = getFieldValue(obj, field);
						if (fieldValue != null) {
							if (fixedParams != null && fixedParams.size() > 0) {
								Iterator<String> keyNames = fixedParams
										.keySet().iterator();
								boolean nextFieldFlag = false;
								while (keyNames.hasNext()) {
									String keyName = (String) keyNames.next();
									if (anno.fieldName().equals(keyName)) {
										nextFieldFlag = true;
										break;
									}
								}
								if (nextFieldFlag) {
									break;
								}
							}
							sqlStr.append(anno.fieldName() + ",");
							switch (anno.fieldType()) {
							case NUMBER:
								valueStr.append(fieldValue + ",");
								break;
							default:
								valueStr.append("'" + fieldValue + "',");
								break;
							}
						}
					}
				}
				insertSql = sqlStr.toString().substring(0, sqlStr.length() - 1)
						+ ")"
						+ valueStr.toString().substring(0,
								valueStr.length() - 1) + ")";
			}
		}
		return insertSql;
	}

	/**
	 * 
	 * @Title: getInsertSql
	 * @Description: TODO(根据实体类对象字段的值生成INSERT SQL语句)
	 * @param @param obj
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String getInsertSql(Object obj) {
		return getInsertSql(obj, null);
	}

	/**
	 * 
	 * @Title: getUpdateSql
	 * @Description: TODO(根据实体类对象字段的值生成UPDATE SQL语句,可选更新条件为主键,可选固定更新参数)
	 * @param @param obj
	 * @param @param reqPk 是否指定更新条件为主键(true=是,false=否)
	 * @param @param fixedParams
	 *        固定参数(如该参数与实体类中有相同的字段,则忽略实体类中的对应字段,HashMap<String
	 *        ,String>,key=指定字段名,value=对应字段的值)
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String getUpdateSql(Object obj, boolean reqPk,
			HashMap<String, String> fixedParams) {
		String updateSql = null;
		String tableName = getTableName(obj);
		if (tableName != null) {
			List<Field> annoFieldList = getAnnoFieldList(obj);
			if (annoFieldList != null && annoFieldList.size() > 0) {
				StringBuffer sqlStr = new StringBuffer("UPDATE " + tableName);
				StringBuffer valueStr = new StringBuffer(" SET ");
				String whereStr = " WHERE ";
				if (fixedParams != null && fixedParams.size() > 0) {
					Iterator<String> keyNames = fixedParams.keySet().iterator();
					while (keyNames.hasNext()) {
						String keyName = (String) keyNames.next();
						valueStr.append(keyName + "="
								+ fixedParams.get(keyName) + ",");
					}
				}
				for (Field field : annoFieldList) {
					String fieldValue = getFieldValue(obj, field);
					if (fieldValue != null) {
						FieldAnnotation anno = field
								.getAnnotation(FieldAnnotation.class);
						if (!anno.pk()) {
							if (fixedParams != null && fixedParams.size() > 0) {
								boolean nextFieldFlag = false;
								Iterator<String> keyNames = fixedParams
										.keySet().iterator();
								while (keyNames.hasNext()) {
									String keyName = (String) keyNames.next();
									if (anno.fieldName().equals(keyName)) {
										nextFieldFlag = true;
										break;
									}
								}
								if (nextFieldFlag) {
									break;
								}
							}
							valueStr.append(anno.fieldName() + "=");
							switch (anno.fieldType()) {
							case NUMBER:
								valueStr.append(fieldValue + ",");
								break;
							default:
								valueStr.append("'" + fieldValue + "',");
								break;
							}
						} else {
							if (reqPk) {
								whereStr += anno.fieldName() + "=" + fieldValue;
							}
						}
					}
				}
				updateSql = sqlStr.toString()
						+ valueStr.toString().substring(0,
								valueStr.length() - 1)
						+ (reqPk ? whereStr : "");
			}
		}
		return updateSql;
	}

	/**
	 * 
	 * @Title: getDeleteSql
	 * @Description: TODO(根据实体类对象字段的值生成有条件的DELETE
	 *               SQL语句，可选主键为删除条件或使用各个字段的值为条件，多个条件用AND连接)
	 * @param @param obj
	 * @param @param reqPk 是否指定更新条件为主键(true=是,false=否)
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String getDeleteSql(Object obj, boolean reqPk) {
		String deleteSql = null;
		String tableName = getTableName(obj);
		if (tableName != null) {
			StringBuffer delSqlBuffer = new StringBuffer("DELETE FROM ");
			List<Field> annoFieldList = getAnnoFieldList(obj);
			if (annoFieldList != null && annoFieldList.size() > 0) {
				delSqlBuffer.append(tableName + " WHERE ");
				for (Field field : annoFieldList) {
					if (reqPk) {
						FieldAnnotation anno = field
								.getAnnotation(FieldAnnotation.class);
						if (anno.pk()) {
							String fieldValue = getFieldValue(obj, field);
							delSqlBuffer.append(anno.fieldName() + "=");
							switch (anno.fieldType()) {
							case NUMBER:
								delSqlBuffer.append(fieldValue);
								break;
							default:
								delSqlBuffer.append("'" + fieldValue + "'");
								break;
							}
							break;
						}
					} else {
						String fieldValue = getFieldValue(obj, field);
						if (fieldValue != null) {
							FieldAnnotation anno = field
									.getAnnotation(FieldAnnotation.class);
							delSqlBuffer.append(anno.fieldName() + "=");
							switch (anno.fieldType()) {
							case NUMBER:
								delSqlBuffer.append(fieldValue + " AND ");
								break;
							default:
								delSqlBuffer
										.append("'" + fieldValue + "' AND ");
								break;
							}
						}
					}
				}
				if (reqPk) {
					deleteSql = delSqlBuffer.toString();
				} else {
					deleteSql = delSqlBuffer.toString().substring(0,
							delSqlBuffer.length() - 5);
				}
			}
		}
		return deleteSql;
	}

	/**
	 * 
	 * @Title: getDeleteSql
	 * @Description: TODO(根据实体类对象字段的值生成有条件的DELETE SQL语句，使用各个字段的值为条件，多个条件用AND连接)
	 * @param @param obj
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String getDeleteSql(Object obj) {
		return getDeleteSql(obj, false);
	}
	
	public static String getUpdateSql(Object obj) {  
        return getUpdateSql(obj, false, null);  
    }  
	
	 public static String getUpdateSql(Object obj, boolean reqPk) {  
	        return getUpdateSql(obj, reqPk, null);  
	    }  

	/**
	 * 
	 * @Title: getSelectAllSql
	 * @Description: TODO(根据实体类对象字段的值生成SELECT SQL语句,无查询条件)
	 * @param @param obj
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String getSelectAllSql(Object obj) {
		String selectSql = null;
		String tableName = getTableName(obj);
		if (tableName != null) {
			StringBuffer selectBuffer = new StringBuffer("SELECT ");
			List<Field> annoFieldList = getAnnoFieldList(obj);
			if (annoFieldList != null && annoFieldList.size() > 0) {
				for (Field field : annoFieldList) {
					FieldAnnotation anno = field
							.getAnnotation(FieldAnnotation.class);
					selectBuffer.append(anno.fieldName() + ",");
				}
				selectSql = selectBuffer.toString().substring(0,
						selectBuffer.length() - 1)
						+ " FROM " + tableName;
			}
		}
		return selectSql;
	}
	
	public static String getSelectOneSql(Object obj) {  
        String selectSql = null;  
        String tableName = getTableName(obj);  
        if (tableName != null) {  
            StringBuffer selectBuffer = new StringBuffer("SELECT ");  
            List<Field> annoFieldList = getAnnoFieldList(obj);  
            if (annoFieldList != null && annoFieldList.size() > 0) {  
                for (Field field : annoFieldList) {  
                    FieldAnnotation anno = field  
                            .getAnnotation(FieldAnnotation.class);  
                    selectBuffer.append(anno.fieldName() + ",");  
                }  
                selectSql = selectBuffer.toString().substring(0,  
                        selectBuffer.length() - 1)  
                        + " FROM " + tableName + " where ";  
            }
            for (Field field : annoFieldList) {
					FieldAnnotation anno = field
							.getAnnotation(FieldAnnotation.class);
					if (anno.pk()) {
						String fieldValue = getFieldValue(obj, field);
						selectSql+=anno.fieldName() + "=";
						switch (anno.fieldType()) {
						case NUMBER:
							selectSql+=(fieldValue);
							break;
						default:
							selectSql+=("'" + fieldValue + "'");
							break;
						}
						break;
					}
			}
        }  
        return selectSql;  
    }  
}
