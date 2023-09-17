package game;

import java.util.Random;


public class Piece {
	
	//the only two relevant variables. Num is set to 3, because that is the Integer for "pieceCenster"
	private int piece;
	private int num = 3;
	
	
	//test
	public int[][] array = new int[15][15];
	
	//constructor that makes a piece
	public Piece()
	{
		//this implementation makes it easier to add additional pieces. 
		//it can generate any number of pieces (if we want to), and makes sure that
		//reserved pieces remain untouched
		while (num == 3 || num == 0 || num ==1)
		{
			num = new Random().nextInt(5);
		}
		this.piece = num;
		
	}
	
	

	public int getPiece() {
		return piece;
	}
	
}
