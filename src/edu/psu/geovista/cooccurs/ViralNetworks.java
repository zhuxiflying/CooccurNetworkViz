package edu.psu.geovista.cooccurs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;

public class ViralNetworks {

	private static HashMap<String, Integer> nodes = null;
	private static HashMap<String, Integer> edges = null;
	private static HashSet<String> viral_topic = null;
	private static String dataFolder = "E:\\xjz5168\\Geotxt\\Data\\";

	public static void main(String[] args) throws IOException {
		loadViralTopic();
		loadNetwork();
		System.out.println(nodes.size());
		System.out.println(edges.size());
		System.out.println(viral_topic.size());
		String fileName = dataFolder + "viral_0.json";
		writeViralNetworkToJSon(fileName);

	}

	private static void writeNetworkToJSon(String fileName) throws FileNotFoundException {
		ArrayList<JSONObject> edge_json = new ArrayList<JSONObject>();
		HashSet<String> nodesSet = new HashSet<String>();
		ArrayList<JSONObject> node_json = new ArrayList<JSONObject>();

		for (String edge : edges.keySet()) {

			String[] tags = edge.split(",");
			int frequency = edges.get(edge);
			if (frequency > 2) {
				if (viral_topic.contains(tags[0]) || viral_topic.contains(tags[1])) {
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
		}

		JSONArray links = new JSONArray(edge_json);
		JSONArray hashtags = new JSONArray(node_json);
		JSONObject data = new JSONObject();
		data.put("links", links);
		data.put("nodes", hashtags);

		PrintWriter out = new PrintWriter(fileName);
		out.println(data.toString());
		out.close();
	}

	private static void writeViralNetworkToJSon(String fileName) throws FileNotFoundException {
		ArrayList<JSONObject> edge_json = new ArrayList<JSONObject>();
		HashSet<String> nodesSet = new HashSet<String>();
		ArrayList<JSONObject> node_json = new ArrayList<JSONObject>();

		for (String hashtag : viral_topic) {
			JSONObject node = new JSONObject();
			node.put("id", hashtag);
			node.put("value", nodes.get(hashtag));
			node_json.add(node);

			for (String hashtag2 : viral_topic) {

				String edge = hashtag + "," + hashtag2;
				if (edges.containsKey(edge)) {
					int frequency = edges.get(edge);

					JSONObject jo = new JSONObject();
					jo.put("source", hashtag);
					jo.put("target", hashtag2);
					jo.put("value", frequency);
					edge_json.add(jo);

				}
			}
		}

		JSONArray links = new JSONArray(edge_json);
		JSONArray hashtags = new JSONArray(node_json);
		JSONObject data = new JSONObject();
		data.put("links", links);
		data.put("nodes", hashtags);

		PrintWriter out = new PrintWriter(fileName);
		out.println(data.toString());
		out.close();
	}

	private static void loadViralTopic() throws IOException {
		viral_topic = new HashSet<String>();
		String viralFile = dataFolder + "viral_topics_0.csv";
		Reader in = new FileReader(viralFile);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String hashtags = record.get("hashtag");
			Double virality = Double.valueOf(record.get("virality_index"));
			if (virality > 5) {
				viral_topic.add(hashtags);
			}
		}
	}

	private static void loadNetwork() throws IOException {
		String nodeFile = dataFolder + "test_node.csv";
		loadNodeFile(nodeFile);
		String edgeFile = dataFolder + "test_edge.csv";
		loadEdgeFile(edgeFile);
	}

	private static void loadNodeFile(String fileName) throws IOException {

		nodes = new HashMap<String, Integer>();
		Reader in = new FileReader(fileName);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String hashtags = record.get("node");
			int frequency = Integer.valueOf(record.get("frequency"));
			nodes.put(hashtags, frequency);
		}
	}

	private static void loadEdgeFile(String fileName) throws IOException {

		edges = new HashMap<String, Integer>();
		Reader in = new FileReader(fileName);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String hashtags1 = record.get("node1");
			String hashtags2 = record.get("node2");
			int frequency = Integer.valueOf(record.get("frequency"));
			String edge = hashtags1 + "," + hashtags2;
			edges.put(edge, frequency);
		}
	}
}
