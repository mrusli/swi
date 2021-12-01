package com.pyramix.swi.webui;

public class App_Char_Array {

	public static void main(String[] args) {
		System.out.println("Hello World !!!");

		char[] alphabet = new char[26];
		
		alphabet[0] = 'A';
		alphabet[1] = 'B';
		alphabet[2] = 'C';
		alphabet[3] = 'D';
		alphabet[4] = 'E';
		alphabet[5] = 'F';
		alphabet[6] = 'G';
		alphabet[7] = 'H';
		alphabet[8] = 'I';
		alphabet[9] = 'J';
		alphabet[10] = 'K';
		alphabet[11] = 'L';
		alphabet[12] = 'M';
		alphabet[13] = 'N';
		alphabet[14] = 'O';
		alphabet[15] = 'P';
		alphabet[16] = 'Q';
		alphabet[17] = 'R';
		alphabet[18] = 'S';
		alphabet[19] = 'T';
		alphabet[20] = 'U';
		alphabet[21] = 'V';
		alphabet[22] = 'W';
		alphabet[23] = 'X';
		alphabet[24] = 'Y';
		alphabet[25] = 'Z';		
		
		int alphagroup = 4;
		int group = 26 / alphagroup;
		int remainder = 26 % alphagroup;
		
		// System.out.println("group="+group+" remain="+remainder);
		
		char[][] alphabetGroup = new char[group+1][alphagroup];

		for (int k = 0; k < group; k++) {
							
			for (int i = 0; i < alphagroup; i++) {				
				int alphaidx = (alphagroup*k)+i;
				// System.out.println("k="+k+" i="+i+" idx="+alphaidx+"->"+alphabet[alphaidx]);
				
				alphabetGroup[k][i] = i==0 || i==alphagroup-1 ? 
						alphabet[alphaidx] : '.';
			}
			System.out.println(alphabetGroup[k]);
		}
		// remainder
		for (int m = 0; m < remainder; m++) {
			int alphaidx = (alphagroup*group)+m;
			// System.out.println("k="+group+" i="+m+" idx="+alphaidx+"->"+alphabet[alphaidx]);
			
			alphabetGroup[group][m] = alphabet[alphaidx];			
		}
		System.out.println(alphabetGroup[group]);
	}
}
