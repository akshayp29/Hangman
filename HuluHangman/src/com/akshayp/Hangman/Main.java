package com.akshayp.Hangman;

import java.io.IOException;
import java.net.MalformedURLException;

import com.akshayp.Hangman.Json.currentGame;

public class Main {

    /**
     * Main method to be called that tests the Hangman AI using JSON requests
     * 
     * @param args
     *            not used
     * @throws MalformedURLException
     *             Thrown when the JSON request could not be made
     * @throws IOException
     *             Thrown when something fails while performing the JSON request
     */
    public static void main(String[] args) throws MalformedURLException,
            IOException {

        MainPlayer robotSavior = new MainPlayer();
        currentGame hangmanGame = Json.makeJsonRequest("");
        int errors = 0;
        StringBuilder guesses = new StringBuilder();

        System.out.println("Let us try to save person " + hangmanGame.token);

        while (hangmanGame.status == currentGame.Status.ALIVE) {

            System.out.println("The current state of the word is: "
                    + hangmanGame.state);

            char guess = robotSavior.guessChar(hangmanGame.state,
                    guesses.toString());

            System.out.println("Let us guess: " + guess);

            String formattedGuess = "&token=" + hangmanGame.token + "&guess="
                    + guess;

            currentGame newState = Json.makeJsonRequest(formattedGuess);

            guesses.append(guess);

            if (hangmanGame.state.equals(newState.state)
                    || newState.status == currentGame.Status.DEAD) {
                System.out.println("We did not guess correctly");
                errors++;

            } else {
                System.out.println("We guessed correctly");

            }

            hangmanGame = newState;
        }

        if (hangmanGame.status == currentGame.Status.DEAD) {
            System.out.println("We failed to guess: " + hangmanGame.state);
        }

        else {
            System.out.println("We guessed \"" + hangmanGame.state + "\" with "
                    + errors + " error(s)!");
        }
    }
}
