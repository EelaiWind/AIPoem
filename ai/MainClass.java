package ai;

import ai.net.JSONReader;
import ai.word.ChineseWord;

public class MainClass {
		public static void main(String[] args){
			final String topic = new String("朋友");
			ChineseWord[] array = JSONReader.GetWordList(topic);
			for (ChineseWord word : array){
				word.PrintWord();
			}
			System.out.println("total result = "+array.length);
		}
}
