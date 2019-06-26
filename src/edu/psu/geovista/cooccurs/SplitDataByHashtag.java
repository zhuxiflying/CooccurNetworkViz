package edu.psu.geovista.cooccurs;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/*
 * split the dataset by hashtag
 */
public class SplitDataByHashtag {

	private static String dataFolder = "D:\\Data\\2017-11-27_newtweets\\";
	private static String outFolder = "D:\\Data\\Hashtags\\";
	private static String hashtagFile = "D:\\Data\\CooccurNetwork\\HashtagStatistics.csv";

	private static HashSet<String> targetHashtags = null;
	private static HashMap<String, ArrayList<String[]>> hashtag_tweets = null;

	public static void main(String[] args) throws IOException {

		loadTargetHashTag();
		System.out.println(targetHashtags.size());

		hashtag_tweets = new HashMap<String, ArrayList<String[]>>();

		for (int i = 10; i < 32; i++) {

			String name = "tweettxt_Jan_" + i + ".csv";
			String fileName = dataFolder + name;
			String outFolder = dataFolder + "\\Jan_" + i;
			splitTweets(fileName, outFolder);
		}

		//hashtag statistic
//		for (String hashtag : hashtag_tweets.keySet()) {
////			int count = countUsers(hashtag);
////			int count_hashtag = countCoocurHashtags(hashtag);
////			System.out.println(hashtag+","+hashtag_tweets.get(hashtag).size()+","+count+","+count_hashtag);
////			HashMap<String,Integer> hashtag_num = countEdgeUser(hashtag);
//			for(String tags:hashtag_num.keySet())
//			{
//				System.out.println(tags+","+hashtag_num.get(tags));
//			}
//		}

		// write tweets by hashtag
		for (String hashtag : hashtag_tweets.keySet()) {
			writeTweetsByHashtag(hashtag);
		}

	}

	/**
	 * This method split tweets by hashtags: the fileName of tweets data
	 * 
	 * @throws IOException
	 */
	private static void splitTweets(String fileName, String outFolder) throws IOException {

		Reader in = new FileReader(fileName);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);

		for (CSVRecord record : records) {

			String[] tweet = new String[5];
			tweet[0] = record.get("User_id");
			tweet[1] = record.get("Tweet_text");
			tweet[2] = record.get("Time_of_tweet");
			tweet[3] = record.get("URLs_in_text");
			tweet[4] = record.get("Hashtags_in_text");

			String hashtags = tweet[4];
			// if the tweets has hashtag;
			if (!hashtags.equals("")) {
				String[] tags = hashtags.split("\\|");

				// remove duplicate
				HashSet<String> tags_set = new HashSet<String>(Arrays.asList(tags));
				ArrayList<String> tags_list = new ArrayList<String>(tags_set);

				for (int j = 0; j < tags_list.size(); j++) {

					String tag1 = tags_list.get(j);
					// count the popularity of the the hashtag
					if (targetHashtags.contains(tag1)) {
						if (hashtag_tweets.containsKey(tag1)) {
							ArrayList<String[]> tweets = hashtag_tweets.get(tag1);
							tweets.add(tweet);
							hashtag_tweets.put(tag1, tweets);
						} else {
							ArrayList<String[]> tweets = new ArrayList<String[]>();
							tweets.add(tweet);
							hashtag_tweets.put(tag1, tweets);
						}
					}

				}
			}
		}
	}

	/**
	 * This method generate a hashtag co-occurrence network from tweets data input:
	 * the fileName of tweets data, the column "Hashtags_in_text" recorded the
	 * hashtags adopted in tweet output:
	 * 
	 * @throws IOException
	 */
	private static void loadTargetHashTag() throws IOException {

		int frequency_threshold = 100;

		targetHashtags = new HashSet<String>();
		Reader in = new FileReader(hashtagFile);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String hashtags = record.get("hashtag");
			int frequency = Integer.valueOf(record.get("users"));
			if (frequency >= frequency_threshold) {
				targetHashtags.add(hashtags);
			}
		}
	}
	

	/**
	 * write tweets with same hashtag into a individual file;
	 */
	private static void writeTweetsByHashtag(String hashtag) throws IOException {
		FileWriter out = new FileWriter(outFolder + hashtag + ".csv");
		CSVPrinter printer = CSVFormat.DEFAULT
				.withHeader("User_id", "Tweet_text", "Time_of_tweet", "URLs_in_text", "Hashtags_in_text").print(out);
		ArrayList<String[]> tweets = hashtag_tweets.get(hashtag);

		for (String[] tw : tweets) {
			printer.printRecord(tw);
		}
		out.close();
	}

	/**
	 * count the number of users adopting the hashtag;
	 */
	private static int countUsers(String hashtag) {
		HashSet<String> users = new HashSet<String>();
		ArrayList<String[]> tweets = hashtag_tweets.get(hashtag);

		for (String[] tw : tweets) {
			users.add(tw[0]);
		}

		return users.size();
	}
	
	/**
	 * count the number of hashtags co-ocuring with the target hashtag;
	 */
	private static int countCoocurHashtags(String hashtag) {
		HashSet<String> hashtags = new HashSet<String>();
		ArrayList<String[]> tweets = hashtag_tweets.get(hashtag);

		for (String[] tw : tweets) {
			String tags = tw[4];
			String[] tag = tags.split("\\|");
			for(String t:tag)
			hashtags.add(t);
		}
		return hashtags.size();
	}
	
	
	private static HashMap<String,Integer> countEdgeUser(String hashtag)
	{
		HashMap<String,Integer> hashtag_userCount = new HashMap<String,Integer>();
		HashMap<String,HashSet<String>> hashtag_users = new HashMap<String,HashSet<String>>();
		ArrayList<String[]> tweets = hashtag_tweets.get(hashtag);

		for (String[] tw : tweets) {
			String tags = tw[4];
			String userid = tw[0];
			String[] tag = tags.split("\\|");
			for(String t:tag)
			{
				hashtag_users.get(t).add(userid);
			}
		}
		
		for(String tag:hashtag_users.keySet())
		{
			hashtag_userCount.put(tag, hashtag_users.get(tag).size());
		}
		return hashtag_userCount;
		
	}
}
