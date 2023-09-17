package tester;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;

import game.Board;
import helpers.Filbehandling;

class BoardTest {

	private Board board;
	private Filbehandling fil;
	
	private int[][] compare;
	
//help methods
	//creates a 2D array
	private void compareBoard() 
	{
		compare = new int[15][15];
		
		
		for(int y = 0; y<15; y++)
		{
			for(int x = 0; x<15; x++)
			{
				if(y== 14) 
				{
					compare[y][x] = 1;	
				}
				else
				{
					compare[y][x] = 0;
				}

			}
		}
		fil = new Filbehandling();
		fil.saveLastGame(0, 15, compare, "Compare.txt");
	}
	//checks if two different 2D arrays are equal
	private boolean check(int[][] com, int[][] com2)
	{
		
		for(int y = 0; y<15; y++)
		{
			for(int x = 0; x<15; x++)
			{
				if(com[y][x] != com2[y][x])
					return false;
			}
		}
		return true;
	}
	
	//initiate a board
	@BeforeEach
	public void start() 
	{
		board = new Board(15);
	}
	
	
	
//tests the two different contructors
	//sizebased
	@Test
	@DisplayName("Tests the sizebased cosntructor")
	public void testConstructorWithSize() {
		compareBoard();
		Assertions.assertTrue(check(board.getShow(), compare));
		
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Board smallBoard = new Board(5);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Board bigBoard = new Board(25);
		});

	}
	//filebased
	@Test
	@DisplayName("Tests the filebased cosntructor")
	public void testConstructorWithFile() {
		compareBoard();
		Board fileBoard = new Board("Compare.txt");
		int[][] fileArray = new int[15][15];
		int counter = 0;
	
		ArrayList<Integer> fromFile = fil.loadFile("Compare.txt", fileBoard);
		
		for (int y =0;y<15;y++)
		{
			for (int x =0;x<15;x++)
			{
				fileArray[y][x] = fromFile.get(counter);
				counter++;
			}
		}
		
		Assertions.assertTrue(check(fileBoard.getShow(), fileArray));
		
		Assertions.assertThrows(IllegalStateException.class, () -> {
			Board emptyBoard = new Board("Empty.txt");
		});
	
	}
//tests the different methods
	//checks if the board is drawn correctly, and if exceptions are thrown
	@Test
	@DisplayName("Tests if the board is drawn correctly, and if the right exceptions are thrown.")
	public void testdrawBoard()
	{	
		board.drawBoard(2, 6, 2);
		Assertions.assertEquals(board.getShow()[2][6], 2);
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.drawBoard(2, 6, 3);
		
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.drawBoard(0, 6, 2);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.drawBoard(15, 6, 2);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.drawBoard(2, -1, 4);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.drawBoard(2, 15, 4);
		});
	
	}
	//checks if the scan method finds and removes the right lines
	@Test
	@DisplayName("Is points given, when a line is filled.")
	public void testScan()
	{
		compareBoard();
		for (int x=0;x<15;x++)
		{
			compare[13][x] = 1;
		}
		board.setShow(compare);
		//is the complete line removed:
		Assertions.assertEquals(0, board.getScore());
		
		
		//scoregiving happens inside a block which depends on canRemove==true.
		board.setCanRemove(true);
		board.scan();
		Assertions.assertEquals(1, board.getScore());
		
	}
	//more specifically checks the removeLine() method, but through scan(), because it is private.
	@Test
	@DisplayName("Is a line moved correctly.")
	public void testRemoveLine()
	{
		compareBoard();
		for (int x=0;x<15;x++)
		{
			compare[13][x] = 1;
		}
		board.setShow(compare);
		board.setCanRemove(true);
		board.scan();
		
		//checks if a spot where the line previously were is gone.
		Assertions.assertEquals(0, board.getShow()[13][0]);
		
	}
	//chekcs if the rest of the board is moved down after the line is removed
	@Test
	@DisplayName("Does the board above the removed line, get moved down.")
	public void testMoveDown()
	{
		compareBoard();
		for (int x=0;x<15;x++)
		{
			compare[13][x] = 1;
		}
		compare[12][0] = 1;
		
		board.setShow(compare);
		board.setPresent(compare);
		board.setCanRemove(true);
		board.scan();
		
		//checks if line is gone, and if the single point is moved down.
		Assertions.assertEquals(0, board.getPresent()[12][0]);
		Assertions.assertEquals(1, board.getPresent()[13][0]);
	}
	
//big hardcoded methods, do they work as expected, and is IllegalArgumentEsceptions thrown.
	//ensures the method denies illegal rotations
	@Test
	@DisplayName("Ensures illegal rotations are not allowed.")
	public void testChechRotation()
	{
//		//for stick
		board.setCanRotate(true);
		board.checkRotation(1, 2, 13, 7);
		Assertions.assertFalse(board.isCanRotate());
		board.setCanRotate(true);
		board.checkRotation(4, 2, 5, 14);
		Assertions.assertFalse(board.isCanRotate());
		board.setCanRotate(true);
		board.checkRotation(2, 2, 5, 0);
		Assertions.assertFalse(board.isCanRotate());
		board.setCanRotate(true);
		board.checkRotation(4, 2, 5, 5);
		Assertions.assertTrue(board.isCanRotate());

		
		//for tee
		board.setCanRotate(true);
		board.checkRotation(1, 4, 13, 7);
		Assertions.assertFalse(board.isCanRotate());
		board.setCanRotate(true);
		board.checkRotation(4, 4, 5, 14);
		Assertions.assertFalse(board.isCanRotate());
		board.setCanRotate(true);
		board.checkRotation(2, 4, 5, 0);
		Assertions.assertFalse(board.isCanRotate());
		board.setCanRotate(true);
		board.checkRotation(4, 4, 5, 5);
		Assertions.assertTrue(board.isCanRotate());
		
		
		
		//exceptions thrown
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.checkRotation(2, 4, 15, 7);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.checkRotation(2, 4, 15, 7);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.checkRotation(2, 4, 12, 15);
		});
		
	
	}
	//checks if a stick is made as it is supposed to
	@Test
	@DisplayName("Is the stick made corretly. If right points are filled.")
	public void testMakeStick()
	{
		ArrayList<Integer> bottom = new ArrayList<>();
		ArrayList<Integer> side = new ArrayList<>();
		
		board.makeStick(1, 6, 2, 2, bottom, side);
		
		Assertions.assertEquals(board.getShow()[2][5], 2);
		Assertions.assertEquals(board.getShow()[2][4], 2);
		Assertions.assertEquals(board.getShow()[2][7], 2);
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.makeStick(5,6,2,2,bottom, side);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.makeStick(2,-1,2,2,bottom, side);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.makeStick(2,6,0,2,bottom, side);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.makeStick(2,6,2,1,bottom, side);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.makeStick(2,6,2,2,bottom, null);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.makeStick(2,6,2,2,null, side);
		});
		
		
		
	}
	//checks if a Tee is made as it is supposed to
	@Test
	@DisplayName("Is the Tee made corretly. If right points are filled.")
	public void testMakeTee()
	{
		ArrayList<Integer> bottom = new ArrayList<>();
		ArrayList<Integer> side = new ArrayList<>();
		
		board.makeTee(1, 6, 2, 2, bottom, side);
		
		Assertions.assertEquals(board.getShow()[2][5], 2);
		Assertions.assertEquals(board.getShow()[1][6], 2);
		Assertions.assertEquals(board.getShow()[2][7], 2);
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.makeTee(5,6,2,2,bottom, side);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.makeTee(2,-1,2,2,bottom, side);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.makeTee(2,6,0,2,bottom, side);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.makeTee(2,6,2,1,bottom, side);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.makeTee(2,6,2,2,bottom, null);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.makeTee(2,6,2,2,null, side);
		});
	}
	//ensures the method denies illegal movement to the right
	@Test
	@DisplayName("Ensures illegal movements to the right are not allowed.")
	public void testCheckMovementR()
	{
		board.setCanMove(true);
		board.checkMovementR(1, 2, 5, 13);
		Assertions.assertFalse(board.isCanMove());
		
		board.setCanMove(true);
		board.checkMovementR(2, 2, 5, 14);
		Assertions.assertFalse(board.isCanMove());
		
		
		board.setCanMove(true);
		board.checkMovementR(1, 4, 5, 13);
		Assertions.assertFalse(board.isCanMove());
		
		board.setCanMove(true);
		board.checkMovementR(2, 4, 5, 13);
		Assertions.assertFalse(board.isCanMove());
	
		//exceptions thrown
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.checkMovementR(2, 4, 15, 7);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.checkMovementR(2, 4, 15, 7);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.checkMovementR(2, 4, 12, 15);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.checkMovementR(5, 4, 12, 7);
		});
	}
	//ensures the method denies illegal movement to the left
	@Test
	@DisplayName("Ensures illegal movements to the right are not allowed.")
	public void testCheckMovementL()
	{
		board.setCanMove(true);
		board.checkMovementL(1, 2, 5, 2);
		Assertions.assertFalse(board.isCanMove());
		
		board.setCanMove(true);
		board.checkMovementL(2, 2, 5, 0);
		Assertions.assertFalse(board.isCanMove());
		
		
		board.setCanMove(true);
		board.checkMovementL(1, 4, 5, 1);
		Assertions.assertFalse(board.isCanMove());
		
		board.setCanMove(true);
		board.checkMovementL(2, 4, 5, 0);
		Assertions.assertFalse(board.isCanMove());
		
		
		
		//exceptions thrown
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.checkMovementL(2, 4, 15, 7);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.checkMovementL(2, 4, 15, 7);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.checkMovementL(2, 4, 12, 15);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			board.checkMovementL(5, 4, 12, 7);
		});
	}
	
	//deletes temporary file.
	@AfterAll
	static void endAll()
	{
		Path filename = Path.of("Compare.txt");
		File file = new File(filename.toString());
		 
		file.delete();
	}
}
