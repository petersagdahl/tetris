package game;

//import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

//import game.Game;
import helpers.Filbehandling;
import helpers.FileNames;

//this controller class binds the fxml file and the logic together. Making it possible for the UI to display the game
public class TetrisController {
	
	//the classes from the logic, needed in this class
	private Game tetris;
	private Filbehandling fil = new Filbehandling();
	
	//these timers makes sure the game runs on a timed loop.
	private Timer timer;
	private Timer timer2;
	
	//the tasks performed in the different timers
	private TimerTask task; 
	private TimerTask task2;
	

	    
	//the method is run in the initial initialization
	private void start()
	{
		//Initiates the timers and the timertasks
		setTime();
		
		//all the necessary methods for the games logic to be initiated
		tetris.genPiece();
		tetris.updateBoard();
		tetris.updateStage();
		
		//diplays the score as a Label in the FXML
		myLabel.setText(tetris.getScore());
		
		//prepares the highscore list to be displayed on demand.
		initiateDisplay();

		//timetasks set. The refresh rate on the loop controlling "falling of tiles", has linger intervals than the one refreshing the display.
		timer2.scheduleAtFixedRate(task2, 1000, 1000);
		timer.scheduleAtFixedRate(task, 0, 100);
	}
	
	
	//The Panes and Labels displayed on the User Interface
	@FXML
	Pane space, bak, input, display, warning;

	@FXML
	Label myLabel;

	//the method that initializes the game
	@FXML
	private void initialize()
	{
		initalState();
	}
	//takes care of starting up the game. It is put separately, so that it can be reset.
	private void initalState()
	{
		//the game logic is initialized
		tetris = new Game(15);
		
		//the board 2D array is converted into tiles of pixels
		createBoard();
		
		//the already described start() method takes care of the rest of the start up
		start();
	}
	//the method called when a file is loaded. It reset the necessary, and restarts as the initalState()
	private void initalLoad()
	{
		//the game logic is initialized from a given file
		tetris = new Game(FileNames.SAVE_FILE.toString());
		
		//the board 2D array is converted into tiles of pixels
		createBoard();
		
		//the already described start() method takes care of the rest of the start up
		start();
	}
	//Sets the time loops, and all that happens inside each timerTask
	private void setTime()
	{
		//the timers are initialized
		timer = new Timer();
		timer2 = new Timer();
		
		//the tasks are initialized
		task = new TimerTask() {
			
			//code run within this loop
			public void run() {
				
				//refreshes and redraws the board
				drawBoard();
				
				//if the game is paused, it will not update the board. (cannot move/rotate)
				if (!tetris.isPaused()) 
					tetris.updateBoard();
			}
			
		};
		task2 = new TimerTask() {
			
			//code run within this loop
			public void run() {
				
				//if the game is paused, the stage will not be updated (the pieces will not continue to fall)
				if (!tetris.isPaused()) {
					tetris.updateStage();
					
				//run outside the timers in the application thread. Immediately executed
				Platform.runLater(new Runnable() {
					
					@Override
	                public void run() {
						
						//sets the FXML Label to show the score
	                	myLabel.setText(tetris.getScore());
	                	
	                	//of the game is lost, it should display loseText "You loose", and make it possible to enter your highscoreName
	                	if (tetris.isLost())
	                	{
	                		//displays the loose text
	                		loseText();
	                		
	                		//method that handles input names
	                		saveHighscoreController();
	                	}	             
	                }
	            });
				}
			}
			
		};
	}
	//Makes it possible to send in name with the highscore
	private void saveHighscoreController() 
	{
		//creating a textfield
		TextField user = new TextField();
		
		//makes textfield visible
		input.setVisible(true);
		
		//the textfield is added to the input Pane
		input.getChildren().add(user); 
		
		//the fileHandler is initiated
		fil = new Filbehandling();
		
		//eventhandler that deals with pressing enter
		EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
			
			//what happens when you press enter
            public void handle(ActionEvent e) 
            { 
            	//user is set in the game
            	try {
                tetris.setUser(user.getText()); 
                
                //highscore and user is sent to file
                
                fil.highscore(tetris.getScoreInt(), tetris.getUser());
              
                 
             	 //the textField is now removed
             	input.getChildren().remove(user);
             	saveText(false, "");
             	
	                //the highscorelist is displayed
	                initiateDisplay();
	                display.setVisible(true);
            	} catch (Exception p) {
            		saveText(true, "Try a new name" + "\n" + p.getMessage());
            		System.out.println(p);
            	}
                
               
            } 
        }; 
	
        //when enter is pressed
        user.setOnAction(event);
	}

	
//Displaying everything
	//a graphical board is created out of the 2D array. Each entry becomes one tile, with a fixed size
	private void createBoard()
	{
		//the space Pane's children is cleared before it is recreated
		space.getChildren().clear();
		
		//goes through the 2D array and makes and places the different tiles in the Pane
		for(int x = 0; x < tetris.getShow().length; x++)
		{
			for(int y = 0; y < tetris.getShow().length; y++)
			{
				//tile is a small Pane within the larger space Pane
				Pane tile = new Pane();
				
				//the position is determined
				tile.setTranslateX(x*30);
				tile.setTranslateY(y*30);
				
				//the size of the tiles is determined
				tile.setPrefHeight(30);
				tile.setPrefWidth(30);
			
				//the tile is added to the space Pane
				space.getChildren().add(tile);
			}
		}
		
	}
	//The loose text is generated
	private void loseText()

	{
		Text lostText = new Text();
		lostText.setText("You Loose \nPress reset!");
		lostText.setStyle("-fx-font-size: 40px;  -fx-font-family: roboto;");
		lostText.setFill(Color.RED);
		lostText.setTranslateX(120);
		lostText.setTranslateY(170);
		space.getChildren().add(lostText);
	}
	private void saveText(boolean AR, String content)
	{
		warning.getChildren().clear();
		
		Text saveText = new Text();
		
		saveText.setText(content);
		saveText.setStyle("-fx-font-size: 16px; -rtfx-background-color: blue;  -fx-font-family: roboto;");
		saveText.setFill(Color.BLACK);
		saveText.setTranslateX(30);
		saveText.setTranslateY(40);
		warning.getChildren().add(saveText);
		warning.setStyle("-fx-background-color: white;");
		warning.setVisible(AR);
	
	}
	//The highscorelist is generated for display
	private void initiateDisplay() {
		display.getChildren().clear();
		
		Text highscoreText = new Text();
		Text titleText = new Text();
		
		if(!fil.isFileEmpty(FileNames.HS_FILE.toString()))
			highscoreText.setText(fil.highscoresAsStringList());
		else
			highscoreText.setText("Tomt");
		highscoreText.setStyle("-fx-font-size: 15px;  -fx-font-family: roboto;");
		highscoreText.setFill(Color.BLACK);
		highscoreText.setTranslateX(35);
		highscoreText.setTranslateY(65);
		
		titleText.setText("Highscores:");
		titleText.setStyle("-fx-font-size: 25px;  -fx-font-family: roboto;");
		titleText.setFill(Color.BLACK);
		titleText.setTranslateX(35);
		titleText.setTranslateY(35);
		
		display.getChildren().add(titleText);
		display.getChildren().add(highscoreText);
		display.setStyle("-fx-background-color: white;  -fx-font-family: roboto;");
		
		//as default it is hidden
		display.setVisible(false);
		
		
	}
	//This method draws the entire board
	private void drawBoard()
	{
		//collects resource from the resources folder, and makes a usable path
		String bakgrunn = getClass().getClassLoader().getResource("bakgrunn.jpg").toExternalForm();
		
		//the background is set as given. It is placed on the Pane in the back, so the constantly updating Panes doesent interfere.
		bak.setStyle("-fx-background-image: url('"+bakgrunn+"'); -fx-background-repeat: stretch; -fx-background-size: 450 450;");
		
		//goes through the 2D logic array, and "paints" the tiles accordingly
		for(int x = 0; x < tetris.getShow().length; x++)
		{
			for(int y = 0; y < tetris.getShow().length; y++)
			{		
				//air os transparent
				if (tetris.getShow()[y][x]==0)
					space.getChildren().get(x*tetris.getShow().length + y).setStyle("-fx-opacity: 0;");
				
				//everyting else is painted with the matching image, which is decided by the getTileColor() method
				else
					space.getChildren().get( (x*tetris.getShow().length + y)).setStyle(" -fx-background-image: url('"+getTileColor(tetris.getShow()[y][x])+"'); -fx-background-repeat: stretch; -fx-background-size: 30 30;");
			}
			
		}
		
		
		
			
	}
	//the method that decides how the different tiles should look
	private String getTileColor(int square)
	{
		//the int value from the logic decides how the tile should be colored
		if(square == 0)
		{
			return  getClass().getClassLoader().getResource("rod_kloss.png").toExternalForm();
		} else if(square == 1)
		{
			return  getClass().getClassLoader().getResource("gra_kloss.png").toExternalForm();
		} else if(square == 2)
		{
			return  getClass().getClassLoader().getResource("rod_kloss.png").toExternalForm();
		} else if (square == 4)
		{
			return getClass().getClassLoader().getResource("gronn_kloss.png").toExternalForm();
		}else
		{
			return "#e5303a";
		}

	}
	
//The methods to control the logic and game. Game logic (paused) is used here, because it adds very little here, and
//nicely ensures the right updates happens only when the game is live. And the logic is hidden behind encapsulated methods.
	
	//rotates the piece and updates the board if it is not paused.
	@FXML
	public void handleRotatePiece()
	{
		if (!tetris.isPaused()) 
		{
			tetris.rotatePiece();
			tetris.updateBoard();
			drawBoard();
		}
		
	}
	//moves the piece right and updates the board if it is not paused.
	@FXML
	public void handleRight()
	{
		if (!tetris.isPaused()) 
		{
			tetris.moveRight();
			tetris.updateBoard();
			drawBoard();
		}
	}
	//moves the piece left and updates the board if it is not paused.
	@FXML
	public void handleLeft()
	{
		if (!tetris.isPaused()) 
		{
			tetris.moveLeft();
			tetris.updateBoard();
			drawBoard();
		}
	}
	//moves the piece all the way down and updates the board if it is not paused.
	@FXML
	public void handleDown()
	{
		if (!tetris.isPaused()) 
		{
			tetris.movePieceDown();
			tetris.updateBoard();
			drawBoard();
		}

	}
	//pauses and unPauses the game, if it is not lost.
	@FXML
	public void handlePaused()
	{
		if (tetris.isLost()==true)
			return;
		saveText(false, "");
		if (tetris.isPaused())
			tetris.setPaused(false);
		else if (tetris.isLost()==false)
			tetris.setPaused(true);
	}
	
//handles the resetting of the game
	@FXML
	public void handleReset()
	{
		saveText(false, "");
		//end last game with clearing the timers, and resetting them.
		timer.cancel();
		timer2.cancel();
		
		task.cancel();
		task2.cancel();
		
		timer.purge();
		timer2.purge();
		
		//hides textfield
		input.setVisible(false);
		
		//starts from scratch with the needed methods encapsulated in this method
		initalState();
	}
	//saves the current logic 2D array to file, with the score and size
	@FXML
	public void handleSave()
	{
		if (!tetris.isLost())
			try {
			fil.saveLastGame(tetris.getScoreInt(), tetris.getBoardSize(), tetris.getShow(), FileNames.SAVE_FILE.toString());
			} catch (Exception e) {
				saveText(true, e.getMessage() + "\n");
				e.printStackTrace();
			}
	}
	//resets the game and initates as the saved file suggests
	@FXML
	public void handleLoad()
	{
		//only if file is not empty
		if(!fil.isFileEmpty(FileNames.SAVE_FILE.toString()))
		{
			//end last game with clearing the timers, and resetting them.
			timer.cancel();
			timer2.cancel();
			
			task.cancel();
			task2.cancel();
			
			timer.purge();
			timer2.purge();
			
			//hides textfield
			input.setVisible(false);
			
			//restarts the game with the needed methods, but this time based on file. (not from scratch)
			initalLoad();
		}
		//if file is empty you will be told to try to save a game first
		else 
		{
			saveText(true, "You must save a game first" + "\n");
		}
	}
	//this method displays the highscorelist
	@FXML
	public void handleDisplay() 
	{
		saveText(false, "");
		//it both hides and shows the list. Depending on its current state, the Pane is either showed or hid.
		if (!display.isVisible()) {
			display.setVisible(true);
		}
		else  {
			display.setVisible(false);
			
		}
			
	}
	
	

}
