package ai.word;

public class ChineseWord {
	public static final int noun = 1, adj = 2, verb = 4;
	private int length;
	private String word;
	char[] bopomofo;
	int[] tone;
	int wordType;
	
	/**
	 * 	建立一個詞，儲存詞性和每個字的平仄、韻腳
	 * 	@param word: 中文字詞
	 * 	@param letterBopomofo: 每個字的注音
	 * 	@param wordType: 代表詞性的數字
	 * 
	 * 	名詞: 1, 形容詞: 2, 動詞: 4
	 * 	因為詞性是從concept net 的 relation 推測的，所以一個詞可能會有多種詞性
	 * 	用二進位表示詞性需要3bit(van) 某個bit是1就表示具有該詞性 
	 * 	例如: 詞性 = 5,van = 101, 表示是動詞和名詞
	 */
	public ChineseWord(String word, String[] letterBopomofo, int wordType){
		String str;
		
		this.length = word.length();
		this.bopomofo = new char[this.length];
		this.tone = new int[this.length];
		this.word = word;
		this.wordType = wordType;
		
		for (int i = 0 ; i < this.length ; i++){
			str = letterBopomofo[i];
			
			if (str.charAt(str.length()-1) =='˙'){
				this.tone[i] = 0;
				this.bopomofo[i] = str.charAt(str.length()-2);
			}
			else if (str.charAt(str.length()-1) == 'ˊ'){
				this.tone[i] = 2;
				this.bopomofo[i] = str.charAt(str.length()-2);
			}
			else if (str.charAt(str.length()-1) == 'ˇ'){
				this.tone[i] = 3;
				this.bopomofo[i] = str.charAt(str.length()-2);
			}
			else if (str.charAt(str.length()-1) == 'ˋ'){
				this.tone[i] = 4;
				this.bopomofo[i] = str.charAt(str.length()-2);
			}
			else{
				this.tone[i] = 1;
				this.bopomofo[i] = str.charAt(str.length()-1);
			}
		}
	}
	
	private String ReadableWordType(){
		String type = new String();
		if ((this.wordType & noun )> 0)
			type += "名";
		if ((this.wordType & adj )> 0)
			type += "形";
		if ((this.wordType & verb )> 0)
			type += "動";
		return type;
	}
	
	public void PrintWord(){
		System.out.println(word+" <"+ReadableWordType()+">");
		for (int i = 0; i < this.length; i++) {
			System.out.println(word.charAt(i)+", "+bopomofo[i]+", "+String.valueOf(tone[i]));
		}
	}
}
