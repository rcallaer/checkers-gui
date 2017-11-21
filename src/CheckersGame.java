import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

enum PieceType {NONE, RED, BLACK, REDKING, BLACKKING}
enum MoveOptions {UL, UR, DL, DR}

public class CheckersGame 
{
	private static final int MAX_PIECES = 12;
	private int opponentNumberOfPieces;
	private int playerNumberOfPieces;
	private Board initialBoard, currentBoard;
	private PieceType playerColor, opponentColor;
	private Move playerMove;
	private ArrayList<Move> aiMove;
	private boolean playerOrientedUp = true;
	private boolean twoPlayerGame = false;
	private boolean playerTurn, playerMovesFirst;
	private String winner = "";
	private int moveNumber = 0, sequenceNumber = 0;
	private ArrayList<Move> sequenceMove;
	private boolean onSequence = false;
	private String sequenceName;
	
	public CheckersGame()
	{
		boardSetup();
	}
	
	public void boardSetup()
	{
		setPlayers();
		opponentNumberOfPieces = playerNumberOfPieces = MAX_PIECES;

		initialBoard = new Board();
		currentBoard = initialBoard;
		
		if(playerColor == PieceType.BLACK)
		//if(playerColor == PieceType.RED)
			currentBoard.flipBoard();
		currentBoard.printBoard();
		sequenceMove = new ArrayList<Move>();
	}
	
	public void setPlayers()
	{
		//playerTurn = false;
		//playerColor = PieceType.RED;
		//opponentColor = PieceType.BLACK;
		
		Scanner input = new Scanner(System.in);
		String playerColor = "", players = "";
		boolean validRed = false, validBlack = false, validOne = false, validTwo = false;
		
		System.out.println("Red or Black (type R or B)?");
		
		do{
			playerColor = input.next();
			validRed = playerColor.matches("[rR]");
			validBlack = playerColor.matches("[bB]");
			
			if(validRed)
			{
				playerOrientedUp = true;
				playerTurn = playerMovesFirst = false;
				this.playerColor = PieceType.RED; 
				opponentColor = PieceType.BLACK;
				//playerOrientedUp = false;
				//playerTurn = playerMovesFirst = true;
				//this.playerColor = PieceType.BLACK; 
				//opponentColor = PieceType.RED;
			}
			else if (validBlack)
			{
				playerOrientedUp = false;
				playerTurn = playerMovesFirst = true;
				this.playerColor = PieceType.BLACK;
				opponentColor = PieceType.RED;
				//playerOrientedUp = true;
				//playerTurn = playerMovesFirst = false;
				//this.playerColor = PieceType.RED; 
				//opponentColor = PieceType.BLACK;
			}
			else
				System.out.println("Invalid color selection. Type R or B.");
		}while (!validRed && !validBlack);
			
		System.out.println("One or two players (type 1 or 2)?");
		
		do{
			players = input.next();
			validOne = players.trim().equals("1");
			validTwo = players.trim().equals("2");
			
			if(validOne)
				twoPlayerGame = false;
			else if(validTwo)
				twoPlayerGame = true;
			else
				System.out.println("Invalid color selection. Type 1 or 2.");
		}while (!validOne && !validTwo);
	}
	
	//move depeinding on whose turn it is
	public boolean move(String move)
	{
		if(playerTurn)
			return playerMove(move, getPlayerColor(1), playerOrientedUp);
		else
			return AIMove(move);
	}
	
	//primary move method that takes the text with a move in the form 23-18, the color of the player's piece,
	//and the player's orientation as a boolean, where true = up
	public boolean playerMove(String moveText, PieceType color, boolean orientedUp)
	{
		ArrayList<Move> moves = new ArrayList<Move>(); //will hold moves available for the piece selected
		ArrayList<Move> allPotentialMoves = new ArrayList<Move>(); //will hold all possible moves for this player on the board
		Move requestedMove; //will hold the full move indicated by the player

		requestedMove = createMove(moveText);
		
		//poorly formed move text
		if(requestedMove.getStart().getCol() == '-' || requestedMove.getStart().getRow() == -1)
			return false;
		
		//retrieve information about the actual current board square
		int startIndex = currentBoard.correspondingIndex(requestedMove.getStart());  //index on the board
		BoardPosition actualPieceLocation = currentBoard.getPosition(startIndex); //the board location
		
		//check if the piece on the board matches the color of the player moving
		if(actualPieceLocation.getColor() != color)
		{
			return false;
		}
		
		//gather and store available moves for this piece/board location
		moves = movesAvailable(currentBoard, actualPieceLocation, orientedUp, false);
		
		//if there are no moves return an error
		if(moves.isEmpty())
			return false;
		
		boolean validMove = false;
		boolean mustJump = false;
		
		//gather all possible moves to check for possible jump
		allPotentialMoves = allMovesAvailable(currentBoard, color, orientedUp);
		
		//search all moves and set a must jump flag if there are any jumps available
		for(Move m : allPotentialMoves)
			if(m.isJump())
				mustJump = true;
		
		int size = moves.size();
		
		//iterate through possible moves for this piece only
		for(int i = 0; i < size; i++)
		{
			//if a possible move matches the one requested, execute it
			if(moves.get(i).toString().equals(requestedMove.toString()))
			{
				//perform a jump move
				if(moves.get(i).isJump())
				{
					//change the piece on the board and update pieces
					currentBoard.jumpMovePiece(requestedMove);
					if(color == playerColor)
						opponentNumberOfPieces--;
					else
						playerNumberOfPieces--;
					validMove = true;
					
					requestedMove=moves.get(i);
				}
				//perform a regular move as long as the must jump flag isn't set
				else if(!mustJump)
				{
					currentBoard.movePiece(requestedMove);
					validMove = true;
				}
				//throw an error if you must jump
				else
				{
					System.out.println("Invalid move. You must jump.");
					return false;
				}
			}
		}
			
		playerMove=requestedMove;
		//if(currentBoard.newKingObtained())
			//currentBoard.setNewKing(PieceType.NONE);
		return validMove;
	}
	
	//move using AI or second player
	public boolean AIMove(String opponentMoveText)
	{
		if(isOver(PieceType.RED) || isOver(PieceType.BLACK))
			return true;
		
		//if a two player game, simply call main move method
		if(isTwoPlayerGame())
		{
			return playerMove(opponentMoveText, getPlayerColor(2), !playerOrientedUp);
		}
		//otherwise, gather all possible moves for player 2, and select a random one for now
		else
		{
			Random generator = new Random();
			int randomNumber;
			boolean mustJump = false;
			
			//regular move from the AI, either one square or single jump
			if(opponentMoveText.equals("AI"))
			{
				MinMaxTree aiTree = new MinMaxTree(this, currentBoard, moveNumber);
				aiTree.setSequenceInfo(sequenceMove, sequenceNumber, onSequence, sequenceName);
				//aiTree.populateTree();
				
				//ArrayList<Move> potentialMoves = allMovesAvailable(currentBoard, getPlayerColor(2), !playerOrientedUp);
				
				//int n = potentialMoves.size();
				//randomNumber = generator.nextInt(potentialMoves.size());
				
				//aiMove = potentialMoves.get(randomNumber);
				aiMove = aiTree.minmax();
				sequenceMove = aiMove;
				sequenceNumber = aiTree.getSequenceNumber();
				onSequence = aiTree.isOnSequence();
				sequenceName = aiTree.getSequenceName();
				
				System.out.println(aiMove);
				
//				for(Move m : potentialMoves)
//					if(m.isJump())
//						mustJump = true;
				
				/*while(mustJump && !aiMove.isJump())
				{
					randomNumber = generator.nextInt(potentialMoves.size());
					aiMove = potentialMoves.get(randomNumber);
				}*/
			}
			//only happens through recursive call, when aiMove will be another Jump in some chain of jumps
			else
			{
				//int
				//BoardPosiiton b = new BoardPosition();
				
				//aiMove = createMove(opponentMoveText);
				//aiMove.setAsJumpMove();
			}
			
			for(Move move : aiMove)
			{
				if(move.isJump())
				{
					currentBoard.jumpMovePiece(move);
					playerNumberOfPieces--;
					if (currentBoard.newKingObtained()) {
				
				//	if(currentBoard.getKingStage() == 1){
						currentBoard.setNewKing(PieceType.NONE);
						//currentBoard.setKingStage(2);
						break;
					}
					
				}
				else
					currentBoard.movePiece(move);
			}
			/*if(aiMove.isJump())
			{
				currentBoard.jumpMovePiece(aiMove);
				playerNumberOfPieces--;
				
			}
			else
				currentBoard.movePiece(aiMove);*/
			moveNumber++;
		}
		
		//if(currentBoard.newKingObtained())
			//currentBoard.setNewKing(PieceType.NONE);
		return true;
	}
	
	//create and return a new Move from a given String with the appropriate text format
	public Move createMove(String moveText)
	{
		Move move;
		
		//check that the move was entered in the correct format, and convert from the old notation
		String converted = convertNotation(moveText);
		if(converted.isEmpty())
			return new Move(new BoardPosition('-',-1, PieceType.NONE),new BoardPosition('-',-1, PieceType.NONE));
		
		//create positions based on the movement data
		BoardPosition start = new BoardPosition(converted.charAt(0), Character.getNumericValue(converted.charAt(1)), PieceType.NONE);
		BoardPosition end = new BoardPosition(converted.charAt(3), Character.getNumericValue(converted.charAt(4)), PieceType.NONE);
		
		move = new Move(start, end);
		return move;
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
	
	//check correct format of move entry
	public boolean correctSyntax(String moveText)
	{
		if(moveText.length() < 5)
			return false;

		boolean valid = moveText.matches("[a-h][1-8][-][a-h][1-8]");

		return valid;
	}
	
	//return the color of the player
	public PieceType getPlayerColor(int player)
	{
		if(player == 1)
			return playerColor;
		else if(player == 2)
			return opponentColor;
		else
			return PieceType.NONE;
			
	}
	
	//return the opposite color of the given color
	public PieceType getOppositeColor(PieceType color)
	{
		if(color == PieceType.RED)
			return PieceType.BLACK;
		else if(color == PieceType.BLACK)
			return PieceType.RED;
		else
			return PieceType.NONE;
			
	}
	
	public Move getPlayerMove()
	{
		return playerMove;
	}
	
	//public Move getAIMove()
	public ArrayList<Move> getAIMoves() 
	{
		return aiMove;
	}
	
	public String getWinner()
	{
		return winner;
	}
	
	public Board getBoard()
	{
		return currentBoard;
	}
	
	public void changePlayerTurn()
	{
		playerTurn = !playerTurn;
	}
	
	public void setPlayerColor(PieceType color)
	{
		playerColor = color;
	}
	
	public void setOpponentColor(PieceType color)
	{
		playerColor = color;
	}
	
	public void setBoard(Board board)
	{
		currentBoard = board;
	}
	
	public void setPlayerOrientation(boolean up)
	{
		playerOrientedUp = up;
	}
	
	public void setPlayerTurn(boolean playerTurn)
	{
		this.playerTurn = playerTurn;
	}
	
	public boolean isTwoPlayerGame()
	{
		return twoPlayerGame;
	}
	
	public boolean isPlayerTurn()
	{
		return playerTurn;
	}
	
	public boolean playerMovesFirst()
	{
		return playerMovesFirst;
	}
	
	public boolean hasPlayerOrientedUp()
	{
		return playerOrientedUp;
	}
	
	public boolean isOver(PieceType color)
	{
		//boolean noRedMoves = noMoves(PieceType.RED);
		boolean noMoves = noMoves(color);
		
		if(noMoves && color == PieceType.RED)
			currentBoard.setRedMoveless();
		else if(noMoves && color == PieceType.BLACK)
			currentBoard.setBlackMoveless();
		
		return currentBoard.endBoard() || noMoves;//noRedMoves || noBlackMoves;
	}

	public void newGame()
	{
		boardSetup();
	}
	
	public boolean noMoves(PieceType color)
	{
		if(playerColor == color)
			return allMovesAvailable(currentBoard, playerColor, playerOrientedUp).isEmpty();
		else
			return allMovesAvailable(currentBoard, opponentColor, !playerOrientedUp).isEmpty();
	}
	
	//convert from the new one(23-18) to old notation(e3-f4)
	public String convertNotation(String text)
	{	
		String [] numbers = text.split("-");
		
		if(numbers.length != 2)
			return "";
		
		if(numbers[0].isEmpty() || numbers[1].isEmpty())
			return "";
		
		if(!text.matches("[1-9]?[0-9][-][1-9]?[0-9]"))
			return "";
		
		if(Integer.parseInt(numbers[0]) > 32 || Integer.parseInt(numbers[0]) < 1 ||
		   Integer.parseInt(numbers[1]) > 32 || Integer.parseInt(numbers[1]) < 1)
			return "";
		
		if(numbers[0].equals("0") || numbers[1].equals("0"))
			return "";
		
		return convertNumber(Integer.parseInt(numbers[0])) +"-"+ convertNumber(Integer.parseInt(numbers[1]));
	}

	//convert a col/row position to numeric index
	public String convertNumber(int index)
	{
		//String temp = currentBoard.getPosition(index - 1).getColAndRow();
		return currentBoard.getPosition(index - 1).getColAndRow();
	}
	
	public void undoMove(Move move, PieceType color, boolean playerUp)
	{
		Move undo = new Move(move.getEnd(),move.getStart());
		
		playerMove(undo+"", color, playerUp);
	}
}