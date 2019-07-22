package edu.psu.geovista.cooccurs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

//filter tweets by hashtags
public class TweetsFilter {

	private static String communityFile = "D:\\data\\CooccurNetwork\\nodes_community.csv";
	private static String dataFolder = "D:\\Data\\2017-11-27_newtweets\\";
	private static String outPut = "D:\\data\\CooccurNetwork\\";

	private static HashSet<String> target_hashtags = null;
	private static ArrayList<String[]> tweets = null;

	public static void main(String[] args) throws IOException {
		loadTargetHashtag2();
		System.out.println(target_hashtags.size());
		filterTweets();
	}

	private static void filterTweets() throws IOException {
		tweets = new ArrayList<String[]>();
		for (int i = 10; i < 32; i++) {
			String name = "tweettxt_Jan_" + i + ".csv";
			String fileName = dataFolder + name;

			Reader in = new FileReader(fileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
			for (CSVRecord record : records) {

				String hashtags = record.get("Hashtags_in_text");
				String text = record.get("Tweet_text");
				// if the tweets has hashtag;
				if (!hashtags.equals("")) {
					String[] tags = hashtags.split("\\|");

					// remove duplicate
					HashSet<String> tags_set = new HashSet<String>(Arrays.asList(tags));
					ArrayList<String> tags_list = new ArrayList<String>(tags_set);

					for (int j = 0; j < tags_list.size(); j++) {
						String tag1 = tags_list.get(j);
						if (target_hashtags.contains(tag1)) {
							String[] data = { text, tag1 };
							tweets.add(data);
						}
					}
				}
			}
			System.out.println(fileName + "," + tweets.size());
		}
		
		String fileName = outPut+ "politic.csv";
		FileWriter out = new FileWriter(fileName);
		CSVPrinter printer = CSVFormat.DEFAULT.withHeader("text", "hashtag").print(out);

		for (String[] data : tweets) {
			printer.printRecord(data[0],data[1]);
		}
		out.close();
	}

	private static void loadTargetHashtag() throws IOException {
		target_hashtags = new HashSet<String>();
		Reader in = new FileReader(communityFile);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String hashtag = record.get("Id");
			int users = Integer.valueOf(record.get("users"));
			String community_id = record.get("modularity_class");
			if (community_id.equals("10")&&users>2000)
				target_hashtags.add(hashtag);
		}
	}
	
	private static void loadTargetHashtag2() throws IOException {
		target_hashtags = new HashSet<String>();
		target_hashtags.add("ObamaFarewell");
		target_hashtags.add("ThankYouObama");
		target_hashtags.add("POTUS");
		target_hashtags.add("trump");
		target_hashtags.add("alternativefacts");
		target_hashtags.add("FakeNews");
		target_hashtags.add("DonaldTrump");
		target_hashtags.add("PresidentTrump");
		target_hashtags.add("Inauguration");
		target_hashtags.add("TrumpInauguration");
		target_hashtags.add("WomensMarch");
		target_hashtags.add("womensmarch");
		target_hashtags.add("NoBanNoWall");
		target_hashtags.add("MuslimBan");
		target_hashtags.add("notmypresident");
		target_hashtags.add("TheResistance");
		target_hashtags.add("MakeAmericaGreatAgain");
		target_hashtags.add("GoldenShowers");
		

	}
	

}
