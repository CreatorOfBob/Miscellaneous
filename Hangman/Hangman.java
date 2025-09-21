import java.util.*;
import java.io.*;

public class Hangman{
	static String target;
	static Set<Character> allLetters, correct, wrong;

	public static void main(String[] args) throws Exception {
		// Load words
		List<String> words = new ArrayList<>();
		try (Scanner fileScanner = new Scanner(new File("words.txt"))) {
			while (fileScanner.hasNextLine()) {
				words.add(fileScanner.nextLine());
			}
		}

		// Choose random word
		Random gen = new Random();
		target = words.get(gen.nextInt(words.size()));

		// Initialize sets
		allLetters = new HashSet<>();
		correct = new HashSet<>();
		wrong = new HashSet<>();

		// Extract distinct letters
		for (char ch : target.toLowerCase().toCharArray()) {
			if (Character.isLetter(ch)) {
				allLetters.add(ch);
			}
		}

		// Play the game
		Scanner in = new Scanner(System.in);
		int numMistakes = 0;

		while (true) {
			guy(numMistakes);
			printPhrase();

			System.out.print("Your guess: ");
			String input = in.nextLine().trim().toLowerCase();
			if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
				System.out.println("Invalid guess");
				continue;
			}

			char guess = input.charAt(0);
			if (correct.contains(guess) || wrong.contains(guess)) {
				System.out.println("Already guessed");
				continue;
			}

			if (allLetters.contains(guess)) {
				correct.add(guess);
				if (correct.size() == allLetters.size()) break;
			} else {
				wrong.add(guess);
				numMistakes++;
				if (numMistakes == 6) break;
			}
		}

		printPhrase();
		System.out.println(numMistakes < 6 ? "Congratulations! You win!" : "You LOSE\n" + target);
	}

	static void printPhrase() {
		System.out.println("\nWord/Phrase: ");
		for (int i = 0; i < target.length(); i++) {
			char ch = target.charAt(i);
			char lowerCh = Character.toLowerCase(ch);
			System.out.print(Character.isLetter(ch) && !correct.contains(lowerCh) ? '*' : ch);
		}
		System.out.println("\nCorrect: " + correct);
		System.out.println("Wrong: " + wrong);
	}

	static void guy(int stage) {
		String[] states = {
			"""
 ___
|   |
    |
    |
    |
____|""",
			"""
 ___
|   |
O   |
    |
    |
____|""",
			"""
 ___
|   |
O   |
|   |
    |
____|""",
			"""
 ___
|   |
O   |
/|  |
    |
____|""",
			"""
 ___
|   |
O   |
/|\\ |
    |
____|""",
			"""
 ___
|   |
O   |
/|\\ |
/    |
____|""",
			"""
 ___
|   |
O   |
/|\\ |
/ \\ |
____|"""
		};
		System.out.println(states[stage]);
	}
}
