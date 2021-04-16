package com.holidayextras.techtest.ajb.testdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.holidayextras.techtest.ajb.domain.ProtoUser;

/**
 * Class to generate random users for the purpose of test data generation.
 */
@Component
public class RandomUserGenerator {
	private static final String[] BRIDGE = {"a", "e", "i", "o", "u", "ai", "au", "ea", "ee", "oa", "oo"};
	private static final String[] HARD = {
			"b", "c", "d", "f", "g", "k", "l", "m", "n", "p", "r", "s", "t", "v", "w", "z",
			"bb", "dd", "ll", "mm", "nn", "pp", "rr", "ss", "tt",
			"ch", "nd", "nt", "rd", "sh"
	};
	private static final String[] HARD_START = {
			"b", "c", "d", "f", "g", "k", "l", "m", "n", "p", "r", "s", "t", "v", "w", "z",
			"cl", "sh", "sl"
	};
	private static final String[] EMAIL_DOMAINS = {
			"aol.com", "att.net", "gmail.com", "gmx.de", "googlemail.com", "holidayextras.com", "hotmail.com", "live.com",
			"mail.ru", "me.com", "msn.com", "outlook.com", "rocketmail.com", "yahoo.com", "yandex.ru"
	};
	
	private Random random = new Random();
	
	private String generateRandomWord() {
		var wordSymbolLength = random.nextInt(6) + 4;
		boolean nextBridge = random.nextBoolean();
		var randomWord = new StringBuilder();
		for (int i = 1; i <= wordSymbolLength; i++) {
			if (nextBridge) {
				randomWord.append(BRIDGE[random.nextInt(BRIDGE.length)]);
			} else if (i == 1) {
				randomWord.append(HARD_START[random.nextInt(HARD_START.length)]);
			} else {
				randomWord.append(HARD[random.nextInt(HARD.length)]);
			}
			nextBridge = !nextBridge;
		}
		return randomWord.toString();
	}
	
	private boolean isUpperCaseVowel(String s) {
		return Arrays.asList("A", "E", "I", "O", "U").contains(s);
	}
	
	private ProtoUser generateRandomUser() {
		var email = generateRandomWord() + "@" + EMAIL_DOMAINS[random.nextInt(EMAIL_DOMAINS.length)];
		var givenName = generateRandomWord();
		givenName = givenName.substring(0,1).toUpperCase() + givenName.substring(1);
		var familyName = generateRandomWord();
		familyName = familyName.substring(0,1).toUpperCase() + familyName.substring(1);
		if ((random.nextInt(5) == 0) && !isUpperCaseVowel(familyName.substring(0,1))) {
			familyName = "O'" + familyName;
		}
		return new ProtoUser(email, givenName, familyName);
	}
	
	public List<ProtoUser> generateRandomUsers(int numUsers) {
		List<ProtoUser> userList = new ArrayList<>();
		for (int i = 1; i <= numUsers; i++) {
			var randomUser = generateRandomUser();
			userList.add(randomUser);
		}
		return userList;
	}
}
