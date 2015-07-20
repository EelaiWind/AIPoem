package ai;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import ai.GeneticAlgorithm.GeneticAlgorithm;
import ai.poem.Template_5_8_1;
import ai.word.WordPile;

public class MainClass {
	
	public static void main(String[] args){
		JSONObject json = new JSONObject(ReadFile("wordPile.json"));
		WordPile wordPile = new WordPile(json);
		//WriteToFile("wordPile.txt",wordPile.toString());
		System.out.println("Done");
		int[][] wordComposition =  new int[][]{{2,2,1},{2,2,1},{2,3},{2,3},{2,2,1},{2,2,1},{2,2,1},{2,2,1}};
		new GeneticAlgorithm(wordPile, new Template_5_8_1(wordComposition)).Evole();
	}
	
	public static String ReadFile(String fileName){
		StringBuilder sb = new StringBuilder();
		int count;
		byte[] buff = new byte[4096];
		
		try {
			FileInputStream fin = new FileInputStream(fileName);
			BufferedInputStream bin = new BufferedInputStream(fin);
			while ((count = bin.read(buff)) != -1){
				sb.append(new String(buff,0,count));
			}
			bin.close();
			fin.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	public static void WriteToFile(String fileName, String content){
		
		try {
			FileOutputStream fout = new FileOutputStream(fileName);
			BufferedOutputStream buf = new BufferedOutputStream(fout);
			buf.write(content.getBytes());
			buf.close();
			fout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
