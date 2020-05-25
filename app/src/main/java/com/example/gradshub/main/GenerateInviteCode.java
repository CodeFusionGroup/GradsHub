package com.example.gradshub.main;

import java.util.*;


// NOTE: this is just sample code but not implemented anywhere in the application
public class GenerateInviteCode {

	public static final String digits = "0123456789";
	public static final String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
	public static final String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String ALLOWED_CHARACTERS = digits + lowerCaseLetters + upperCaseLetters;

	public static String generateString() {

		Random generator = new Random();
		StringBuilder randomStringBuilder = new StringBuilder();
		int randomStringLength = 6;

		for(int i = 0; i < randomStringLength; i++) {
			randomStringBuilder.append(ALLOWED_CHARACTERS.charAt(generator.nextInt(ALLOWED_CHARACTERS.length())));
		}

		return randomStringBuilder.toString();
	}


	public static void main(String[] args) {
		System.out.println("generated string: " + generateString());
	}

}
