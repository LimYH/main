package application;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class LocalStorage {
	private static final String TEST_TXT = "test.txt";
	/*
	 * below is for testing purpose(to be removed) Assume that APIs similar to
	 * CE2 functionality provided.
	 */
	private static File file;

	public LocalStorage() {
		file = new File(TEST_TXT);
		try{
			file.createNewFile();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		
	}

	public ArrayList<String> readFile() {
		ArrayList<String> textLine = new ArrayList<String>();
		FileInputStream fIn = null;
		try {
			fIn = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
        String aDataRow = "";
        try {
			while ((aDataRow = myReader.readLine()) != null) 
			{
			    textLine.add(aDataRow);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			myReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return textLine;
	}

	public void clear() {
		saveToFile(new ArrayList<String>());
	}

	public int changePath(String textContent) { 
		return 1;
	}

	public void saveToFile(ArrayList<String> list) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for (String elem : list) {;
				bw.append(elem);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
		}
	}
}
