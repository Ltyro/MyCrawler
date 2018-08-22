package Lnstark.MyCrawler.CloudMusic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author Lnstark 2018年8月9日
 *
 */
public class CMCrawler {

	private String baseUrl = "http://localhost:3000/";
	private int maxPageSize = 100;

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

	public int getMaxPageSize() {
		return maxPageSize;
	}

	public void setMaxPageSize(int maxPageSize) {
		this.maxPageSize = maxPageSize;
	}

	public static void main(String[] args) {
		CMCrawler c = new CMCrawler();
		c.setMaxPageSize(100);
		String baseUrl = c.getBaseUrl();
		String myID = "259220217", targetUser = "644803921";
		String targetID = "1441876771", twelve = "437042395";
		int weekRecord = 1, allRecord = 0;
		Map<String, String> songs = new HashMap<String, String>();
//		JSONArray recordList = c.getRecordList(targetID, allRecord);
//
//		for (int i = 0; i < recordList.length(); i++) {
//
//			JSONObject song = (JSONObject) ((JSONObject) recordList.get(i)).get("song");
//			String songID = String.valueOf(song.get("id"));
//			JSONObject songDetail = c.getSongDetail(songID);
//			String songName = (String) songDetail.get("name");
//			songs.put(songID, songName);
//		}
		Set<String> myListSongs = new HashSet<String>();
		myListSongs = c.crawPlayList("1441876771", 11);
		String comments = c.get(baseUrl + "comment/music?id=" + 5050335 + "&limit=" + 100 + "&offset=" + 0);
//		JSONObject commentsJson = new JSONObject(comments);
//		Set<String> commenters = c.getCommenters(commentsJson);
//		Iterator<String> it;
//		for(String commenter : commenters) {
//			c.p(commenter+"'s songs");
//			Set<String> listSongs = c.crawPlayList(commenter, 100);
//			c.p(listSongs);
//			break;
//		}
		c.p(myListSongs);

		// long startTime = System.currentTimeMillis();
		// for(int i = 0, l = 12; i < l; i ++) {
		// JSONObject record = (JSONObject) records.get(i);
		// JSONObject song = (JSONObject) record.get("song");
		// String songID = String.valueOf(song.get("id"));
		// c.handleCommentsOfSongID(songID, targetID);
		// }
		// c.p("单线程总用时：" + (System.currentTimeMillis() - startTime) + "ms");
	}

	/**
	 * 获取听歌排行（100）
	 * 
	 * @param userID
	 * @param type 1: 周排行，0 所有排行
	 * @return
	 */
	private JSONArray getRecordList(String userID, int type) {
		JSONArray recordList = new JSONArray();
		String weekOrAll = type == 1 ? "周记录" : "所有记录";
		String record = get(baseUrl + "user/record?uid=" + userID + "&type=" + type);
		if (record == null)
			return recordList;
		JSONObject recorJson = new JSONObject(record);
		if (!recorJson.has("allData")) {
			p("无" + weekOrAll);
			return recordList;
		}
		recordList = (JSONArray) recorJson.get("allData");
		return recordList;
	}

	private Set<String> crawPlayList(String playListHost, int listLimit) {
		Set<String> SongsOfPlaylists = new HashSet<String>();
		// 歌单
		JSONArray playList = getPlayList(playListHost);
		Integer hostIDInt = Integer.parseInt(playListHost);
		int listNum = 12;// 要爬的歌单数
		for (int i = 0; i < listLimit && i < playList.length(); i++) {
			JSONObject songList = (JSONObject) playList.get(i);
			if (!songList.get("userId").equals(hostIDInt))
				continue;
			String listID = String.valueOf(songList.get("id"));
			// 歌单内歌曲
			System.out.println("crawling list: " + String.valueOf(songList.get("name")));
			String songs = get(baseUrl + "playlist/detail?id=" + listID);
			if (songs == null)
				continue;
			JSONArray songsArray = (JSONArray) new JSONObject(songs).get("privileges");
			for (int j = 0, sl = songsArray.length(); j < sl; j++) {
				// 获取歌
				JSONObject song = (JSONObject) songsArray.get(j);
				String songID = String.valueOf(song.get("id"));
//				JSONObject songDetail = getSongDetail(songID);
//				String songName = (String) songDetail.get("name");
				// 获取评论
				try {
					SongsOfPlaylists.add(songID);
//					handleCommentsOfSongID(songID, commenter);
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}
		return SongsOfPlaylists;
	}

	private JSONArray getPlayList(String userID) {
		JSONArray playList = new JSONArray();
		String myPlayList = get(baseUrl + "user/playlist?uid=" + userID);
		if (myPlayList == null)
			return playList;
		playList = (JSONArray) new JSONObject(myPlayList).get("playlist");
		return playList;
	}

	public void handleCommentsOfSongID(String songID, String commenter) {
		String commentLimit = "100";
		int offset = 0;
		JSONObject song = getSongDetail(songID);
		String songName = (String) song.get("name");
		System.out.println("crawling comments of song: " + songName);
		String comments = get(baseUrl + "comment/music?id=" + songID + "&limit=" + commentLimit + "&offset=" + offset);
		if (comments == null)
			return;
		JSONObject commentsJson = new JSONObject(comments);
		int totalComments = commentsJson.getInt("total"), step = 100;

		for (int i = 0, page = totalComments / step + 1; i < page && i < maxPageSize; i++) {
			comments = get(baseUrl + "comment/music?id=" + songID + "&limit=" + commentLimit + "&offset=" + offset);
			if (comments == null)
				continue;
			commentsJson = new JSONObject(comments);
			System.out.println("crawling comments " + offset);
			handleComments(commentsJson, commenter);

			offset += step;

		}
	}

	private void handleComments(JSONObject commentsJson, String commenter) {
		JSONArray commentsArray = (JSONArray) commentsJson.get("comments");
		try {
			for (int j = 0, l = commentsArray.length(); j < l; j++) {
				JSONObject comment = (JSONObject) commentsArray.get(j);
				JSONObject user = (JSONObject) comment.get("user");
				String userID = String.valueOf(user.get("userId"));
				if (userID.equals(commenter)) {
					String userNickName = String.valueOf(user.get("nickname"));
					String targetComment = "{comment id: " + comment.get("commentId") + ", content: "
							+ comment.get("content") + ", userNickName: " + userNickName + "}";
					System.out.printf(targetComment);
					bw.write(targetComment);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Set<String> getCommenters(JSONObject commentsJson) {
		Set<String> commenter = new HashSet<String>();
		JSONArray commentsArray = (JSONArray) commentsJson.get("comments");
		for (int j = 0, l = commentsArray.length(); j < l; j++) {
			JSONObject comment = (JSONObject) commentsArray.get(j);
			JSONObject user = (JSONObject) comment.get("user");
			String userID = String.valueOf(user.get("userId"));
			commenter.add(userID);
		}
		return commenter;
	}
	
	private String getSongLyric(String songID) {
		String lyricStr = get(baseUrl + "lyric?id=" + songID);
		JSONObject lyric = (JSONObject) new JSONObject(lyricStr).get("lrc");
		lyricStr = String.valueOf(lyric.get("lyric"));
		return lyricStr;
	}

	private JSONObject getSongDetail(String songID) {
		JSONObject song;
		String songDetail = get(baseUrl + "song/detail?ids=" + songID);
		JSONObject songDetailJson = new JSONObject(songDetail);
		if(!songDetailJson.has("songs")) return null;
		JSONArray songs = (JSONArray) songDetailJson.get("songs");
		song = (JSONObject) songs.get(0);
		return song;
	}

	private String removeLyricTime(String lyric) {
		return lyric.replaceAll("\\[.*\\]", "");
	}

	public void p(Object o) {
		System.out.println(o);
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
			connection.setRequestProperty("accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			connection.setRequestProperty("accept-Encoding", "gzip, deflate, br");
			connection.setRequestProperty("accept-Language", "zh-CN,zh;q=0.9");
			connection.setRequestProperty("cache-Control", "max-age=0");
			connection.setRequestProperty("connection", "keep-alive");
			// connection.setRequestProperty("cookie", "__remember_me=true;
			// __csrf=55c673b43a251184871d5a29311b7163;
			// MUSIC_U=3d977e46dd78b5406100d9c47b2783e9649b5c559c8ba279eff7ba073b28095041a7d9dffb55091b8ff2a6411cd164387955a739ab43dce1;
			// appver=1.5.9; os=osx; channel=netease;
			// osver=%E7%89%88%E6%9C%AC%2010.13.2%EF%BC%88%E7%89%88%E5%8F%B7%2017C88%EF%BC%89");
			connection.setRequestProperty("host", "localhost:3000");
			connection.setRequestProperty("if-none-match", "W/\"47c2-h6H2jVWTbJkJ8w+jhUYo9/EtIDA\"");
			connection.setRequestProperty("upgrade-insecure-requests", "1");
			connection.setRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.84 Safari/537.36");
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
