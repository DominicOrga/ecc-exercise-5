package com.ecc.util;

import java.util.Scanner;
import java.util.Optional;
import java.net.URL;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

public class Utility {
	
	public static String EMPTY_STRING = "";

	public static Optional<String> getResourcePath(String resource) {
		Optional<URL> resourceURL = 
			Optional.ofNullable(Utility.class.getClassLoader().getResource(resource));

		Optional<String> resourcePath = Optional.empty();

		if (resourceURL.isPresent()) {
			try {
				resourcePath = Optional.of(URLDecoder.decode(resourceURL.get().getPath(), "UTF-8"));
			}
			catch (UnsupportedEncodingException e) {
				return resourcePath;
			}
		}

		return resourcePath;
	}

	public static int countOccurrence(String str, String substr) {

		int count = 0;
		int s = str.length() - substr.length();

		// substr length is longer than the string to be searched. Hence, return 0.
		if (s < 0) {
			return count;
		}

		for (int k = 0; k <= s; k++) {
			if (str.substring(k, k + substr.length()).equals(substr)) {
				count++;
			}
		}

		return count;
	}

	public static String getStringInput(String message, String... invalidSubstrings) {
		Scanner scanner = new Scanner(System.in);
		boolean isInputValidated = false;

		String input;

		loop:
		do {
			System.out.print(message + " ");
			input = scanner.nextLine();

			if (invalidSubstrings != null) {
				for (int i = 0, s = invalidSubstrings.length; i < s; i++) {
					if (input.contains(invalidSubstrings[i])) {
						System.out.printf(
							"Input contains invalid substring (%s)\n", invalidSubstrings[i]);
						continue loop;
					};
				}
			}

			isInputValidated = true;

		} while(!isInputValidated);

		return input;
	}

	public static int getIntegerInput(String message, int min, int max) {
		Scanner scanner = new Scanner(System.in);
		String onlyIntegerRegex = "-?\\d+";
		
		boolean isInputValidated = false;
		int intInput = 0;

		do {
			System.out.print(message + " ");
			String input = scanner.nextLine();

			if (input.matches(onlyIntegerRegex) && 
				(intInput = Integer.parseInt(input)) >= min && intInput <= max) {
				isInputValidated = true;
			}
			else {
				System.out.printf("Input should be an integer between %d (inclusive) and %d " + 
					"(inclusive)\n", min, max);				
			}

		} while (!isInputValidated);

		return intInput;
	}

	public static boolean getBooleanInput(String message, char trueSymbol, char falseSymbol) {
		Scanner scanner = new Scanner(System.in);
		boolean isInputValidated = false;

		String input;

		do {
			System.out.print(message + " ");
			input = scanner.nextLine();

			if (input.equals(trueSymbol + EMPTY_STRING) || input.equals(falseSymbol + EMPTY_STRING)) {
				isInputValidated = true;
			}
			else {
				System.out.printf("Input should either be %s or %s\n", trueSymbol, falseSymbol);
			}

		} while(!isInputValidated);

		return input.equals(trueSymbol + EMPTY_STRING);
	}
}