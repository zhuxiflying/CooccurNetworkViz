package edu.psu.geovista.cooccurs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class provide methods to generate a hashtag co-occurrence network from
 * tweets data, write network to JSON file or CSV file.
 */
public class CooccurNetworkBuilder {

	private static HashMap<String, Integer> nodes = null;
	private static HashMap<String, Integer> edges = null;
	private static String dataFolder = "E:\\xjz5168\\Geotxt\\2017-11-27_newtweets\\";
	private static String outPutFolder = "E:\\xjz5168\\Geotxt\\Data\\";

	public static void main(String[] args) throws Exception {

//		for (int i = 12; i < 32; i++) {

		int i = 10;
		String name = "tweettxt_Jan_" + i + ".csv";
		String fileName = dataFolder + name;


		extractNetwork(fileName);
//		writeCSVfile("test");
		writeNetworkToJSon(outPutFolder+"test.json");
//		}

	}

	/**
	 * This method generate a hashtag co-occurrence network from tweets data input:
	 * the fileName of tweets data, the column "Hashtags_in_text" recorded the
	 * hashtags adopted in tweet output:
	 * 
	 * @throws IOException
	 */
	private static void extractNetwork(String fileName) throws IOException {

		nodes = new HashMap<String, Integer>();
		edges = new HashMap<String, Integer>();

		Reader in = new FileReader(fileName);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {

			String hashtags = record.get("Hashtags_in_text");
			// if the tweets has hashtag;
			if (!hashtags.equals("")) {
				String[] tags = hashtags.split("\\|");

				for (int j = 0; j < tags.length; j++) {
					String tag1 = tags[j];


					// count the popularity of the the hashtag
					if (nodes.containsKey(tag1)) {
						int popularity = nodes.get(tag1);
						popularity++;
						nodes.put(tag1, popularity);
					} else {
						nodes.put(tag1, 1);
					}

					// if the tweets has more than one hashtag
					for (int k = j + 1; k < tags.length; k++) {
						String tag2 = tags[k];

						// check both direction of the co-occurrence
						String edge1 = tag1 + "," + tag2;
						String edge2 = tag2 + "," + tag1;
						if (edges.containsKey(edge1)) {
							int popularity = edges.get(edge1);
							popularity++;
							edges.put(edge1, popularity);
						} else if (edges.containsKey(edge2)) {
							int popularity = edges.get(edge2);
							popularity++;
							edges.put(edge2, popularity);
						} else {
							edges.put(edge1, 1);
						}
					}

				}
			}
		}
		return;
	}

	/**
	 * This method write generated network to JSON file This method should invoked
	 * after method extractNetwork. output: JSON file records nodes and edges with
	 * weight value
	 * 
	 * @throws FileNotFoundException
	 */
	private static void writeNetworkToJSon(String fileName) throws FileNotFoundException {
		ArrayList<JSONObject> edge_json = new ArrayList<JSONObject>();
		HashSet<String> nodesSet = new HashSet<String>();
		ArrayList<JSONObject> node_json = new ArrayList<JSONObject>();

		for (String edge : edges.keySet()) {
			if (edges.get(edge) > 20) {
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

		PrintWriter out = new PrintWriter(fileName);
		out.println(data.toString());
		out.close();
	}

	
	/**
	 * The method write nodes and edges of generated network to CSV file. The method should
	 * invoke after extractNetwork method.
	 * @output: CSV file records nodes and edges with weight value.
	 * @throws FileNotFoundException
	 */
	private static void writeCSVfile(String fileNmae) throws IOException {
		
		String nodeFile = outPutFolder + fileNmae +"_node.csv";
		String edgeFile = outPutFolder + fileNmae +"_edge.csv";
		writeNodefile(nodeFile);
		writeEdgefile(edgeFile);

	}
	
	/**
	 * The method write nodes of generated network to CSV file. The method should
	 * invoke after extractNetwork method.
	 * @output: CSV file records nodes with weight value.
	 * @throws FileNotFoundException
	 */
	private static void writeNodefile(String fileNmae) throws IOException {
		FileWriter out = new FileWriter(fileNmae);
		CSVPrinter printer = CSVFormat.DEFAULT.withHeader("node", "frequency").print(out);

		for (String node : nodes.keySet()) {
			printer.printRecord(node, nodes.get(node));
		}
		out.close();
	}
	
	/**
	 * The method write nodes of generated network to CSV file. The method should
	 * invoke after extractNetwork method.
	 * @output: CSV file records nodes with weight value.
	 * @throws FileNotFoundException
	 */
	private static void writeEdgefile(String fileNmae) throws IOException {
		FileWriter out = new FileWriter(fileNmae);
		CSVPrinter printer = CSVFormat.DEFAULT.withHeader("node1","node2", "frequency").print(out);

		for (String edge : edges.keySet()) {
			String[] nodePoint = edge.split(",");
			printer.printRecord(nodePoint[0],nodePoint[1], edges.get(edge));
		}
		out.close();
	}

}
