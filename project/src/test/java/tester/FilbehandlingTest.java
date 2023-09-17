package tester;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import game.Board;
import helpers.Filbehandling;

class FilbehandlingTest {

	private Filbehandling file;
	private Board board;
	private Board boardTwo;
	
	//method to make an 2D array out of an 1D array
	private int[][] makeArray(ArrayList<Integer> array, int size)
	{
		int[][] toReturn = new int[size][size];
		Integer counter = 0;
		
		for(int y=0;y<size;y++)
		{
			for(int x=0;x<size;x++)
			{
				toReturn[y][x] = array.get(counter);
				counter ++;
			}
		}
		return toReturn;
	}
	
	
	//initiates a file object and a board object
	@BeforeEach
	public void start() 
	{
		file = new Filbehandling();
		board = new Board(15);
	}

//test saveLastGame
	//The optimal solution would be to create a new file to save the highscore to, since then the game-files would've been untouched (as i did for game-saving)
	//i chose not to do this, because i dont want the highscore save file to be easy to change. And demonstrates this with save- and loadFile instead
	
	@Test
	@DisplayName("Highscore and username is added to the file.")
	public void testHighscore() {
		//adds high highscore, and checks if it is in the file's first line
		file.highscore(100, "Peter");
		Assertions.assertEquals("1: Peter:   100", file.highscoresAsStringList().split("\n")[0]);
		
		//checks is exception is thrown when illegal arguments are entered
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			file.highscore(-1, "Peter");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			file.highscore(2, "");
		});
		
	}
	//tests if it returns the entire highscorelist
	@Test
	@DisplayName("Return the saved highscorelist as string, separated with \n. It should be 10 long.")
	public void testHighscoreAsStringList() {
		//checks if it has the same amount of lines as the document.
		Assertions.assertEquals(10, file.highscoresAsStringList().split("\n").length);
	}
	//tests gamesaving
	@Test
	@DisplayName("Saves a game, and checks if illegal arguments is allowed, and if the game is stored properly.")
	public void testSaveLastGame() {
		file.saveLastGame(10, 15, board.getShow(), "testLagre.txt");	
		
		//checks if the saved array is the same as the board's array. Entry by entry.
		for(int y=0;y<15;y++)
		{
			for(int x=0;x<15;x++)
			{
				Assertions.assertEquals(board.getShow()[y][x], makeArray(file.loadFile("testLagre.txt", board), 15)[y][x]);
			}
		}
		
		//checks if exceptions are thrown at the right time.
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			file.saveLastGame(-1, 15, board.getShow(), "testLagre.txt");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			file.saveLastGame(10, 0, board.getShow(), "testLagre.txt");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			file.saveLastGame(10, 15, null, "testLagre.txt");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			file.saveLastGame(10, 15, board.getShow(), "");
		});
		
	}
	//tests if a file can be loaded correctly
	@Test
	@DisplayName("Tests if a file can be loaded correctly.")
	public void testLoadFile() {
		file.saveLastGame(10, 15, board.getShow(), "testLagre.txt");
		
		//uses constructor in board, because it depends on loadFile. Therefore a valid test.
		boardTwo = new Board("testLagre.txt");		
		assertEquals(board.toString(), boardTwo.toString());
		
		//checks if exception is thrown at the right time
		Assertions.assertThrows(IllegalStateException.class, () -> {
				file.loadFile("emptyFile.txt", board);
				
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Board boardThree = null;
			file.loadFile("testLagre.txt", boardThree);
			
	});
		
		
	}
	//checks if the method can detect an empty file.
	@Test
	@DisplayName("Can the method detect an empty file.")
	public void testIsFileEmpty()
	{
		Assertions.assertTrue(file.isFileEmpty("emptyFile.txt"));
		Assertions.assertTrue(!file.isFileEmpty("testLagre.txt"));
	}
	//deletes the test file.
	 @AfterAll
	 static void endAll() {
		 Path filename = Path.of("testLagre.txt");
		 File file = new File(filename.toString());
		 
		 file.delete();
	 }
	 

}
