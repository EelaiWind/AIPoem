package ai;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import ai.GeneticAlgorithm.GeneticAlgorithm;
import ai.net.JSONReader;
import ai.word.WordPile;

public class MainClass {
	private final static int NET_SOURCE = 0, FILE_SOURCE = 1;
	
	public static void main(String[] args){
		WordPile wordPile;
		/*=====================================*/
		/*選擇字詞來源 NET_SOURCE(從conceptnet) 或 FILE_SOURCE(wordPile.json)*/
		final int SOURCE = FILE_SOURCE;
		/*如果來源是NET_SOURCE則要指定主題*/
		final String topic = new String("狗");
		/*=====================================*/
		
		switch (SOURCE){
		case NET_SOURCE :
			wordPile = new WordPile(JSONReader.GetWordList(topic));
			new GeneticAlgorithm(8, 5, wordPile).Evole();
			WriteToFile("wordPile.json", wordPile.GetJSONString());
			break;
		case FILE_SOURCE:
			wordPile = new WordPile(new JSONObject(ReadFile("wordPile.json")));
			new GeneticAlgorithm(8, 5, wordPile).Evole();
			break;
		default:
			System.err.println("error Incorrect source");
			System.exit(1);
		}
		
		
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

class Point{
	XY xy;
	String name;
	
	public Point(String name, int x, int y) {
		this.name = name;
		this.xy = new XY(x,y);
	}
	
	public String toString(){
		return name+"("+xy.getX()+","+xy.getY()+")";
	}
}

class XY{
	int x,y;
	public XY(int x,int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
}
