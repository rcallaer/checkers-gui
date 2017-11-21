import java.awt.*;
import java.awt.Event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;
import javax.swing.*;

public class Gui  extends JFrame implements ActionListener, MouseListener, MouseMotionListener, Runnable{
	JLayeredPane layeredPane;
	JPanel checkerBoard, previousCheckerBoard;
	JLabel checkerPiece;
	CheckersGame game, previousGame;
	
	
	int xAdjusments;
	int yAdjusments;

	int startX, startY;
	int endX,endY;
	int boardWidth =600,boardHeight=600;
	
	boolean playerMoved, noAI = false;
	boolean aiMoving = false;

	public Gui ()
	{
		game = new CheckersGame();
		Dimension boardSize = new Dimension(boardWidth,boardHeight);

		//JButton button = new JButton("Undo Move");
		//button.setActionCommand("undo");
		//button.addActionListener((ActionListener) this);
		layeredPane = new JLayeredPane();
		getContentPane().add(layeredPane);
		layeredPane.setPreferredSize(boardSize);
		layeredPane.addMouseListener(this);
		layeredPane.addMouseMotionListener(this);
		//getContentPane().add(button);

		//add checkers board the layered pane
		checkerBoard = new JPanel();
		layeredPane.add(checkerBoard,JLayeredPane.DEFAULT_LAYER);
		checkerBoard.setLayout(new GridLayout(8, 8));
		checkerBoard.setSize(boardSize);
		checkerBoard.setBounds(0, 0, boardSize.width, boardSize.height);
		JPanel square;

		boolean orient = game.hasPlayerOrientedUp();

		int [] positions2 ={1,3,5,7,8,10,12,14,17,19,21,23, 24, 26, 28, 30, 33, 35, 37, 39,40,42,44,46,49,51,53,55,56,58,60,62};
		for(int i=0; i< 64; i++)
		{
			if (inPositions(i,positions2)) {
				if (orient)
					square = new MyPanel(convertFrom64to32(i)+"");
				else
					square = new MyPanel(convertFrom64to32flipped(i)+"");	
			}
			else {
				 square = new MyPanel("");
			}
			checkerBoard.add(square);
			
			int row = (i/8)%2;
			if(row == 0)
			{
				square.setBackground(i % 2 == 0 ? Color.gray : Color.white);
			}
			else
			{
				square.setBackground(i % 2 == 0 ? Color.white : Color.gray);
			}

		}
		


		//add checker pieces to the board
		JLabel piece;
		JPanel panel = null;
		int [] positions ={1,3,5,7,8,10,12,14,17,19,21,23,40,42,44,46,49,51,53,55,56,58,60,62};
		int pos [] = {1,3,5,7,8,10,12,14,17,19,21,23,40,42,44,46,49,51,53,55,56,58,60,62,24,26,28,30,33,35,37,39};
		int pos2 [] = {0,2,4,6,9,11,13,15,16,18,20,22,25,27,29,31,32,34,36,38,41,43,45,47,48,50,52,54,57,59,61,63};
		
		for(int i=0; i<positions.length;i++)
		{
			if(positions[i] > 23)
			{
				if (game.getPlayerColor(1) == PieceType.BLACK)
					piece = new JLabel(new ImageIcon("b.png"));
				else
					piece = new JLabel(new ImageIcon("a.png"));
			}
			else
			{
				if (game.getPlayerColor(1) == PieceType.BLACK)
					piece = new JLabel(new ImageIcon("a.png"));
				else
					piece = new JLabel(new ImageIcon("b.png"));
			}
			
			//piece.setText(""+(64-i));
			//piece.setVerticalTextPosition(SwingConstants.BOTTOM);
			//piece.setHorizontalTextPosition(SwingConstants.CENTER);
			panel = (JPanel)checkerBoard.getComponent(positions[i]);
			panel.add(piece);
			
		
		}
		
		/********************** Undo code *****************/
		/*JButton undoButton = new JButton("Undo move");
		JButton changeAiButton = new JButton("Switch AI");
		
		undoButton.setActionCommand("Undo");
		changeAiButton.setActionCommand("Change");
		
		undoButton.addActionListener(this);
		changeAiButton.addActionListener(this);
		
		panel = (JPanel)checkerBoard.getComponent(positions.length);
		panel.add(undoButton);*/
		
		
		
//		int dex = 0;
//		boolean switchit = false;
//		for(int i=0; i<pos2.length;i++)
//		{
//			//if(!findPos(i,pos))
//			//{
//			if(i%4==0)
//				switchit = true;
//			if(i%8==0)
//				switchit = false;
//			
//			piece = new JLabel();
//			//piece.setVerticalTextPosition(SwingConstants.BOTTOM);
//			if(switchit)
//				piece.setHorizontalAlignment(SwingConstants.LEFT);
//			else
//				piece.setHorizontalAlignment(SwingConstants.RIGHT);
//			if (game.getPlayerColor(1) == PieceType.BLACK)
//				piece.setText(""+(32 - i));
//			else
//				piece.setText(""+(i+1));
//			panel = (JPanel)checkerBoard.getComponent(pos2[i]);
//			panel.add(piece);
//					//dex++;
//			//}
//		}
		aiMoving = !game.isPlayerTurn();
		Thread aiMoveThread = new Thread(){
			public void run()
			{
				aiMoveLoop();
			}
		};
		if(!game.isTwoPlayerGame())
			aiMoveThread.start();
		//aiMove();
	}
	
	/*public void actionPerformed (ActionEvent e)
	{
		if("undo".equals(e.getActionCommand()))
		{
			game.undoMove(game.getBoard().getMoves().get(game.getBoard().getMoves().size()), game.getPlayerColor(1), game.hasPlayerOrientedUp());
			game.undoMove(game.getBoard().getMoves().get(game.getBoard().getMoves().size()-1), game.getPlayerColor(2), !game.hasPlayerOrientedUp());
		}
	}*/
	
	
	public boolean findPos(int num, int pos[])
	{
		for(int i=0; i<23;i++)
		{
			if(pos[i] == num)
			{
				return true;
			}
		}
		return false;
	}
	public void run()
	{
		aiMoveLoop();
	}
	
	
	public boolean inPositions(int j, int[] positions2)
	{
		for (int i =0; i<positions2.length; i++)
		{
			if (j == positions2[i])
				return true;
		}
		return false;
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if(game.isPlayerTurn() && e.getActionCommand().equals("Undo"))
		{
			game = previousGame;
			checkerBoard = previousCheckerBoard;
			checkerBoard.repaint();
		}
		
		if(game.isPlayerTurn() && e.getActionCommand().equals("Change"))
		{
			game.setPlayerTurn(!game.isPlayerTurn());
		}
	}
	
	public void mousePressed(MouseEvent e)
	{
		startX=e.getX();
        startY=e.getY();
        
        //System.out.println("start "+startX+" "+startY);
		
		checkerPiece = null;
		Component c = checkerBoard.findComponentAt(e.getX(), e.getY());
		
		if(c instanceof JPanel)
			return;
		
		Point parentLocation = c.getParent().getLocation();
		xAdjusments = parentLocation.x - e.getX();
		yAdjusments = parentLocation.y - e.getY();
		checkerPiece = (JLabel)c;
		checkerPiece.setLocation(e.getX() + xAdjusments, e.getY()+ yAdjusments);
		layeredPane.add(checkerPiece,JLayeredPane.DRAG_LAYER);
		
		
	}
	
	//move the checker piece around
	public void mouseDragged(MouseEvent me)
	{
		if(checkerPiece == null) return;
		Component c = checkerBoard.findComponentAt(me.getX(),me.getY());
		
		checkerPiece.setLocation(me.getX()+xAdjusments, me.getY()+yAdjusments);
	
	
		
	}
	
	//Drop the chess piece back onto the chess board
	
	public void mouseReleased(MouseEvent e)
	{
		endX = e.getX();
    	endY = e.getY();
    	String move = moveCordinates(startX, startY, endX, endY);
    	
    	move(move, game.isPlayerTurn());
    	//aiMove();
		
		/*if(!game.isTwoPlayerGame() && !noAI)
    	{
			boolean jumpMore = false;
    		game.AIMove("");
	    	Move ai = game.getAIMove();
	    	
	    	int aiStart = game.getBoard().correspondingIndex( ai.getStart().getCol(), ai.getStart().getRow()) + 1;
	    	int aiEnd = game.getBoard().correspondingIndex( ai.getEnd().getCol(), ai.getEnd().getRow()) + 1;
	
	    	aiStart = convertFrom32to64(aiStart);
	    	aiEnd = convertFrom32to64(aiEnd);
	    	
	    	if (ai.isJump()) {
	    		int middlePiece;
	    		MoveOptions jumpOrientation = game.getBoard().jumpMoveOrientation(ai);
	    		switch (jumpOrientation) {
	    			case UR:
	    				middlePiece = aiStart - 7;
	    				break;
	    			case UL:
	    				middlePiece = aiStart - 9;
	    				break;
	    			case DR:
	    				middlePiece = aiStart + 9;
	    				break;
	    			default:
	    				middlePiece = aiStart + 7;
	    				break;
	    		}
	    		
	    		
	    		jumpMore = game.jumpAvailable(game.movesAvailable(game.getBoard().getPosition(convertFrom64to32(aiEnd) - 1), !game.hasPlayerOrientedUp(), false));
	    				
	    		JPanel toBeRemoved = (JPanel)checkerBoard.getComponent(middlePiece);
	    		toBeRemoved.remove(0);
	    		
	    		checkerBoard.repaint();
	    	}
	    	
	    	JPanel jp = (JPanel)checkerBoard.getComponent(aiStart);
	    	JPanel destination =(JPanel)(checkerBoard.getComponent(aiEnd));
	    	
	    	
	    	JLabel p =(JLabel) jp.getComponent(0);
	    	jp.remove(0);
	    	destination.add(p);
	      
	        System.out.println(p.getIcon());
	        //check for king, one orientation supported for now
	        if ((aiEnd < 8) && (p.getIcon().toString().equals("a.png"))) {
	        	
	        	
	        	p.setIcon(new ImageIcon("ak.png"));
	        	  checkerBoard.repaint();
	        }
			else if ((aiEnd < 100 && aiEnd > 55) && (p.getIcon().toString().equals("b.png"))) {
	        	
	        	
	        	p.setIcon(new ImageIcon("bk.png"));
	           
	        	  checkerBoard.repaint();
	        }		
	        checkerBoard.repaint();
	        
	        if(!jumpMore)
	        	game.changePlayerTurn();
	    	
	    
    	}*/
		noAI = false;
		playerMoved = false;
		
	}
	
	public void mouseClicked(MouseEvent e){}
	
	public void mouseMoved(MouseEvent e){}
	
	public void mouseEntered(MouseEvent e){}
	
	public void mouseExited(MouseEvent e){}
	
	/******************AI LOOP
	 * @throws InterruptedException ********************/
	public void aiMoveLoop()
	{
		while(!game.isOver(PieceType.BLACK) || game.isOver(PieceType.RED))
		{
			try{
			while(!game.isPlayerTurn())
			{
				aiMove();
			}
			
			Thread.sleep(1000);
			}
			catch(Exception e)
			{
				System.out.println("Interuppted");
			}
		}
	}
	/******************AI LOOP********************/
	
	public void move(String move, boolean player1)
	{
		int playerEnd =100;
    	boolean jumpMore = false;
		//System.out.println(move + " " + game.playerMove(move, game.getPlayerColor(2), game.hasPlayerOrientedUp()));
    	//if(game.isPlayerTurn())
    	if(game.isTwoPlayerGame() || !game.isTwoPlayerGame())//(player1 == true && !game.isTwoPlayerGame() && !aiMoving))
    	{
    		PieceType type;
    		boolean orientation;
    		if(player1)
    		{
    			type = game.getPlayerColor(1);
    			orientation = game.hasPlayerOrientedUp();
    		}
    		else
    		{
    			type = game.getPlayerColor(2);
    			orientation = !game.hasPlayerOrientedUp();
    		}
    			
    		if(!game.playerMove(move, type, orientation) || (player1 == false && !game.isTwoPlayerGame()))
        	{
    			endX = startX;
        		endY = startY;
        		noAI = true;
        	}
    		else {
    			previousGame = game;
    			previousCheckerBoard = checkerBoard;
    			
    			Move playerMove = game.getPlayerMove();
    			
    			if (playerMove.isJump()) 
    			{
    				int middlePiece;
    	    		MoveOptions jumpOrientation = game.getBoard().jumpMoveOrientation(playerMove);
    	    	  	int playerStart = game.getBoard().correspondingIndex( playerMove.getStart().getCol(), playerMove.getStart().getRow()) + 1;
    		    	 playerEnd = game.getBoard().correspondingIndex( playerMove.getEnd().getCol(), playerMove.getEnd().getRow()) + 1;
    		
    		    	 if(game.getPlayerColor(1) == PieceType.BLACK)
    		    	 {
    		    		 playerStart = convertFrom32to64flipped(playerStart);
    		    		 playerEnd = convertFrom32to64flipped(playerEnd);
    		    	 }
    		    	 else
    		    	 {
    		    		 playerStart = convertFrom32to64(playerStart);
    		    		 playerEnd = convertFrom32to64(playerEnd);
    		    	 }
    		    	 middlePiece = jumpDestination(jumpOrientation, playerStart);
    	    			    				
    	    				
    	    		JPanel toBeRemoved = (JPanel)checkerBoard.getComponent(middlePiece);
    	    		toBeRemoved.remove(0);
    	    		
    	    		checkerBoard.repaint();
    	    	
    	    		if(game.getPlayerColor(1) == PieceType.BLACK)
    	    			jumpMore = game.jumpAvailable(game.movesAvailable(game.getBoard(), game.getBoard().getPosition(convertFrom64to32flipped(playerEnd) - 1), game.hasPlayerOrientedUp(), false));
    	    		else
    	    			jumpMore = game.jumpAvailable(game.movesAvailable(game.getBoard(), game.getBoard().getPosition(convertFrom64to32(playerEnd) - 1), game.hasPlayerOrientedUp(), false));
    	    		
    	    		if(game.isTwoPlayerGame() && !game.isPlayerTurn())
    	    		{
    	    			if(game.getPlayerColor(1) == PieceType.BLACK)
        	    			jumpMore = game.jumpAvailable(game.movesAvailable(game.getBoard(), game.getBoard().getPosition(convertFrom64to32flipped(playerEnd) - 1), !game.hasPlayerOrientedUp(), false));
        	    		else
        	    			jumpMore = game.jumpAvailable(game.movesAvailable(game.getBoard(), game.getBoard().getPosition(convertFrom64to32(playerEnd) - 1), !game.hasPlayerOrientedUp(), false));
    	    		}
    			}
    			boolean b = game.getBoard().newKingObtained();
    			if(!jumpMore || game.getBoard().newKingObtained())
    			{
    				game.changePlayerTurn();
    				aiMoving = true;
    			}
    		}

    	}

    	System.out.println(moveCordinates(startX, startY, endX, endY));  	
		
		if(checkerPiece == null)
			return;
		
		
		checkerPiece.setVisible(false);
		Component c = checkerBoard.findComponentAt(endX, endY);
		
		if(c instanceof JLabel)
		{
			
			Container parent = c.getParent();
			//parent.remove(0);
			parent.add(checkerPiece);
			
		}
		else
		{
		
			Container parent = (Container)c;
			parent.add(checkerPiece); 
			
		}
		
		int componentIndex = getComponentIndex(c);
		JPanel ourPanel = (JPanel) checkerBoard.getComponent(componentIndex);
		JLabel ourLabel = (JLabel) ourPanel.getComponent(0);
	
		String s1 = (game.getPlayerColor(1) != PieceType.BLACK) ? "a.png" : "b.png";
    	String s2 = (game.getPlayerColor(1) != PieceType.BLACK) ? "b.png" : "a.png";
    	String sk1 = (game.getPlayerColor(1) != PieceType.BLACK) ? "ak.png" : "bk.png";
    	String sk2 = (game.getPlayerColor(1) != PieceType.BLACK) ? "bk.png" : "ak.png";
    	
		if ((componentIndex < 8) && (ourLabel.getIcon().toString().equals(s1))) {
        	
        	
        	ourLabel.setIcon(new ImageIcon(sk1));
        	  checkerBoard.repaint();
        }
		else if ((componentIndex < 100 && componentIndex > 55) && (ourLabel.getIcon().toString().equals(s2))) {
        	
        	
        	ourLabel.setIcon(new ImageIcon(sk2));
        	  checkerBoard.repaint();
        }		
		
		if(game.getBoard().newKingObtained())
			game.getBoard().setNewKing(PieceType.NONE);
		checkerPiece.setVisible(true);
		checkGameStatus(game.getPlayerColor(1));
	}
	
	public void aiMove() throws InterruptedException
	{

		if(!game.isTwoPlayerGame() && !noAI)
    	{
			boolean jumpMore = false;
    		game.AIMove("AI");
	    	ArrayList<Move> aiList = game.getAIMoves();
	    	
	    	for(Move ai : aiList)
	    	{
	    		Thread.sleep(300);
		    	int aiStart = game.getBoard().correspondingIndex( ai.getStart().getCol(), ai.getStart().getRow()) + 1;
		    	int aiEnd = game.getBoard().correspondingIndex( ai.getEnd().getCol(), ai.getEnd().getRow()) + 1;
		
		    	if (game.getPlayerColor(1) == PieceType.BLACK)
		    	{
		    		aiStart = convertFrom32to64flipped(aiStart);
		    		aiEnd = convertFrom32to64flipped(aiEnd);
		    	}
		    	else
		    	{
		    		aiStart = convertFrom32to64(aiStart);
		    		aiEnd = convertFrom32to64(aiEnd);
		    	}
		    	
		    	if (ai.isJump()) {
		    		int middlePiece;
		    		MoveOptions jumpOrientation = game.getBoard().jumpMoveOrientation(ai);
		    		middlePiece = jumpDestination(jumpOrientation, aiStart);
		    		
		    		
		    		/*if(game.getPlayerColor(1) == PieceType.BLACK)
		    			jumpMore = game.jumpAvailable(game.movesAvailable(game.getBoard(), game.getBoard().getPosition(convertFrom64to32flipped(aiEnd) - 1), !game.hasPlayerOrientedUp(), false));
		    		else
		    			jumpMore = game.jumpAvailable(game.movesAvailable(game.getBoard(), game.getBoard().getPosition(convertFrom64to32(aiEnd) - 1), !game.hasPlayerOrientedUp(), false));*/
		    				
		    		JPanel toBeRemoved = (JPanel)checkerBoard.getComponent(middlePiece);
		    		toBeRemoved.remove(0);
		    		
		    		checkerBoard.repaint();
		    	}
		    	
		    	JPanel jp = (JPanel)checkerBoard.getComponent(aiStart);
		    	JPanel destination =(JPanel)(checkerBoard.getComponent(aiEnd));
		    	
		    	
		    	JLabel p =(JLabel) jp.getComponent(0);
		    	jp.remove(0);
		    	destination.add(p);
		    	
		      
		        //System.out.println(p.getIcon());
		        //check for king, one orientation supported for now
		    	String s1 = (game.getPlayerColor(1) != PieceType.BLACK) ? "a.png" : "b.png";
		    	String s2 = (game.getPlayerColor(1) != PieceType.BLACK) ? "b.png" : "a.png";
		    	String sk1 = (game.getPlayerColor(1) != PieceType.BLACK) ? "ak.png" : "bk.png";
		    	String sk2 = (game.getPlayerColor(1) != PieceType.BLACK) ? "bk.png" : "ak.png";
		    	checkerBoard.repaint();
		        if ((aiEnd < 8) && (p.getIcon().toString().equals(s1))) {
		        	
		        	
		        	p.setIcon(new ImageIcon(sk1));
		        	  checkerBoard.repaint();
		        	  break;
		        }
				else if ((aiEnd < 100 && aiEnd > 55) && (p.getIcon().toString().equals(s2))) {
		        	
		        	
		        	p.setIcon(new ImageIcon(sk2));
		           
		        	  checkerBoard.repaint();
		        	  break;
		        }		
		        
		        checkGameStatus(game.getPlayerColor(2));
		        //if(!jumpMore || game.getBoard().newKingObtained())
		        /*if(game.getBoard().newKingObtained())
		        {
		        	game.getBoard().setNewKing(PieceType.NONE);
		        	break;
		        }*/
		        
	    	}
	    	
	    	//if(game.getBoard().newKingObtained())
	        	game.changePlayerTurn();
	    	aiMoving = false;
	        //if(game.getBoard().newKingObtained())
				//game.getBoard().setNewKing(PieceType.NONE);
    	}
	}
	public int jumpDestination(MoveOptions jumpOrientation, int start ) {
		
		int middlePiece, change;
		boolean up = game.getPlayerColor(1) == PieceType.BLACK;
		
		switch (jumpOrientation) {
		case UR: 			
			change = (up) ? 7 : -7;
			break;
		case UL:
			change = (up) ? 9 : -9;
			break;
		case DR:
			change = (up) ? -9 : 9;
			break;
		default:
			change = (up) ? -7 : 7;
			break;
		}
		
		middlePiece = start + change;
		return middlePiece;
	}
	public void checkGameStatus(PieceType color) {
		if (game.isOver(color)) {
			String s = game.getBoard().winner();
			JOptionPane.showMessageDialog(null, s);
			System.exit(0);
		}
			
	}
	public String moveCordinates(int x1, int y1, int x2, int y2)
	{
		if(game.getPlayerColor(1) == PieceType.BLACK)
			return convertFrom64to32flipped(getComponentIndex(checkerBoard.findComponentAt(x1, y1))) + "-"+ convertFrom64to32flipped(getComponentIndex(checkerBoard.findComponentAt(x2, y2)));
		else
			return convertFrom64to32(getComponentIndex(checkerBoard.findComponentAt(x1, y1))) + "-"+ convertFrom64to32(getComponentIndex(checkerBoard.findComponentAt(x2, y2)));
		//return subCordinates(x1, y1)+"-"+subCordinates(x2, y2);
	}
	
	
	public int convertFrom64to32(int i)
	{
		int row = i/8;
	
		if((i%2 == 0 && row%2 == 0) || (i%2 == 1 && row%2 == 1))
		{
			return 100;
		}
		else if(i%2 == 0)
			return (i + 2)/2;
		else
			return (i + 1)/2;
	}
	
	public int convertFrom32to64(int i)
	{
		int n = 0;
		
		
		int row = i /4;
		if (i % 4 == 0) 
			row -=1;
			
		
	
		if(row%2 == 0)
			return i*2 - 1;
		else
			return i*2 -2;
	}
	
	public int convertFrom64to32flipped(int i)
	{
		int x = convertFrom64to32(i);
		return  (33 - x);
	}
	
	public int convertFrom32to64flipped(int i)
	{
		return 63 - convertFrom32to64(i);

	}
	
	public static final int getComponentIndex(Component component) {
	    if (component != null && component.getParent() != null) {
	      Container c = component.getParent();
	      for (int i = 0; i < c.getComponentCount(); i++) {
	        if (c.getComponent(i) == component)
	          return i;
	      }
	    }

	    return -1;
	  }
	
	public CheckersGame copyGame(CheckersGame game)
	{
		CheckersGame copy = new CheckersGame();
		copy.setBoard(copyBoard(game.getBoard()));
		
		return game;
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
	public static void main(String args[])
	{
		JFrame frame = new Gui();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
	}

	
	
	


}
