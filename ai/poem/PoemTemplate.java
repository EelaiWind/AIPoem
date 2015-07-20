package ai.poem;

import java.util.HashMap;

import ai.word.ChineseWord;

public class PoemTemplate implements Comparable<PoemTemplate>{
	
	private static final boolean DEBUG = false;
	private final static int scoreRhyme = 500;
	private final static int scoreTone = 100;
	private final static int scoreAntithesis = 100;
	
	private int col, row;
	private int[][] wordComposition;
	private ChineseWord[][] poem = null;
	private int fitnessScore;
	private int maxRhythmMatch, maxToneMatch, maxAntithesisMatch;
	
	public PoemTemplate(int row,int col,int[][] wordComposition, ChineseWord[][] poem){
		this.row = row;
		this.col = col;
		this.wordComposition = wordComposition;
		this.poem = poem;
		this.fitnessScore = 0;
		maxRhythmMatch = row/2;
		if (col == 5){
			maxToneMatch = 3*row;
		}
		else{
			maxToneMatch = 4*row;
		}
		maxAntithesisMatch= 0;
		for (int i = 0 ; i < row ; i+= 2){
			maxAntithesisMatch += wordComposition[i].length;
		}
	}
	
	public ChineseWord[][] getPoem() {
		return poem;
	}
	
	public int getFitnessScore() {
		return fitnessScore;
	}

	public int[][] getTeplate(){
		return wordComposition;
	}
	
	public int FitnessFunction(){
		
		if (poem.length != wordComposition.length)
			return 0;
		
		fitnessScore = GetRhythmScore()+GetToneScore()+GetAntithesisScore();
		
		return fitnessScore;
	}
	
	private int GetAntithesisScore(){
		int countAntithesis = 0;
		for ( int i = 0 ; i < row ; i += 2){
			for (int j = 0 ; j < wordComposition[i].length ; j++){
				int wordType = poem[i][j].getWordType() & poem[i+1][j].getWordType();
				if ( wordType > 0){
					countAntithesis += 1;
					if (DEBUG) System.out.println(poem[i][j].getWord()+" , "+poem[i+1][j].getWord()+" => "+ ChineseWord.ReadableWordType(wordType));
				}
			}
		}
		if (DEBUG)System.out.printf(">>對偶的詞共有  %d / %d 個\n",countAntithesis,maxAntithesisMatch);
		return countAntithesis*scoreAntithesis/maxAntithesisMatch;
	}
	
	private int GetToneScore(){
		final int[][] standardTone = new int[][]{{0,1,0},{1,0,1},{1,0,1},{0,1,0}};
		int countMatchTone = 0;
		int countMatchRhythmTone = 0;
		int index;
		if (GetToneAt(2, poem) == 0){
			index = 0;  /*平起式*/
		}
		else{
			index = 2;  /*仄起示*/
		}
		
		if (DEBUG) System.out.println("===平仄===");
		for ( int i = 0 ; i < row ; i ++){
			for ( int j = 2, k = 0 ; j <= col ; j+=2, k++){
				int charIndex = i*col+j;
				if (DEBUG) System.out.printf("%c(%d)",GetCharAt(charIndex, poem),GetToneAt(charIndex, poem));
				if (GetToneAt(charIndex, poem) == standardTone[index][k]){
					countMatchTone += 1;
					if (DEBUG) System.out.print("O ");
				}
				else{
					if (DEBUG) System.out.print("X ");
				}
			}
			if (DEBUG)  System.out.println();
			index = (index+1)%4;
		}
		/*處理韻腳的平仄*/
		if (DEBUG) System.out.println("===韻腳平仄===");
		if (poem[0][wordComposition[0].length-1].GetRythm() == poem[1][wordComposition[1].length-1].GetRythm()){
			if (GetToneAt(1*col, poem) == 0){
				countMatchRhythmTone += 1; /*首句押韻用平聲*/
				if (DEBUG) System.out.println("第1句 : 平");
			}
		}
		else{
			if (GetToneAt(1*col, poem) == 1){
				countMatchRhythmTone += 1; /*首句不押韻用仄聲*/
				if (DEBUG) System.out.println("第1句 : 仄");
			}
		}
		if (GetToneAt(2*col, poem) == 0){
			countMatchRhythmTone += 1;
			if (DEBUG) System.out.println("第2句 : 平");
		}
		for (int i = 4 ; i <= row ; i += 2){
			if ( GetToneAt((i-1)*col, poem) == 1){
				countMatchRhythmTone += 1;
				if (DEBUG) System.out.println("第"+(i-1)+"句 : 仄");
			}
			if ( GetToneAt(i*col, poem) == 0){
				countMatchRhythmTone += 1;
				if (DEBUG) System.out.println("第"+i+"句 : 平");
			}
		}
		if (DEBUG)System.out.printf(">>符合平仄的字有 (%d + %d(韻腳相關)) / %d 個\n",countMatchTone,countMatchRhythmTone,maxToneMatch);
		return (countMatchTone+countMatchRhythmTone)*scoreTone/maxToneMatch;
	}
	
	private char GetCharAt(int index, ChineseWord[][] poem){
		if ( index > row*col){
			System.err.println("error : index out of bound");
			System.exit(1);
		}
		index -= 1;
		int atRow = index/col;
		index = index-atRow*col+1;
		int cumulativeSum = 0;
		for ( ChineseWord word : poem[atRow]){
			int len = word.getLength();
			if (cumulativeSum + len >= index){
				return word.getCharAt(index-cumulativeSum-1);
			}
			cumulativeSum += len;
		}
		return '?';
	}
	
	private int GetToneAt(int index, ChineseWord[][] poem){
		if ( index > row*col){
			System.err.println("error : index out of bound");
			System.exit(1);
		}
		index -= 1;
		int atRow = index/col;
		index = index-atRow*col+1;
		int cumulativeSum = 0;
		for ( ChineseWord word : poem[atRow]){
			int len = word.getLength();
			if (cumulativeSum + len >= index){
				return word.getToneAt(index-cumulativeSum-1);
			}
			cumulativeSum += len;
		}
		return -1;
	}
	
	private int GetRhythmScore(){
		HashMap<Character,Integer> recordRhythm = new HashMap<Character,Integer>();
		int maxCountSameRhytm = 0;
		char mostRhythm='?';
		for (int  i = 1 ; i < row ; i += 2){
			char rhythm = poem[i][wordComposition[i].length-1].GetRythm();
			int temp;
			if (recordRhythm.containsKey(rhythm)){
				temp = recordRhythm.get(rhythm)+1;
			}
			else{
				temp = 1;
			}
			recordRhythm.put(rhythm,temp);
			if ( maxCountSameRhytm < temp){
				maxCountSameRhytm = temp;
				mostRhythm = rhythm;
			}
		}
		if (DEBUG) System.out.printf(">>最多的韻腳是 \"%c\"，共有 %d / %d 個\n",mostRhythm,maxCountSameRhytm,maxRhythmMatch);
		return maxCountSameRhytm*scoreRhyme/maxRhythmMatch;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ChineseWord[] line : poem){
			for (ChineseWord word : line)
				sb.append(word.getWord()+" ");
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public int compareTo(PoemTemplate other) {
		// TODO Auto-generated method stub
		if (this.fitnessScore > other.getFitnessScore())
			return -1;
		else if (this.fitnessScore < other.getFitnessScore())
			return 1;
		else {
			return 0;
		}
	}
}
