package Lnstark.MyCrawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadTaohuayuan {
	// 构造URL  
    URL url ;  
    // 打开连接  
    URLConnection con;  
     
    // 输入流  
    InputStream is;  
    OutputStream os;
    // 1K的数据缓冲  
    byte[] bs = new byte[1024];  
    // 读取到的数据长度  
    int len;  
    DownloadTaohuayuan() {
    	
    }
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		DownloadTaohuayuan dt = new DownloadTaohuayuan();
		for(int i = 7; i < 9;i++) {
			int dirnum = ( 1 << i - 4) * 5;
			for(int j = 0; j < dirnum; j++) {
				//mkdirs
				String dirname = "D://Thytiles//" + i + "//" + j;
//				File dir = new File(dirname);
//				if(!dir.exists())
//					dir.mkdirs();
				//download images
				for(int k = 0; k < dirnum; k++) {
					String urlString = "http://dl.cnthy.com.cn/thytiles/" + i + "/" + j + "/" + k + ".jpg";
					String picname = k + ".jpg";
					dt.download(urlString, picname, dirname);
				}
				System.out.println("saving into dir " + i + "/" + j);
			}
		}
//		dt.close();
		System.out.println("succeed!");
	} 
	
	public void download(String urlString, String filename,String savePath) throws Exception {  
		// 构造URL  
	    url = new URL(urlString);  
	    // 打开连接  
	    con = url.openConnection();  
	    //设置请求超时为5s  
        con.setConnectTimeout(5*1000); 
	    // 输入流  
	    is = con.getInputStream();  
	  
	    // 1K的数据缓冲  
	    bs = new byte[1024];  
	    // 读取到的数据长度  
	    int len; 
        // 输出的文件流  
       File sf=new File(savePath);  
       if(!sf.exists()){  
           sf.mkdirs();  
       }  
       os = new FileOutputStream(sf.getPath()+"\\"+filename);  
        // 开始读取  
        while ((len = is.read(bs)) != -1) {  
          os.write(bs, 0, len);  
        }  
         
    } 
	
	public void close() throws IOException {
		// 完毕，关闭所有链接  
        os.close();  
        is.close(); 
	}
}
