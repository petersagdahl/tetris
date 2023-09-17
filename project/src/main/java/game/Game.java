package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import helpers.FileNames;

/*
* Things to do:
* 
* 
* 
*/
public class Game {
	
//to achieve good encapsulation, all the variables are set as private. The only way to reach them, is through getter and setter.
	// the grid
	private Board board;
	
	// guide pieces, with default values.
	private int stage = 2;
	private boolean bottom = false;
	private int pieceNow;
	private int rotation = 1;
	private int piecePos = 6;

	// reference pieces. Since bottomPieces never uses index or value to find it, it can be a collection.
	private Collection<Integer> bottomPieces;
	private ArrayList<Integer> sidePieces; 
	
	// gamestate
	private boolean paused = true;
	private boolean lost = false;
	
	//score and player
	private int score = 0;
	private String user;
	
	
	
//Overload used for two different scenarios. A game can be initiated both through size and saved file.
	//Game initialized through size.
	public Game(int size)
	{
		// sets score to 0, when a new game is generated
		setScore(0);
		
		//a board-object is initiated, using its first constructor. And the initial piece position established.
		board = new Board(size);
		piecePos = board.getSize()/2;

		//the two arrays which keep track of the surroundings is initiated.
		bottomPieces = new ArrayList<>();
		sidePieces = new ArrayList<>();
	}
	//game initialized through a saved file
	public Game(String fileName)
	{	
		//a board-object is initiated, using its second constructor. And the initial piece position established.
		board = new Board(fileName);
		piecePos = board.getSize()/2;
		
		//the two arrays which keep track of the surroundings is initiated.
		bottomPieces = new ArrayList<>();
		sidePieces = new ArrayList<>();
	}
	
//Basic movement of pieces, with crucial error-checking
	//move the piece right
	public void moveRight()

	{
		//calls the checkMovementR() method from the Board class
		board.checkMovementR(rotation, pieceNow, stage, piecePos);
		
		//as long as the piece keeps within the boards boundaries, and the game is not paused.
		//the paused-check is done twice (both here and in the controller). This is for testing purposes and safety.
		if (sidePieces.get(0) != board.getSize() -1 && sidePieces.get(1) != board.getSize() -1 && board.isCanMove() &&
			!isPaused())
			//moved piece one step to the right.
			piecePos += 1;
		//if the checkMovementR sets canMove = false, the value has to be updated (to true) before the moveRight() method is exited.
		board.setCanMove(true);
	}
	//move the piece left
	public void moveLeft()

	{
		//calls the checkMovementL() method from the Board class
		board.checkMovementL(rotation, pieceNow, stage, piecePos);
		//as long as the piece keeps within the boards boundaries, and the game is not paused.
		//the paused-check is done twice (both here and in the controller). This is for testing purposes and safety.
		if (sidePieces.get(0) != 0 && sidePieces.get(1) != 0 && board.isCanMove() &&
			!isPaused())
			//moved piece one step to the right.
			piecePos -= 1;
		//if the checkMovementL sets canMove = false, the value has to be updated (to true) before the moveLeft() method is exited.
		board.setCanMove(true);
	}
	//rotate the piece
	public void rotatePiece()
	{
		//checks if the rotation is ok. Using the method found in the board class.
		board.checkRotation(rotation, pieceNow, stage, piecePos);
		
		//if it caRoate= true and the game is not paused, it will rotate the piece
		if (board.isCanRotate() && !isPaused())	
		{
			//the piece can have one of four possible rotations. After the last one, it will return to the first.
			if (rotation == 4)	
				rotation = 1;
			else
				rotation +=1;
		}
		//if the canRoate() sets canMove = false, the value has to be updated (to true) before the roatePiece() method is exited.
		board.setCanRotate(true);
		
	}	
	//move the piece fast towards the bottom
	public void movePieceDown()
	{
		//checks if the game is paused
		if (!isPaused())
		{
			//moves the piece until it reaches the bottom
			while (!checkUnderBottom())
			{
				updateStage();
				updateBoard();
			}
		}
	}
	
	
//game logic
	//converts the displayed board into the board everything will operate from afterwards. (Making pieces into stone)
	public void updateBoard()
	{
		
		//a board is made into "scenery" when the piece reaches the bottom.
		if (bottom) {
			//Lines can be removed when the piece has reached the bottom
			board.setCanRemove(true);
			
			// Copies the board represented by "present" over to show, without making a reference. 
			//this makes it possible to manipulate one, without changing the other.
			int[][] toShow =Arrays.stream(board.getPresent())
	                .map(a ->  Arrays.copyOf(a, a.length))
	                .toArray(int[][]::new);
			board.setShow(toShow);			
		}

		//makes the board to be used from now on
		board.drawBoard(stage, piecePos, pieceNow);
		
		//makes the piece at the right position. This happens in the board class.
		try {
		if (pieceNow == 2)
		{
			board.makeStick(rotation, piecePos, stage, pieceNow, bottomPieces, sidePieces);
		}
		if (pieceNow == 4)
			board.makeTee(rotation, piecePos, stage, pieceNow, bottomPieces, sidePieces);
		} catch (Exception e) { 
			System.out.println(e.getMessage());
		}
		
		//Checks if a point has been scored, and if line(s) are to be removed.
		board.scan();
		
		//gets the score from the board class, and stores it in the game class.
		setScore(board.getScore());
		
	}
	//checks if the piece has reached some sort of bottom.
	private boolean checkUnderBottom()
	{
		//Since we dont need the indexes we can use a for-each loop. Here to check whether the piece is at the bottom.
		for (int pieceType : bottomPieces)
		{
			if (pieceType != 0)
				return true;
		}
		return false;
		
	}
	// moves the piece down every given update. And generates piece when the piece has reached the bottom.
	public void updateStage()
	{	
		//if the piece has reached the bottom it will:
		if (checkUnderBottom())
		{
			//clear the bottomPiece array, since a new piece will be generated.
			bottomPieces.clear();
			
			//sets bottom = true
			bottom=true;
			
			//now a line can be removed
			board.setCanRemove(true);
		}	
		
		//updates the stage, as long as there is more "board" to move in.
		if (stage < board.getSize() -1 && !bottom)
		{
			stage+=1;
		} 
		
		//if it has reached the bottom the live board will become the "scenery", stage reset and piece generated.
		else if (bottom)
		{
			//the live-board (called show) is copied over to present
			present();
			
			//stage is set to the default 2, where a new piece can be generated.
			stage = 2;
			
			//a new piece is generated.
			genPiece();
			
			//the current piece is not longer at the bottom (since it is a new one).
			bottom = false;
		}
		
		
	}
	//copies the live board (show) over to present.
	private void present()
	{	
		//uses streams to take every entry in show in to present.
		int[][] toPresent =Arrays.stream(board.getShow())
                .map(a ->  Arrays.copyOf(a, a.length))
                .toArray(int[][]::new);
		board.setPresent(toPresent);
	}
	//generates a new random piece.
	public void genPiece()
	{
		//if the stack of previous placed pieces has reached the piece-generation-spot, the game is lost
		if(board.getShow()[stage][board.getShow().length/2-1] != 0)
		{
			//the game is now lost
			setLost(true);
			//paused= true ensures nothing more can be done after the game is lost.
			setPaused(true);
			
//eventuelt kast en illegalstate her.

		}
		// a new piece has the default rotation 1
		rotation =1;
		
		//piecePos is set to its default position.
		piecePos = board.getSize()/2-1;
		
		//gets a new piece from the piece class
		Piece p = new Piece();
		
		//stores the value of the new piece in Game
		pieceNow = p.getPiece();
	}
	
	
	


//getters for all the relevant variables.
	public int[][] getShow() {
		return board.getShow();
	}
	public String getScore() {
		return score+"";
	}
	public int getScoreInt()
	{
		return score;
	}
	public boolean isPaused() {
		return paused;
	}
	public boolean isLost() {
		return lost;
	}
	public String getUser() {
		return user;
	}
	public int getBoardSize()
	{
		return board.getSize();
	}
	
	//getters for tests
	public int getStage() {
		return stage;
	}
	public int getPieceNow() {
		return pieceNow;
	}
	public int getPiecePos() {
		return piecePos;
	}
	public int getRotation() {
		return rotation;
	}
	public Board getBoard() {
		return board;
	}


	//setters
	public void setPaused(boolean paused) {
		//does not need check, because only valid value of paused is false or true anyway.
		this.paused = paused;
	}
	public void setLost(boolean won) {
		//does not need check, because only valid value of paused is false or true anyway.
		this.lost = won;
	}
	public void setScore(int score) {
		if (score <0)
			throw new IllegalArgumentException("A score value is always positive.");
		this.score = score;
	}
	//throws because it has to be handled
	public boolean setUser(String user) throws IllegalArgumentException {
		if (user == null)
			throw new IllegalArgumentException("Username cannot be null");
		else if (user.isEmpty())
			throw new IllegalArgumentException("Username cannot be empty");
		else if (user.length()> 20)
			throw new IllegalArgumentException("Username cannot be that long");
	
		this.user = user;
		return true;
	}

//for testing.
	@Override
	public String toString() {
		String print = "";
		
		for(int y = 0; y<board.getSize(); y++)
		{
			for(int x = 0; x<board.getSize(); x++)
			{
				print += board.getShow()[y][x]+" ";
			}
			print+= "\n";
		}
		return print;
	}
	public static void main(String[] args)
	{
		Game game = new Game(15);
		game.setPaused(false);
//		while(g.getPieceNow()!=2)
//		{
//			g.genPiece();
//		}
//		g.updateBoard();
//		for(int i=0; i<19; i++)
//		{
//			g.moveLeft();
//			g.updateStage();
//			g.updateBoard();
//		
//		}
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
		System.out.println(game.getRotation());
		game.rotatePiece();
	
	
		System.out.println(FileNames.HS_FILE);
		
	}



	
	
	
}
