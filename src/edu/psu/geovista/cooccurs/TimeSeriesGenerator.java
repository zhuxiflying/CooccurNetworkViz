package edu.psu.geovista.cooccurs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * This class provide methods to generate time series of hashtags
 */
public class TimeSeriesGenerator {

	private static HashSet<String> targetHashtags = null;
	private static HashMap<String, int[]> hashtags_ts = null;
	
	// The file recorded the frequency of the hashtags
	private static String hashtag_frequecy = "E:\\xjz5168\\Geotxt\\Data\\HashtagStatistics.csv";
	private static String dataFolder = "E:\\xjz5168\\Geotxt\\2017-11-27_newtweets\\";

	public static void main(String[] args) throws IOException, Exception {

		loadTargetHashTag();

		hashtags_ts = new HashMap<String, int[]>();

		for (int i = 10; i < 32; i++) {

			String name = "tweettxt_Jan_" + i + ".csv";
			String fileName = dataFolder + name;
			System.out.println(name);
			generateTimeSeries(fileName);

		}

		for (String tag : hashtags_ts.keySet()) {
			System.out.print(tag + ",");
			int[] ts = hashtags_ts.get(tag);
			for (int i = 0; i < ts.length - 1; i++) {
				System.out.print(ts[i] + ",");
			}
			System.out.println(ts[ts.length - 1]);
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
		Reader in = new FileReader(hashtag_frequecy);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String hashtags = record.get("Hashtag");
			int frequency = Integer.valueOf(record.get("Times"));
			if (frequency > frequency_threshold) {
				targetHashtags.add(hashtags);
			}
		}
	}

	/**
	 * This method generate a hashtag co-occurrence network from tweets data input:
	 * the fileName of tweets data, the column "Hashtags_in_text" recorded the
	 * hashtags adopted in tweet output:
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	private static void generateTimeSeries(String fileName) throws IOException, ParseException {

		DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		Date date;

		Reader in = new FileReader(fileName);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {

			String hashtags = record.get("Hashtags_in_text");

			// if the tweets has hashtag;
			if (!hashtags.equals("")) {
				String[] tags = hashtags.split("\\|");

				for (int j = 0; j < tags.length; j++) {
					String tag = tags[j];

					// if the hashtag contained in the target hashtag set
					if (targetHashtags.contains(tag)) {
						String dateString = record.get("Time_of_tweet");
						date = dateFormat.parse(dateString);
						cal.setTime(date);
//						int index = cal.get(Calendar.DATE) * 24 + cal.get(Calendar.HOUR_OF_DAY)-240;
						int index = cal.get(Calendar.DATE) - 10;
						if (hashtags_ts.get(tag) != null) {
							int[] ts = hashtags_ts.get(tag);
							ts[index]++;
							hashtags_ts.put(tag, ts);
						} else {
							int[] ts = new int[22];
							ts[index]++;
							hashtags_ts.put(tag, ts);
						}
					}

				}
			}
		}

	}

}
