package helpers;

import java.util.ArrayList;
import game.Board;



//an interface so that one can change how files are treated in the fileHandling class, without changing the functionality
public interface GameFile {
	
	public void highscore(int score, String name);
	
	public void saveLastGame(int score, int size, int[][] show, String filename);
	
	public ArrayList<Integer> loadFile(String fileName, Board board);
	
	public String highscoresAsStringList();

	public boolean isFileEmpty(String fileName);
	
	
	
	
}
