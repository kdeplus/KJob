package kde.jobcontainer.util.utils.db;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class JsonToSqlTool {
	/*
	{"rsvr.stcd":"30559804","rsvr.tm":"2014-08-01 08:00","pptn.stcd":"30559804","pptn.tm":"2014-08-01 08:00","pptn.intv":6,
	"pptn.drp":"10","pptn.dyp":"24.5","pptn.wth":"8","rsvr.wth":"8","rsvr.r":"1500","rsvr.inq":"308","rsvr.rz":"318.05",
	"rsvr.otq":"401","rsvr.rwptn":"4"}
	{name:'rsvr',table:'ST_RSVR_R',fields:[
		{'keyName':'stcd','fieldName':'stcd',pk:'true',type:'String'},
		{'keyName':'tm','fieldName':'tm',pk:'true',type:'Date'},
		{'keyName':'rz','fieldName':'rz',pk:'false',type:'Double'},
		{'keyName':'inq','fieldName':'inq',pk:'false',type:'Double'},
		{'keyName':'w','fieldName':'w',pk:'false',type:'Double'},
		{'keyName':'blrz','fieldName':'blrz',pk:'false',type:'Double'},
		{'keyName':'otq','fieldName':'otq',pk:'false',type:'Double'},
		{'keyName':'inqdr','fieldName':'inqdr',pk:'false',type:'Double'},
		{'keyName':'rwchrcd','fieldName':'rwchrcd',pk:'false',type:'String'},
		{'keyName':'rwptn','fieldName':'rwptn',pk:'false',type:'String'},
		{'keyName':'msqmt','fieldName':'msqmt',pk:'false',type:'String'}
	]}
	*/
	/**
	 * <pre>
	 *  根据json中的数据以及表的转换配置，将json对象转换为插入语句
	 * </pre>
	 * @author Lidong 2014-4-16
	 * @param json	数据对象
	 * @param cfgs	针对多个表的表属性和字段的配置
	 * @param dbTypeHelper	对应数据库的相关处理类
	 * @return
	 */
	public static List<String> getInsertSqlFromJson(JSONObject json,JSONArray cfgs,String dbTypeHelper){
		List<String> sqls = new ArrayList<String>();
		JSONObject tbl = null;
		StringBuilder sb = null;
		StringBuilder sb2 = null;
		JSONArray arr = null;
		JSONObject fieldObj = null;
		for(int i=0;i<cfgs.size();i++){
			tbl = cfgs.getJSONObject(i);
			sb = new StringBuilder(" insert into  ").append( tbl.getString("table") ).append( " (" ) ;
			sb2 = new StringBuilder(" values(");
			arr = tbl.getJSONArray( "fields" );
			for(int m=0;m<arr.size();m++){
				fieldObj = arr.getJSONObject(m);
				sb.append( fieldObj.getString( "fieldName" ) ).append( ',');
				sb2.append( 
					DbUtil.getJsonSQLValue( json , tbl.getString("name")+'.'+fieldObj.getString("keyName"), fieldObj.getString("type"), dbTypeHelper)
				).append( ',' );
			}
			if(sb.charAt( sb.length()-1 )==',')
				sb.deleteCharAt( sb.length()-1 );
			if(sb2.charAt( sb2.length()-1 )==',')
				sb2.deleteCharAt( sb2.length()-1 );
			sb.append( " ) " ).append( sb2 ).append( " ) " );
			sqls.add( sb.toString() );
		}
		
		return sqls;
	}
}
