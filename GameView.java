/**
 * SurfaceView with game
 * @author Elin Henriksson elnhen-2
 */
package se.elnhen.lardiglasa;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;



import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author admin
 *
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
	Activity context;			// The parent activity
	int screenWidth;			// width of current screen
	int screenHeight;			// Height of current screen
	
	int pieceXSize;				// Size of boardPiece
	int pieceYSize;				
	
	private BoardPiece[] theBoard;		// The gameboard. Different character rectangles
	private static final int NBRCHARS = 29;		// 29 characters in alphabeth
	private static final int NBRPIECES = NBRCHARS+1;	// One more piece with the arrow
	private int[] printOrder;				// Order to print in
	private static final int CHARSPACE = 10;		// Space 
	 
	// Values for player1 area
	private int p1XStart = 0;
	private int p1XEnd = 0;
	private int p1YStart = 0;
	private int p1YEnd = 0;
	private int p1XText = 0;
	private int p1YText = 0;
	
	// Values for player2 area
	private int p2XStart = 0;
	private int p2YStart = 0;
	private int p2XText = 0;
	private int p2YText = 0;
	
	// Values for gameboard area
	private int boardXStart = 0;
	private int boardXEnd = 0;
	private int boardYStart;
	private int boardYEnd = 0;
	
	
	// Values for character area
	private int charXStart = 0;
	private int charXEnd = 0;
	private int charYStart = 0;
	private int charYEnd = 0;
	
	// Values for diceArea
	private int diceXStart = 0;
	private int diceXEnd = 0;
	private int diceYStart = 0;
	private int diceYEnd = 0;
	
	private Bitmap[] dice;			// Bitmapvalues for dice
	
	// Characters in the bottom of the screen
	private List<CharPiece> chars = new ArrayList<CharPiece>();
	// Size of characters in the bottom of the screen
	private int charXsize;
	private int charYsize;
	
	private int usedTextSize = 35;
	
	private float scale = 1.0f;
	
	// Paint settings
	private Paint charTextPaint = new Paint();
	private Paint charShadowPaint = new Paint();
	
	private Paint player1TextPaint = new Paint();
	private Paint player2TextPaint = new Paint();
	
	
	// Constants for state of game
	private static final int ROLL = 1;
	private static final int MOVE = 2;
	private static final int TURNCHAR = 3;
	private static final int TAKECHAR = 4;
	
	private boolean charPieceTurned = false;		// Whether character is turned or not.
	
	private int state = ROLL;		// Start with state Player 1 roll dice
	
	private static final int STARDICE = 6;
	private int diceValue = STARDICE;			// Current dicevalue
	private int rollState = 0;			// Number of times to roll dice to animate
	private int  diceSound = -1;		// Sound to play when dice is rolled
	private SoundPool soundPool;
	
	private Piece[] player;				// Holds the players pieces
	public static final int PLAYER1 = 0;	// Constant number for players
	public static final int PLAYER2 = 1;
	private int plNbr = PLAYER1;		// PlayerNbr 0 or 1
	private String pl1Name = "Spelare 1";		// Just to save until "player-piece" is ready
	private String pl2Name = "Spelare 2";
	
	private int markColor = Color.CYAN;		// The color used to mark possible moves
	
	// To save which characters each player has collected
	private List<CharPiece> player1Chars = new ArrayList<CharPiece>();
	private List<CharPiece> player2Chars = new ArrayList<CharPiece>();
	
	private Bitmap exitSign;
	
	private boolean useAI = false;
	
	private boolean showHelp = true;
	private int helpShowed = 3;
	
	/**
	 * Called when surface is created
	 * @param holder the surfaceholder
	 */
	public void surfaceCreated(SurfaceHolder holder) 
	{

	    Canvas c = getHolder().lockCanvas();
	    draw(c);
	    getHolder().unlockCanvasAndPost(c);
	}
	
	/**
	 * (re)Draws the gameview
	 * @param  myCanvas    the canvas to draw on
	 */
	public void draw(Canvas myCanvas)
	{

		Paint p = new Paint();
		p.setColor(Color.WHITE);		// Draw background
		myCanvas.drawRect(0, 0, screenWidth, screenHeight, p);
		
		drawCharacters(myCanvas);			// Draw all characters
		drawBoard(myCanvas);				// Draw the board and dice

	}
	
	/**
	 * Constructor
	 * @param context   the parent activity
	 * @param attributeSet  Attributes
	 */
	public GameView(Context context, AttributeSet attributeSet) 
	{
	    super(context, attributeSet);
	    this.context = (Activity)context;

	    Resources resources = context.getResources();
	    
		scale = resources.getDisplayMetrics().density;

	    // Settings for character Paint
	    charTextPaint.setColor(Color.BLACK);
		charTextPaint.setTextSize(usedTextSize);
		Typeface comic = Typeface.createFromAsset(context.getAssets(), "fonts/comicsans.ttf");
		charTextPaint.setTypeface(comic);			// Use another typeface
	    
		// Settings for character shadow
		charShadowPaint.setAntiAlias(true);
		charShadowPaint.setShadowLayer(2.0f, 2.0f, 5.0f, Color.BLACK);

		// Settings for playerName text
		player1TextPaint.setColor(Color.BLACK);
		player1TextPaint.setTypeface(comic);
		player1TextPaint.setTextSize(usedTextSize);
		
		player2TextPaint.setColor(Color.BLACK);
		player2TextPaint.setTypeface(comic);
		player2TextPaint.setTextSize(usedTextSize);

		// Initilize players pieces
	    player = new Piece[2];
	    player1Chars.clear();
	    player2Chars.clear();
	    chars.clear();
	    
	    // random which player should start
	    Random generator = new Random();
	    plNbr = generator.nextInt(2);
	    
	    // Create space for the diceBitmaps
	    dice = new Bitmap[6];
	    
		// initialize SoundPool to play the app's different sound effects
	    soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

	    diceSound = soundPool.load(context, R.raw.diceroll, 1);
	    
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;

	    exitSign = BitmapFactory.decodeResource(getResources(), R.drawable.stop, options) ;

	    getHolder().addCallback(this);
	    
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
	 */	
	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh)
	{
		
	    screenWidth = width;
	    screenHeight = height;

	    setupBoard();			// Calculate sizes
	    
	}
	
	/**
	 * Setup the board and load bitmaps
	 */
	private void setupBoard()
	{
		theBoard = new BoardPiece[NBRPIECES];		// 28 characters and an arrow
		printOrder = new int[NBRPIECES];
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
		// Create all board pieces
		for (int i = 0; i < NBRPIECES; i++)
		{
			String tmp = "pic";
			if (i == (NBRPIECES-1))		// Arrow
			{
				tmp += "0";
			}
			else if (i < (NBRPIECES-4))	// A to Z
			{
				int nbr = i + (int) 'a';
				char tmpChar = (char)nbr;
				tmp += tmpChar;
			}
			else
			{
				if (i == (NBRPIECES-4))
					tmp += "aa";
				else if (i == (NBRPIECES-3))
					tmp += "ae";
				else
					tmp += "oo";
			}
			
			int resID = getResources().getIdentifier(tmp, "drawable", "se.elnhen.lardiglasa");
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), resID, options);
			theBoard[i] = new BoardPiece( i, bmp);
						
		}
		
		// calculate area for players, gameboard and characters to select.
		p1XText = 0;
		p1YText = screenHeight/10;
		
		p1XStart = 0;
		p1XEnd = screenWidth / 6;
		
		p1YStart = p1YText + CHARSPACE;
		p1YEnd = 3* screenHeight / 4;

		p2XText = 5*screenWidth/6 + CHARSPACE;
		p2YText = p1YText;
		
		p2XStart = 5*screenWidth/6;
		
		p2YStart = p1YStart;
		
		boardXStart = p1XEnd;
		boardXEnd = p2XStart;
		boardYStart = screenHeight/40;
		boardYEnd = 3* screenHeight / 4;
		
		charXStart = 0;
		charXEnd = screenWidth;
		charYStart = p1YEnd;
		charYEnd = screenHeight;

		setupChars();
		p1YEnd = charYStart;
		boardYEnd = charYStart;
		
		p1XStart = p1XStart + charXsize/2;
		p2XStart = p2XStart + charXsize/2;
		
		calcPieceLocations();
	
		// Change drawing order to make shadows look nice
		printOrder[0] = 29;		// Arrow
		printOrder[1] = 0;		//A
		printOrder[2] = 1;		// B
		printOrder[3] = 2;		// C
		printOrder[4] = 3;		// D
		printOrder[5] = 4;		// E
		printOrder[6] = 5;		// F
		printOrder[7] = 28;		// Ö
		printOrder[8] = 6;		// G
		printOrder[9] = 27;		// Ä
		printOrder[10] = 26;		// Å
		printOrder[11] = 9;		// J
		printOrder[12] = 8;		// I
		printOrder[13] = 7;		// H
		printOrder[14] = 25;		// Z
		printOrder[15] = 10;		// K
		printOrder[16] = 23;		// X
		printOrder[17] = 24;		// Y
		printOrder[18] = 11;		// L
		printOrder[19] = 12;		// M
		printOrder[20] = 13;		// N
		printOrder[21] = 22;		// W
		printOrder[22] = 14;		// O
		printOrder[23] = 21;		// V
		printOrder[24] = 20;		// U
		printOrder[25] = 19;		// T
		printOrder[26] = 18;		// S
		printOrder[27] = 17;		// R
		printOrder[28] = 16;		// Q
		printOrder[29] = 15;		// P
		
		
		// Calculate diceArea
		diceXStart = boardXStart + (int)(2.5*pieceXSize);
		diceXEnd = boardXEnd - (int)(3.5*pieceXSize);
		diceYStart = boardYStart + (int)(1.5*pieceYSize);
		diceYEnd = boardYEnd - (int)(1.5*pieceYSize);
		
		// Make it a square!!
		int xLen = diceXEnd - diceXStart;
		int yLen = diceYEnd - diceYStart;
		
		if ((yLen > xLen))
			diceYEnd = diceYStart + xLen;
		else if (xLen > yLen)
			diceXEnd = diceXStart + yLen;

	    // Dice values
		dice[0] = BitmapFactory.decodeResource(getResources(), R.drawable.one, options);
		dice[1] = BitmapFactory.decodeResource(getResources(), R.drawable.two, options);
		dice[2] = BitmapFactory.decodeResource(getResources(), R.drawable.three, options);
		dice[3] = BitmapFactory.decodeResource(getResources(), R.drawable.four, options);
		dice[4] = BitmapFactory.decodeResource(getResources(), R.drawable.five, options);
		dice[5] = BitmapFactory.decodeResource(getResources(), R.drawable.star, options);
		
		
		// Initialise players pieces
		Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.blue, options);
	    player[PLAYER1] = new Piece(theBoard[29].getxPos() + pieceXSize/4 , theBoard[29].getyPos() + pieceYSize/4, 29, bmp1);
		player[PLAYER1].setName(pl1Name);
		
		Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.green, options);
	    player[PLAYER2] = new Piece(theBoard[29].getxPos() + 3*pieceXSize/4 , theBoard[29].getyPos()  + pieceYSize/4, 29, bmp2);
	    player[PLAYER2].setName(pl2Name);
	    
	    if (useAI && plNbr == PLAYER2)
	    	runAI();
	}
	
	/**
	 * Calculate where boardPieces should be located
	 */
	private void calcPieceLocations()
	{
		pieceXSize = (boardXEnd - boardXStart)/7;
		pieceYSize = (boardYEnd - boardYStart)/7;
		
		int currX = boardXStart + pieceXSize;
		int currY = boardYStart;
		int i = 0;
		// First row. Same y-values
		for (i = 0; i < 6; i++)		// A to F
		{
			theBoard[i].setPos(currX, currY);
			currX += pieceXSize;
		}
		// G 
		currX -= pieceXSize;
		currY += pieceYSize;
		theBoard[i++].setPos(currX,  currY);
		
		// H
		currY += pieceYSize;
		theBoard[i++].setPos(currX,  currY);
		
		// I
		currX -= pieceXSize;
		theBoard[i++].setPos(currX, currY);
		
		//J
		currX -= pieceXSize;
		theBoard[i++].setPos(currX, currY);
		
		// K
		currY += pieceYSize;
		theBoard[i++].setPos(currX, currY);
		
		// L
		currY += pieceYSize;
		theBoard[i++].setPos(currX, currY);
		
		// M
		currX += pieceXSize;
		theBoard[i++].setPos(currX, currY);
		
		// N
		currX += pieceXSize;
		theBoard[i++].setPos(currX, currY);
		
		// O
		currY += pieceYSize;
		theBoard[i++].setPos(currX, currY);

		currY += pieceYSize;

		// P - V
		for (int j = 0; j < 7; j++)
		{
			theBoard[i++].setPos(currX, currY);
			currX -= pieceXSize;
		}
		
		currX += pieceXSize;
		
		// W
		currY -= pieceYSize;
		theBoard[i++].setPos(currX, currY);
		
		// X
		currY -= pieceYSize;
		theBoard[i++].setPos(currX, currY);
		
		// Y
		currX += pieceXSize;
		theBoard[i++].setPos(currX, currY);

		// Z
		currY -= pieceYSize;
		theBoard[i++].setPos(currX, currY);

		// Å
		currY -= pieceYSize;
		theBoard[i++].setPos(currX, currY);

		// Ä
		currX -= pieceXSize;
		theBoard[i++].setPos(currX, currY);

		// Ö
		currY -= pieceYSize;
		theBoard[i++].setPos(currX, currY);
		
		// Arrow = nbr 29
		theBoard[NBRPIECES-1].setPos(boardXStart, boardYStart);
		
	}
	
	/**
	 * Setup character parts
	 */
	private void setupChars()
	{
		int space = charXEnd/(NBRCHARS*2);
		
		charXsize = charXEnd/(NBRCHARS/2 + 1)-space;
		charYsize  = charXsize;
		charYStart = charYEnd - (int)(3.5*charYsize);
		int currX = charXStart + charXsize/4;
		int currY = charYStart + charYsize/2;
		
		// Set x and y positions
		for (int i = 0; i < NBRCHARS; i++)
		{
			CharPiece tmpChar = new CharPiece(currX, currY);
			chars.add(tmpChar);

			// Organize in two rows
			if ((i % 2) == 0)
			{
				currY += charYsize*1.5 ;		
			}
			else
			{
				currY = charYStart + charYsize/2;
				currX += (charXsize + space);
			}
		}
		
		// Random characters
		Random generator = new Random();
		for (int i = (int)'A'; i < (int)'A' + NBRCHARS-3; i++)		// Loop through so all characters are set. Not å ä ö
		{
			int charNbr = generator.nextInt(chars.size());
			while (!chars.get(charNbr).getChar().equals(" "))
			{
				charNbr = generator.nextInt(chars.size());
			}
			CharPiece tmpChar = chars.get(charNbr);
			tmpChar.setChar(new String(Character.toChars(i)), (i-'A'));
		}
		// Å
		int charNbr = generator.nextInt(chars.size());
		while (!chars.get(charNbr).getChar().equals(" "))
		{
			charNbr = generator.nextInt(chars.size());
		}
		CharPiece tmpChar = chars.get(charNbr);
		tmpChar.setChar("Å", 26);
		// Ä
		charNbr = generator.nextInt(chars.size());
		while (!chars.get(charNbr).getChar().equals(" "))
		{
			charNbr = generator.nextInt(chars.size());
		}
		tmpChar = chars.get(charNbr);
		tmpChar.setChar("Ä", 27);
		// Ö
		charNbr = generator.nextInt(chars.size());
		while (!chars.get(charNbr).getChar().equals(" "))
		{
			charNbr = generator.nextInt(chars.size());
		}
		tmpChar = chars.get(charNbr);
		tmpChar.setChar("Ö", 28);
		
		// Four of them should be visible at start.
		for (int i = 0 ; i < 4;i ++)
		{
			int tal1 = generator.nextInt(chars.size());
			while (chars.get(tal1).getVisible())
			{
				tal1 = generator.nextInt(chars.size());
			}
			tmpChar = chars.get(tal1);
			tmpChar.setVisible();
		}
	}
	
	/**
	 * Draw the characters in bottom of the screen
	 * @param canvas  the canvas to draw on
	 */
	private void drawCharacters(Canvas canvas)
	{
		Paint p = new Paint();
		p.setColor(Color.LTGRAY);		// Defaultcolor is lightgray
		
		Paint highLight = new Paint();
		if (plNbr == PLAYER1)
			markColor = Color.BLUE;
		else
			markColor = Color.GREEN;
		
		highLight.setColor(markColor);		// Highlightcolor
			 			
		for (CharPiece c : chars)
		{
			RectF r = new RectF(c.getXpos(), c.getYpos(), c.getXpos() + charXsize, c.getYpos() + charYsize);
			canvas.drawRoundRect(r, 5.0f, 5.0f, charShadowPaint);		// First a rounded shadow
			if (state == TURNCHAR)			// Highlight the ones that could be selected
			{
				if (c.getVisible())
				{
					canvas.drawRoundRect(r, 5.0f, 5.0f, p);		// A rounded rect on top
					canvas.drawText(c.getChar(), c.getXpos() + charXsize/4, c.getYpos() + 3*charYsize/4, charTextPaint);		// And the text
				}
				else
					canvas.drawRoundRect(r, 5.0f, 5.0f, highLight);		// A rounded highlighted on top
			}
			else if (state == TAKECHAR)		// Highlight the ones that could be taken
			{

				if (c.getVisible())
				{
					canvas.drawRoundRect(r, 5.0f, 5.0f, highLight);		// A rounded rect 
					canvas.drawText(c.getChar(), c.getXpos() + charXsize/4, c.getYpos() + 3*charYsize/4, charTextPaint);		// the text
				}
				else
					canvas.drawRoundRect(r, 5.0f, 5.0f, p);			// A rounded rect
			}	
			else			// No highlight
			{
				canvas.drawRoundRect(r, 5.0f, 5.0f, p);		// a rounded not highlighted one
				if (c.getVisible())
				{
					canvas.drawText(c.getChar(), c.getXpos() + charXsize/4, c.getYpos() + 3*charYsize/4, charTextPaint);
				}
			}
				
		}
	}
	
	/** 
	 * Draw the board
	 * @param canvas   the canvas to paint on
	 */
	private void drawBoard(Canvas canvas)
	{

		Paint paint = new Paint();
	    paint.setAntiAlias(true);
		paint.setShadowLayer(5.5f, 6.0f, 6.0f, Color.BLACK);		// The shadow
			
		for (int j = 0; j < NBRPIECES; j++)
		{
			int i = printOrder[j];
			Rect myDestRect = new Rect(theBoard[i].getxPos(), theBoard[i].getyPos(), theBoard[i].getxPos() + pieceXSize, theBoard[i].getyPos() + pieceYSize);
			canvas.drawRect(myDestRect, paint);		// Shadowrect first
			if (theBoard[i].getBmp() != null)
				canvas.drawBitmap(theBoard[i].getBmp(), null, myDestRect, null);		// then bitmap
		}
		
		Paint hLight  = new Paint();
		hLight.setColor(markColor);

		// Draw the dice
		// First at rectangle to scale the bitmap in
		Rect myDestRect = new Rect(diceXStart, diceYStart, diceXEnd, diceYEnd);

		if (state == ROLL)				// Highlight if roll
		{			
		    canvas.drawText("Slå!", diceXStart, diceYStart + dice[0].getHeight() + CHARSPACE*scale, player1TextPaint);
		}
		if (dice[diceValue-1] != null)
			canvas.drawBitmap(dice[diceValue-1], null,  myDestRect, null);
		
		// Tell player what star means
		if (state == MOVE && diceValue == STARDICE && rollState == 0)
		{
			if (!useAI || plNbr == PLAYER1)
			{
				Toast myToast = Toast.makeText(context, "Flytta till valfri spelruta när du fått stjärna!", Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			}
		}
		// Draw players names


		if (plNbr == PLAYER1)			// Draw a circle to mark players turn
		{
			RectF r = new RectF(p1XText /* + CHARSPACE*/, p1YText-3*CHARSPACE, /*p1XText + 5*3*CHARSPACE*/ boardXStart, p1YText+CHARSPACE);
//			canvas.drawCircle(p1XText + 3*CHARSPACE, p1YText-CHARSPACE, 3*CHARSPACE, hLight);
			canvas.drawRoundRect(r, 5.0f, 5.0f, hLight);
		}
		else
		{
			RectF r = new RectF(p2XText /*+ CHARSPACE*/, p2YText-3*CHARSPACE, screenWidth/*p2XText + 5*3*CHARSPACE*/, p2YText+CHARSPACE);
//			canvas.drawCircle(p2XText + 3*CHARSPACE, p2YText-CHARSPACE, 3*CHARSPACE, hLight);
			canvas.drawRoundRect(r, 5.0f, 5.0f, hLight);
		}	
		canvas.drawText(player[PLAYER1].getName(), p1XText , p1YText, player1TextPaint);
		canvas.drawText(player[PLAYER2].getName(), p2XText , p2YText, player2TextPaint);
		
		// Draw exitsign in upper right corner
		if (exitSign != null)
		{
			Rect myDestRect2 = new Rect((int)(screenWidth-scale*3*CHARSPACE), 0, screenWidth, (int)scale*3*CHARSPACE);

			canvas.drawBitmap(exitSign, null, myDestRect2, null);
		}
		// Draw players pieces.
		Paint p2 = new Paint();
		p2.setColor(Color.LTGRAY);
		
		// Draw the characters every player has collected
		for (CharPiece pChar : player1Chars)
		{
			RectF r = new RectF(pChar.getXpos(), pChar.getYpos(), pChar.getXpos() + charXsize, pChar.getYpos() + charYsize);
			canvas.drawRoundRect(r, 5.0f, 5.0f, charShadowPaint);		// A shadow
			canvas.drawRoundRect(r, 5.0f, 5.0f, p2);			// A rounded rect
			
			canvas.drawText(pChar.getChar(), pChar.getXpos() + charXsize/4, pChar.getYpos() + 3*charYsize/4, charTextPaint);		// The text
			
		}
		for (CharPiece pChar : player2Chars)
		{
			RectF r = new RectF(pChar.getXpos(), pChar.getYpos(), pChar.getXpos() + charXsize, pChar.getYpos() + charYsize);	
			canvas.drawRoundRect(r, 5.0f, 5.0f, charShadowPaint);		// The shadow
			canvas.drawRoundRect(r, 5.0f, 5.0f, p2);					// A rounded rect
			
			canvas.drawText(pChar.getChar(), pChar.getXpos() + charXsize/4, pChar.getYpos() + 3*charYsize/4, charTextPaint);		// The text
			
		}
		
		// Draw players pieces.
		// Draw a rectangle to scale the bitmap in
		myDestRect = new Rect(player[PLAYER1].getxPos(), player[PLAYER1].getyPos(), player[PLAYER1].getxPos() + pieceXSize/4, player[PLAYER1].getyPos() + 2*pieceYSize/3);

		if (player[PLAYER1].getBitmap() != null)
			canvas.drawBitmap(player[PLAYER1].getBitmap(), null, myDestRect, null);
		myDestRect = new Rect(player[PLAYER2].getxPos(), player[PLAYER2].getyPos(), player[PLAYER2].getxPos() + pieceXSize/4, player[PLAYER2].getyPos() + 2*pieceYSize/3);
		if (player[PLAYER2].getBitmap() != null)
			canvas.drawBitmap(player[PLAYER2].getBitmap(), null, myDestRect, null);
	}
	
	/**
	 * Called when surfaceview is changed
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
	    Canvas c = getHolder().lockCanvas();
	    c.drawRect(0, 0,width,height, new Paint(Color.WHITE));
	    getHolder().unlockCanvasAndPost(c);
	    

	}

	/**
	 * Called when surface is destroyed
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// Recycle bitmaps
		for (int i = 0; i < NBRPIECES; i++)
		{
			theBoard[i].recycleBmp();
		}
		for (int i = 0; i < 6; i++)
		{
			dice[i].recycle();
			dice[i] = null;
		}
		
		// Release soundpool
		soundPool.release();
		soundPool = null;
		diceSound = -1;
	}
	
	/**
	 * Called from MainActivity when screen is touched
	 * @param x  Xposition
	 * @param y  Yposition
	 */
	public void screenTouched(float x, float y)
	{
		if (x > (screenWidth-scale*3*CHARSPACE) && (y < scale*3*CHARSPACE))		// Exit pressed
			context.finish();
		if (state == ROLL)			// Time to roll dice
		{
			// check if correct area is pressed
			if (x >= diceXStart && x <= diceXEnd && y >= diceYStart && y <= diceYEnd)
			{
				rollState = 5;		// Change state 5 times before finished
				if (diceSound != -1)
					soundPool.play(diceSound, 1, 1, 1, 0, 1f);		// Play the sound
				animateDice();					// Animate diceroll
				state = MOVE;
/*				if (showHelp)
				{
					Toast myToast = Toast.makeText((Context)context, "Klicka på rutan du ska flytta din spelpjäs till", Toast.LENGTH_LONG);
					myToast.setGravity(Gravity.CENTER, 0, 0);
					myToast.show();
				}*/
			}
		}
		else if (state == MOVE)		// Player should move. 
		{
			move(x,y);
			if (showHelp)
			{
				Toast myToast =Toast.makeText((Context)context, "Välj en bokstavsbricka (färgmarkerad) att vända upp", Toast.LENGTH_LONG);
				myToast.setGravity(Gravity.CENTER, 0, 0);
				myToast.show();
			}
		}
		else if (state == TAKECHAR || state == TURNCHAR)		// Turn or take character
		{
			turnOrTake(x, y);
		}
		invalidate();			// Redraw the screen
		
	}
	
	/* 
	 * A recursive method for animating the dice roll
	 */
	private void animateDice()
	{
		final Handler handler = new Handler(); 

        Timer t = new Timer(); 		// Create a timer

        t.schedule(new TimerTask() 		// Schedule it to 200ms
        { 

        	public void run() 
        	{ 
        		handler.post(new Runnable() 
        		{ 
        			public void run() 
        			{ 
        				if (rollState != 0)			// If rolls left
        				{
        					rollDice();				// Roll
        					animateDice();			// Animate again
        					invalidate();			// redraw screen
        					rollState--;
        				}
                    } 
                }); 
        	} 
        }, 200); 
	}
	
	/**
	 * Roll to make a new dicevalue
	 */
	private void rollDice()
	{
		Random generator = new Random();
		diceValue = generator.nextInt(6) + 1;
	}
	
	/**
	 * Check if selected point is on a valid boardpiece
	 * @param x   xposition
	 * @param y   yposition
	 * @return    true if it is inside it
	 */
	private int checkPieceSelected(float x, float y)
	{
		int pos = -1;		// If not found
		
		for (int i = 0; i < NBRPIECES; i++)
		{
			if ( x > theBoard[i].getxPos() && x < (theBoard[i].getxPos() + pieceXSize) && 
					y > theBoard[i].getyPos() && y < (theBoard[i].getyPos() + pieceYSize))
			{
				// selected position found.
				pos = i;
				break;
			}
		}
		
		return pos;
	}
	
	/**
	 * Check if touched point is on a character area
	 * @param x   xposition
	 * @param y   yposition
	 * @return    the character touched or null if no match
	 */
	private CharPiece checkCharSelected(float x, float y)
	{
		CharPiece theChar = null;
		for (CharPiece c : chars)
		{
			if ( x > c.getXpos()  && x < (c.getXpos() + charXsize) && 
			y > c.getYpos() && y < (c.getYpos() + charYsize))
			{
				// selected position found.
				theChar = c;
				break;
			}
		}
		return theChar;
		
	}
	
	/** 
	 * Check if any visible character is matching the players character 
	 * @return   true if ther is a visible character that matchs board character 
	 */
	private boolean checkCorrectChar()
	{
		boolean found = false;
		int charNbr = player[plNbr].getCurrentBoardPieceNbr();
		
		for (CharPiece c : chars)
		{
			if (c.getVisible())
			{
				if (c.getCharNbr() == charNbr)
				{
					found = true;
					break;
				}
			}
		}
		
		return found;
	}
	
	/**
	 * Check if all characterpieces are turned
	 * @return   if all characterpieces are turned (=visible)
	 */
	private boolean checkAllTurned()
	{
		boolean allTurned = true;
		for (CharPiece c : chars)
		{
			if (!c.getVisible())
			{
				allTurned = false;
			}
		}
		return allTurned;
	}
	
	/**
	 * Save the players names. Called from MainActivity
	 * @param name  String with the name
	 * @param playerNbr    players number
	 */
	public void setPlayerName(String name, int playerNbr)
	{
		if (playerNbr == PLAYER1)
			pl1Name = name;
		else
			pl2Name = name;
	}
	
	/**
	 * Restarts the game
	 */
	private void restartGame()
	{	
		// Initialize player positions
		player[PLAYER1].setCurrentBoardPieceNbr(29);
		player[PLAYER1].setxPos(theBoard[29].getxPos() + pieceXSize/5);
		player[PLAYER1].setyPos( theBoard[29].getyPos() + pieceYSize/4);
		
		player[PLAYER2].setCurrentBoardPieceNbr(29);
		player[PLAYER2].setxPos(theBoard[29].getxPos() + 2*pieceXSize/5);
		player[PLAYER2].setyPos( theBoard[29].getyPos() + pieceYSize/4);
		
		player1Chars.clear();		// Clear characters for players	
		player2Chars.clear();
		chars.clear();		// Clear old characters
		setupChars();
		diceValue = STARDICE;		// Clear dice
		Random generator = new Random();
		plNbr = generator.nextInt(2);		// Randomize which player should start
		state = ROLL;

		invalidate();		// Redraw
		if (plNbr == PLAYER2 && useAI)
			runAI();

	}
	
	public void setAI(boolean newVal)
	{
		useAI = newVal;
	}
	
	private void runAI()
	{
		if (diceSound != -1)
			soundPool.play(diceSound, 1, 1, 1, 0, 1f);		// Sound
		state = ROLL;

		rollDice();			// Make the roll (no animation at this point)
		state = MOVE;
		invalidate();	
		
		final Handler handler = new Handler(); 

        Timer t = new Timer(); 		// Create a timer

        t.schedule(new TimerTask() 		// Schedule it to 200ms
        { 

        	public void run() 
        	{ 
        		handler.post(new Runnable() 
        		{ 
        			public void run() 
        			{ 
        				// Move piece
        				float x = 0, y = 0;
        				if (diceValue == STARDICE)
        				{
        					int val = getGoodCharacterValues();		// If star get x and y values of a turned character
        					x = theBoard[val].getxPos() + pieceXSize/2;
        					y = theBoard[val].getyPos() + pieceYSize/2;
        				}
        				move(x, y);		// Move the piece to calculated area
        				invalidate();
        				
        				// Pick character
        				if (state == TAKECHAR || state == TURNCHAR)
        				{
        					if (!charPieceTurned)			// Already turned or no character to turn
        					{
        						int val = getLocationToTurn();		// Get location of charPiece to turn
        						if (val != -1)
        						{
        							x = chars.get(val).getXpos() + charXsize/2;
        							y = chars.get(val).getYpos() + charYsize/2;
        						}
        						turnOrTake(x, y);
        						invalidate();
        					}
        					if (state == TAKECHAR )
        					{
        						int val = getLocationToTake();		// Get location of character you can take
        						if (val != -1)
        						{
        							x = chars.get(val).getXpos() + charXsize/2;
        							y = chars.get(val).getYpos() + charYsize/2;

        						}
        						turnOrTake(x, y);
        						// Take character
        						state = ROLL;
        						plNbr = PLAYER1;
        						invalidate();
        					}
        				}

        			} 
                }); 
        	} 
        }, 1000); 			// Wait two seconds to move pieces
		
		
	}		
	
	private void turnOrTake(float x, float y)
	{
		if (checkAllTurned())	// No characters to turn
			charPieceTurned = true;
		if (charPieceTurned)	// Character already turned
		{
			if (!checkCorrectChar())		// No character to take
			{
				if (plNbr == PLAYER1)
				{
					plNbr = PLAYER2;
				}
				else
				{
					plNbr = PLAYER1;
				}
				state = ROLL;

			}
			else
			{
				// Check if character should be moved to player
				CharPiece c = checkCharSelected(x, y);
				if (c != null)
				{
					if (c.getVisible())	// And visible
					{
						// Check if selected is correct
						if(player[plNbr].getCurrentBoardPieceNbr() == c.getCharNbr())
						{	
							if (plNbr == PLAYER1)
							{
								int xPos = p1XStart + (player1Chars.size() % 2)*(charXsize + CHARSPACE);		// A Little space
								int yPos = p1YStart + (player1Chars.size() / 2)*(charYsize + CHARSPACE);
								CharPiece theChar = new CharPiece(xPos, yPos);
								theChar.setChar(c.getChar(), c.getCharNbr());
								player1Chars.add(theChar);			// Add character to players char list
								chars.remove(c);			// Remove from bottom
								state = ROLL;
			
								charPieceTurned = false;
								plNbr = PLAYER2;
								if (useAI && chars.size() != 0)		
									runAI();
							}
							else
							{
								int xPos = p2XStart + (player2Chars.size() % 2)*(charXsize+CHARSPACE);		
								int yPos = p2YStart + (player2Chars.size() / 2)*(charYsize+CHARSPACE);
								CharPiece theChar = new CharPiece(xPos, yPos);
								theChar.setChar(c.getChar(), c.getCharNbr());
								player2Chars.add(theChar);
								chars.remove(c);
								state = ROLL;

								charPieceTurned = false;
								plNbr = PLAYER1;
							}
						}
					}
					if (chars.size() == 0)		// Game over
					{
						// custom dialog
						final Dialog dialog = new Dialog(context);
						dialog.setContentView(R.layout.windialog);
						dialog.setTitle("Spelet är slut");
			 
						// Change font and size
						Resources resources = context.getResources();
						scale = resources.getDisplayMetrics().density;

						// set the custom dialog components - text, image and button
						TextView text = (TextView) dialog.findViewById(R.id.tvWinMessage);
						text.setTextSize(usedTextSize);
						Typeface comic = Typeface.createFromAsset(context.getAssets(), "fonts/comicsans.ttf");
						text.setTypeface(comic);
						if (player1Chars.size() > player2Chars.size())
							text.setText(player[PLAYER1].getName() + " vann spelet.");
						else
							text.setText(player[PLAYER2].getName() + " vann spelet.");
			 
						ImageButton dialogButton = (ImageButton) dialog.findViewById(R.id.imgPlayAgain);
						dialogButton.setMaxHeight(screenHeight/4);
						dialogButton.setMaxWidth(screenWidth/4);
						dialogButton.setAdjustViewBounds(true);
						
						// if button is clicked, close the custom dialog
						dialogButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								restartGame();
							}
						});
						ImageButton dialogButton2 = (ImageButton) dialog.findViewById(R.id.imgExit);
						dialogButton2.setMaxHeight(screenHeight/4);
						dialogButton2.setMaxWidth(screenWidth/4);
						dialogButton2.setAdjustViewBounds(true);

						// if button is clicked, close the custom dialog
						dialogButton2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								context.setResult(SetupActivity.GAME_ENDED);
								context.finish();											// Exit game
							}
						});
						dialog.show();
					}
				}
			}
		}
		else			// Character not turned yet, do it
		{	
			// Check which character to turn, if not turned before
			CharPiece c = checkCharSelected(x, y);
			if (c != null)
			{
				if (!c.getVisible())	// Not  visible yet
				{
					c.setVisible();	// make it visible
					charPieceTurned = true;
					state = TAKECHAR;
					// Check if any visible character is correct, otherwise let next player roll
					if (!checkCorrectChar())
					{
						state = ROLL;
						charPieceTurned = false;
						if (plNbr == PLAYER1)
						{
							plNbr = PLAYER2;
							if (useAI)
								runAI();
						}
						else
						{
							plNbr = PLAYER1;

						}
					}
					else
					{
						if (showHelp)
						{
							if (!useAI || plNbr == PLAYER1)
							{
								Toast myToast = Toast.makeText((Context)context, "Klicka på bokstavsbrickan som matchar den ruta du står på", Toast.LENGTH_LONG);
								myToast.setGravity(Gravity.CENTER, 0, 0);
								myToast.show();
			/*					helpShowed--;
								if (helpShowed == 0)
									showHelp = false;*/
							}
						}
					}
				}
			}
		}

	}
	
	/**
	 * Move the playerPiece to calculated or selected area
	 * @param x  xposition
	 * @param y   yposition
	 */
	private void move(float x, float y)
	{
		int pos = 0;
		// If a star on dice the player should select where to move, otherwise no option
		if (diceValue == STARDICE)	// a star, check where on screen user touched. Move to that position
		{
			pos = checkPieceSelected(x, y );		// check a piece is touched				
		}
		else
		{
			// Move to correct position
			pos = player[plNbr].getCurrentBoardPieceNbr();
			pos += diceValue;		// Add diceValue to current value
			if (pos > NBRPIECES-1)		// Loop around
			{
				pos -= NBRPIECES;
			}
		}
		if (pos != -1)			// A valid area is touched
		{
			int xPos = 0;
			if (plNbr == PLAYER1)		// Player1
				xPos = theBoard[pos].getxPos() + pieceXSize/5;
			else			// player2
				xPos = theBoard[pos].getxPos() + 2*pieceXSize/5;
			
			int yPos = theBoard[pos].getyPos() + pieceYSize/4;
			player[plNbr].setxPos(xPos);
			player[plNbr].setyPos(yPos);
			player[plNbr].setCurrentBoardPieceNbr(pos);
			state = TURNCHAR;			// Time to turn character next
			if (checkAllTurned())		// If all turned and no character to take
			{
				charPieceTurned = true;		
				if (!checkCorrectChar())		// Check if you can collect any character	
				{
					if (plNbr == PLAYER1)			// Roll the dice again
					{
						plNbr = PLAYER2;
						if (useAI)
							runAI();
					}
					else
					{
						plNbr = PLAYER1;
					}
					state = ROLL;

				}
				else
				{
					state = TAKECHAR;		// There is a character to take

				}
			}

		}

	}

	/**
	 * Calculates a good place to move to.  For AI
     *  @return  the index of a visible character
	 */
	private int getGoodCharacterValues()
	{
		int value = -1;
		// Check for a character that is visible if possible
		for (CharPiece c : chars)
		{
			if (c.getVisible())
			{
				value = c.getCharNbr();		// Get the index
				break;
			}
		}
		if (value == -1)	// No visible found
		{
			value = 0;		// Get first one
		}
		return value;
	}
	
	/**
	 * Get the location of any character that should be turned when AI active
	 *  @return  index of character or -1 if none applicable
	 */
	private int getLocationToTurn()
	{
		int value = -1;
		Random randGen = new Random();
		int toTurn = randGen.nextInt(chars.size());
		while (chars.get(toTurn).getVisible())		// Already turned. 
		{
			toTurn = randGen.nextInt(chars.size());
		}
		value = toTurn;
		return value;
	}
	
	/**
	 * Calculates the index of the character AI should/could take
	 * @return  the index
	 */
	private int getLocationToTake()
	{
		int value = -1;			// If no valid location
		for (CharPiece c : chars)
		{
			if (c.getCharNbr() == player[PLAYER2].getCurrentBoardPieceNbr())
			{
				if (c.getVisible())
				{
					value = chars.indexOf(c);
					break;
				}
			}
		}
		return value;		
	}


}
