package edu.psu.geovista.cooccurs;

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

public class ViralTopicsWordCloud {

	private static List<WordFrequency> wordFrequencies = null;
	private static String dataFolder = "E:\\xjz5168\\Geotxt\\Data\\";

	public static void main(String[] args) throws Exception {

		
		for(int index=0;index<22;index++)
		{
		String fileName = dataFolder + "viral_topics_"+index+".csv";
		int date = index+10;
		String output = dataFolder+"ViralTopics_Jan"+date+".png";
		loadData(fileName);
		drawWordCloud(output);
		}

	}

	static void loadData(String fileName) throws IOException {

		wordFrequencies = new ArrayList<WordFrequency>();
		
		Reader in = new FileReader(fileName);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String tags = record.get("hashtag");
			int frequency = Integer.valueOf(record.get("frequency"));
			double virality = Double.valueOf(record.get("virality_index"));
			if (virality > 5) {
				WordFrequency word_fre = new WordFrequency(tags, frequency);
				wordFrequencies.add(word_fre);
			}
		}
		System.out.println(wordFrequencies.size());
	}

	
	
	static void drawWordCloud(String outputImage) throws IOException {

		final Dimension dimension = new Dimension(1500, 1500);
		final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
		wordCloud.setPadding(0);
		wordCloud.setBackground(new RectangleBackground(dimension));
		wordCloud.setColorPalette(new LinearGradientColorPalette(new Color(177,0,38),new Color(255,255,178),15));
		wordCloud.setFontScalar(new SqrtFontScalar(20, 80));
		wordCloud.build(wordFrequencies);
		wordCloud.writeToFile(outputImage);
	}
}
