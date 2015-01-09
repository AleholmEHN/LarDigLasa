package se.elnhen.lardiglasa;

import android.graphics.Bitmap;

/**
 * @author elnhen-2
 * This is the boardpiece that the gameboard consists of.
 */
public class BoardPiece
{
	private int xPos;		// X position
	private int yPos;		// Y position
	private Bitmap bmp;		// Bitmap to display
	private int charNbr;	// piece/characternumber
	
	/**
	 * Constructor
	 * @param c    Number of piece (A = 1 etc)
	 * @param b	   Bitmap to show
	 */
	public BoardPiece( int c, Bitmap b)
	{
		charNbr = c;
		bmp = b;
	}
	
	/**
	 * Get the bitmap to show
	 * @return the bitmap
	 */
	public Bitmap getBmp()
	{
		return bmp;
	}
	
	/**
	 * Get the characternumber / boardpiecenumber
	 * @return  the number
	 */
	public int getCharNbr()
	{
		return charNbr;
	}
	
	
	/**
	 * Set x and y position
	 * @param x   xposition
	 * @param y    yposition
	 */
	public void setPos(int x, int y)
	{
		xPos = x;
		yPos = y;
	}
	

	/**
	 * get the xposition
	 * @return the xPos
	 */
	public int getxPos()
	{
		return xPos;
	}

	/**
	 * Get the yposition
	 * @return the yPos
	 */
	public int getyPos()
	{
		return yPos;
	}
	
	/**
	 * Recycle the bitmaps after use
	 */
	public void recycleBmp()
	{
		bmp.recycle();
		bmp = null;
	}


}
