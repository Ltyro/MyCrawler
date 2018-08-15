package Lnstark.MyCrawler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


 
//import pojo.DataPackage;
 
public class GetSomething {
    
   public static void main(String[] args){
	   //创建一个客户端，类似于打开一个浏览器  
	   HttpClient httpclient = new DefaultHttpClient();
	   //创建一个 get方法，类似于在浏览器地址栏中输入一个地址  
	   HttpGet httpget=new HttpGet("http://www.kugou.com/yy/html/search.html#searchType=song&searchKeyWord=告白气球");
	   //回车，获得响应状态码
	   HttpResponse response;
	   String html = null;
	   try {
			response = httpclient.execute(httpget);
			//System.out.println(response.getStatusLine());
		    HttpEntity httpEntity = response.getEntity();
		    //System.out.println(httpEntity.getContentType());
	        //System.out.println(httpEntity.getContentLength());
	        html=EntityUtils.toString(httpEntity , "UTF-8").trim();
	        System.out.println(html);
        
	    //System.out.println(httpEntity.getContent()); 
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	   BufferedWriter writer=null;
	   try {
	     writer=new BufferedWriter(new  FileWriter("d:\\test.txt"));
	   } catch (IOException e1) {
	    e1.printStackTrace();
	   }
	   try{
		writer.append(html);
	    writer.newLine();//换行
	    writer.flush();//需要及时清掉流的缓冲区，万一文件过大就有可能无法写入了
	   }catch (IOException e) {
		   e.printStackTrace();
	   }
	   
	   //释放  
       httpget.releaseConnection();
       
       Document doc = Jsoup.parse(html);
       //System.out.println(doc.title());
//       Element content = doc.getElementById("7d");
//       for (Element link : doc.getElementsByClass("t clearfix")) {
//    	   String linkText = link.text();
//    	   System.out.println(linkText);
//    	  }
       
   }
   
   
    
}