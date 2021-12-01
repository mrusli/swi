package com.pyramix.swi.webui;

public class App_AlphaGroup {

	public static void main(String[] args) {
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

		int groupOf = 3;
		int numberOfGroup = 26 / groupOf;
		int remainder = 26 % groupOf;
		
		System.out.println("numberOfGroup="+numberOfGroup+" remainder="+remainder);
		
		String alphaGroupDisp = "";
		String alphaGroup = "";
		
		int i = 0;
		int groupNum = 0;
		
		while (i < alphabet.length) {
			for (int k = 0; k < groupOf; k++) {
				// display
				if ((k==0) || (k==(groupOf-1))) {
					alphaGroupDisp = alphaGroupDisp+alphabet[i];				
				} else {
					alphaGroupDisp = alphaGroupDisp+".";
				}
				// for access
				alphaGroup = alphaGroup+alphabet[i];
				i++;
			}
			System.out.println("groupNum="+groupNum+" : "+alphaGroupDisp);
			alphaGroupDisp = "";
			groupNum++;
			if (!(groupNum<numberOfGroup)) {
				break;
			}
		}
		// remainder
		int idxAlpha = numberOfGroup*groupOf;
		while (idxAlpha < alphabet.length) {
			// display
			alphaGroupDisp = alphaGroupDisp+alphabet[idxAlpha];
			
			// access
			alphaGroup = alphaGroup+alphabet[idxAlpha];
			idxAlpha++;
		}
		System.out.println("groupNum="+groupNum+" : "+alphaGroupDisp);
	}

}
