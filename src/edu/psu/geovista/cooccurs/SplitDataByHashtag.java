package edu.psu.geovista.cooccurs;

import java.io.File;
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
	private static String hashtagFile = "D:\\Data\\CooccurNetwork\\all_node.csv";
	
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
			System.out.println(fileName);

			splitTweets(fileName, outFolder);

//        System.out.println("Jan"+i+","+nodes.keySet().size()+","+edges.keySet().size());

		}

//		for (String hashtag : hashtag_tweets.keySet()) {
//			System.out.println(hashtag + "," + hashtag_tweets.get(hashtag).size());
//		}

		// create folder
		File dir = new File(outFolder);
		dir.mkdir();

		// write tweets by hashtag
		for (String hashtag : hashtag_tweets.keySet()) {
			FileWriter out = new FileWriter(outFolder + hashtag + ".csv");
			CSVPrinter printer = CSVFormat.DEFAULT
					.withHeader("User_id", "Tweet_text", "Time_of_tweet", "URLs_in_text", "Hashtags_in_text")
					.print(out);
			ArrayList<String[]> tweets = hashtag_tweets.get(hashtag);

			for (String[] tw : tweets) {
				printer.printRecord(tw);
			}
			out.close();
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
			String hashtags = record.get("node");
			int frequency = Integer.valueOf(record.get("frequency"));
			if (frequency > frequency_threshold) {
				targetHashtags.add(hashtags);
			}
		}
	}
}