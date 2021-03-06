package ai.word;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class WordPile {
	
	private ArrayList<ArrayList<ChineseWord>> nounWord = new ArrayList<ArrayList<ChineseWord>>();
	private ArrayList<ArrayList<ChineseWord>> adjWord = new ArrayList<ArrayList<ChineseWord>>();
	private ArrayList<ArrayList<ChineseWord>> verbWord = new ArrayList<ArrayList<ChineseWord>>();
	private ChineseWord[] wordList;
	
	public WordPile(ChineseWord[] wordList) {
		this.wordList = wordList;
		for ( int i  = 0 ; i <= 3 ; i++){
			nounWord.add(new ArrayList<ChineseWord>());
			adjWord.add(new ArrayList<ChineseWord>());
			verbWord.add(new ArrayList<ChineseWord>());
		}
		ClassifyWords();
	}
	
	public WordPile(JSONObject json){
		
		for ( int i  = 0 ; i <= 3 ; i++){
			nounWord.add(new ArrayList<ChineseWord>());
			adjWord.add(new ArrayList<ChineseWord>());
			verbWord.add(new ArrayList<ChineseWord>());
		}
		
		JSONArray arr = json.optJSONArray("wordPile");
		this.wordList = new ChineseWord[arr.length()];
		if ( arr != null){
			for ( int i = 0 ; i < arr.length() ; i++){
				JSONObject item = arr.getJSONObject(i);
				
				wordList[i] = new ChineseWord(item.optString("word"),GetBopomofo(item.optJSONArray("bopomofo")), 
						GetTone(item.optJSONArray("tone")),item.optInt("wordType"),item.optInt("length"));
			}
		}
		ClassifyWords();
	}
	
	private int[] GetTone(JSONArray arr){
		int[] tone = new int[arr.length()];
		for (int i = 0 ; i < arr.length() ; i++){
			tone[i] = arr.optInt(i);
		}
		
		return tone;
	}
	private char[] GetBopomofo(JSONArray arr){
		char[] bopomofo = new char[arr.length()];
		for (int i = 0 ; i < arr.length() ; i++){
			bopomofo[i] = arr.optString(i).charAt(0);
		}
		return bopomofo;
	}
	
	public String GetJSONString(){
		JSONObject json = new JSONObject();
		JSONArray arr = new JSONArray();
		for ( ChineseWord word : wordList){
			JSONObject obj = new JSONObject(word);
			arr.put(obj);
		}
		json.put("wordPile",arr);
		return json.toString();
	}
	
	private void ClassifyWords(){
		System.out.printf("===詞庫中總共有%d個詞===\n",wordList.length);
		for (ChineseWord word : wordList){
			/*一個詞可能會有很多詞性*/
			if ( (word.getWordType() & ChineseWord.noun) > 0){
				nounWord.get(word.getLength()).add(word);
			}
			if ((word.getWordType() & ChineseWord.adj) > 0){
				adjWord.get(word.getLength()).add(word);
			}
			if ((word.getWordType() & ChineseWord.verb) > 0){
				verbWord.get(word.getLength()).add(word);
			}
		}
	}
	
	public ChineseWord GetAWord(int wordType, int wordLength) {
		ArrayList<ChineseWord> list;
		Random rand = new Random();
		if ((wordType & ChineseWord.noun) > 0){
			list =nounWord.get(wordLength);
		}
		else if ((wordType & ChineseWord.adj) > 0){
			list =adjWord.get(wordLength);
		}
		else if ((wordType & ChineseWord.verb) > 0){
			list =verbWord.get(wordLength);
		}
		else{
			System.err.println("error : invalid word type");
			System.exit(1);
			return null;
		}
		return list.get(rand.nextInt(list.size()));
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("=== 名詞清單 ===\n");
		for ( ArrayList<ChineseWord> list : nounWord ){
			for ( ChineseWord word : list){
				sb.append(word.toString());
			}
		}
		sb.append("=== 形容詞清單 ===\n");
		for ( ArrayList<ChineseWord> list : adjWord ){
			for ( ChineseWord word : list){
				sb.append(word.toString());
			}
		}
		sb.append("=== 動詞清單 ===\n");
		for ( ArrayList<ChineseWord> list : verbWord ){
			for ( ChineseWord word : list){
				sb.append(word.toString());
			}
		}
		return sb.toString();
	}
}
