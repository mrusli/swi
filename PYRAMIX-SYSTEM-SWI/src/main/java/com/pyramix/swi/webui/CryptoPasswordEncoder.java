package com.pyramix.swi.webui;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CryptoPasswordEncoder {

	public static void main(String[] args) {
		System.out.println("hello world!!! - password encoder");
		
		String password = "789";
		
		PasswordEncoder passwordEncoder = new PasswordEncoder() {
			
			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				
				return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
			}
			
			@Override
			public String encode(CharSequence rawPassword) {

				return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt());
			}
		};
		
		System.out.println("Password: '"+password+"' is encoded to: "+passwordEncoder.encode(password.toString()));
		
	}

}
