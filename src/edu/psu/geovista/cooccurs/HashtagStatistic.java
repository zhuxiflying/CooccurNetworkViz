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

public class HashtagStatistic {

	private static String hashtagFile = "D:\\Data\\CooccurNetwork\\HashtagStatistics.csv";
	private static String hashtagFolder = "D:\\Data\\Hashtags\\";
	private static String outputFolder = "D:\\Data\\CooccurNetwork\\";
	private static HashMap<String, Integer> targetHashtags = null;
	private static HashMap<String, Integer> edge_count = null;

	public static void main(String[] args) throws IOException {
		edge_count = new HashMap<String, Integer>();
		loadTargetHashtag();
		System.out.println(targetHashtags.size());
		for (String hashtag : targetHashtags.keySet()) {
			ArrayList<String[]> tweets = loadHashtag(hashtag);
			HashMap<String, Integer> hashtag_user = countEdgeUser(tweets);
			for (String tag : hashtag_user.keySet()) {
				if (targetHashtags.containsKey(tag) && !tag.equals(hashtag) && hashtag_user.get(tag) > 9) {
					if (!edge_count.containsKey(tag + "," + hashtag))
						edge_count.put(hashtag + "," + tag, hashtag_user.get(tag));
				}
			}
		}
		
		writeEdgeFile(outputFolder+"edges.csv");
		writeNodeFile(outputFolder+"nodes.csv");

	}
	
	/**
	 * load tweets by hashtag;
	 * 
	 * @param hashtag
	 * @return
	 * @throws IOException
	 */
	private static ArrayList<String[]> loadHashtag(String hashtag) throws IOException {
		ArrayList<String[]> tweets = new ArrayList<String[]>();
		Reader in = new FileReader(hashtagFolder + hashtag + ".csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String[] tweet = new String[5];
			tweet[0] = record.get("User_id");
			tweet[1] = record.get("Tweet_text");
			tweet[2] = record.get("Time_of_tweet");
			tweet[3] = record.get("URLs_in_text");
			tweet[4] = record.get("Hashtags_in_text");
			tweets.add(tweet);
		}
		return tweets;

	}

	/**
	 * load target hashtags by setting threshold;
	 * 
	 * @throws IOException
	 */
	private static void loadTargetHashtag() throws IOException {
		targetHashtags = new HashMap<String, Integer>();
		Reader in = new FileReader(hashtagFile);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String hashtags = record.get("hashtag");
			int user_count = Integer.valueOf(record.get("users"));
			if (user_count >= 100)
				targetHashtags.put(hashtags, user_count);
		}
	}

	/**
	 * calculate co-occurence weight
	 * 
	 * @param tweets
	 * @return
	 */
	private static HashMap<String, Integer> countEdgeUser(ArrayList<String[]> tweets) {
		HashMap<String, Integer> hashtag_userCount = new HashMap<String, Integer>();
		HashMap<String, HashSet<String>> hashtag_users = new HashMap<String, HashSet<String>>();
		for (String[] tw : tweets) {
			String tags = tw[4];
			String userid = tw[0];
			String[] tag = tags.split("\\|");
			// remove duplicate
			HashSet<String> tags_set = new HashSet<String>(Arrays.asList(tag));
			ArrayList<String> tags_list = new ArrayList<String>(tags_set);

			for (String t : tags_list) {
				if (hashtag_users.containsKey(t)) {
					hashtag_users.get(t).add(userid);
				} else {
					HashSet<String> users = new HashSet<String>();
					users.add(userid);
					hashtag_users.put(t, users);
				}
			}
		}
		for (String tag : hashtag_users.keySet()) {
			hashtag_userCount.put(tag, hashtag_users.get(tag).size());
		}

		return hashtag_userCount;
	}
	
	/**
	 * write edge file
	 * @throws IOException 
	 * 
	 */
	private static void writeEdgeFile(String fileName) throws IOException
	{
		FileWriter out = new FileWriter(fileName);
		CSVPrinter printer = CSVFormat.DEFAULT.withHeader("Source","Target", "frequency").print(out);

		for (String edge : edge_count.keySet()) {
			String[] nodePoint = edge.split(",");
			printer.printRecord(nodePoint[0],nodePoint[1], edge_count.get(edge));
		}
		out.close();
	}
	
	/**
	 * write node file
	 * @throws IOException 
	 */
	private static void writeNodeFile(String fileName) throws IOException
	{
		HashMap<String,Integer> nodes = new HashMap<String,Integer>();
		FileWriter out = new FileWriter(fileName);
		CSVPrinter printer = CSVFormat.DEFAULT.withHeader("Id", "users","Label").print(out);

		for (String edge : edge_count.keySet()) {
			String[] nodePoint = edge.split(",");
			if(!nodes.containsKey(nodePoint[0]))
			{
				nodes.put(nodePoint[0], targetHashtags.get(nodePoint[0]));
			}
			if(!nodes.containsKey(nodePoint[1]))
			{
				nodes.put(nodePoint[1], targetHashtags.get(nodePoint[1]));
			}

		}
		
		for(String node:nodes.keySet())
		{
			int user = targetHashtags.get(node);
			if(user>=2000)
			{
			printer.printRecord(node, user,node);
			}
			else
			{
			printer.printRecord(node, user,"");	
			}
		}
		out.close();
	}
	
}
