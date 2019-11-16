package kde.jobcontainer.job.synchydro.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kde.jobcontainer.job.synchydro.domain.HYITSTriggerRecordSyncConfig;
import kde.jobcontainer.job.synchydro.domain.TriggerRecord;
import kde.jobcontainer.util.domain.DEPJobConfig;
import kde.jobcontainer.util.domain.DbConfig;
import kde.jobcontainer.util.utils.DateUtil;

public class HYITSTriggerRecordSyncToGDService extends HYITSTriggerRecordSyncService {
	public static final Logger logger = LoggerFactory.getLogger( HYITSTriggerRecordSyncToGDService.class );
	
	private static HYITSTriggerRecordSyncToGDService _instance;
	
	public static HYITSTriggerRecordSyncToGDService getInstance(){	//单例方法
		if(_instance==null)
			_instance = new HYITSTriggerRecordSyncToGDService();
		return _instance; 
	}
	/*
	 {
		    "adcd":"440101",//以区县为级别进行上报,如数据中为更大区域，也需要拆分为各个区县进行上报，防止一次上报的数据量过大
		    "sysid":"gzsys",//对方系统的标识，需向省级系统申请，提供服务器ip，生成token，反馈给对接系统
		    "token":"hjGDKjfm3sadfkjsadRfkjsam5MRFKbn23HHFjlks1dfsYTw", //申请对接时提供的账户
		    "time":"2019-07-01 18:01:21",//本次报文时间
		    "dataType":"RTD",//
		    "datas":{
		        "insert":{
		            "pptn":[{"stcd":"87654321","tm":"2019-09-01 08:00","drp":10.0,"intv":1.00,"pdr":2.5,"dyp":24.5,"wth":7},{},{}],
		            "rsvr":[{"stcd":"87654321","tm":"2019-09-01 08:00","rz":10.0,"inq":1.00,"w":2.5,"blrz":24.5,"otq":24.5,"rwchrcd":5,"rwptn":5,"inqdr":1.5,"msqmt":4},{},{}],
		            "picture":[{"stcd":"87654321","tm":"2019-09-01 08:00","picType":"png","picName":"2019090108309932.png","picData":"/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL"},{
		                "stcd":"87654321","tm":"2019-09-01 08:00","picType":"png","picName":"2019090108309932.png","picPath":"http://sk.digitwater.com/v4/../skjgimages/2019/1102/90012833/20191102180612.jpg"
		            },{}],
		            "seepage":[{"stcd":"87654321","tm":"2019-09-01 08:00","q":10.34,"z":109.3,"p":13.2},{},{}] //MPa
		        },
		        "delete":{
		            "pptn":[{"stcd":"87654321","tm":"2019-09-01 08:00"},{},{}],
		            "rsvr":[{"stcd":"87654321","tm":"2019-09-01 08:00"},{},{}],
		            "picture":[{"stcd":"87654321","tm":"2019-09-01 08:00"},{},{}],
		            "seepage":[{"stcd":"87654321","tm":"2019-09-01 08:00"},{},{}] //MPa
		        },
		        "update":{
		            "pptn":[{"stcd":"87654321","tm":"2019-09-01 08:00","drp":10.0,"intv":1.00,"pdr":2.5,"dyp":24.5,"wth":7},{},{}],
		            "rsvr":[{"stcd":"87654321","tm":"2019-09-01 08:00","rz":10.0,"inq":1.00,"w":2.5,"blrz":24.5,"otq":24.5,"rwchrcd":5,"rwptn":5,"inqdr":1.5,"msqmt":4},{},{}],
		            "picture":[{"stcd":"87654321","tm":"2019-09-01 08:00","picType":"png","picName":"2019090108309932.png","picData":"/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL"},{},{}],
		            "seepage":[{"stcd":"87654321","tm":"2019-09-01 08:00","q":10.34,"z":109.3,"p":13.2},{},{}] //MPa
		        }
		    }
		}
	*/
	private String[] operations_json = {"insert","delete","update"};//对应报文操作类型
	private String[] operations_key = {"I","D","U"};//对应触发器操作类型
	private String[] tables_json = {"pptn","rsvr","picture","seepage"};//对应报文数据类型
	private String[] tables_key = {"ST_PPTN_R","ST_RSVR_R","ST_PICTURE_R","ST_SEEPAGEFLOW_R"}; //对应数据库表类型
	
	/* 
	 * 使用报文的方式同步有关数据
	 * (non-Javadoc)
	 * @see kde.jobcontainer.job.synchydro.service.HYITSTriggerRecordSyncService#syncRecord(com.alibaba.fastjson.JSONArray, kde.jobcontainer.util.domain.DEPJobConfig)
	 */
	@Override
	public void syncRecord(JSONArray arr, DEPJobConfig depConfig) throws Exception {
		if(arr==null||arr.size()==0) {
			logger.info("查询得到的数据集为空，不进行同步操作");
			return;
		}
		
		//1、构造报文，添加验证信息等
		JSONObject rpt = this.getBaseJson(depConfig);
		//2、构造数据有关内容
		JSONObject datas = this.getDatasJson(arr,depConfig);
		if(datas!=null&&datas.keySet().size()>0){
			//判断数据不为空，放到外部的封装中，然后执行
			rpt.put("datas", datas);
			//3、发送报文
			this.sendRpt( rpt , depConfig );
		}
	}

	/**
	 * 提供一个最外层认证信息等的封装
	 * @return
	 */
	public JSONObject getBaseJson(DEPJobConfig depConfig) {
		JSONObject rpt = new JSONObject();
		rpt.put("adcd", "440000");
		rpt.put("sysid", "shuikuguanjia");
		rpt.put("token", "440000");
		rpt.put("time", DateUtil.converDateToString( new Date() ));
		rpt.put("dataType", GDSkgjConstants.DATATYPE_RTD);
		
		return rpt;
	}
	
	/**
	 * 发送报文到服务器
	 * @param rpt
	 * @throws Exception
	 */
	public void sendRpt(JSONObject rpt,DEPJobConfig dep) throws Exception{
		JSONObject config = dep.getJobConfigJson();
		String url = config.getString("sendToUrl");
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		
		try {
			httpClient = HttpClients.createDefault();
			URIBuilder uriBuilder = new URIBuilder(url);
			HttpPost post = new HttpPost(uriBuilder.build());
			/*gzip 压缩
			ByteArrayOutputStream originalContent = new ByteArrayOutputStream();

			originalContent.write(rpt.toJSONString().getBytes(Charset.forName("UTF-8")));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			GZIPOutputStream gzipOut = new GZIPOutputStream(baos);

			originalContent.writeTo(gzipOut);

			
			post.setEntity(new ByteArrayEntity(baos.toByteArray()));*/
		
			post.setEntity( new StringEntity(rpt.toJSONString(),"utf-8") );
			//执行请求
			response =httpClient.execute(post);
	
			//取响应的结果
			int statusCode =response.getStatusLine().getStatusCode();
	
			logger.info(String.valueOf(statusCode));
			
			HttpEntity entity =response.getEntity();
	
			String string = EntityUtils.toString(entity,"utf-8");
			//TODO 进行后续处理，如解析数据，入库等
			logger.info(string);
			if(statusCode!=200) {
				throw new Exception("未能向服务器正常发送数据！返回http状态码为"+statusCode+"\t"+string);
			}
		}catch(Exception e) {
			logger.error( "发送报文时出现异常",e );
			//继续把异常跑出去
			throw e;
		}finally {
			if(response!=null)
				response.close();
			if(httpClient!=null)
				httpClient.close();
		}
		
	}
	
	/**
	 * 循环获取到各相关数据，进行分组和最终数据的拼接
	 * @param arr
	 * @return
	 */
	public JSONObject getDatasJson(JSONArray arr,DEPJobConfig depConfig)  throws Exception{
		JSONObject datas = new JSONObject();//保存最终的结果
		JSONObject tmpJson = null;
		TriggerRecord tmpRec = null;
		Map<String,List<TriggerRecord>> datasMap = new HashMap<String,List<TriggerRecord>>();
		List<TriggerRecord> tmpList = null;
		String tmpStr = null;
		for(int i=0;i<arr.size();i++) {
			tmpJson = arr.getJSONObject(i);
			tmpRec = (TriggerRecord)JSONObject.toJavaObject( tmpJson,TriggerRecord.class );
			//按操作类型和数据类别分组，这样循环多一些可能，但是程序更清楚
			tmpStr = tmpRec.getOPERATION()+"_"+tmpRec.getTABID();
			tmpList = datasMap.get( tmpStr );
			if(tmpList==null) {
				tmpList = new ArrayList<TriggerRecord>();
				datasMap.put(tmpStr, tmpList);
			}
			tmpList.add( tmpRec );
		}
		//将分组的数据拼到json中
		this.addRecToJSON( datas , datasMap,depConfig);
		return datas;
	}
	
	/**
	 * 将已经分好组的数据，按操作类型和表生成报文
	 * @param datas 用来保存数据结果的JSON对象
	 * @param datasMap 已经按操作和表名进行分组的记录数据
	 */
	public void addRecToJSON( JSONObject datas,Map<String,List<TriggerRecord>> datasMap ,DEPJobConfig depConfig)  throws Exception{
		JSONArray toPutArr = null;//存某一类操作，某一张表对应的一组数据
		JSONObject operJson = null;
		List<TriggerRecord> tmpList = null;
		//循环操作类型
		for(int i=0;i<operations_json.length;i++) {
			operJson = new JSONObject();
			//循环数据类型
			for(int m=0;m<tables_json.length;m++) {
				tmpList = datasMap.get( operations_key[i]+"_"+tables_key[m] );
				if(tmpList==null||tmpList.size()==0) {
					//没有值
					continue;
				}else {
					HYITSTriggerRecordSyncConfig cfg = (HYITSTriggerRecordSyncConfig)
							JSONObject.toJavaObject( depConfig.getJobConfigJson(), HYITSTriggerRecordSyncConfig.class);
					//转为一个JSONArray，放进去
					DbConfig dbCfg = new DbConfig( cfg.getSourceDbConfig());
					toPutArr = this.getOperationTableArr(tmpList,tables_json[m], dbCfg );
					//把当前操作类型下，这个类别的数据，放进来
					operJson.put( tables_json[m] , toPutArr);
				}
			}
			if(!operJson.isEmpty()) {
				//如果不为空，标识放进去了值，要放到大的json中
				datas.put( operations_json[i] , operJson);
			}
		}
		
	}
	/**
	 * <pre>
	 * 将每个数据，转成json格式进行报文的拼接，图片的需要单独处理下
	 * picture:	STCD:"0000190605",TM:"2019-11-13 04:43:11",PICTM:"2019-11-13 04:43:11",PICPATH:"20191113/0000190605_1_20191113124500.jpg",
	 * rsvr:	STCD:"0000170801",TM:"2019-11-13 21:00:00",RZ:"1008.64",
	 * pptn:	STCD:"0000170801",TM:"2019-11-13 21:00:00",DRP:"0",
	 * 触发器已经都改成了报文的小写和驼峰形式
	 * </pre>
	 * @param listTriRec 已经不为0或null的触发器记录内容
	 * @return
	 */
	public JSONArray getOperationTableArr(List<TriggerRecord> listTriRec,String table,DbConfig cfg) throws Exception{
		JSONArray arr = new JSONArray();
		JSONObject tmpJson = null;
		StringBuilder tmpStr = null;
		
		if("picture".equals( table )) {
			List<String> stcds = this.getStcds(listTriRec);
			StringBuilder sb = new StringBuilder();
			//每个测站和图像访问路径的对应关系
			Map<String,String> stcdImgUrl = this.getGwtpDao().getStbprpListByStcds(cfg, sb, stcds);
			String tmpSiteUrl = null;
			for(TriggerRecord rec: listTriRec) {
				tmpJson = this.getTriRecJson(rec);
				String path = tmpJson.getString("picPath");
				tmpSiteUrl = stcdImgUrl.get(rec.getSTCD());
				if(tmpSiteUrl!=null&&path!=null) {
					tmpJson.put("picPath", tmpSiteUrl+path);
				}
				arr.add( tmpJson );
			}
			
			
		}else {
			for(TriggerRecord rec: listTriRec) {

				tmpJson = this.getTriRecJson(rec);
				arr.add( tmpJson );
			}
		}
		return arr;
	}
	
	public JSONObject getTriRecJson(TriggerRecord rec) throws Exception {
		StringBuilder tmpStr = null;
		try {
			tmpStr = new StringBuilder() ;
			tmpStr.append('{');
			tmpStr.append( rec.getEXCINF() );
			tmpStr.append('}');
			return  (JSONObject)JSONObject.parse(tmpStr.toString());
		}catch(Exception e) {
			logger.error("转换异常:{}",tmpStr.toString(),e);
			throw e;
		}
	}
	
	public List<String> getStcds( List<TriggerRecord> listTriRec ){
		List<String> list = new ArrayList<String>();
		for(TriggerRecord trc:listTriRec) {
			list.add( trc.getSTCD() );
		}
		return list;
	}
	
	
	
	public static void main(String[] args) {
		String  ff = "{STCD:\"0000170801\",TM:\"2019-11-13 21:00:00\",RZ:\"1008.64\",}";
		System.out.println(JSONObject.parse( ff ));  
	}
	
	
}
