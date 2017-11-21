/*
 * A class that holds an entire move from one BoardPosition
 * to another BoardPosition, and whether or not it is a 
 * jump move.
 */
public class Move 
{
	private BoardPosition from, to;
	private boolean jumpMove = false;
	private boolean anotherJump = false;
	
	//constructor
	public Move(BoardPosition one, BoardPosition two)
	{
		this.from = one;
		this.to = two;
	}
	
	//return the BoardPosition the move starts from
	public BoardPosition getStart()
	{
		return from;
	}
	
	//return the BoardPosition the move ends at
	public BoardPosition getEnd()
	{
		return to;
	}
	
	//set the current move as a jump move
	public void setAsJumpMove()
	{
		jumpMove = true;
	}
	
	public void setAnotherJump()
	{
		anotherJump = true;
	}
	
	//return whether the current move is a jump move
	public boolean isJump()
	{
		return jumpMove;
	}
	
	public boolean hasAnotherJump()
	{
		return anotherJump;
	}
	
	//toString method in the form 23-18
	public String toString()
	{
		//return this.from.getColAndRow() + "-" + this.to.getColAndRow();
		return convertColAndRow(this.from.getColAndRow()) + "-" + convertColAndRow(this.to.getColAndRow());
	}
	
	//convert from the col/row notation to number notation
	public String convertColAndRow(String s)
	{
		char one = s.charAt(0);
		char two = s.charAt(1);
		int num = 0;
		switch(two)
		{
			case '8': num += 4; break;
			case '7': num += 8; break;
			case '6': num += 12; break;
			case '5': num += 16; break;
			case '4': num += 20; break;
			case '3': num += 24; break;
			case '2': num += 28; break;
			case '1': num += 32; break;
		}
		
		switch(one)
		{
			case 'h': num += 0; break;
			case 'g': num += 0; break;
			case 'f': num += -1; break;
			case 'e': num += -1; break;
			case 'd': num += -2; break;
			case 'c': num += -2; break;
			case 'b': num += -3; break;
			case 'a': num += -3; break;
		}
		return num + "";
	}
}
