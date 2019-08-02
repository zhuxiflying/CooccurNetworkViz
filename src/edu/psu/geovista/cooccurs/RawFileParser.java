package edu.psu.geovista.cooccurs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.type.Name;

public class RawFileParser {

	private static String dataFolder = "D:\\data\\geotwitter01\\";
	private static String outputFolder = "D:\\data\\geotwitter01_1\\";
	private static MultiPolygon boundary = null;

	public static void main(String[] args) throws Exception {

		File f = new File(dataFolder);
		 for (final File fileEntry : f.listFiles()) {
			 String fileName = fileEntry.getName();
			 String year = fileName.substring(34,38);
			 String month = fileName.substring(39,41);
			 String day = fileName.substring(42,44);
			String output = outputFolder + "tweets_"+year+"_" + month + "_" + day + ".csv";

			 preprocessTwitter(dataFolder+fileName,output);
		    }



//		filterFile();

	}

	private static void filterFile() throws IOException {

		// read boundary
		readShapeFileBoundary();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		String month = "05";
		String day = "01";
		String output = outputFolder + "tweets_2017_" + month + "_" + day + "_CA.csv";
		FileWriter out = new FileWriter(output);
		CSVPrinter printer = CSVFormat.DEFAULT.withHeader("Tweet_id", "Time_of_tweet", "User_id", "Tweet_text",
				"Hashtags_in_text", "x", "y", "Place_id", "Place_type", "Place_full_name", "Place_name", "Country_code",
				"Country", "place_centroid_x", "place_centroid_y").print(out);

		// TODO Auto-generated method stub
		String fileName = "D:\\data\\geotwitter01_1\\tweets_2017_05_01.csv";
		Reader in = new FileReader(fileName);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			double[] location1 = new double[2];
			if (!record.get("x").equals("")) {
				location1[0] = Double.valueOf(record.get("x"));
				location1[1] = Double.valueOf(record.get("y"));
				Coordinate coord = new Coordinate(location1[1], location1[0]);
				Geometry point = geometryFactory.createPoint(coord);
				if(boundary.contains(point))
				{
					printer.printRecord(record);
				}
			}
			else
			{
				location1[0] = Double.valueOf(record.get("place_centroid_x"));
				location1[1] = Double.valueOf(record.get("place_centroid_y"));
				Coordinate coord = new Coordinate(location1[1], location1[0]);
				Geometry point = geometryFactory.createPoint(coord);
				if(boundary.contains(point))
				{
					printer.printRecord(record);
				}
			}
		}

	}

	private static void preprocessTwitter(String input, String output) throws Exception {
		int index = 0;
		FileWriter out = new FileWriter(output);
		CSVPrinter printer = CSVFormat.DEFAULT.withHeader("Tweet_id", "Time_of_tweet", "User_id", "Tweet_text",
				"Hashtags_in_text", "x", "y", "Place_id", "Place_type", "Place_full_name", "Place_name", "Country_code",
				"Country", "place_centroid_x", "place_centroid_y").print(out);
		JSONParser parser = new JSONParser();
		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
			String line;
			while ((line = br.readLine()) != null) {
				// process the line.
				if (!line.equals(",")) {

					JSONObject jsonObject = (JSONObject) parser.parse(line);
					String[] dataitem = extractDataFromJson(jsonObject);
					index++;
					printer.printRecord(dataitem);
				}
			}
		}
		out.close();
		 System.out.print(output+",");
		 System.out.println(index);
	}

	/*
	 * extract data fields from a jsonObject these fields are including:
	 * tweets_date, tweets_id, user_id,hashtags,locations
	 */
	private static String[] extractDataFromJson(JSONObject object) throws IOException {

		
		String[] dataitems = new String[15];

		String tweet_id = (String) object.get("id_str");
		dataitems[0] = tweet_id;

		String date = (String) object.get("created_at");
		dataitems[1] = date;

		JSONObject user = (JSONObject) object.get("user");
		String user_id = (String) user.get("id_str");
		dataitems[2] = user_id;

		String text = (String) object.get("text");
		dataitems[3] = text;

		JSONObject entity = (JSONObject) object.get("entities");
		JSONArray hashtags = (JSONArray) entity.get("hashtags");
		String hashtag = convertHashtag2String(hashtags);
		dataitems[4] = hashtag;

		JSONObject geo = (JSONObject) object.get("geo");
		if (geo != null) {
			JSONArray coordinates = (JSONArray) geo.get("coordinates");
			dataitems[5] = coordinates.get(0).toString();
			dataitems[6] = coordinates.get(1).toString();
		}

		JSONObject place = (JSONObject) object.get("place");
		if (place != null) {
			String place_id = (String) place.get("id");
			dataitems[7] = place_id;
			String place_type = (String) place.get("place_type");
			dataitems[8] = place_type;
			String full_name = (String) place.get("full_name");
			dataitems[9] = full_name;
			String name = (String) place.get("name");
			dataitems[10] = name;
			String country_code = (String) place.get("country_code");
			dataitems[11] = country_code;
			String country = (String) place.get("country");
			dataitems[12] = country;

			JSONObject bounding_box = (JSONObject) place.get("bounding_box");
			JSONArray box = (JSONArray) bounding_box.get("coordinates");
			JSONArray coordinates = (JSONArray) box.get(0);
			double[] centroid = calculateCentroid(coordinates);
			// reverse the order
			dataitems[13] = String.valueOf(centroid[1]);
			dataitems[14] = String.valueOf(centroid[0]);			
		}

		return dataitems;
	}

	/*
	 * convert a set of hashtags to a string concatenated by symbol "|"
	 */
	private static String convertHashtag2String(JSONArray hashtags) {
		String hashtag = "";
		if (hashtags.size() > 0) {
			hashtag = (String) ((JSONObject) hashtags.get(0)).get("text");
			if (hashtags.size() > 1) {
				for (int i = 1; i < hashtags.size(); i++) {
					hashtag = hashtag + "|" + (String) ((JSONObject) hashtags.get(i)).get("text");
					;
				}
			}
		}
		return hashtag;

	}

	/*
	 * calculate the centroid of a bounding box
	 */
	private static double[] calculateCentroid(JSONArray bounding_box) {
		double[] centroids = new double[2];
		for (int i = 0; i < bounding_box.size(); i++) {
			JSONArray coordiantes = (JSONArray) bounding_box.get(i);
			double x1 = Double.valueOf(coordiantes.get(0).toString());
			double y1 = Double.valueOf(coordiantes.get(1).toString());
			centroids[0] += x1;
			centroids[1] += y1;
		}
		centroids[0] = centroids[0] / bounding_box.size();
		centroids[1] = centroids[1] / bounding_box.size();
		return centroids;
	}




	private static void readShapeFileBoundary() throws IOException {
		// read shapefile
		String shapefile = "D:\\data\\geotwitter_CA\\CA_boundary_WGS84\\CA_boundary_WGS843.shp";
		File file = new File(shapefile);
		Map<String, String> connect = new HashMap();
		connect.put("url", file.toURI().toString());

		DataStore dataStore = DataStoreFinder.getDataStore(connect);
		String[] typeNames = dataStore.getTypeNames();
		String typeName = typeNames[0];

		FeatureSource featureSource = dataStore.getFeatureSource(typeName);
		FeatureCollection collection = featureSource.getFeatures();
		FeatureIterator iterator = collection.features();

		while (iterator.hasNext()) {
			Feature feature = iterator.next();
			GeometryAttribute sourceGeometry = feature.getDefaultGeometryProperty();
			GeometryAttribute geom = feature.getDefaultGeometryProperty();
			Name name = geom.getName();
			boundary = (MultiPolygon) geom.getValue();
		}
	}
}
