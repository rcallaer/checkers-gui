/*
 * A class that holds a position on the checkers board in col/row format,
 * along with the type of piece, if any, on that board position.
 */
public class BoardPosition 
{
	private char col;
	private int row;
	private PieceType piece;
	private boolean movablePiece;
	
	//constructor
	public BoardPosition()
	{
		col = 'a';
		row = 0;
		piece = PieceType.NONE;
		movablePiece = false;
	}
	
	//parameterized
	public BoardPosition(char col, int row, PieceType p)
	{
		this.row = row;
		this.col = col;
		this.piece = p;
	}
	
	//return the column of this board position
	public char getCol()
	{
		return this.col;
	}
	
	//return the row of this board position
	public int getRow()
	{
		return this.row;
	}
	
	//return both column and row as a string
	public String getColAndRow()
	{
		return this.col + "" + this.row;
	}
	
	//return the type of piece on this board position
	public PieceType getPiece()
	{
		return this.piece;
	}
	
	//return the color of the piece on this board position
	public PieceType getColor()
	{
		PieceType returnColor;
		
		if(piece == PieceType.RED || piece == PieceType.REDKING)
			returnColor = PieceType.RED;
		else if(piece == PieceType.BLACK || piece == PieceType.BLACKKING)
			returnColor = PieceType.BLACK;
		else
			returnColor = PieceType.NONE;
		
		return returnColor;
	}
	
	public boolean isMovablePiece() 
	{
		return movablePiece;
	}
	
	public void setMovablePiece(boolean movablePiece) 
	{
		this.movablePiece = movablePiece;
	}

	//set the row
	public void setRow(int row)
	{
		this.row = row;
	}
	
	//set the column
	public void setCol(char col)
	{
		this.col = col;
	}
	
	//set the piece type
	public void setPiece(PieceType p)
	{
		this.piece = p;
	}
	
	public void setMovable()
	{
		movablePiece = true;
	}
	
	public void setNotMovable()
	{
		movablePiece = false;
	}
	
	//change a red king to a regular red piece and a black king to a black piece
	//on this board position
	public void demote()
	{
		if(piece == PieceType.REDKING)
			piece = PieceType.RED;
		else if(piece == PieceType.BLACKKING)
			piece = PieceType.BLACK;
		else
			return;
	}
	
	//change regular red piece to a red king and a black piece to a black king
	//on this board position
	public void promote()
	{
		if(piece == PieceType.RED)
			piece = PieceType.REDKING;
		else if(piece == PieceType.BLACK)
			piece = PieceType.BLACKKING;
		else
			return;
	}
	
	//return whether the piece is the color red
	public boolean pieceIsRed()
	{
		return piece == PieceType.RED || piece == PieceType.REDKING;
	}
	
	//return whether the piece is a type of king
	public boolean pieceIsKing()
	{
		return piece == PieceType.REDKING || piece == PieceType.BLACKKING; 
	}
	
	//return whether there is a piece on this board position
	public boolean hasPiece()
	{
		return piece != PieceType.NONE;
	}
	
	//remove a piece from this board position
	public void removePiece()
	{
		setPiece(PieceType.NONE);
		setNotMovable();
	}
	
	//given a color in piece type form, return the corresponding king piece type
	public PieceType correspondingKing()
	{
		if(piece == PieceType.RED)
			return PieceType.REDKING;
		else
			return PieceType.BLACKKING;
	}
	
	//given a king in piece type form, return the corresponding color piece type
	public PieceType correspondingPiece()
	{
		if(piece == PieceType.REDKING)
			return PieceType.RED;
		else 
			return PieceType.BLACK;
	}
	
	//to String that simply returns the piece type
	public String toString()
	{
		//return this.col + "" + this.row + "-" + piece;
		if(piece == PieceType.NONE)
			return "----";
		else
			return "" + piece;
	}
}
