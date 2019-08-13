package edu.psu.geovista.cooccurs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class HashtagUserAnalysis {

	private static String dataFolder = "D:\\data\\geotwitter\\hashtag_users\\";
	private static String ts_dataFolder = "D:\\data\\geotwitter\\hahstags_ts\\";
	private static HashMap<String, HashSet<String>> hashtag_users;
	private static HashMap<String,Integer> targetHashtags;
	

	public static void main(String[] args) throws IOException {
		
		loadTargetHashtag();
		System.out.println(targetHashtags.keySet().size());
		loadTargetHashtagUsers();
		for(String hashtag:hashtag_users.keySet())
		{
			if(hashtag_users.get(hashtag).size()>=10)
			System.out.println(hashtag+","+hashtag_users.get(hashtag).size()+","+targetHashtags.get(hashtag));
		}
//		loadTargetHashtagUsers();

	}

	private static void loadAllHashtagUsers() throws IOException {
		hashtag_users = new HashMap<String, HashSet<String>>();
		File f = new File(dataFolder);
		for (final File fileEntry : f.listFiles()) {
			String fileName = fileEntry.getName();
			System.out.println(fileName);
			Reader in = new FileReader(dataFolder + fileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
			for (CSVRecord record : records) {
				String hashtag = record.get("Hashtag");
				String userid = record.get("Users");
				if (hashtag_users.containsKey(hashtag)) {
					HashSet<String> ids = hashtag_users.get(hashtag);
					ids.add(userid);
					hashtag_users.put(hashtag, ids);
				} else {
					HashSet<String> ids = new HashSet<String>();
					ids.add(userid);
					hashtag_users.put(hashtag, ids);
				}
			}
		}
	}

	/**
	 * load target hashtags by setting threshold;
	 * 
	 * @throws IOException
	 */
	private static void loadTargetHashtag() throws IOException {
		targetHashtags = new HashMap<String,Integer>();
		Reader in = new FileReader("D:\\data\\geotwitter\\hahstag_frequ.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			int freq = Integer.valueOf(record.get("Frequency"));
			String hashtag = record.get("Hashtag");
			if (freq < 1000&&freq>=10) {
				targetHashtags.put(hashtag,freq);
			}
		}
	}
	
	private static void loadTargetHashtag2() throws IOException {
		targetHashtags = new HashMap<String,Integer>();
		Reader in = new FileReader("D:\\data\\geotwitter\\hahstags_ts\\hashtag_ts_statistics.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			int freq = Integer.valueOf(record.get("max"));
			String hashtag = record.get("hashtag");
			int id = Integer.valueOf(record.get("idd"));
			if (freq >= 6470) {
				targetHashtags.put(hashtag,id);
			}
		}
	}

	private static void loadTargetHashtagUsers() throws IOException {
		hashtag_users = new HashMap<String, HashSet<String>>();
		File f = new File(dataFolder);
		for (final File fileEntry : f.listFiles()) {
			String fileName = fileEntry.getName();
			System.out.println(fileName);
			Reader in = new FileReader(dataFolder + fileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
			for (CSVRecord record : records) {
				String hashtag = record.get("Hashtag");
				if (targetHashtags.containsKey(hashtag)) {
					String userid = record.get("Users");
					if (hashtag_users.containsKey(hashtag)) {
						HashSet<String> ids = hashtag_users.get(hashtag);
						ids.add(userid);
						hashtag_users.put(hashtag, ids);					
					} else {
						HashSet<String> ids = new HashSet<String>();
						ids.add(userid);
						hashtag_users.put(hashtag, ids);

					}
				}
			}
		}
	}
	
	
	private static int[] loadHashtagTSbyId(int id) throws IOException
	{
		int[] ts = new int[732];
		Reader in = new FileReader(ts_dataFolder+id+".csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			int index = Integer.valueOf(record.get("Index"));
			int volume = Integer.valueOf(record.get("Users"));
			ts[index] = volume;
		}
		return ts;
	}

}
