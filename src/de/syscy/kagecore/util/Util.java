package de.syscy.kagecore.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Random;

import lombok.Getter;

public class Util {
	private static final @Getter Random random = new Random();

	public static boolean isNumber(String string) {
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(string, pos);

		return string.length() == pos.getIndex();
	}
	
	public static int clamp(int value, int min, int max) {
	    return Math.max(min, Math.min(max, value));
	}
	
	public static double clamp(double value, double min, double max) {
	    return Math.max(min, Math.min(max, value));
	}
	
	public static float clamp(float value, float min, float max) {
	    return Math.max(min, Math.min(max, value));
	}

	public static void copy(InputStream inputStream, File file) {
		try {
			OutputStream outputStream = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int length;
			
			while((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}
			
			outputStream.close();
			inputStream.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}