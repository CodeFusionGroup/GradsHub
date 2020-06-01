package com.example.gradshub.main.creategroup;

import java.util.*;


public class GenerateInviteCode {

	private static final String digits = "0123456789";
	private static final String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
	private static final String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String ALLOWED_CHARACTERS = digits + lowerCaseLetters + upperCaseLetters;

	public static String generateString() {

		Random generator = new Random();
		StringBuilder randomStringBuilder = new StringBuilder();
		int randomStringLength = 6;

		for(int i = 0; i < randomStringLength; i++) {
			randomStringBuilder.append(ALLOWED_CHARACTERS.charAt(generator.nextInt(ALLOWED_CHARACTERS.length())));
		}

		return randomStringBuilder.toString();
	}

}
