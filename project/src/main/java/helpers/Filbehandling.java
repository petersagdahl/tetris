package helpers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import game.Board;
import game.HighScorer;

	
//implements the GameFile interface, so that one easily can change the methods used for file reading and writing
public class Filbehandling implements GameFile{
	
	//Naming the file where the highscores are stored (the value is from enum, but for easier use it is renamed)
	final String HSFileName = FileNames.HS_FILE.toString();
	
	//writing the highscore and input name to the file
	public void highscore(int score, String name) throws IllegalArgumentException
	{
		//declares scanner, FileWriter, Highscorer and File (also initiated). This ensures, the path to the file works everywhere.
		Scanner scanner;
		FileWriter writer;
		HighScorer HS;
		File file = new File(HSFileName);
		
		//makes an ArrayList to sort the different scores, after adding the last one.
		//the HighScorer class implements comparable (not comparator because the sortingparameters are fixed). 
		//it is first sorted by score, then name
		ArrayList<HighScorer> highest = new ArrayList<>();
		
		//adds the new highscorer to the ArrayList of all the stored scores
		highest.add(HS = new HighScorer(score, name));
		

		//the scanner has to be inside a try-catch block (or the method could "throw")
		if (!isFileEmpty(HSFileName)) {
		try {
			//generates a new file, with the filename given.
			scanner = new Scanner(new FileReader(file));
			
			//every line in the new file is a string
			String line = scanner.nextLine();
			
			//goes through the entire file. line by line, as long there is a next one
			while (scanner.hasNextLine())
			{
				//splits the line into 3 usable pieces
				String[] info = line.split(":");
				
				//if the line has enough parts, it will get the needed info
				if (info.length>2)
				{
					//makes a new highscorer, using the info from the file: score and name
					HS = new HighScorer(Integer.parseInt(info[2].trim()),info[1].trim());
					
					//the new highscorer is added to the ArrayList
					highest.add(HS);
				}			

				line = scanner.nextLine();
			}
			
			//the scanner is closed
			scanner.close();
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		}
		
		//the arrayList is sorted, making sure the names with the highest score is at the top.
		Collections.sort(highest);
		
		//the FileWriter has to be inside a try-catch block (or the method could "throw")
		try 
		{
			//initiates the FileWriter from the given filename
			writer = new FileWriter(new File(HSFileName));
			
			//makes sure the new highscore-list only has 10 entries
			for(int i=0;i<10;i++)
			{	
				//writes the information to the file, while the ArrayList has more info (up to 10)
				if (i<highest.size())
					writer.write((i+1)+":"+" "+highest.get(i));
				//if there are no more highscorers, it will only add position and a ":"
				else
					writer.write((i+1)+":");
				//the last entry should not have a "next-line" (this is just formatting, and for reading purposes)
				if (i!=9)
					writer.write("\n");
			}
			
			//the FileWriter is closed
			writer.close();
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	//this method returns the highscore list as a string with "next-line", and is used for displaying the list
	public String highscoresAsStringList()
	{
		//uses a scanner to read the file, and gets the highscore-list file.
		Scanner scanner;
		File file = new File(HSFileName);
		
		//initiates the string to return
		String toReturn = "";
		
		//scanner has to be inside a try-catch block (or method could "throw")
		try {
				//initiates the scanner, with the file constant wherever this code is executed (the file is always found/made)
				scanner = new Scanner(new FileReader(file));
				

				//while the file has a next line it will ad it to the return string
				while (scanner.hasNextLine())
				{
					//adds the line + next-line to the return string
					toReturn += scanner.nextLine() + "\n";
	
				}
				
				//the scanner must be closed (to not keep using resources)
				scanner.close();
				
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		
		//returns the finished list as a string
		return toReturn;
	}
	//stores the last game as a textfile. With all the information needed to resume next session.
	public void saveLastGame(int score, int size, int[][] show, String filename) throws IllegalArgumentException
	{
		//input validation
		if (score<0)
			throw new IllegalArgumentException("score cannot be less than 0");
		if (size<10 || size > 20)
			throw new IllegalArgumentException("The size must be between 10 and 20");
		if (show == null)
			throw new IllegalArgumentException("the array cannot be null");
		if (filename.isEmpty() || filename == null)
			throw new IllegalArgumentException("Enter a valid filename.");
		
		
		//declares a FileWriter
		FileWriter writer;
		
		//FileWriter must be inside a try-catch block (or the method could "throws" sending the problem "upwards")
		try 
		{
			//making sure the FileWriter generates or finds the requested file
			writer = new FileWriter(new File(filename));
			
			//first score and size is added to the files first lines
			writer.write("Score"+":"+score);
			writer.write("\n");
			writer.write("Size"+":"+size);
			writer.write("\n");
			
			//next it will go through the game-board, and write it to the file. Using the integers as symbols. (as for the game-logic)
			for(int y=0;y<size;y++)
			{
				for(int x=0;x<size;x++)
				{
					writer.write(show[y][x]+"");
				}
				
				//formated as a 2D array in the text-file as well, making it easy to read
				writer.write("\n");
			}
			
			//writer must be closed
			writer.close();
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	//loads a stored 
	public ArrayList<Integer> loadFile(String fileName, Board board)

	{
		//declars scanner and initiates the ArrayList
		Scanner reader;
		ArrayList<Integer> loadedArray = new ArrayList<>();
		
		//checks if file is empty. This is also handled in the controller, without throwing an exception.
		//chose to throw an illegalState, because the arguemnt is illegal due to the games state; unsaved game
		if(isFileEmpty(fileName))
			throw new IllegalStateException("The file is empty. You will have to save a game before you can load one.");
		//checks if board is null
		if(board == null)
			throw new IllegalArgumentException("This is not a valid board object.");
		
		//scanner must be in a try-catch block (or method could "throws")
		try {
			
			//reads from right file, and generates it if it doesent exist.
			reader = new Scanner(new FileReader(fileName));
			String line;
			Integer counter = 0;
		
			//goes through the file, while it has a next line
			while (reader.hasNextLine())
			{
				//line is initiated as the next line represented as a string
				line = reader.nextLine();
				
				//the second line should be the size
				if(counter ==1)
				{
					//cleans up the line and extracts the size
					board.setSize(Integer.parseInt(line.split(":")[1].trim()));
				}
				//the first line should be the score
				else if(counter ==0)
				{
					//cleans up the line and extracts the score
					board.setScore(Integer.parseInt(line.split(":")[1].trim()));
				}
				//the rest is just the game board
				else
				{
					//adds each entry to the Array.
					for(int i =0; i<line.length();i++)
					{
						//every string is also here converted into an Integer
						loadedArray.add(Character.getNumericValue(line.charAt(i)));
					}
				}
				counter ++;
			}
			//the reader has to be closed (to not take up resources)
			reader.close();
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return loadedArray;
	}
	//Checks if a file is empty
	public boolean isFileEmpty(String fileName)
	{
		File check = new File(fileName);
		if (check.length() ==0)
			return true;
		return false;
	}
	

}
