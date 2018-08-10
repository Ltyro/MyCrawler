package Lnstark.MyCrawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author Lnstark
 * 2018年8月9日
 *
 */
public class CMCrawler {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CMCrawler cmcrawler = new CMCrawler();
		String baseUrl = "http://localhost:3000/";
		String myID = "259220217", targetUser = "644803921";
		// 歌单
		String myPlayList = cmcrawler.get(baseUrl + "user/playlist?uid=" + myID);
		JSONArray playList = (JSONArray)new JSONObject(myPlayList).get("playlist");
		JSONObject favorateList = (JSONObject) playList.get(0);
		String listID = String.valueOf(favorateList.get("id"));
		// 歌单内歌曲
		String songs = cmcrawler.get(baseUrl + "playlist/detail?id=" + listID);
		
		JSONArray songsArray = (JSONArray) new JSONObject(songs).get("privileges");
		JSONObject song = (JSONObject) songsArray.get(0);// 第一首歌
		String songID = String.valueOf(song.get("id"));
		// 获取评论
		String commentLimit = "100";
		
		String comments = cmcrawler.get(baseUrl + "comment/music?id=" + songID + "&limit=" + commentLimit);
		JSONObject commentsJson = new JSONObject(comments);
		JSONArray commentsArray = (JSONArray) commentsJson.get("comments");
		for(int i = 0, l = commentsArray.length(); i < l; i ++) {
			JSONObject comment = (JSONObject) commentsArray.get(i);
			JSONObject user = (JSONObject) comment.get("user");
			String userID = String.valueOf(user.get("userId"));
			if(userID.equals(targetUser))
				System.out.println(comment.get("content"));
		}
//		System.out.println(jsonObject);
	}
	
	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 */
	public String get(String url) {
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			connection.setRequestProperty("accept-Encoding", "gzip, deflate, br");
			connection.setRequestProperty("accept-Language", "zh-CN,zh;q=0.9");
			connection.setRequestProperty("cache-Control", "max-age=0");
			connection.setRequestProperty("connection", "keep-alive");
			connection.setRequestProperty("cookie", "__remember_me=true; __csrf=55c673b43a251184871d5a29311b7163; MUSIC_U=3d977e46dd78b5406100d9c47b2783e9649b5c559c8ba279eff7ba073b28095041a7d9dffb55091b8ff2a6411cd164387955a739ab43dce1; appver=1.5.9; os=osx; channel=netease; osver=%E7%89%88%E6%9C%AC%2010.13.2%EF%BC%88%E7%89%88%E5%8F%B7%2017C88%EF%BC%89");
			connection.setRequestProperty("host", "localhost:3000");
			connection.setRequestProperty("if-none-match", "W/\"47c2-h6H2jVWTbJkJ8w+jhUYo9/EtIDA\"");
			connection.setRequestProperty("upgrade-insecure-requests", "1");
			connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.84 Safari/537.36");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			// 建立实际的连接
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			System.out.println("Exception occur when send http get request!");
			System.out.println(e.toString());
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}

}
