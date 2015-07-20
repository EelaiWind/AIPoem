package ai.poem;

import ai.word.ChineseWord;

public abstract class PoemTemplate {
	
	final static int scoreRhyme = 100;
	final static int scoreTone = 10;
	final static int scoreAntithesis = 3;
	
	public abstract int[][] GetTeplate();
	
	public abstract int GetScore(ChineseWord[][] poem);
}
