package edu.psu.geovista.cooccurs;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class ViralityTest {
	
	private static HashMap<String, int[]> hashtag_ts = null;
	private static String dataFolder = "E:\\xjz5168\\Geotxt\\Data\\";
	
	
	public static void main(String[] args) throws Exception
	{
		String TS_file = dataFolder + "Hashtags_TS.csv";
		loadData(TS_file);
        for(int index = 0;index<23;index++)
        {
		String viralTopics =dataFolder +  "viral_topics_"+index+".csv";
		saveToFile(viralTopics,index);
        }
	}
	
	private static void loadData(String fileName) throws Exception
	{
		hashtag_ts = new HashMap<String, int[]>();
		Reader in = new FileReader(fileName);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
		for (CSVRecord record : records) {
			String hashtags = record.get(0);
			int[] ts = new int[record.size()-1];
			for(int i=1;i<record.size();i++)
			{
				ts[i-1] = Integer.valueOf(record.get(i));
			}
			hashtag_ts.put(hashtags,ts);
		}
	}
	
	
	
	private static double testVarility(int[] ts, int index)
	{
		double sum =0;
		int ts_length = ts.length;
		for(int i=0;i<ts.length;i++)
		{
			sum+=(double)ts[i];
		}
		return ts_length*ts[index]/sum;
	}
	
	private static void saveToFile(String fileName, int index) throws IOException
	{
		
		FileWriter out = new FileWriter(fileName);
		CSVPrinter printer = CSVFormat.DEFAULT.withHeader("hashtag", "frequency","virality_index").print(out);
		for (String tag : hashtag_ts.keySet()) {
			int[] ts = hashtag_ts.get(tag);
			double viral_index = testVarility(ts,index);
			printer.printRecord(tag, ts[index],viral_index);
		}
		out.close();
	}

}
