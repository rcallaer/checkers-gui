import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MinMaxTree 
{
	private static final int DEPTH = 8;
	public int currentDepth = 0;
	private BoardNode root;
	private CheckersGame topGame;
	private int moveNumber = 0;
	private ArrayList<ArrayList<BoardNode>> openings;
	private ArrayList<ArrayList<BoardNode>> responses;
	private int sequenceNumber = 0;
	private boolean onSequence = false;
	private ArrayList<Move> sequenceMove;
	private String sequenceName;
	private boolean weirdBool =  false;
	private boolean endgame = false;
	private boolean allKings = false;
	private boolean losing = false;
	private int redKings = 0;
	private int blackKings = 0;
	
	private class BoardNode
	{
		private double value; 
		private Board board;
		private Move move;
		
		private BoardNode(Board board)
		{
			this.board = board;
			value = 0;
		}
		
		public Board getBoard()
		{
			return board;
		}
		
		public double getValue()
		{
			return value;
		}
		
		public Move getMove()
		{
			return move;
		}
		
		public void setValue(double value)
		{
			this.value = value;
		}
		
		public void setMove(Move m)
		{
			move = m;
		}
		
		public String toString()
		{
			return move.toString();
		}
	}
	
	public MinMaxTree(CheckersGame game, Board board, int moveNumber)
	{
		root = new BoardNode(copyBoard(board));
		topGame = game;
		this.moveNumber = moveNumber;
		openings = new ArrayList<ArrayList<BoardNode>>();
		responses = new ArrayList<ArrayList<BoardNode>>();
	}
	
	//public Move minmax()
	public ArrayList<Move> minmax()
	{
		//getOpenings(root, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,topGame.getPlayerColor(2), !topGame.hasPlayerOrientedUp(), 2, true);
		double time = System.currentTimeMillis();
		ArrayList<Move> moves = new ArrayList<Move>();
		Move m;
		boolean best = false;
		endgame = endgame();
		allKings = allKings();
		redKings = topGame.getBoard().getRedKings();
		blackKings = topGame.getBoard().getBlackKings();
		
		if(opening() || onSequence)
		{
			String converted = "";
			if(topGame.getPlayerColor(2) == PieceType.BLACK && moveNumber == 0)
			{
				//Move previousMove = topGame.getPlayerMove();
				String [] converts = {"11-15","10-15","10-14"};
				Random gen = new Random();
				
				//int rand = gen.nextInt(3);
				
				double rand = Math.random();
				int choice = 0;
				
				if(rand < 0.5)
					choice = 0;
				else if(rand < 0.8)
					choice = 1;
				else
					choice = 2;
					
				converted = topGame.convertNotation(converts[choice]);
				
				//checkSequence(previousMove);
				BoardPosition start = new BoardPosition(converted.charAt(0), Character.getNumericValue(converted.charAt(1)), PieceType.NONE);
				BoardPosition end = new BoardPosition(converted.charAt(3), Character.getNumericValue(converted.charAt(4)), PieceType.NONE);
				
				m = new Move(start, end);
				sequenceMove.add(m);
			}
			//returnValue = 0.0;
			else if(topGame.getPlayerColor(2) == PieceType.BLACK)
			{
				Move previousMove = topGame.getPlayerMove();
				//converted = topGame.convertNotation("23-19");
				sequenceMove = checkSequence(previousMove, PieceType.BLACK);
				
			}
			else
			{
				converted = topGame.convertNotation("23-19");
				Move previousMove = topGame.getPlayerMove();
				//converted = topGame.convertNotation("23-19");
				sequenceMove = checkSequence(previousMove, PieceType.RED);
			}
			
			//moves.add(sequenceMove);
			if(!sequenceMove.isEmpty())
				return sequenceMove;
		}
		onSequence = false;
		//ArrayList<BoardNode> bn = root.getBoards();
		
		//THIS SHOULLD RETURN LIST OF LIST OF MOVES FOR DOUBLE JUMPS
		ArrayList<ArrayList<BoardNode>> bn = generateMultipleBoardMoves(root, topGame.getPlayerColor(2),!topGame.hasPlayerOrientedUp());
		ArrayList<ArrayList<BoardNode>> jumpMoves = new ArrayList<ArrayList<BoardNode>>();
		boolean isThereJump = false;
		
		for(ArrayList<BoardNode> jumpMove : bn) 
		{
			if ( jumpMove.get(0).getMove().isJump()) 
			{
				jumpMoves.add(jumpMove);
				isThereJump = true;
			}
		}
		
		if (isThereJump)
			bn = jumpMoves;
		
		
		m = bn.get(0).get(bn.get(0).size()-1).getMove();
		double minVal = Double.POSITIVE_INFINITY, maxVal = Double.NEGATIVE_INFINITY;
		int count = 0, totalCount = 0;
		ArrayList<Integer> ints = new ArrayList<Integer>();
		ArrayList<BoardNode> moveList = new ArrayList<BoardNode>();
		
		int depth = DEPTH;
		
		//for(BoardNode b : bn.get(bn.size()-1))
		for(ArrayList<BoardNode> bs : bn)
		{
			BoardNode b = bs.get(bs.size()-1);
			
			//System.out.println(b.getMove());
			double value = minmaxAlphaBeta(b, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, topGame.getPlayerColor(1), topGame.hasPlayerOrientedUp(), DEPTH - 1, weirdBool);
			b.setValue(value);
			
			System.out.print(value + " " + b.getMove() + " " + b.getValue() + "\n");
			
			if(value == maxVal && depth % 2 == 0)
			{
				count++;
				ints.add(totalCount);
			}
			if(value == minVal && depth % 2 == 1)
			{
				count++;
				ints.add(totalCount);
			}
			
			if(value > maxVal && depth % 2 == 0)
			{
				moves = new ArrayList<Move>();
				maxVal = value;
				m = b.getMove();
				for(BoardNode moveNode : bs)
				{
				//do
				//{
					moves.add(moveNode.getMove());
					//tempNode = bn.get((int)ints.get(randomNumber)).get(bn.size()-1);
					//m = tempNode.getMove();
				}
				best = true;
				count = 0;
				ints = new ArrayList<Integer>();
			}
			
			if(value < minVal && depth % 2 == 1)
			{
				moves = new ArrayList<Move>();
				minVal = value;
				m = b.getMove();
				for(BoardNode moveNode : bs)
				{
				//do
				//{
					moves.add(moveNode.getMove());
					//tempNode = bn.get((int)ints.get(randomNumber)).get(bn.size()-1);
					//m = tempNode.getMove();
				}
				best = true;
				count = 0;
				ints = new ArrayList<Integer>();
			}
			totalCount++;
		}
		best = count > 0;

		if(count > 0)
		{
			Random generator = new Random();
			int randomNumber;
			//BoardNode tempNode;
			moveList = new ArrayList<BoardNode>();
			moves = new ArrayList<Move>();
			
			randomNumber = generator.nextInt(count);
			moveList = bn.get((int)ints.get(randomNumber));
			
			for(BoardNode moveNode : moveList)
			{
			//do
			//{
				moves.add(moveNode.getMove());
				//tempNode = bn.get((int)ints.get(randomNumber)).get(bn.size()-1);
				//m = tempNode.getMove();
			}
				//System.out.println("index: "+(int)ints.get(randomNumber));
				//System.out.println("random: " + randomNumber + " max: " + maxVal + " min: " + minVal);
			//}while(maxVal < tempNode.getValue());
		}
		
		System.out.println("time: " + (System.currentTimeMillis() - time));
		System.out.println(moveNumber);
		return moves;
	}
	
	public double minmaxAlphaBeta(BoardNode node, double alpha, double beta, PieceType color, boolean orientation, int depth, boolean maxPlayer)
	{
		//THIS SHOULD BE LIKELY BE ADDED HERE ALSO
		Board evalBoard = node.getBoard();
		if(currentWins(evalBoard, color))
			//return Double.POSITIVE_INFINITY;
			if(maxPlayer)
				return Double.POSITIVE_INFINITY;
			else
				return Double.NEGATIVE_INFINITY;
		else if(opponentWins(evalBoard, color))
			//return Double.NEGATIVE_INFINITY;
			if(maxPlayer)
				return Double.NEGATIVE_INFINITY;
			else
				return Double.POSITIVE_INFINITY;
		
		if(depth <= 0)
		{
			return evaluateBoard(node, color, orientation);
		}
		
		if(maxPlayer)
		{
			ArrayList<BoardNode> boards = generateBoardMoves(node, color,orientation);//node.getBoards();
			ArrayList<BoardNode> jumps = new ArrayList<BoardNode>();
			boolean jump = false;
			
			for(BoardNode n : boards)
			{
				if(n.getMove().isJump())
				{
					jumps.add(n);
					jump = true;
				}
			}
			
			if(jump)
				boards = jumps;
			
			for(BoardNode n : boards)
			{
				alpha = Math.max(alpha, minmaxAlphaBeta(n, alpha, beta, topGame.getOppositeColor(color), !orientation, depth - 1, false));
				if(beta <= alpha)
				{
					break;
				}
			}
			
			return alpha;
		}
		else
		{
			ArrayList<BoardNode> boards = generateBoardMoves(node, color,orientation);//node.getBoards();
			ArrayList<BoardNode> jumps = new ArrayList<BoardNode>();
			boolean jump = false;
			
			for(BoardNode n : boards)
			{
				if(n.getMove().isJump())
				{
					jumps.add(n);
					jump = true;
				}
			}
			
			if(jump)
				boards = jumps;
			
			for(BoardNode n : boards)
			{
				beta = Math.min(beta, minmaxAlphaBeta(n, alpha, beta, topGame.getOppositeColor(color), !orientation, depth - 1, true));
				if(beta <= alpha)
				{
					break;
				}
			}
			
			return beta;
		}
		//alpha = Double.NEGATIVE_INFINITY;
		
		//return node;
	}
	
	public double evaluateBoard(BoardNode boardNode, PieceType color, boolean orientation)
	{
		double returnValue = 0.0;
		Board evalBoard = boardNode.getBoard();
		if(currentWins(evalBoard, color))
			//return Double.POSITIVE_INFINITY;
			returnValue += 100000;
		else if(opponentWins(evalBoard, color))
			//return Double.NEGATIVE_INFINITY;
			returnValue += -100000;
		
		int red = 100*(12 - evalBoard.getBlackCapturedPieces());
		int black = 100*(12 - evalBoard.getRedCapturedPieces());
		
		int redKings = 100*(evalBoard.getRedKings());
		int blackKings = 100*(evalBoard.getBlackKings());
		
		//red = red - redKings;
		//black = black - blackKings;
		int pos1, pos2;
		
		if(color == PieceType.BLACK)
		{
			pos1 = 28;
			pos2 = 21;
		}
		else
		{
			pos1 = 5;
			pos2 = 12;
		}
		
		int [] sides = {pos1,pos2, 14,19};
		
		int edges[] = {1,2,3,4,5,12,13,20,21,28,29,30,31,32};
		int corners [] = {4,29};
		int doubleCorners [] = {1,5,28,32};
		boolean aggressiveKings = false;
		boolean losing = losing(color);
		double kingRatio = 1.0;
		
		ArrayList<BoardPosition> endGamePlayerPieces = new ArrayList<BoardPosition>();
		ArrayList<BoardPosition> endGameOpponentPieces = new ArrayList<BoardPosition>();
		
		if(color == PieceType.RED)
		{
			returnValue += red - black + 1.4*(redKings - blackKings);
		}
		else
		{
			returnValue += black - red + 1.4*(blackKings - redKings);
		}
		
		/*if((redKings > 2 && color == PieceType.RED) ||
		   (blackKings > 2 && color == PieceType.BLACK))*/
		if((redKings > 2 && color == PieceType.RED) ||
			(blackKings > 2 && color == PieceType.BLACK) ||
			(endgame && redKings > 1 && color == PieceType.RED && redKings > blackKings) ||
			(endgame && blackKings > 1 && color == PieceType.BLACK && blackKings > redKings))
		{
			aggressiveKings = true;
			if(color == PieceType.BLACK)
				kingRatio = (blackKings - redKings)/100;
			else
				kingRatio = (redKings - blackKings)/100;
		}
		
		BoardPosition squares [] = boardNode.getBoard().getSquares();
	
		/*if(kingInACorner(color))
		{
			returnValue -= 40;
		}*/
		
		double row = 0;
		for(int i = 0; i < 32; i++)
		{
			PieceType currentPiece = squares[i].getPiece();
			
			if(i%4 == 0 && i != 0)
				row++;
				
			//if((currentPiece == PieceType.RED && color == PieceType.RED) ||
			//   (currentPiece == PieceType.BLACK && color == PieceType.BLACK) && !orientation)
			if((currentPiece == PieceType.RED && color == PieceType.RED))
			{
				//returnValue += (row)*(row);
				returnValue += (7-row);
				
				//man to side of board +10
				for (int side : sides)
					if(i == side-1)
						returnValue += 8;
			}
			
			if((currentPiece == PieceType.RED && color == PieceType.BLACK))
			{
				returnValue -= (7-row);
				
				//man to side of board +10
				for (int side : sides)
					if(i == side-1)
						returnValue -= 8;
			}
			//if((currentPiece == PieceType.RED && color == PieceType.RED) ||
			//   (currentPiece == PieceType.BLACK && color == PieceType.BLACK) && orientation)
			if((currentPiece == PieceType.BLACK && color == PieceType.BLACK))
			{
				//
				//returnValue += (7-row)*(7-row);
				//returnValue += (7-row);
				returnValue += row;
				
				//man to side of board +10
				for (int side : sides)
					if(i == side-1)
						returnValue += 8;
			}
			
			if((currentPiece == PieceType.BLACK && color == PieceType.RED))
			{
				returnValue -= (row);
				
				//man to side of board +10
				for (int side : sides)
					if(i == side-1)
						returnValue -= 8;
			}
			
			/*if((currentPiece == PieceType.REDKING && color == PieceType.RED) ||
               (currentPiece == PieceType.BLACKKING && color == PieceType.BLACK))
			{
				
			}*/
			
			if(endgame)
			{
				if(allKings)
				{
					if((currentPiece == PieceType.REDKING && color == PieceType.REDKING) ||
	                        (currentPiece == PieceType.BLACKKING && color == PieceType.BLACKKING))
					{
						//kings move less to edges -20
						for(int edge: edges)
							if(i == edge-1)
								returnValue += -15; 
						
						//kings move less to edges -40
		                for(int corner : corners)
		                	if(i == corner-1)
		                		returnValue += -30;
					}
					
					if(losing)
					{
						//kings move less to double corners
		                for(int corner : doubleCorners)
		                	if(i == corner-1)
		                		returnValue += 40;
					}
				}
				
				/*if((squares[i].getPiece() == PieceType.REDKING && color == PieceType.RED) ||
				  ((squares[i].getPiece() == PieceType.BLACKKING )&& color == PieceType.BLACK))
				//if((squares[i].pieceIsRed() && color == PieceType.RED) ||
				//  ((squares[i].getPiece() == PieceType.BLACK || squares[i].getPiece() == PieceType.BLACK )&& color == PieceType.BLACK))
				{
					endGamePlayerPieces.add(squares[i]);
					//endGameOpponentPieces.add(squares[i]);
				}
				else if((squares[i].pieceIsRed() && color == PieceType.BLACK) ||
				 ((squares[i].getPiece() == PieceType.BLACK || squares[i].getPiece() == PieceType.BLACKKING ) && color == PieceType.RED))
				//if((currentPiece == PieceType.REDKING && color == PieceType.RED) ||
		        //  (currentPiece == PieceType.BLACKKING && color == PieceType.BLACK))
				{
					//endGamePlayerPieces.add(squares[i]);
					endGameOpponentPieces.add(squares[i]);
				}*/
			}
			
			if(aggressiveKings)
			{
				if((squares[i].getPiece() == PieceType.REDKING && color == PieceType.RED) ||
				  ((squares[i].getPiece() == PieceType.BLACKKING )&& color == PieceType.BLACK))
				{
					endGamePlayerPieces.add(squares[i]);
				}
				else if((squares[i].pieceIsRed() && color == PieceType.BLACK) ||
				 ((squares[i].getPiece() == PieceType.BLACK || squares[i].getPiece() == PieceType.BLACKKING ) && color == PieceType.RED))
				{
					endGameOpponentPieces.add(squares[i]);
				}
			}
		
		}
		
		if(aggressiveKings)
		{
			//char [] colPositions = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
			//ArrayList<Double> distances = new ArrayList<Double>();
			double longest = 0.0;

			for(BoardPosition opponentSquare : endGameOpponentPieces)
			{
				for(BoardPosition playerSquare : endGamePlayerPieces)
				{
					int correspondingPlayerNumber = evalBoard.correspondingIndex(playerSquare) + 1;
					int correspondingOpponentNumber = evalBoard.correspondingIndex(opponentSquare) + 1;
					
					int playerCol = convertCol(playerSquare.getCol());
					int playerRow = 8 - playerSquare.getRow();
					
					int oppCol = convertCol(opponentSquare.getCol());
					int oppRow = 8 - opponentSquare.getRow();
					
					double distance = distance(oppCol, oppRow, playerCol, playerRow);
					//distances.add(distance);
					if(distance > longest)
					{
						//oppIndex = i;
						//playerIndex = j;
						longest = distance;
					}
				}
			}
			returnValue -= kingRatio*longest*longest;
		}
		
		if(endgame)
		{
			
		}
		else
		{
			if((color == PieceType.RED))
			{
				if(squares[29].getPiece() == PieceType.RED && squares[31].getPiece() == PieceType.RED)
					returnValue += 15;
				else if(squares[31].getPiece() == PieceType.RED)
					returnValue += 5;
				else if(squares[29].getPiece() == PieceType.RED)
					returnValue += 5;
				if(squares[30].getPiece() == PieceType.RED)
					returnValue += 2;
				
				if(squares[29].getPiece() == PieceType.RED)
					returnValue -= 5;
			}
			if((color == PieceType.BLACK))
			{
				if(squares[0].getPiece() == PieceType.BLACK && squares[2].getPiece() == PieceType.BLACK)
					returnValue += 15;
				else if(squares[2].getPiece() == PieceType.BLACK)
					returnValue += 5;
				else if(squares[0].getPiece() == PieceType.BLACK)
					returnValue += 5;
				if(squares[1].getPiece() == PieceType.BLACK)
					returnValue += 2;
				
				if(squares[3].getPiece() == PieceType.BLACK)
					returnValue -= 5;
			}
		}
		//}
		
		//do evaluation stuff
		return returnValue;
	}
	
	public boolean opening()
	{
		return moveNumber <= 1;
	}
	
	public boolean endgame()
	{
		int red = 12 - topGame.getBoard().getBlackCapturedPieces();
		int black = 12 - topGame.getBoard().getRedCapturedPieces();
		
		int total = red + black;
		
		return total <= 6;
	}
	
	public boolean allKings()
	{
		BoardPosition [] squares = topGame.getBoard().getSquares();
		
		for (BoardPosition square : squares)
		{
			if(square.getPiece() == PieceType.RED || square.getPiece() == PieceType.BLACK)
				return false;
		}
		return true;
	}
	
	public boolean losing(PieceType color)
	{
		int red = 12 - topGame.getBoard().getBlackCapturedPieces();
		int black = 12 - topGame.getBoard().getRedCapturedPieces();
		
		if(color == PieceType.RED && red < black)
			return true;
		if(color == PieceType.BLACK && black < red)
			return true;
		return false;
	}
	
	public boolean kingInACorner(PieceType color)
	{
		BoardPosition [] squares = topGame.getBoard().getSquares();
		
		if((color == PieceType.BLACK && squares[3].getPiece() == PieceType.BLACKKING) ||
			(color == PieceType.BLACK && squares[28].getPiece() == PieceType.BLACKKING))
			return true;
		
		if((color == PieceType.RED && squares[3].getPiece() == PieceType.REDKING) ||
			(color == PieceType.RED && squares[28].getPiece() == PieceType.REDKING))
			return true;
		return false;
	}
	
	public int convertCol(char col)
	{
		int ret = 0;
		
		switch(col)
		{
			case 'a': ret = 0;
			break;
			case 'b': ret = 1;
			break;
			case 'c': ret = 2;
			break;
			case 'd': ret = 3;
			break;
			case 'e': ret = 4;
			break;
			case 'f': ret = 5;
			break;
			case 'g': ret = 6;
			break;
			case 'h': ret = 7;
			break;
		}
		return ret;
	}
	
	public double distance(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	public boolean currentWins(Board board, PieceType color)
	{
		if(color == PieceType.RED)
			return board.redWins();
		else
			return board.blackWins();
	}
	
	public boolean opponentWins(Board board, PieceType color)
	{
		if(color == PieceType.RED)
			return board.blackWins();
		else
			return board.redWins();
	}
	
	public boolean depthLimitReached()
	{
		return currentDepth == DEPTH;
	}
	
	//make and return a deep copy of the given board
	public Board copyBoard(Board board)
	{
		Board copy = new Board();
		
		copy.setSquares(board.getSquares());
		copy.setMoves(board.getMoves());
		copy.setFlipped(board.isFlipped());
		copy.setBlackCanMove(board.blackCanMove());
		copy.setRedCanMove(board.redCanMove());
		copy.setBlackCapturedPieces(board.getBlackCapturedPieces());
		copy.setRedCapturedPieces(board.getRedCapturedPieces());
		copy.setNewKing(board.getNewKing());
		
		return copy;
	}
	
	public ArrayList<BoardNode> generateBoardMoves(BoardNode node, PieceType color, boolean playerUp)
	{
		Board board = new Board(); 
		board = copyBoard(node.getBoard());

		ArrayList<Move> moves = board.allMovesAvailable(board, color, playerUp);
		ArrayList<BoardNode> nodes = new ArrayList<BoardNode>();
		
		boolean addJump = true;
		
		for(Move move : moves)
		{
			Board boardMove = new Board();
			boardMove = copyBoard(board);

			if(move.isJump())
			{
				boardMove.jumpMovePiece(move);
				
				ArrayList<BoardNode> moreJumps = getMoreJumps(move, boardMove, color, playerUp,0);
				
				if(!moreJumps.isEmpty())
				{
					nodes.addAll(moreJumps);
					addJump = false;
				}
			}
			else
				boardMove.movePiece(move);
			
			//boardMoves.add(board);
			if(addJump)
			{
				BoardNode boardMoveNode = new BoardNode(boardMove);
				boardMoveNode.setMove(move);
				nodes.add(boardMoveNode);
			}
			
			addJump = true;
			//populateTree(boardMoveNode, game, game.getOppositeColor(color), !orientation, depth+1);
		}
		return nodes;
	}
	
	public ArrayList<ArrayList<BoardNode>> generateMultipleBoardMoves(BoardNode node, PieceType color, boolean playerUp)
	{
		Board board = new Board(); 
		board = copyBoard(node.getBoard());

		ArrayList<Move> moves = board.allMovesAvailable(board, color, playerUp);
		ArrayList<BoardNode> nodes = new ArrayList<BoardNode>();
		ArrayList<ArrayList<BoardNode>> returnNodes = new ArrayList<ArrayList<BoardNode>>();
		
		boolean addJump = true;
		
		for(Move move : moves)
		{
			Board boardMove = new Board();
			boardMove = copyBoard(board);
			nodes = new ArrayList<BoardNode>();

			if(move.isJump())
			{
				boardMove.jumpMovePiece(move);
				
				ArrayList<ArrayList<BoardNode>> moreJumps = getAllMoreJumps(new ArrayList<BoardNode>(),move, boardMove, color, playerUp,0);
				
				if(!moreJumps.isEmpty())
				{
					returnNodes.addAll(moreJumps);
					addJump = false;
				}
			}
			else
				boardMove.movePiece(move);
			
			//boardMoves.add(board);
			if(addJump)
			{
				BoardNode boardMoveNode = new BoardNode(boardMove);
				boardMoveNode.setMove(move);
				nodes.add(boardMoveNode);
				returnNodes.add(nodes);
			}
			
			addJump = true;
			//populateTree(boardMoveNode, game, game.getOppositeColor(color), !orientation, depth+1);
			//returnNodes.add(nodes);
		}
		return returnNodes;
	}
	
	//returns additional jumps recursively
	public ArrayList<BoardNode> getMoreJumps(Move currentMove, Board board, PieceType color, boolean playerUp, int depth)
	{
		ArrayList<BoardNode> nodes = new ArrayList<BoardNode>();
		int index = board.correspondingIndex(currentMove.getEnd().getCol(),currentMove.getEnd().getRow());
		BoardPosition position = board.getPosition(index);
		ArrayList<Move> possibleJumps = board.movesAvailable(board, position, playerUp, false);
		ArrayList<Move> actualJumps = new ArrayList<Move>();
		
		for(Move move : possibleJumps)
		{
			if(move.isJump())
				actualJumps.add(move);
		}
		
		//base case for recursion, no more jumps available
		if(actualJumps.isEmpty())
		{
			//at depth 0 means no double jumps are available, so return blank list
			if(depth == 0)
				return nodes;
			
			//set board node with current board and associated move
			BoardNode currentNode = new BoardNode(copyBoard(board));
			currentNode.setMove(currentMove);
			
			//add the node to the list and return it
			nodes.add(currentNode);
			return nodes;
		}
		
		for(Move move: actualJumps)
		{
			Board boardMove = copyBoard(board);
			boardMove.jumpMovePiece(move);
			nodes.addAll(getMoreJumps(move, boardMove, color, playerUp,depth+1));
		}
		return nodes;
	}
	
	//returns additional jumps recursively as a list of moves in BoardNode format
	public ArrayList<ArrayList<BoardNode>> getAllMoreJumps(ArrayList<BoardNode> listOfMoves, Move currentMove, Board board, PieceType color, boolean playerUp, int depth)
	{
		ArrayList<ArrayList<BoardNode>> nodes = new ArrayList<ArrayList<BoardNode>>();
		int index = board.correspondingIndex(currentMove.getEnd().getCol(),currentMove.getEnd().getRow());
		BoardPosition position = board.getPosition(index);
		ArrayList<Move> possibleJumps = board.movesAvailable(board, position, playerUp, false);
		ArrayList<Move> actualJumps = new ArrayList<Move>();
		
		for(Move move : possibleJumps)
		{
			if(move.isJump())
				actualJumps.add(move);
		}
		
		//base case for recursion, no more jumps available
		if(actualJumps.isEmpty())
		{
			if(depth == 0)
				return nodes;
			BoardNode currentNode = new BoardNode(copyBoard(board));
			currentNode.setMove(currentMove);
			//nodes.add(currentNode);
			listOfMoves.add(currentNode);
			nodes.add(listOfMoves);
			return nodes;
		}
		
		for(Move move: actualJumps)
		{
			ArrayList<BoardNode> tempList = new ArrayList<BoardNode>();
			
			for(BoardNode node : listOfMoves)
			{
				BoardNode temp = new BoardNode(copyBoard(node.getBoard()));
				temp.setMove(node.getMove());
				tempList.add(temp);
			}
			
			Board boardMove = copyBoard(board);
			boardMove.jumpMovePiece(move);
			
			BoardNode currentNode = new BoardNode(boardMove);
			//currentNode.setMove(currentMove);
			currentNode.setMove(currentMove);
			//nodes.add(currentNode);
			tempList.add(currentNode);
			
			nodes.addAll(getAllMoreJumps(tempList, move, boardMove, color, playerUp,depth+1));
		}
		return nodes;
	}
	
	public void create4pieceBoards()
	{
		ArrayList<Board> boards = new ArrayList<Board>();
		Board start = new Board();
		BoardPosition[] squares = start.getSquares();
		
		for(int i = 0; i < squares.length; i++)
		{
			squares[i] = new BoardPosition(squares[i].getCol(),squares[i].getRow(), PieceType.NONE);
		}
		start.setSquares(squares);
		start.printBoard();
		
		squares[0].setPiece(PieceType.BLACK);
		squares[1].setPiece(PieceType.BLACK);
		squares[2].setPiece(PieceType.RED);
		squares[3].setPiece(PieceType.RED);
		
		start.printBoard();
	}
	
	public double getOpenings(BoardNode node, double alpha, double beta, PieceType color, boolean orientation, int depth, boolean maxPlayer)
	{

		//THIS SHOULD BE LIKELY BE ADDED HERE ALSO
		/*Board evalBoard = node.getBoard();
		if(currentWins(evalBoard, color))
			return Double.POSITIVE_INFINITY;
		else if(opponentWins(evalBoard, color))
			return Double.NEGATIVE_INFINITY;*/
		ArrayList<BoardNode> addopen = new ArrayList<BoardNode>();
		ArrayList<BoardNode> addresponse = new ArrayList<BoardNode>();
		openings.add(addopen);
		responses.add(addresponse);
		
		if(depth <= 0)
		{
			return 10;//evaluateBoard(node, color, orientation);
		}
		
		if(maxPlayer)
		{
			ArrayList<BoardNode> boards = generateBoardMoves(node, color,orientation);//node.getBoards();
			
			//if(!maxPlayer)
			//	responses.get(2-depth).addAll(boards);
			//else
				openings.get(2-depth).addAll(boards);
			for(BoardNode n : boards)
			{
				getOpenings(n, alpha, beta, topGame.getOppositeColor(color), !orientation, depth - 1, false);
				//alpha = Math.max(alpha, getOpenings(n, alpha, beta, topGame.getOppositeColor(color), !orientation, depth - 1, false));
				/*if(beta <= alpha)
				{
					break;
				}*/
			}
			
			return alpha;
		}
		else
		{
			ArrayList<BoardNode> boards = generateBoardMoves(node, color,orientation);//node.getBoards();
			
			//if(!maxPlayer)
			//	openings.get(2-depth).addAll(boards);
			//else
				responses.get(2-depth).addAll(boards);
			for(BoardNode n : boards)
			{
				getOpenings(n, alpha, beta, topGame.getOppositeColor(color), !orientation, depth - 1, true);
				/*beta = Math.min(beta, getOpenings(n, alpha, beta, topGame.getOppositeColor(color), !orientation, depth - 1, true));
				if(beta <= alpha)
				{
					break;
				}*/
			}
			
			return beta;
		}
		
		//alpha = Double.NEGATIVE_INFINITY;
		
		//return node;
	}
	
	public ArrayList<Move> checkSequence(Move playerMove, PieceType aiColor)
	{
		ArrayList<Move> response = new ArrayList<Move>();
		try
		{
			Scanner fileReader = new Scanner(new FileInputStream("openings.txt"));
			String line = "";
			int index = 0;
			boolean foundNextMove = false;
			boolean inSequenceGroup = false;
			boolean aiRed = topGame.getPlayerColor(2) == PieceType.RED;
			boolean aiBlack = topGame.getPlayerColor(2) == PieceType.BLACK;
			boolean skipRest = false;
			int sequenceNum = 0;
			int ai = 0, notAi = 1;
			
			if(aiColor == PieceType.RED)
			{
				ai = 1;
				notAi = 0;
			}
			
			if(aiBlack)
			{
				while(!line.replaceAll("\\s+", "").equals("*Black*"))
					line = fileReader.nextLine();
				fileReader.nextLine();
			}
			
			while(fileReader.hasNextLine())
			{
				line = fileReader.nextLine();
				String [] moves = line.split("\\s+");
				
				if(aiRed && line.replaceAll("\\s+", "").equals("*Black*"))
				{
					skipRest = true;
					break;
				}
				
				if(onSequence && !sequenceName.equals(line) && !inSequenceGroup)
					continue;
				
				if(moves.length == 0)
					continue;
				
				if(moves[0].equals(""))
					continue;
				
				if(moves[0].charAt(0) == '#')
				{
					sequenceNum = 0;
					inSequenceGroup = true;
					sequenceName = line;
					continue;
				}
				
				if(!onSequence && sequenceNum >= 2 || !onSequence && sequenceNum >= 1)
					continue;
				
				if(onSequence && sequenceNum != sequenceNumber)
				{
					sequenceNum++;
					continue;
				}
				
				if(foundNextMove)
				{
					String [] moveList = moves[ai].split(",");
					for(String string : moveList)
					{
						String [] numbers = string.split("-");
						int one = Integer.parseInt(numbers[0]);
						int two = Integer.parseInt(numbers[1]);
						int diff = Math.abs(one - two);
							
						String converted = topGame.convertNotation(string);
						BoardPosition start = new BoardPosition(converted.charAt(0), Character.getNumericValue(converted.charAt(1)), PieceType.NONE);
						BoardPosition end = new BoardPosition(converted.charAt(3), Character.getNumericValue(converted.charAt(4)), PieceType.NONE);
						
						Move move = new Move(start,end);
						
						if(diff >= 7)
							move.setAsJumpMove();
						response.add(move);
					}
					return response;
				}
				
				System.out.println(moves[ai].replaceAll("\\s+", ""));
				
				String [] aiMoves = moves[ai].replaceAll("\\s+", "").split(",");
				boolean same = true;
				
				int i = 0;
				if(sequenceMove.size() == aiMoves.length)
				{
					for(String move : aiMoves)
					{
						if(!move.equals(sequenceMove.get(i).toString()))
						{
							same = false;
						}
						i++;
					}
				}
				else
				{
					same = false;
				}
				
				if(moves.length > 1 && aiColor == PieceType.BLACK && same)
				{
					System.out.println(moves[notAi]);
					
					String [] moveList = moves[notAi].split(",");
					
					if(playerMove.toString().equals(moveList[moveList.length-1]))
					{
						//double rand = Math.random();
						
						//if(rand > 0.6)
						//	continue;
						sequenceNumber++;
						onSequence = true;
						foundNextMove = true;
					}
				}
				
				if(moves.length > 1 && aiColor == PieceType.RED && playerMove.toString().equals(moves[notAi].replaceAll("\\s+", "")))
				{
					double rand = Math.random();
					//if(rand > 0.6)
					//	continue;
					
					System.out.println(moves[ai]);
					
					String [] moveList = moves[ai].split(",");
					boolean jumps = false;
					
					if(moveList.length > 1)
						jumps = true;
					
					sequenceNumber++;
					onSequence = true;
					
					for(String string : moveList)
					{
						String [] numbers = string.split("-");
						int one = Integer.parseInt(numbers[0]);
						int two = Integer.parseInt(numbers[1]);
						int diff = Math.abs(one - two);
						
						String converted = topGame.convertNotation(string);
						BoardPosition start = new BoardPosition(converted.charAt(0), Character.getNumericValue(converted.charAt(1)), PieceType.NONE);
						BoardPosition end = new BoardPosition(converted.charAt(3), Character.getNumericValue(converted.charAt(4)), PieceType.NONE);
						
						Move move = new Move(start,end);
						
						if(diff >= 7)
							move.setAsJumpMove();
						
						response.add(move);
					}
					
					String nextLine = fileReader.nextLine().replaceAll("\\s+", "");
					
					if(nextLine.isEmpty())
						onSequence = false;
					return response;
				}
				sequenceNum++;
			}
			fileReader.close();
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return response;
	}
	
	
	public void setSequenceInfo(ArrayList<Move> moves, int number, boolean on, String name)
	{
		sequenceMove = moves;
		sequenceNumber = number;
		onSequence = on;
		sequenceName = name;
	}
	
	public boolean isOnSequence()
	{
		return onSequence;
	}
	
	public int getSequenceNumber()
	{
		return sequenceNumber;
	}
	
	public String getSequenceName()
	{
		return sequenceName;
	}
}
