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
 * basic statistics measures from raw data: hashtag frequency, hashtag_user count, hashtag degree, hashtag weighted degree;
 */
public class HashtagStatistics {

	// private static String dataFolder = "D:\\Data\\2017-11-27_newtweets\\";
	private static String dataFolder = "D:\\data\\geotwitter01_1\\New folder\\";
	private static String outputFolder = "D:\\data\\geotwitter\\hashtag_frequency\\";
	private static HashMap<String, Integer> hashtag_frequency = null;
	private static HashMap<String, HashSet<String>> hashtag_users = null;
	private static HashMap<String, HashSet<String>> hashtag_degree = null;

	public static void main(String[] args) throws IOException {

		File f = new File(dataFolder);
		for (final File fileEntry : f.listFiles()) {
			String fileName = fileEntry.getName();
			countFreqency(fileName, fileName);
		}
	}

	/*
	 * count frequency and users of hashtags
	 */
	private static void countFreqency(String input, String output) throws IOException {

		hashtag_frequency = new HashMap<String, Integer>();
		hashtag_users = new HashMap<String, HashSet<String>>();

		int count = 0;
		Reader in = new FileReader(dataFolder + input);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			count++;
			String hashtags = record.get("Hashtags_in_text");
			String userid = record.get("User_id");
			
			// if the tweets has hashtag;
			if (!hashtags.equals("")) {
				String[] tags = hashtags.split("\\|");

				// remove duplicate
				HashSet<String> tags_set = new HashSet<String>(Arrays.asList(tags));
				ArrayList<String> tags_list = new ArrayList<String>(tags_set);

				for (int j = 0; j < tags_list.size(); j++) {
					String tag1 = tags_list.get(j);
					// count the popularity of the hashtag
					if (hashtag_frequency.containsKey(tag1)) {
						int popularity = hashtag_frequency.get(tag1);
						popularity++;
						hashtag_frequency.put(tag1, popularity);
					} else {
						hashtag_frequency.put(tag1, 1);
					}
					// count the users of the hashtag
					if (hashtag_users.containsKey(tag1)) {
						HashSet<String> users = hashtag_users.get(tag1);
						users.add(userid);
						hashtag_users.put(tag1, users);
					} else {
						HashSet<String> users = new HashSet<String>();
						users.add(userid);
						hashtag_users.put(tag1, users);
					}
				}
			}
		}
		System.out.println(input + "," + count);

//		FileWriter out = new FileWriter(outputFolder + output);
//		CSVPrinter printer = CSVFormat.DEFAULT.withHeader("Hashtag", "Frequency","Users").print(out);
//		for (String hashtag : hashtag_frequency.keySet()) {
//			printer.printRecord(hashtag , hashtag_frequency.get(hashtag),hashtag_users.get(hashtag).size());
//		}
//		out.close();
		
	}

	/*
	 * count number of users
	 */
	private static void countUsers() throws IOException {
		hashtag_users = new HashMap<String, HashSet<String>>();

		for (int i = 10; i < 32; i++) {
			String name = "tweettxt_Jan_" + i + ".csv";
			String fileName = dataFolder + name;
			Reader in = new FileReader(fileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
			for (CSVRecord record : records) {

				String hashtags = record.get("Hashtags_in_text");
				String userid = record.get("User_id");
				// if the tweets has hashtag;
				if (!hashtags.equals("")) {
					String[] tags = hashtags.split("\\|");

					// remove duplicate
					HashSet<String> tags_set = new HashSet<String>(Arrays.asList(tags));
					ArrayList<String> tags_list = new ArrayList<String>(tags_set);

					for (int j = 0; j < tags_list.size(); j++) {
						String tag1 = tags_list.get(j);
						// count the users of the hashtag
						if (hashtag_users.containsKey(tag1)) {
							HashSet<String> users = hashtag_users.get(tag1);
							users.add(userid);
							hashtag_users.put(tag1, users);
						} else {
							HashSet<String> users = new HashSet<String>();
							users.add(userid);
							hashtag_users.put(tag1, users);
						}

					}
				}
			}
		}

		for (String hashtag : hashtag_users.keySet()) {
			System.out.println(hashtag + "," + hashtag_users.get(hashtag).size());
		}

	}

	/*
	 * count degree of hashtagss
	 */
	private static void countDegree() throws IOException {
		hashtag_degree = new HashMap<String, HashSet<String>>();

		int index = 0;
		for (int i = 10; i < 32; i++) {
			String name = "tweettxt_Jan_" + i + ".csv";
			String fileName = dataFolder + name;
			Reader in = new FileReader(fileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
			for (CSVRecord record : records) {

				String hashtags = record.get("Hashtags_in_text");
				// if the tweets has hashtag;
				if (!hashtags.equals("")) {
					String[] tags = hashtags.split("\\|");

					// remove duplicate
					HashSet<String> tags_set = new HashSet<String>(Arrays.asList(tags));
					ArrayList<String> tags_list = new ArrayList<String>(tags_set);

					for (int j = 0; j < tags_list.size(); j++) {
						String tag1 = tags_list.get(j);

						for (int k = 0; k < tags_list.size(); k++) {
							String tag2 = tags_list.get(k);
							if (!tag1.equals(tag2)) {
								// count the degree of the hashtag
								if (hashtag_degree.containsKey(tag1)) {
									HashSet<String> neighbors = hashtag_degree.get(tag1);
									neighbors.add(tag2);
									hashtag_degree.put(tag1, neighbors);
								} else {
									HashSet<String> neighbors = new HashSet<String>();
									neighbors.add(tag2);
									hashtag_degree.put(tag1, neighbors);
								}
							}
						}
					}
				}
			}
		}

		// int size = 0;
		// for(String hashtag: hashtag_degree.keySet())
		// {
		// if(hashtag_degree.get(hashtag).size()==1)size++;
		// }
		// System.out.println(size);

		// // write hashtag degree file
		// String hashtag_degreeFile = outputFolder + "hashtag_degree2.csv";
		// FileWriter out = new FileWriter(hashtag_degreeFile);
		// CSVPrinter printer = CSVFormat.DEFAULT.withHeader("hashtag",
		// "frequency").print(out);
		// for (String hashtag : hashtag_degree.keySet()) {
		// if (hashtag_degree.get(hashtag).size() > 1)
		// printer.printRecord(hashtag, hashtag_degree.get(hashtag).size());
		// }
		// out.close();
		//
		// HashMap<Integer, Integer> degree_distribution = new HashMap<Integer,
		// Integer>();
		// for (String hashtag : hashtag_degree.keySet()) {
		// int degree = hashtag_degree.get(hashtag).size();
		// if (degree_distribution.containsKey(degree)) {
		// int frequency = degree_distribution.get(degree);
		// frequency++;
		// degree_distribution.put(degree, frequency);
		// } else {
		// degree_distribution.put(degree, 1);
		// }
		// }
		//
		// // write degree distribution file
		// String degree_distributionFile = outputFolder +
		// "degree_distribution.csv";
		// FileWriter out2 = new FileWriter(degree_distributionFile);
		// CSVPrinter printer2 = CSVFormat.DEFAULT.withHeader("degree",
		// "frequency").print(out2);
		// for (Integer degree : degree_distribution.keySet()) {
		// printer2.printRecord(degree, degree_distribution.get(degree));
		// }
		// out2.close();

	}
}
