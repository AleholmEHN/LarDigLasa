/**
 * Class for characters to display
 * @author Elin Henriksson elnhen-2
 */
package se.elnhen.lardiglasa;

/**
 * @author elnhen-2
 *
 */
public class CharPiece
{
	private int xPos = 0;		// xposition of the characterpiece
	private int yPos = 0;		// yposition of the characterpiece
	
	private String theChar = " ";	// Character of the piece
	private int charNbr;			// Characternumber of the piece
	
	private boolean visible = false;		// If character is visible or not.
	
	/**
	 * Constructor
	 * @param x  xposition
	 * @param y  yposition
	 */
	public CharPiece(int x, int y)
	{
		xPos = x;
		yPos = y;
	}

	/**
	 * Set charactervalues
	 * @param newC   character as string
	 * @param cNbr    characternumber
	 */
	public void setChar(String newC, int cNbr)
	{
		theChar = newC;
		charNbr = cNbr;
	}
	
	/**
	 * Get the xposition
	 * @return  xposition
	 */
	public int getXpos()
	{
		return xPos;
	}
	
	/**
	 * Get the yposition
	 * @return  yposition
	 */
	public int getYpos()
	{
		return yPos;
	}
	
	/**
	 * Get the character as a string (to later show it)
	 * @return  the characterstring
	 */
	public String getChar()
	{
		return theChar;
	}
	
	/**
	 * Get characternumber
	 * @return  characternumber
	 */
	public int getCharNbr()
	{
		return charNbr;
	}
	
	/**
	 * Get if character should be visible or not
	 * @return  true if visible
	 */
	public boolean getVisible()
	{
		return visible;
	}
	
	/**
	 * Set the character visible
	 */
	public void setVisible()
	{
		visible = true;
	}
	
	/** 
	 * Set the characeter invisible
	 */
	public void setInvisible()
	{
		visible = false;
	}

}
