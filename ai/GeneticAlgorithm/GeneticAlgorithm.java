package ai.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Random;

import ai.poem.PoemTemplate;
import ai.word.ChineseWord;
import ai.word.WordPile;

public class GeneticAlgorithm {
	private static final boolean DEBUG = true ;
	 private Random rand = new Random();
	 
    /*族群大小*/
    private static final int populationSize = 1;
    /*交配機率*/
    private static final double crossoverRate = 0.5;
    /*突變機率*/
    private static final double mutationRate = 0.1;
    
    /*詞庫*/
    private WordPile wordPile;
    
    /*詩模板*/
    private PoemTemplate template;
    private int[][] wordComposition;
    /*一個世代*/
    private ChineseWord[][][] population;
	private int[] fitScore;
    /*用於隨機產生不重複的數字*/
	private ArrayList<Integer> indexList = new ArrayList<Integer>();
	
	/*終止條件*/
	private final int maxGeneration = 1;
	private final int targetScore = 1000000;

    public GeneticAlgorithm(WordPile wordPile, PoemTemplate poemTemplate) {
		this.wordPile = wordPile;
		this.template = poemTemplate;
		this.wordComposition = this.template.GetTeplate();
		this.fitScore = new int[populationSize];
	}
    
    
    public void Evole() {
		int[] maxScore = new int[maxGeneration];
		int[] minScore = new int[maxGeneration];
		int[] avgScore = new int[maxGeneration];
    	int sumScore;
    	int counPoint = 0;
    	
    	InitPopulation();
    	
    	for ( int i = 0 ; i < maxGeneration ; i++, counPoint ++){
    		Crossover();
			Mutation();
			Select();
			maxScore[i] = 0; minScore[i] = 1000000; sumScore = 0;
			for ( int j = 0 ; j < populationSize ; j++){
				if (maxScore[i] < fitScore[j])
					maxScore[i] = fitScore[j];
				if (minScore[i] > fitScore[j])
					minScore[i] = fitScore[j];
				sumScore += fitScore[j];
			}
			avgScore[i] = sumScore/populationSize;
			if (DEBUG) PrintPoem();
			if (maxScore[i] >= targetScore)
				break;
    	}
    	new StatisticWindow(new GenerationData(counPoint, maxScore, minScore, avgScore));
	}
    /*初始化族群*/
    public void InitPopulation(){
    	population = new ChineseWord[populationSize][wordComposition.length][7];
        for (int i = 0 ; i < populationSize ; i++){
        	ChineseWord[][] poem = RandomPoem();
            population[i] = poem;
        }
        if (DEBUG) PrintPoem();
    }
    
    private ChineseWord[][] RandomPoem(){
    	ChineseWord[][] poem = new ChineseWord[wordComposition.length][7];
    	for (int i = 0 ; i < wordComposition.length ; i++){
    		for ( int j = 0 ; j < wordComposition[i].length ; j++){
    			poem[i][j] = wordPile.GetAWord(ChineseWord.noun|ChineseWord.adj|ChineseWord.verb, wordComposition[i][j]); 
    		}
    	}
    	return poem;
    }

    /* 隨機取2首詩並交換其中的2個詞，已經交配過的詩不會重複交配*/
    private void Crossover(){

		final int count_crossover = 2;
        int id1,id2;
        
		if (DEBUG) System.out.println("===Crossover===");
        for (int i = 0; i < populationSize/2 ; i++){
            id1 = RandIndex(populationSize);
            id2 = RandIndex(0);
			
            if ( CanHappen(crossoverRate)){
				for (int j = 0 ; j < count_crossover ; j++){
					int row = rand.nextInt(wordComposition.length);
					int col = rand.nextInt(wordComposition[row].length);
					ExchangeTwoWord(id1,id2,row,col);
					if (DEBUG) System.out.printf("交換第%d句地%d個詞\n",row,col);
				}
				
				if (DEBUG){
					System.out.println("第 "+(id1+1)+" 首詩 = \n"+ GetPoem(id1));
					System.out.println("第 "+(id2+1)+" 首詩 = \n"+ GetPoem(id2));
				}
            }
			if (DEBUG) System.out.println();
        }
    }
	
    private void Mutation(){
		int row,col;
		if (DEBUG) System.out.println("===Mutation===");
		for(int i = 0 ; i < populationSize ; i ++){
			if (CanHappen(mutationRate)){
				row = rand.nextInt(wordComposition.length);
				col = rand.nextInt(wordComposition[row].length);
				if (DEBUG) System.out.printf("Mutation(%d) at (%d,%d)\n",i,row,col);
				population[i][row][col] = wordPile.GetAWord(ChineseWord.all,wordComposition[row][col]);
				if (DEBUG) System.out.println("第 "+(i+1)+" 首詩 =\n "+ GetPoem(i));
			}
		}
		if (DEBUG) System.out.println();
	}
	
	/*	依照轉盤法決定下一個世代
	 * 	前10%保持不變
	 * 	後10%Random產生新的
	 *  中間80%用轉盤法
	 * */
    private void Select(){
		
		int head = 0;
		int tail = 0;
		int middle = populationSize - head - tail;
		int[] cumulativeSum = new int[middle];
		
		int totalSum = 0;
		ChineseWord[][][] populationCopy = new ChineseWord[middle][wordComposition.length][7];
		
		for (int i = 0 ; i < populationSize ; i++){
			fitScore[i] = template.GetScore(population[i]);
		}
		
		for ( int i = 0 ; i < middle ; i++){
			CopyPoem(population[i+head],populationCopy[i]);
			if (i ==0)
				cumulativeSum[i] = fitScore[i+head];
			else
				cumulativeSum[i] = cumulativeSum[i-1] + fitScore[i+head];
		}
		totalSum = cumulativeSum[middle-1];
		
		if (DEBUG)  System.out.println("===Select===");
		
		for (int i = 0 ; i < middle ; i++){
			int nextIndex = rand.nextInt(totalSum)+1;
			for ( int j = 0 ;j< middle ; j++){
				if ( cumulativeSum[j] >= nextIndex){
					if (DEBUG)  System.out.println("第  "+i+" 次；選到第 "+(j+head)+" 首");
					CopyPoem(populationCopy[j], population[i+head]);
					break;
				}
			}
		}
		int offset = populationSize - tail;
		for ( int i = 0 ; i < tail ; i++){
			population[offset+i] = RandomPoem();
		}
	}
	
    private void CopyPoem(ChineseWord[][] src, ChineseWord[][] des){
		for (int i = 0 ; i < wordComposition.length ; i++){
			for ( int j = 0 ; j < wordComposition[i].length ; j++){
				des[i][j] = src[i][j];
			}
		}
	}
	
	/*	回傳 0 - bound 之間的隨機整數
	*	如果 bound <= 0，則會傳上次0 - bound中還沒被選到的隨機整數
	*/
	private int RandIndex(int bound){
		int temp,index;
		
		if ( bound > 0){
			indexList.clear();
			for ( int i = 0 ; i < bound ; i++)
				indexList.add(i);
		}
		if ( indexList.isEmpty())
			return 0;
		index = rand.nextInt(indexList.size());
		temp = indexList.get(index);
		indexList.remove(index);
		return temp;
	}	

    /*交換 poem1 和 poem2 中第 index 個詞*/
    private void ExchangeTwoWord(int index1,int index2, int row, int col){
        ChineseWord tmp = population[index1][row][col];
        population[index1][row][col] = population[index2][row][col];
        population[index2][row][col] = tmp;
        
    }

    /*決定某機率(在0~1之間)是否發生*/
    private boolean CanHappen(double probability){
        if (probability >= 1)
            return true;
        else{
            if ((int)(rand.nextDouble()*100) < (int)(probability*100) )
                return true;
            else{
				return false;
			}
        }
    }

    private void PrintPoem(){
        for (int i = 0 ; i < populationSize ; i++){
            System.out.println("第 "+(i+1)+" 首詩 \n"+GetPoem(i));
        }
    }
	
    private String GetPoem(int index){
		StringBuilder strBuilder = new StringBuilder();
		
		if (index >= populationSize)
			return null;
		else{
			for (int i = 0 ; i < wordComposition.length ; i++){
				for ( int j = 0 ; j < wordComposition[i].length ; j++){
					strBuilder.append(population[index][i][j].getWord());
				}
				strBuilder.append("\n");
			}
			
			return strBuilder.toString();
		}
	}
}
