/**
 * 
 */
package se.elnhen.lardiglasa;

import android.graphics.Bitmap;

/**
 * @author elnhen-2
 * The piece that can be moved around the board
 */
public class Piece
{

	private int xPos;		// Piece x position
	private int yPos;		// Piece y position
	private int currentBoardPieceNbr;		// Which piece on the board the player is on
	private String name;		// Playername
	private Bitmap theBmp;		// Bitmap to show
	
	/**
	 * Constructor
	 * @param x   xposition
	 * @param y	   yposition
	 * @param cNbr    characternumber
	 * @param bmp     Bitmap to show
	 */
	public Piece(int x, int y, int cNbr,  Bitmap bmp)
	{
		xPos = x;
		yPos = y;
		currentBoardPieceNbr = cNbr;
		name = " ";
		theBmp = bmp;
	}
	
	/**
	 * Get the x position of the piece
	 * @return the xPos
	 */
	public int getxPos()
	{
		return xPos;
	}


	/**
	 * Set the x position of the piece
	 * @param xPos the xPos to set
	 */
	public void setxPos(int x)
	{
		xPos = x;
	}


	/**
	 * Get the y position of the piece
	 * @return the yPos
	 */
	public int getyPos()
	{
		return yPos;
	}


	/**
	 * Set the y position of the piece
	 * @param yPos the yPos to set
	 */
	public void setyPos(int y)
	{
		yPos = y;
	}


	/**
	 * Get the current position of the piece
	 * @return the currentBoardPieceNbr
	 */
	public int getCurrentBoardPieceNbr()
	{
		return currentBoardPieceNbr;
	}


	/**
	 * Set boardposition of the piece
	 * @param currentBoardPieceNbr the currentBoardPieceNbr to set
	 */
	public void setCurrentBoardPieceNbr(int cNbr)
	{
		currentBoardPieceNbr = cNbr;
	}
	
	/*
	 * Get bitmap of the piece
	 * @return The bitmap to show
	 */
	public Bitmap getBitmap()
	{
		return theBmp;
	}

	/**
	 * Set the players name
	 * @param n  The name
	 */
	public void setName(String n)
	{
		name = n;
	}
	
	/**
	 * Get the players name
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	
}
