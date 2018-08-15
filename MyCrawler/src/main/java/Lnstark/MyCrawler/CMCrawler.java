package Lnstark.MyCrawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	
	private String baseUrl = "http://localhost:3000/";
	String targetFile = "D:\\GitReposity\\NeteaseCloudMusicApi\\output\\result.txt";
	BufferedWriter bw;
	
	public CMCrawler() {
		try {
			bw = new BufferedWriter(new FileWriter(new File(targetFile)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public static void main(String[] args){
		// TODO Auto-generated method stub
		CMCrawler cmcrawler = new CMCrawler();
		String baseUrl = cmcrawler.getBaseUrl();
		String myID = "259220217", targetUser = "644803921";
		String targetID = "259220217", commenter = targetID;
		
		int weekRecord = 1, allRecord = 0;
		String record = cmcrawler.get(baseUrl + "user/record?uid="+targetID+"&type="+allRecord);
		if(record == null) return;
		JSONArray recordList = (JSONArray)new JSONObject(record).get("allData");
		
		for(int i = 0; i < recordList.length(); i ++) {
			JSONObject song = (JSONObject) ((JSONObject) recordList.get(i)).get("song");
			String songID = String.valueOf(song.get("id"));
			try {
				cmcrawler.handleCommentsOfSongID(songID, commenter);
			} catch(Exception e) {
				System.out.println(e);
			}
		}
		
		
	}
	
	private void crawPlayList(String playListHost, String commenter) {
		// 歌单
		String myPlayList = get(baseUrl + "user/playlist?uid=" + playListHost);
		if(myPlayList == null) return;
		JSONArray playList = (JSONArray)new JSONObject(myPlayList).get("playlist");
		int listNum = 12;// 要爬的歌单数
		for(int i = 2; i < listNum; i ++) {
			JSONObject songList = (JSONObject) playList.get(i);
			String listID = String.valueOf(songList.get("id"));
			// 歌单内歌曲
			System.out.println("crawling list: " + String.valueOf(songList.get("name")));
			String songs = get(baseUrl + "playlist/detail?id=" + listID);
			if(songs == null) continue;
			JSONArray songsArray = (JSONArray) new JSONObject(songs).get("privileges");
			for(int j = 0, sl = songsArray.length(); j < sl; j ++) {
				// 获取歌
				JSONObject song = (JSONObject) songsArray.get(j);// 
				String songID = String.valueOf(song.get("id"));
				// 获取评论
				try {
					handleCommentsOfSongID(songID, commenter);
				} catch(Exception e) {
					System.out.println(e);
				}
			}
			
		}
	}
	
	private void handleCommentsOfSongID(String songID, String commenter) {
		String commentLimit = "100";
		int offset = 0;
		String songDetail = get(baseUrl + "song/detail?ids=" + songID);
		if(songDetail == null) return;
		JSONObject songDetailJson = new JSONObject(songDetail);
		JSONArray songs = (JSONArray) songDetailJson.get("songs");
		JSONObject song = (JSONObject) songs.get(0);
		String songName = (String) song.get("name");
		System.out.println("crawling comments of song: " + songName);
		String comments = get(baseUrl + "comment/music?id=" + songID + 
				"&limit=" + commentLimit + "&offset=" + offset);
		if(comments == null) return;
		JSONObject commentsJson = new JSONObject(comments);
		int totalComments = commentsJson.getInt("total"), step = 100, maxPage = 100;
		
		for(int i = 0, page = totalComments/step + 1; i < page && i < maxPage; i ++) {
			comments = get(baseUrl + "comment/music?id=" + songID 
					+ "&limit=" + commentLimit + "&offset=" + offset);
			if(comments == null) continue;
			commentsJson = new JSONObject(comments);
			System.out.println("crawling comments " + offset);
			handleComments(commentsJson, commenter);
			
			offset += step;
			
		} 
	}
	
	private void handleComments(JSONObject commentsJson, String commenter) {
		JSONArray commentsArray = (JSONArray) commentsJson.get("comments");
		try {
			for(int j = 0, l = commentsArray.length(); j < l; j ++) {
				JSONObject comment = (JSONObject) commentsArray.get(j);
				JSONObject user = (JSONObject) comment.get("user");
				String userID = String.valueOf(user.get("userId"));
				if(userID.equals(commenter)) {
					String userNickName = String.valueOf(user.get("nickname"));
					String targetComment = "{comment id: "+ comment.get("commentId") 
										+", content: "+ comment.get("content") +
										", userNickName: "+ userNickName +"}";
					System.out.printf(targetComment);
					bw.write(targetComment);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
