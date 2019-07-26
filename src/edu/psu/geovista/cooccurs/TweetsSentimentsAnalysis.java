package edu.psu.geovista.cooccurs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class TweetsSentimentsAnalysis {

	private static String folder = "D:\\data\\CooccurNetwork\\";
	private static String tweetsFolder = "D:\\data\\Hashtags_clean\\";


	// lexicon library
	private static HashSet<String> positive = null;
	private static HashSet<String> negative = null;
	private static HashSet<String> anger = null;
	private static HashSet<String> anticipation = null;
	private static HashSet<String> disgust = null;
	private static HashSet<String> fear = null;
	private static HashSet<String> joy = null;
	private static HashSet<String> sadness = null;
	private static HashSet<String> surprise = null;
	private static HashSet<String> trust = null;

	public static void main(String[] args) throws Exception {

		String fileName = folder + "NRC-Emotion-Lexicon.csv";
		loadLexicon(fileName);

		String hashtag = " PennState";
		ArrayList<String> tweets = loadHashtag(hashtag);
		HashMap<String, Integer> sentiments_score = getNRCsentiments(tweets);
		System.out.println(sentiments_score.get("positive"));
		System.out.println(sentiments_score.get("negative"));
		
	}

	// load tweets by hashtag
	private static ArrayList<String> loadHashtag(String hashtag) throws Exception {
		String hashtag_path = tweetsFolder + hashtag + ".csv";
		ArrayList<String> tweets = new ArrayList<String>();
		Reader in = new FileReader(hashtag_path);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String tweet = record.get("text");
			tweets.add(tweet);
		}
		return tweets;
	}

	// calcuate the sentiments for the tweets, implement the NRC method
	private static HashMap<String, Integer> getNRCsentiments(ArrayList<String> tweets) {
		HashMap<String, Integer> sentiments_score = new HashMap<String, Integer>();
		HashMap<String, Integer> word_frequency = wordFrequency(tweets);
		int positive_score=0;
		int negative_score=0;
		int anger_score=0;
		int anticipation_score=0;
		int disgust_score=0;
		int fear_score=0;
		int joy_score=0;
		int sadness_score=0;
		int surprise_score=0;
		int trust_score=0;
		
		for (String word : word_frequency.keySet()) {
			if (positive.contains(word))
				positive_score += word_frequency.get(word);
			if (negative.contains(word))
				negative_score += word_frequency.get(word);
			if (anger.contains(word))
				anger_score += word_frequency.get(word);
			if (anticipation.contains(word))
				anticipation_score += word_frequency.get(word);
			if (disgust.contains(word))
				disgust_score += word_frequency.get(word);
			if (fear.contains(word))
				fear_score += word_frequency.get(word);
			if (joy.contains(word))
				joy_score += word_frequency.get(word);
			if (sadness.contains(word))
				sadness_score += word_frequency.get(word);
			if (surprise.contains(word))
				surprise_score += word_frequency.get(word);
			if (trust.contains(word))
				trust_score += word_frequency.get(word);
		}
		
		sentiments_score.put("positive", positive_score);
		sentiments_score.put("negative", negative_score);
		sentiments_score.put("anger", anger_score);
		sentiments_score.put("anticipation", anticipation_score);
		sentiments_score.put("disgust", disgust_score);
		sentiments_score.put("fear", fear_score);
		sentiments_score.put("joy", joy_score);
		sentiments_score.put("sadness", sadness_score);
		sentiments_score.put("surprise", surprise_score);
		sentiments_score.put("trust", trust_score);
		
		return sentiments_score;
	}

	// calcuate the word frequency for the tweets in a hashtag
	private static HashMap<String, Integer> wordFrequency(ArrayList<String> tweets) {
		HashMap<String, Integer> word_frequency = new HashMap<String, Integer>();
		for (String tweet : tweets) {
			String[] words = tweet.split(" ");
			for (String word : words) {
				if (word_frequency.containsKey(word)) {
					int freq = word_frequency.get(word);
					freq++;
					word_frequency.put(word, freq);
				} else {
					word_frequency.put(word, 1);
				}
			}
		}
		return word_frequency;
	}

	// load the NRC lexicon
	private static void loadLexicon(String fileName) throws Exception {
		positive = new HashSet<String>();
		negative = new HashSet<String>();
		anger = new HashSet<String>();
		anticipation = new HashSet<String>();
		disgust = new HashSet<String>();
		fear = new HashSet<String>();
		joy = new HashSet<String>();
		sadness = new HashSet<String>();
		surprise = new HashSet<String>();
		trust = new HashSet<String>();
		Reader in = new FileReader(fileName);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String word = record.get("word");
			String type = record.get("type");
			int score = Integer.valueOf(record.get("score"));
			if (score == 1) {
				switch (type) {
				case "positive":
					positive.add(word);
					break;
				case "negative":
					negative.add(word);
					break;
				case "anger":
					anger.add(word);
					break;
				case "anticipation":
					anticipation.add(word);
					break;
				case "disgust":
					disgust.add(word);
					break;
				case "fear":
					fear.add(word);
					break;
				case "joy":
					joy.add(word);
					break;
				case "sadness":
					sadness.add(word);
					break;
				case "surprise":
					surprise.add(word);
					break;
				case "trust":
					trust.add(word);
					break;
				}
			}
		}
	}
}
