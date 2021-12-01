package com.pyramix.swi.webui.common;

import java.security.SecureRandom;

public class PasswordGenerator {

	private SecureRandom random = new SecureRandom();
	
	public String generatePassword(int len, String dic) {
	    String result = "";
	    for (int i = 0; i < len; i++) {
	        int index = random.nextInt(dic.length());
	        result += dic.charAt(index);
	    }
	    return result;
	}

}
