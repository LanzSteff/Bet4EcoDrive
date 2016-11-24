package com.example.bet4ecodrive;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.cordova.DroidGap;

import android.webkit.WebView;

public class Bet4EcoDriveService {
	private WebView mAppView;
	private DroidGap mGap;

	public Bet4EcoDriveService(DroidGap gap, WebView view)
	{
		mAppView = view;
		mGap = gap;
	}

	public ArrayList<String[]> readFile() {
		ArrayList<String[]> list = new ArrayList<String[]>();
		String csvFile = "..\\..\\test1.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] splitLine = line.split(cvsSplitBy);
				list.add(splitLine);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

}