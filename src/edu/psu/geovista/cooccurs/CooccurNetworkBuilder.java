package edu.psu.geovista.cooccurs;

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;

public class CooccurNetworkBuilder {

	public static void main(String[] args) throws Exception {

		for (int i = 12; i < 32; i++) {

			HashMap<String, Integer> nodes = new HashMap<String, Integer>();
			HashMap<String, Integer> edges = new HashMap<String, Integer>();

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

			ArrayList<JSONObject> edge_json = new ArrayList<JSONObject>();
			HashSet<String> nodesSet = new HashSet<String>();
			ArrayList<JSONObject> node_json = new ArrayList<JSONObject>();

			for (String edge : edges.keySet()) {
				if (edges.get(edge) > 10) {
					String[] tags = edge.split(",");
					int frequency = edges.get(edge);
					JSONObject jo = new JSONObject();
					jo.put("source", tags[0]);
					jo.put("target", tags[1]);
					jo.put("value", frequency);
					edge_json.add(jo);
					if (!nodesSet.contains(tags[0])) {
						nodesSet.add(tags[0]);
						JSONObject node = new JSONObject();
						node.put("id", tags[0]);
						node.put("value", nodes.get(tags[0]));
						node_json.add(node);
					}
					if (!nodesSet.contains(tags[1])) {
						nodesSet.add(tags[1]);
						JSONObject node = new JSONObject();
						node.put("id", tags[1]);
						node.put("value", nodes.get(tags[1]));
						node_json.add(node);
					}

				}
			}

			JSONArray links = new JSONArray(edge_json);
			JSONArray hashtags = new JSONArray(node_json);
			JSONObject data = new JSONObject();
			data.put("links", links);
			data.put("nodes", hashtags);

			String filename = "C:\\Users\\xjz5168\\eclipse-workspace\\CooccurNetworkViz\\WebContent\\data" + i
					+ ".json";
			System.out.println(filename);
			PrintWriter out = new PrintWriter(filename);
			out.println(data.toString());
			out.close();
		}

	}

}
