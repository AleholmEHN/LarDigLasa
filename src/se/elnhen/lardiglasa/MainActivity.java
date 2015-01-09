package se.elnhen.lardiglasa;

/**
 * @author Elin Henriksson elnhen-2
 */



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity
{
	private GameView theGameView;			// The gameView
	
	/**
	 * Called when view is created
	 * @param savedInstanceState   the state
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Remove title and batteryrow from window so app fills whole screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);

		theGameView = (GameView)findViewById(R.id.gameView);	// Connect to actual gameView

		// Get information about names from Intent
		Intent intent = getIntent();
		String name1 = intent.getStringExtra(SetupActivity.MESSAGE_NAME1);
		String name2 = intent.getStringExtra(SetupActivity.MESSAGE_NAME2);
		if (name1 != null)
			theGameView.setPlayerName(name1, GameView.PLAYER1);
		if (name2 != null)
		{
			theGameView.setPlayerName(name2, GameView.PLAYER2);
			theGameView.setAI(false);		// AI not present
		}
		else		// Now AI
		{
			theGameView.setAI(true);
			theGameView.setPlayerName("Dator", GameView.PLAYER2);
		}	
		
	}
	
	/**
	 * called when screen is pressed
	 * @param event    current event
	 * @return   always true
	 */
	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getActionMasked();		// Event type
		
		switch (action)
		{
		
		case MotionEvent.ACTION_DOWN: 
		case MotionEvent.ACTION_POINTER_DOWN: 	//  Start drawing
			theGameView.screenTouched(event.getX(), event.getY());
			break;

			
		}
		
		return true;
	}
	
	
	/**
	 * Called when game is paused
	 */
	@Override
	public void onPause()
	{
		super.onPause();
	}
	
	/**
	 * Called when game is destroyed
	 */
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	


}
