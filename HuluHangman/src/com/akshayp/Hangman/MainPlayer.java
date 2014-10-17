package com.akshayp.Hangman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainPlayer {

    private Map<Integer, List<String>> wordsByLength;
    private Map<String, Integer> frequencyOfWords;

    /**
     * Constructor for the class that initializes the maps used (mapping a word
     * length to the words that satisfy this word length and mapping a word to
     * the number of times it appears based on data analyzing text (frequency)).
     * 
     * @throws NumberFormatException
     *             Thrown when the file being parsed is not in the correct
     *             format
     * @throws IOException
     *             Thrown when there is an error reading the file
     */
    MainPlayer() throws NumberFormatException, IOException {

        frequencyOfWords = new HashMap<String, Integer>();
        for (File file : new File("lib/frequency/").listFiles()) {
            if (file.getName().compareTo(".DS_Store") != 0) {

                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                String[] lineArray;

                while (line != null) {
                    lineArray = line.split("\\s+");
                    if (lineArray.length > 2) {

                        if (frequencyOfWords.get(lineArray[1].toLowerCase()) == null) {
                            frequencyOfWords.put(lineArray[1].toLowerCase(),
                                    Integer.parseInt(lineArray[0]));
                        } else {
                            frequencyOfWords.put(
                                    lineArray[1].toLowerCase(),
                                    Integer.parseInt(lineArray[0])
                                            + frequencyOfWords.get(lineArray[1]
                                                    .toLowerCase()));
                        }
                    }

                    line = reader.readLine();
                }

                reader.close();
            }
        }

        wordsByLength = new HashMap<Integer, List<String>>();
        for (File file : new File("lib/words/").listFiles()) {
            if (file.getName().compareTo(".DS_Store") != 0) {

                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();

                while (line != null) {
                    line = line.toLowerCase().trim();
                    if (wordsByLength.get(line.length()) == null) {
                        wordsByLength.put(line.length(), new ArrayList<String>(
                                Arrays.asList(line)));
                    } else {
                        wordsByLength.get(line.length()).add(line);
                    }
                    line = reader.readLine();
                }

                reader.close();
            }
        }
    }

    /**
     * Method that guesses a character, given the current state. It does so in
     * the following manner. First, for all words we are guessing, it finds all
     * the words in a limited dictionary that are the same length as the word we
     * are trying to guess, contain the already guessed letters in the right
     * position, and do not contain letters that were already guessed. Then,
     * using a limited frequency database, it assigns to all words that satisfy
     * the above conditions a frequency that represents how often it is used.
     * Based on these probabilities, the letter with the highest frequency
     * (calculated as the number of times it occurs in a word times that word's
     * frequency) over all words that satisfy the above conditions is chosen. If
     * there is a tie, we return the letter from this tie that shows up first in
     * a String that represents the probabilities of letters (from highest to
     * lowest) appearing in an isolated word. If no words in our limited
     * dictionary satisfy the above conditions, then we choose the letter that
     * shows up first in the String mentioned above and that has not been
     * guessed.
     * 
     * @param state
     *            a String representing the state of the current Hangman game
     * @param guesses
     *            a String representing the all the guesses made in the current
     *            Hangman game
     * @return char representing the character that was guessed
     */
    public char guessChar(String state, String guesses) {

        List<String> wordsList = new ArrayList<String>(Arrays.asList(state
                .split("[^a-z_']+")));

        double[] frequencyOfAllLetters = new double[26];

        for (String word : wordsList) {
            if (word.contains("_")) {

                String charsToUse;

                if (guesses.length() > 0) {
                    charsToUse = "[a-z&&[^" + guesses + "]]";
                } else {
                    charsToUse = "[a-z]";
                }
                Pattern regex = Pattern.compile(word.replace("_", charsToUse));

                List<String> wordsThatWork = new ArrayList<String>();

                if (wordsByLength.containsKey(word.length())) {

                    for (String wordToTry : wordsByLength.get(word.length())) {

                        Matcher matcher = regex.matcher(wordToTry);
                        if (matcher.find())
                            wordsThatWork.add(wordToTry);
                    }
                }

                double[] frequencyOfLettersFromWord = new double[26];
                int frequencyOfWord, index;

                for (String currentWord : wordsThatWork) {

                    if (frequencyOfWords.containsKey(currentWord)) {
                        frequencyOfWord = frequencyOfWords.get(currentWord);
                    } else {
                        frequencyOfWord = 1;
                    }

                    for (int i = 0; i < currentWord.length(); i++) {

                        index = currentWord.charAt(i) - 'a';

                        if (index >= 0 && index < frequencyOfAllLetters.length)
                            frequencyOfLettersFromWord[index] += frequencyOfWord;
                    }
                }
                for (int i = 0; i < frequencyOfLettersFromWord.length; i++) {

                    frequencyOfLettersFromWord[i] /= wordsThatWork.size();

                    frequencyOfAllLetters[i] += frequencyOfLettersFromWord[i];
                }
            }
        }

        String orderOfCharToGuess = "esiarntolcdupmghbyfvkwzxqj";
        char maxChar = 'a';
        double maxFrequency = 0.0;

        for (int i = 0; i < frequencyOfAllLetters.length; ++i) {
            if (frequencyOfAllLetters[i] > maxFrequency) {
                char newChar = (char) ((int) 'a' + i);
                if (guesses.indexOf(newChar) == -1) {
                    maxFrequency = frequencyOfAllLetters[i];
                    maxChar = newChar;
                }
            } else if (frequencyOfAllLetters[i] == maxFrequency) {
                char newChar = (char) ((int) 'a' + i);
                if (guesses.indexOf(newChar) == -1
                        && orderOfCharToGuess.indexOf(newChar) < orderOfCharToGuess
                                .indexOf(maxChar)) {
                    maxFrequency = frequencyOfAllLetters[i];
                    maxChar = newChar;
                }
            }
        }

        if (guesses.indexOf(maxChar) != -1) {
            maxChar = orderOfCharToGuess.charAt(0);
            int i = 1;
            while (guesses.indexOf(maxChar) != -1) {
                maxChar = orderOfCharToGuess.charAt(i);
                i++;
            }
        }

        return maxChar;
    }
}
