package com.howell.protocol;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.howell.protocol.entity.ClientCredential;
import com.howell.utils.JsonUtils;

import android.util.Log;


public class HttpProtocol implements Const{
	//创建请求对象
    private HttpPost post;
    private HttpGet get;
    //创建客户端对象
    private HttpClient client;
    //创建发送请求的对象
    private HttpResponse response;
    private String retSrc = "";
    private byte[] bmp = new byte[1];
    
    private String handleHttpProtocol(int mode , String url , JSONObject param ,String cookie){
    	//包装请求的地址
    	if(mode == POST){
    		post = new HttpPost(url);
    		if(cookie != null){
    			Log.e("cookie", cookie);
    			post.setHeader("Cookie", cookie);
    		}
    	}else{ 
    		get = new HttpGet(url);
    		if(cookie != null){
    			Log.e("cookie", cookie);
    			get.setHeader("Cookie", cookie);
    		}
    	}
        //创建默认的客户端对象
    	client = new DefaultHttpClient();
        // 请求超时
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
        // 读取超时
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 8000  );
        try {
        	if(param != null){
        		post.addHeader("Content-Type", "application/json;charset=utf-8");  
        		Log.e("param", param.toString());	
        		Log.i("log123", "param= "+param.toString());
        		StringEntity se = new StringEntity(param.toString(),HTTP.UTF_8); 
//        		StringEntity se = new StringEntity(param.toString());
        		post.setEntity(se);  
        	}
            //客户端开始向指定的网址发送请求
        	if(mode == POST){
        		response = client.execute(post);
//        		Log.e("", "post");
        	}else {
        		response = client.execute(get);
//        		Log.e("", "get");
        	}
        	System.out.println("http code:"+response.getStatusLine().getStatusCode());
        	//若状态码为200 ok
        	if(response.getStatusLine().getStatusCode() == 200) {
	            //获得请求的Entity
        		retSrc = EntityUtils.toString(response.getEntity());  
        		Log.e("http response", retSrc);
//        		Toast.makeText(AlarmDetailActivity.this, ""+retSrc, Toast.LENGTH_SHORT).show();
        		
        	}
        	return retSrc;
        } catch (ConnectTimeoutException e) {
        	Log.e("", "ConnectTimeoutException");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
        	client.getConnectionManager().shutdown();  
        }
		return retSrc;
    }
    
    private byte[] handleHttpProtocol(int retMode,int mode , String url , JSONObject param ,String cookie){
    	//包装请求的地址
    	if(mode == POST){
    		post = new HttpPost(url);
    		if(cookie != null){
//    			Log.e("cookie", cookie);
    			post.setHeader("Cookie", cookie);
    		}
    	}else{ 
    		get = new HttpGet(url);
    		if(cookie != null){
//    			Log.e("cookie", cookie);
    			get.setHeader("Cookie", cookie);
    		}
    	}
        //创建默认的客户端对象
    	client = new DefaultHttpClient();
        // 请求超时
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
        // 读取超时
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 8000  );
        try {
        	if(param != null){
        		post.addHeader("Content-Type", "application/json");  
//        		Log.e("param", param.toString());	
        		StringEntity se = new StringEntity(param.toString());  
        		post.setEntity(se);  
        	}
            //客户端开始向指定的网址发送请求
        	if(mode == POST){
        		response = client.execute(post);
//        		Log.e("", "post");
        	}else {
        		response = client.execute(get);
//        		Log.e("", "get");
        	}
        	System.out.println("http code:"+response.getStatusLine().getStatusCode());
        	//若状态码为200 ok
        	if(response.getStatusLine().getStatusCode() == 200) {
	            //获得请求的Entity
        		bmp = EntityUtils.toByteArray(response.getEntity());
        	}
        	return bmp;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
        	client.getConnectionManager().shutdown();  
        }
		return bmp;
    }
    
    public void cancelAlarm(String webServiceIp, String id ,String signal){
    	handleHttpProtocol(POST,"http://"+webServiceIp+":8800/howell/ver10/Annunciator/Modbus/System/Devices/"+id +"/Signals/"+signal+"/Eliminate",null,null);
    }
    
    //获取认证的协议的服务器随机值
    public String nonce(String webServiceIp,String userName){
    	return handleHttpProtocol(GET,"http://"+webServiceIp+":8800/howell/ver10/user_authentication/Authentication/Nonce?UserName="+userName,null,null);
    }
    
    //用户认证
    public String authenticate(String webServiceIp,ClientCredential clientCredential) throws JSONException{
    	return handleHttpProtocol(POST,"http://"+webServiceIp+":8800/howell/ver10/user_authentication/Authentication/Authenticate",JsonUtils.createAuthenticateJsonObject(clientCredential.getUserName(), clientCredential.getNonce(), clientCredential.getDomain(), clientCredential.getClientNonce(), clientCredential.getVerifySession()),null);
    }
    
    //查询地图
    public String maps(String webServiceIp,int pageIndex,int pageSize,String cookie) throws JSONException{
    	String url = "http://"+webServiceIp+":8800/howell/ver10/data_service/management/System/Maps?PageIndex="+pageIndex+"&PageSize="+pageSize;
    	Log.i("123", "url:"+url+"webip:"+webServiceIp);
    	return handleHttpProtocol(GET,url,null,cookie);    	
    }
    
    //通过地图id查询地图信息
    public byte[] mapsData(String webServiceIp,String id,String cookie) throws JSONException{
    	return handleHttpProtocol(BYTEARRAY,GET,"http://"+webServiceIp+":8800/howell/ver10/data_service/management/System/Maps/"+id+"/Data",null,cookie);
    }
    
    //查询地图子项
    public String items(String webServiceIp,String id,int pageIndex,int pageSize,String cookie) throws JSONException{
    	return handleHttpProtocol(GET,"http://"+webServiceIp+":8800/howell/ver10/data_service/management/System/Maps/"+id+"/Items?PageIndex="+pageIndex+"&PageSize="+pageSize,null,cookie);
    }
    
    //查询事件联动
    public String linkages(String webServiceIp,String componentId,String eventType,String eventState,int pageIndex,int pageSize,String cookie) throws JSONException{
    	Log.i("123", "componentId="+componentId);
    	return handleHttpProtocol(GET,"http://"+webServiceIp+":8800/howell/ver10/data_service/management/System/Events/Linkages?ComponentId="+componentId+"&EventType="+eventType+"&EventState="+eventState+"&PageIndex="+pageIndex+"&PageSize="+pageSize,null,cookie);
//    	FIXME
//    	return handleHttpProtocol(GET,"http://"+webServiceIp+":8800/howell/ver10/data_service/management/System/Events/Linkages?ComponentId="+componentId+"&EventType="+eventType+"&EventState="+eventState+"&PageIndex="+pageIndex+"&PageSize="+pageSize,null,cookie);
    }
    
    //创建视频回放任务
    public String playback(String webServiceIp,String videoInputChannelId,int streamNo,int beginTime,int endTime,String cookie) throws JSONException{
    	String url = "http://"+webServiceIp+":8800/howell/ver10/data_service/business/informations/Business/Clients/Tasks/Playback?VideoInputChannelId="+videoInputChannelId+"&StreamNo="+streamNo+"&BeginTime="+beginTime+"&EndTime="+endTime;
    	Log.e("123", "playback  url:"+url);
    	return handleHttpProtocol(GET,url,null,cookie);
    } 
    
    //处理报警输入通道的报警
    public String process(String webServiceIp,String id,String process,String cookie) throws JSONException{
    	Log.i("log123", process);
    	String url = "http://"+webServiceIp+":8800/howell/ver10/data_service/Business/Informations/IO/Inputs/Channels/"+id+"/Status/Process";
    	Log.e("123", "process url:"+url);
    	return handleHttpProtocol(POST,url,JsonUtils.createProcessJsonObject(process),cookie);
    } 
    
    //获取用户权限下的设备列表
    public String informations(String webServiceIp,String cookie) throws JSONException{
//    	return handleHttpProtocol(GET,"http://"+webServiceIp+":8800/howell/ver10/data_service/business/informations/Business/Informations/Devices",null,cookie);
    	return handleHttpProtocol(GET,"http://"+webServiceIp+":8800/howell/ver10/data_service/Business/Informations/Devices",null,cookie);
    } 
    //查询特定事件联动
    public String linkage(String webServiceIp,String id,String eventType,String eventState,String cookie) throws JSONException{
    	String url = "http://"+webServiceIp+":8800/howell/ver10/data_service/management/System/Events/Linkages/Components/"+id+"/"+eventType+"/"+eventState;
    	Log.i("123", "linkage url:"+url);
    	String ret =  handleHttpProtocol(GET,url,null,cookie);
    	if (ret.equals("")) {
			if (eventState.equals("Active")) {
				eventState = "Inactive";
			}else{
				eventState = "Active";
			}
			url = "http://"+webServiceIp+":8800/howell/ver10/data_service/management/System/Events/Linkages/Components/"+id+"/"+eventType+"/"+eventState;
			Log.i("123", "linkage url:"+url);
			ret = handleHttpProtocol(GET,url,null,cookie);
    	}
    	return ret;
    }
    //获取设备信息
    public String device(String webServiceIp,String deviceId,String cookie){
    	return handleHttpProtocol(GET,"http://"+webServiceIp+":8800/howell/ver10/data_service/management/System/Devices/"+deviceId,null,cookie);
    }
    
    
    //获取报警信息 列表
    public String historyAlarms(String webServiceIp,String cookie,String begTime,String endTime){//返回 json
//    	String url = "http://"+webServiceIp+":8800/howell/ver10/data_service/Business/Informations/Event/Linkages";
//    	String url = "http://"+webServiceIp+":8800/howell/ver10/data_service/business/informations/Business/Informations/Event/Records?BeginTime="+begTime+"&EndTime="+endTime+"&PageIndex="+1+"&PageSize="+10;
//    	String url = "http://"+webServiceIp+":8800/howell/ver10/data_service/Business/Informations/Event/Records?BeginTime="+begTime+"&EndTime="+endTime;
    	String url = "http://"+webServiceIp+":8800/howell/ver10/data_service/Business/Informations/Event/Records?BeginTime="+begTime+"&EndTime="+endTime+"&PageIndex=1&PageSize=10";
    	Log.i("123", "url:"+url);
    	return handleHttpProtocol(GET, url, null, cookie);//
    }
    
}
