package Lnstark.MyCrawler.CloudMusic;

import org.json.JSONArray;
import org.json.JSONObject;

public class CMThread extends Thread {

	private CMCrawler crawler;
	private JSONArray records;
	private int begin, end;
	private String targetID;
	
	private static long start = System.currentTimeMillis();
	public CMThread() {}
	
	public CMThread(CMCrawler crawler, JSONArray records, int begin, int end, String targetID) {
		super();
		this.crawler = crawler;
		this.records = records;
		this.begin = begin;
		this.end = end;
		this.targetID = targetID;
	}

	public String getTargetID() {
		return targetID;
	}

	public void setTargetID(String targetID) {
		this.targetID = targetID;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public CMCrawler getCrawler() {
		return crawler;
	}

	public void setCrawler(CMCrawler crawler) {
		this.crawler = crawler;
	}

	public JSONArray getRecords() {
		return records;
	}

	public void setRecords(JSONArray records) {
		this.records = records;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void run() {
		for(int i = begin; i < end; i ++) {
			JSONObject record = (JSONObject) records.get(i);
			JSONObject song = (JSONObject) record.get("song");
			String songID = String.valueOf(song.get("id"));
			crawler.handleCommentsOfSongID(songID, targetID);
		}
		crawler.p("总用时：" + (System.currentTimeMillis() - start) + "ms");
	}
}
