package kde.jobcontainer.util.domain;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.util.utils.BeanUtil;
import kde.jobcontainer.util.utils.db.DbUtil;

public abstract class DataObj implements Serializable {
	public abstract String[] getPrimaryKeys();
	public abstract String[] getFileds();
	public abstract String getTableName();
	public abstract String notInsertField();
//	public abstract String getInsertSql();
//	public abstract String getUpdateSql();
//	public abstract String getDeleteSql();
	
	public String getDeleteSql(Object obj){
		DataObj o = (DataObj)obj;
		StringBuilder sb = new StringBuilder( "delete from " );
		sb.append( o.getTableName() ).append( " where 1=1 " );
		String[] pks = o.getPrimaryKeys();
		for(int i=0;i<pks.length;i++){
			sb.append( " and " ).append( pks[i] ).append( "=" )
				.append( DbUtil.getObjSQLValue(o, pks[i], "DerbyDbHelper")).append(" ");
		}
		return sb.toString();
	}
	public String getUpdateSql(Object obj){
		DataObj o = (DataObj)obj;
		StringBuilder sb = new StringBuilder(" update ").append( o.getTableName() ).append(" set ");
		String tmpStr = null;
		for(int i=0;i<o.getFileds().length;i++){
			tmpStr = o.getFileds()[i];
			sb.append( tmpStr ).append("=").append( DbUtil.getObjSQLValue( o , tmpStr, "DerbyDbHelper" ));
			sb.append( " ," );
		}
		if(sb.charAt( sb.length()-1 )==',')
			sb.deleteCharAt( sb.length()-1 );
		sb.append(" where 1=1 ");
		for(int i=0;i<o.getPrimaryKeys().length;i++){
			tmpStr = o.getPrimaryKeys()[i];
			sb.append(" and ").append( tmpStr ).append("=").append( DbUtil.getObjSQLValue( o , tmpStr, "DerbyDbHelper" ));
		}
		return sb.toString();
	}
	
	public String getInsertSql( Object obj ){
		DataObj o = (DataObj)obj;
		StringBuilder sb = new StringBuilder(" insert into ").append( o.getTableName() ).append(" ( ");
		StringBuilder values = new StringBuilder(" values(");
		String tmpStr = null;
		for(int i=0;i<o.getFileds().length;i++){
			tmpStr = o.getFileds()[i];
			if(o.notInsertField().indexOf( tmpStr )==-1){	//字段不在 不插入数据的字段中
				sb.append( tmpStr ).append(",");
				values.append( DbUtil.getObjSQLValue( o , tmpStr, "DerbyDbHelper") ).append( "," );
			}
		}
		for(int i=0;i<o.getPrimaryKeys().length;i++){
			tmpStr = o.getPrimaryKeys()[i];
			if(o.notInsertField().indexOf( tmpStr )==-1){	//字段不在 不插入数据的字段中
				sb.append( tmpStr ).append(",");
				values.append( DbUtil.getObjSQLValue( o , tmpStr, "DerbyDbHelper") ).append( "," );
			}
		}
		if(sb.charAt( sb.length()-1 )==',')
			sb.deleteCharAt( sb.length()-1 );
		if(values.charAt( values.length()-1 )==',')
			values.deleteCharAt( values.length()-1 );
		sb.append( " ) " ).append( values ).append( " ) " );
		
		return sb.toString();
	}
	
	protected void setValue(JSONObject vs,String[] ss,String name,Object obj) throws Exception{
		String propName = null;
		for(String tmpStr:ss){
			propName = name+"."+ tmpStr;
			if(vs.containsKey( propName )){
				BeanUtil.SetValueByPropertyName(tmpStr, new Object[]{vs.get( propName )}, obj);
			}
		}
	}
}




