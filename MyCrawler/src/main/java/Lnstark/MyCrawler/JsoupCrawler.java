package Lnstark.MyCrawler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupCrawler {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Document document=Jsoup.connect("http://music.163.com/")
                //模拟火狐浏览器
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36")
                .get();
		Save_Html("https://www.zhihu.com");
		
	}
	//将抓取的网页变成html文件，保存在本地
    public static void Save_Html(String url) throws Exception {
        try {
            File dest = new File("src/temp_html/" + "保存的html的名字.html");
            //接收字节输入流
            InputStream is;
            //字节输出流
            FileOutputStream fos = new FileOutputStream(dest);
    
            URL temp = new URL(url);
            
            trustAllHttpsCertificates();  
            HttpsURLConnection.setDefaultHostnameVerifier(hv);  
            
            URLConnection uc = temp.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36");
            is = temp.openStream();
            
            //为字节输入流加缓冲
            BufferedInputStream bis = new BufferedInputStream(is);
            //为字节输出流加缓冲
            BufferedOutputStream bos = new BufferedOutputStream(fos);
    
            int length;
    
            byte[] bytes = new byte[1024*20];
            while((length = bis.read(bytes, 0, bytes.length)) != -1){
                fos.write(bytes, 0, length);
            }

            bos.close();
            fos.close();
            bis.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static HostnameVerifier hv = new HostnameVerifier() {  
        public boolean verify(String urlHostName, SSLSession session) {  
            System.out.println("Warning: URL Host: " + urlHostName + " vs. "  
                               + session.getPeerHost());  
            return true;  
        }

		public boolean verify1(String arg0, SSLSession arg1) {
			// TODO Auto-generated method stub
			return false;
		}  
    };  
      
    private static void trustAllHttpsCertificates() throws Exception {  
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];  
        javax.net.ssl.TrustManager tm = new miTM();  
        trustAllCerts[0] = tm;  
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext  
                .getInstance("SSL");  
        sc.init(null, trustAllCerts, null);  
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc  
                .getSocketFactory());  
    }  
  
    static class miTM implements javax.net.ssl.TrustManager,  
            javax.net.ssl.X509TrustManager {  
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
            return null;  
        }  
  
        public boolean isServerTrusted(  
                java.security.cert.X509Certificate[] certs) {  
            return true;  
        }  
  
        public boolean isClientTrusted(  
                java.security.cert.X509Certificate[] certs) {  
            return true;  
        }  
  
        public void checkServerTrusted(  
                java.security.cert.X509Certificate[] certs, String authType)  
                throws java.security.cert.CertificateException {  
            return;  
        }  
  
        public void checkClientTrusted(  
                java.security.cert.X509Certificate[] certs, String authType)  
                throws java.security.cert.CertificateException {  
            return;  
        }  
    }  
}
