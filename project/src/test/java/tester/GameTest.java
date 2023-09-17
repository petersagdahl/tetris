/**
 * 
 */
package tester;

import java.util.Arrays;



import game.Game;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GameTest {

	private Game game;
	
	//setup before movetests
	private void setUpForMovementTest()
	{
		game.genPiece();
		game.updateBoard();
		game.setPaused(false);
	}
	//a game object is initiated
	@BeforeEach
	public void start() 
	{
		game = new Game(15);
		game.setPaused(false);
	}

//test constructors
	//initialized with size
	@Test
	@DisplayName("Initializes a game based on size. Checks if (some of the) points are filled.")
	public void testGameWithSize() {
		
		Assertions.assertEquals(1, game.getBoard().getShow()[14][0]);
		Assertions.assertEquals(0, game.getBoard().getShow()[0][0]);
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Game smallGame = new Game(5);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Game bigGame = new Game(50);
		});
		
		
	}
	//initialized with file
	@Test
	@DisplayName("Initializes a game based on a file. Checks if (some of the) points are filled.")
	public void testGameWithFile() {
		Assertions.assertEquals(1, game.getBoard().getShow()[14][0]);
		Assertions.assertEquals(0, game.getBoard().getShow()[0][0]);
		
		Assertions.assertThrows(IllegalStateException.class, () -> {
			Game emptyGame = new Game("Empty.txt");
		});
	}
	
//Normal functionality
	//movement with no obstacles
	@Test
	@DisplayName("Test if a piece is moved to the right")
	public void testMoveRight() {
		
		
		setUpForMovementTest();
		game.moveRight();
		game.updateStage();
		Assertions.assertEquals(7, game.getPiecePos());
		
		
	}
	
	@Test
	@DisplayName("Test if a piece is moved to the left")
	public void testMoveLeft() {
		
		
		setUpForMovementTest();
		game.moveLeft();
		game.updateStage();
		Assertions.assertEquals(5,game.getPiecePos());
	}
	
	@Test
	@DisplayName("Test if a piece is moved down")
	public void testMoveDown() {
		setUpForMovementTest();
		game.movePieceDown();
	
		Assertions.assertEquals(13, game.getStage());
	}
	
	@Test
	@DisplayName("Test if the rotations gets updated.")
	public void testRotatePiece() {
		
		setUpForMovementTest();
		game.rotatePiece();
		game.updateStage();
		Assertions.assertEquals(2, game.getRotation());
		
		
	}
	
	
//edge-cases and illegal moves
	//the setup is done so that i know the bricks are where they should be to have something in the way.
	//first we check that you cannot move outside the board
	@Test
	@DisplayName("Ensures movement outside the board is illegal.")
	void outsideBoard() 
	{
		while(game.getPieceNow()!= 4)
			game.genPiece();
		game.updateBoard();
	
		for(int i=0; i<8; i++)
		{
			game.moveRight();
			
		}
		game.moveRight();
		Assertions.assertEquals(13, game.getPiecePos());
		
		
		for(int r=0; r<15; r++)
		{
			game.moveLeft();
		}
		game.moveLeft();
		Assertions.assertEquals(1, game.getPiecePos());
	}
	//now we check if it is possible to move into another brick
	@Test
	@DisplayName("Checks if the gamelogic prevents movement into another piece.")
	void intoBrick() 
	{
		while(game.getPieceNow()!=2)
			game.genPiece();
		game.updateStage();
		game.updateBoard();
		game.rotatePiece();
		game.moveRight();;
		game.moveRight();
		game.moveRight();
		for(int i=0; i<19; i++)
		{
			game.updateStage();
			game.updateBoard();
			
		}
		for(int i=0; i<1; i++)
		{
			
			game.moveRight();
			game.updateStage();
			game.updateBoard();
			Assertions.assertEquals(7, game.getPiecePos());
		}
		
	}
	//checks if illegal rotations are blocked
	@Test
	@DisplayName("Checks if the gamelogic prevents rotation into another piece.")
	void blockedRotation() 
	{
		//setup so that we make sure a blocked rotation cannot occur (regardless of pieceType.
		while(game.getPieceNow()!=2)
		{
			game.genPiece();
		}
		game.updateStage();
		game.updateBoard();
		game.rotatePiece();
		game.moveRight();
		game.moveRight();
		game.moveRight();
		for(int i=0; i<10; i++)
		{
			game.updateStage();
			game.updateBoard();
			
			
		}
		game.rotatePiece();
		game.updateBoard();
		game.rotatePiece();
		game.updateBoard();
		game.rotatePiece();
		game.updateBoard();

		for(int s=0; s<11; s++)
		{
			game.updateStage();
			game.updateBoard();
			
		}
		game.rotatePiece();
		game.moveRight();
		for(int r=0; r<10; r++)
		{
			game.updateStage();
			game.updateBoard();
			System.out.println(game);

		}
		Assertions.assertEquals(2, game.getRotation());
		game.rotatePiece();
		Assertions.assertEquals(2, game.getRotation());

	
	}
	//checks if piece moves down when stage is updated
	@Test
	@DisplayName("Checks if stageupdate (what happens every loop) moves the piece down.")
	void updateStageMovesDown() 

	{
		//when a piece is generated the stageValue is 2
		
		Assertions.assertEquals(2, game.getStage());
		game.updateStage();
		Assertions.assertEquals(3, game.getStage());
	}
	
	
	
//setters 
	//can set score, but not a score less than 0
	@Test
	@DisplayName("Checks if the score is updated, with legal values.")
	public void testSetScore() {
		game.setScore(10);
		
		Assertions.assertEquals(10, game.getScoreInt());
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			game.setScore(-1);
		});
	}
	//set user name within certain limitations. 
	@Test
	@DisplayName("Checks if the user is updated, with legal values.")
	public void testSetUser() {
		game.setUser("peter");
		
		Assertions.assertEquals("peter", game.getUser());
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			game.setUser("");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			game.setUser(null);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			game.setUser("qwertyuiopåasdfghjkll");
		});
	}
	//cannot move when the game is paused
	@Test
	@DisplayName("Checks that a paused game, prevents movement temporarely.")
	void testPaused() 
	{
		
		setUpForMovementTest();
		game.setPaused(true);

		//piece-position is by default 6
		game.moveRight();
		Assertions.assertEquals(6, game.getPiecePos());
		game.moveLeft();
		Assertions.assertEquals(6, game.getPiecePos());
		game.rotatePiece();
		Assertions.assertEquals(1, game.getRotation());
		
		game.setPaused(false);
		game.moveRight();
		Assertions.assertEquals(7, game.getPiecePos());

	}
	//one out of two pieces is generated
	@Test
	@DisplayName("Is one out of two pieces generated.")
	void testGenPiece() 
	{
		Integer[] expectedPieces = {2,4};
		game.genPiece();
		Assertions.assertTrue(Arrays.asList(expectedPieces).contains(game.getPieceNow()));
	}
	//game is set to lost, when a piece reaches the generation-of-piece-spot
	@Test
	@DisplayName("Is the gamestate set to lost, when a piece reaches the top.")
	public void testIsLost()
	{
		
		game.getShow()[game.getStage()][game.getShow().length/2-1] = 1;
		game.genPiece();
		
		Assertions.assertTrue(game.isLost());
		Assertions.assertTrue(game.isPaused());
	}
	

}
