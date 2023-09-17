package tester;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import game.HighScorer;

class HighScorerTest {
	private HighScorer highscorer;
	private ArrayList<HighScorer> list;
	
	//initiates a highscorer.
	@BeforeEach
	public void start() 
	{
		highscorer = new HighScorer(10, "peter");
		
	}
	//test constructor
	@Test
	@DisplayName("Tests if a HighScorer can be made correctly, and if Exceptions are thrown.")
	public void testHighScorer() {
		
		Assertions.assertEquals(10, highscorer.getScore());
		Assertions.assertEquals("peter", highscorer.getName());
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			HighScorer h1 = new HighScorer(-1, "peter");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			HighScorer h1 = new HighScorer(-1, "peter");
		});
	}
	//
	@Test
	@DisplayName("Name correctly decided.")
	public void testSetName()
	{
		highscorer.setName("lars");
		Assertions.assertEquals("lars", highscorer.getName());
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			highscorer.setName("");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			highscorer.setName(null);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			highscorer.setName("qwertyuiopåasdfghjkløæ");
		});
	}
	//
	@Test
	@DisplayName("Score properly put.")
	public void testSetScore()
	{
		highscorer.setScore(11);
		Assertions.assertEquals(11, highscorer.getScore());
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			highscorer.setScore(-1);
		});
	}
	
	
//formatting and comparable
	//checks if the sorting is correct
	@Test
	@DisplayName("Is the compare interface correctly implemented")
	public void testCompareTo()
	{
		list = new ArrayList<HighScorer>();
		
		HighScorer highscorer1;
		list.add( highscorer1 = new HighScorer(9, "a"));
		HighScorer highscorer3;
		list.add( highscorer3 = new HighScorer(11, "c"));
		HighScorer highscorer2;
		list.add( highscorer2 = new HighScorer(9, "b"));
		
		Collections.sort(list);
		
		Assertions.assertEquals(11, list.get(0).getScore());
		Assertions.assertEquals("a", list.get(1).getName());
		Assertions.assertEquals("b", list.get(2).getName());
		

	}
	//checks if the toString() method formats the highscorer correct
	@Test
	@DisplayName("Is the HighsScorer properly formated.")
	public void testToString()
	{
		Assertions.assertEquals("peter:   10", highscorer.toString());
		

	}
}
