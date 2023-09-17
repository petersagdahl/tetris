package tester;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import game.Piece;

class PieceTest {
	
	private Piece piece;
	
	//initiate a piece object
	@BeforeEach
	public void setup()
	{
		piece = new Piece();
	}
	
	
	//tests constructor
	@Test
	@DisplayName("Is one out of the two pieces generated.")
	void testPiece() {
		//the class can generate to different types of pieces: 2 and 4. 1 is obstacle and 3 is the general piece position.
		Assertions.assertTrue(piece.getPiece() == 2 || piece.getPiece() == 4);
	}

}
