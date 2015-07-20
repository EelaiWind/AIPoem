package ai;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import ai.GeneticAlgorithm.GeneticAlgorithm;
import ai.word.WordPile;

public class MainClass {
	
	public static void main(String[] args){
		
		/*Point[] array1 = new Point[2];
		Point[] array2 = new Point[2];
		
		array1[0] = new Point("AAA", 1, 2);
		array1[1] = new Point("BBB", 5, 15);

		for (int i = 0 ; i < array1.length ; i++)
			array2[i] = array1[i];
		
		for (Point p : array1)
			System.out.println(p.toString());
		for (Point p : array2)
			System.out.println(p.toString());
		System.out.println();
		
		array1[0] = new Point(array2[1].name, array2[1].xy.getX(), array2[1].xy.getY());
		array1[1] = new Point(array2[1].name, array2[1].xy.getX(), array2[1].xy.getY());
		for (Point p : array1)
			System.out.println(p.toString());
		for (Point p : array2)
			System.out.println(p.toString());
		System.out.println();
		
		array1[0].name = new String("GG");
		for (Point p : array1)
			System.out.println(p.toString());
		for (Point p : array2)
			System.out.println(p.toString());*/
		
		JSONObject json = new JSONObject(ReadFile("wordPile.json"));
		WordPile wordPile = new WordPile(json);
		//WriteToFile("wordPile.txt",wordPile.toString());
		new GeneticAlgorithm(8, 5, wordPile).Evole();
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
