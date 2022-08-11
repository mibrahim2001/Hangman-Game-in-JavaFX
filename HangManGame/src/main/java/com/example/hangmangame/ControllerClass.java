package com.example.hangmangame;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Optional;

public class ControllerClass {

    //variables to be used
    private Label[] alphabetDashes;
    private HangManModel hangManModel;

    //fxml components

    @FXML
    private ImageView blueBackgroundImage;
    @FXML
    private ImageView deadFace;
    @FXML
    private ImageView hangFrame;
    @FXML
    private ImageView happyFace;
    @FXML
    private Text hintLabel;
    @FXML
    private ImageView leftArm;
    @FXML
    private ImageView leftLeg;
    @FXML
    private ImageView rightArm;
    @FXML
    private ImageView rightLeg;
    @FXML
    private ImageView ropeToHang;
    @FXML
    private ImageView shirt;
    @FXML
    private ImageView shockFace;
    @FXML
    private ImageView shorts;
    @FXML
    private HBox textLabelHBox;
    @FXML
    private ImageView startBtnImage;
    @FXML
    private Label startLabel;
    @FXML
    private ImageView hangmanLogo;
    @FXML
    private Label enterCharacterHereLabel;
    @FXML
    private TextField characterInputTF;
    @FXML
    private Button checkBtn;


    //methods for this controller class


    //a method to start the game when the start game image is clicked
    @FXML
    void startBtnImgClicked(MouseEvent event){
        //initializing the hangman model to use for the game
        hangManModel = new HangManModel();
        //sound
        hangManModel.playSound("click");
        //adding limit to the text field
        addLimitToTextField(characterInputTF,1);
        //binding enter key to check btn
        bindEnterKeyToCheckBtn();
        //start a new round
        newRound();

    }

    //a method when check btn is clicked
    @FXML
    public void checkBtnClicked(){
       performCheckActions();

    }

    public void bindEnterKeyToCheckBtn() {
        checkBtn.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event){
                if(event.getCode().equals(KeyCode.ENTER)){
                    performCheckActions();
                }
            }
        });
    }


    //a method that perform check actions
    public void performCheckActions(){
        //first we will get the alphabet entered by the user
        if(!characterInputTF.getText().isEmpty()) {
            String alphabet = String.valueOf(characterInputTF.getText().charAt(0));
            //clear the text field
            characterInputTF.clear();
            characterInputTF.requestFocus();
            //get the indices of the alphabet from the model
            ArrayList<Integer> indices = hangManModel.checkCharacterIndices(alphabet);
            //checking the indices
            checkIndicesAndPerformActionsAccordingly(indices, alphabet);
        }
    }

    //a method to start a new round
    public void newRound(){
        //hiding the hangman
        hideHangMan();

        //resetting model settings
        hangManModel.initForNewGame();

        //clearing the previous children of hbox
        textLabelHBox.getChildren().clear();

        //getting the string array from the hang man model
        String st[] = hangManModel.getCategoryAndWordForNextRound();//index 0 contains category and 1 contains the word

        String category = st[0];
        String word = st[1];

        //initializing the view for game
        initTheDashesAndTexts(word);
        textLabelHBox.setVisible(true);
        hintLabel.setText(category);
        hintLabel.setVisible(true);
        startBtnImage.setVisible(false);
        hangmanLogo.setVisible(false);
        startLabel.setVisible(false);
        characterInputTF.setVisible(true);
        enterCharacterHereLabel.setVisible(true);
        checkBtn.setVisible(true);
    }

    //a method to initialize the dashes show for the word
    public void initTheDashesAndTexts(String word){
        textLabelHBox.setAlignment(Pos.CENTER);
        int length = word.length();
        alphabetDashes = new Label[length];
        for(int i=0; i<length; i++) {

            if (word.charAt(i) != ' ') {
                alphabetDashes[i] = new Label("_");
            } else {
                alphabetDashes[i] = new Label(" "); //if word contains empty space we will add a empty label
            }
            alphabetDashes[i].getStyleClass().add("text-style");
            alphabetDashes[i].setTextAlignment(TextAlignment.CENTER);
            textLabelHBox.getChildren().add(alphabetDashes[i]);
        }

    }


    //a method to check the indices
    public void checkIndicesAndPerformActionsAccordingly(ArrayList<Integer> indices, String alphabet){
        //we will check the size of the array list if it is zero it means
        //that the alphabet is not present in the word
        if(indices.size() == 0){
            proceedWithTheHanging();
        }
        else{
            placeTheAlphabetAtTheRightIndices(indices,alphabet);
        }
    }

    //a method to show the alphabet on screen if it was right
    public void placeTheAlphabetAtTheRightIndices(ArrayList<Integer> indices, String alphabet){
        //setting the text to alphabet
        String convertedUpperCase = alphabet.toUpperCase();
        for(Integer ind:indices){
            alphabetDashes[ind].setText(convertedUpperCase);
        }
        //play a sound
        hangManModel.playSound("correct");
        //after placing the alphabet check for win
        checkForWin();
    }

    //a method that will proceed the hanging based on the lives left for the user
    public void proceedWithTheHanging(){
        int livesLeft = hangManModel.getNumberOfLivesLeft();
        hangManModel.playSound("wrong");
        switch (livesLeft){
            case 9-> hangFrame.setVisible(true);
            case 8-> ropeToHang.setVisible(true);
            case 7-> happyFace.setVisible(true);
            case 6-> shirt.setVisible(true);
            case 5-> shorts.setVisible(true);
            case 4 -> {leftArm.setVisible(true);shockFace.setVisible(true);}
            case 3-> rightArm.setVisible(true);
            case 2-> leftLeg.setVisible(true);
            case 1-> rightLeg.setVisible(true);
            case 0-> {deadFace.setVisible(true);endOfRound(false);}
            default -> System.out.println("There was some error in commencing the game please restart!");
        }
    }

    //a method to end the round and start new round if necessary
    public void endOfRound(boolean win){
        //check if user lost or won
        String message;
        String headerText;
        String title;
        ImageView icon;

        if(win){
            message = "You won this round. Please select what to do next";
            headerText = "Congratulations!";
            title = "Won";
            icon = new ImageView("file:.\\src\\main\\resources\\com\\example\\hangmangame\\HangManImages\\body01-head.png");
            hangManModel.playSound("win");
        }else{
            String correctWord = hangManModel.getWordForThisRound();
            message = "You lost this round.\nPlease select what to do next";
            headerText = "Failed!\nCorrect Word: "+correctWord;
            title = "Lost";
            icon = new ImageView("file:.\\src\\main\\resources\\com\\example\\hangmangame\\HangManImages\\body01-head-dead.png");
            hangManModel.playSound("lost");
        }
        //show an alert to the user
        ButtonType newRound = new ButtonType("New Round", ButtonBar.ButtonData.OK_DONE);
        ButtonType endGame = new ButtonType("End Game",ButtonBar.ButtonData.CANCEL_CLOSE);
        icon.setFitWidth(48);
        icon.setFitHeight(48);
        Alert endOfRoundAlert = new Alert(Alert.AlertType.CONFIRMATION,message,newRound,endGame);
        endOfRoundAlert.setTitle(title);
        endOfRoundAlert.setHeaderText(headerText);
        endOfRoundAlert.getDialogPane().setGraphic(icon);
        Optional<ButtonType> result = endOfRoundAlert.showAndWait();
        if(result.isPresent() && result.get() == newRound){
            newRound();
        }
        else{
            endGame();
        }
    }

    //a method to end the game
    public void endGame(){
        //to end the game we will set visible of everything to false
        hideHangMan();
        characterInputTF.setVisible(false);
        enterCharacterHereLabel.setVisible(false);
        checkBtn.setVisible(false);
        hintLabel.setVisible(false);

        //empty the texts array
        textLabelHBox.setVisible(false);

        //now making the start game components visible
        hangmanLogo.setVisible(true);
        startLabel.setVisible(true);
        startBtnImage.setVisible(true);
    }

    //a method to hide hangman
    public void hideHangMan(){
        hangFrame.setVisible(false);
        ropeToHang.setVisible(false);
        happyFace.setVisible(false);
        shirt.setVisible(false);
        shorts.setVisible(false);
        shockFace.setVisible(false);
        leftArm.setVisible(false);
        rightArm.setVisible(false);
        leftLeg.setVisible(false);
        rightLeg.setVisible(false);
        deadFace.setVisible(false);
    }

    //a method to set the limit of the text field to one
    public void addLimitToTextField(TextField textField, final int maxLength){
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(textField.getText().length() > maxLength){
                    String alphabet = textField.getText().substring(0,maxLength);
                    textField.setText(alphabet);
                }
            }
        });
    }

    //a method to check if the player has won this round
    public void checkForWin(){
        //we will check if there is any dash remaining if yes we will return without doing anything
        for(Label alphabet:alphabetDashes){
            if(alphabet.getText().equals("_")){
                return;
            }
        }
        //else we will perform following actions
        endOfRound(true);
    }



}
