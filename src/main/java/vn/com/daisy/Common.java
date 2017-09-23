package vn.com.daisy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Common {
	public static boolean DEBUG = false;

	public static void print(String header, String body) {
		System.out.println("[" + header + "]: " + body);
	}

	static ArrayList<String> removeDuplicates(List<String> al1) {

		// Store unique items in result.
		ArrayList<String> result = new ArrayList<>();

		// Record encountered Strings in HashSet.
		HashSet<String> set = new HashSet<>();

		// Loop over argument list.
		for (String item : al1) {
			// If String is not in set, add it to the list and the set.
			if (!set.contains(item)) {
				result.add(item);
				set.add(item);
			}
		}
		return result;
	}

	public static List<String> readFile(String urlFile) {
		List<String> result = new ArrayList<>();
		String currentLine = null;
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(urlFile), "UTF8"));
			try {
				while ((currentLine = bufferedReader.readLine()) != null) {
					result.add(currentLine);
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			Common.print("writeFile()", "not support Encoder");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			Common.print("writeFile()", "File not found");
		}
		return result;
	}

	public static boolean checkUpdate(List<String> strs, String str) {
		return strs.contains(str);

	}

	public static void writeFile(String urlFile, String str) {
		File file = new File(urlFile);
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			if (!file.exists())
				file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			fw.write(str);
			fw.write("\n");

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				fw.close();
				bw.close();
			} catch (IOException e) {
				Common.print("writeFile()", "Cannot free buffer Reader");
				e.printStackTrace();
			}

		}

	}

}
