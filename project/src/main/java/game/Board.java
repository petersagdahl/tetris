package game;


import java.util.ArrayList;
import java.util.Collection;

import helpers.Filbehandling;

public class Board {

//to achieve good encapsulation, alle the variables are set as private. The only way to reach them, is through getter and setter.
	
	
	
	//gamedriven arrays - the grid in which all lives. And their sizes (which always are equal)
	private int[][] show;
	private int[][] present;
	private int size;
	
	//checks
	private boolean canRotate = true;
	private boolean canRemove = false;
	private boolean canMove = true;
	
	//points
	private int score = 0;

	//file management
	private Filbehandling fil;
	

//the initial board, with the option to use existing maps. Overloading with two different contructors
	//Inital board without a presaved file. This one generates "map" based on size only.
	public Board(int size) 
	{
		//exception for invalid size is done in the setSize() method
		setSize(size);
		
		//the two arrays used for the manipulation and display of the game is initialized
		present = new int[this.size][this.size];
		show = new int[this.size][this.size];
		
		//creating the board
		for(int y = 0; y<this.size; y++)
		{
			for(int x = 0; x<this.size; x++)
			{
				//bottom / rock
				if(y== size-1) 
				{
					show[y][x] = 1;	
					present[y][x] = 1;	
				}
				//air
				else
				{
					show[y][x] = 0;
					present[y][x] = 0;	
				}
			}
		}

	}
	//Board created based on the saved game, located in generated file.
	public Board(String fileName) 
	{
		//initiates this board, because the loadFile() method requires a board Object
		Board board = this;
		fil = new Filbehandling();
		
		
		//The input fil is read, and through the loadFile() method checked
		ArrayList<Integer> squares = fil.loadFile(fileName, board);
		
		
		//the two arrays used for the manipulation and desplay of the game is initialized
		present = new int[this.size][this.size];
		show = new int[this.size][this.size];
		
		//Information from the file is made into an 2D array (the to mentioned above)
		Integer counter = 0;
		for(int y = 0; y<this.size; y++)
		{
			for(int x = 0; x<this.size; x++)
			{
				//ground
				if (squares.get(counter) == 0||squares.get(counter)==1)
				{
					show[y][x] = squares.get(counter);	
					present[y][x] = squares.get(counter);
				}
				//air
				else
				{
					show[y][x] = 0;
					present[y][x] = 0;
				}
				//Because the returned array from the file is one dimensional, we have to keep track of the index
				counter++;
			}
		}

	}
	
	
	
	//creation of the editable boards
	public void drawBoard(int stage, int piecePos, int pieceNow)
	{
		if (stage <2 || stage > getSize()-1||
			piecePos<0|| piecePos>getSize()-1||
			(pieceNow!=2 && pieceNow!=4))
				throw new IllegalArgumentException("The input values are not within the board, or the piece is not valid.");
				
		
		//redraws the board adding live object (piece) to the "landscape" made by previous activity
		for(int y = 0; y<show.length; y++)
		{
			for(int x = 0; x<show.length; x++)
			{
				//draws the piece
				if (y == stage && x == piecePos)
				{
					show[y][x] = pieceNow;
				}
				//draws the bottom
				else if (y == show.length -1 || (present[y][x]!=0))
				{
					show[y][x] = 1;
				}
				//air
				else
				{
					show[y][x] = 0;
				}
			}
		
		}
	}
	
	
	
//methods to manipulate the arrays, public so that GameClass can reach them
	
	public void scan()
	{
		//goes through the entire board, to see if a point should be given
		//(uses helpermethods to:)
			//and a line removed
			//and the board above moved down
		for(int y = 0; y<show.length-1; y++)
		{
			int positionsFilled =0;
			
			for(int x = 0; x<show.length; x++)
			{
				if (show[y][x] == 1)
				{
					//checks if pieces at one point covers from side to side
					positionsFilled ++;
				}
			}
			if (positionsFilled==show.length && canRemove)
			{
				//gives a point
				score +=1;
				
				//removes the line
				removeLine(y);
			}
			positionsFilled =0;
		}
		//makes sure the canRemove value is reset after completed operation
		setCanRemove(false);
	}
	//removes the line(s) scored points on, and moves everything above down.
	private void removeLine(int y)
	{
		//not really necessary with an input check here, because "private" ensures that i have full control over input.
		//but it is helpful in development, to detect errors.
		if (y < 0 || y > getSize()-1)
			throw new IllegalArgumentException("The line input, is out of bounds.");
	
		for(int x = 0; x<show.length; x++)
		{
			show[y][x] =0;
			present[y][x] =0;
		}
		for(int x = 0; x<show.length; x++)
		{
			show[0][x] =0;
		}
		moveDown(y);
			
			
			
	}
	private void moveDown(int s)
	{
		//does not need check for illegalargument, because this is handled in removeLine() for this exact value.
		//and the method is private, so only I can decide the input through code.
		for(int y = s; y>0; y-=1)
		{
			for(int x = 0; x<getSize(); x++)
			{
					present[y][x] = getSpacePoint(y-1, x);
			}
		}
	}
	
	
	//I have chosen to manually code each piece, both for generating and checks,
	//because performance were worse with HashMaps and Arrays, and the code did not
	//become that much shorter (longer many places), since all the bits of each piece must
	//be programmed anyway.
	
//Checks if changes are ok.
	//instead of throwing illegalStateException everytime a move is blocked, this method ensures the move is impossible and illegal.
	//this is because such an state will happen rapidly, and many exceptions would have been thrown and handled.
	public void checkMovementR(int rotation, int piece, int stage, int piecePos)
	{
		//Basic check of the input parameters
		if (stage>getSize()-1 || stage < 2)
			throw new IllegalArgumentException("A piece should not go that far down");
		if (rotation > 4 || rotation <1 || 
			(piece !=2&& piece !=4))
			throw new IllegalArgumentException("The rotation and/or pieceNow is wrong.");
		if (piecePos>getSize()-1 || piecePos < 0)
			throw new IllegalArgumentException("A piece should not go that far out to the side.");
		
	//check if requested movement is ok for either of the pieces
		//lists of the coordinates for each point on the piece
		int[]	stx = new int[4];
		int[]	sty = new int[4];
		
		
		//this is an collectino (not arraylist since only a limited functionality is needed) makes it possible to distinguish between "not air"
				//that is a part of the board, and "not air" that is a part of the current piece.
		Collection<Integer> current;
			
		
		
		//creates the list for the two pieces --> to make it more genreal, this could have been done in the making of the piece, but
		//since i decided to only have two pieces, i put it here... This is an easy modification if the game is to be extended
		if (piece == 2)
		{
			stx[0] = -2; stx[1] = -1; stx[2] = 1; stx[3] = 0; 
			sty[0] = 0; sty[1] = 0;  sty[2] = 0; sty[3] = 0;
		}
		if (piece == 4) {
		
			stx[0] = -1; stx[1] = 0; stx[2] = 1; stx[3] = 0;
			sty[0] = 0; sty[1] = -1;  sty[2] = 0; stx[3] = 0;
		}
	
		//switch-case for each of the rotations
		//checks the rotation based on the input. Easy grid logic is umplemented: (x,y) rotated 90 around (0,0) where the pieceNow is, is transformed to (y -x)
		switch (rotation)
		{
		case 1:		
			current = new ArrayList<>();
			for(int i=0; i<4;i++)
			{
				if (piecePos+stx[i]+1 > getSize()-1 )
				{
					setCanMove(false);
					break;
				}
				current.add(sty[i]);
				
				if (present[stage + sty[i]][piecePos + stx[i]+1]!= 0 && current.contains(sty[i]))
				{
					setCanMove(false);	
					break;
				}	
			}
			break;
				
		case 2:
			current = new ArrayList<>();
			for(int i=0; i<4;i++)
			{
				if (piecePos - sty[i]+1 > getSize()-1 )
				{
					setCanMove(false);
					break;
				}
				
				current.add(stx[i]);
				
				if (present[stage + stx[i]][piecePos - sty[i]+1]!= 0 && current.contains(stx[i]))
				{
					setCanMove(false);	
					break;
				}	
			}
			break;
					
		case 3:		
			current = new ArrayList<>();
			for(int i=0; i<4;i++)
			{
				if ((piecePos-stx[i]+1 > getSize()-1))
				{
					setCanMove(false);
					break;
				}
				current.add(sty[i]);
				
				if (present[stage - sty[i]][piecePos - stx[i]+1]!= 0 && current.contains(sty[i]))
				{
					setCanMove(false);	
					break;
				}	
			}
			break;		
		case 4:		
			current = new ArrayList<>();
			for(int i=0; i<4;i++)
			{
				if ((piecePos+sty[i]+1 > getSize()-1 ))
				{
					setCanMove(false);
					break;
				}
				current.add(stx[i]);
			
				if (present[stage - stx[i]][piecePos + sty[i]+1]!= 0 && current.contains(stx[i]))
				{
					setCanMove(false);	
					break;
				}	
			}
			break;

		}
	
	
		}
	public void checkMovementL(int rotation, int piece, int stage, int piecePos)
	{
		//Basic check of the input parameters
		if (stage>getSize()-1)
			throw new IllegalArgumentException("En feil har oppstått, en brikke skal ikke kunne så langt ned");
		if (rotation > 4 || rotation <1 || 
			(piece !=2&& piece !=4))
			throw new IllegalArgumentException("En feil har oppstått, Ugydldige paramenter er blitt sendt inn til sjekk.");
		if (piecePos>getSize()-1)
			throw new IllegalArgumentException("En feil har oppstått, en brikke skal ikke kunne så ut til siden");
		
		
	//check if requested movement is ok for either of the pieces
		//lists of the coordinates for each point on the piece
		int[]	stx = new int[4];
		int[]	sty = new int[4];
		
		//this is an collectino (not arraylist since only a limited functionality is needed) makes it possible to distinguish between "not air"
		//that is a part of the board, and "not air" that is a part of the current piece.
		Collection<Integer> current;
		
		
		//creates the list for the two pieces --> to make it more genreal, this could have been done in the making of the piece, but
		//since i decided to only have two pieces, i put it here... This is an easy modification if the game is to be extended
		if (piece == 2)
		{
			stx[0] = -2; stx[1] = -1; stx[2] = 1; stx[3] = 0; 
			sty[0] = 0; sty[1] = 0;  sty[2] = 0; sty[3] = 0;
		}
		if (piece == 4) {
		
			stx[0] = -1; stx[1] = 0; stx[2] = 1; stx[3] = 0;
			sty[0] = 0; sty[1] = -1;  sty[2] = 0; stx[3] = 0;
		}
	
		//switch-case for each of the rotations
		//checks the rotation based on the input. Easy grid logic is umplemented: (x,y) rotated 90 around (0,0) where the pieceNow is, is transformed to (y -x)
		switch (rotation)
		{
		case 1:		
			current = new ArrayList<>();
			for(int i=0; i<4;i++)
			{
				if (piecePos+stx[i]-1 < 0 )
				{
					setCanMove(false);
					break;
				}
				current.add(sty[i]);
				
				if (present[stage + sty[i]][piecePos + stx[i]-1]!= 0 && current.contains(sty[i]))
				{
					setCanMove(false);	
					break;
				}	
			}
			break;
				
		case 2:
			current = new ArrayList<>();
			for(int i=0; i<4;i++)
			{
				if (piecePos - sty[i]-1 < 0 )
				{
					setCanMove(false);
					break;
				}
				
				current.add(stx[i]);
				
				if (present[stage + stx[i]][piecePos - sty[i]-1]!= 0 && current.contains(stx[i]))
				{
					setCanMove(false);	
					break;
				}	
			}
			break;
					
		case 3:		
			current = new ArrayList<>();
			
			for(int i=0; i<4;i++)
			{
				if ((piecePos-stx[i]-1 < 0 ))
				{
					setCanMove(false);
					break;
				}
				
				current.add(sty[i]);
				if (present[stage - sty[i]][piecePos - stx[i]-1]!= 0 && current.contains(sty[i]))
				{
					setCanMove(false);	
					break;
				}	
			}
			break;		
		case 4:		
			current = new ArrayList<>();
			for(int i=0; i<4;i++)
			{
				if ((piecePos+sty[i]-1 < 0 ))
				{
					setCanMove(false);
					break;
				}
				current.add(stx[i]);
			
				if (present[stage - stx[i]][piecePos + sty[i]-1]!= 0 && current.contains(stx[i]))
				{
					setCanMove(false);	
					break;
				}	
			}
			break;

		}
		
		
	}
	public void checkRotation(int rotation, int piece, int stage, int piecePos)
	{
		//basic check of input parameters
		if (stage>getSize()-1)
			throw new IllegalArgumentException("En feil har oppstått, en brikke skal ikke kunne så langt ned");
		if (rotation > 4 || rotation <1 || 
			(piece !=2&& piece !=4))
			throw new IllegalArgumentException("En feil har oppstått, Ugydldige paramenter er blitt sendt inn til sjekk.");
		if (piecePos>getSize()-1)
			throw new IllegalArgumentException("En feil har oppstått, en brikke skal ikke kunne så ut til siden");
		
		//Want to check the next rotation, not the current.
		Integer rotate = rotation +1;
		if(rotate > 4)
			rotate = 1;
		
//check of requested rotation for each of the pieces
		//lists of the coordinates for each point on the piece
		int[]	stx = new int[3];
		int[]	sty = new int[3];
		
	
		//creates the list for the two pieces --> to make it more genreal, this could have been done in the making of the piece, but
		//since i decided to only have two pieces, i put it here... This is an easy modification if the game is to be extended
		if (piece == 2)
		{
			stx[0] = -2; stx[1] = -1; stx[2] = 1;
			sty[0] = 0; sty[1] = 0;  sty[2] = 0; 
		}
		if (piece == 4) {
		
			stx[0] = -1; stx[1] = 0; stx[2] = 1;
			sty[0] = 0; sty[1] = -1;  sty[2] = 0; 
		}
		
		//checks the rotation based on the input. Easy grid logic is umplemented: (x,y) rotated 90 around (0,0) where the pieceNow is, is transformed to (y -x)
			
		switch (rotate)
		{
		case 1:	
			for (int v =0; v<3; v++)
			{
				if (piecePos + stx[v]< 0 || piecePos + stx[v] > getSize() -1 || 
					stage + sty[v]< 0 || stage + sty[v] > getSize()-1)
					
				{
					setCanRotate(false);
					break;
				}
				if(present[stage + sty[v]][piecePos+stx[v]] != 0)
				{
					setCanRotate(false);
					break;
				}
					
			}
			break;
			
		case 2:
			for (int v =0; v<3; v++)
			{
				if (piecePos + sty[v]< 0 || piecePos + sty[v] > getSize() -1 ||
					stage + stx[v] < 0 || stage + stx[v] > getSize()-1)
				{
					setCanRotate(false);
					break;
				}
				if(present[stage + stx[v]][piecePos - sty[v]] != 0)
				{
					setCanRotate(false);
					break;
				}
					
			}
			break;
		
		case 3:
			for (int v =0; v<3; v++)
			{
				if (piecePos - stx[v]< 0 || piecePos - stx[v] > getSize() -1 || 
						stage - sty[v]< 0 || stage - sty[v] > getSize()-1)
				{
					setCanRotate(false);
					break;
				}
				if(present[stage - sty[v]][piecePos - stx[v]] != 0)
				{
					setCanRotate(false);
					break;
				}
					
			}
			break;	
			
		case 4:				
			for (int v =0; v<3; v++)
			{
				if (piecePos - sty[v]< 0 || piecePos - sty[v] > getSize() -1 ||
					stage - stx[v]< 0 || stage - stx[v] > getSize()-1)
				{
					setCanRotate(false);
					break;
				}
				if(present[stage - stx[v]][piecePos + sty[v]] != 0)
				{
					setCanRotate(false);
					break;
				}			
			}
			break;
		}		
	}
		
	public ArrayList<Integer> defaultArray()
	{
		ArrayList<Integer> defArr = new ArrayList<>();	

		for(int y = 0; y<this.size; y++)
		{
			for(int x = 0; x<this.size; x++)
			{
				//bottom 
				if(y== size-1) 
				{
					defArr.add(1);	
				}
				//air
				else
				{
					defArr.add(0);
				}
			}
		}
		return defArr;
	}
	
	
	// pieceGeneration
	// input as collection, because index is not needed
	public void makeStick(int rotation, int piecePos, int stage, int pieceNow, Collection<Integer> bottomPieces, ArrayList<Integer> sidePieces)
		{
			//basic check of the different input parameters
			//the check for the piece's position is further regulated in checkMovement... It will make sure a piece is not drawn in an illegal spot.
			if (stage>getSize()-1 || stage < 2)
				throw new IllegalArgumentException("A piece should not go that far down");
			if (rotation > 4 || rotation <1 || 
				(pieceNow !=2&& pieceNow !=4))
				throw new IllegalArgumentException("The rotation and/or pieceNow is wrong.");
			if (piecePos>getSize()-1 || piecePos < 0)
				throw new IllegalArgumentException("A piece should not go that far out to the side.");
			if (bottomPieces == null)
				throw new IllegalArgumentException("BottomPieces cannot be null");
			if (sidePieces == null)
				throw new IllegalArgumentException("BottomPieces cannot be null");
			
			
			//generates the piece based on the rotation, and ads values to help-Arrays.
			if(rotation == 1) 
			{
				bottomPieces.clear();
				sidePieces.clear();
				
				// find edge
				sidePieces.add(piecePos-2);
				sidePieces.add(piecePos+1);
				
				// find whats under
				bottomPieces.add(show[stage+1][piecePos-2]);
				bottomPieces.add(show[stage+1][piecePos-1]);
				bottomPieces.add(show[stage+1][piecePos]);
				bottomPieces.add(show[stage+1][piecePos+1]);
				
				
				// construct the entire piece
				show[stage][piecePos-2] = pieceNow;
				show[stage][piecePos-1] = pieceNow;
				show[stage][piecePos+1] = pieceNow;
			}
			if(rotation == 2) 
			{
				bottomPieces.clear();
				sidePieces.clear();
				
				//find edge
				sidePieces.add(piecePos);
				sidePieces.add(piecePos);
				
				
				//find bottom
				bottomPieces.add(show[stage+2][piecePos]);
				
				// constuct the entire piece
				show[stage-2][piecePos] = pieceNow;
				show[stage-1][piecePos] = pieceNow;
				show[stage+1][piecePos] = pieceNow;
			}
			if(rotation == 3) 
			{
				bottomPieces.clear();
				sidePieces.clear();
				
				
				
				// find edge
				sidePieces.add(piecePos+2);
				sidePieces.add(piecePos+1);
				
				//find bottom
				bottomPieces.add(show[stage+1][piecePos+2]);
				bottomPieces.add(show[stage+1][piecePos-1]);
				bottomPieces.add(show[stage+1][piecePos]);
				bottomPieces.add(show[stage+1][piecePos+1]);
				
				// construct the entire piece
				show[stage][piecePos+2] = pieceNow;
				show[stage][piecePos-1] = pieceNow;
				show[stage][piecePos+1] = pieceNow;
			}
			if(rotation == 4) 
			{
				bottomPieces.clear();
				sidePieces.clear();
				
				//find edge
				sidePieces.add(piecePos);
				sidePieces.add(piecePos);
				
				//find bottom
				bottomPieces.add(show[stage+2][piecePos]);
				
				// constuct the entire piece
				show[stage-2][piecePos] = pieceNow;
				show[stage-1][piecePos] = pieceNow;
				show[stage+1][piecePos] = pieceNow;
			}
		}
	public void makeTee(int rotation, int piecePos, int stage, int pieceNow, Collection<Integer> bottomPieces, ArrayList<Integer> sidePieces)
		{
			//basic check of input parameters
			//the check for the piece's position is further regulated in checkMovement... It will make sure a piece is not drawn in an illegal spot.
			if (stage>getSize()-1 || stage < 2)
				throw new IllegalArgumentException("A piece should not go that far down");
			if (rotation > 4 || rotation <1 || 
				(pieceNow !=2&& pieceNow !=4))
				throw new IllegalArgumentException("The rotation and/or pieceNow is wrong.");
			if (piecePos>getSize()-1 || piecePos < 0)
				throw new IllegalArgumentException("A piece should not go that far out to the side.");
			if (bottomPieces == null)
				throw new IllegalArgumentException("BottomPieces cannot be null");
			if (sidePieces == null)
				throw new IllegalArgumentException("BottomPieces cannot be null");
			
			//generates the piece based on the rotation, and ads values to help-Arrays.
			if(rotation == 1) 
			{
				bottomPieces.clear();
				sidePieces.clear();
				
				// find edge
				sidePieces.add(piecePos-1);
				sidePieces.add(piecePos+1);
				
				// find whats under
				bottomPieces.add(show[stage+1][piecePos+1]);
				bottomPieces.add(show[stage+1][piecePos-1]);
				bottomPieces.add(show[stage+1][piecePos]);
				
				
				// construct the entire piece
				show[stage][piecePos-1] = pieceNow;
				show[stage][piecePos+1] = pieceNow;
				show[stage-1][piecePos] = pieceNow;
				
			}
			if(rotation == 2) 
			{
				bottomPieces.clear();
				sidePieces.clear();
				
				//find edge
				sidePieces.add(piecePos+1);
				sidePieces.add(piecePos);
				
				//find bottom
				bottomPieces.add(show[stage+2][piecePos]);
				bottomPieces.add(show[stage+1][piecePos+1]);
				
				// constuct the entire piece
				show[stage][piecePos+1] = pieceNow;
				show[stage-1][piecePos] = pieceNow;
				show[stage+1][piecePos] = pieceNow;
			}
			if(rotation == 3) 
			{
				bottomPieces.add(show[stage+2][piecePos]);
				bottomPieces.add(show[stage+1][piecePos-1]);
				bottomPieces.add(show[stage+1][piecePos+1]);
				
				// find edge
				sidePieces.add(piecePos+1);
				sidePieces.add(piecePos-1);
				
				
				// construct the entire piece
				show[stage+1][piecePos] = pieceNow;
				show[stage][piecePos-1] = pieceNow;
				show[stage][piecePos+1] = pieceNow;
			}
			if(rotation == 4) 
			{
				bottomPieces.clear();
				sidePieces.clear();
				
				// find edge
				sidePieces.add(piecePos-1);
				sidePieces.add(piecePos);
				
				// find whats under
				bottomPieces.add(show[stage+1][piecePos-1]);
				bottomPieces.add(show[stage+2][piecePos]);
				
				
				// construct the entire piece
				show[stage+1][piecePos] = pieceNow;
				show[stage-1][piecePos] = pieceNow;
				show[stage][piecePos-1] = pieceNow;
				
				
			}
		}
	
	
	
	//all getters
	public int[][] getShow() {
		return show;
	}
	public int[][] getPresent() {
		return present;
	}
	public int getSize()
	{
		return size;
	}
	public int getSpacePoint(int y, int x)
	{
		return present[y][x];
	}
	public int[] getSpaceLine(int y)
	{
		return present[y];
	}
	public boolean isCanRemove() {
		return canRemove;
	}
	public boolean isCanRotate() {
		return canRotate;
	}
	public int getScore() {
		return score;
	}
	public boolean isCanMove() {
		return canMove;
	}
	
	
	// all setters
	public void setShow(int[][] show) {
		if (show == null)
				throw new IllegalArgumentException("The value for show is equal to null");
		this.show = show;
	}
	public void setPresent(int[][] present) {
		if (present == null)
			throw new IllegalArgumentException("The value for show is equal to null");
		this.present = present;
	}
	public void setCanRotate(boolean set){
		//does not need check - boolean is either false or true
		this.canRotate = set;
	}
	public void setCanRemove(boolean canRemove) {
		//does not need check - boolean is either false or true
		this.canRemove = canRemove;
	}
	public void setCanMove(boolean canMove)
	{
		//does not need check - boolean is either false or true
		this.canMove = canMove;
	}
	public void setSize(int size) {
		if (size < 10)
			throw new IllegalArgumentException("The size of the game is too small. It has to be at least 10");
		if (size > 20)
			throw new IllegalArgumentException("The size of the game is too big. Limit yourself to a max-value of 20");
		this.size = size;
	}
	public void setScore(int score) {
		if (score < 0)
			throw new IllegalArgumentException("The score cannot be negative.");
		this.score = score;
	}
	
	
	//used for testing
	@Override
	public String toString() {
		String print = "";
		
		for(int y = 0; y<size; y++)
		{
			for(int x = 0; x<size; x++)
			{
				print += show[y][x]+" ";
			}
			print+= "\n";
		}
		return print;
	}
	public static void main(String[] args)
	{
		Board b = new Board(10);
		
		System.out.println(b);
	}
}


	