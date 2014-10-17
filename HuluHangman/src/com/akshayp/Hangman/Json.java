package com.akshayp.Hangman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;

public class Json {

    private static final String hangmanGameURL = "http://gallows.hulu.com/play?code=akshayp@mit.edu";

    /**
     * Method to return a currentGame object that contains all the information
     * about the current game.
     * 
     * @param addToUrl
     *            String that is appended to the end of the JSON request (this
     *            is used when we are guessing a letter; otherwise, to start a
     *            game, an empty string is inputted)
     * @return a currentGame object that contains all the information about the
     *         current Hangman game that is being played
     * @throws MalformedURLException
     * @throws IOException
     *             Thrown when something fails when performing the JSON request
     */
    public static currentGame makeJsonRequest(String addToUrl)
            throws MalformedURLException, IOException {

        URL urlToCall = new URL(hangmanGameURL + addToUrl);
        InputStreamReader reader = new InputStreamReader(urlToCall.openStream());
        BufferedReader bufferReader = new BufferedReader(reader);

        StringBuilder gameInfo = new StringBuilder();
        String line = bufferReader.readLine();

        while (line != null) {
            gameInfo.append(line);
            line = bufferReader.readLine();
        }

        bufferReader.close();

        currentGame hangmanGame = new Gson().fromJson(gameInfo.toString(),
                currentGame.class);

        hangmanGame.state = hangmanGame.state.toLowerCase();

        return hangmanGame;
    }

    /**
     * Structure representing the values received from the JSON request
     */
    public static class currentGame {

        public enum Status {
            ALIVE, DEAD, FREE
        }

        public Status status;
        public String token;
        public int remainingGuesses;
        public String state;
    }
}
