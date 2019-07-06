package com.sso.Util;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ql on 2019/7/4.
 */
public class HttpClientUtil {

    /**
     * 无参数的get请求
     * @param url
     * @return
     */
    public static String doGet(String url){
        return doGet(url,null);
    }
    /**
     * 带参数的get请求
     * @param url
     * @param param
     * @return
     */
    public static String doGet(String url,
                               Map<String,String> param){
        /*创建一个默认可关闭的HttpClient对象*/
        CloseableHttpClient httpClient = HttpClients.createDefault();
        /*设置返回值*/
        String resultMsg="";
        /*定义HttpResponse对象*/
        CloseableHttpResponse response=null;

        /*创建URI，可以设置host，设置参数等*/
        try {
            URIBuilder builder=new URIBuilder(url);
            if (param!=null){
                for (String key:param.keySet()){
                    //向URI中添加参数
                    builder.addParameter(key,param.get(key));
                }
            }
            URI uri=builder.build();
            //创建httpget请求
            HttpGet httpGet=new HttpGet(uri);
            //执行请求
            response=httpClient.execute(httpGet);
            //若返回状态为200，则表示请求得到正确的回应，返回响应具体内容
            if (response.getStatusLine().getStatusCode()==200){
                resultMsg= EntityUtils.toString(response.getEntity(),"UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (response!=null){
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultMsg;
    }

    /**
     * 无参post请求函数
     * @param url
     * @return
     */
    public static String doPost(String url){
        return doPost(url,null);
    }

    /**
     * 有参post请求
     * @param url
     * @param param
     * @return
     */
    public static String doPost(String url,Map<String,String>param) {
        CloseableHttpClient httpClient= HttpClients.createDefault();
        String resultMap="";
        CloseableHttpResponse response=null;
        try {
            //创建Http Post请求
        HttpPost httpPost=new HttpPost(url);
        if (param!=null){
            //创建参数列表
            List<NameValuePair> paramList=new ArrayList<>();
            for (String key:param.keySet()){
                paramList.add(new BasicNameValuePair(key,param.get(key)));
            }
                //模拟表单
            UrlEncodedFormEntity entity=new UrlEncodedFormEntity(paramList);
            //post请求实体
            httpPost.setEntity(entity);
        }
        response=httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode()==200){
                resultMap=EntityUtils.toString(response.getEntity(),"UTF-8");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (response!=null){
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultMap;
    }
    public static String doPostJson(String url,String json){
        CloseableHttpClient httpClient=HttpClients.createDefault();
        CloseableHttpResponse response=null;
        String resultMsg="";
        try {
            HttpPost httpPost=new HttpPost(url);
            StringEntity entity=new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            response=httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode()==200){
                resultMsg=EntityUtils.toString(response.getEntity(),"UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response!=null){
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
           return resultMsg;
        }
    }

