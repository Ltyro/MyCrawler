package Lnstark.MyCrawler;

import java.io.BufferedReader;
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;
import java.io.InputStream;  
import java.io.InputStreamReader;
import java.io.OutputStream;  
import java.net.URL;  
import java.net.URLConnection;  
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
  
  
public class DownloadImage {  
  
    /** 
     * @param args 
     * @throws Exception  
     */  
	static Logger logger = Logger.getLogger("DownloadImage");
	
	public static void log(String msg){
//		logger.info(msg);
		System.out.println(msg);
	}
	
	public static String open(String url) throws ParseException, IOException{
		HttpClient httpclient = new DefaultHttpClient();
    	HttpGet httpget=new HttpGet(url);
    	HttpResponse response = httpclient.execute(httpget);
    	HttpEntity httpEntity = response.getEntity();
    	String html = EntityUtils.toString(httpEntity , "UTF-8");
    	return html;
	}
	
    public static void main(String[] args) throws Exception {  
        // TODO Auto-generated method stub
    	String oriurl = "http://www.win4000.com/hj/wangzherongyao.html";
    	String orihtml = open(oriurl);
//    	System.out.println(orihtml);
    	Document oridoc = Jsoup.parse(orihtml);
    	Elements titles = oridoc.getElementsByClass("tit01");
    	Element allherosul = null;
    	for(Element ele : titles){
    		if(ele.text().equals("全部英雄皮肤图片")){
    			allherosul = ele;
    			break;
    		}
    	}
    	
		List<Node> lis = allherosul.nextSibling().nextSibling().childNodes();
		for(int i = 1; i<lis.size(); i += 2){
			Node li = lis.get(i);
			Node a = li.childNode(0);
			String src = a.attr("href");
			String oneherohtml = open(src);
			Document htmldoc = Jsoup.parse(oneherohtml);
			if(htmldoc.getElementsByClass("liSelected").size() > 1){
				Element liSelected = htmldoc.getElementsByClass("liSelected").get(0);
				int j = 1;
				while(liSelected != null){
					String skinsrc = liSelected.childNode(1).attr("src");
					liSelected = (Element) liSelected.nextSibling().nextSibling();
//					log(a.childNode(0).attr("alt"));
					download(skinsrc,a.childNode(0).attr("alt") + (j++) + ".jpg","D:\\我的图片\\KOH");
				}
			}
//			break;//excute once
		}
    	log("下载完毕.");
    	
        //download("http://pic1.win4000.com/wallpaper/5/57cfaba635f9e.jpg", "程咬金.jpg","D:\\我的图片\\KOH\\");  
    }  
      
    public static void download(String urlString, String filename,String savePath) throws Exception {  
        // 构造URL  
        URL url = new URL(urlString);  
        // 打开连接  
        URLConnection con = url.openConnection();  
        //设置请求超时为5s  
        con.setConnectTimeout(5*1000);  
        // 输入流  
        InputStream is = con.getInputStream();  
      
        // 1K的数据缓冲  
        byte[] bs = new byte[1024];  
        // 读取到的数据长度  
        int len;  
        // 输出的文件流  
       File sf=new File(savePath);  
       if(!sf.exists()){  
           sf.mkdirs();  
       }  
       OutputStream os = new FileOutputStream(sf.getPath()+"\\"+filename);  
        // 开始读取  
        while ((len = is.read(bs)) != -1) {  
          os.write(bs, 0, len);  
        }  
        // 完毕，关闭所有链接  
        os.close();  
        is.close();  
    }   
    
    
}  