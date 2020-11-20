package com.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

public class CurrencyConversionManager extends TimerTask {
	private int buildId = 0;
	public static String BUILD_ID_FILE_NAME = "buildID.txt";
	public static String FILE_PATH = "/tmp/exchange/";
	public static int TIMER = 60000;
	private Map<String, Float> currencyMap;

	public CurrencyConversionManager() {
		currencyMap = new HashMap<String, Float>();
	}

	public static void main(String[] args) {
		CurrencyConversionManager t = new CurrencyConversionManager();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(t, 0, TIMER);
	}

	@Override
	public void run() {

		try {
			String dataFromFile = readFile(FILE_PATH + BUILD_ID_FILE_NAME);
			this.processDataFromFile(dataFromFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processDataFromFile(String data) throws Exception {
		JSONObject json = converToJSON(data);
		int bid = json.getInt("buildID");
		if (bid == this.buildId) {
			System.out.println("This is a duplicate Build ID" + bid);
			return;
		}
		this.buildId = bid;
		System.out.println("This is a new build id from File :  " + bid);
		String dataFileName = json.getString("FileName");
		data = readFile(FILE_PATH + dataFileName);
		this.updateInMemoryData(data);
		this.printInMemoryData();
	}

	private void updateInMemoryData(String data) {
		try {
			JSONObject json = converToJSON(data);
			Iterator<String> it = json.keys();

			while (it.hasNext()) {
				String strVal = it.next();
				float val = json.getFloat(strVal);
				currencyMap.put(strVal, val);
				System.out.println(strVal + " - " + val);
			}

			System.out.println("   ");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void printInMemoryData() {

		System.out.println("Cached Data");
		System.out.println("    ");

		for (String currencyCode : currencyMap.keySet()) {
			System.out.println(currencyCode + " : " + currencyMap.get(currencyCode));
		}

	}

	public static String readFile(String path) throws IOException {
		StringBuffer sb = new StringBuffer();
		try {
			File file = new File(path);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			fr.close();

			System.out.println(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static JSONObject converToJSON(String data) throws IOException {
		return new JSONObject(data);
	}
}
