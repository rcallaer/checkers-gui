import java.util.ArrayList;

public class Board 
{
	private BoardPosition [] boardSquares = new BoardPosition[32];
	private char [] colPositions = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
	private int [] rowPositions = {8, 7, 6, 5, 4, 3, 2, 1};
	private int redCapturedPieces, blackCapturedPieces;
	private ArrayList<Move> movesMade;
	private PieceType newKing = PieceType.NONE;
	private PieceType newKing2 = PieceType.NONE;
	private int kingStage = 0;
	private boolean flipped = false;
	private boolean redCanMove = true, blackCanMove = true;
	
	//constructor
	public Board()
	{
		initializeBoard();
	}
	
	//create the initial checkers board, with 12 black and 12 red pieces in starting position
	public void initializeBoard()
	{
		int boardIndex = 0;
		
		//loop through all 64 squares
		for (int i = 0; i < 8; i++)
		{	
			for (int j = 0; j < 8; j++)
			{		
				char boardCol = colPositions[j];
				int boardRow = rowPositions[i];
				
				//skip white squares
				if((i % 2 == 0 && j % 2 == 0)  || (i % 2 == 1 && j % 2 == 1))
					continue;//System.out.print("W");
				//on each black square create a new board position
				else
				{
					if(i >= 0 && i < 3)
						boardSquares[boardIndex] = new BoardPosition(boardCol, boardRow, PieceType.BLACK);
					else if(i >= 3 && i < 5)
						boardSquares[boardIndex] = new BoardPosition(boardCol, boardRow, PieceType.NONE);
					else
						boardSquares[boardIndex] = new BoardPosition(boardCol, boardRow, PieceType.RED);
					//System.out.print("B");
					boardIndex++;
				}
			}
			//System.out.println();
		}
		
		//initialize other components
		redCapturedPieces = blackCapturedPieces = 0;
		movesMade = new ArrayList<Move>();
	}
	
	//return the board square as a BoardPosition given an index into the array of all board squares
	public BoardPosition getPosition(int index)
	{
		return boardSquares[index];
	}
	
	//return the index to the boardSquares array given a particular BoardPosition
	public int correspondingIndex(BoardPosition pos)
	{
		int index = 0;
		
		for (int i = 0; i < 32; i++)
		{
			index = i;
			if(pos.getCol() == boardSquares[i].getCol() && pos.getRow() == boardSquares[i].getRow())
				break;
		}
		
		return index;
	}
	
	//return the index to the boardSquares array given the known column and row of the board
	public int correspondingIndex(char col, int row)
	{
		int index = 0;
		
		for (int i = 0; i < 32; i++)
		{
			index = i;
			if(col == boardSquares[i].getCol() && row == boardSquares[i].getRow())
				break;
		}
		
		return index;
	}
	
	//move a piece from one board square to another, given the move, and print the board afterwards
	public void movePiece(Move move)
	{
		//break the move into start and end board locations
		BoardPosition startLocation = move.getStart();
		BoardPosition endLocation = move.getEnd();
		
		//get the indexes into the boardSquares array
		int index = correspondingIndex(startLocation);
		int index2 = correspondingIndex(endLocation);
		
		//get the actual board squares in the array
		BoardPosition boardSquareStart = getPosition(index);
		BoardPosition boardSquareEnd = getPosition(index2);
		boolean b1 = boardSquareEnd.getRow() == rowPositions[0];
		boolean b2 = boardSquareEnd.getRow() == rowPositions[7];
		boolean b3 = !boardSquareStart.pieceIsKing();
		
		//crown a king if needed
		if((boardSquareEnd.getRow() == rowPositions[0] || boardSquareEnd.getRow() == rowPositions[7]) && !boardSquareStart.pieceIsKing())
		{
			setNewKing(boardSquareStart.correspondingKing());
			//setNewKing2(boardSquareStart.correspondingKing());
			///setKingStage(1);
			boardSquareEnd.setPiece(boardSquareStart.correspondingKing());
		}
		//otherwise set the piece on the board
		else
			boardSquareEnd.setPiece(boardSquareStart.getPiece());
		boardSquareStart.removePiece();
		
		//add the move to the total list of moves
		addPerformedMove(move);
		
		//printBoard();
		
		//displayMovesList();
	}
	
	//make a jump move
	public void jumpMovePiece(Move move)
	{
		//get which direction the jump is going
		MoveOptions direction = jumpMoveOrientation(move); 
		char a = move.getStart().getCol();
		int b = move.getStart().getRow();
		
		//grab the index and board position
		int index = correspondingIndex(a, b);
		BoardPosition p = getPosition(index);

		/*if(move.getStart().getRow() != rowPositions[7] && move.getEnd().getRow() == rowPositions[7] && p.pieceIsRed() && !p.pieceIsKing())
			setNewKing(PieceType.REDKING);
		else if(move.getStart().getRow() != rowPositions[0] && move.getEnd().getRow() == rowPositions[0] && !p.pieceIsRed() && !p.pieceIsKing())
			setNewKing(PieceType.BLACKKING);*/
			
		//add captured pieces as needed
		if(p.pieceIsRed())
			redCapturedPieces++;
		else
			blackCapturedPieces++;
		
		//set the column and row of the piece to remove depending on the direction
		switch(direction)
		{
			case UL: a = Character.toChars(a - 1)[0]; b += 1; break;
			case UR: a = Character.toChars(a + 1)[0]; b += 1; break;
			case DL: a = Character.toChars(a - 1)[0]; b -= 1; break;
			case DR: a = Character.toChars(a + 1)[0]; b -= 1; break;
		}
		
		//grab the position again using the new col and row,, and remove the piece
		index = correspondingIndex(a, b);
		p = getPosition(index);
		p.removePiece();
		
		//move the jumping piece
		movePiece(move);
	}
	
	//return the direction for a jump move
	public MoveOptions jumpMoveOrientation(Move move)
	{
		MoveOptions mo = MoveOptions.UL;
		
		if(move.getStart().getCol() < move.getEnd().getCol() && move.getStart().getRow() > move.getEnd().getRow())
			mo = MoveOptions.DR;
		else if(move.getStart().getCol() < move.getEnd().getCol() && move.getStart().getRow() < move.getEnd().getRow())
			mo = MoveOptions.UR;
		else if(move.getStart().getCol() > move.getEnd().getCol() && move.getStart().getRow() > move.getEnd().getRow())
			mo = MoveOptions.DL;
		else
			mo = MoveOptions.UL;
		
		return mo;
	}
	
	//add a move to the running total list of moves
	public void addPerformedMove(Move move)
	{
		movesMade.add(move);
	}
	
	public void updateBoard()
	{
		printBoard();	
	}
	
	//display the board along with other information
	public void printBoard()
	{
		//print captured pieces for each side
		displayCapturedPieces();
		
		int boardIndex = 0;

		if(flipped)
		{
			boardIndex = 31;
			//loop through 64 squares, alternating between white and "black"
			for(int i = 0; i < 8; i++)
			{
				System.out.println(" ---------------------------------------------------------------------------------------------------------");
				System.out.println(" |            |            |            |            |            |            |            |            |");
				//System.out.print(rowPositions[i]);
				for(int j = 0; j < 8; j++)
				{
					//print a blank square only on white
					if((i % 2 == 0 && j % 2 == 0)  || (i % 2 == 1 && j % 2 == 1))
					{
						//if((i % 2 != 0 && boardIndex % 4 != 0) || i % 2 == 0)
							//System.out.print(" |");
						//System.out.print("           |");
						System.out.print(" |           ");
						if(j == 7)
							System.out.print(" |");
						
					}
					//print any pieces on black squares
					else
					{
						//if(boardIndex % 4 == 0 && i % 2 != 0)
							//System.out.print("|");
						if(boardSquares[boardIndex].pieceIsKing())
							System.out.printf(" | %5s ",boardSquares[boardIndex]);
						else
							System.out.printf(" |   %5s   ",boardSquares[boardIndex]);//,%3s boardIndex+1);//boardSquares[boardIndex].getColAndRow());
						if(j == 7)
							System.out.print(" |");
						//if(boardIndex % 4 == 3)
							//System.out.print("|");
						boardIndex--;
					}
				}
				System.out.println();
				
				if(i%2 == 0)
					System.out.printf(" |            |     %2s     |            |     %2s     |            |     %2s     |            |     %2s     |\n", boardIndex+5,boardIndex+4, boardIndex+3,boardIndex+2);
				else
					System.out.printf(" |    %2s      |            |     %2s     |            |     %2s     |            |     %2s     |            |\n", boardIndex+5,boardIndex+4, boardIndex+3,boardIndex+2);
			}
			System.out.println(" ---------------------------------------------------------------------------------------------------------");
		}
		else
		{
			//loop through 64 squares, alternating between white and "black"
			for(int i = 0; i < 8; i++)
			{
				System.out.println(" ---------------------------------------------------------------------------------------------------------");
				System.out.println(" |            |            |            |            |            |            |            |            |");
				//System.out.print(rowPositions[i]);
				if(i%2==0)
					System.out.print(" ");
				for(int j = 0; j < 8; j++)
				{
					//print a blank square only on white
					if((i % 2 == 0 && j % 2 == 0)  || (i % 2 == 1 && j % 2 == 1))
					{
						if((i % 2 != 0 && boardIndex % 4 != 0) || i % 2 == 0)
							System.out.print("|");
						System.out.print("            |");
					}
					//print any pieces on black squares
					else
					{
						if(boardIndex % 4 == 0 && i % 2 != 0)
							System.out.print(" |");
						if(boardSquares[boardIndex].pieceIsKing())
							System.out.printf("   %5s",boardSquares[boardIndex]);
						else
							System.out.printf("   %5s    ",boardSquares[boardIndex]);//, boardIndex+1);//boardSquares[boardIndex].getColAndRow());
						
						if(boardIndex % 4 == 3)
							System.out.print("|");
						boardIndex++;
					}
					
				}
				System.out.println();
				
				if(i%2 == 0)
					System.out.printf(" |            |     %2s     |            |     %2s     |            |     %2s     |            |     %2s     |\n", boardIndex-3,boardIndex-2, boardIndex-1,boardIndex);
				else
					System.out.printf(" |    %2s      |            |     %2s     |            |     %2s     |            |     %2s     |            |\n", boardIndex-3,boardIndex-2, boardIndex-1,boardIndex);
				//System.out.println(" |            |            |            |            |            |            |            |            |");
			}
			System.out.println(" ---------------------------------------------------------------------------------------------------------");
			//System.out.printf(" %8s%11s%12s%13s%12s%12s%12s%12s", "A", "B", "C", "D", "E", "F", "G", "H");
			System.out.println();
		}
		//print the list of moves as well
		//displayMovesList();
		System.out.println();
	}
	
	//display the board along with other information
	public String stringBoard()
	{
		//print captured pieces for each side
		//displayCapturedPieces();
		String retString = "";
		
		int boardIndex = 0;

		if(flipped)
		{
			boardIndex = 31;
			//loop through 64 squares, alternating between white and "black"
			for(int i = 0; i < 8; i++)
			{
				//System.out.println(" ---------------------------------------------------------------------------------------------------------");
				retString += " ---------------------------------------------------------------------------------------------------------\n";
				//System.out.println(" |            |            |            |            |            |            |            |            |");
				//retString += " |            |            |            |            |            |            |            |            |\n";
				//System.out.print(rowPositions[i]);
				for(int j = 0; j < 8; j++)
				{
					//print a blank square only on white
					if((i % 2 == 0 && j % 2 == 0)  || (i % 2 == 1 && j % 2 == 1))
					{
						//if((i % 2 != 0 && boardIndex % 4 != 0) || i % 2 == 0)
							//System.out.print(" |");
						//System.out.print("           |");
						//System.out.print(" |           ");
						retString += " |           ";
						if(j == 7)
							retString+= " |";
							//System.out.print(" |");
						
					}
					//print any pieces on black squares
					else
					{
						//if(boardIndex % 4 == 0 && i % 2 != 0)
							//System.out.print("|");
						if(boardSquares[boardIndex].pieceIsKing())
							//System.out.printf(" | %5s ",boardSquares[boardIndex]);
							retString += String.format(" | %5s ",boardSquares[boardIndex]);
						else
							retString += String.format(" |   %5s   ",boardSquares[boardIndex]);
							//System.out.printf(" |   %5s   ",boardSquares[boardIndex]);//,%3s boardIndex+1);//boardSquares[boardIndex].getColAndRow());
						if(j == 7)
							//System.out.print(" |");
							retString += " |";
						//if(boardIndex % 4 == 3)
							//System.out.print("|");
						boardIndex--;
					}
				}
				//System.out.println();
				retString += "\n";
				
				if(i%2 == 0)
					//System.out.printf(" |            |     %2s     |            |     %2s     |            |     %2s     |            |     %2s     |\n", boardIndex+5,boardIndex+4, boardIndex+3,boardIndex+2);
					retString += String.format(" |            |     %2s     |            |     %2s     |            |     %2s     |            |     %2s     |\n", boardIndex+5,boardIndex+4, boardIndex+3,boardIndex+2);
				else
					//System.out.printf(" |    %2s      |            |     %2s     |            |     %2s     |            |     %2s     |            |\n", boardIndex+5,boardIndex+4, boardIndex+3,boardIndex+2);
					retString += String.format(" |    %2s      |            |     %2s     |            |     %2s     |            |     %2s     |            |\n", boardIndex+5,boardIndex+4, boardIndex+3,boardIndex+2);
			}
			//System.out.println(" ---------------------------------------------------------------------------------------------------------");
			retString += " ---------------------------------------------------------------------------------------------------------\n";
		}
		else
		{
			//loop through 64 squares, alternating between white and "black"
			for(int i = 0; i < 8; i++)
			{
				retString += " ---------------------------------------------------------------------------------------------------------\n";
				//System.out.println(" |            |            |            |            |            |            |            |            |");
				//retString += " |            |            |            |            |            |            |            |            |\n";
				//System.out.print(rowPositions[i]);
				if(i%2==0)
					retString += " ";
				for(int j = 0; j < 8; j++)
				{
					//print a blank square only on white
					if((i % 2 == 0 && j % 2 == 0)  || (i % 2 == 1 && j % 2 == 1))
					{
						if((i % 2 != 0 && boardIndex % 4 != 0) || i % 2 == 0)
							retString += "|";
						retString += "            |";
					}
					//print any pieces on black squares
					else
					{
						if(boardIndex % 4 == 0 && i % 2 != 0)
							retString += " |";
						if(boardSquares[boardIndex].pieceIsKing())
							retString += String.format("   %5s",boardSquares[boardIndex]);
						else
							retString += String.format("   %5s    ",boardSquares[boardIndex]);//, boardIndex+1);//boardSquares[boardIndex].getColAndRow());
						
						if(boardIndex % 4 == 3)
							retString += "|";
						boardIndex++;
					}
					
				}
				retString += "\n";
				
				if(i%2 == 0)
					retString += String.format(" |            |     %2s     |            |     %2s     |            |     %2s     |            |     %2s     |\n", boardIndex-3,boardIndex-2, boardIndex-1,boardIndex);
				else
					retString += String.format(" |    %2s      |            |     %2s     |            |     %2s     |            |     %2s     |            |\n", boardIndex-3,boardIndex-2, boardIndex-1,boardIndex);
				//System.out.println(" |            |            |            |            |            |            |            |            |");
			}
			retString += " ---------------------------------------------------------------------------------------------------------\n";
			//System.out.printf(" %8s%11s%12s%13s%12s%12s%12s%12s", "A", "B", "C", "D", "E", "F", "G", "H");
			retString += "\n";
		}
		//print the list of moves as well
		//displayMovesList();
		retString += "\n";
		
		return retString;
	}
	
	//flip the board for player 1 to be oriented up
	public void flipBoard()
	{
		flipped = true;
	}
	
	public void setSquares(BoardPosition [] squares)
	{
		boardSquares = squares;
	}
	
	public BoardPosition [] getSquares()
	{
		BoardPosition bps[] = new BoardPosition[boardSquares.length];
		for(int i = 0; i<boardSquares.length;i++)
		{
			bps[i] = new BoardPosition();
			bps[i].setRow(boardSquares[i].getRow());
			bps[i].setCol(boardSquares[i].getCol());
			bps[i].setPiece(boardSquares[i].getPiece());
		}
		return bps;
	}
	
	public void setMoves(ArrayList<Move> moves)
	{
		movesMade = moves;
	}
	
	public ArrayList<Move> getMoves()
	{
		ArrayList<Move> mvs = new ArrayList<Move>();
		
		for(int i = 0; i< movesMade.size();i++)
		{
			Move m = new Move(movesMade.get(i).getStart(), movesMade.get(i).getEnd());
			if(movesMade.get(i).isJump())
				m.setAsJumpMove();	
			mvs.add(m);
		}
		return mvs;
	}
	
	public int getRedCapturedPieces() {
		return redCapturedPieces;
	}

	public void setRedCapturedPieces(int redCapturedPieces) {
		this.redCapturedPieces = redCapturedPieces;
	}

	public int getBlackCapturedPieces() {
		return blackCapturedPieces;
	}

	public void setBlackCapturedPieces(int blackCapturedPieces) {
		this.blackCapturedPieces = blackCapturedPieces;
	}

	public boolean isFlipped() {
		return flipped;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}

	public boolean redCanMove() {
		return redCanMove;
	}

	public void setRedCanMove(boolean redCanMove) {
		this.redCanMove = redCanMove;
	}

	public boolean blackCanMove() {
		return blackCanMove;
	}

	public void setBlackCanMove(boolean blackCanMove) {
		this.blackCanMove = blackCanMove;
	}
	
	public PieceType getNewKing() {
		return newKing;
	}

	//print any captured pieces for each side
	public void displayCapturedPieces()
	{
		System.out.println("Red captured pieces: " + redCapturedPieces);
		System.out.println("Black captured pieces: " + blackCapturedPieces);
	}
	
	//print all moves made up to this point
	public void displayMovesList()
	{
		for(Move move : movesMade)
		{
			//System.out.println(move);
			System.out.println(move + " ");
		}
	}
	
	//determine if the current board is an ending condition
	public boolean endBoard()
	{
		//return draw;
		return blackCapturedPieces == 12 || redCapturedPieces == 12;
	}
	
	public void setKingStage(int stage)
	{
		kingStage = stage;
	}
	
	public int getKingStage()
	{
		return kingStage;
	}
	
	public void setNewKing(PieceType piece)
	{
		newKing = piece;

	}
	public void setNewKing2(PieceType piece)
	{
		newKing2 = piece;

	}
	
	public void setRedMoveless()
	{
		redCanMove = false;
	}
	
	public void setBlackMoveless()
	{
		blackCanMove = false;
	}
	
	public boolean newKingObtained()
	{
		return newKing == PieceType.BLACKKING || newKing == PieceType.REDKING;
	}
	public boolean newKing2Obtained()
	{
		return newKing2 == PieceType.BLACKKING || newKing2 == PieceType.REDKING;
	}
	
	public int getRedKings()
	{
		int count = 0;
		
		for(BoardPosition square : boardSquares)
		{
			if(square.pieceIsKing() && square.pieceIsRed())
				count++;
		}
		
		return count;
	}
	
	public int getBlackKings()
	{
		int count = 0;
		
		for(BoardPosition square : boardSquares)
		{
			if(square.pieceIsKing() && !square.pieceIsRed())
				count++;
		}
		
		return count;
	}
	
	public boolean redWins()
	{
		return redCapturedPieces == 12 || !blackCanMove;
	}
	
	public boolean blackWins()
	{
		return blackCapturedPieces == 12 || !redCanMove;
	}
	
	//return the winning side as a string
	public String winner()
	{
		if(redCapturedPieces == 12 || !blackCanMove)
			return "Red Wins";
		else if(blackCapturedPieces == 12 || !redCanMove)
			return "Black Wins";
		else
			return "Game is a Draw";
	}
	
	//finds and returns all moves available for a given board position, assuming player orientation up = true
	public ArrayList<Move> movesAvailable(Board board, BoardPosition pieceLocation, boolean orientedUp, boolean king)
	{
		ArrayList<Move> moves = new ArrayList<Move>(); //return variable
		
		//if there's no piece, return a blank list
		if(!pieceLocation.hasPiece())
		{
			return moves;
		}
		
		//check for moves if the location has a regular piece
		if(pieceLocation.getPiece() == PieceType.RED || pieceLocation.getPiece() == PieceType.BLACK)
		{
			boolean inboundsLeft = true, inboundsRight = true, inboundsUp = true, inboundsDown = true;
			boolean inbounds = true;
			char moveLeftCol, moveRightCol;
			int moveLeftRow, moveRightRow;
			
			char currentCol = pieceLocation.getCol();
			int currentRow = pieceLocation.getRow();
			
			moveLeftCol = Character.toChars(currentCol - 1)[0];
			moveRightCol = Character.toChars(currentCol + 1)[0];
			
			//move up a row
			if(orientedUp)
			{	
				moveLeftRow = currentRow + 1;
				moveRightRow = currentRow + 1;
			}
			//move down a row
			else
			{
				moveLeftRow = currentRow - 1;
				moveRightRow = currentRow - 1;
			}
			
			//ensure that the move is in bounds
			if(moveLeftRow > 8 || moveRightRow > 8)
				inboundsUp = false;
			if(moveLeftRow < 1 || moveRightRow < 1)
				inboundsDown = false;
			if(moveLeftCol < 'a' || moveRightCol < 'a')
				inboundsLeft = false;
			if(moveRightCol > 'h' ||  moveLeftCol > 'h')
				inboundsRight = false;
			
			BoardPosition moveLeftOne = new BoardPosition(moveLeftCol, moveLeftRow, PieceType.NONE);
			BoardPosition moveRightOne = new BoardPosition(moveRightCol, moveRightRow, PieceType.NONE);
				
			int moveLeftIndex = board.correspondingIndex(moveLeftOne);
			BoardPosition moveLeftBoardSquare = board.getPosition(moveLeftIndex);
				
			int moveRightIndex = board.correspondingIndex(moveRightOne);
			BoardPosition moveRightBoardSquare = board.getPosition(moveRightIndex);
				
			inbounds = ((orientedUp && inboundsUp) || (!orientedUp && inboundsDown));
			inbounds = inboundsLeft && ((orientedUp && inboundsUp) || (!orientedUp && inboundsDown));
			//as long as there's no piece at the new location, add the move
			if(!moveLeftBoardSquare.hasPiece() && inbounds)
			{
				moves.add(new Move(pieceLocation, moveLeftBoardSquare));
			}
			//check for jump move
			else if(moveLeftBoardSquare.hasPiece() && moveLeftBoardSquare.getPiece() != pieceLocation.getPiece() && 
					moveLeftBoardSquare.getPiece() != pieceLocation.correspondingKing() && moveLeftBoardSquare.correspondingKing() != pieceLocation.getPiece())
			{
				inboundsLeft = inboundsUp = inboundsDown = true;
				moveLeftCol = Character.toChars(currentCol - 2)[0];
				
				//move up 2 rows
				if(orientedUp)
				{	
					moveLeftRow = currentRow + 2;
				}
				//move down 2 rows
				else
				{
					moveLeftRow = currentRow - 2;
				}
				
				BoardPosition moveLeftTwo = new BoardPosition(moveLeftCol, moveLeftRow, PieceType.NONE);
					
				if((!orientedUp && moveLeftRow < 1) || (orientedUp && moveLeftRow > 8) || moveLeftCol < 'a')
					inbounds = false;
				
				moveLeftIndex = board.correspondingIndex(moveLeftTwo);
				BoardPosition moveLeftTwoBoardSquare = board.getPosition(moveLeftIndex);
				
				if(!moveLeftTwoBoardSquare.hasPiece() && inbounds)
				{
					Move jump = new Move(pieceLocation, moveLeftTwoBoardSquare);
					jump.setAsJumpMove();
					moves.add(jump);
				}
			}
				
			inbounds = ((orientedUp && inboundsUp) || (!orientedUp && inboundsDown));
			inbounds = inboundsRight && ((orientedUp && inboundsUp) || (!orientedUp && inboundsDown));
			//as long as there's no piece at the new location, add the move
			if(!moveRightBoardSquare.hasPiece() && inbounds)
			{
				moves.add(new Move(pieceLocation, moveRightBoardSquare));
			}
			//check for jump move
			else if(moveRightBoardSquare.hasPiece() && moveRightBoardSquare.getPiece() != pieceLocation.getPiece() && 
					moveRightBoardSquare.getPiece() != pieceLocation.correspondingKing() && moveRightBoardSquare.correspondingKing() != pieceLocation.getPiece())
			{
				moveRightCol = Character.toChars(currentCol + 2)[0];
				inbounds = true;
				
				//move up 2 rows
				if(orientedUp)
				{	
					moveRightRow = currentRow + 2;
				}
				//move down 2 rows
				else
				{
					moveRightRow = currentRow - 2;
				}

				BoardPosition moveRightTwo = new BoardPosition(moveRightCol, moveRightRow, PieceType.NONE);
					
				if((!orientedUp && moveRightRow < 1) || (orientedUp && moveRightRow > 8) || moveRightCol > 'h')
					inbounds = false;
				
				moveRightIndex = board.correspondingIndex(moveRightTwo);
				BoardPosition moveRightTwoBoardSquare = board.getPosition(moveRightIndex);

				if(!moveRightTwoBoardSquare.hasPiece() && inbounds)
				{
					Move jump = new Move(pieceLocation, moveRightTwoBoardSquare);
					jump.setAsJumpMove();
					moves.add(jump);
				}
			}
		}
		//recursively check for moves if the current piece is a king
		else if(pieceLocation.getPiece() == PieceType.REDKING || pieceLocation.getPiece() == PieceType.BLACKKING )
		{
			ArrayList<Move> m = new ArrayList<Move>(8);
			
			//simply add all the moves available for both orientations
			pieceLocation.demote();
			m = movesAvailable(board, pieceLocation, true, false);
			moves.addAll(m);
			m = movesAvailable(board, pieceLocation, false, false);
			moves.addAll(m);
			pieceLocation.promote();
		}
		
		return moves;
	}
	
	//find and return all possible moves on the board  for a given player color and orientation
	public ArrayList<Move> allMovesAvailable(Board board, PieceType color, boolean orientation)
	{
		ArrayList<Move> moves = new ArrayList<Move>(50);
		
		for(int i = 0; i < 32; i++)
		{
			BoardPosition p = board.getPosition(i);
			
			if(p.getColor() == color)
				moves.addAll(movesAvailable(board, p, orientation, false));
		}
		return moves;
	}
	
	//check for and return whether a jump is available given a list of moves
	public boolean jumpAvailable(ArrayList<Move> moves)
	{
		//ArrayList<Move> moreMoves = movesAvailable(jumpLocation, orientedUp, false);
		boolean jump = false;
		
		for(Move move : moves)
		{
			if(move.isJump())
				jump = true;
		}
		return jump;
	}
	
	public String toString()
	{
		/*String s = "";
		int i = 0;
		int j = 0;
		for(BoardPosition square : boardSquares)
		{
			if(i%4==0)
				s = s + "\n";
			s = s + square + " "+(i+1)+ ",";
			i++;
		}*/
		return stringBoard();
	}
}