

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;

public class CooccurNetworkBuilder {

	public static void main(String[] args) throws Exception {

		HashMap<String, Integer> nodes = new HashMap<String, Integer>();
		HashMap<String, Integer> edges = new HashMap<String, Integer>();

		int i = 21;
		String folder = "E:\\xjz5168\\Geotxt\\2017-11-27_newtweets\\";
		String fileNmae = "tweettxt_Jan_" + i + ".csv";
		String csvFile = folder + fileNmae;

		Reader in = new FileReader(csvFile);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {

			String hashtags = record.get("Hashtags_in_text");
			String replace = hashtags.replace("|", ",");
			String[] tags = replace.split(",");

			for (int j = 0; j < tags.length; j++) {
				String tag = tags[j];
				if (nodes.containsKey(tag)) {
					int popularity = nodes.get(tag);
					popularity++;
					nodes.put(tag, popularity);
				} else {
					nodes.put(tag, 1);
				}

				for (int k = j + 1; k < tags.length; k++) {
					String tag2 = tags[k];
					String edge = tag + "," + tag2;
					if (edges.containsKey(edge)) {
						int popularity = edges.get(edge);
						popularity++;
						edges.put(edge, popularity);
					} else {
						edges.put(edge, 1);
					}
				}

			}
		}
		
		ArrayList<JSONObject > edge_json = new ArrayList<JSONObject >();
		
		for(String edge:edges.keySet())
		{
			if(edges.get(edge)>20)
			{
				String[] tags = edge.split(",");
				int frequency = edges.get(edge);
				JSONObject jo = new JSONObject();
				jo.put("source", tags[0]);
				jo.put("target", tags[1]);
				jo.put("value", frequency);
				edge_json.add(jo);

			}
		}
		
		
		JSONArray ja = new JSONArray(edge_json);
		
		System.out.println(ja.toString());
		
	}

}
