package com.example.hangmangame;

import javafx.scene.control.Alert;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;

public class HangManModel {
    private ArrayList<ArrayList<String>> allWords = new ArrayList<>();
    private String[] categories;
    private String wordForThisRound;
    private int numberOfLivesLeft;

    //constructor
    public HangManModel(){
        //these are the categories that we will use
        categories = new String[]{"Movie Character", "Fruit", "Country", "Movie"};
        //init the arraylists
        ArrayList<String> movieCharacters = new ArrayList<>();
        ArrayList<String> fruits = new ArrayList<>();
        ArrayList<String> countries = new ArrayList<>();
        ArrayList<String> movies = new ArrayList<>();

        //add to the all words
        allWords.add(movieCharacters);
        allWords.add(fruits);
        allWords.add(countries);
        allWords.add(movies);

        //adding the words from the file
        String fileName = "words";
        readCsvFileToGetWords(fileName);


        //number of lives are 10 in our case so, we initialize to 10
        numberOfLivesLeft = 10;

    }

    //a method to get word for next round
    public String getWordForThisRound(){
        return wordForThisRound;
    }

    public String[] getCategoryAndWordForNextRound(){
        String[] wordAndCategory = null;
        if(!isWordsArrayListEmpty()) {
            wordAndCategory = randomWord();
            wordForThisRound = wordAndCategory[1]; // because word is at the first index
        }else {
            Alert a = new Alert(Alert.AlertType.ERROR, "There is no word to show!");
        }
        return wordAndCategory;
    }


    //a method to select a random word from the words
    public String[] randomWord(){
        Random rand = new Random();
        //we will return an array of string
        //it will contain the category and the word
        String[] result = new String[2];
        String randomWord;
        int category;
        int word;
        boolean aValidWordFound = false;
        while(!aValidWordFound) {
            category = rand.nextInt(allWords.size());
            //we have to check if the size of this category is greater than 0
            //if size is 0 it means it do not contain any word
            if(allWords.get(category).size() != 0){
                word = rand.nextInt(allWords.get(category).size());
                randomWord = allWords.get(category).get(word);
                result[0] = categories[category];
                result[1] = randomWord;
                //we will also remove the word so that we do not get it again
                allWords.get(category).remove(word);
                aValidWordFound = true;
            }

        }
        return result;
    }

    //a method to get the number of lives left
    public int getNumberOfLivesLeft(){
        return numberOfLivesLeft;
    }

    //a method to reduce lives
    public void reduceLivesLeft(){
        numberOfLivesLeft--;
    }

    //a method to check if a character is present or not
    //if that alphabet is present return the indices of that character
    public ArrayList<Integer> checkCharacterIndices(String alphabet){
        ArrayList<Integer> indicesOfAlphabetInWord = new ArrayList<>();
        for(int i=0; i<wordForThisRound.length(); i++){
            if(alphabet.equalsIgnoreCase(String.valueOf(wordForThisRound.charAt(i)))){
                indicesOfAlphabetInWord.add(i);
            }
        }

        //we will check if there was no match for the alphabet
        //in the string we will reduce the lives left
        if(indicesOfAlphabetInWord.size() == 0)
            reduceLivesLeft();

        return indicesOfAlphabetInWord;
    }


    //a method to initialize a new round
    public void initForNewGame(){
        numberOfLivesLeft = 10;
    }

    //a method to play sounds according to actions
    public void playSound(String fileName){
        File file = new File(".\\src\\main\\resources\\com\\example\\hangmangame\\HangManSounds\\"+fileName+".wav");
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }


    }

    //a method to read the csv file to get the words
    public void readCsvFileToGetWords(String fileName){
        File file = new File(".\\src\\main\\resources\\com\\example\\hangmangame\\HangManWords\\"+fileName+".csv");

        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            String words[];
            int category;
            while((line = reader.readLine()) != null){
                words = line.split(",");
                //we will get the category of the line by passing the 0 index
                //because our category is the first word of line
                category = getIndexOfCategory(words[0]);

                //add the words to the array list
                if(category != -1) {
                    for (int i = 1; i < words.length; i++) {
                        allWords.get(category).add(words[i]);
                    }
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //a method to check which index to assign according to category
    public int getIndexOfCategory(String token){
        int index = -1;
        if(token.equals("Movie Character:")){
            index = 0;
        }
        else if(token.equals("Fruit:")){
            index =1;
        }
        else if(token.equals("Country:")){
            index = 2;
        }
        else if(token.equals("Movie:")){
            index = 3;
        }

        return index;
    }

    //a method to check if there is any word in the array list or not
    public boolean isWordsArrayListEmpty(){
        for(ArrayList<String> category:allWords){
            if(category.size() != 0){
                return false;
            }
        }

        return true;
    }









}
