package com.roadrunner.hollysgame.model;

import java.util.Comparator;

import com.badlogic.gdx.utils.SerializationException;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class HighScore {
	public static final String highScoreFilename = "hollysgame_highScore.json";
	public static final int MaxCount = 5;
	
	private String name;
	private Duration time;
	
	public HighScore() {}
	
	public HighScore(String name, Duration time) {
		this.name = name;
		this.time = time;
	}
	
	public String getName() {
		return name;
	}
	
	public Duration getTime() {
		return time;
	}
	
	private static PeriodFormatter formatter = null;
	
	public static PeriodFormatter getPeriodFormatter() {
		if(formatter == null) {
			PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
			builder.printZeroIfSupported().minimumPrintedDigits(2).appendMinutes().appendSeparator(":").printZeroIfSupported().minimumPrintedDigits(2).appendSeconds().appendSeparator(":").printZeroIfSupported().minimumPrintedDigits(3).appendMillis();
			formatter = builder.toFormatter();
		}
		return formatter;
	}
	
	private static HighScore[] highScores = null;
	
	public static HighScore[] getHighScores() {
		if(highScores == null) {
			FileHandle hsFileHandle = Gdx.files.external(highScoreFilename);
			if(hsFileHandle.exists()) {
				highScores = new HighScore[MaxCount];
				try {
					JsonValue json = new JsonReader().parse(hsFileHandle);
					for (int i = 0; i < json.size && i < highScores.length; i++) {
						JsonValue hsValue = json.get(i);
						if (!hsValue.isNull()) {
							highScores[i] = new HighScore(hsValue.getString("name"), new Duration(hsValue.getLong("time")));
						}
					}
				} catch (SerializationException e) {
					highScores = new HighScore[MaxCount];
				}
			} else {
				highScores = new HighScore[MaxCount];
			}
		}
		return highScores;
	}
	
	public static void saveHighScores() {
		HighScore[] highScores = getHighScores();
		FileHandle highScoreFileHandle = Gdx.files.external(highScoreFilename);
		JsonValue json = new JsonValue(JsonValue.ValueType.array);
		for (HighScore hs: highScores) {
			if (hs != null) {
				JsonValue hsValue = new JsonValue(JsonValue.ValueType.object);
				hsValue.addChild("name", new JsonValue(hs.name));
				hsValue.addChild("time", new JsonValue(hs.time.getMillis()));
				json.addChild(hsValue);
			}
		}
		String hsJson = json.toJson(JsonWriter.OutputType.json);
		highScoreFileHandle.writeString(hsJson, false);
	}
	
	public static class HighScoreComparator implements Comparator<HighScore> {
		@Override
		public int compare(HighScore o1, HighScore o2) {
			if(o1 == o2) {
				return 0;
			} else if(o1 == null) {
				return 1;
			} else if(o2 == null) {
				return -1;
			}
			return o2.getTime().compareTo(o1.getTime());
		}
	}
}
